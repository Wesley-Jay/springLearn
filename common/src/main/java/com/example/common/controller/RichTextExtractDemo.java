package com.example.common.controller;

import com.google.common.base.Joiner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author wsj
 * @description 富文本提取
 * @date 2024/3/12
 */
public class RichTextExtractDemo {
    public static void main1(String[] args) {
        // 假设这是你的富文本内容
        String richText = "<html><body>" +
                "<img src='http://10.143.164.5:7480/1-1-1/3/1bc142c76b42212595585141c2271394.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240312T055626Z&X-Amz-SignedHeaders=host&X-Amz-Expires=899&X-Amz-Credential=437PTG0EMGDWYFUEO16X%2F20240312%2F%2Fs3%2Faws4_request&X-Amz-Signature=ab5d6e44a079a0f17cc35e0275257e3e5a18034bdd4c20060664b2c4e5f815f5'>" +
                "<img src='http://10.143.164.5:7480/1-1-1/3/1bc142c76b42212595585141c2271394.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240312T055626Z&X-Amz-SignedHeaders=host&X-Amz-Expires=899&X-Amz-Credential=437PTG0EMGDWYFUEO16X%2F20240312%2F%2Fs3%2Faws4_request&X-Amz-Signature=ab5d6e44a079a0f17cc35e0275257e3e5a18034bdd4c20060664b2c4e5f815f5'>" +
                "</body></html>";
        Document doc = Jsoup.parse(richText);
        Long[] userInfos = {1L, 1L, 1L};
        String bucketName =  Joiner.on("-").join(userInfos);
        // 查找所有的图片元素
        Elements images = doc.select("img");
        // 遍历所有图片元素，并替换它们的src属性（即图片链接）
        for (Element image : images) {
            String src = image.attr("src");
            int startNum = src.indexOf(bucketName) + bucketName.length();
            int endNum = src.indexOf("?");
            String newSrc = src.substring(startNum, endNum);
            // 假设我们要将所有的图片链接替换为一个新的链接
            image.attr("src", "http://newexample.com" + newSrc);
        }
        System.out.println(doc.html());
    }

    public static void main(String[] args) {
        String url = "http://10.143.164.5:7480/1-1-1/74134/7d403858495c5beec0cd04e2267f9cb2.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&amp;X-Amz-Date=20240325T023113Z&amp;X-Amz-SignedHeaders=host&amp;X-Amz-Expires=900&amp;X-Amz-Credential=437PTG0EMGDWYFUEO16X%2F20240325%2F%2Fs3%2Faws4_request&amp;X-Amz-Signature=810e792452f5f2d90b67bc0fdd51406efae8f5005a93ed2a7028b579fcbeaa14";
        String bucketName ="1-1-1";
        int startNum = url.indexOf(bucketName) + bucketName.length()+1;
        int endNum = url.indexOf("?");
        String kewStr = url.substring(startNum, endNum);
        System.out.println(kewStr);
    }

}
