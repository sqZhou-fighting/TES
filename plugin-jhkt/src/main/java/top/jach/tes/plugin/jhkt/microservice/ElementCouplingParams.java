package top.jach.tes.plugin.jhkt.microservice;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @Author: zhoushiqi
 * @date: 2020/10/21
 * @description: 短暂地存放一下元素耦合的结果
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ElementCouplingParams {
    String pkgName;
    String microserviceName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElementCouplingParams that = (ElementCouplingParams) o;

        return new EqualsBuilder()
                .append(pkgName, that.getPkgName())
                .append(microserviceName, that.getMicroserviceName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(pkgName)
                .append(microserviceName)
                .toHashCode();
    }
}
