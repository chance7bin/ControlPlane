package com.example.controlplane.entity.bo.envconfg;

import lombok.Data;

import javax.xml.bind.annotation.*;

/**
 * @author 7bin
 * @date 2024/03/01
 */
@Data
@XmlRootElement(name = "Preference")
@XmlAccessorType(XmlAccessType.FIELD)
public class Preference {

    @XmlAttribute(name = "weight")
    Integer weight;

    @XmlElement(name = "MatchLabels")
    MatchLabels matchLabels;

    @XmlElement(name = "MatchExpressions")
    MatchExpressions matchExpressions;

}
