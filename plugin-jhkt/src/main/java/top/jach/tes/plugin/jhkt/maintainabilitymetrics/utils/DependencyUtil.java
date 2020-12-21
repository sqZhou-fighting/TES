package top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils;

import javafx.util.Pair;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.tes.code.dependency.CSPair;
import top.jach.tes.plugin.tes.code.dependency.Direction;

import java.util.*;

/**
 * @Author: zhoushiqi
 * @date: 2020/8/27
 */
public class DependencyUtil {
    // 计算服务的出入度
    public static InAndOutDegree getInAndOutDegree(List<CSPair> dependencies, List<Microservice> allMircoservices){
        if (dependencies == null || allMircoservices == null){
            return null;
        }

        // outDegree中存放的是服务及为其提供operation的providers
        // outDegree = <src, <dest1, dest2, ..., destn>>
        Map<String, Set<String>> outDegree = new HashMap<>();
        // inDegree中存放的是服务及消费其operation的consumers
        // inDegree = <dest, <src1, src2, .., srcm>>
        Map<String, Set<String>> inDegree = new HashMap<>();
        for (Microservice ms : allMircoservices){
            outDegree.put(ms.getElementName(), new HashSet<>());
            inDegree.put(ms.getElementName(), new HashSet<>());
        }

        for (CSPair csPair : dependencies){
            Direction dir = csPair.getReq();
            outDegree.get(dir.getSrc_cs()).add(dir.getDest_cs());
            inDegree.get(dir.getDest_cs()).add(dir.getSrc_cs());
        }

        return new InAndOutDegree(inDegree, outDegree);
    }

    // 汇总serviceUtil = <serviceName, Pair<Set<serviceName1, serviceName2, ..., serviceNameN>, Set<serviceName1, serviceName2, ..., serviceNameM>>>
    // Pair<左边是该服务的消费者服务列表(入度)， 右边是为该服务提供operation的提供者(出度)>
    public static Map<String, Pair<Set<String>, Set<String>>> getServiceUtils(List<CSPair> dependencies, List<Microservice> allMircoservices){
        if (dependencies == null || allMircoservices == null){
            return null;
        }

        Map<String, Pair<Set<String>, Set<String>>> serviceUnits = new HashMap<>();
        for (Microservice ms : allMircoservices){
            serviceUnits.put(ms.getElementName(), new Pair<>(new HashSet<String>(), new HashSet<String>()));
        }

        for (CSPair csPair : dependencies){
            Direction dir = csPair.getReq();
            serviceUnits.get(dir.getSrc_cs()).getValue().add(dir.getDest_cs());
            serviceUnits.get(dir.getDest_cs()).getKey().add(dir.getSrc_cs());
        }

        return serviceUnits;
    }

    // 获取单个服务的出度和入度
    public static Pair<Set<String>, Set<String>> getServiceInAndOut(List<CSPair>dependencies, Microservice microservice){
        Pair<Set<String>, Set<String>> inAndOut = new Pair<>(new HashSet<>(), new HashSet<>());
        for (CSPair csPair : dependencies){
            Direction dir = csPair.getReq();
            if (dir.getDest_cs().equals(microservice.getElementName())){
                inAndOut.getKey().add(dir.getSrc_cs());
            }
            if (dir.getSrc_cs().equals(microservice.getElementName())){
                inAndOut.getValue().add(dir.getDest_cs());
            }
        }
        return inAndOut;
    }

    // 统计系统中的consumer aggregate and provider
    public static CAP getCAP(List<CSPair> dependencies, List<Microservice> allMircoservices){
        if (dependencies == null || allMircoservices == null){
            return null;
        }

        Map<String, Pair<Set<String>, Set<String>>> serviceUnits = getServiceUtils(dependencies, allMircoservices);
        List<String> consumers = new ArrayList<>();
        List<String> aggregators = new ArrayList<>();
        List<String> providers = new ArrayList<>();

        for (Map.Entry<String, Pair<Set<String>, Set<String>>> entry : serviceUnits.entrySet()){
            if (entry.getValue().getKey().size() > 0 && entry.getValue().getValue().size() == 0){
                providers.add(entry.getKey());
            }else if (entry.getValue().getKey().size() == 0 && entry.getValue().getValue().size() > 0){
                consumers.add(entry.getKey());
            }else {
                aggregators.add(entry.getKey());
            }
        }

        return new CAP(consumers, aggregators, providers);
    }

    // 获取系统中服务间依赖，也就是req部分，不包含rsp部分
    public static List<Direction> getDependencies(List<CSPair> dependencies, List<Microservice> allMircoservices){
        if (dependencies == null || allMircoservices == null){
            return null;
        }

        List<Direction> reqs = new ArrayList<>();
        for (CSPair csPair : dependencies){
            reqs.add(csPair.getReq());
        }
        return reqs;
    }
}
