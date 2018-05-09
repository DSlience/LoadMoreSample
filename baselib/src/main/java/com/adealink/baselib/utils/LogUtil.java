package com.adealink.baselib.utils;

import android.util.Log;

import com.adealink.baselib.constants.Const;

/**
 * 日志打印
 * Created by Xuefu_Du on 2018/5/9.
 */
public class LogUtil {

    public static void e(String tag, String msg) {
        if (Const.PRINT_LOG) {
            Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (Const.PRINT_LOG) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (Const.PRINT_LOG) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (Const.PRINT_LOG) {
            Log.w(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (Const.PRINT_LOG) {
            Log.v(tag, msg);
        }
    }

    public static void println(String tag, String msg) {
        if (Const.PRINT_LOG) {
            System.out.println(tag + "--" + msg);
        }
    }

}
