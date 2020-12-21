package top.jach.tes.plugin.jhkt.microservice;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/6/28
 */
@Getter
@Setter
public class API {
    String name;  //API名称
    Map<Parameter, Integer> params = new HashMap<>();  //API各参数类型及每种类型参数的个数
}
