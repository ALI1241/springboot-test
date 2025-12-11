package com.demo.controller;

import cn.z.clock.Clock;
import cn.z.id.Id;
import cn.z.ip2region.Ip2Region;
import cn.z.phone2region.Phone2Region;
import cn.z.qrcode.encoder.QRCode;
import cn.z.tinytoken.T4s;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
@AllArgsConstructor
public class IndexController {

    private final T4s t4s;

    /**
     * 时间
     * http://localhost:8080/
     */
    @GetMapping(value = {"", "/", "index"})
    public Timestamp index() {
        return Clock.timestamp();
    }

    /**
     * id
     * http://localhost:8080/id
     */
    @GetMapping("id")
    public long id() {
        return Id.next();
    }

    /**
     * IP地址
     * http://localhost:8080/ip/123.132.0.0
     */
    @GetMapping("ip/{ip}")
    public cn.z.ip2region.Region ip(@PathVariable String ip) {
        return Ip2Region.parse(ip);
    }

    /**
     * 手机号码
     * http://localhost:8080/phone/18754710000
     */
    @GetMapping("phone/{phone}")
    public cn.z.phone2region.Region phone(@PathVariable String phone) {
        return Phone2Region.parse(phone);
    }

    /**
     * 显示二维码
     * http://localhost:8080/qrShow/123
     */
    @GetMapping(value = "qrShow/{content}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] qrShow(@PathVariable String content) {
        QRCode qr = new QRCode(content);
        BufferedImage image = qrMatrix2Image(qr.Matrix, 10);
        return show(image);
    }

    /**
     * 下载二维码
     * http://localhost:8080/qrDownload/123
     */
    @GetMapping("qrDownload/{content}")
    public ResponseEntity<byte[]> qrDownload(@PathVariable String content) {
        QRCode qr = new QRCode(content);
        BufferedImage image = qrMatrix2Image(qr.Matrix, 10);
        return download(image, "qr.png");
    }

    /**
     * 获取Authorization(当前Context)<br>
     * http://localhost:8080/getAuth
     */
    @GetMapping("getAuth")
    public String getAuth() {
        return t4s.getAuth();
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
     * @param image    BufferedImage
     * @param fileName 文件名
     * @return ResponseEntity byte[]
     */
    public static ResponseEntity<byte[]> download(BufferedImage image, String fileName) {
        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            stream.close();
            BodyBuilder builder = ResponseEntity.ok();
            builder.contentType(MediaType.APPLICATION_OCTET_STREAM);
            builder.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            return builder.body(stream.toByteArray());
        } catch (Exception ignored) {
            return null;
        }
    }

}
