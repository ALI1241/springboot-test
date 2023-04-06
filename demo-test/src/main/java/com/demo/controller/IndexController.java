package com.demo.controller;

import cn.z.clock.Clock;
import cn.z.id.Id;
import cn.z.ip2region.Ip2Region;
import cn.z.phone2region.Phone2Region;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

}
