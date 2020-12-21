package top.jach.tes.app.jhkt.zhoushiqi.validationdata;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import top.jach.tes.app.dev.DevApp;
import top.jach.tes.app.mock.Environment;
import top.jach.tes.app.mock.InfoTool;
import top.jach.tes.core.api.domain.context.Context;
import top.jach.tes.plugin.jhkt.DataAction;
import top.jach.tes.plugin.jhkt.InfoNameConstant;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.size.SizeMetrics;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.DependencyUtil;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.InAndOutDegree;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.tes.code.dependency.CSPair;
import top.jach.tes.plugin.tes.code.dependency.DependenciesInfo;
import top.jach.tes.plugin.tes.code.git.version.Version;
import top.jach.tes.plugin.tes.code.git.version.VersionsInfo;
import top.jach.tes.plugin.tes.code.repo.ReposInfo;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/12/14
 * @description: 输出size类的所有指标计算结果
 */
public class SizeData extends DevApp {
    public static void main(String[] args) {
        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        ReposInfo reposInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.TargetSystem, ReposInfo.class);
        VersionsInfo versionsInfoForRelease = DataAction.queryLastInfo(context, InfoNameConstant.VersionsForRelease, VersionsInfo.class);

        for (Version version : versionsInfoForRelease.getVersions()) {
            // 计算指标所需要的数据
            MicroservicesInfo microservicesInfo = DataAction.queryLastMicroservices(context, reposInfo.getId(), null, version);
            DependenciesInfo dependenciesInfo = DataAction.queryLastDependencies(context, version);
            List<CSPair> dependencies = dependenciesInfo.getDependencies();
            InAndOutDegree inAndOutDegree = DependencyUtil.getInAndOutDegree(dependencies, microservicesInfo.getMicroservices());


            // 分别计算每个版本的指标结果，每个版本都有一个单独的Excel表
            HSSFWorkbook wb = new HSSFWorkbook();

            HSSFSheet sheet1 = wb.createSheet("size metrics data");
            HSSFRow row00 = sheet1.createRow(0);
            row00.createCell(0).setCellValue("microservice");
            row00.createCell(1).setCellValue("Size - Number of Consumer Services Directly");
            row00.createCell(2).setCellValue("Size - Number of Producer Services Directly");
            row00.createCell(3).setCellValue("Size - Avg Number Of Connected Services Directly");
            row00.createCell(4).setCellValue("Size - Number of Operation");
            row00.createCell(5).setCellValue("Size - Number of Pkg");
            row00.createCell(6).setCellValue("Size - Number of Class");

            // Size类服务级指标
            Map<String, Integer> serviceAndConsumers = SizeMetrics.numberOfConsumerServicesDirectly(inAndOutDegree.getOutDegree());
            Map<String, Integer> serviceAndProducers = SizeMetrics.numberOfProducerServicesDirectly(inAndOutDegree.getInDegree());
            Map<String, Integer> serviceAndDirectly = SizeMetrics.avgNumberOfConnectedServicesDirectly(inAndOutDegree.getInDegree(), inAndOutDegree.getOutDegree(), microservicesInfo.getMicroservices().size());
            Map<String, Integer> numberOfPkg = SizeMetrics.numberOfPackages(microservicesInfo);
            Map<String, Integer> numberOfClass = SizeMetrics.numberOfClasses(microservicesInfo);
            Map<String, Integer> numberOfInterface = SizeMetrics.numberOfInterfaces(microservicesInfo);

            int k = 1;
            for (Microservice microservice : microservicesInfo.getMicroservices()) {
                HSSFRow row_service = sheet1.createRow(k);
                String msName = microservice.getElementName();
                row_service.createCell(0).setCellValue(msName);
                row_service.createCell(1).setCellValue(serviceAndConsumers.get(msName));
                row_service.createCell(2).setCellValue(serviceAndProducers.get(msName));
                row_service.createCell(3).setCellValue(serviceAndDirectly.get(msName));
                row_service.createCell(4).setCellValue(numberOfInterface.get(msName));
                row_service.createCell(5).setCellValue(numberOfPkg.get(msName));
                row_service.createCell(6).setCellValue(numberOfClass.get(msName));
                k++;
            }


            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream("D:\\NJU\\GP\\Data\\validation\\Association\\size_metrics_data\\" + version.getVersionName() + ".xls");
                wb.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
