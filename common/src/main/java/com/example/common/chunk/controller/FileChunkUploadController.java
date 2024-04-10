package com.example.common.chunk.controller;

import com.example.common.chunk.domain.FileChunkUpload;
import com.example.common.chunk.service.FileChunkUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author wsj
 * @description 接口映射层
 * @date 2023/6/16
 */
@RestController
@RequestMapping("/chunk")
@Slf4j
public class FileChunkUploadController {
    @Resource
    private FileChunkUploadService uploadService;

    @PostMapping("/chunkUpload")
    public String chunkUpload(@RequestParam("file") MultipartFile file, FileChunkUpload chunkUpload){
        long time = System.currentTimeMillis();
        uploadService.saveFileChunk(chunkUpload, file);
        log.info(chunkUpload.getFileMd5()+ "_"+chunkUpload.getChunkIndex() + "执行分片上传完成，耗时：{}", System.currentTimeMillis() - time);
        return "成功";
    }

    @PostMapping("/mergeFile")
    public String mergeFile(String fileMd5){
        return uploadService.mergeFile(fileMd5);
    }

    @GetMapping("/cancelMergeFile")
    public String cancelMergeFile(String fileMd5){
        uploadService.cancelMergeFile(fileMd5);
        return "success";
    }
}
