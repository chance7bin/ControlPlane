package com.example.controlplane.service.impl;

import com.example.controlplane.constant.Operator;
import com.example.controlplane.entity.bo.Label;
import com.example.controlplane.entity.bo.envconfg.*;
import com.example.controlplane.entity.po.Node;
import com.example.controlplane.utils.file.FileUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 节点选择器工具类
 *
 * @author 7bin
 * @date 2024/03/02
 */
public class SelectorUtils {


    /**
     * 算数运算符比较器 根据operator选择排序方式
     */
    public static int comparator(String a, String b, String operator){

        if (Operator.GREATER_THAN.equals(operator)){
            return FileUtils.compare(b, a);
        } else if (Operator.LESS_THAN.equals(operator)){
            return FileUtils.compare(a, b);
        } else if (Operator.GREATER_THAN_EQUAL.equals(operator)){
            return FileUtils.compare(b, a);
        } else if (Operator.LESS_THAN_EQUAL.equals(operator)){
            return FileUtils.compare(a, b);
        } else {
            throw new IllegalArgumentException("不支持的操作符");
        }

    }

    /**
     * 排序（按优先级降序排序）
     * @return 排序后的node列表（仅包含存在指定label的节点）
     */
    public static List<Node> sort(List<Preference> preferences, List<Node> nodes){

        if (nodes == null || nodes.isEmpty()){
            return new ArrayList<>();
        }

        if (preferences == null || preferences.isEmpty()){
            return new ArrayList<>(nodes);
        }


        List<Node> result = new ArrayList<>();

        // 先根据preference的weight字段进行排序
        preferences.sort((o1, o2) -> o2.getWeight() - o1.getWeight());

        // 再根据相应label和expression排序
        List<Node> candidates = new ArrayList<>(nodes);

        for (Preference preference : preferences) {
            MatchLabels matchLabels = preference.getMatchLabels();
            if (matchLabels != null && !matchLabels.getLabels().isEmpty()){
                for (com.example.controlplane.entity.bo.envconfg.Label lb : matchLabels.getLabels()) {
                    List<Node> sorted = sort(lb, candidates);
                    result.addAll(sorted);
                    candidates = difference(candidates, sorted);
                }
            }

            if (preference.getMatchExpressions() != null && !preference.getMatchExpressions().getExpressions().isEmpty()){
                MatchExpressions matchExpressions = preference.getMatchExpressions();
                for (Expression exp : matchExpressions.getExpressions()) {
                    List<Node> sorted = sort(exp, candidates);
                    result.addAll(sorted);
                    candidates = difference(candidates, sorted);
                }
            }

        }

        result.addAll(candidates);

        return result;
    }

    // nodes1 - nodes2
    public static List<Node> difference(List<Node> nodes1, List<Node> nodes2) {
        List<Node> result = new ArrayList<>(nodes1);
        result.removeAll(nodes2);
        return result;
    }

    /**
     * 排序（按label的value值排序）
     * @return 排序后的node列表（仅包含存在指定label的节点）
     */
    public static List<Node> sort(com.example.controlplane.entity.bo.envconfg.Label label, List<Node> nodes) {

        String key = label.getKey();
        String value = label.getValue();

        // 深拷贝nodes
        List<Node> tmp = new ArrayList<>(nodes);

        removeIfNotContainsLabel(key, value, tmp);

        // nodes.sort((o1, o2) -> {
        //     // 获取node的label列表里key为指定key的label
        //     Label label1 = o1.getLabels().stream().filter(l -> l.getKey().equals(key)).findFirst().orElse(null);
        //     Label label2 = o2.getLabels().stream().filter(l -> l.getKey().equals(key)).findFirst().orElse(null);
        //     if (label1 == null && label2 == null){
        //         return 0;
        //     } else if (label1 == null){
        //         return 1;
        //     } else if (label2 == null){
        //         return -1;
        //     }
        //
        //     // node中的label包含value的排在前面
        //     if (label1.getValue().equals(value) && !label2.getValue().equals(value)){
        //         return -1;
        //     } else if (!label1.getValue().equals(value) && label2.getValue().equals(value)){
        //         return 1;
        //     }
        //     return 0;
        // });

        return tmp;

    }

    private static void removeIfNotContainsLabel(String key, String value, List<Node> nodes) {
        nodes.removeIf(node -> {
            Label lb = node.getLabels().stream().filter(l -> l.getKey().equals(key)).findFirst().orElse(null);
            if (lb != null) {
                return !value.equals(lb.getValue());
            }
            return true;
        });
    }

