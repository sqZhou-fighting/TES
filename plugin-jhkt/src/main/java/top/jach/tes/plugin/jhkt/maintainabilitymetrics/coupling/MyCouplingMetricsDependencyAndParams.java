package top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling;

import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.TopicUtil;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.tes.code.dependency.CSPair;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/10/22
 * @description: 基于MyCouplingMetricsDependency中的指标添加了参数所包含的信息量的影响
 * 要根据依赖中的topic名字找到其参数，所以先要对多有微服务中的topicStruct做一个统计，拿到系统所有topic
 */

public class MyCouplingMetricsDependencyAndParams {

    public static double incomingCouplingOfSByDependencyAndParams(String microserviceName,
                                                                  List<CSPair> allDependencies,
                                                                  Map<String, Integer> topics) {
        double res = 0.0;
        for (CSPair csPair : allDependencies) {
            double tmp = 0.0;
            // 找到目标服务的入度依赖
            if (csPair.getReq().getDest_cs().equals(microserviceName)) {
                // 如何这条依赖的topic有数据结构
                if (topics.get(csPair.getReq().getTopic()) != null) {
                    // 每个参数权重为0.1
                    tmp += topics.get(csPair.getReq().getTopic()) * 0.1;
                    // 如果还有rsp
                    if (csPair.getRsp() != null) {
                        if (topics.get(csPair.getRsp().getTopic()) != null) {
                            // 加上rsp的topic的参数，并将总参数的值*2(表示双向依赖耦合强于单向依赖)
                            tmp = (tmp + topics.get(csPair.getRsp().getTopic()) * 0.1) * 2;
                        }
                    }
                }
            }
            res += tmp;
        }
        return res;
    }

    public static double outgoingCouplingOfSByDependencyAndParams(String microserviceName,
                                                                  List<CSPair> allDependencies,
                                                                  Map<String, Integer> topics) {
        double res = 0.0;

        for (CSPair csPair : allDependencies) {
            double tmp = 0.0;
            if (csPair.getReq().getSrc_cs().equals(microserviceName)) {
                if (topics.get(csPair.getReq().getTopic()) != null) {
                    tmp += topics.get(csPair.getReq().getTopic()) * 0.1;
                    if (csPair.getRsp() != null) {
                        if (topics.get(csPair.getRsp().getTopic()) != null) {
                            tmp = (tmp + topics.get(csPair.getRsp().getTopic()) * 0.1) * 2;
                        }
                    }
                }
            }
            res += tmp;
        }
        return res;
    }

    public static double totalCouplingOfSByDependencyAndParams(String microserviceName,
                                                               List<CSPair> allDependencies,
                                                               Map<String, Integer> topics) {
        double res1 = 0.0;
        double res2 = 0.0;
        for (CSPair csPair : allDependencies) {
            double tmp1 = 0.0;
            double tmp2 = 0.0;
            if (csPair.getReq().getSrc_cs().equals(microserviceName)) {
                if (topics.get(csPair.getReq().getTopic()) != null) {
                    tmp1 += topics.get(csPair.getReq().getTopic()) * 0.1;
                    if (csPair.getRsp() != null) {
                        if (topics.get(csPair.getRsp().getTopic()) != null) {
                            tmp1 = (tmp1 + topics.get(csPair.getRsp().getTopic()) * 0.1) * 2;
                        }
                    }
                }

            }
            res1 += tmp1;
            if (csPair.getReq().getDest_cs().equals(microserviceName)) {
                if (topics.get(csPair.getReq().getTopic()) != null) {
                    tmp2 += topics.get(csPair.getReq().getTopic()) * 0.1;
                    if (csPair.getRsp() != null) {
                        if (topics.get(csPair.getRsp().getTopic()) != null) {
                            tmp2 = (tmp2 + topics.get(csPair.getRsp().getTopic()) * 0.1) * 2;
                        }
                    }
                }
            }
            res2 += tmp2;
        }
        return res1 + res2;
    }

    public static double callsCouplingInServicePairAndParams(String microserviceName1,
                                                             String microserviceName2,
                                                             List<CSPair> allDependencies,
                                                             Map<String, Integer> topics) {

        double res1 = 0.0;
        double res2 = 0.0;

        for (CSPair csPair : allDependencies) {
            double tmp1 = 0.0;
            double tmp2 = 0.0;
            if (csPair.getReq().getSrc_cs().equals(microserviceName1) &&
                    csPair.getReq().getDest_cs().equals(microserviceName2)) {
                if (topics.get(csPair.getReq().getTopic()) != null) {
                    tmp1 += topics.get(csPair.getReq().getTopic()) * 0.1;
                    if (csPair.getRsp() != null) {
                        if (topics.get(csPair.getRsp().getTopic()) != null) {
                            tmp1 = (tmp1 + topics.get(csPair.getRsp().getTopic()) * 0.1) * 2;
                        }
                    }
                }
                res1 += tmp1;

            }
            if (csPair.getReq().getDest_cs().equals(microserviceName1) &&
                    csPair.getReq().getSrc_cs().equals(microserviceName2)) {
                if (topics.get(csPair.getReq().getTopic()) != null) {
                    tmp2 += topics.get(csPair.getReq().getTopic()) * 0.1;
                    if (csPair.getRsp() != null) {
                        if (topics.get(csPair.getRsp().getTopic()) != null) {
                            tmp2 = (tmp2 + topics.get(csPair.getRsp().getTopic()) * 0.1) * 2;
                        }
                    }
                }
                res2 += tmp2;
            }
        }
        return res1 != 0.0 && res2 != 0.0 ? (res1 + res2) * 1.5 : (res1 + res2);
    }


}
