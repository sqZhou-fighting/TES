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
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.cohesion.CohesionMetrics;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling.CouplingMetrics;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling.MyCouplingMetricsDependency;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling.MyCouplingMetricsDependencyAndParams;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.size.SizeMetrics;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.DependencyUtil;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.InAndOutDegree;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.TopicUtil;
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
 * @description: 输出所有的可维护性指标数据
 */
public class MaintainMetricsAllData extends DevApp {
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
            Map<String, Integer> topics = TopicUtil.getAllTopic(microservicesInfo);


            // 分别计算每个版本的指标结果，每个版本都有一个单独的Excel表
            HSSFWorkbook wb = new HSSFWorkbook();

            HSSFSheet sheet1 = wb.createSheet("maintain metrics data");
            HSSFRow row00 = sheet1.createRow(0);
            row00.createCell(0).setCellValue("microservice");
            row00.createCell(1).setCellValue("Size - Number of Consumer Services Directly");
            row00.createCell(2).setCellValue("Size - Number of Producer Services Directly");
            row00.createCell(3).setCellValue("Size - Avg Number Of Connected Services Directly");
            row00.createCell(4).setCellValue("Size - Number of Operation");
            row00.createCell(5).setCellValue("Size - Number of Pkg");
            row00.createCell(6).setCellValue("Size - Number of Class");

            row00.createCell(7).setCellValue("Coupling - Relative Coupling of Service");
            row00.createCell(8).setCellValue("Coupling - Relative Importance of Service");
            row00.createCell(9).setCellValue("Coupling - Absolute Criticality of the Service");
            row00.createCell(10).setCellValue("Coupling - IncomingCouplingOfSByDependency");
            row00.createCell(11).setCellValue("Coupling - OutgoingCouplingOfSByDependency");
            row00.createCell(12).setCellValue("Coupling - TotalCouplingOfSByDependency");
            row00.createCell(13).setCellValue("Coupling - IncomingCouplingOfSByDependencyAndParams");
            row00.createCell(14).setCellValue("Coupling - OutgoingCouplingOfSByDependenyAndParams");
            row00.createCell(15).setCellValue("Coupling - TotalCouplingOfSByDependencyAndParams");

            row00.createCell(16).setCellValue("Cohesion - Service Interface Data Cohesion");
            row00.createCell(17).setCellValue("Cohesion - Service Interface Usage Cohesion");
            row00.createCell(18).setCellValue("Cohesion - Total Interface Cohesion of a Service");


            // Size类服务级指标
            Map<String, Integer> serviceAndConsumers = SizeMetrics.numberOfConsumerServicesDirectly(inAndOutDegree.getOutDegree());
            Map<String, Integer> serviceAndProducers = SizeMetrics.numberOfProducerServicesDirectly(inAndOutDegree.getInDegree());
            Map<String, Integer> serviceAndDirectly = SizeMetrics.avgNumberOfConnectedServicesDirectly(inAndOutDegree.getInDegree(), inAndOutDegree.getOutDegree(), microservicesInfo.getMicroservices().size());
            Map<String, Integer> numberOfPkg = SizeMetrics.numberOfPackages(microservicesInfo);
            Map<String, Integer> numberOfClass = SizeMetrics.numberOfClasses(microservicesInfo);
            Map<String, Integer> numberOfInterface = SizeMetrics.numberOfInterfaces(microservicesInfo);

            // Coupling类服务级指标
            Map<String, Double> relativeCouplingOfService = CouplingMetrics.relativeCouplingOfService(inAndOutDegree.getOutDegree(), microservicesInfo);
            Map<String, Double> relativeImportanceOfService = CouplingMetrics.relativeImportanceOfService(inAndOutDegree.getInDegree(), microservicesInfo);
            Map<String, Double> absCriticalityOfService = CouplingMetrics.absCriticalityOfService(inAndOutDegree.getInDegree(), inAndOutDegree.getOutDegree(), microservicesInfo);

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

                row_service.createCell(7).setCellValue(relativeCouplingOfService.get(msName));
                row_service.createCell(8).setCellValue(relativeImportanceOfService.get(msName));
                row_service.createCell(9).setCellValue(absCriticalityOfService.get(msName));
                row_service.createCell(10).setCellValue(MyCouplingMetricsDependency.incomingCouplingOfSByDependency(dependencies, msName));
                row_service.createCell(11).setCellValue(MyCouplingMetricsDependency.outgoingCouplingOfSByDependency(dependencies, msName));
                row_service.createCell(12).setCellValue(MyCouplingMetricsDependency.totalCouplingOfSByDependency(dependencies, msName));
                row_service.createCell(13).setCellValue((MyCouplingMetricsDependencyAndParams.incomingCouplingOfSByDependencyAndParams(msName, dependencies, topics)));
                row_service.createCell(14).setCellValue((MyCouplingMetricsDependencyAndParams.outgoingCouplingOfSByDependencyAndParams(msName, dependencies, topics)));
                row_service.createCell(15).setCellValue((MyCouplingMetricsDependencyAndParams.totalCouplingOfSByDependencyAndParams(msName, dependencies, topics)));

                row_service.createCell(16).setCellValue((CohesionMetrics.SIDC(microservice)));
                row_service.createCell(17).setCellValue((CohesionMetrics.SIUC(microservice, dependenciesInfo.getDependencies())));
                row_service.createCell(18).setCellValue((CohesionMetrics.TICS(microservice, dependenciesInfo.getDependencies())));

                k++;
            }


            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream("D:\\NJU\\GP\\Data\\maintain_metrics_data\\" + version.getVersionName() + ".xls");
                wb.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
