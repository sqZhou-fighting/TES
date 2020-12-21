/*
package top.jach.tes.plugin.tes.code.microservice;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import top.jach.tes.core.api.domain.info.Info;
import top.jach.tes.core.impl.domain.element.ElementsInfo;
import top.jach.tes.plugin.tes.code.repo.WithRepo;

import java.util.*;

*/
/**
 * @Author: zhoushiqi
 * @date: 2020/6/28
 *//*

@Getter
@Setter
public class HWMicroservicesInfo extends ElementsInfo<HWMicroservice> implements WithRepo {
    private Long reposId;
    private String repoName;
    private String version;
    private List<HWMicroservice> hwMicroservices = new ArrayList<>();

    public static HWMicroservicesInfo createInfo(){
        HWMicroservicesInfo info = new HWMicroservicesInfo();
        info.initBuild();
        return info;
    }

    public static HWMicroservicesInfo createInfo(Info... infos){
        HWMicroservicesInfo microservices = createInfo();
        for (Info info : infos) {
            if (info instanceof HWMicroservicesInfo) {
                HWMicroservicesInfo hwmicroservicesInfo = (HWMicroservicesInfo) info;
                for (HWMicroservice m : hwmicroservicesInfo.getHwMicroservices()) {
                    String[] paths = hwmicroservicesInfo.getRepoName().split("/");
                    m.setElementName(StringUtils.strip(paths[paths.length-1]+"/"+m.getPath(), "/"));
                    microservices.addMicroservice(m);
                }
            }
        }
        return microservices;
    }

    public HWMicroservicesInfo addMicroservice(HWMicroservice... hwMicroservices){
        this.hwMicroservices.addAll(Arrays.asList(hwMicroservices));
        return this;
    }

    public List<HWMicroservice> getMicroserviceBySubTopic(String subTopic){
        List<HWMicroservice> hwMicroservices = new ArrayList<>();
        for (HWMicroservice hwMicroservice : this.hwMicroservices) {
            if (hwMicroservice.getSubTopics().contains(subTopic)) {
                hwMicroservices.add(hwMicroservice);
            }
        };
        return hwMicroservices;
    }

    public List<HWMicroservice> getMicroserviceBySubTopics(Set<String> subTopics){
        List<HWMicroservice> hwMicroservices = new ArrayList<>();
        for (HWMicroservice hwMicroservice : this.hwMicroservices) {
            Set<String> requl=new HashSet<>(new HashSet<>(subTopics));
            requl.retainAll(hwMicroservice.getSubTopics());
            if (requl.size()>0) {
                hwMicroservices.add(hwMicroservice);
            }
        }
        return hwMicroservices;
    }

    @Override
    public HWMicroservice getElementByName(String elementName) {
        for (HWMicroservice hwMicroservice : hwMicroservices){
            if (hwMicroservice.getElementName().equals(elementName)){
                return hwMicroservice;
            }
        }
        return null;
    }

    @Override
    public Iterator<HWMicroservice> iterator() {
        return this.hwMicroservices.iterator();
    }
}
*/
