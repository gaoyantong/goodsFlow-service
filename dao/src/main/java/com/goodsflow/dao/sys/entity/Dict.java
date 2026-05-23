package com.goodsflow.dao.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict")
public class Dict extends BaseEntity {
    /** 字典编码 */
    private String code;

    /** 英文名称 */
    private String nameEnus;

    /** 简体中文名称 */
    private String nameZhcn;

    /** 繁体中文名称 */
    private String nameZhtw;

    /** 父级编码 */
    private String parent;

    /** 字典值 */
    private String vals;

    /** 备注 */
    private String remarks;
}
