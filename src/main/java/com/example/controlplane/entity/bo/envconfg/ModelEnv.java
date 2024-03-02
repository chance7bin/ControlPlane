package com.example.controlplane.entity.bo.envconfg;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 7bin
 * @date 2024/03/01
 */
@Data
//根元素
@XmlRootElement(name = "ModelEnv")
//访问类型，通过字段
@XmlAccessorType(XmlAccessType.FIELD)
public class ModelEnv {

    @XmlElement(name = "Selector")
    Selector selector;

}
