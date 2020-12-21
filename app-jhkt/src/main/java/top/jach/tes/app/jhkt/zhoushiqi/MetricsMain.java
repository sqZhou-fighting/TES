package top.jach.tes.app.jhkt.zhoushiqi;

import javafx.util.Pair;
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
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.complexity.ComplexityMetrics;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling.CouplingMetrics;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling.MyCouplingMetricsDependency;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling.MyCouplingMetricsDependencyAndParams;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling.MyCouplingMetricsPkg;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.size.SizeMetrics;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.CAP;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.DependencyUtil;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.InAndOutDegree;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.TopicUtil;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.jhkt.microservice.Pkg;
import top.jach.tes.plugin.tes.code.dependency.CSPair;
import top.jach.tes.plugin.tes.code.dependency.DependenciesInfo;
import top.jach.tes.plugin.tes.code.dependency.Direction;
import top.jach.tes.plugin.tes.code.git.version.Version;
import top.jach.tes.plugin.tes.code.git.version.VersionsInfo;
import top.jach.tes.plugin.tes.code.repo.ReposInfo;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: zhoushiqi
 * @date: 2020/11/26
 * @description: 计算所有指标
 */
public class MetricsMain extends DevApp {
    public static void main(String[] args) {
        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        ReposInfo reposInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.TargetSystem, ReposInfo.class);
        VersionsInfo versionsInfoForRelease = DataAction.queryLastInfo(context, InfoNameConstant.VersionsForRelease, VersionsInfo.class);
//        DecimalFormat df = new DecimalFormat("0.0000000000000000");
//        DecimalFormat df = new DecimalFormat("0.0000");

