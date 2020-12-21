package top.jach.tes.app.jhkt.zhoushiqi.maintainres;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhoushiqi
 * @date: 2020/12/11
 * @description: 暂存可维护性数据
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmpData {
    private Double CommitOverlapRatio = 0.0;
    private Double CommitFilesetOverlapRatio = 0.0;
    private Double PairwiseCommitterOverlap = 0.0;
}
