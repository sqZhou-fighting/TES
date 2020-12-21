package top.jach.tes.plugin.tes.code.microservice;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.jgit.internal.storage.file.LazyObjectIdSetFile;
import top.jach.tes.core.impl.domain.element.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhoushiqi
 * @date: 2020/6/28
 */
@Getter
@Setter
public class HWMicroservice extends Element {
    String path;
    String repoName;
    Long codeLines;
    List<Topic> pubTopics = new ArrayList<>();
    List<Topic> subTopics = new ArrayList<>();

    public HWMicroservice setElementName(String elementName){
        this.elementName = elementName;
        return this;
    }

    public String getAllPath(){
        String allPath=this.repoName+"/"+this.path;
        return allPath;
    }
}
