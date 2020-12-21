package top.jach.tes.plugin.jhkt.microservice;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/9/28
 * @description: 包的数据结构
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pkg {
    String pkgName;  //包名
    String pkgPath;  //包路径
    List<String> files = new ArrayList<>();  //包中所含文件（文件名）
    Map<String, List<String>> invokedPkgs = new HashMap<>();  //包中文件及该文件调用的包名<文件名， List<包名>>
    Map<String, List<String>> structs = new HashMap<>();  //包中文件及文件中声明的类<文件名， List<类名>>
}
