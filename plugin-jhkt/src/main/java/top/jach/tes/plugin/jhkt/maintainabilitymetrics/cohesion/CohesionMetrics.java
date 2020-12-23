package top.jach.tes.plugin.jhkt.maintainabilitymetrics.cohesion;

import org.omg.CORBA.INTERNAL;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.TopicStruct;
import top.jach.tes.plugin.tes.code.dependency.CSPair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: zhoushiqi
 * @date: 2020/9/28
 * @description: 耦合度度量指标
 */
public class CohesionMetrics {
    //74 - Service Interface Data Cohesion
    public static double SIDC(Microservice microservice) {
        List<TopicStruct> structs = microservice.getStructs();
        int allInterface = 0;
        Map<String, Integer> paramsStatistics = new HashMap<>();

        for (TopicStruct struct : structs) {
            // topic含有oneof时，以oneof为主
            if (struct.getOneofs().size() != 0) {
                allInterface += struct.getOneofs().size();
                for (int i = 0; i < struct.getOneofs().size(); i++) {
                    Map<String, String> structParams = struct.getParams();
                    for (Map.Entry<String, String> entry : structParams.entrySet()) {
                        if (entry.getValue().equals("oneof")) {
                            continue;
                        }
                        paramsStatistics.merge(entry.getValue(), 1, (oldValue, newValue) -> oldValue + newValue);
                    }
                    for (Map.Entry<String, String> entry : struct.getOneofs().get(i).getParams().entrySet()) {
                        paramsStatistics.merge(entry.getValue(), 1, (oldValue, newValue) -> oldValue + newValue);
                    }
                }
            } else {
                allInterface += 1;
                Map<String, String> structParams = struct.getParams();
                for (Map.Entry<String, String> entry : structParams.entrySet()) {
                    paramsStatistics.merge(entry.getValue(), 1, (oldValue, newValue) -> oldValue + newValue);
                }
            }
        }

        int count = 0;
        for (Map.Entry<String, Integer> entry : paramsStatistics.entrySet()) {
            if (entry.getValue() >= allInterface) {
                int x = 0;
                for (TopicStruct struct : structs) {
                    if (struct.getOneofs().size() != 0) {
                        for (int i=0; i<struct.getOneofs().size(); i++){
                            Map<String, String> params = new HashMap<>();
                            params.putAll(struct.getParams());
                            params.putAll(struct.getOneofs().get(i).getParams());
                            for (Map.Entry<String, String> entry1 : params.entrySet()){
                                if (entry1.getValue().equals(entry.getKey())){
                                    x ++;
                                    break;
                                }
                            }
                        }
                    } else {
                        for (Map.Entry<String, String> entry1 : struct.getParams().entrySet()){
                            if (entry1.getValue().equals(entry.getKey())){
                                x ++;
                                break;
                            }
                        }
                    }
                }
                if (x == allInterface) {
                    count++;
                }
            }
        }
        System.out.println(microservice.getElementName());
        System.out.println("allInterface   " + allInterface);
        System.out.println("struct num   " + structs.size());
        System.out.println("count   " + count);
        System.out.println("papramsStatistic   " + paramsStatistics.size());
        System.out.println();
        if (paramsStatistics.size() == 0) {
            return 0;
        }
        return (count * 1.0) / paramsStatistics.size();
    }

    // 75 - Service Interface Usage Cohesion
    public static double SIUC(Microservice microservice, List<CSPair> dependencies) {
        int count = 0;
        Map<String, Integer> middleRes = new HashMap<>();
        for (CSPair csPair : dependencies) {
            if (csPair.getReq().getDest_cs().equals(microservice.getElementName())) {
                middleRes.merge(csPair.getReq().getSrc_cs(), 1, (oldValue, newValue) -> (oldValue + newValue));
            }
        }
        for (Map.Entry<String, Integer> entry : middleRes.entrySet()) {
            if (entry.getValue() > 1) {
                entry.setValue(entry.getValue() - 1);
                count += entry.getValue();
            }
        }
        if (middleRes.size() == 0 || microservice.getSubTopics().size() == 0) {
            return 0;
        }

        int allInterface = 0;
        List<TopicStruct> structs = microservice.getStructs();
        for (TopicStruct struct : structs) {
            if (struct.getOneofs().size() != 0) {
                allInterface += struct.getOneofs().size();
            } else {
                allInterface += 1;
            }
        }

        if (middleRes.size() == 0 || allInterface == 0) {
            return 0;
        }
        return (count * 1.0) / (middleRes.size() * allInterface);
    }

    // 105 - Total Interface Cohesion of a Service
    public static double TICS(Microservice microservice, List<CSPair> dependencies) {
        double x = SIDC(microservice);
        double y = SIUC(microservice, dependencies);
        return (x + y) / 2;
    }


}
