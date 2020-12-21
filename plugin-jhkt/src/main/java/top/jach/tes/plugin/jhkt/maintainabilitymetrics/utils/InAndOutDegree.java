package top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils;

import lombok.*;

import java.util.Map;
import java.util.Set;

/**
 * @Author: zhoushiqi
 * @date: 2020/8/28
 * @description: 该类为系统微服务及其出入度统计map类
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InAndOutDegree {
    private Map<String, Set<String>> inDegree;
    private Map<String, Set<String>> outDegree;
}
