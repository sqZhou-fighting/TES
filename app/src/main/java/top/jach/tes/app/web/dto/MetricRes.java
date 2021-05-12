package top.jach.tes.app.web.dto;

import lombok.*;

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
public class MetricRes {
    private String category;
    private String metric_short;
    private String metric_name;
    private Double index;
}
