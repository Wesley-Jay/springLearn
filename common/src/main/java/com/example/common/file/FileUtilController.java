package com.example.common.file;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wsj
 * @description 请求层
 * @date 2023/6/28
 */
@RestController
public class FileUtilController {


    @GetMapping(value = "/common/download/chunk")
    public void fileChunkDownload(@RequestHeader(value = "Range") String range, String path,
                                  HttpServletRequest request, HttpServletResponse response) {
        FileUtil.fileChunkDownload(range,path,request,response);
    }
}
