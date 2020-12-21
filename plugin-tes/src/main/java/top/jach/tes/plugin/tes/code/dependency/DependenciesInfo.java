package top.jach.tes.plugin.tes.code.dependency;

import lombok.Data;
import lombok.extern.java.Log;
import top.jach.tes.core.api.domain.info.Info;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhoushiqi
 * @date: 2020/11/26
 * @description: 用于将依赖信息存入数据库
 */

@Log
@Data
public class DependenciesInfo extends Info {
    public static final String INFO_Name = "DependenciesInfo";
    String version;
    List<CSPair> dependencies = new ArrayList<>();

    public static DependenciesInfo createInfo() {
        DependenciesInfo dependenciesInfo = new DependenciesInfo();
        dependenciesInfo.initBuild();
        return dependenciesInfo;
    }

    public DependenciesInfo addDependency(CSPair csPair){
        this.dependencies.add(csPair);
        return this;
    }

    public static DependenciesInfo createInfoByDependencies(String version, List<CSPair>... dependencies) {
        DependenciesInfo dependenciesInfo = createInfo();
        dependenciesInfo.version = version;
        for (List<CSPair> csPairs : dependencies) {
            for (CSPair csPair : csPairs) {
                dependenciesInfo.addDependency(csPair);
            }
        }
        return dependenciesInfo;
    }

    public String getVersion() {
        return version;
    }

    public DependenciesInfo setVersion(String version) {
        this.version = version;
        return this;
    }
}