    /**
     * 排序（按expression排序）
     * @return 排序后的node列表（仅包含存在指定label的节点）
     */
    public static List<Node> sort(Expression expression, List<Node> nodes) {

        String key = expression.getKey();
        String op = expression.getOperator();

        // 深拷贝nodes
        List<Node> copyNodes = new ArrayList<>(nodes);

        Set<Node> tmp = new HashSet<>();

        copyNodes.sort((o1, o2) -> {
            // 获取node的label列表里key为指定key的label
            Label label1 = o1.getLabels().stream().filter(label -> label.getKey().equals(key)).findFirst().orElse(null);
            Label label2 = o2.getLabels().stream().filter(label -> label.getKey().equals(key)).findFirst().orElse(null);
            if (label1 == null && label2 == null){
                return 0;
            } else if (label1 == null){
                tmp.add(o2);
                return 1;
            } else if (label2 == null){
                tmp.add(o1);
                return -1;
            }
            tmp.add(o1);
            tmp.add(o2);
            return comparator(label1.getValue(), label2.getValue(), op);
        });

        List<Node> res = new ArrayList<>();
        copyNodes.forEach(node -> {
            if (tmp.contains(node)){
                res.add(node);
            }
        });

        return new ArrayList<>(res);
    }



    /**
     * 过滤节点
     */
    public static List<Node> filter(Required required, List<Node> nodes) {

        if (nodes == null || nodes.isEmpty()){
            return new ArrayList<>();
        }

        if (required == null || (required.getMatchLabels().getLabels().isEmpty() && required.getMatchExpressions().getExpressions().isEmpty())){
            return new ArrayList<>(nodes);
        }

        List<Node> copyNodes = new ArrayList<>(nodes);

        required.getMatchLabels().getLabels().forEach(lb -> {
            String key = lb.getKey();
            String value = lb.getValue();
            removeIfNotContainsLabel(key, value, copyNodes);
        });

        required.getMatchExpressions().getExpressions().forEach(exp -> {
            String key = exp.getKey();
            String value = exp.getValue();
            String op = exp.getOperator();
            copyNodes.removeIf(node -> {
                List<Label> labels = node.getLabels();
                return !match(key, value, op, labels);
            });
        });

        return copyNodes;
    }

    // 判断操作符是否是算符运算符
    public static boolean isArithOp(String operator) {
        return Operator.GREATER_THAN.equals(operator)
            || Operator.LESS_THAN.equals(operator)
            || Operator.GREATER_THAN_EQUAL.equals(operator)
            || Operator.LESS_THAN_EQUAL.equals(operator)
            || Operator.EQUAL.equals(operator)
            || Operator.NOT_EQUAL.equals(operator);
    }


    /**
     * 判断表达式是否匹配
     */
    public static boolean match(String key, String value, String operator, List<Label> labels) {
        String val1 = null; // 节点信息，待比较
        String val2 = value; // 标签信息
        for (Label label : labels) {
            if (label.getKey().equals(key)) {
                val1 = label.getValue();
                break;
            }
        }
        if (val1 == null) {
            return false;
        }

        if (isArithOp(operator)) {

            if (Operator.GREATER_THAN.equals(operator)){
                return FileUtils.compare(val1, val2) > 0;
            } else if (Operator.LESS_THAN.equals(operator)){
                return FileUtils.compare(val1, val2) < 0;
            } else if (Operator.GREATER_THAN_EQUAL.equals(operator)){
                return FileUtils.compare(val1, val2) >= 0;
            } else if (Operator.LESS_THAN_EQUAL.equals(operator)){
                return FileUtils.compare(val1, val2) <= 0;
            } else if (Operator.EQUAL.equals(operator)){
                return val1.equals(val2);
            } else if (Operator.NOT_EQUAL.equals(operator)){
                return !val1.equals(val2);
            } else {
                throw new IllegalArgumentException("[" + key + operator + value + "] 不支持的操作符: " + operator);
            }

        } else if (Operator.IN.equals(operator)) {
            return in(key, value, labels);
        } else if (Operator.NOT_IN.equals(operator)) {
            return notIn(key, value, labels);
        } else if (Operator.EXISTS.equals(operator)) {
            return exists(key, labels);
        } else if (Operator.DOES_NOT_EXIST.equals(operator)) {
            return doesNotExist(key, labels);
        } else {
            throw new IllegalArgumentException("[" + key + operator + value + "] 不支持的操作符: " + operator);
        }

    }


    public static boolean in(String key, String value, List<Label> labels) {
        // 将[a, b, c]字符串转换为 list列表
        // String arr = value.substring(1, value.length() - 1);
        // String[] arrList = arr.split(",");
        for (Label label : labels) {
            if (label.getKey().equals(key)) {
                // key相同时，判断value是否在arrList中
                return value.contains(label.getValue());
            }
        }
        return false;
    }

    public static boolean notIn(String key, String value, List<Label> labels) {
        for (Label label : labels) {
            if (label.getKey().equals(key)) {
                // key相同时，判断value是否在arrList中
                return !value.contains(label.getValue());
            }
        }
        return true;
    }


    public static boolean exists(String key, List<Label> labels) {
        for (Label label : labels) {
            if (label.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean doesNotExist(String key, List<Label> labels) {
        for (Label label : labels) {
            if (label.getKey().equals(key)) {
                return false;
            }
        }
        return true;
    }



}
