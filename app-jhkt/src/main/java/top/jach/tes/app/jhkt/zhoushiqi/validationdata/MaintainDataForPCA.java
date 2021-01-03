package top.jach.tes.app.jhkt.zhoushiqi.validationdata;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import top.jach.tes.app.dev.DevApp;
import top.jach.tes.app.jhkt.zhoushiqi.maintainres.MaintainRes;
import top.jach.tes.app.jhkt.zhoushiqi.maintainres.TmpData;
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
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.tes.code.git.version.Version;
import top.jach.tes.plugin.tes.code.git.version.VersionsInfo;
import top.jach.tes.plugin.tes.code.repo.ReposInfo;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/12/11
 * @description: 输入各版本的可维护性数据准备做PCA
 */
public class MaintainDataForPCA extends DevApp {
    public static void main(String[] args) {
        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        ReposInfo reposInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.TargetSystem, ReposInfo.class);
        VersionsInfo versionsInfoForRelease = DataAction.queryLastInfo(context, InfoNameConstant.VersionsForRelease, VersionsInfo.class);

        for (Version version : versionsInfoForRelease.getVersions()) {
            MicroservicesInfo microservicesInfo = DataAction.queryLastMicroservices(context, reposInfo.getId(), null, version);

            // 计算可维护性直接度量所需要的数据
            DtssInfo dtssInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.BugDts, DtssInfo.class);
            PairRelationsInfo bugMicroserviceRelations = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.RelationBugAndMicroservice, PairRelationsInfo.class);
            if (bugMicroserviceRelations == null) {
                bugMicroserviceRelations = PairRelationsInfo.createInfo();
            }
            Map<String, GitCommitsForMicroserviceInfo> gitCommitsForMicroserviceInfoMap = new HashMap<>();

            for (Microservice microservice :
                    microservicesInfo.getMicroservices()) {
                /*GitCommitsForMicroserviceInfo gitCommitsForMicroserviceInfo = new GitCommitsForMicroserviceInfo();
                gitCommitsForMicroserviceInfo
                        .setReposId(microservicesInfo.getReposId())
                        .setMicroserviceName(microservice.getElementName())
                        .setStatisticDiffFiles(null)
                        .setGitCommits(null);
                List<Info> infos = Environment.infoRepositoryFactory.getRepository(GitCommitsForMicroserviceInfo.class)
                        .queryDetailsByInfoAndProjectId(gitCommitsForMicroserviceInfo, Environment.defaultProject.getId(), PageQueryDto.create(1, 1).setSortField("createdTime"));
                if (infos.size() > 0) {
                    gitCommitsForMicroserviceInfoMap.put(microservice.getElementName(), (GitCommitsForMicroserviceInfo) infos.get(0));
                }*/

                GitCommitsForMicroserviceInfo gitCommitsForMicroserviceInfo = DataAction.queryLastGitCommitsForMicroserviceInfo(context, reposInfo.getId(), microservice.getElementName(), version);
                gitCommitsForMicroserviceInfoMap.put(microservice.getElementName(), gitCommitsForMicroserviceInfo);
            }

            Map<String, TmpData> mainTainRes = MaintainRes.mainTainResult_version(microservicesInfo, dtssInfo, bugMicroserviceRelations, gitCommitsForMicroserviceInfoMap, version.getVersionName());

            // 分别计算每个版本的指标结果，每个版本都有一个单独的Excel表
            HSSFWorkbook wb = new HSSFWorkbook();

            HSSFSheet sheet1 = wb.createSheet("可维护性直接度量");
            HSSFRow row00 = sheet1.createRow(0);

            row00.createCell(0).setCellValue("microservice");
            row00.createCell(1).setCellValue("Commit Overlap Ratio (COR)");
            row00.createCell(2).setCellValue("Commit Fileset Overlap Ratio (CFOR)");
            row00.createCell(3).setCellValue("Pairwise Committer Overlap (PCO)");

            int k = 1;
            for (Microservice microservice : microservicesInfo.getMicroservices()) {
                HSSFRow row_service = sheet1.createRow(k);
                String msName = microservice.getElementName();
                row_service.createCell(0).setCellValue(msName);

                if (mainTainRes.get(msName).getCommitOverlapRatio() != null) {
                    row_service.createCell(1).setCellValue(mainTainRes.get(msName).getCommitOverlapRatio());
                }
                if (mainTainRes.get(msName).getCommitFilesetOverlapRatio() != null) {
                    row_service.createCell(2).setCellValue(mainTainRes.get(msName).getCommitFilesetOverlapRatio());
                }
                if (mainTainRes.get(msName).getPairwiseCommitterOverlap() != null) {
                    row_service.createCell(3).setCellValue(mainTainRes.get(msName).getPairwiseCommitterOverlap());
                }
                k++;
            }

            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream("D:\\NJU\\GP\\Data\\maintain_data\\" + version.getVersionName() + ".xls");
                wb.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}


