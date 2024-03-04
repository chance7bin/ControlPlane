package com.example.controlplane;

import com.example.controlplane.clients.EngineClient;
import com.example.controlplane.clients.NodeClient;
import com.example.controlplane.dao.ModelDao;
import com.example.controlplane.dao.PolicyDao;
import com.example.controlplane.dao.TemplateDao;
import com.example.controlplane.entity.bo.envconfg.ModelEnv;
import com.example.controlplane.entity.po.FileInfo;
import com.example.controlplane.service.IFileService;
import com.example.controlplane.service.ITaskServerService;
import com.example.controlplane.utils.XMLUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ControlPlaneApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void downloadFile() {


    }

    @Test
    void parseXML() {

        String path = "E:\\control-plane\\file\\tmp\\envconfig_c54c62edf719df78c550d0a0a5dsae.xml";
        ModelEnv o = (ModelEnv) XMLUtils.convertXmlFileToObject(ModelEnv.class, path);
        System.out.println(o);

    }

    @Autowired
    private NodeClient nodeClient;

    @Test
    void testGetModel(){
        Boolean rsp = nodeClient.checkDeployed("localhost", "8060", "818daf6969e1d7472739677b81f3bd98");
        System.out.println(rsp);
    }

    @Autowired
    ITaskServerService taskServerService;

    @Test
    void testNodeApi(){

        // JSONArray rsp = nodeClient.getModelServiceInfoByPid("localhost", "8060", "c54c62edf719df78c550d0a0a5da9bae");
        // JSONArray rsp = nodeClient.getModelServiceInfoByPid("localhost", "8060", "818daf6969e1d7472739677b81f3bd98");
        // JSONObject rsp = nodeClient.updateModel("localhost", "8060", "65def7311ba6887e084e6bdc", "start");
        // JSONObject rsp = nodeClient.getModelServiceInfoByMsid("localhost", "8060", "65d84bc4e24e7a1cb0884e9c");
        List<String> rsp = taskServerService.getDeployedNodeByPid("fbdea29202b49aff95398e4371e3bd73");

        System.out.println();

    }

    @Autowired
    TemplateDao templateDao;

    @Autowired
    PolicyDao policyDao;

    @Autowired
    ModelDao modelDao;

    @Autowired
    IFileService fileService;

    @Test
    void testLookup(){

        // Policy policy = new Policy();
        // policy.setPolicyName("test");
        // policy.setHaMode(PolicyMode.EXACTLY);
        // policy.setCount(2);
        // policyDao.insert(policy);
        //
        // Policy policy2 = new Policy();
        // policy2.setPolicyName("test2");
        // policy2.setHaMode(PolicyMode.NODES);
        // policy2.setTargetIp(new ArrayList<>(Arrays.asList("1", "2")));
        // policyDao.insert(policy2);
        //
        // Model model1 = new Model();
        // model1.setMd5("1");
        // model1.setPolicyId(policy.getId());
        // modelDao.insert(model1);
        //
        // Model model2 = new Model();
        // model2.setMd5("2");
        // model2.setPolicyId(policy2.getId());
        // modelDao.insert(model2);

        // Model model = templateDao.getModelByMd5("3");
        // System.out.println(model);

        FileInfo fileByMd5 = fileService.getFileByMd5(null);
        System.out.println();

    }

    @Autowired
    EngineClient engineClient;

    @Test
    void testDocker(){
        boolean localhost = engineClient.checkDocker("223.2.43.157");
        System.out.println();
    }

}
