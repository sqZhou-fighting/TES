package top.jach.tes.app.jhkt.zhoushiqi;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import top.jach.tes.app.dev.DevApp;
import top.jach.tes.app.mock.Environment;
import top.jach.tes.app.mock.InfoTool;
import top.jach.tes.core.api.domain.context.Context;
import top.jach.tes.plugin.jhkt.DataAction;
import top.jach.tes.plugin.jhkt.InfoNameConstant;
import top.jach.tes.plugin.jhkt.dts.Dts;
import top.jach.tes.plugin.jhkt.dts.DtssInfo;
import top.jach.tes.plugin.jhkt.git.commit.GitCommitsForMicroserviceInfo;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.tes.code.git.commit.GitCommit;
import top.jach.tes.plugin.tes.code.git.version.Version;
import top.jach.tes.plugin.tes.code.git.version.VersionsInfo;
import top.jach.tes.plugin.tes.code.repo.ReposInfo;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: zhoushiqi
 * @date: 2021/1/14
 * @description: 统计不同版本中不同微服务及其bug数量
 */
public class MicroserviceAndBugNum extends DevApp {
    public static void main(String[] args) {
        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        ReposInfo reposInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.TargetSystem, ReposInfo.class);
        VersionsInfo versionsInfoForRelease = DataAction.queryLastInfo(context, InfoNameConstant.VersionsForRelease, VersionsInfo.class);
        DtssInfo dtssInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.BugDts, DtssInfo.class);

        HSSFWorkbook wb = new HSSFWorkbook();

        for (Version version : versionsInfoForRelease.getVersions()) {
            // 计算指标所需要的数据
            MicroservicesInfo microservicesInfo = DataAction.queryLastMicroservices(context, reposInfo.getId(), null, version);
            Map<String, GitCommitsForMicroserviceInfo> gitCommitsForMicroserviceInfoMap = new HashMap<>();

            // Map<commit_sha, microservice>
            Map<String, String> shaAndMicroservice = new HashMap<>();
            Map<String, Long> microserviceAndBugNum = new HashMap<>();
            Map<String, Long> microserviceAndCommiteNum = new HashMap<>();
            Map<String, Long> microserviceAndCodeChangeLineNum = new HashMap<>();

            for (Microservice microservice :
                    microservicesInfo.getMicroservices()) {
                // 初始化
                microserviceAndBugNum.put(microservice.getElementName(), 0L);
                microserviceAndCommiteNum.put(microservice.getElementName(), 0L);
                microserviceAndCodeChangeLineNum.put(microservice.getElementName(), 0L);

                // 获取gitCommit数据
                GitCommitsForMicroserviceInfo gitCommitsForMicroserviceInfo = DataAction.queryLastGitCommitsForMicroserviceInfo(context, reposInfo.getId(), microservice.getElementName(), version);
                gitCommitsForMicroserviceInfoMap.put(microservice.getElementName(), gitCommitsForMicroserviceInfo);

                if (gitCommitsForMicroserviceInfo != null) {
                    microserviceAndCommiteNum.put(microservice.getElementName(), (long) gitCommitsForMicroserviceInfo.getGitCommits().size());
                    Long tmp = 0L;
                    for (GitCommit gitCommit : gitCommitsForMicroserviceInfo.getGitCommits()) {
                        shaAndMicroservice.put(gitCommit.getSha(), microservice.getElementName());
                        tmp += (gitCommit.getStatisticDiffFiles().getAddSize() + gitCommit.getStatisticDiffFiles().getSubSize());
                    }
                    microserviceAndCodeChangeLineNum.put(microservice.getElementName(), tmp);
                }
            }

            int shasNum = 0;
            for (Dts dts : dtssInfo.getBugs()){
                for (Map.Entry<String, Set<String>> entry : dts.getRepoShasMap().entrySet()){
                    shasNum += entry.getValue().size();
                }
            }

            for (Dts dts : dtssInfo.getBugs()) {
                Set<String> allShas = new HashSet<>();
                for (Map.Entry<String, Set<String>> entry : dts.getRepoShasMap().entrySet()) {
                    allShas.addAll(entry.getValue());
                }

                // 整理一个bug所涉及的微服务列表
                Set<String> bug_ms = new HashSet<>();
                for (String sha : allShas) {
                    if (shaAndMicroservice.get(sha) != null) {
                        bug_ms.add(shaAndMicroservice.get(sha));
                    }
                }

                // 为bug涉及的微服务们的bug数量计数+1
                for (String ms : bug_ms){
                    microserviceAndBugNum.merge(ms, 1L, (oldValue, newValue) -> oldValue + newValue);
                }
            }

            HSSFSheet sheet = wb.createSheet(version.getVersionName().substring(version.getVersionName().length() - 15));

            HSSFRow head = sheet.createRow(0);
            head.createCell(0).setCellValue("Microservice");
            head.createCell(1).setCellValue("BugNum");
            head.createCell(2).setCellValue("CommitNum");
            head.createCell(3).setCellValue("CodeChangeLineNum");

            int i = 1;
            for (Microservice microservice : microservicesInfo.getMicroservices()) {
                String msName = microservice.getElementName();
                HSSFRow row = sheet.createRow(i);
                row.createCell(0).setCellValue(msName);
                row.createCell(1).setCellValue(microserviceAndBugNum.get(msName));
                row.createCell(2).setCellValue(microserviceAndCommiteNum.get(msName));
                row.createCell(3).setCellValue(microserviceAndCodeChangeLineNum.get(msName));
                i++;
            }
        }

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream("D:\\NJU\\GP\\Data\\MicroserviceAndBugNum.xls");
            wb.write(fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

