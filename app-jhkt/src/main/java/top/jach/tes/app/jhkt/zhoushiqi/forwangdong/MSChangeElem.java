package top.jach.tes.app.jhkt.zhoushiqi.forwangdong;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class MSChangeElem {
    private String ms_name;
    private Map<String, List<String>> shaDifffFiles = new HashMap<>();
}
