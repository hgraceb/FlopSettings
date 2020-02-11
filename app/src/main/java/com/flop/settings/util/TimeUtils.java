package com.flop.settings.util;

/**
 * 时间工具类
 * <p>
 * Created by Flop on 2020/2/10.
 */
public class TimeUtils {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 60 * 60;
    private static final int SECONDS_PER_DAY = 24 * 60 * 60;

    /**
     * 格式化毫秒
     *
     * @param millisecond 毫秒
     * @return 天-时-分-秒-毫秒 格式
     */
    public static String formatMillis(long millisecond) {
        if (millisecond <= 0) {
            return millisecond + "毫秒";
        }

        int millis = (int) (millisecond % 1000);
        int seconds = (int) Math.floor(millisecond / 1000);
        int days = 0, hours = 0, minutes = 0;

        if (seconds > SECONDS_PER_DAY) {
            days = seconds / SECONDS_PER_DAY;
            seconds -= days * SECONDS_PER_DAY;
        }
        if (seconds > SECONDS_PER_HOUR) {
            hours = seconds / SECONDS_PER_HOUR;
            seconds -= hours * SECONDS_PER_HOUR;
        }
        if (seconds > SECONDS_PER_MINUTE) {
            minutes = seconds / SECONDS_PER_MINUTE;
            seconds -= minutes * SECONDS_PER_MINUTE;
        }

        return (days == 0 ? "" : days + "天") +
                (days == 0 && hours == 0 ? "" : hours + "时") +
                (days == 0 && hours == 0 && minutes == 0 ? "" : minutes + "分") +
                (days == 0 && hours == 0 && minutes == 0 && seconds == 0 ? "" : seconds + "秒") +
                (millis == 0 ? "" : millis + "毫秒");
    }
}
