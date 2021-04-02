package top.jach.tes.app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class Microservice {
    String ms_name;
    String version;
    List<MSMetricRes> msMetricRes;
}
