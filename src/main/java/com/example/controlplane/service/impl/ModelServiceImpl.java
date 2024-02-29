package com.example.controlplane.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.example.controlplane.clients.NodeClient;
import com.example.controlplane.clients.PortalClient;
import com.example.controlplane.constant.DeployStatus;
import com.example.controlplane.dao.DeployInfoDao;
import com.example.controlplane.dao.UpdateDao;
import com.example.controlplane.entity.dto.*;
import com.example.controlplane.entity.po.DeployInfo;
import com.example.controlplane.entity.po.FileInfo;
import com.example.controlplane.exception.ServiceException;
import com.example.controlplane.manager.ThreadPoolManager;
import com.example.controlplane.service.IFileService;
import com.example.controlplane.service.IModelService;
import com.example.controlplane.utils.Threads;
import com.example.controlplane.utils.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * @author 7bin
 * @date 2024/02/27
 */
@Slf4j
@Service
public class ModelServiceImpl implements IModelService {


    @Autowired
    PortalClient portalClient;

    @Autowired
    NodeClient nodeClient;

    @Autowired
    IFileService fileService;

    @Autowired
    DeployInfoDao deployInfoDao;

    @Autowired
    UpdateDao updateDao;

    @Value("${nodePort}")
    String nodePort;

    @Value("${file.save-path}")
    String savePath;

    private static final String PKG_PATH = "/packages";


    @Override
    public PortalResponse getModelList(FindDTO findDTO) {
        return portalClient.getDeployModel(findDTO);
    }

    @Override
    public void deployModel(DeployDTO deployDTO) {

        // 把部署包保存至控制平面中
        FileInfo fileInfo = fileService.getFileByMd5(deployDTO.getMd5());
        if (fileInfo == null) {
            FileDTO fileDTO = new FileDTO();
            fileDTO.setPath(PKG_PATH + "/" + deployDTO.getFile().getOriginalFilename());
            fileDTO.setFile(deployDTO.getFile());
            fileDTO.setMd5(deployDTO.getMd5());
            String fileId = fileService.uploadFiles(fileDTO);
            fileInfo = fileService.getFileById(fileId);
        }

        MultipartFile mf = FileUtils.file2MultipartFile(new File(savePath + fileInfo.getFilePath()));
        // 把部署包发送到目标节点中部署
        List<String> ipList = deployDTO.getTargetIp();
        for (String ip : ipList) {

            DeployInfo deployInfo = new DeployInfo();
            deployInfo.setTargetIp(ip);
            deployInfoDao.insert(deployInfo);

            TimerTask task = new TimerTask() {
                @Override
                public void run() {

                    updateDao.updatedDeployStatusById(deployInfo.getId(), DeployStatus.DEPLOYING);

                    try {

                        // 先检查下节点是否可用
                        nodeClient.ping(ip, nodePort);

                        // 部署模型
                        NodeResponse rsp = nodeClient.deployModel(ip, nodePort, mf);
                        if (NodeResponse.isSuccess(rsp)) {

                            // 更新模型信息
                            DeployInfo info = deployInfoDao.findFirstById(deployInfo.getId());
                            JSONObject data = rsp.getData();
                            info.setStatus(DeployStatus.FINISHED);
                            info.setModelId(data.getString("_id"));
                            info.setModelName(data.getJSONObject("ms_model").getString("m_name"));
                            info.setModelMd5(data.getJSONObject("ms_model").getString("p_id"));
                            info.setUpdateTime(new Date());
                            deployInfoDao.save(info);

                        } else {
                            log.error("deploy model to node [{}] failed: {}", ip, rsp.getResult());
                            updateDao.updatedDeployStatusAndMsgById(deployInfo.getId(), DeployStatus.FAILED, rsp.getResult());
                        }
                    } catch (Exception e) {
                        log.error("deploy model to node [{}] failed: {}", ip, e.getMessage());
                        updateDao.updatedDeployStatusAndMsgById(deployInfo.getId(), DeployStatus.FAILED, e.getMessage());
                    }


                }
            };

            // 执行异步任务
            ThreadPoolManager.instance().execute(task);
            Threads.sleep(100);

        }

    }

    @Override
    public void migrateModel(MigrateDTO migrateDTO) {


        DeployDTO deployDTO = new DeployDTO();
        deployDTO.setTargetIp(migrateDTO.getTargetIp());
        deployDTO.setMd5(migrateDTO.getModelMd5());

        FileInfo file = fileService.getFileByMd5(migrateDTO.getModelMd5());
        if (file != null) {

            deployDTO.setFile(FileUtils.file2MultipartFile(new File(savePath + file.getFilePath())));

        } else {

            // 从部署了当前服务的任意节点获取模型部署包

            List<String> deployedMSR = migrateDTO.getDeployedMSR();

            // 可ping通节点
            List<String> availableNode = new ArrayList<>();

            // 使用CountDownLatch来等待所有节点都ping通
            CountDownLatch countDownLatch = new CountDownLatch(deployedMSR.size());

            for (String ip : deployedMSR) {

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            // 先ping下节点
                            nodeClient.ping(ip, nodePort);
                            availableNode.add(ip);
                        } catch (Exception e) {
                            // log.error("get deploy package from [{}] failed: {}", ip, e.getMessage());
                            log.error(e.getMessage());
                        } finally {
                            countDownLatch.countDown();
                        }
                    }
                };

                ThreadPoolManager.instance().execute(task);

                Threads.sleep(100);

            }

            try {

                // 等待所有节点都测试完毕
                countDownLatch.await();

                if (availableNode.size() == 0) {
                    throw new ServiceException("no available node can get deploy package");
                }

            } catch (InterruptedException e) {
                log.error("ping interrupted: {}", e.getMessage());
            }

            // 从可用节点中获取部署包
            String fileId = null;
            for (String ip : availableNode) {
                try {
                    fileId = getDeployPackage(ip, migrateDTO.getModelMd5());
                    if (fileId != null) {
                        break;
                    }
                } catch (Exception e) {
                    log.error("get deploy package from [{}] failed: {}", ip, e.getMessage());
                }
            }
            if (fileId == null) {
                throw new ServiceException("not found any deploy package");
            }

            // 设置部署包
            FileInfo fileInfo = fileService.getFileById(fileId);
            MultipartFile mf = FileUtils.file2MultipartFile(new File(savePath + fileInfo.getFilePath()));
            deployDTO.setFile(mf);

        }

        deployModel(deployDTO);

    }


    /**
     * 获取部署包
     *
     * @param ip  计算节点 IP
     * @param md5 模型部署包 md5
     * @return 上传后返回的fileId
     */
    private String getDeployPackage(String ip, String md5) {
        String tmpPath = savePath + "/tmp/" + md5 + ".zip";
        nodeClient.downloadPackage(ip, nodePort, md5, tmpPath);
        FileDTO fileDTO = new FileDTO();
        fileDTO.setPath(PKG_PATH + "/" + md5 + ".zip");
        fileDTO.setFile(FileUtils.file2MultipartFile(new File(tmpPath)));
        fileDTO.setMd5(md5);
        String fileId = fileService.uploadFiles(fileDTO);
        // 删除临时文件
        FileUtils.delete(tmpPath);
        return fileId;
    }

}
