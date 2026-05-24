package com.goodsflow.common.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResData<T> {
    private Integer code;
    private String message;
    private T data;
    private Long total;

    public static <T> ResData<T> success(T data) {
        return new ResData<>(0, "成功", data, null);
    }

    public static <T> ResData<T> success(T data, long total) {
        return new ResData<>(0, "成功", data, total);
    }

    public static ResData<Void> success() {
        return new ResData<>(0, "成功", null, null);
    }

    public static <T> ResData<T> fail(String message) {
        return new ResData<>(1, message, null, null);
    }
}
