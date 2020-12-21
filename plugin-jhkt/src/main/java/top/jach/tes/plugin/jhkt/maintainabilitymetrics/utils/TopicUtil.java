package top.jach.tes.plugin.jhkt.maintainabilitymetrics.utils;

import sun.nio.cs.ext.MacIceland;
import top.jach.tes.plugin.jhkt.microservice.Microservice;
import top.jach.tes.plugin.jhkt.microservice.MicroservicesInfo;
import top.jach.tes.plugin.jhkt.microservice.OneOf;
import top.jach.tes.plugin.jhkt.microservice.TopicStruct;
import top.jach.tes.plugin.tes.code.microservice.Topic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2020/10/22
 * @description: 关于Topic的一些方法
 */
public class TopicUtil {
    public static Map<String, Integer> getAllTopic(MicroservicesInfo microservicesInfo){
        // Map<topic名称， topic数据结构>
        Map<String, Integer> topics = new HashMap();
        for (Microservice microservice : microservicesInfo.getMicroservices()){
            for (TopicStruct topicStruct : microservice.getStructs()){
                int tmp = 0;
                for (OneOf oneOf: topicStruct.getOneofs()){
                    tmp += oneOf.getParams().size();
                }
                topics.put(topicStruct.getTopicName(),
                        topicStruct.getParams().size() + tmp);
            }
        }
        return topics;
    }
}
