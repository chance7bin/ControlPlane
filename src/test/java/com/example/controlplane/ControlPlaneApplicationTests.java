package com.example.controlplane;

import com.example.controlplane.clients.EngineClient;
import com.example.controlplane.clients.NodeClient;
import com.example.controlplane.dao.ModelDao;
import com.example.controlplane.dao.PolicyDao;
import com.example.controlplane.dao.TemplateDao;
import com.example.controlplane.entity.bo.envconfg.*;
import com.example.controlplane.entity.po.FileInfo;
import com.example.controlplane.entity.po.Node;
import com.example.controlplane.service.IFileService;
import com.example.controlplane.service.ITaskServerService;
import com.example.controlplane.service.impl.SelectorUtils;
import com.example.controlplane.utils.XMLUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
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
        Boolean rsp = nodeClient.checkDeployed("localhost" , "818daf6969e1d7472739677b81f3bd98");
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
        // boolean localhost = engineClient.checkDocker("223.2.43.157");
        // System.out.println();

        long l = Long.parseLong("7.04");
        System.out.println(l);

    }

    @Test
    void testSort(){

        List<Preference> preferences = new ArrayList<>();
        Preference preference = new Preference();
        preference.setWeight(8);
        MatchExpressions matchExpressions = new MatchExpressions();
        List<Expression> expressions = new ArrayList<>();
        expressions.add(new Expression("cpuNum", "Ge", "8"));
        expressions.add(new Expression("totalMem", "Ge", "16GB"));
        matchExpressions.setExpressions(expressions);
        preference.setMatchExpressions(matchExpressions);
        preferences.add(preference);

        Preference preference2 = new Preference();
        preference2.setWeight(6);
        MatchLabels matchLabels = new MatchLabels();
        List<Label> labels = new ArrayList<>();
        labels.add(new Label("network_connection", "true"));
        matchLabels.setLabels(labels);
        preference2.setMatchLabels(matchLabels);
        preferences.add(preference2);

        List<Node> nodes = new ArrayList<>();
        Node node1 = new Node();
        node1.setIp("node1");
        List<com.example.controlplane.entity.bo.Label> labels1 = new ArrayList<>();
        labels1.add(new com.example.controlplane.entity.bo.Label("cpuNum", "32", true));
        labels1.add(new com.example.controlplane.entity.bo.Label("totalMem", "16GB", true));
        node1.setLabels(labels1);
        nodes.add(node1);

        Node node3 = new Node();
        node3.setIp("node3");
        List<com.example.controlplane.entity.bo.Label> labels3 = new ArrayList<>();
        labels3.add(new com.example.controlplane.entity.bo.Label("cpuNum", "16", true));
        labels3.add(new com.example.controlplane.entity.bo.Label("totalMem", "8GB", true));
        node3.setLabels(labels3);
        nodes.add(node3);

        Node node4 = new Node();
        node4.setIp("node4");
        List<com.example.controlplane.entity.bo.Label> labels4 = new ArrayList<>();
        labels4.add(new com.example.controlplane.entity.bo.Label("cpuNum", "4", true));
        labels4.add(new com.example.controlplane.entity.bo.Label("totalMem", "16GB", true));
        labels4.add(new com.example.controlplane.entity.bo.Label("network_connection", "true", true));
        node4.setLabels(labels4);
        nodes.add(node4);

        Node node5 = new Node();
        node5.setIp("node5");
        List<com.example.controlplane.entity.bo.Label> labels5 = new ArrayList<>();
        labels5.add(new com.example.controlplane.entity.bo.Label("cpuNum", "8", true));
        labels5.add(new com.example.controlplane.entity.bo.Label("totalMem", "32GB", true));
        node5.setLabels(labels5);
        nodes.add(node5);

        nodes = SelectorUtils.sort(preferences, nodes);

        System.out.println();

    }

}
