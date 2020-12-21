package top.jach.tes.plugin.jhkt.microservice;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/6/28
 */
@Getter
@Setter
public class CallAPI {
    String name;
    Map<Parameter, Integer> params = new HashMap<>();
    String MicroserviceName;  //callAPI所在微服务
}
