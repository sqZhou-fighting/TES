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
import top.jach.tes.plugin.jhkt.metricmsres.MSRes;
import top.jach.tes.plugin.jhkt.metricmsres.MetricMSRes;
import top.jach.tes.plugin.jhkt.metricmsres.MetricMSResInfo;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.jhkt.msmetricsres.MSMetricRes;
import top.jach.tes.plugin.tes.code.dependency.CSPair;
import top.jach.tes.plugin.tes.code.dependency.DependenciesInfo;
import top.jach.tes.plugin.tes.code.dependency.Direction;
import top.jach.tes.plugin.tes.code.git.version.Version;
import top.jach.tes.plugin.tes.code.git.version.VersionsInfo;
import top.jach.tes.plugin.tes.code.repo.ReposInfo;

import java.util.*;

/**
 * @Author: zhoushiqi
 * @date: 2021/3/29
 * @description:
 */
public class MetricMSResInfoSave extends DevApp {
    public static void main(String[] args) {
        Context context = Environment.contextFactory.createContext(Environment.defaultProject);
        ReposInfo reposInfo = InfoTool.queryLastInfoByNameAndInfoClass(InfoNameConstant.TargetSystem, ReposInfo.class);
        VersionsInfo versionsInfoForRelease = DataAction.queryLastInfo(context, InfoNameConstant.VersionsForRelease, VersionsInfo.class);
//        DecimalFormat df = new DecimalFormat("0.0000000000000000");
//        DecimalFormat df = new DecimalFormat("0.0000");

        for (Version version : versionsInfoForRelease.getVersions()) {
            List<MetricMSRes> metricMSRess = new ArrayList<>();

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
            Map<String, Integer> numberOfPkg = SizeMetrics.numberOfPackages(microservicesInfo);
            Map<String, Integer> numberOfClass = SizeMetrics.numberOfClasses(microservicesInfo);
            Map<String, Integer> numberOfInterface = SizeMetrics.numberOfInterfaces(microservicesInfo);

            // Coupling类服务级指标
            Map<String, Double> relativeCouplingOfService = CouplingMetrics.relativeCouplingOfService(inAndOutDegree.getOutDegree(), microservicesInfo);
            Map<String, Double> relativeImportanceOfService = CouplingMetrics.relativeImportanceOfService(inAndOutDegree.getInDegree(), microservicesInfo);
            Map<String, Double> absCriticalityOfService = CouplingMetrics.absCriticalityOfService(inAndOutDegree.getInDegree(), inAndOutDegree.getOutDegree(), microservicesInfo);
            Map<String, Integer> serviceAndDirectly = SizeMetrics.avgNumberOfConnectedServicesDirectly(inAndOutDegree.getInDegree(), inAndOutDegree.getOutDegree(), microservicesInfo.getMicroservices().size());

            // 22个指标开始存
            List<MSRes> msRess1 = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : serviceAndConsumers.entrySet()){
                msRess1.add(new MSRes(entry.getKey(), (double)entry.getValue()));
            }
            MetricMSRes metricMSRes1 = new MetricMSRes("NDCS", version.getVersionName(), msRess1);
            metricMSRess.add(metricMSRes1);

            List<MSRes> msRess2 = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : serviceAndProducers.entrySet()){
                msRess2.add(new MSRes(entry.getKey(), (double)entry.getValue()));
            }
            MetricMSRes metricMSRes2 = new MetricMSRes("NDPS", version.getVersionName(), msRess2);
            metricMSRess.add(metricMSRes2);

