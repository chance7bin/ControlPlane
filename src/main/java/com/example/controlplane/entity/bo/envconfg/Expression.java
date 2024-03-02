package com.example.controlplane.entity.bo.envconfg;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 7bin
 * @date 2024/03/01
 */
@Data
@XmlRootElement(name = "Expression")
@XmlAccessorType(XmlAccessType.FIELD)
public class Expression {

    @XmlAttribute(name = "key")
    String key;

    @XmlAttribute(name = "operator")
    String operator;

    @XmlAttribute(name = "value")
    String value;

}
