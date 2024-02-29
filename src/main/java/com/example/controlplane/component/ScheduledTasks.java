package com.example.controlplane.component;

import com.example.controlplane.service.INodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 *
 * @author 7bin
 * @date 2024/02/27
 */
@Slf4j
@Component
public class ScheduledTasks {

    private static final int _5MINUTE = 5 * 60 * 1000;

    private static final int _5SECOND = 5 * 1000;

    @Autowired
    INodeService nodeService;

    @Scheduled(fixedRate = _5MINUTE) // 每隔5秒执行一次
    public void updateRemoteNode() {
        log.info("update remote node...");
        // nodeService.updateRemoteNode();
    }

}
