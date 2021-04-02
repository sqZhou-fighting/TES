package top.jach.tes.app.jhkt.zhoushiqi.forwangdong;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import top.jach.tes.app.dev.DevApp;
import top.jach.tes.app.mock.Environment;
import top.jach.tes.app.mock.InfoTool;
import top.jach.tes.core.api.domain.context.Context;
import top.jach.tes.plugin.jhkt.DataAction;
import top.jach.tes.plugin.jhkt.InfoNameConstant;
import top.jach.tes.plugin.jhkt.git.commit.GitCommitsForMicroserviceInfo;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.tes.code.git.commit.DiffFile;
import top.jach.tes.plugin.tes.code.git.commit.GitCommit;
import top.jach.tes.plugin.tes.code.git.version.Version;
import top.jach.tes.plugin.tes.code.git.version.VersionsInfo;
import top.jach.tes.plugin.tes.code.repo.ReposInfo;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2021/4/2
 * @description:
 */
public class MaintainChangeReasonSha extends DevApp {
    public static void main(String[] args) {
        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        ReposInfo reposInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.TargetSystem, ReposInfo.class);
        VersionsInfo versionsInfoForRelease = DataAction.queryLastInfo(context, InfoNameConstant.VersionsForRelease, VersionsInfo.class);

        for (Version version : versionsInfoForRelease.getVersions()) {
            MicroservicesInfo microservicesInfo = DataAction.queryLastMicroservices(context, reposInfo.getId(), null, version);
            List<MSChangeSha> msChangeShas = new ArrayList<>();
            for (Microservice microservice : microservicesInfo.getMicroservices()) {
                MSChangeSha msChangeSha = new MSChangeSha();
                msChangeSha.setMs_name(microservice.getElementName());
                List<String> sha_list = new ArrayList<>();

                // 微服务的gitcommit信息
                GitCommitsForMicroserviceInfo gitCommitsForMicroserviceInfo = DataAction.queryLastGitCommitsForMicroserviceInfo(context, reposInfo.getId(), microservice.getElementName(), version);
                // 获取每个微服务在这个版本中的变化的sha
                if (gitCommitsForMicroserviceInfo != null) {
                    List<GitCommit> gitCommits = gitCommitsForMicroserviceInfo.getGitCommits();
                    for (GitCommit gitCommit : gitCommits) {
                        sha_list.add(gitCommit.getSha());
                    }
                }
                msChangeSha.setSha_list(sha_list);
                msChangeShas.add(msChangeSha);
            }

            //一个版本一个Excel，一个Excel中的一个Sheet写一个微服务的所有sha
            HSSFWorkbook wb = new HSSFWorkbook();

            for (MSChangeSha msChangeSha : msChangeShas) {
                HSSFSheet sheet = wb.createSheet(msChangeSha.getMs_name().replace('/', '#'));
                HSSFRow row00 = sheet.createRow(0);
                row00.createCell(0).setCellValue("sha");

                int k = 1;
                for (String sha : msChangeSha.getSha_list()) {
                    HSSFRow row = sheet.createRow(k);
                    row.createCell(0).setCellValue(sha);
                    k ++;
                }
            }

            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream("D:\\NJU\\GP\\Data\\sha\\" + version.getVersionName() + ".xls");
                wb.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
