package com.goodsflow.dao;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        strictInsertFill(metaObject, "deleted", () -> false, Boolean.class);
        strictInsertFill(metaObject, "createdAt", System::currentTimeMillis, Long.class);
        strictInsertFill(metaObject, "updatedAt", System::currentTimeMillis, Long.class);
        strictInsertFill(metaObject, "sortedNum", () -> 1, Integer.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updatedAt", System::currentTimeMillis, Long.class);
    }
}
