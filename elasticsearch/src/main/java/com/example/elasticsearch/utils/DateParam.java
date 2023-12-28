package com.example.elasticsearch.utils;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wsj
 * @description 时间操作传参
 * @date 2023/11/16
 */

@Data
@Builder
public class DateParam {
    private LocalDateTime dateTime;
    private Integer number;
}
