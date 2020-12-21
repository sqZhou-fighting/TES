package top.jach.tes.plugin.jhkt.microservice;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/9/28
 * @description: oneof的数据结构
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OneOf {
    String name;  //oneof的名称
    int count;  //参数个数
    Map<String, String> params = new HashMap<>();  //参数列表 <参数名， 参数类型>
}
