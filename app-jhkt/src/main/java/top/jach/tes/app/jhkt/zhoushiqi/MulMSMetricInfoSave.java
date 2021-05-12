package top.jach.tes.app.jhkt.zhoushiqi;

import javafx.util.Pair;
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
import top.jach.tes.plugin.jhkt.msmetricsres.MSMetricRes;
import top.jach.tes.plugin.jhkt.msmetricsres.MetricRes;
import top.jach.tes.plugin.jhkt.multimsmetric.MulMSMetric;
import top.jach.tes.plugin.jhkt.multimsmetric.MulMSMetricInfo;
import top.jach.tes.plugin.tes.code.dependency.CSPair;
import top.jach.tes.plugin.tes.code.dependency.DependenciesInfo;
import top.jach.tes.plugin.tes.code.dependency.Direction;
import top.jach.tes.plugin.tes.code.git.version.Version;
import top.jach.tes.plugin.tes.code.git.version.VersionsInfo;
import top.jach.tes.plugin.tes.code.repo.ReposInfo;

import java.util.*;

/**
 * @Author: zhoushiqi
 * @date: 2021/4/6
 * @description:
 */
public class MulMSMetricInfoSave extends DevApp {
    public static void main(String[] args) {
        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        ReposInfo reposInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.TargetSystem, ReposInfo.class);
        VersionsInfo versionsInfoForRelease = DataAction.queryLastInfo(context, InfoNameConstant.VersionsForRelease, VersionsInfo.class);

        // 最终结果
        List<List<MSMetricRes>> msMetricResss = new ArrayList<>();
        for (Version version : versionsInfoForRelease.getVersions()) {
            List<MSMetricRes> msMetricRess = new ArrayList<>();

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
                GitCommitsForMicroserviceInfo gitCommitsForMicroserviceInfo = DataAction.queryLastGitCommitsForMicroserviceInfo(context, reposInfo.getId(), microservice.getElementName(), version);
                gitCommitsForMicroserviceInfoMap.put(microservice.getElementName(), gitCommitsForMicroserviceInfo);
            }

            Map<String, TmpData> mainTainRes = MaintainRes.mainTainResult_version(microservicesInfo, dtssInfo, bugMicroserviceRelations, gitCommitsForMicroserviceInfoMap, version.getVersionName());


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

