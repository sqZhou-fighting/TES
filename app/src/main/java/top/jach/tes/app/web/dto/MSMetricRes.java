package top.jach.tes.app.web.dto;

import lombok.*;

import java.util.List;

/**
 * @Author: zhoushiqi
 * @date: 2021/3/29
 * @description:
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MSMetricRes
{
    private String ms_name;
    private String version;
    private List<MetricRes> metricsRes;
}
