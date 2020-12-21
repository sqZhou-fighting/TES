package top.jach.tes.app.jhkt.zhoushiqi;

import org.apache.commons.io.FileUtils;
import top.jach.tes.app.dev.DevApp;
import top.jach.tes.app.mock.Environment;
import top.jach.tes.app.mock.InfoTool;
import top.jach.tes.core.api.domain.context.Context;
import top.jach.tes.core.api.domain.info.Info;
import top.jach.tes.core.api.dto.PageQueryDto;
import top.jach.tes.core.impl.domain.relation.PairRelationsInfo;
import top.jach.tes.plugin.jhkt.DataAction;
import top.jach.tes.plugin.jhkt.InfoNameConstant;
import top.jach.tes.plugin.jhkt.dts.DtssInfo;
import top.jach.tes.plugin.jhkt.git.commit.GitCommitsForMicroserviceInfo;
import top.jach.tes.plugin.jhkt.maintain.MainTain;
import top.jach.tes.plugin.jhkt.maintain.MainTainsInfo;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.tes.code.git.version.Version;
import top.jach.tes.plugin.tes.code.git.version.VersionsInfo;
import top.jach.tes.plugin.tes.code.repo.ReposInfo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: zhoushiqi
 * @date: 2020/11/27
 * @description: 获取可维护性数据
 */
public class MaintainMain extends DevApp {
    public static void main(String[] args) {

        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        ReposInfo reposInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.TargetSystem, ReposInfo.class);
        VersionsInfo versionsInfoForRelease = DataAction.queryLastInfo(context, InfoNameConstant.VersionsForRelease, VersionsInfo.class);

