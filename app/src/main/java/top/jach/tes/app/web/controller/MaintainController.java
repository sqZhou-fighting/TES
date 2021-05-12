package top.jach.tes.app.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.jach.tes.app.web.dto.*;
import top.jach.tes.app.web.service.MaintainService;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2021/3/29
 * @description:
 */
@RestController
@RequestMapping("/api/ms")
public class MaintainController {

    @Autowired
    MaintainService maintainService;

    @GetMapping(value = "/pros")
    public ResponseEntity<List<Project>> allPros() {
        System.out.println("-----pros-----");
        return ResponseEntity.ok(maintainService.findAllProject());
    }

    @PostMapping(value = "/ms_metrics")
    public ResponseEntity<MSMetricRes> msMetricsRes(@RequestParam String ms_name){
        System.out.println("-----ms_metrics-----");
        return ResponseEntity.ok(maintainService.findMSAllMetricsRes(ms_name));
    }

    @PostMapping(value = "/metric_mss")
    public ResponseEntity<MetricMSResPlus> metricMSsRes(@RequestParam String metric_short){
        System.out.println("-----metric_mss-----");
        return ResponseEntity.ok(maintainService.findMetricAllMSRes(metric_short));
    }

    @PostMapping(value = "/m_m")
    public ResponseEntity<List<MMRes>> m_m(@RequestParam String ms_name, String metric_short){
        System.out.println("-----m_m-----");
        return ResponseEntity.ok(maintainService.findMulMSMetricRes(ms_name, metric_short));
    }

    @GetMapping(value = "/export")
    public ResponseEntity<MSMetricRes> export(@RequestParam String ms_name){
        return ResponseEntity.ok(maintainService.findMSAllMetricsRes(ms_name));
    }

}
