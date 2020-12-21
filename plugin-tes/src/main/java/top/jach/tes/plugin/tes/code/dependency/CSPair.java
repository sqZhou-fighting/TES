package top.jach.tes.plugin.tes.code.dependency;

import lombok.*;

/**
 * @Author: zhoushiqi
 * @date: 2020/8/27
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CSPair {
    private Direction req;
    private Direction rsp;
}
