package top.jach.tes.plugin.jhkt.microservice;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/9/28
 * @description: 类的数据结构
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TopicStruct {
    String topicName;  //topic名称
    String structName;  //topic对应的数据结构的名称
    int count;  //数据结构中参数个数
    List<OneOf> oneofs = new ArrayList<>();  //该topic所对应的oneof的列表
    Map<String, String> params = new HashMap<>();  //topic对应的数据结构中的参数列表<参数名，参数类型>，其中，代表oneof的参数的参数类型标识为“oneof”
}
