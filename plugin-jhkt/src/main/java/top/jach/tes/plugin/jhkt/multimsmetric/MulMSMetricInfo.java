package top.jach.tes.plugin.jhkt.multimsmetric;

import lombok.Data;
import lombok.extern.java.Log;
import top.jach.tes.core.api.domain.info.Info;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhoushiqi
 * @date: 2021/4/6
 * @description:
 */
@Log
@Data
public class MulMSMetricInfo extends Info {
    public static final String INFO_Name = "MulMSMetric";
    String version;
    List<MulMSMetric> mulMSMetricList = new ArrayList<>();

    public static MulMSMetricInfo createInfo() {
        MulMSMetricInfo mulMSMetricInfo = new MulMSMetricInfo();
        mulMSMetricInfo.initBuild();
        return mulMSMetricInfo;
    }

    public MulMSMetricInfo addMulMSMetric(MulMSMetric mulMSMetric){
        this.mulMSMetricList.add(mulMSMetric);
        return this;
    }

    public static MulMSMetricInfo createInfoByMulMSMetric(String version, List<MulMSMetric>... mulMSMetricss) {
        MulMSMetricInfo mulMSMetricInfo = createInfo();
        mulMSMetricInfo.version = version;
        for (List<MulMSMetric> mulMSMetrics : mulMSMetricss) {
            for (MulMSMetric mulMSMetric : mulMSMetrics) {
                mulMSMetricInfo.addMulMSMetric(mulMSMetric);
            }
        }
        return mulMSMetricInfo;
    }

    public String getVersion() {
        return version;
    }

    public MulMSMetricInfo setVersion(String version) {
        this.version = version;
        return this;
    }

}
