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
public class Project {
    String pro_name;
    String description;
    Long create_time;
    Long update_time;
}
