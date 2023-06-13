package com.demo.controller;

import cn.z.clock.Clock;
import cn.z.id.Id;
import cn.z.ip2region.Ip2Region;
import cn.z.phone2region.Phone2Region;
import cn.z.qrcode.encoder.QRCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;

/**
 * <h1>首页</h1>
 *
 * <p>
 * createDate 2023/04/05 17:28:42
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
@RestController
public class IndexController {

    /**
     * 时间
     */
    @GetMapping(value = {"", "/", "index"})
    public Timestamp index() {
        return Clock.timestamp();
    }

    /**
     * id
     */
    @GetMapping("id")
    public long id() {
        return Id.next();
    }

    /**
     * IP地址
     */
    @GetMapping("ip/{ip}")
    public cn.z.ip2region.Region ip(@PathVariable String ip) {
        return Ip2Region.parse(ip);
    }

    /**
     * 手机号码
     */
    @GetMapping("phone/{phone}")
    public cn.z.phone2region.Region phone(@PathVariable String phone) {
        return Phone2Region.parse(phone);
    }

    /**
     * 二维码
     */
    @GetMapping(value = "qr/{content}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] qr(@PathVariable String content) {
        QRCode qr = new QRCode(content);
        BufferedImage image = qrMatrix2Image(qr.Matrix, 10);
        return show(image);
    }

    /**
     * 下载二维码
     */
    @GetMapping("qr2/{content}")
    public ResponseEntity<byte[]> qr2(@RequestHeader("User-Agent") String userAgent, @PathVariable String content) {
        QRCode qr = new QRCode(content);
        BufferedImage image = qrMatrix2Image(qr.Matrix, 10);
        return download(userAgent, image, "qr.png");
    }

    /**
     * 二维码boolean[][]转BufferedImage
     *
     * @param bytes     boolean[][](false白 true黑)
     * @param pixelSize 像素尺寸
     * @return BufferedImage
     */
    public static BufferedImage qrMatrix2Image(boolean[][] bytes, int pixelSize) {
        int length = bytes.length;
        int size = (length + 2) * pixelSize;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.BLACK);
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < length; y++) {
                if (bytes[x][y]) {
                    graphics.fillRect((x + 1) * pixelSize, (y + 1) * pixelSize, pixelSize, pixelSize);
                }
            }
        }
        graphics.dispose();
        return image;
    }

    /**
     * 显示图片
     *
     * @param image BufferedImage
     * @return byte[]
     */
    public static byte[] show(BufferedImage image) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            stream.close();
            return stream.toByteArray();
        } catch (Exception ignored) {
            return new byte[0];
        }
    }

    /**
     * 下载文件
     *
     * @param userAgent UserAgent
     * @param image     BufferedImage
     * @param fileName  文件名
     * @return ResponseEntity
     */
    public static ResponseEntity<byte[]> download(String userAgent, BufferedImage image, String fileName) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            stream.close();
            BodyBuilder builder = ResponseEntity.ok();
            builder.contentLength(stream.size());
            builder.contentType(MediaType.APPLICATION_OCTET_STREAM);
            fileName = URLEncoder.encode(fileName, "UTF-8");
            if (userAgent.indexOf("MSIE") > 0) {
                builder.header("Content-Disposition", "attachment;filename=" + fileName);
            } else {
                builder.header("Content-Disposition", "attachment;filename*=UTF-8''" + fileName);
            }
            return builder.body(stream.toByteArray());
        } catch (Exception ignored) {
            return null;
        }
    }

}
