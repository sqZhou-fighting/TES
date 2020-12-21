package top.jach.tes.plugin.jhkt.maintainabilitymetrics.complexity;

import javafx.util.Pair;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.CAP;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.InAndOutDegree;
import top.jach.tes.plugin.tes.code.dependency.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: zhoushiqi
 * @date: 2020/8/28
 * @description: complexity类可维护性指标实现，方法注释格式为“指标ID 指标名称”
 */
public class ComplexityMetrics {

    // 92 Extent of Aggregation 系统聚合范围
    public static double extentOfAggregation(List<Direction> reqs, CAP cap) {
        if (reqs == null) {
            return 0;
        }

        double res = 0;
        List<String> customers = cap.getConsumers();
        for (String customer : customers) {
            // 分子
            int molecular = 0;
            List<String> aggregators = cap.getAggregators();
            for (String aggregator : aggregators) {
                for (Direction dir : reqs) {
                    if (dir.getSrc_cs().equals(customer) && dir.getDest_cs().equals(aggregator)) {
                        molecular++;
                    }
                }
            }
            // 分母
            List<String> providers = cap.getProviders();
            providers.addAll(aggregators);
            int denominator = 0;
            for (String provider : providers) {
                for (Direction dir : reqs) {
                    if (dir.getSrc_cs().equals(customer) && dir.getDest_cs().equals(provider)) {
                        denominator++;
                    }
                }
            }
            res += (double) (molecular / denominator);
        }

        return res;
    }

    // 93 System’s CentraliZation
    public static double sysCentralization(CAP cap, InAndOutDegree inAndOutDegree) {
        if (inAndOutDegree == null) {
            return -1;
        }

        List<String> customers = cap.getConsumers();
        customers.addAll(cap.getAggregators());
        int cos_c = 0;
        Map<String, Set<String>> outDegree = inAndOutDegree.getOutDegree();
        for (String customer : customers) {
            cos_c += outDegree.get(customer).size();
        }

        List<String> aggregators = cap.getAggregators();
        int cos_a = 0;
        for (String aggregator : aggregators) {
            cos_a += outDegree.get(aggregator).size();
        }

        double k = 0.9 * (customers.size() - aggregators.size()) - (aggregators.size() - 1) * (aggregators.size() - 1);

        return 1 - ((cos_c - cos_a - k) / (cos_c + (aggregators.size() - 1) * (aggregators.size() - 1)));
    }

    // 94 Density of Aggregation
    public static double densityOfAggregation(Map<String, Pair<Set<String>, Set<String>>> serviceUtils) {
        double res = 0;
        for (Map.Entry<String, Pair<Set<String>, Set<String>>> entry : serviceUtils.entrySet()) {
            double inDegree = entry.getValue().getKey().size();
            double outDegree = entry.getValue().getValue().size();
            if (inDegree > 0 && outDegree > 0) {
                res += Math.log((outDegree / (inDegree + outDegree)) * 2);
            }
        }
        return res;
    }

    // 95 的支撑指标 Aggregator Density
    public static Map<String, Integer> allAggregatorDensity(Map<String, Pair<Set<String>, Set<String>>> serviceUtils) {
        Map<String, Integer> AD = new HashMap<>();

        for (Map.Entry<String, Pair<Set<String>, Set<String>>> entry : serviceUtils.entrySet()) {
            double inDegree = entry.getValue().getKey().size();
            double outDegree = entry.getValue().getValue().size();
            if (inDegree > 0 && outDegree > 0) {
                double tmp = outDegree / (inDegree + outDegree);
                if (tmp >= 0.5 && tmp <= 0.6) {
                    AD.put(entry.getKey(), 0);
                } else {
                    AD.put(entry.getKey(), 1);
                }
            }
        }

        return AD;
    }

    // 95 Aggregator Centralization
    public static double aggregatorCentralization(Map<String, Pair<Set<String>, Set<String>>> serviceUtils) {
        Map<String, Integer> AD = allAggregatorDensity(serviceUtils);
        double res;

        if (AD.size() == 1) {
            res = 1;
        } else {
            int tmp = 0;
            for (Map.Entry<String, Integer> entry : AD.entrySet()) {
                tmp += entry.getValue();
            }
            res = 1 - (double) (tmp / AD.size());
        }

        return res;
    }

}
