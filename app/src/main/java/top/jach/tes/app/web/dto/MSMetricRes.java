package top.jach.tes.app.web.dto;

import lombok.*;

/**
 * @Author: zhoushiqi
 * @date: 2021/3/29
 * @description: 度量结果
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MSMetricRes {
    String category;
    String metric_short;
    String metric_name;
    Double index;
}
