package com.example.product.controller;

import com.example.product.service.create_human.*;
import com.vladsch.flexmark.parser.Parser;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.ast.Document;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author wsj
 * @implNote 造人控制器
 * @date 2022/10/27
 */
@RestController
@RequestMapping("/human")
public class CreateHumanController {
    @RequestMapping("/create")
    public void createHuman() {
        AbstractCreateHumanFactory humanFactory = new CreateHumanFactory();
        humanFactory.createHuman(YellowHuman.class).speak();
        humanFactory.createHuman(WhiteHuman.class).speak();
        humanFactory.createHuman(BlackHuman.class).speak();
    }

    public static void main(String[] args) throws IOException {
        String  text = "# Hello, *world*!  \n\nThis is a **Markdown** document.";
        String filePath = "/Users/wesley/Downloads/test.md";
        FileOutputStream outputStream = new FileOutputStream(filePath);
        //Parser parser = Parser.builder().build();
        //Document document = parser.parse(text);
        //HtmlRenderer renderer = HtmlRenderer.builder().build();
        //String html = renderer.render(document);
        Path path = Paths.get(filePath);
        Files.write(path, text.getBytes());
    }
}
