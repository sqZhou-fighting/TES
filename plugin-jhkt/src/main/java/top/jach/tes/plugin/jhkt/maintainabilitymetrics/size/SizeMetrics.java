package top.jach.tes.plugin.jhkt.maintainabilitymetrics.size;

import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.jhkt.microservice.Pkg;

import java.util.*;

/**
 * @Author: zhoushiqi
 * @date: 2020/8/27
 * @description: size类指标实现，方法注释格式为“指标ID 指标名称”
 * A -> topicX -> B 其中A为生产者，B为消费者
 */
public class SizeMetrics {

    // 25 Number of Services
    public static int numberOfService(List<Microservice> microservicesList) {
        return microservicesList.size();
    }

    /*Metrics in this group are used to measure the characteristics of consumer
    and producer services either directly connected to a given service.
    */
    // 9 Number of Directly Connected Consumer Services
    public static Map<String, Integer> numberOfConsumerServicesDirectly(Map<String, Set<String>> outDegree) {
        if (outDegree == null || outDegree.size() == 0) {
            return null;
        }
        Map<String, Integer> directlyConsumers = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : outDegree.entrySet()) {
            directlyConsumers.put(entry.getKey(), entry.getValue().size());
        }
        return directlyConsumers;
    }

    // 8 Number of Directly Connected Producer Serivces
    public static Map<String, Integer> numberOfProducerServicesDirectly(Map<String, Set<String>> inDegree) {
        if (inDegree == null || inDegree.size() == 0) {
            return null;
        }
        Map<String, Integer> directlyProducers = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : inDegree.entrySet()) {
            directlyProducers.put(entry.getKey(), entry.getValue().size());
        }
        return directlyProducers;
    }

    // 10 = (8 + 9) / number of service in system
    public static Map<String, Integer> avgNumberOfConnectedServicesDirectly(Map<String, Set<String>> inDegree,
                                                                            Map<String, Set<String>> rOutDegree,
                                                                            Integer number){
        if (inDegree == null || inDegree.size() == 0) {
            return null;
        }
        if (rOutDegree == null || rOutDegree.size() == 0) {
            return null;
        }
        Map<String, Set<String>> outDegree = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : rOutDegree.entrySet()){
            outDegree.put(entry.getKey(), entry.getValue());
        }
        Map<String, Integer> directlyServices = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : inDegree.entrySet()) {
            int tmp;
            tmp = (entry.getValue().size() + outDegree.get(entry.getKey()).size()) / 2;
            directlyServices.put(entry.getKey(), tmp);
            outDegree.remove(entry.getKey());
        }
        if (outDegree.size() != 0){
            for (Map.Entry<String, Set<String>> entry : outDegree.entrySet()){
                directlyServices.put(entry.getKey(), entry.getValue().size() / 2);
            }
        }
        return directlyServices;
    }

    // n1 Number of Packages
    public static Map<String, Integer> numberOfPackages(MicroservicesInfo microservicesInfo) {
        Map<String, Integer> res = new HashMap<>();
        for (Microservice microservice : microservicesInfo.getMicroservices()) {
            res.put(microservice.getElementName(), microservice.getPkgs().size());
        }
        return res;
    }

    // n2 Number of Classes
    public static Map<String, Integer> numberOfClasses(MicroservicesInfo microservicesInfo) {
        Map<String, Integer> res = new HashMap<>();
        for (Microservice microservice : microservicesInfo.getMicroservices()) {
            int tmp = 0;
            for (Pkg pkg : microservice.getPkgs()) {
                for (Map.Entry<String, List<String>> entry : pkg.getStructs().entrySet()) {
                    tmp += entry.getValue().size();
                }
            }
            res.put(microservice.getElementName(), tmp);
        }
        return res;
    }

    // n3 Number of interfaces
    public static Map<String, Integer> numberOfInterfaces(MicroservicesInfo microservicesInfo) {
        Map<String, Integer> res = new HashMap<>();
        for (Microservice microservice : microservicesInfo.getMicroservices()) {
            int tmp = 0;
            for (String str : microservice.getSubTopics()) {
                if (microservice.getSubTopicOneOf().get(str) != null) {
                    tmp += microservice.getSubTopicOneOf().get(str);
                } else {
                    tmp += 1;
                }
            }
            res.put(microservice.getElementName(), tmp);
        }
        return res;
    }


}
