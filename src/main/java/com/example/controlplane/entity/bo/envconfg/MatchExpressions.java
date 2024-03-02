package com.example.controlplane.entity.bo.envconfg;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author 7bin
 * @date 2024/03/01
 */
@Data
@XmlRootElement(name = "MatchExpressions")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchExpressions {

    @XmlElement(name = "Expression")
    List<Expression> expressions;

}