            for (Microservice microservice : microservicesInfo.getMicroservices()) {
                String msName = microservice.getElementName();
                MSMetricRes msMetricRes = new MSMetricRes();
                msMetricRes.setMs_name(msName);
                List<MetricRes> metricRess = new ArrayList<>();
                // 写22个指标的数据
                // Size
                MetricRes metricRes1 = new MetricRes("Size", "NDCS", "Number of Consumer Services Directly", serviceAndConsumers.get(msName).doubleValue());
                metricRess.add(metricRes1);
                MetricRes metricRes2 = new MetricRes("Size", "NDPS", "Number of Producer Services Directly", serviceAndProducers.get(msName).doubleValue());
                metricRess.add(metricRes2);
                MetricRes metricRes3 = new MetricRes("Size", "NO", "Number of Operation", numberOfInterface.get(msName).doubleValue());
                metricRess.add(metricRes3);
                MetricRes metricRes4 = new MetricRes("Size", "NP", "Number of Pkg", numberOfPkg.get(msName).doubleValue());
                metricRess.add(metricRes4);
                MetricRes metricRes5 = new MetricRes("Size", "NC", "Number of Class", numberOfClass.get(msName).doubleValue());
                metricRess.add(metricRes5);

                // Coupling

                MetricRes metricRes6 = new MetricRes("Coupling", "RCS", "Relative Coupling of Service", relativeCouplingOfService.get(msName));
                metricRess.add(metricRes6);

                MetricRes metricRes7 = new MetricRes("Coupling", "RIS", "Relative Importance of Service", relativeImportanceOfService.get(msName));
                metricRess.add(metricRes7);

                MetricRes metricRes8 = new MetricRes("Coupling", "ACS", "Absolute Criticality of the Service", absCriticalityOfService.get(msName));
                metricRess.add(metricRes8);

                MetricRes metricRes9 = new MetricRes("Coupling", "AVGDS", "Average Number of Directly Connected Services", serviceAndDirectly.get(msName).doubleValue());
                metricRess.add(metricRes9);

                MetricRes metricRes10 = new MetricRes("Coupling", "ID", "Incoming Coupling of Service by Dependency", (double) MyCouplingMetricsDependency.incomingCouplingOfSByDependency(dependencies, msName));
                metricRess.add(metricRes10);

                MetricRes metricRes11 = new MetricRes("Coupling", "OD", "Outgoing Coupling of Service by Dependency", (double) MyCouplingMetricsDependency.outgoingCouplingOfSByDependency(dependencies, msName));
                metricRess.add(metricRes11);

                MetricRes metricRes12 = new MetricRes("Coupling", "TD", "Total Coupling of Service by Dependency", (double) MyCouplingMetricsDependency.totalCouplingOfSByDependency(dependencies, msName));
                metricRess.add(metricRes12);

                MetricRes metricRes13 = new MetricRes("Coupling", "IDP", "Incoming Coupling of Service by Dependency and Params",
                        MyCouplingMetricsDependencyAndParams.incomingCouplingOfSByDependencyAndParams(msName, dependencies, topics));
                metricRess.add(metricRes13);

                MetricRes metricRes14 = new MetricRes("Coupling", "ODP", "Outgoing Coupling of Service by Dependency and Params",
                        MyCouplingMetricsDependencyAndParams.outgoingCouplingOfSByDependencyAndParams(msName, dependencies, topics));
                metricRess.add(metricRes14);

                MetricRes metricRes15 = new MetricRes("Coupling", "TDP", "Total Coupling of Service by Dependency and Params",
                        MyCouplingMetricsDependencyAndParams.totalCouplingOfSByDependencyAndParams(msName, dependencies, topics));
                metricRess.add(metricRes15);

                // Cohesion

                MetricRes metricRes16 = new MetricRes("Cohesion", "SIDC", "Service Interface Data Cohesion", CohesionMetrics.SIDC(microservice));
                metricRess.add(metricRes16);

                MetricRes metricRes17 = new MetricRes("Cohesion", "SIUC", "Service Interface Usage Cohesion", CohesionMetrics.SIUC(microservice, dependenciesInfo.getDependencies()));
                metricRess.add(metricRes17);

                MetricRes metricRes18 = new MetricRes("Cohesion", "TSIC", "Total Interface Cohesion of a Service", CohesionMetrics.TICS(microservice, dependenciesInfo.getDependencies()));
                metricRess.add(metricRes18);

                MetricRes metricRes19 = new MetricRes("Cohesion", "PCG", "Pkgs Cohesion Graph", MyCohesionMetrics.MSCN(microservice));
                metricRess.add(metricRes19);

                // Direct Measurement

                MetricRes metricRes20 = new MetricRes("Direct Measurement", "COR", "Commit Overlap Ratio", mainTainRes.get(msName).getCommitOverlapRatio());
                metricRess.add(metricRes20);

                MetricRes metricRes21 = new MetricRes("Direct Measurement", "CFOR", "Commit Fileset Overlap Ratio", mainTainRes.get(msName).getCommitFilesetOverlapRatio());
                metricRess.add(metricRes21);

                MetricRes metricRes22 = new MetricRes("Direct Measurement", "PCO", "Pairwise Committer Overlap", mainTainRes.get(msName).getPairwiseCommitterOverlap());
                metricRess.add(metricRes22);

                msMetricRes.setMetricsRes(metricRess);
                msMetricRes.setVersion(version.getVersionName());
                msMetricRess.add(msMetricRes);
            }
            msMetricResss.add(msMetricRess);
        }

        //对七个版本开始整理
        List<MSMetric> allMSMetrics = new ArrayList<>();
        for (int i=0; i<msMetricResss.size(); i++){
            for (MSMetricRes msMetricRes : msMetricResss.get(i)){
                for (MetricRes msMetric : msMetricRes.getMetricsRes()){
                    MSMetric msAndMetric = new MSMetric();
                    msAndMetric.ms_name = msMetricRes.getMs_name();
                    msAndMetric.metric_name = msMetric.getMetric_name();
                    msAndMetric.metric_short = msMetric.getMetric_short();
                    msAndMetric.index = msMetric.getIndex();
                    msAndMetric.version = msMetricRes.getVersion();
                    allMSMetrics.add(msAndMetric);
                }
            }
        }

        // 开始归类
        List<MulMSMetric> mulMSMetricList = new ArrayList<>();
        int flag = allMSMetrics.size();
        while (allMSMetrics.size()>0){
            String ms_name = allMSMetrics.get(0).ms_name;
            String metric_name = allMSMetrics.get(0).metric_name;
            String metric = allMSMetrics.get(0).metric_short;
            Map<String, Double> version_indexs = new HashMap<>();
            version_indexs.put(allMSMetrics.get(0).version.substring(allMSMetrics.get(0).version.length()-5), allMSMetrics.get(0).index);
            //开始构建
            List<Integer> tmp = new ArrayList<>();
            for (int j=1; j<allMSMetrics.size(); j++){
                MSMetric msMetric = allMSMetrics.get(j);
                if (msMetric.ms_name.equals(ms_name) && msMetric.metric_short.equals(metric)){
                    version_indexs.put(msMetric.version.substring(msMetric.version.length()-5), msMetric.index);
                    tmp.add(j);
                    allMSMetrics.remove(j);
                    j--;
                }
            }

//            for (int k: tmp){
//                allMSMetrics.remove(k);
//            }

            // 构建最终形态
            MulMSMetric mulMSMetric = new MulMSMetric();
            mulMSMetric.setMs_name(ms_name);
            mulMSMetric.setMetric_name(metric_name);
            mulMSMetric.setMetric_short(metric);
            mulMSMetric.setVersion_indexs(version_indexs);
            mulMSMetricList.add(mulMSMetric);
            allMSMetrics.remove(0);
        }
        System.out.println("bp");

        MulMSMetricInfo mulMSMetricInfo = MulMSMetricInfo.createInfoByMulMSMetric("all", mulMSMetricList);
        mulMSMetricInfo.setName(InfoNameConstant.MulMSMetric);
        InfoTool.saveInfos(mulMSMetricInfo);
    }
}

class MSMetric{
    String ms_name;
    String metric_name;
    String metric_short;
    Double index;
    String version;
}