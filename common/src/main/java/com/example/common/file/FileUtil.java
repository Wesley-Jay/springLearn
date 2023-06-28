package com.example.common.file;

import com.example.common.amazon.s3.AmazonS3Util;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.UnixStat;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author wsj
 * @description 文件相关操作
 * @date 2023/6/28
 */
@Slf4j
public class FileUtil {

    /**
     * 支持单文件或多层文件夹的压缩
     *
     * @param srcPath 需要压缩文件夹路径
     * @param targetPath 目标路径
     */
    public static   void threadZipFile(String srcPath, String targetPath) {
        int length;
        long time = System.currentTimeMillis();
        File file = new File(srcPath);
        List<File> filesToArchive;
        if (file.isDirectory()) {
            filesToArchive = getAllFile(new File(srcPath));
            length= srcPath.length();
        } else {
            filesToArchive = Collections.singletonList(file);
            length = file.getParent().length()+1;
        }
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("compressFileList-pool-").build();
        ExecutorService executor = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(20), factory);
        ParallelScatterZipCreator parallelScatterZipCreator = new ParallelScatterZipCreator(executor);
        OutputStream outputStream = null;
        ZipArchiveOutputStream zipArchiveOutputStream = null;
        try {
            outputStream = new FileOutputStream(targetPath);
            zipArchiveOutputStream = new ZipArchiveOutputStream(outputStream);
            zipArchiveOutputStream.setEncoding("UTF-8");
            for (File inFile : filesToArchive) {
                final InputStreamSupplier inputStreamSupplier = () -> {
                    try {
                        return new FileInputStream(inFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                };
                ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(inFile, inFile.getPath().substring(length));
                zipArchiveEntry.setMethod(ZipArchiveEntry.DEFLATED);
                zipArchiveEntry.setSize(inFile.length());
                zipArchiveEntry.setUnixMode(UnixStat.FILE_FLAG | 436);
                parallelScatterZipCreator.addArchiveEntry(zipArchiveEntry, inputStreamSupplier);
            }
            parallelScatterZipCreator.writeTo(zipArchiveOutputStream);
            log.info("压缩包下载耗时：{}", System.currentTimeMillis() - time);
            //删除批量下载的csv文件以及文件夹
            delFolderAndFile(new File(srcPath));
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException("压缩失败" + e);
        } finally {
            try {
                if (zipArchiveOutputStream != null) {
                    zipArchiveOutputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void delFolderAndFile(File file) {
        //获取目录下子文件
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        if (files != null && files.length != 0) {
            for (File f : files) {
                //判断子目录是否存在子目录,如果是文件则删除
                if (f.isDirectory()) {
                    //递归删除目录下的文件
                    delFolderAndFile(f);
                } else {
                    //文件删除
                    if (!f.delete()) {
                        throw new RuntimeException("Delete file failed ");
                    }
                }
            }
        }
        if(!file.delete()) {
            throw new RuntimeException("Delete file failed ");
        }
    }

    private static List<File> getAllFile(File dirFile) {
        File[] childrenFiles = dirFile.listFiles();
        if (Objects.isNull(childrenFiles) || childrenFiles.length == 0) {
            return Collections.emptyList();
        }
        List<File> files = new ArrayList<>();
        for (File childFile : childrenFiles) {
            if (childFile.isFile()) {
                files.add(childFile);
            } else {
                List<File> cFiles = getAllFile(childFile);
                if (cFiles.isEmpty()) {
                    continue;
                }
                files.addAll(cFiles);
            }
        }
        return files;
    }

    /**
     * 文件分片下载
     * @param range http请求头Range，用于表示请求指定部分的内容。
     *              格式为：Range: bytes=start-end  [start,end]表示，即是包含请求头的start及end字节的内容
     * @param request 请求
     * @param response 响应
     */
    public static void fileChunkDownload(String range, String path, HttpServletRequest request, HttpServletResponse response) {
        File file = new File(path);
        if(!file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        //开始下载位置
        long startByte = 0;
        //结束下载位置
        long endByte = file.length();
        if (range == null || !range.contains("bytes=") || !range.contains("-"))
            throw new RuntimeException("range数据格式错误");
        range = range.substring(range.lastIndexOf("=") + 1).trim();
        String ranges[] = range.split("-");
        try {
            //根据range解析下载分片的位置区间
            if (ranges.length == 1) {
                //情况1，如：bytes=-1024  从开始字节到第1024个字节的数据
                if (range.startsWith("-")) {
                    endByte = Long.parseLong(ranges[0]);
                }
                //情况2，如：bytes=1024-  第1024个字节到最后字节的数据
                else if (range.endsWith("-")) {
                    startByte = Long.parseLong(ranges[0]);
                }
            }
            //情况3，如：bytes=1024-2048  第1024个字节到2048个字节的数据
            else if (ranges.length == 2) {
                startByte = Long.parseLong(ranges[0]);
                endByte = Long.parseLong(ranges[1]);
            }else {
                throw new RuntimeException("数据格式错误");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("数据格式错误");
        }
        if(file.length()<endByte)
            throw new RuntimeException("超出了边界！");
        //要下载的长度
        long contentLength = endByte - startByte;
        //文件名
        String fileName = file.getName();
        //文件类型
        String contentType = request.getServletContext().getMimeType(fileName);
        //响应头设置
        //https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Accept-Ranges
        response.setHeader("Accept-Ranges", "bytes");
        //Content-Type 表示资源类型，如：文件类型
        response.setHeader("Content-Type", contentType);
        //Content-Disposition 表示响应内容以何种形式展示，是以内联的形式（即网页或者页面的一部分），还是以附件的形式下载并保存到本地。
        // 这里文件名换成下载后你想要的文件名，inline表示内联的形式，即：浏览器直接下载
        response.setHeader("Content-Disposition", "inline;filename="+file.getName());
        //Content-Length 表示资源内容长度，即：文件大小
        response.setHeader("Content-Length", String.valueOf(contentLength));
        //Content-Range 表示响应了多少数据，格式为：[要下载的开始位置]-[结束位置]/[文件总大小]
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + file.length());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(contentType);
        BufferedOutputStream outputStream = null;
        RandomAccessFile randomAccessFile = null;
        //已传送数据大小
        long transmitted = 0;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int len = 0;
            randomAccessFile.seek(startByte);
            //判断是否到了最后不足2048（buff的length）个byte
            while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
                transmitted += len;
            }
            //处理不足buff.length部分
            if (transmitted < contentLength) {
                len = randomAccessFile.read(buff, 0, (int) (contentLength - transmitted));
                outputStream.write(buff, 0, len);
                transmitted += len;
            }
            outputStream.flush();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 存储缩略图
     * @param inputStream
     * @param imgUuid
     * @param suffix
     * @param catalogUuid
     */
    private void saveLocalThumbnail(InputStream inputStream, String imgUuid, String suffix, String catalogUuid )  {
        String basePath = SchneiderConfig.getUploadPath() + "/thumbnail/" + catalogUuid ;
        String fileName = imgUuid + suffix;
        String redisKey =  "thumbnail_" + imgUuid;
        File file = new File(basePath + "/" + fileName);
        OutputStream outputStream = null;
        try {
            if (!file.exists()) {
                File parentFile = file.getParentFile();

                if (parentFile.mkdirs()) {
                    if (!file.createNewFile()) {
                        throw new RuntimeException("创建文件失败");
                    }
                }
            }
            outputStream = new FileOutputStream(file);
            long time = System.currentTimeMillis();
            //ImgUtil.scale(inputStream,outputStream,0.2F);
            BufferedImage inputImage = ImageIO.read(inputStream);
            int width = inputImage.getWidth();
            int height = inputImage.getHeight();
            BufferedImage outputImage = new BufferedImage(width/5, height/5, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = outputImage.createGraphics();
            graphics2D.drawImage(inputImage, 0, 0, width/5, height/5, null);
            graphics2D.dispose();
            ImageIO.write(outputImage, "jpeg", outputStream);
            outputStream.write(1);
            String webShowUrl = FileUploadUtils.getPathFileName(basePath, fileName);
            log.info("图片地址：" + webShowUrl +"，缩略图耗时：{}", System.currentTimeMillis()-time);
            //redisCache.setCacheObject(redisKey, webShowUrl);
            //redisCache.expire(redisKey, 2, TimeUnit.HOURS);
        } catch (IOException e) {
            throw new RuntimeException("未找到文件");
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从云端下载原图
     * @param content
     * @param imagePath
     */
    private void downloadNonFormat(AiImageContent content, String imagePath) {
        String imageUrl = content.getImageUrl();
        String imageName = content.getName();
        int index = imageName.lastIndexOf(".");
        String suffix = imageName.substring(index + 1);
        byte[] bytes = AmazonS3Util.getObject(imageUrl);
        try {
            BufferedImage  bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
            File outFile = new File(imagePath + "/"  + imageName);

            this.createZipFolder(outFile);
            //ImageIO.write(bufferedImage,suffix,out);
            Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName(suffix);
            if (iterator.hasNext()) {
                ImageWriter writer = iterator.next();
                ImageWriteParam param = writer.getDefaultWriteParam();

                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                // 该参数在0.92～0.93左右是最接近原图大小的，最大是1
                param.setCompressionQuality(0.92f);
                FileImageOutputStream out = new FileImageOutputStream(outFile);
                writer.setOutput(out);
                // writer.write(bi);
                writer.write(null, new IIOImage(bufferedImage, null, null), param);
                out.close();
                writer.dispose();
            }
        } catch (IOException e) {
            throw new RuntimeException("S3文件保存本地失败，" + e.getMessage());
        }
    }
    private void createZipFolder(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent == null) {
            throw new IOException("该文件父集为null");
        }
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("创建文件夹失败");
            }
        }
    }
}
