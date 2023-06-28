package com.example.common.chunk.controller;

import com.se.ai.chunk.domain.FileChunkUpload;
import com.se.ai.chunk.service.FileChunkUploadService;
import com.se.common.core.domain.AjaxResult;
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
    public AjaxResult chunkUpload(@RequestParam("file") MultipartFile file, FileChunkUpload chunkUpload){
        long time = System.currentTimeMillis();
        uploadService.saveFileChunk(chunkUpload, file);
        log.info(chunkUpload.getFileMd5()+ "_"+chunkUpload.getChunkIndex() + "执行分片上传完成，耗时：{}", System.currentTimeMillis() - time);
        return AjaxResult.success();
    }

    @PostMapping("/mergeFile")
    public AjaxResult mergeFile(String fileMd5){
        return AjaxResult.success(uploadService.mergeFile(fileMd5));
    }

    @GetMapping("/cancelMergeFile")
    public AjaxResult cancelMergeFile(String fileMd5){
        uploadService.cancelMergeFile(fileMd5);
        return AjaxResult.success();
    }
}
