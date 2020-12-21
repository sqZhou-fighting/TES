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
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.tes.code.git.version.Version;
import top.jach.tes.plugin.tes.code.git.version.VersionsInfo;
import top.jach.tes.plugin.tes.code.repo.ReposInfo;

import java.io.FileOutputStream;

/**
 * @Author: zhoushiqi
 * @date: 2020/12/14
 * @description: 统计一下不同版本中微服务的代码行数，也没什么用，就是想对系统的规模有点印象
 * @PS: 如果最后输出文件的后缀名为.xlsx那么excel文件就打不开，但如果输出文件的后缀名为.xls就可以打开
 */
public class StatisticCodeLine extends DevApp {
    public static void main(String[] args) {
        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        ReposInfo reposInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.TargetSystem, ReposInfo.class);
        VersionsInfo versionsInfoForRelease = DataAction.queryLastInfo(context, InfoNameConstant.VersionsForRelease, VersionsInfo.class);

        HSSFWorkbook wb = new HSSFWorkbook();

        for (Version version : versionsInfoForRelease.getVersions()) {
            MicroservicesInfo microservicesInfo = DataAction.queryLastMicroservices(context, reposInfo.getId(), null, version);

            HSSFSheet sheet1 = wb.createSheet(version.getVersionName().substring(version.getVersionName().length()-6, version.getVersionName().length()));
            HSSFRow row00 = sheet1.createRow(0);
            row00.createCell(0).setCellValue("microservice");
            row00.createCell(1).setCellValue("code lines");

            int k = 1;
            for (Microservice microservice : microservicesInfo.getMicroservices()) {
                HSSFRow row_service = sheet1.createRow(k);
                String msName = microservice.getElementName();
                row_service.createCell(0).setCellValue(msName);
                row_service.createCell(1).setCellValue(microservice.getCodeLines());
                k++;
            }
        }

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream("D:\\NJU\\GP\\Data\\code_lines.xls");
            wb.write(fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
