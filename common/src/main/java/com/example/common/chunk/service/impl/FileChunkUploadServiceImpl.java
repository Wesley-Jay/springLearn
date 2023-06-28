package com.example.common.chunk.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.chunk.domain.FileChunkUpload;
import com.example.common.chunk.mapper.FileChunkUploadMapper;
import com.example.common.chunk.service.FileChunkUploadService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author wsj
 * @description 实现类
 * @date 2023/6/15
 */

@Service
@Slf4j
public class FileChunkUploadServiceImpl extends ServiceImpl<FileChunkUploadMapper, FileChunkUpload> implements FileChunkUploadService {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    @Override
    public void saveFileChunk(FileChunkUpload  chunkUpload, MultipartFile file) {
        String fileMd5 = chunkUpload.getFileMd5();
        Integer chunkIndex = chunkUpload.getChunkIndex();
        String key = "lock_" + fileMd5 + "_" + chunkIndex;
        try {
            if (RedisLock.getLock(key)) {
                String chunkKey = "path_" + fileMd5 + "_" + chunkIndex;
                String mergeFilePath = SchneiderConfig.getDownloadPath() + fileMd5+".zip";
                if (Objects.nonNull(this.checkZipFileExist(mergeFilePath))) {
                    return;
                }
                boolean existChunk = false;
                Integer chunkSize = chunkUpload.getChunkSize();
                //如果redis没有，需要再查询一下数据库是否有，如果有需要重新写入redis
                FileChunkUpload cacheChunk = redisCache.getCacheObject(chunkKey);
                if (Objects.isNull(cacheChunk)) {
                    List<FileChunkUpload> chunkList = this.list(Wrappers.lambdaQuery(FileChunkUpload.class)
                            .eq(FileChunkUpload::getFileMd5,fileMd5).eq(FileChunkUpload::getChunkIndex, chunkIndex)
                            .select(FileChunkUpload::getChunkFilePath,FileChunkUpload::getId));
                    if (chunkList.size() > 0) {
                        cacheChunk = chunkList.get(0);
                        redisCache.setCacheObject(chunkKey,cacheChunk);
                    }
                }
                if (Objects.nonNull(cacheChunk)) {
                    File chunkFile = new File(cacheChunk.getChunkFilePath());
                    if (chunkFile.exists()) {
                        if (chunkFile.length() == chunkSize) {
                            existChunk = true;
                        } else {
                            //删除数据库记录
                            this.removeById(cacheChunk.getId());
                        }
                    } else {
                        //删除数据库记录
                        this.removeById(cacheChunk.getId());
                    }
                }
                //获取上传文件夹
                String folderPath = SchneiderConfig.getDownloadPath()  + fileMd5 + "/";
                if (!existChunk) {
                    //保存文件到文件夹
                    String chunkFileName = fileMd5 + "-" + chunkIndex + ".zip";
                    String filePath = folderPath + chunkFileName;
                    taskExecutor.execute(() -> this.cacheChunkFile(file, filePath) );
                    //插入chunk表
                    chunkUpload.setChunkFilePath(filePath);
                    chunkUpload.setMergeStatus(0);
                    this.save(chunkUpload);
                    redisCache.setCacheObject(chunkKey,chunkUpload);
                    redisCache.expire(chunkKey,10, TimeUnit.MINUTES);
                }
            }
        } finally {
            RedisLock.unlock(key);
        }
    }

    private void cacheChunkFile(MultipartFile multipartFile, String filePath ) {
        OutputStream outputStream = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (parentFile.mkdirs()) {
                    if (!file.createNewFile()) {
                        throw new RuntimeException("创建文件失败");
                    }
                }
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("未找到文件");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public String mergeFile(String fileMd5){
        String mergeFilePath = SchneiderConfig.getDownloadPath() + fileMd5+".zip";
        if (Objects.nonNull(this.checkZipFileExist(mergeFilePath))) {
            return mergeFilePath;
        }
        String needDeleteFolder = SchneiderConfig.getDownloadPath() + fileMd5;
        List<FileChunkUpload> uploadList = this.list(Wrappers.lambdaQuery(FileChunkUpload.class)
                .eq(FileChunkUpload::getFileMd5, fileMd5).orderByAsc(FileChunkUpload::getChunkIndex));
        int size = uploadList.size();
        if (size  == 0) {
            FileUtils.deleteFiles(needDeleteFolder);
            throw new RuntimeException("该文件的MD5在系统中未找到数据，" + fileMd5);
        }
        if  (size != uploadList.get(0).getChunkCount()) {
            FileUtils.deleteFiles(needDeleteFolder);
            throw new RuntimeException("实际分片总数和该文件的分片总数不相等");
        }
        try {
            File mergeFile = new File(mergeFilePath);
            if(!mergeFile.exists()){
                BufferedOutputStream destOutputStream = new BufferedOutputStream(new FileOutputStream(mergeFile));
                for (FileChunkUpload upload : uploadList) {
                    File file = new File(upload.getChunkFilePath());
                    if (!file.exists()) {
                        throw new FileNotFoundException("分片文件未找到");
                    }
                    //文件读写缓存
                    byte[] fileBuffer = new byte[1024 * 1024 * 5];
                    //每次读取字节数
                    int readBytesLength = 0;
                    BufferedInputStream sourceInputStream = new BufferedInputStream(new FileInputStream(file));
                    while ((readBytesLength = sourceInputStream.read(fileBuffer)) != -1) {
                        destOutputStream.write(fileBuffer, 0, readBytesLength);
                    }
                    sourceInputStream.close();
                }
                destOutputStream.flush();
                destOutputStream.close();
            }
            FileUtils.deleteFiles(needDeleteFolder);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("合并文件出错，");
        }

        String md5= Md5CaculateUtil.getMD5(new File(mergeFilePath));
        if (!Objects.equals(fileMd5,md5)) {
            FileUtils.deleteFile(mergeFilePath);
            throw new RuntimeException("合并的文件MD5和请求的文件MD5不一致");
        }
        return  mergeFilePath;
    }

    private String checkZipFileExist(String filePath) {
        File cacheFile = new File(filePath);
        if (cacheFile.exists()) {
            log.info("上传的压缩包已存在！");
            return filePath;
        } else {
            return  null;
        }
    }


    @Override
    public void cancelMergeFile(String fileMd5) {
        String folderPath = SchneiderConfig.getDownloadPath()  + fileMd5;
        this.remove(Wrappers.lambdaQuery(FileChunkUpload.class).eq(FileChunkUpload::getFileMd5,fileMd5));
        taskExecutor.execute(() -> FileUtils.deleteFiles(folderPath));
        ;
    }
}
