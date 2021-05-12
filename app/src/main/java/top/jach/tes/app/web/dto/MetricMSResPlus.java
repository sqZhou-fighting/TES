package top.jach.tes.app.web.dto;

import lombok.*;

import java.util.List;

/**
 * @Author: zhoushiqi
 * @date: 2021/4/18
 * @description:
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MetricMSResPlus {
    private String metric_short;
    private String version;
    private List<DoubleMSRes> msRess;
}
