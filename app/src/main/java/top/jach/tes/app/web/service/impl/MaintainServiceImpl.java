package top.jach.tes.app.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.springframework.stereotype.Service;
import top.jach.tes.app.web.AppApplication;
import top.jach.tes.app.web.dto.*;
import top.jach.tes.app.web.service.MaintainService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            pro.setCreate_time(Long.parseLong(doc.getString("createdTime")));
            pro.setUpdate_time(Long.parseLong(doc.getString("updatedTime")));
            projects.add(pro);
        }
        return projects;
    }

    @Override
    public Microservice findMSAllMetricsRes(String ms_name) {
        Microservice microservice = new Microservice();
        microservice.setMs_name(ms_name);
        List<MSMetricRes> msMetricResList = new ArrayList<>();
        MongoCollection<Document> collection = AppApplication.generalInfoCollection();
        FindIterable<Document> documents = collection.find(
                Filters.and(Filters.eq("name","MSMetricRes"),
                        Filters.eq("version","x_1635-x_95d.x_4af.x_893.x_ec8b_x_1ff_x_2922d"))
                ).limit(1).projection(Projections.include("name", "msMetricRess", "createdTime", "version"));
        List<Microservice> microservices = new ArrayList<>();
        for (Document doc : documents) {
            Microservice ms = new Microservice();
            JSONObject jo = (JSONObject) doc.get("msMetricRess");
            List<MSMetricRes> msMetricRes = JSON.parseArray(doc.get("MSMetricRess").toString(), MSMetricRes.class);
            System.out.println("bp");
        }
        microservice.setMsMetricRes(msMetricResList);
        return microservice;
    }

    @Override
    public Metric findMetricAllMSRes(String metric_abb) {
        return null;
    }

    @Override
    public List<Double> findMulMSMetricRes(String ms_name, String metric_name) {
        return null;
    }
}
