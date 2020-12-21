package top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils;

import lombok.*;

import java.util.List;

/**
 * @Author: zhoushiqi
 * @date: 2020/8/28
 * @description: 系统consumer aggregator and provider 集合
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CAP {
    // consumer指的是只有消费能力的服务，即出度 > 0，而入度 = 0
    private List<String> consumers;
    // aggregator指的是兼具消费与服务能力的服务，即出度 > 0，而入度 > 0
    private List<String> aggregators;
    // provider指的是只有服务能力的服务，即出度 = 0，而入度 > 0
    private List<String> providers;
}
