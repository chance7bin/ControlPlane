package com.example.controlplane.entity.dto.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 7bin
 * @date 2024/03/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeDTO {

    String ip;

    int score;

}
