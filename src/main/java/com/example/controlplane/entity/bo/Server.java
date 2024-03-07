package com.example.controlplane.entity.bo;

import lombok.Data;

/**
 * 节点信息
 *
 * @author 7bin
 * @date 2024/02/27
 */
@Data
public class Server {

    String hostname;

    String systemType;

    String platform;

    String release;

    String totalMem;

    String freeMem;

    Integer usageMem;

    Integer cpuNum;

    Double cpuUsage;

    String processDisk;

    String totalDisk;

    String freeDisk;

    Integer usageDisk;

    Boolean deployDocker;

}
