package com.example.elasticsearch.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author wsj
 * @description 时间工具类
 * @date 2023/11/16
 */
public class DateTimeUtils {

    private static final String[] PARSE_PATTERNS = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "MM-dd HH:mm", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",};

    private static final Map<String, Function<DateParam,Date>> FUNCTION_MAP = new HashMap<String, Function<DateParam,Date>>()
    {{
        put("day",DateTimeUtils::minusDay);
        put("month",DateTimeUtils::minusMonth);
        put("year",DateTimeUtils::minusYear);
    }};
    private  static  final ZoneId ZONE_ID = ZoneId.systemDefault();
    public static String getDateStr(Date date, int index) {
        if (date == null) {
            date = new Date();
        }
        return DateFormatUtils.format(date,PARSE_PATTERNS[index]);
    }

    public static  Date calcDate(Date date, int number, String unit) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateParam param = DateParam.builder().dateTime(localDateTime).number(number).build();
        return FUNCTION_MAP.get(unit).apply(param);
    }

    private static Date minusMonth(DateParam param) {
        LocalDateTime resultDate = param.getDateTime().minusMonths(param.getNumber());
        return Date.from(resultDate.atZone(ZONE_ID).toInstant());
    }

    private static Date minusYear(DateParam param) {
        LocalDateTime resultDate = param.getDateTime().minusYears(param.getNumber());
        return Date.from(resultDate.atZone(ZONE_ID).toInstant());
    }

    private static Date minusDay(DateParam param) {
        LocalDateTime resultDate = param.getDateTime().minusDays(param.getNumber());
        return Date.from(resultDate.atZone(ZONE_ID).toInstant());
    }
}
