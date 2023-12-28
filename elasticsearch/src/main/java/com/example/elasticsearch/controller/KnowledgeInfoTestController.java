package com.example.elasticsearch.controller;


import com.example.elasticsearch.domain.KnowledgeInfo;
import com.example.elasticsearch.utils.ElasticsearchUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author wsj
 * @description Test
 * @date 2023/11/6
 */

@RestController
@RequestMapping("/knowledge")
public class KnowledgeInfoTestController {
    @Autowired
    private ElasticsearchUtils elasticsearchHandle;

    @GetMapping("/test")
    public void test(String path) {
        String filePath = "C:\\uploadPath\\upload\\knowledge\\123.docx";
        try  {
            Path path1 = new File(filePath).toPath();
            InputStream is = Files.newInputStream(path1);
            Tika tika = new Tika();
            String content = tika.parseToString(is);
            System.out.println("Document content: " +content); // 输出 HTML 内容到控制台
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/testTow")
    public void testTow() throws TikaException, IOException, SAXException {
        String filepath = "C:\\Users\\sesa692850\\Documents\\图片数据集接口文档.md";
        Path path = Paths.get(filepath);
        byte[] bytes = Files.readAllBytes(path);
        String str = new String(bytes);
        KnowledgeInfo info = new KnowledgeInfo();
        info.setTitle("下载在线文档示例");
        info.setContent(str);
        info.setVersion("V1.0");
        info.setStatus("draft");
        info.setEditType("2");
        info.setEquipmentCode("E88");
        info.setSource("1");
    }


    @GetMapping("/testThree/{a}")
    public int testThree(@PathVariable("a") Integer a)  {
        return this.calc(a);
    }

    private  int calc(int a) {
        int b = 0, c = 1;

        try {
            if (a < 3) {
                c = a + b;
                return  c;
            } else {
                c = a/b;
                return  c;
            }
        } catch (Exception e) {
            c = 4;
            throw new RuntimeException(e);
        } finally {
            c = 3;
            return c;
        }
    }

}
