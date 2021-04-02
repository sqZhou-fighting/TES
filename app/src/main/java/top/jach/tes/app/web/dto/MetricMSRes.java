package top.jach.tes.app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: zhoushiqi
 * @date: 2021/3/29
 * @description:
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetricMSRes {
    String ms_name;
    Double index;
    String ms_name2;
    Double index2;
};

