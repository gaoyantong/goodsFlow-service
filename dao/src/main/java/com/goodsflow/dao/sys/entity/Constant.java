package com.goodsflow.dao.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_constant")
public class Constant extends BaseEntity {
    /** 常量编码 */
    private String code;

    /** 常量名称 */
    private String name;

    /** 父级编码 */
    private String parent;

    /** 常量值 */
    private String vals;

    /** 备注 */
    private String remarks;
}
