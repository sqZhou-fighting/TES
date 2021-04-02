package top.jach.tes.app.jhkt.zhoushiqi.forwangdong;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import top.jach.tes.app.dev.DevApp;
import top.jach.tes.app.jhkt.zhoushiqi.maintainres.MaintainRes;
import top.jach.tes.app.jhkt.zhoushiqi.maintainres.TmpData;
import top.jach.tes.app.mock.Environment;
import top.jach.tes.app.mock.InfoTool;
import top.jach.tes.core.api.domain.context.Context;
import top.jach.tes.core.impl.domain.relation.PairRelationsInfo;
import top.jach.tes.plugin.jhkt.DataAction;
import top.jach.tes.plugin.jhkt.InfoNameConstant;
import top.jach.tes.plugin.jhkt.dts.DtssInfo;
import top.jach.tes.plugin.jhkt.git.commit.GitCommitsForMicroserviceInfo;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.tes.code.git.commit.DiffFile;
import top.jach.tes.plugin.tes.code.git.commit.GitCommit;
import top.jach.tes.plugin.tes.code.git.commit.StatisticDiffFiles;
import top.jach.tes.plugin.tes.code.git.version.Version;
import top.jach.tes.plugin.tes.code.git.version.VersionsInfo;
import top.jach.tes.plugin.tes.code.repo.ReposInfo;

import java.io.FileOutputStream;
import java.util.*;

/**
 * @Author: zhoushiqi
 * @date: 2021/4/2
 * @description:
 */
public class MaintainChangeReason extends DevApp {
    public static void main(String[] args) {
        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        ReposInfo reposInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.TargetSystem, ReposInfo.class);
        VersionsInfo versionsInfoForRelease = DataAction.queryLastInfo(context, InfoNameConstant.VersionsForRelease, VersionsInfo.class);

        for (Version version : versionsInfoForRelease.getVersions()) {
            MicroservicesInfo microservicesInfo = DataAction.queryLastMicroservices(context, reposInfo.getId(), null, version);
            List<MSChangeElem> msChangeElems = new ArrayList<>();
           for (Microservice microservice : microservicesInfo.getMicroservices()) {
               MSChangeElem msChangeElem = new MSChangeElem();
               msChangeElem.setMs_name(microservice.getElementName());
               Map<String, List<String>> shaDifFiles = new HashMap<>();

               // 微服务的gitcommit信息
                GitCommitsForMicroserviceInfo gitCommitsForMicroserviceInfo = DataAction.queryLastGitCommitsForMicroserviceInfo(context, reposInfo.getId(), microservice.getElementName(), version);
                // 获取每个微服务在这个版本中的变化的sha
                if (gitCommitsForMicroserviceInfo != null){
                    List<GitCommit> gitCommits = gitCommitsForMicroserviceInfo.getGitCommits();
                    for (GitCommit gitCommit : gitCommits) {
                        List<String> diffFiles = new ArrayList<>();
                        for (DiffFile diffFile : gitCommit.getDiffFiles()){
                            diffFiles.addAll(diffFile.getFilePath());
                        }
                        shaDifFiles.put(gitCommit.getSha(), diffFiles);
                    }
                }
                msChangeElem.setShaDifffFiles(shaDifFiles);
                msChangeElems.add(msChangeElem);
            }

            //一个版本一个Excel，一个Excel中的一个Sheet写一个微服务的所有sha
            HSSFWorkbook wb = new HSSFWorkbook();

           for (MSChangeElem msChangeElem : msChangeElems){
               HSSFSheet sheet = wb.createSheet(msChangeElem.getMs_name().replace('/','#'));
               HSSFRow row00 = sheet.createRow(0);
               row00.createCell(0).setCellValue("sha");
               row00.createCell(1).setCellValue("FilePath");

               int k = 1;
               for (Map.Entry<String, List<String>> entry : msChangeElem.getShaDifffFiles().entrySet()){
                   HSSFRow row = sheet.createRow(k);
                   int tmp = entry.getValue().size();
                   row.createCell(0).setCellValue(entry.getKey());
                   k++;
                   for (int i=0; i<tmp; i++){
                       HSSFRow row1 = sheet.createRow(k);
                       row1.createCell(0);
                       row1.createCell(1).setCellValue(entry.getValue().get(i));
                       k++;
                   }
               }
           }

            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream("D:\\NJU\\GP\\Data\\sha_difffiles\\" + version.getVersionName() + ".xls");
                wb.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }



        }
    }
}
