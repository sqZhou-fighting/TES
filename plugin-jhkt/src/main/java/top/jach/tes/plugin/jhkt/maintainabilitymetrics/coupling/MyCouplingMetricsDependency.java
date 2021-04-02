package top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling;

import top.jach.tes.plugin.tes.code.dependency.CSPair;

import java.util.List;

/**
 * @Author: zhoushiqi
 * @date: 2020/10/21
 * @description: 947自己设计的耦合部分的指标，主要涉及到了依赖之间的耦合
 */
public class MyCouplingMetricsDependency {

    // #Single5 一个服务的入度依赖及权重和
    public static int incomingCouplingOfSByDependency(List<CSPair> allDepedencies, String microserviceName) {
        int res = 0;
        for (CSPair csPair : allDepedencies){
            if (csPair.getReq().getDest_cs().equals(microserviceName)) {
                res += csPair.getRsp() != null ? 2.5 : 1;
            }
        }
        return res;
    }

    // #Single6 一个服务的出度依赖及权重和
    public static int outgoingCouplingOfSByDependency(List<CSPair> allDepedencies, String microserviceName) {
        int res = 0;
        for (CSPair csPair : allDepedencies){
            if (csPair.getReq().getSrc_cs().equals(microserviceName)) {
                res += csPair.getRsp() != null ? 2.5 : 1;
            }
        }
        return res;
    }

    // #single7 一个服务的总依赖耦合，也即single5和single6总和
    public static int totalCouplingOfSByDependency(List<CSPair> allDepedencies, String microserviceName) {
        int res = 0;
        for (CSPair csPair : allDepedencies){
            if (csPair.getReq().getSrc_cs().equals(microserviceName) ||
                    csPair.getReq().getDest_cs().equals(microserviceName)) {
                res += csPair.getRsp() != null ? 2.5 : 1;
            }
        }
        return res;
    }

    // #Double8 两个服务之间耦合度
    public static double callsCouplingInServicePair(List<CSPair> allDepedencies,
                                                    String microserviceName1,
                                                    String microserviceName2) {
        double res1 = 0.0;
        double res2 = 0.0;
        for (CSPair csPair : allDepedencies){
            if (csPair.getReq().getSrc_cs().equals(microserviceName1) &&
                    csPair.getReq().getDest_cs().equals(microserviceName2)) {
                res1 += csPair.getRsp() != null ? 2 : 1;
            }
            if (csPair.getReq().getSrc_cs().equals(microserviceName2) &&
                    csPair.getReq().getDest_cs().equals(microserviceName1)) {
                res2 += csPair.getRsp() != null ? 2 : 1;
            }
        }
        return res1 != 0.0 && res2 != 0.0 ? (res1 + res2)*1.5 : (res1 + res2);
    }
}