            List<MSRes> msRess3 = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : numberOfInterface.entrySet()){
                msRess3.add(new MSRes(entry.getKey(), (double)entry.getValue()));
            }
            MetricMSRes metricMSRes3 = new MetricMSRes("NO", version.getVersionName(), msRess3);
            metricMSRess.add(metricMSRes3);

            List<MSRes> msRess4 = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : numberOfPkg.entrySet()){
                msRess4.add(new MSRes(entry.getKey(), (double)entry.getValue()));
            }
            MetricMSRes metricMSRes4 = new MetricMSRes("NP", version.getVersionName(), msRess4);
            metricMSRess.add(metricMSRes4);

            List<MSRes> msRess5 = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : numberOfClass.entrySet()){
                msRess5.add(new MSRes(entry.getKey(), (double)entry.getValue()));
            }
            MetricMSRes metricMSRes5 = new MetricMSRes("NC", version.getVersionName(), msRess5);
            metricMSRess.add(metricMSRes5);

            List<MSRes> msRess6 = new ArrayList<>();
            for (Map.Entry<String, Double> entry : relativeCouplingOfService.entrySet()){
                msRess6.add(new MSRes(entry.getKey(), entry.getValue()));
            }
            MetricMSRes metricMSRes6 = new MetricMSRes("RCS", version.getVersionName(), msRess6);
            metricMSRess.add(metricMSRes6);

            List<MSRes> msRess7 = new ArrayList<>();
            for (Map.Entry<String, Double> entry : relativeImportanceOfService.entrySet()){
                msRess7.add(new MSRes(entry.getKey(), entry.getValue()));
            }
            MetricMSRes metricMSRes7 = new MetricMSRes("RIS", version.getVersionName(), msRess7);
            metricMSRess.add(metricMSRes7);

            List<MSRes> msRess8 = new ArrayList<>();
            for (Map.Entry<String, Double> entry : absCriticalityOfService.entrySet()){
                msRess8.add(new MSRes(entry.getKey(), entry.getValue()));
            }
            MetricMSRes metricMSRes8 = new MetricMSRes("ACS", version.getVersionName(), msRess8);
            metricMSRess.add(metricMSRes8);

            List<MSRes> msRess9 = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : serviceAndDirectly.entrySet()){
                msRess9.add(new MSRes(entry.getKey(), (double)entry.getValue()));
            }
            MetricMSRes metricMSRes9 = new MetricMSRes("AVGDS", version.getVersionName(), msRess9);
            metricMSRess.add(metricMSRes9);

            List<MSRes> msRess10 = new ArrayList<>();
            List<MSRes> msRess11 = new ArrayList<>();
            List<MSRes> msRess12 = new ArrayList<>();
            List<MSRes> msRess13 = new ArrayList<>();
            List<MSRes> msRess14 = new ArrayList<>();
            List<MSRes> msRess15 = new ArrayList<>();
            List<MSRes> msRess16 = new ArrayList<>();
            List<MSRes> msRess17 = new ArrayList<>();
            List<MSRes> msRess18 = new ArrayList<>();
            List<MSRes> msRess19 = new ArrayList<>();
            List<MSRes> msRess20 = new ArrayList<>();
            List<MSRes> msRess21 = new ArrayList<>();
            List<MSRes> msRess22 = new ArrayList<>();
            for (Microservice microservice : microservicesInfo.getMicroservices()){
                String msName = microservice.getElementName();
                msRess10.add(new MSRes(msName, (double)MyCouplingMetricsDependency.incomingCouplingOfSByDependency(dependencies, msName)));
                msRess11.add(new MSRes(msName, (double)MyCouplingMetricsDependency.outgoingCouplingOfSByDependency(dependencies, msName)));
                msRess12.add(new MSRes(msName, (double)MyCouplingMetricsDependency.totalCouplingOfSByDependency(dependencies, msName)));
                msRess13.add(new MSRes(msName, MyCouplingMetricsDependencyAndParams.incomingCouplingOfSByDependencyAndParams(msName, dependencies, topics)));
                msRess14.add(new MSRes(msName, MyCouplingMetricsDependencyAndParams.outgoingCouplingOfSByDependencyAndParams(msName, dependencies, topics)));
                msRess15.add(new MSRes(msName, MyCouplingMetricsDependencyAndParams.totalCouplingOfSByDependencyAndParams(msName, dependencies, topics)));
                msRess16.add(new MSRes(msName, CohesionMetrics.SIDC(microservice)));
                msRess17.add(new MSRes(msName, CohesionMetrics.SIUC(microservice, dependenciesInfo.getDependencies())));
                msRess18.add(new MSRes(msName, CohesionMetrics.TICS(microservice, dependenciesInfo.getDependencies())));
                msRess19.add(new MSRes(msName, MyCohesionMetrics.MSCN(microservice)));
                msRess20.add(new MSRes(msName, mainTainRes.get(msName).getCommitOverlapRatio()!=null?mainTainRes.get(msName).getCommitOverlapRatio():-1));
                msRess21.add(new MSRes(msName, mainTainRes.get(msName).getCommitFilesetOverlapRatio()!=null?mainTainRes.get(msName).getCommitFilesetOverlapRatio():-1));
                msRess22.add(new MSRes(msName, mainTainRes.get(msName).getPairwiseCommitterOverlap()!=null?mainTainRes.get(msName).getPairwiseCommitterOverlap():-1));
            }
            MetricMSRes metricMSRes10 = new MetricMSRes("ID", version.getVersionName(), msRess10);
            MetricMSRes metricMSRes11 = new MetricMSRes("OD", version.getVersionName(), msRess11);
            MetricMSRes metricMSRes12 = new MetricMSRes("TD", version.getVersionName(), msRess12);
            MetricMSRes metricMSRes13 = new MetricMSRes("IDP", version.getVersionName(), msRess13);
            MetricMSRes metricMSRes14 = new MetricMSRes("ODP", version.getVersionName(), msRess14);
            MetricMSRes metricMSRes15 = new MetricMSRes("TDP", version.getVersionName(), msRess15);
            MetricMSRes metricMSRes16 = new MetricMSRes("SIDC", version.getVersionName(), msRess16);
            MetricMSRes metricMSRes17 = new MetricMSRes("SIUC", version.getVersionName(), msRess17);
            MetricMSRes metricMSRes18 = new MetricMSRes("TSIC", version.getVersionName(), msRess18);
            MetricMSRes metricMSRes19 = new MetricMSRes("PCG", version.getVersionName(), msRess19);
            MetricMSRes metricMSRes20 = new MetricMSRes("COR", version.getVersionName(), msRess20);
            MetricMSRes metricMSRes21 = new MetricMSRes("CFOR", version.getVersionName(), msRess21);
            MetricMSRes metricMSRes22 = new MetricMSRes("PCO", version.getVersionName(), msRess22);

            metricMSRess.add(metricMSRes10);
            metricMSRess.add(metricMSRes11);
            metricMSRess.add(metricMSRes12);
            metricMSRess.add(metricMSRes13);
            metricMSRess.add(metricMSRes14);
            metricMSRess.add(metricMSRes15);
            metricMSRess.add(metricMSRes16);
            metricMSRess.add(metricMSRes17);
            metricMSRess.add(metricMSRes18);
            metricMSRess.add(metricMSRes19);
            metricMSRess.add(metricMSRes20);
            metricMSRess.add(metricMSRes21);
            metricMSRess.add(metricMSRes22);

            MetricMSResInfo metricMSResInfo = MetricMSResInfo.createInfoByMetricMSRes(version.getVersionName(),
                    metricMSRess);
            metricMSResInfo.setName(InfoNameConstant.MetricMSRes);
            InfoTool.saveInfos(metricMSResInfo);
        }
    }
}
