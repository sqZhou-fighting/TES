package top.jach.tes.app.web.service.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.springframework.stereotype.Service;
import top.jach.tes.app.web.AppApplication;
import top.jach.tes.app.web.dto.*;
import top.jach.tes.app.web.service.MaintainService;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: zhoushiqi
 * @date: 2021/3/29
 * @description: 实现可维护性界面所需的这些操作
 */

@Service
public class MaintainServiceImpl implements MaintainService {
    @Override
    public List<Project> findAllProject() {
        List<Project> projects = new ArrayList<>();
        MongoCollection<Document> collection = AppApplication.generalInfoCollection();
        FindIterable<Document> documents = collection.find(
                Filters.eq("name","TES_INPUT_INFO"))
                .projection(Projections.include("name", "status", "createdTime", "updatedTime"));
        for (Document doc : documents) {
            Project pro = new Project();
            pro.setPro_name(doc.getString("name"));
            pro.setDescription(doc.getString("status"));
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pro.setCreate_time(sdf.format(new Date(doc.getLong("createdTime") * 1000)));
            pro.setUpdate_time(sdf.format(new Date(doc.getLong("updatedTime")* 1000)));
            projects.add(pro);
        }
        return projects;
    }

    @Override
    public MSMetricRes findMSAllMetricsRes(String ms_name) {
        MSMetricRes ms = new MSMetricRes();
        MongoCollection<Document> collection = AppApplication.generalInfoCollection();
        FindIterable<Document> documents = collection.find(
                Filters.and(Filters.eq("name","MSMetricRes"),
                        Filters.eq("version","x_1635-x_95d.x_4af.x_893.x_ec8b_x_1ff_x_2922d"))
                ).limit(1).projection(Projections.include("name", "msMetricRess", "createdTime", "version"));
        for (Document doc : documents) {
            for (Object ob_ms : (ArrayList)doc.get("msMetricRess")){
                Document doc_ms = (Document) ob_ms;
                if (doc_ms.getString("ms_name").equals(ms_name)){
                    ms.setMs_name(ms_name);
                    ms.setVersion(doc_ms.getString("version"));
                    List<MetricRes> metricResList = new ArrayList<>();
                    for (Object ob_metricsRes : (ArrayList)doc_ms.get("metricsRes")){
                        Document doc_metric = (Document) ob_metricsRes;
                        MetricRes metricRes = new MetricRes();
                        metricRes.setCategory(doc_metric.getString("category"));
                        metricRes.setMetric_name(doc_metric.getString("metric_name"));
                        metricRes.setMetric_short(doc_metric.getString("metric_short"));
                        metricRes.setIndex(doc_metric.getDouble("index"));
                        metricResList.add(metricRes);
                    }
                    ms.setMetricsRes(metricResList);
                }
            }
        }
        return ms;
    }

    @Override
    public MetricMSResPlus findMetricAllMSRes(String metric_short) {
        MetricMSResPlus metricMSRes = new MetricMSResPlus();
        MongoCollection<Document> collection = AppApplication.generalInfoCollection();
        FindIterable<Document> documents = collection.find(
                Filters.and(Filters.eq("name","MetricMSRes"),
                        Filters.eq("version","x_1635-x_95d.x_4af.x_893.x_ec8b_x_1ff_x_2922d"))
        ).limit(1).projection(Projections.include("name", "metricMSRess", "createdTime", "version"));
        for (Document doc : documents){
            for (Object ob_metric : (ArrayList)doc.get("metricMSRess")){
                Document doc_metric = (Document) ob_metric;
                if (metric_short.equals(doc_metric.getString("metric_short"))){
                    metricMSRes.setMetric_short(metric_short);
                    metricMSRes.setVersion(doc_metric.getString("version"));
                    List<MSRes> msResList = new ArrayList<>();
                    List<DoubleMSRes> msResList1 = new ArrayList<>();
                    for (Object ob_ms : (ArrayList)((Document) ob_metric).get("msRess")){
                        Document doc_ms = (Document) ob_ms;
                        MSRes msRes = new MSRes();
                        msRes.setMs_name(doc_ms.getString("ms_name"));
                        msRes.setIndex(doc_ms.getDouble("index"));
                        msResList.add(msRes);
                    }
                    int i=0;
                    while (i<msResList.size()-1){
                        msResList1.add(new DoubleMSRes(msResList.get(i).getMs_name(),
                                msResList.get(i).getIndex(),
                                msResList.get(i+1).getMs_name(),
                                msResList.get(i+1).getIndex()));
                        i += 2;
                    }
                    if (i == msResList.size()-1){
                        DoubleMSRes doubleMSRes = new DoubleMSRes();
                        doubleMSRes.setMs_name(msResList.get(i).getMs_name());
                        doubleMSRes.setIndex(msResList.get(i).getIndex());
                        msResList1.add(doubleMSRes);
                    }
                    metricMSRes.setMsRess(msResList1);
                }
            }
        }
        return metricMSRes;
    }

    @Override
    public List<MMRes> findMulMSMetricRes(String ms_name, String metric_short) {
        MulMSMetric item = new MulMSMetric();
        MongoCollection<Document> collection = AppApplication.generalInfoCollection();
        FindIterable<Document> documents = collection.find(
                Filters.and(Filters.eq("name","MulMSMetric"),
                        Filters.eq("version","all"))
        ).projection(Projections.include("name", "mulMSMetricList", "createdTime", "version"));
        for (Document doc : documents){
            for (Object ob_mulMM : (ArrayList)doc.get("mulMSMetricList")){
                Document doc_mulMM = (Document) ob_mulMM;
                if (doc_mulMM.getString("metric_short").equals(metric_short) &&
                        doc_mulMM.getString("ms_name").equals(ms_name)){
                    item.setMetric_short(metric_short);
                    item.setMs_name(ms_name);
                    item.setMetric_name(doc_mulMM.getString("metric_name"));
                    Map<String, Double> v_i = new HashMap<>();
                    Document doc_ = (Document) doc_mulMM.get("version_indexs");
                    String[] versions = {"_e0a1", "_e0a7", "29229", "2922b", "_e0ab", "2922d", "_e0b1"};
                    String[] ver = {"V1", "V2", "V3", "V4", "V5", "V6", "V7"};
                    for (int i=0; i<7; i++){
                        if (doc_.get(versions[i]) != null){
                            v_i.put(ver[i], doc_.getDouble(versions[i]));
                        }
                    }
                    Map new_v_i = sortMapByKey(v_i);
                    item.setVersion_indexs(new_v_i);
                }
            }
        }
        List<MMRes> res = new ArrayList<>();
        for (Map.Entry<String, Double> entry: item.getVersion_indexs().entrySet()){
            MMRes mmRes = new MMRes(ms_name, entry.getKey(), entry.getValue());
            res.add(mmRes);
        }
        return res;
    }

    @Override
    public void export(String name) {
        
    }

    public static Map<String, Double> sortMapByKey(Map<String, Double> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, Double> sortMap = new TreeMap<String, Double>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }
}

class MapKeyComparator implements Comparator<String>{

    @Override
    public int compare(String str1, String str2) {

        return str1.compareTo(str2);
    }
}
