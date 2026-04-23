package com.xuxi.learningspringaiexample.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 增强版日期时间工具类
 * 用于演示ToolCallAdvisor的功能
 * 
 * 这个工具类提供了多个工具方法，包括：
 * 1. 获取当前日期时间
 * 2. 设置闹钟
 * 3. 计算两个日期之间的天数
 * 4. 格式化日期
 * 
 * @author xuxi
 */
@Component
public class EnhancedDateTimeTools {

    /**
     * 获取用户时区的当前日期和时间
     * 
     * @return 当前日期时间字符串
     */
    @Tool(description = "Get the current date and time in the user's timezone. Returns the current datetime in ISO-8601 format.",returnDirect = true)
    public String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String result = now.atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
        System.out.println("[Tool Called] getCurrentDateTime -> " + result);
        return result;
    }

    /**
     * 为指定时间设置闹钟
     * 
     * @param time ISO-8601格式的时间
     */
    @Tool(description = "Set a user alarm for the given time. The time should be provided in ISO-8601 format (e.g., 2024-01-15T10:30:00)")
    public void setAlarm(@ToolParam(description = "Alarm time in ISO-8601 format") String time) {
        try {
            LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
            System.out.println("[Tool Called] setAlarm -> Alarm set for " + alarmTime);
            // 在实际应用中，这里可以集成真正的闹钟服务
        } catch (Exception e) {
            System.err.println("[Tool Error] setAlarm -> Invalid time format: " + time);
            throw new IllegalArgumentException("时间格式无效，请使用ISO-8601格式，例如：2024-01-15T10:30:00");
        }
    }

    /**
     * 计算两个日期之间的天数差
     * 
     * @param startDate 开始日期（ISO-8601格式）
     * @param endDate 结束日期（ISO-8601格式）
     * @return 两个日期之间的天数
     */
    @Tool(description = "Calculate the number of days between two dates. Both dates should be in ISO-8601 format.")
    public long daysBetweenDates(
            @ToolParam(description = "Start date in ISO-8601 format") String startDate,
            @ToolParam(description = "End date in ISO-8601 format") String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);
            long days = java.time.Duration.between(start, end).toDays();
            System.out.println("[Tool Called] daysBetweenDates -> " + days + " days between " + startDate + " and " + endDate);
            return Math.abs(days);
        } catch (Exception e) {
            System.err.println("[Tool Error] daysBetweenDates -> Invalid date format");
            throw new IllegalArgumentException("日期格式无效，请使用ISO-8601格式");
        }
    }

    /**
     * 将日期格式化为指定格式
     * 
     * @param date ISO-8601格式的日期
     * @param format 目标格式（例如：yyyy-MM-dd, dd/MM/yyyy, EEEE MMMM dd, yyyy）
     * @return 格式化后的日期字符串
     */
    @Tool(description = "Format a date from ISO-8601 format to a specified format pattern.")
    public String formatDate(
            @ToolParam(description = "Date in ISO-8601 format") String date,
            @ToolParam(description = "Target format pattern (e.g., yyyy-MM-dd, dd/MM/yyyy, EEEE MMMM dd, yyyy)") String format) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
            String formatted = dateTime.format(DateTimeFormatter.ofPattern(format));
            System.out.println("[Tool Called] formatDate -> " + date + " formatted to " + format + " = " + formatted);
            return formatted;
        } catch (Exception e) {
            System.err.println("[Tool Error] formatDate -> Invalid date or format");
            throw new IllegalArgumentException("日期或格式无效");
        }
    }

    /**
     * 获取当前时间戳（秒）
     * 
     * @return 当前时间戳
     */
    @Tool(description = "Get the current timestamp in seconds since epoch (January 1, 1970)")
    public long getCurrentTimestamp() {
        long timestamp = System.currentTimeMillis() / 1000;
        System.out.println("[Tool Called] getCurrentTimestamp -> " + timestamp);
        return timestamp;
    }

    /**
     * 将时间戳转换为可读的日期时间
     * 
     * @param timestamp 时间戳（秒）
     * @return 可读的日期时间字符串
     */
    @Tool(description = "Convert a timestamp (in seconds since epoch) to a human-readable date-time string")
    public String timestampToDateTime(@ToolParam(description = "Timestamp in seconds since epoch") long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timestamp, 0, 
                LocaleContextHolder.getTimeZone().toZoneId().getRules().getOffset(java.time.Instant.now()));
        String result = dateTime.toString();
        System.out.println("[Tool Called] timestampToDateTime -> " + timestamp + " = " + result);
        return result;
    }
}
