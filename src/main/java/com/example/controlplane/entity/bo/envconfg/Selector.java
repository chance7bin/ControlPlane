package com.example.controlplane.entity.bo.envconfg;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 节点选择标签规范
 *
 * @author 7bin
 * @date 2024/03/01
 */
@Data
@XmlRootElement(name = "Selector")
@XmlAccessorType(XmlAccessType.FIELD)
public class Selector {

    @XmlElement(name = "Required")
    Required required;

    @XmlElement(name = "Preference")
    List<Preference> preferences;

}
