package com.example.product;

import com.vladsch.flexmark.html.HtmlRenderer;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.misc.Extension;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

@SpringBootTest
class ProductApplicationTests {

    @Test
    void StringToMarkdown() throws IOException {
        String  text = "主要更新内容：\\n  1. 增加看板分类\\n  2. 看板支持多人协作\\n  3. 看板增加组件视角\\n  4. 增加组件库功能";
        String filePath = "/Users/wesley/Downloads/test.md";
        FileOutputStream outputStream = new FileOutputStream(filePath);
        Parser  parser = Parser.builder().build();
        Document  document = parser.parse(text);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(html);
    }

}
