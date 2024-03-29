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
@Data
public class MetricMSRes {
    private String metric_short;
    private String version;
    private List<MSRes> msRess;
}
