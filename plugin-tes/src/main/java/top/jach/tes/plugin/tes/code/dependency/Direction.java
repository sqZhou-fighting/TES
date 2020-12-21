package top.jach.tes.plugin.tes.code.dependency;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @Author: zhoushiqi
 * @date: 2020/8/27
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Direction {
    private String src_cs;
    private String dest_cs;
    private String topic;
    private String oneof;
    private String version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Direction direction = (Direction) o;

        return new EqualsBuilder()
                .append(src_cs, direction.getSrc_cs())
                .append(dest_cs, direction.getDest_cs())
                .append(topic, direction.getTopic())
                .append(oneof, direction.getOneof())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(src_cs)
                .append(dest_cs)
                .append(topic)
                .append(oneof)
                .toHashCode();
    }
}
