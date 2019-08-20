package com.tools.wx;

import android.content.Context;

/***
 *   created by zhongrui on 2019/8/20
 */
public class ScreenUtil {
    public static int  getScreenWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    public static int  getScreenHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    public static int  dp2px(Context context,int value){
        return (int) (context.getResources().getDisplayMetrics().density*value);
    }
}
