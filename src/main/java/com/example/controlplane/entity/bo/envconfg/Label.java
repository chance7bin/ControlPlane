package com.example.controlplane.entity.bo.envconfg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 7bin
 * @date 2024/03/01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Label")
@XmlAccessorType(XmlAccessType.FIELD)
public class Label {

    @XmlAttribute(name = "key")
    String key;

    @XmlAttribute(name = "value")
    String value;

}
