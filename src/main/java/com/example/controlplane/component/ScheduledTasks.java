package com.example.controlplane.component;

import com.example.controlplane.service.IModelService;
import com.example.controlplane.service.INodeService;
import com.example.controlplane.utils.Threads;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 定时任务
 *
 * @author 7bin
 * @date 2024/02/27
 */
@Slf4j
// @Component
public class ScheduledTasks {

    private static final int _5MINUTE = 5 * 60 * 1000;

    private static final int _30MINUTE = 30 * 60 * 1000;

    private static final int _5SECOND = 5 * 1000;

    @Autowired
    INodeService nodeService;

    @Autowired
    IModelService modelService;

    // @Scheduled(fixedRate = _5SECOND) // 每隔5秒执行一次
    public void updateRemoteNode() {
        log.info("update remote node...");
        nodeService.updateRemoteNode();

        // 5s后开始ha处理
        Threads.sleep(_5SECOND);
        log.info("start ha oper...");
        modelService.haOper();

    }

}
