package top.jach.tes.app.jhkt.zhoushiqi.validationdata;

import javafx.util.Pair;
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
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.cohesion.CohesionMetrics;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.cohesion.MyCohesionMetrics;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling.CouplingMetrics;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling.MyCouplingMetricsDependency;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling.MyCouplingMetricsDependencyAndParams;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.size.SizeMetrics;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.CAP;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.DependencyUtil;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.InAndOutDegree;
import top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils.TopicUtil;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.tes.code.dependency.CSPair;
import top.jach.tes.plugin.tes.code.dependency.DependenciesInfo;
import top.jach.tes.plugin.tes.code.dependency.Direction;
import top.jach.tes.plugin.tes.code.git.version.Version;
import top.jach.tes.plugin.tes.code.git.version.VersionsInfo;
import top.jach.tes.plugin.tes.code.repo.ReposInfo;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: zhoushiqi
 * @date: 2020/12/9
 * @description: 所有指标一起做相关性分析
 */
public class AssocaitionAllData extends DevApp {
    public static void main(String[] args) {
        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        ReposInfo reposInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.TargetSystem, ReposInfo.class);
        VersionsInfo versionsInfoForRelease = DataAction.queryLastInfo(context, InfoNameConstant.VersionsForRelease, VersionsInfo.class);

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

            HSSFSheet sheet1 = wb.createSheet("21个指标");
            HSSFRow row00 = sheet1.createRow(0);
            row00.createCell(0).setCellValue("microservice");
            row00.createCell(1).setCellValue("Size - Number of Consumer Services Directly");
            row00.createCell(2).setCellValue("Size - Number of Producer Services Directly");
            row00.createCell(3).setCellValue("Size - Number of Operation");
            row00.createCell(4).setCellValue("Size - Number of Pkg");
            row00.createCell(5).setCellValue("Size - Number of Class");

            row00.createCell(6).setCellValue("Coupling - Relative Coupling of Service");
            row00.createCell(7).setCellValue("Coupling - Relative Importance of Service");
            row00.createCell(8).setCellValue("Coupling - Absolute Criticality of the Service");
            row00.createCell(9).setCellValue("Coupling - Avg Number Of Connected Services Directly");
            row00.createCell(10).setCellValue("Coupling - IncomingCouplingOfSByDependency");
            row00.createCell(11).setCellValue("Coupling - OutgoingCouplingOfSByDependency");
            row00.createCell(12).setCellValue("Coupling - TotalCouplingOfSByDependency");
            row00.createCell(13).setCellValue("Coupling - IncomingCouplingOfSByDependencyAndParams");
            row00.createCell(14).setCellValue("Coupling - OutgoingCouplingOfSByDependenyAndParams");
            row00.createCell(15).setCellValue("Coupling - TotalCouplingOfSByDependencyAndParams");

            row00.createCell(16).setCellValue("Cohesion - Service Interface Data Cohesion");
            row00.createCell(17).setCellValue("Cohesion - Service Interface Usage Cohesion");
            row00.createCell(18).setCellValue("Cohesion - Total Interface Cohesion of a Service");
            row00.createCell(19).setCellValue("Cohesion - pkgs graph");

            row00.createCell(20).setCellValue("Lines of Code");

            row00.createCell(21).setCellValue("Commit Overlap Ratio (COR)");
            row00.createCell(22).setCellValue("Commit Fileset Overlap Ratio (CFOR)");
            row00.createCell(23).setCellValue("Pairwise Committer Overlap (PCO)");


            // Size类服务级指标
            Map<String, Integer> serviceAndConsumers = SizeMetrics.numberOfConsumerServicesDirectly(inAndOutDegree.getOutDegree());
            Map<String, Integer> serviceAndProducers = SizeMetrics.numberOfProducerServicesDirectly(inAndOutDegree.getInDegree());
            Map<String, Integer> numberOfPkg = SizeMetrics.numberOfPackages(microservicesInfo);
            Map<String, Integer> numberOfClass = SizeMetrics.numberOfClasses(microservicesInfo);
            Map<String, Integer> numberOfInterface = SizeMetrics.numberOfInterfaces(microservicesInfo);

