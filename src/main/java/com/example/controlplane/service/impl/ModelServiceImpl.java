package com.example.controlplane.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.controlplane.clients.NodeClient;
import com.example.controlplane.clients.PortalClient;
import com.example.controlplane.constant.DeployStatus;
import com.example.controlplane.constant.NodeConstants;
import com.example.controlplane.constant.PolicyMode;
import com.example.controlplane.dao.*;
import com.example.controlplane.entity.bo.envconfg.ModelEnv;
import com.example.controlplane.entity.bo.envconfg.Selector;
import com.example.controlplane.entity.dto.*;
import com.example.controlplane.entity.dto.page.PageInfo;
import com.example.controlplane.entity.po.*;
import com.example.controlplane.exception.ServiceException;
import com.example.controlplane.manager.ThreadPoolManager;
import com.example.controlplane.service.IFileService;
import com.example.controlplane.service.IModelService;
import com.example.controlplane.service.INodeService;
import com.example.controlplane.service.ITaskServerService;
import com.example.controlplane.utils.Threads;
import com.example.controlplane.utils.XMLUtils;
import com.example.controlplane.utils.file.FileUtils;
import com.example.controlplane.utils.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
    PolicyDao policyDao;

    @Autowired
    TemplateDao templateDao;

    @Autowired
    ModelDao modelDao;

    @Autowired
    HaRecordDao haRecordDao;

    @Value("${file.save-path}")
    String savePath;

    @Autowired
    NodeDao nodeDao;



    @Autowired
    ITaskServerService taskServerService;

    private static final String PKG_PATH = "/packages";


    @Override
    public PortalResponse getModelList(FindDTO findDTO) {
        return portalClient.getDeployModel(findDTO);
    }


    // 缓存模型部署包
    public FileInfo cacheFile(String md5, MultipartFile file) {
        FileInfo fileInfo = fileService.getFileByMd5(md5);
        if (fileInfo == null) {
            if (file == null) {
                List<String> deployed = taskServerService.getDeployedNodeByPid(md5);
                if (deployed.size() == 0) {
                    throw new ServiceException("not found model deploy package");
                }
                // 从部署了相同模型的节点获取模型部署包
                String fileId = getDeployPackage(md5, deployed);
                fileInfo = fileService.getFileById(fileId);
            } else {
                FileDTO fileDTO = new FileDTO();
                fileDTO.setPath(PKG_PATH + "/" + file.getOriginalFilename());
                fileDTO.setFile(file);
                fileDTO.setMd5(md5);
                String fileId = fileService.uploadFiles(fileDTO);
                fileInfo = fileService.getFileById(fileId);
            }
        }

        // 临时解压模型部署包，缓存其中的模型环境配置文件
        String tmpDir = savePath + "/tmp/" + fileInfo.getMd5();
        try {
            FileUtils.unzip(savePath + fileInfo.getFilePath(), tmpDir);
            String envConfig = savePath + "/envconfig/" + fileInfo.getMd5() + ".xml";
            FileUtils.copy(tmpDir + "/model/env_config.xml", envConfig);
        } finally {
            FileUtils.delete(tmpDir);
        }

        return fileInfo;
    }

    public FileInfo cacheFile(MultipartFile file){
        if (file == null) {
            throw new ServiceException("cacheFile: file is required");
        }
        return cacheFile(null, file);
    }

    @Override
    public List<String> deployModel(DeployDTO deployDTO) {

        List<String> res = new ArrayList<>();

        // 把部署包保存至控制平面中
        FileInfo fileInfo = cacheFile(deployDTO.getMd5(), deployDTO.getFile());

        MultipartFile mf = FileUtils.file2MultipartFile(new File(savePath + fileInfo.getFilePath()));
        // 把部署包发送到目标节点中部署
        List<String> ipList = deployDTO.getTargetIp();
        for (String ip : ipList) {

            DeployInfo deployInfo = new DeployInfo();
            deployInfo.setTargetIp(ip);
            deployInfo.setModelMd5(deployDTO.getMd5());
            deployInfo.setModelName(deployDTO.getModelName());
            deployInfoDao.insert(deployInfo);
            res.add(deployInfo.getId());
            TimerTask task = new TimerTask() {
                @Override
                public void run() {

                    templateDao.updatedDeployStatusById(deployInfo.getId(), DeployStatus.DEPLOYING);

                    try {

                        // 先检查下节点是否可用
                        nodeClient.ping(ip);

                        // 检查节点是否已经部署过该模型
                        JSONArray msList = nodeClient.getModelServiceInfoByPid(ip, deployDTO.getMd5());
                        JSONObject ms;
                        if (msList.size() > 0) {
                            // 开启服务
                            startService(ip, msList);
                            ms = msList.getJSONObject(0);
                        } else {
                            // 部署模型
                            ms = nodeClient.deployModel(ip, mf);
                        }
                        // 更新模型信息
                        DeployInfo info = deployInfoDao.findFirstById(deployInfo.getId());
                        info.setStatus(DeployStatus.FINISHED);
                        // info.setModelId(data.getString("_id"));
                        // info.setModelName(ms.getJSONObject("ms_model").getString("m_name"));
                        // info.setModelMd5(ms.getJSONObject("ms_model").getString("p_id"));
                        info.setUpdateTime(new Date());
                        deployInfoDao.save(info);
                    } catch (Exception e) {
                        log.error("deploy model to node [{}] failed: {}", ip, e.getMessage());
                        templateDao.updatedDeployStatusAndMsgById(deployInfo.getId(), DeployStatus.FAILED, e.getMessage());
                    }

                }
            };

            // 执行异步任务
            ThreadPoolManager.instance().execute(task);
            Threads.sleep(100);

        }


        return res;
    }

    private void startService(String ip, JSONArray msList) {

        for (int i = 0; i < msList.size(); i++) {
            JSONObject ms = msList.getJSONObject(i);
            String msid = ms.getString("_id");
            try {
                nodeClient.startModelService(ip, msid);
            } catch (Exception e) {
                log.error("start model service [{}] on [{}] failed: {}", msid, ip, e.getMessage());
            }
        }

    }

    @Override
    public void migrateModel(MigrateDTO migrateDTO) {


        DeployDTO deployDTO = new DeployDTO();
        deployDTO.setTargetIp(migrateDTO.getTargetIp());
        deployDTO.setMd5(migrateDTO.getModelMd5());
        deployDTO.setModelName(migrateDTO.getModelName());

        FileInfo file = fileService.getFileByMd5(migrateDTO.getModelMd5());
        if (file != null) {

            deployDTO.setFile(FileUtils.file2MultipartFile(new File(savePath + file.getFilePath())));

        } else {

            // 从部署了当前服务的任意节点获取模型部署包

            List<String> deployedMSR = migrateDTO.getDeployedMSR();

            String fileId = getDeployPackage(migrateDTO.getModelMd5(), deployedMSR);

            // 设置部署包
            FileInfo fileInfo = fileService.getFileById(fileId);
            MultipartFile mf = FileUtils.file2MultipartFile(new File(savePath + fileInfo.getFilePath()));
            deployDTO.setFile(mf);

        }

        deployModel(deployDTO);

    }

    // 从部署了当前服务的任意节点获取模型部署包
    private String getDeployPackage(String md5, List<String> deployedMSR) {

        if (deployedMSR == null || deployedMSR.size() == 0) {
            throw new ServiceException("no deployed model service record");
        }

        // 可ping通节点
        List<String> availableNode = new ArrayList<>();

        // 使用CountDownLatch来等待所有节点都ping通
        CountDownLatch countDownLatch = new CountDownLatch(deployedMSR.size());

        for (String d : deployedMSR) {

            // ip:port => ip port
            String ip = d.split(":")[0];

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        // 先ping下节点
                        nodeClient.ping(ip);
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
                fileId = getDeployPackage(ip, md5);
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
        return fileId;
    }

    @Override
    public ModelEnv getModelEnvConfig(String pid) {
        // 模型环境配置文件位置
        String dest = savePath + "/envconfig/" + pid + ".xml";

        // 判断本地文件是否有对应的模型环境配置文件
        if (!(new File(dest).exists())) {

            // 从部署了相同模型的节点中获取模型环境配置文件
            List<String> dn = taskServerService.getDeployedNodeByPid(pid);
            if (dn.size() == 0) {
                log.warn("not found model env config");
                return null;
            }


            for (String n : dn) {
                // 该接口支支持特定版本的模型容器
                if (SpringUtils.getBean(INodeService.class).geVersion(n)) {
                    if (SpringUtils.getBean(INodeService.class).geVersion(n)) {
                        nodeClient.downloadModelEnvConfig(n, pid, dest);
                        break;
                    }
                }
            }

        }

        if (!(new File(dest).exists())) {
            log.warn("not found model env config");
            return null;
        }

        // 解析环境配置xml
        ModelEnv modelEnv = (ModelEnv) XMLUtils.convertXmlFileToObject(ModelEnv.class, dest);

        if (modelEnv == null) {
            log.warn("parse model env config failed");
            // throw new ServiceException("parse model env config failed");
        }

        return modelEnv;
    }


    @Override
    public List<Node> getAvailableNodes(String pid) {

        // 更新集群节点信息
        // 获取INodeService的bean并调用其方法
        INodeService ns = SpringUtils.getBean(INodeService.class);
        // ns.updateRemoteNode();

        // 获取在线节点
        List<Node> availableNodes = nodeDao.findAllByStatus(NodeConstants.ONLINE);

        // 只支持模型容器版本大于等于 msMinVersion 的节点
        availableNodes.removeIf(n -> !SpringUtils.getBean(INodeService.class).geVersion(n.getIp()));

        // 根据pid获取模型环境描述文档信息中的计算资源标签规则描述
        ModelEnv config = getModelEnvConfig(pid);

        // 如果没有配置，返回所有在线节点
        if (config == null || config.getSelector() == null) {
            return availableNodes;
        }

        Selector selector = config.getSelector();
        
        // 排除已部署模型的节点
        List<String> deployedNodes = taskServerService.getDeployedNodeByPid(pid);
        for (String dn : deployedNodes) {
            availableNodes.removeIf(on -> on.getIp().equals(dn));
        }

        // 根据Required过滤节点
        availableNodes = SelectorUtils.filter(selector.getRequired(), availableNodes);

        // 根据Preference排序节点
        availableNodes = SelectorUtils.sort(selector.getPreferences(), availableNodes);

        return availableNodes;
    }

    @Override
    public void configHa(PolicyDTO policyDTO) {

        // 策略处理
        Policy policy;
        if (policyDTO.getPolicyId() != null) {
            policy = policyDao.findFirstById(policyDTO.getPolicyId());
            if (policy == null) {
                throw new ServiceException("policy not found");
            }
        } else {
            Policy p = new Policy();
            p.setPolicyName(policyDTO.getPolicyName());
            p.setHaMode(policyDTO.getHaMode());
            p.setCount(policyDTO.getCount());
            p.setTargetIp(policyDTO.getTargetIp());
            policy = buildPolicy(p);
            policyDao.insert(policy);
        }

        String md5 = policyDTO.getModelMd5();

        if (md5 == null) {
            FileInfo fileInfo = cacheFile(policyDTO.getFile());
            md5 = fileInfo.getMd5();
        }

        // md5标识需监控的模型
        Model model = templateDao.getModelByMd5(policyDTO.getModelMd5());
        if(model != null){
            model.setPolicyId(policy.getId());
            modelDao.save(model);
        } else {
            model = new Model();
            model.setName(policyDTO.getModelName());
            model.setMd5(md5);
            model.setPolicyId(policy.getId());
            modelDao.insert(model);
        }

        // 更新节点信息时遍历所有记录的模型 根据policy 获取待迁移节点数量 然后获取候选列表  迁移节点

    }

    @Override
    public PageInfo<DeployInfo> getDeployList(FindDTO findDTO) {
        Page<DeployInfo> page = deployInfoDao.findAll(findDTO.getPageable());
        PageInfo<DeployInfo> res = PageInfo.of(page);
        return res;

    }

    @Override
    public PageInfo<HaRecord> getHaRecordList(FindDTO findDTO) {
        Page<HaRecord> page = haRecordDao.findAll(findDTO.getPageable());
        PageInfo<HaRecord> res = PageInfo.of(page);
        return res;
    }

    @Override
    public PageInfo<Model> getHaModelList(FindDTO findDTO) {
        Page<Model> page = modelDao.findAll(findDTO.getPageable());
        PageInfo<Model> res = PageInfo.of(page);
        List<Model> models = res.getList();
        for (Model model : models) {
            List<String> d = taskServerService.getDeployedNodeByPid(model.getMd5());
            model.setDeployedNodes(d);
            Policy p = policyDao.findFirstById(model.getPolicyId());
            model.setPolicy(p);
        }
        return res;
    }


    public void haOper(){
        List<Model> model = modelDao.findAll();
        for (Model m : model) {


            if (!m.getMd5().equals("2b36a742c37dec471c6488cf68f26bc3")){
                continue;
            }

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    haOper(m.getMd5());
                }
            };

            ThreadPoolManager.instance().execute(task);
        }
    }


    /**
     * 容错处理
     *
     * @param modelMd5 模型md5
     */
    public void haOper(String modelMd5){

        // 拿到容错策略
        Model model = templateDao.getModelByMd5(modelMd5);
        Policy policy = model.getPolicy();

        // 获取部署了当前模型的节点列表
        List<String> originalList = taskServerService.getDeployedNodeByPid(modelMd5);
        List<Node> availableList = getAvailableNodes(modelMd5);

        // 获取deployedList中不在availableList的节点
        List<String> toBeRemoved = new ArrayList<>();
        for (String ip : originalList) {
            boolean found = false;
            for (Node node : availableList) {
                if (node.getIp().equals(ip)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                toBeRemoved.add(ip);
            }
        }
        offlineService(toBeRemoved, modelMd5);

        // 再获取一次部署了当前模型的节点列表
        List<String> deployedList = taskServerService.getDeployedNodeByPid(modelMd5);

        List<String> targetList = new ArrayList<>();
        switch (policy.getHaMode()) {
            case PolicyMode.EXACTLY:


                if (deployedList.size() < policy.getCount()) {
                    int cnt = policy.getCount() - deployedList.size();
                    for (Node node : availableList) {
                        if (!deployedList.contains(node.getIp())) {
                            targetList.add(node.getIp());
                            if (--cnt == 0) {
                                break;
                            }
                        }
                    }
                }

                break;
            case PolicyMode.ALL:

                // 如果availableList不在deployedList中，则部署
                for (Node node : availableList) {
                    if (!deployedList.contains(node.getIp())) {
                        targetList.add(node.getIp());
                    }
                }

                break;
            case PolicyMode.NODES:

                // 如果availableList在policy.getTargetIp()中，则部署
                for (Node node : availableList) {
                    if (policy.getTargetIp().contains(node.getIp())) {
                        targetList.add(node.getIp());
                    }
                }

                break;
            default:
                throw new ServiceException("unknown ha mode");
        }

        // 部署模型
        HaRecord haRecord = new HaRecord();
        haRecord.setOriginIp(deployedList);
        haRecord.setTargetIp(targetList);
        if (targetList.size() > 0) {
            List<String> deployList = deployModel(new DeployDTO(model.getName(), null, modelMd5, targetList));
            haRecord.setDeployId(deployList);
            haRecordDao.save(haRecord);
        }


    }

    private void offlineService(String ip, String md5) {
        // 看下这个ip能不能ping通
        try {
            nodeClient.ping(ip);

            // ping的通的话再让部署于该节点的模型下线
            JSONArray array = nodeClient.getModelServiceInfoByPid(ip, md5);
            for (int i = 0; i < array.size(); i++) {
                JSONObject ms = array.getJSONObject(i);
                String msid = ms.getString("_id");
                try {
                    nodeClient.stopModelService(ip, msid);
                } catch (Exception e) {
                    log.error("stop model service [{}] on [{}] failed: {}", msid, ip, e.getMessage());
                }
            }


        } catch (Exception e) {

            // ping不通的话就不做处理了

        }
    }

    private void offlineService(List<String> targetIp, String md5) {

        for (String ip : targetIp) {
            offlineService(ip, md5);
        }

    }

    private Policy buildPolicy(Policy policy) {

        Policy newPolicy = new Policy();

        String mode = policy.getHaMode();
        newPolicy.setHaMode(mode);
        newPolicy.setPolicyName(policy.getPolicyName());
        switch (mode) {
            case PolicyMode.EXACTLY:
                if (policy.getCount() == null) {
                    throw new ServiceException("exact count is required");
                }
                newPolicy.setCount(policy.getCount());
                break;
            case PolicyMode.ALL:
                break;
            case PolicyMode.NODES:
                if (policy.getTargetIp() == null || policy.getTargetIp().size() == 0) {
                    throw new ServiceException("target node is required");
                }
                newPolicy.setTargetIp(policy.getTargetIp());
                break;
            default:
                throw new ServiceException("unknown ha mode");
        }

        return newPolicy;

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
        nodeClient.downloadPackage(ip, md5, tmpPath);
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
