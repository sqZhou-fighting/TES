package top.jach.tes.app.web.dto;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

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
public class DoubleMSRes {
    private String ms_name;
    private double index;
    private String ms_name_1;
    private double index_1;
}
