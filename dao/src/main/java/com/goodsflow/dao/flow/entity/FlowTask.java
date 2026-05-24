package com.goodsflow.dao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gf_flow_task")
public class FlowTask extends BaseEntity {
    /** 任务编号 */
    private String taskNo;

    /** 货品ID，关联货品资料 */
    @NotBlank(message = "请选择货品")
    private String goodsId;

    /** 待配送数量 */
    @NotNull(message = "请输入待配送数量")
    @Min(value = 1, message = "待配送数量必须大于0")
    private Integer pendingDeliveryQty;

    /** 配送开始日期 */
    @NotNull(message = "请选择配送开始日期")
    private LocalDate deliveryStartDate;

    /** 配送截止日期 */
    @NotNull(message = "请选择配送截止日期")
    private LocalDate deliveryEndDate;

    /** 单笔零售最大数量 */
    @NotNull(message = "请输入单笔零售最大数量")
    @Min(value = 1, message = "单笔零售最大数量必须大于0")
    private Integer maxRetailQtyPerOrder;

    /** 生成零售天数 */
    @NotNull(message = "请输入生成零售天数")
    @Min(value = 1, message = "生成零售天数必须大于0")
    private Integer retailDays;

    /** 批号 */
    @NotBlank(message = "请输入批号")
    private String batchNo;

    /** 有效期 */
    @NotNull(message = "请选择有效期")
    private LocalDate expiryDate;

    /** 门店范围类型，ALL=全部门店，SELECTED=指定门店 */
    private String storeScopeType;

    /** 任务状态，PENDING=待处理，GENERATED=已生成 */
    private String status;

    /** 生成时间，毫秒时间戳 */
    private Long generatedAt;

    /** 备注 */
    private String remark;
}
