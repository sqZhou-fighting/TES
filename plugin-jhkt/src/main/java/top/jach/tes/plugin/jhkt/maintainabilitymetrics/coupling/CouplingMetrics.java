package top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling;

import top.jach.tes.plugin.jhkt.maintainabilitymetrics.size.SizeMetrics;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.CAP;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;

import java.util.*;

/**
 * @Author: zhoushiqi
 * @date: 2020/8/28
 * @description: coupling类可维护性指标实现 方法注释格式为“指标ID 指标名称”
 */
public class CouplingMetrics {
    // 96 Average Number of Directly Connected Services
    public static int avgNumServicesDirectly(Map<String, Set<String>> inDegree,
                                             Map<String, Set<String>> outDegree) {
        int res = 0;
        for (Map.Entry<String, Set<String>> entry : inDegree.entrySet()) {
            res += entry.getValue().size() + outDegree.get(entry.getKey()).size();
        }
        return res / inDegree.size();
    }

    // 88 Relative Coupling of Service 给定服务的出度
    public static Map<String, Double> relativeCouplingOfService(Map<String, Set<String>> outDegree,
                                                                MicroservicesInfo microservicesInfo) {
        if (outDegree == null || outDegree.size() == 0) {
            return null;
        }

        Map<String, Double> relativeCouplings = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : outDegree.entrySet()) {
            relativeCouplings.put(entry.getKey(),
                    (entry.getValue().size() * 1.0 / microservicesInfo.getMicroservices().size()));
        }
        return relativeCouplings;
    }

    // 89 Relative Importance of Service 给定服务的入度
    public static Map<String, Double> relativeImportanceOfService(Map<String, Set<String>> inDegree,
                                                                  MicroservicesInfo microservicesInfo) {
        if (inDegree == null || inDegree.size() == 0) {
            return null;
        }

        Map<String, Double> relativeImportances = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : inDegree.entrySet()) {
            relativeImportances.put(entry.getKey(),
                    (entry.getValue().size() * 1.0 / microservicesInfo.getMicroservices().size()));
        }

        return relativeImportances;
    }

    // 90 Service Coupling Factor
    public static double servicesCouplingFactor(Map<String, Set<String>> outDegree) {
        if (outDegree == null || outDegree.size() == 0) {
            return -1;
        }

        int tmp = 0;

        for (Map.Entry<String, Set<String>> entry : outDegree.entrySet()) {
            tmp += entry.getValue().size();
        }

        return (double) (tmp / (outDegree.size() * (outDegree.size() - 1)));
    }

    // 91 System’s Service Coupling
    public static double sysServicesCoupling(Map<String, Set<String>> outDegree, CAP cap) {
        if (outDegree == null || outDegree.size() == 0) {
            return -1;
        }

        List<String> consumers = new ArrayList<>(cap.getConsumers());
        consumers.addAll(cap.getAggregators());

        List<String> providers = new ArrayList<>(cap.getProviders());
        providers.addAll(cap.getAggregators());

        int tmp = 0;
        for (String msName : consumers) {
            tmp += outDegree.get(msName).size();
        }

        return (double) (tmp / (consumers.size() * providers.size()));
    }

    // 142 Absolute Criticality of the Service
    public static Map<String, Double> absCriticalityOfService(Map<String, Set<String>> inDegree,
                                                              Map<String, Set<String>> outDegree,
                                                              MicroservicesInfo microservicesInfo) {
        if (inDegree == null || outDegree == null){
            return null;
        }
        Map<String, Double> rcs = relativeCouplingOfService(outDegree, microservicesInfo);
        Map<String, Double> ris = relativeImportanceOfService(inDegree, microservicesInfo);

        Map<String, Double> absCriticality = new HashMap<>();
        for (Map.Entry<String, Double> entry : rcs.entrySet()){
            absCriticality.put(entry.getKey(), entry.getValue() * ris.get(entry.getKey()));
        }

        return absCriticality;
    }

    //
}