        for (Version version : versionsInfoForRelease.getVersions()) {
            // 计算指标所需要的数据
            MicroservicesInfo microservicesInfo = DataAction.queryLastMicroservices(context, reposInfo.getId(), null, version);
            DependenciesInfo dependenciesInfo = DataAction.queryLastDependencies(context, version);
            List<CSPair> dependencies = dependenciesInfo.getDependencies();
            CAP cap = DependencyUtil.getCAP(dependencies, microservicesInfo.getMicroservices());
            List<Direction> reqs = DependencyUtil.getDependencies(dependencies, microservicesInfo.getMicroservices());
            InAndOutDegree inAndOutDegree = DependencyUtil.getInAndOutDegree(dependencies, microservicesInfo.getMicroservices());
            Map<String, Pair<Set<String>, Set<String>>> serviceUtils = DependencyUtil.getServiceUtils(dependencies, microservicesInfo.getMicroservices());
            Map<String, Integer> topics = TopicUtil.getAllTopic(microservicesInfo);

            // 分别计算每个版本的指标结果，每个版本都有一个单独的Excel表
            HSSFWorkbook wb = new HSSFWorkbook();

            HSSFSheet sheet = wb.createSheet("系统级指标");
            HSSFRow head = sheet.createRow(0);
            head.createCell(0).setCellValue("System metric");
            head.createCell(1).setCellValue("Index");

            HSSFRow row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Size - Number of Service");
            row1.createCell(1).setCellValue(SizeMetrics.numberOfService(microservicesInfo.getMicroservices()));

            HSSFRow row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("Complexity - Extent of Aggregation");
            row2.createCell(1).setCellValue(ComplexityMetrics.extentOfAggregation(reqs, cap));

            HSSFRow row3 = sheet.createRow(3);
            row3.createCell(0).setCellValue("Complexity - Systems Centralization");
            row3.createCell(1).setCellValue(ComplexityMetrics.sysCentralization(cap, inAndOutDegree));

            HSSFRow row4 = sheet.createRow(4);
            row4.createCell(0).setCellValue("Complexity - Density of Aggregation");
            row4.createCell(1).setCellValue(ComplexityMetrics.densityOfAggregation(serviceUtils));

            HSSFRow row5 = sheet.createRow(5);
            row5.createCell(0).setCellValue("Complexity - Aggregator Centralization");
            row5.createCell(1).setCellValue(ComplexityMetrics.aggregatorCentralization(serviceUtils));

            HSSFRow row6 = sheet.createRow(6);
            row6.createCell(0).setCellValue("Coupling - Average Number of Directly Connected Services");
            row6.createCell(1).setCellValue(CouplingMetrics.avgNumServicesDirectly(inAndOutDegree.getInDegree(), inAndOutDegree.getOutDegree()));

            HSSFRow row7 = sheet.createRow(7);
            row7.createCell(0).setCellValue("Coupling - Systems Service Coupling");
            row7.createCell(1).setCellValue(CouplingMetrics.sysServicesCoupling(inAndOutDegree.getOutDegree(), cap));

            HSSFRow row8 = sheet.createRow(8);
            row8.createCell(0).setCellValue("Coupling - Service Coupling Factor");
            row8.createCell(1).setCellValue(CouplingMetrics.servicesCouplingFactor(inAndOutDegree.getOutDegree()));

            HSSFSheet sheet1 = wb.createSheet("服务级指标");
            HSSFRow row00 = sheet1.createRow(0);
            row00.createCell(0).setCellValue("microservice");
            row00.createCell(1).setCellValue("Size - Number of Consumer Services Directly");
            row00.createCell(2).setCellValue("Size - Number of Producer Services Directly");
            row00.createCell(3).setCellValue("Size - Number of Pkg");
            row00.createCell(4).setCellValue("Size - Number of Class");
            row00.createCell(5).setCellValue("Size - Number of Operation");
            row00.createCell(6).setCellValue("Coupling - Relative Coupling of Service");
            row00.createCell(7).setCellValue("Coupling - Relative Importance of Service");
            row00.createCell(8).setCellValue("Coupling - Absolute Criticality of the Service");
            row00.createCell(9).setCellValue("Coupling - IncomingCouplingOfSByDependency");
            row00.createCell(10).setCellValue("Coupling - OutgoingCouplingOfSByDependency");
            row00.createCell(11).setCellValue("Coupling - TotalCouplingOfSByDependency");
            row00.createCell(12).setCellValue("Coupling - IncomingCouplingOfSByDependencyAndParams");
            row00.createCell(13).setCellValue("Coupling - OutgoingCouplingOfSByDependenyAndParams");
            row00.createCell(14).setCellValue("Coupling - TotalCouplingOfSByDependencyAndParams");
            row00.createCell(15).setCellValue("Coupling - ExtraServiceIncomingCouplingOfS");
            row00.createCell(16).setCellValue("Coupling - ExtraServiceOutgoingCouplingOfS");
            row00.createCell(17).setCellValue("Coupling - TotalExtraServiceCouplingOfS");
            row00.createCell(18).setCellValue("Cohesion - Service Interface Data Cohesion");
            row00.createCell(19).setCellValue("Cohesion - Service Interface Usage Cohesion");
            row00.createCell(20).setCellValue("Coesion - Total Interface Cohesion of a Service");
            row00.createCell(21).setCellValue("Size - Avg Number Of Connected Services Directly");

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
                row_service.createCell(3).setCellValue(numberOfPkg.get(msName));
                row_service.createCell(4).setCellValue(numberOfClass.get(msName));
                row_service.createCell(5).setCellValue(numberOfInterface.get(msName));
                row_service.createCell(6).setCellValue(relativeCouplingOfService.get(msName));
                row_service.createCell(7).setCellValue(relativeImportanceOfService.get(msName));
                row_service.createCell(8).setCellValue(absCriticalityOfService.get(msName));
                row_service.createCell(9).setCellValue(MyCouplingMetricsDependency.incomingCouplingOfSByDependency(dependencies, msName));
                row_service.createCell(10).setCellValue(MyCouplingMetricsDependency.outgoingCouplingOfSByDependency(dependencies, msName));
                row_service.createCell(11).setCellValue(MyCouplingMetricsDependency.totalCouplingOfSByDependency(dependencies, msName));
//                row_service.createCell(12).setCellValue(df.format(MyCouplingMetricsDependencyAndParams.incomingCouplingOfSByDependencyAndParams(msName, dependencies, topics)));
//                row_service.createCell(13).setCellValue(df.format(MyCouplingMetricsDependencyAndParams.outgoingCouplingOfSByDependencyAndParams(msName, dependencies, topics)));
//                row_service.createCell(14).setCellValue(df.format(MyCouplingMetricsDependencyAndParams.totalCouplingOfSByDependencyAndParams(msName, dependencies, topics)));
                row_service.createCell(12).setCellValue((MyCouplingMetricsDependencyAndParams.incomingCouplingOfSByDependencyAndParams(msName, dependencies, topics)));
                row_service.createCell(13).setCellValue((MyCouplingMetricsDependencyAndParams.outgoingCouplingOfSByDependencyAndParams(msName, dependencies, topics)));
                row_service.createCell(14).setCellValue((MyCouplingMetricsDependencyAndParams.totalCouplingOfSByDependencyAndParams(msName, dependencies, topics)));

                row_service.createCell(15).setCellValue(MyCouplingMetricsPkg.extraServiceIncomingCouplingOfS(msName, microservicesInfo).size());
                row_service.createCell(16).setCellValue(MyCouplingMetricsPkg.extraServiceOutgoingCouplingOfS(microservice, microservicesInfo).size());
                row_service.createCell(17).setCellValue(MyCouplingMetricsPkg.totalExtraServiceCouplingOfS(microservice, microservicesInfo).size());
//                row_service.createCell(18).setCellValue(df.format(CohesionMetrics.SIDC(microservice)));
//                row_service.createCell(19).setCellValue(df.format(CohesionMetrics.SIUC(microservice, dependenciesInfo.getDependencies())));
//                row_service.createCell(20).setCellValue(df.format(CohesionMetrics.TICS(microservice, dependenciesInfo.getDependencies())));
                row_service.createCell(18).setCellValue((CohesionMetrics.SIDC(microservice)));
                row_service.createCell(19).setCellValue((CohesionMetrics.SIUC(microservice, dependenciesInfo.getDependencies())));
                row_service.createCell(20).setCellValue((CohesionMetrics.TICS(microservice, dependenciesInfo.getDependencies())));
                row_service.createCell(21).setCellValue(serviceAndDirectly.get(msName));

                k++;
            }

