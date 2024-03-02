package com.example.controlplane;

import com.example.controlplane.clients.NodeClient;
import com.example.controlplane.entity.bo.envconfg.ModelEnv;
import com.example.controlplane.utils.XMLUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        nodeClient.checkDeployed("localhost", "8060", "c54c62edf719df780d0a0a5da9bae");
    }

}
