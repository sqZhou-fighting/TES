package top.jach.tes.plugin.jhkt.multimsmetric;

import lombok.*;
import org.eclipse.jgit.internal.storage.file.LazyObjectIdSetFile;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhoushiqi
 * @date: 2021/4/6
 * @description:
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MulMSMetric {
    private String ms_name;
    private String metric_name;
    private String metric_short;
    private Map<String, Double> version_indexs;

}
