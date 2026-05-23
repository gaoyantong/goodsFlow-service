package com.goodsflow.dao;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseEntity implements Serializable {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    private Long createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedAt;

    @TableField(fill = FieldFill.INSERT)
    private Integer sortedNum;
}
