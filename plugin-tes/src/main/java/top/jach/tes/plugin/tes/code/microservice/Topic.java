package top.jach.tes.plugin.tes.code.microservice;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: zhoushiqi
 * @date: 2020/6/28
 */
//该类为topic
@Getter
@Setter
public class Topic {
    String topicName;
    int structNum;  //该topic对应的结构种类
    Set<Struct> structs = new HashSet<>();
}
