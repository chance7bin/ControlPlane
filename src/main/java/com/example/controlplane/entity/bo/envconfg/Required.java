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
@XmlRootElement(name = "Required")
@XmlAccessorType(XmlAccessType.FIELD)
public class Required {

    @XmlElement(name = "MatchLabels")
    MatchLabels matchLabels;

    @XmlElement(name = "MatchExpressions")
    MatchExpressions MatchExpressions;

}
