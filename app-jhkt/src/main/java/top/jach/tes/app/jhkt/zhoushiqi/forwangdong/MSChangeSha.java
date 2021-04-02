package top.jach.tes.app.jhkt.zhoushiqi.forwangdong;

import lombok.*;

import java.util.List;

/**
 * @Author: zhoushiqi
 * @date: 2021/4/2
 * @description:
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MSChangeSha {
    private String ms_name;
    private List<String> sha_list;
}
