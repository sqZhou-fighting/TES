package top.jach.tes.plugin.jhkt.msmetricsres;

import lombok.Data;
import lombok.extern.java.Log;
import top.jach.tes.core.api.domain.info.Info;
import top.jach.tes.plugin.tes.code.dependency.CSPair;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhoushiqi
 * @date: 2021/3/29
 * @description:
 */
@Log
@Data
public class MSMetricResInfo extends Info {
    public static final String INFO_Name = "MSMetricRes";
    String version;
    List<MSMetricRes> msMetricRess = new ArrayList<>();

    public static MSMetricResInfo createInfo() {
        MSMetricResInfo msMetricResInfo = new MSMetricResInfo();
        msMetricResInfo.initBuild();
        return msMetricResInfo;
    }

    public MSMetricResInfo addMSMetricRes(MSMetricRes msMetricRes){
        this.msMetricRess.add(msMetricRes);
        return this;
    }

    public static MSMetricResInfo createInfoByMsMetricRes(String version, List<MSMetricRes>... msMetricResss) {
        MSMetricResInfo msMetricResInfo = createInfo();
        msMetricResInfo.version = version;
        for (List<MSMetricRes> msMetricRess : msMetricResss) {
            for (MSMetricRes msMetricRes : msMetricRess) {
                msMetricResInfo.addMSMetricRes(msMetricRes);
            }
        }
        return msMetricResInfo;
    }

    public String getVersion() {
        return version;
    }

    public MSMetricResInfo setVersion(String version) {
        this.version = version;
        return this;
    }
}