            HSSFSheet sheet2 = wb.createSheet("两服务间依赖耦合");
            HSSFRow row22 = sheet2.createRow(0);
            row22.createCell(0).setCellValue("Microservice A");
            row22.createCell(1).setCellValue("Microservice B");
            row22.createCell(2).setCellValue("Coupling Index");

            k = 1;
            List<Microservice> microservices = microservicesInfo.getMicroservices();
            for (int i = 0; i < microservices.size() - 1; i++) {
                for (int j = i + 1; j < microservices.size(); j++) {
                    HSSFRow row_two = sheet2.createRow(k);
                    row_two.createCell(0).setCellValue(microservices.get(i).getElementName());
                    row_two.createCell(1).setCellValue(microservices.get(j).getElementName());
                    row_two.createCell(2).setCellValue(MyCouplingMetricsDependency.callsCouplingInServicePair(dependencies,
                            microservices.get(i).getElementName(),
                            microservices.get(j).getElementName()));
                    k++;
                }
            }

            HSSFSheet sheet3 = wb.createSheet("两服务间依赖及信息耦合");
            HSSFRow row33 = sheet3.createRow(0);
            row33.createCell(0).setCellValue("Microservice A");
            row33.createCell(1).setCellValue("Microservice B");
            row33.createCell(2).setCellValue("Coupling Index");
            k = 1;
            for (int i = 0; i < microservices.size() - 1; i++) {
                for (int j = i + 1; j < microservices.size(); j++) {
                    HSSFRow row_three = sheet3.createRow(k);
                    row_three.createCell(0).setCellValue(microservices.get(i).getElementName());
                    row_three.createCell(1).setCellValue(microservices.get(j).getElementName());
//                    row_three.createCell(2).setCellValue(df.format(MyCouplingMetricsDependencyAndParams.callsCouplingInServicePairAndParams(microservices.get(i).getElementName(),
//                            microservices.get(j).getElementName(),
//                            dependencies,
//                            topics)));
                    row_three.createCell(2).setCellValue((MyCouplingMetricsDependencyAndParams.callsCouplingInServicePairAndParams(microservices.get(i).getElementName(),
                            microservices.get(j).getElementName(),
                            dependencies,
                            topics)));
                    k++;
                }
            }

            HSSFSheet sheet4 = wb.createSheet("包级指标");
            HSSFRow row44 = sheet4.createRow(0);
            row44.createCell(0).setCellValue("Microservice");
            row44.createCell(1).setCellValue("Pkg");
            row44.createCell(2).setCellValue("ExtraServiceIncomingCouplingOfE");
            row44.createCell(3).setCellValue("ExtraServiceOutgoingCouplingOfE");
            row44.createCell(4).setCellValue("TotalExtraServiceCouplingOfE");
            k = 1;
            for (Microservice microservice : microservices) {
                for (Pkg pkg : microservice.getPkgs()) {
                    HSSFRow row_pkg = sheet4.createRow(k);
                    row_pkg.createCell(0).setCellValue(microservice.getElementName());
                    row_pkg.createCell(1).setCellValue(pkg.getPkgPath());
                    row_pkg.createCell(2).setCellValue(MyCouplingMetricsPkg.extraServiceIncomingCouplingOfE(pkg, microservicesInfo).size());
                    row_pkg.createCell(3).setCellValue(MyCouplingMetricsPkg.extraServiceOutgoingCouplingOfE(pkg, microservice, microservicesInfo).size());
                    row_pkg.createCell(4).setCellValue(MyCouplingMetricsPkg.totalExtraServiceCouplingOfE(pkg, microservice, microservicesInfo).size());
                    k++;
                }
            }

            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream("D:\\NJU\\GP\\Data\\metrics_results_optimize\\" + version.getVersionName() + ".xls");
                wb.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
