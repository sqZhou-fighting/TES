package top.jach.tes.plugin.jhkt.metricmsres;

import lombok.Data;
import lombok.extern.java.Log;
import top.jach.tes.core.api.domain.info.Info;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhoushiqi
 * @date: 2021/3/29
 * @description:
 */
@Log
@Data
public class MetricMSResInfo extends Info {
    public static final String INFO_Name = "MetricMSRes";
    String version;
    List<MetricMSRes> metricMSRess = new ArrayList<>();

    public static MetricMSResInfo createInfo() {
        MetricMSResInfo metricMSResInfo = new MetricMSResInfo();
        metricMSResInfo.initBuild();
        return metricMSResInfo;
    }

    public MetricMSResInfo addMetricMSRes(MetricMSRes metricMSRes){
        this.metricMSRess.add(metricMSRes);
        return this;
    }

    public static MetricMSResInfo createInfoByMetricMSRes(String version, List<MetricMSRes>... msMetricResss) {
        MetricMSResInfo metricMSResInfo = createInfo();
        metricMSResInfo.version = version;
        for (List<MetricMSRes> metricMSRess : msMetricResss) {
            for (MetricMSRes metricMSRes : metricMSRess) {
                metricMSResInfo.addMetricMSRes(metricMSRes);
            }
        }
        return metricMSResInfo;
    }

    public String getVersion() {
        return version;
    }

    public MetricMSResInfo setVersion(String version) {
        this.version = version;
        return this;
    }
}
