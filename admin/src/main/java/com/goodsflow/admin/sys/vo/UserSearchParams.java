package com.goodsflow.admin.sys.vo;

import com.goodsflow.dao.PageParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserSearchParams extends PageParams {
    private String name;
    private String loginName;
    private String workNum;
}
