package com.goodsflow.dao.flow.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("gf_flow_task_store")
public class FlowTaskStore implements Serializable {
    /** 主键ID */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /** 数据录入任务ID */
    private String taskId;

    /** 门店ID */
    private String storeId;

    /** 门店名称快照 */
    private String storeName;

    /** 创建时间，毫秒时间戳 */
    @TableField(fill = FieldFill.INSERT)
    private Long createdAt;

    /** 更新时间，毫秒时间戳 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedAt;
}
