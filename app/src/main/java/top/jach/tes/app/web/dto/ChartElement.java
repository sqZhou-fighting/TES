package top.jach.tes.app.web.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

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
public class ChartElement {
    Map<String, String> all = new HashMap<>();
//    Map<String, Double> index = new HashMap<>();
//    Map<String, String> version = new HashMap<>();
}
