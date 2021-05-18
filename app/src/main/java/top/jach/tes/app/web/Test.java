package top.jach.tes.app.web;

import top.jach.tes.app.web.dto.MSMetricRes;
import top.jach.tes.app.web.dto.MetricMSRes;
import top.jach.tes.app.web.dto.Project;
import top.jach.tes.app.web.service.impl.MaintainServiceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2021/3/29
 * @description:
 */
public class Test {
    public static void main(String[] args) {
        MaintainServiceImpl mm = new MaintainServiceImpl();
//        MSMetricRes microservice = mm.findMSAllMetricsRes("x_25");
//        MetricMSRes metricMSRes = mm.findMetricAllMSRes("NDCS");
//        List<Project> pros = mm.findAllProject();
//        Map<String, Double> res = mm.findMulMSMetricRes("x_25", "NO");
//        System.out.println("bp");
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date(1607672558279L)));

    }
}
