package com.demo.service;

import cn.z.tinytask.annotation.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * <h1>TaskService</h1>
 *
 * <p>
 * createDate 2023/10/04 21:45:41
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
@Service
@Slf4j
public class TaskService {

    /**
     * 每10秒执行一次
     */
    @Async
    @Task("0/10 * * * * *")
    public void test() {
        log.info("每10秒执行一次");
    }

}
