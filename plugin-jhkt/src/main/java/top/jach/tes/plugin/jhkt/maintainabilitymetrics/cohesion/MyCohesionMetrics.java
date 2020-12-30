package top.jach.tes.plugin.jhkt.maintainabilitymetrics.cohesion;

import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.Pkg;

import java.util.*;

/**
 * @Author: zhoushiqi
 * @date: 2020/12/29
 * @description: 自定义的cohesion指标，求图中连通块构成的微服务内聚
 */
public class MyCohesionMetrics {

    // 求微服务中包调用的连通块数
    // microservice cohesion number
    public static double MSCN(Microservice microservice) {
        double res = 0;

        List<Pkg> pkgs = statisticOwnerPkgs(microservice);
        List<List<String>> connectedBlocks = new ArrayList<>();


        while (pkgs.size() > 0) {
            List<String> block = BSF(pkgs.get(0), pkgs, microservice.getElementName());
            if (block != null) {
                connectedBlocks.add(block);
            }

        }

//        System.out.println("---------------------------------------------------------------------------");
        for (List<String> bl : connectedBlocks) {
//            System.out.println(bl.size() + " " + bl.size() * 1.0 / microservice.getPkgs().size());
            res += (bl.size() * 1.0 / microservice.getPkgs().size())*(bl.size() * 1.0 / microservice.getPkgs().size());
        }

//        System.out.println("包数量  " + microservice.getPkgs().size() + "  计算后的包数量 " + res_pkg);

//        System.out.println(microservice.getElementName() + " " + microservice.getPkgs().size() + " "+ res);

        return res;
    }

    private static List<String> BSF(Pkg pkg, List<Pkg> pkgs, String msName) {

        List<String> block = new ArrayList<>();
        block.add(pkg.getPkgPath());
        pkgs.remove(pkg);
        Set<String> tmp_ = new HashSet<>();
        tmp_.addAll(block);

        // 将目标包调用的包的包名入队
        for (Map.Entry<String, List<String>> entry : pkg.getInvokedPkgs().entrySet()) {
            for (int i = 0; i < entry.getValue().size(); i++) {
                String pkgPath = entry.getValue().get(i).substring(1, entry.getValue().get(i).length() - 1);
                if (pkgPath.contains(msName) && !tmp_.contains(pkgPath)) {
                    block.add(pkgPath);
                    tmp_.add(pkgPath);
                }
            }
        }
        // 将调用目标包的包的包名入队
        for (Pkg pkg1 : pkgs) {
            if (pkg1.getPkgPath().contains(pkg.getPkgPath())
                    && !tmp_.contains(pkg1.getPkgPath())) {
                block.add(pkg1.getPkgPath());
                tmp_.add(pkg1.getPkgPath());
            }else {
                for (Map.Entry<String, List<String>> entry : pkg1.getInvokedPkgs().entrySet()) {
                    for (int i = 0; i < entry.getValue().size(); i++) {
                        if (pkg.getPkgPath().equals(entry.getValue().get(i).substring(1, entry.getValue().get(i).length() - 1))
                                && !tmp_.contains(pkg1.getPkgPath())) {
                            block.add(pkg1.getPkgPath());
                            tmp_.add(pkg1.getPkgPath());
                        }
                    }
                }
            }

        }
        int flag = 1;

        while (flag < block.size()) {
            if (pkgs.size() > 0) {
                List<Pkg> rmPkg = new ArrayList<>();
                for (Pkg pkg1 : pkgs) {
                    if (flag >= block.size()) {
                        break;
                    }
                    // 添加节点的所有的子节点
                    if (pkg1.getPkgPath().equals(block.get(flag))) {
                        for (Map.Entry<String, List<String>> entry : pkg1.getInvokedPkgs().entrySet()) {
                            for (int i = 0; i < entry.getValue().size(); i++) {
                                String pkgPath = entry.getValue().get(i).substring(1, entry.getValue().get(i).length() - 1);
                                if (!tmp_.contains(pkgPath) && pkgPath.contains(msName)) {
                                    block.add(pkgPath);
                                    tmp_.add(pkgPath);
                                }
                            }
                        }
                        rmPkg.add(pkg1);
                        flag++;
                    } else {
                        // 添加调用节点的父节点
                        if (pkg1.getPkgPath().contains(block.get(flag))
                                && !tmp_.contains(pkg1.getPkgPath())) {
                            block.add(pkg1.getPkgPath());
                            tmp_.add(pkg1.getPkgPath());
                        }else {
                            for (Map.Entry<String, List<String>> entry : pkg1.getInvokedPkgs().entrySet()) {
                                boolean fl = false;
                                for (int i = 0; i < entry.getValue().size(); i++) {
                                    if ((block.get(flag)).equals(entry.getValue().get(i).substring(1, entry.getValue().get(i).length() - 1))
                                            && !tmp_.contains(pkg1.getPkgPath())) {
                                        block.add(pkg1.getPkgPath());
                                        tmp_.add(pkg1.getPkgPath());
                                        fl = true;
                                        break;
                                    }
                                }
                                if (fl) {
                                    break;
                                }
                            }
                        }

                    }

                }
                if (rmPkg.size() == 0) {
                    block.remove(block.get(flag));
                    continue;
                }
                for (Pkg pkg1 : rmPkg) {
                    pkgs.remove(pkg1);
                }
            } else {
                break;
            }
        }
        return block;
    }

    private static List<Pkg> statisticOwnerPkgs(Microservice microservice) {
        List<Pkg> ownerPkgs = new ArrayList<>();
        for (Pkg pkg : microservice.getPkgs()) {
            if (pkg.getPkgPath().contains(microservice.getRepoName())) {
                ownerPkgs.add(pkg);
            }
        }
        return ownerPkgs;
    }
}
