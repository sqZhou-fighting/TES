package top.jach.tes.plugin.jhkt.msmetricsres;

import lombok.*;
import org.w3c.dom.stylesheets.MediaList;

import java.util.List;
import java.util.Map;

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
