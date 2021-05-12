package top.jach.tes.app.web.service;

import top.jach.tes.app.web.dto.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2021/3/29
 * @description: 可维护性部分的一些接口
 */
public interface MaintainService {
    // 项目列表
    List<Project> findAllProject();

    // 单个微服务的所有度量结果
    MSMetricRes findMSAllMetricsRes(String ms_name);

    // 单个指标的所有微服务度量结果
    MetricMSResPlus findMetricAllMSRes(String metric_short);

    // 单个指标单个微服务多个版本的数值
    List<MMRes> findMulMSMetricRes(String ms_name, String metric_name);

    void export(String name);
}
