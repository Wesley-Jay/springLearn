package com.example.orderservice.controller;

import com.example.orderservice.utils.GoogleBarCodeUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author wsj
 * @description 测试
 * @date 2024/3/8
 */
public class TestBarCode {

    public static void main(String[] args) {
        String barcodeData = "95647"; // 条形码的数据
        String filePath = "/Users/wesley/Downloads/barcode.png"; // 生成的条形码图片的保存路径
        int width = 200; // 条形码的宽度
        int height = 50; // 条形码的高度

        // 设置条形码参数
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // 设置纠错级别为L（低）
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 设置字符编码为UTF-8

        try {
            // 生成条形码的矩阵
            BitMatrix matrix = new MultiFormatWriter().encode(barcodeData, BarcodeFormat.CODE_128, width, height, hints);

            // 保存生成的条形码图片
            Path path = FileSystems.getDefault().getPath(filePath);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
            BufferedImage image =  GoogleBarCodeUtils.insertWords(bufferedImage, barcodeData);
            if (Objects.isNull(image)) {
                throw new RuntimeException("条形码生成失败");
            }
            ImageIO.write(image, "png", path.toFile());
            System.out.println("条形码生成成功！");
        } catch (WriterException | IOException e) {
            System.out.println("条形码生成失败！");
        }
    }

}
