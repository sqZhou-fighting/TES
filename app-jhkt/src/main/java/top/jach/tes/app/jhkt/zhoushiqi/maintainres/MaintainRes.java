package top.jach.tes.app.jhkt.zhoushiqi.maintainres;

import top.jach.tes.core.impl.domain.relation.PairRelationsInfo;
import top.jach.tes.plugin.jhkt.DataAction;
import top.jach.tes.plugin.jhkt.dts.DtssInfo;
import top.jach.tes.plugin.jhkt.git.commit.GitCommitsForMicroserviceInfo;
import top.jach.tes.plugin.jhkt.maintain.MainTain;
import top.jach.tes.plugin.jhkt.maintain.MainTainsInfo;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/12/11
 * @description: 获取可维护性结果的方法
 */
public class MaintainRes {
    public static Map<String, TmpData> mainTainResult_version(MicroservicesInfo microservices, DtssInfo dtssInfo, PairRelationsInfo bugMicroserviceRelations, Map<String, GitCommitsForMicroserviceInfo> gitCommitsForMicroserviceInfoMap, String version) {
        MainTainsInfo mainTainsInfo = MainTainsInfo.newCreateInfo(DataAction.DefaultReposId,
                microservices,
                gitCommitsForMicroserviceInfoMap,
                dtssInfo,
                bugMicroserviceRelations,
                version
        );
        Map<String, TmpData> res = new HashMap<>();

        for (MainTain mainTain : mainTainsInfo.getMainTainList()) {
            res.put(mainTain.getElementName(), new TmpData(mainTain.getCommitOverlapRatio(),
                    mainTain.getCommitFilesetOverlapRatio(),
                    mainTain.getPairwiseCommitterOverlap()));
        }

        return res;
    }
}

