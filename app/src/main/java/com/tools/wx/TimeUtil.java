package com.tools.wx;

import java.text.SimpleDateFormat;
import java.util.Date;

/***
 *   created by android on 2019/8/19
 */
public class TimeUtil {
    public static String timeFormat(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(time));
    }
}
