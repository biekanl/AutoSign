package cn.unbug.autosign.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @ProjectName: autosign
 * @Package: cn.unbug.autosign.config
 * @ClassName: DateUtil
 * @Author: Administrator
 * @Description: []
 * @Date: 2023/6/7 21:49
 */
public class DateUtil {

    /**
     * 获取当前日期是星期几<br>
     *
     * @param date
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDays = {"0", "1", "2", "3", "4", "5", "6"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }


}