        for (Version version : versionsInfoForRelease.getVersions()){
            MicroservicesInfo microservices = DataAction.queryLastMicroservices(context, reposInfo.getId(), null, version);

            DtssInfo dtssInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.BugDts, DtssInfo.class);
            PairRelationsInfo bugMicroserviceRelations = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.RelationBugAndMicroservice, PairRelationsInfo.class);
            if(bugMicroserviceRelations == null){
                bugMicroserviceRelations = PairRelationsInfo.createInfo();
            }
            Map<String, GitCommitsForMicroserviceInfo> gitCommitsForMicroserviceInfoMap = new HashMap<>();

            for (Microservice microservice :
                    microservices.getMicroservices()) {
                GitCommitsForMicroserviceInfo gitCommitsForMicroserviceInfo = new GitCommitsForMicroserviceInfo();
                gitCommitsForMicroserviceInfo
                        .setReposId(microservices.getReposId())
                        .setMicroserviceName(microservice.getElementName())
                        .setStatisticDiffFiles(null)
                        .setGitCommits(null);
                List<Info> infos = Environment.infoRepositoryFactory.getRepository(GitCommitsForMicroserviceInfo.class)
                        .queryDetailsByInfoAndProjectId(gitCommitsForMicroserviceInfo, Environment.defaultProject.getId(), PageQueryDto.create(1, 1).setSortField("createdTime"));
                if (infos.size() > 0) {
                    gitCommitsForMicroserviceInfoMap.put(microservice.getElementName(), (GitCommitsForMicroserviceInfo) infos.get(0));
                }
            }

            mainTainResult_version(microservices, dtssInfo, bugMicroserviceRelations, gitCommitsForMicroserviceInfoMap, version.getVersionName());

        }

        // here
        /*MicroservicesInfo microservices = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.MicroservicesForRepos, MicroservicesInfo.class);
        DtssInfo dtssInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.BugDts, DtssInfo.class);
        PairRelationsInfo bugMicroserviceRelations = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.RelationBugAndMicroservice, PairRelationsInfo.class);
        if(bugMicroserviceRelations == null){
            bugMicroserviceRelations = PairRelationsInfo.createInfo();
        }
        Map<String, GitCommitsForMicroserviceInfo> gitCommitsForMicroserviceInfoMap = new HashMap<>();

        for (Microservice microservice :
                microservices.getMicroservices()) {
            GitCommitsForMicroserviceInfo gitCommitsForMicroserviceInfo = new GitCommitsForMicroserviceInfo();
            gitCommitsForMicroserviceInfo
                    .setReposId(microservices.getReposId())
                    .setMicroserviceName(microservice.getElementName())
                    .setStatisticDiffFiles(null)
                    .setGitCommits(null);
            List<Info> infos = Environment.infoRepositoryFactory.getRepository(GitCommitsForMicroserviceInfo.class)
                    .queryDetailsByInfoAndProjectId(gitCommitsForMicroserviceInfo, Environment.defaultProject.getId(), PageQueryDto.create(1, 1).setSortField("createdTime"));
            if (infos.size() > 0) {
                gitCommitsForMicroserviceInfoMap.put(microservice.getElementName(), (GitCommitsForMicroserviceInfo) infos.get(0));
            }
        }

        *//*20190523
        20190815
        20190918
        20191007
        20191108
        20191204
        20200107*//*
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        *//*int[] year = {2019, 2019, 2019, 2019, 2019, 2019, 2020, 2020};
        int[] month = {4, 7, 8, 9, 10, 11, 12, 5};
        int[] day = {23, 15, 18, 7, 8, 4, 7, 30};*//*
        String[] versions = {
                "x_3c9_x_95d.x_893.x_893.x_e09d_x_43_x_8b_x_e09f_x_e0a1",
                "x_1635-x_95d.x_893.x_893_x_e0a3_x_1ff_x_e0a5_x_e0a7",
                "x_1635-x_95d.x_893.x_935_x_1ff_x_e0a9_x_29229",
                "x_1635-x_95d.x_893.x_935_x_1ff_x_e0a9_x_2922b",
                "x_1635-x_95d.x_893.x_935_x_1ff_x_e0a9_x_e0ab",
                "x_1635-x_95d.x_4af.x_893.x_ec8b_x_1ff_x_2922d",
                "x_1635-x_95d.x_4af.x_893_x_1ff_x_e0af_x_e0a3_x_e0b1"
        };
        for (int i=0; i<7;i++){

            mainTainResult_version(microservices, dtssInfo, bugMicroserviceRelations, gitCommitsForMicroserviceInfoMap, versions[i]);
        }*/
        /*for (int i=0; i<7; i++){
            Calendar start = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
            start.set(year[i], month[i], day[i]);
            Calendar end = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
            end.set(year[i+1], month[i+1], day[i+1]);
            mainTainResult(microservices, dtssInfo, bugMicroserviceRelations, gitCommitsForMicroserviceInfoMap, start.getTimeInMillis(), end.getTimeInMillis());
        }*/
        /*int[] ds = {1, 2, 3, 6};
        for (int di = 0; di < ds.length; di++) {
            int d = ds[di];
            for (int i = 0; i + d < 7; i++) {
                Calendar start = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
                start.set(2019, 5 + i, 1);
                Calendar end = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
                end.set();
                mainTainResult(microservices, dtssInfo, bugMicroserviceRelations, gitCommitsForMicroserviceInfoMap, start.getTimeInMillis(), end.getTimeInMillis());
            }
        }*/
    }

    private static void mainTainResult(MicroservicesInfo microservices, DtssInfo dtssInfo, PairRelationsInfo bugMicroserviceRelations, Map<String, GitCommitsForMicroserviceInfo> gitCommitsForMicroserviceInfoMap, long start, long end) {
        MainTainsInfo info = MainTainsInfo.createInfo(DataAction.DefaultReposId,
                microservices,
                gitCommitsForMicroserviceInfoMap,
                dtssInfo,
                bugMicroserviceRelations,
                start,
                end
        );
        StringBuilder sb = new StringBuilder();
        sb.append("Name,")
                .append("bugCount,")
                .append("commitCount,")
                .append("commitAddLineCount,")
                .append("commitDeleteLineCount,")
                .append("Commit Overlap Ratio (COR),")
                .append("Commit Fileset Overlap Ratio (CFOR),")
                .append("Pairwise Committer Overlap (PCO),")
                .append('\n')
        ;
        for (MainTain mainTain :
                info.getMainTainList()) {
            sb.append(mainTain.getElementName())
                    .append(',')
                    .append(nullToEmpty(mainTain.getBugCount()))
                    .append(',')
                    .append(nullToEmpty(mainTain.getCommitCount()))
                    .append(',')
                    .append(nullToEmpty(mainTain.getCommitAddLineCount()))
                    .append(',')
                    .append(nullToEmpty(mainTain.getCommitDeleteLineCount()))
                    .append(',')
                    .append(nullToEmpty(mainTain.getCommitOverlapRatio()))
                    .append(',')
                    .append(nullToEmpty(mainTain.getCommitFilesetOverlapRatio()))
                    .append(',')
                    .append(nullToEmpty(mainTain.getPairwiseCommitterOverlap()))
                    .append(',')
                    .append('\n')
            ;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        try {
            System.out.println(sb);
            FileUtils.write(new File(String.format("D:\\NJU\\GP\\Data\\bug_commit_add_sub_COR_CFOR_PCO\\%s_%s.csv", format.format(new Date(start)), format.format(new Date(end)))), sb, "utf8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void mainTainResult_version(MicroservicesInfo microservices, DtssInfo dtssInfo, PairRelationsInfo bugMicroserviceRelations, Map<String, GitCommitsForMicroserviceInfo> gitCommitsForMicroserviceInfoMap, String version) {
        MainTainsInfo info = MainTainsInfo.newCreateInfo(DataAction.DefaultReposId,
                microservices,
                gitCommitsForMicroserviceInfoMap,
                dtssInfo,
                bugMicroserviceRelations,
                version
        );
        StringBuilder sb = new StringBuilder();
        sb.append("Name,")
                .append("bugCount,")
                .append("commitCount,")
                .append("commitAddLineCount,")
                .append("commitDeleteLineCount,")
                .append("Commit Overlap Ratio (COR),")
                .append("Commit Fileset Overlap Ratio (CFOR),")
                .append("Pairwise Committer Overlap (PCO),")
                .append('\n')
        ;
        for (MainTain mainTain :
                info.getMainTainList()) {
            sb.append(mainTain.getElementName())
                    .append(',')
                    .append(nullToEmpty(mainTain.getBugCount()))
                    .append(',')
                    .append(nullToEmpty(mainTain.getCommitCount()))
                    .append(',')
                    .append(nullToEmpty(mainTain.getCommitAddLineCount()))
                    .append(',')
                    .append(nullToEmpty(mainTain.getCommitDeleteLineCount()))
                    .append(',')
                    .append(nullToEmpty(mainTain.getCommitOverlapRatio()))
                    .append(',')
                    .append(nullToEmpty(mainTain.getCommitFilesetOverlapRatio()))
                    .append(',')
                    .append(nullToEmpty(mainTain.getPairwiseCommitterOverlap()))
                    .append(',')
                    .append('\n')
            ;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        try {
            System.out.println(sb);
            FileUtils.write(new File(String.format("D:\\NJU\\GP\\Data\\bug_commit_add_sub_COR_CFOR_PCO\\%s.csv", version)), sb, "utf8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String nullToEmpty(Object obj) {
        return (obj == null) ? "" : obj.toString();
    }
}