            // Coupling类服务级指标
            Map<String, Double> relativeCouplingOfService = CouplingMetrics.relativeCouplingOfService(inAndOutDegree.getOutDegree(), microservicesInfo);
            Map<String, Double> relativeImportanceOfService = CouplingMetrics.relativeImportanceOfService(inAndOutDegree.getInDegree(), microservicesInfo);
            Map<String, Double> absCriticalityOfService = CouplingMetrics.absCriticalityOfService(inAndOutDegree.getInDegree(), inAndOutDegree.getOutDegree(), microservicesInfo);
            Map<String, Double> avgNumberOfConnectedServices = CouplingMetrics.avgNumberOfConnectedServicesDirectly(inAndOutDegree.getInDegree(), inAndOutDegree.getOutDegree(), microservicesInfo.getMicroservices().size());

            int k = 1;
            for (Microservice microservice : microservicesInfo.getMicroservices()) {
                HSSFRow row_service = sheet1.createRow(k);
                String msName = microservice.getElementName();
                row_service.createCell(0).setCellValue(msName);
                row_service.createCell(1).setCellValue(serviceAndConsumers.get(msName));
                row_service.createCell(2).setCellValue(serviceAndProducers.get(msName));
                row_service.createCell(3).setCellValue(numberOfInterface.get(msName));
                row_service.createCell(4).setCellValue(numberOfPkg.get(msName));
                row_service.createCell(5).setCellValue(numberOfClass.get(msName));

                row_service.createCell(6).setCellValue(relativeCouplingOfService.get(msName));
                row_service.createCell(7).setCellValue(relativeImportanceOfService.get(msName));
                row_service.createCell(8).setCellValue(absCriticalityOfService.get(msName));
                row_service.createCell(9).setCellValue(avgNumberOfConnectedServices.get(msName));

                row_service.createCell(10).setCellValue(MyCouplingMetricsDependency.incomingCouplingOfSByDependency(dependencies, msName));
                row_service.createCell(11).setCellValue(MyCouplingMetricsDependency.outgoingCouplingOfSByDependency(dependencies, msName));
                row_service.createCell(12).setCellValue(MyCouplingMetricsDependency.totalCouplingOfSByDependency(dependencies, msName));
                row_service.createCell(13).setCellValue((MyCouplingMetricsDependencyAndParams.incomingCouplingOfSByDependencyAndParams(msName, dependencies, topics)));
                row_service.createCell(14).setCellValue((MyCouplingMetricsDependencyAndParams.outgoingCouplingOfSByDependencyAndParams(msName, dependencies, topics)));
                row_service.createCell(15).setCellValue((MyCouplingMetricsDependencyAndParams.totalCouplingOfSByDependencyAndParams(msName, dependencies, topics)));

                row_service.createCell(16).setCellValue((CohesionMetrics.SIDC(microservice)));
                row_service.createCell(17).setCellValue((CohesionMetrics.SIUC(microservice, dependenciesInfo.getDependencies())));
                row_service.createCell(18).setCellValue((CohesionMetrics.TICS(microservice, dependenciesInfo.getDependencies())));
                row_service.createCell(19).setCellValue(MyCohesionMetrics.MSCN(microservice));

                row_service.createCell(20).setCellValue(microservice.getCodeLines());

                if (mainTainRes.get(msName).getCommitOverlapRatio() != null) {
                    row_service.createCell(21).setCellValue(mainTainRes.get(msName).getCommitOverlapRatio());
                }
                if (mainTainRes.get(msName).getCommitFilesetOverlapRatio() != null) {
                    row_service.createCell(22).setCellValue(mainTainRes.get(msName).getCommitFilesetOverlapRatio());
                }
                if (mainTainRes.get(msName).getPairwiseCommitterOverlap() != null) {
                    row_service.createCell(23).setCellValue(mainTainRes.get(msName).getPairwiseCommitterOverlap());
                }

                k++;
            }


            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream("D:\\NJU\\GP\\Data\\validation\\Association\\all_data\\" + version.getVersionName() + ".xls");
                wb.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
