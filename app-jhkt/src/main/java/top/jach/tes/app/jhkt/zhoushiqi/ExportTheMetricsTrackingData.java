package top.jach.tes.app.jhkt.zhoushiqi;

import top.jach.tes.app.mock.Environment;
import top.jach.tes.app.mock.InfoTool;
import top.jach.tes.core.api.domain.context.Context;
import top.jach.tes.plugin.jhkt.DataAction;
import top.jach.tes.plugin.jhkt.InfoNameConstant;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.tes.code.git.version.Version;
import top.jach.tes.plugin.tes.code.git.version.VersionsInfo;
import top.jach.tes.plugin.tes.code.repo.ReposInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhoushiqi
 * @date: 2020/12/8
 * @description: 导出指标计算后的结果，但是是按指标分类的，一个指标一个sheet，每个sheet中存该指标的七个版本的数据。
 * @mind：我赶脚有一点点麻烦
 */
public class ExportTheMetricsTrackingData {
    public static void main(String[] args) {
        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        ReposInfo reposInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.TargetSystem, ReposInfo.class);
        VersionsInfo versionsInfoForRelease = DataAction.queryLastInfo(context, InfoNameConstant.VersionsForRelease, VersionsInfo.class);

        //把七个版本的微服务数据都拿到
        List<MicroservicesInfo> allMicroserviceInfos = new ArrayList<>();
        for (Version version : versionsInfoForRelease.getVersions()) {

        }

    }
}
