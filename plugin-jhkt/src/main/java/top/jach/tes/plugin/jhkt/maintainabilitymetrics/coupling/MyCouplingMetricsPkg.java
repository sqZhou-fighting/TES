package top.jach.tes.plugin.jhkt.maintainabilitymetrics.coupling;

import org.eclipse.jgit.util.io.LimitedInputStream;
import top.jach.tes.core.impl.domain.element.Element;
import top.jach.tes.plugin.jhkt.microservice.ElementCouplingParams;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.jhkt.microservice.Pkg;

import java.util.*;

/**
 * @Author: zhoushiqi
 * @date: 2020/10/21
 * @description: 947 自己设计的关于耦合的和指标，主要是微服务之间的包调用所构成的耦合
 */
public class MyCouplingMetricsPkg {
    public static Set<ElementCouplingParams> extraServiceIncomingCouplingOfE(Pkg pkg,
                                                                             MicroservicesInfo microservicesInfo) {
        // 有一个待确定的问题是：Pkg类中的pkgName与pkgPath都分别表示什么呢？
        // 本方法的前提假设是：pkgName为go文件中import所写明的就是包名
        // pkgPath才是import里写的那个
        Set<ElementCouplingParams> incomingCouplingE = new HashSet<>();
        for (Microservice microservice : microservicesInfo.getMicroservices()) {
            for (Pkg pkg1 : microservice.getPkgs()) {
                for (Map.Entry<String, List<String>> entry : pkg1.getInvokedPkgs().entrySet()) {
                    for (String pkg_path : entry.getValue()) {
                        if (pkg_path.equals(pkg.getPkgPath())) {
                            System.out.println("in - " + pkg_path);
                            System.out.println(pkg.getPkgPath());
                            incomingCouplingE.add(new ElementCouplingParams(pkg1.getPkgPath(), microservice.getElementName()));
                        }
                    }
                }
            }
        }
        return incomingCouplingE;
    }

    public static Set<ElementCouplingParams> extraServiceOutgoingCouplingOfE(Pkg pkg,
                                                                             Microservice thisMicroservice,
                                                                             MicroservicesInfo microservicesInfo) {
        List<String> ms = new ArrayList<>();
        for (Microservice microservice : microservicesInfo.getMicroservices()) {
            ms.add(microservice.getElementName());
        }

        Set<ElementCouplingParams> outgoingCouplingE = new HashSet<>();
        for (Map.Entry<String, List<String>> entry : pkg.getInvokedPkgs().entrySet()) {
            for (String pkg_path : entry.getValue()) {
                for (int i = 0; i < ms.size(); i++) {
                    if (pkg_path.contains(ms.get(i))&& !pkg_path.contains(thisMicroservice.getElementName())){
                        outgoingCouplingE.add(new ElementCouplingParams(pkg_path, ms.get(i)));
                        break;
                    }
                }
            }
        }

        return outgoingCouplingE;
    }

    public static Set<ElementCouplingParams> totalExtraServiceCouplingOfE(Pkg pkg,
                                                                          Microservice thisMicroservice,
                                                                          MicroservicesInfo microservicesInfo) {
        Set<ElementCouplingParams> totalCouplingE = new HashSet<>();
        totalCouplingE.addAll(extraServiceIncomingCouplingOfE(pkg, microservicesInfo));
        totalCouplingE.addAll(extraServiceOutgoingCouplingOfE(pkg, thisMicroservice, microservicesInfo));
        return totalCouplingE;
    }

    public static Set<ElementCouplingParams> extraServiceIncomingCouplingOfS(String microserviceName,
                                                                             MicroservicesInfo microservicesInfo) {
        Set<ElementCouplingParams> IncomingCoupingS = new HashSet<>();
        for (Microservice microservice : microservicesInfo.getMicroservices()) {
            if (microservice.getElementName().equals(microserviceName)) {
                for (Pkg pkg : microservice.getPkgs()) {
                    IncomingCoupingS.addAll(extraServiceIncomingCouplingOfE(pkg, microservicesInfo));
                }
            }
        }
        return IncomingCoupingS;
    }

    public static Set<ElementCouplingParams> extraServiceOutgoingCouplingOfS(Microservice thisMicroservice,
                                                                             MicroservicesInfo microservicesInfo) {
        Set<ElementCouplingParams> OutgoingCouplingS = new HashSet<>();
        for (Microservice microservice : microservicesInfo.getMicroservices()) {
            if (microservice.getElementName().equals(thisMicroservice.getElementName())) {
                for (Pkg pkg : microservice.getPkgs()) {
                    OutgoingCouplingS.addAll(extraServiceOutgoingCouplingOfE(pkg, thisMicroservice, microservicesInfo));
                }
            }
        }
        return OutgoingCouplingS;
    }

    public static Set<ElementCouplingParams> totalExtraServiceCouplingOfS(Microservice thisMicroservice,
                                                                          MicroservicesInfo microservicesInfo) {
        Set<ElementCouplingParams> totalCouplingS = new HashSet<>();
        totalCouplingS.addAll(extraServiceIncomingCouplingOfS(thisMicroservice.getElementName(), microservicesInfo));
        totalCouplingS.addAll(extraServiceOutgoingCouplingOfS(thisMicroservice, microservicesInfo));
        return totalCouplingS;
    }

}
