package com.flop.settings.util;

import android.net.Uri;
import android.provider.Settings;

import com.flop.settings.base.MyApplication;

/**
 * Created by Flop on 2020/2/10.
 */
public class SettingUtil {

    /**
     * 获取系统休眠时间
     */
    public static int getDormant() {
        int result = 0;
        try {
            result = Settings.System.getInt(MyApplication.getContext().getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置系统的休眠时间
     */
    public static void setDormant(int time) {
        Settings.System.putInt(MyApplication.getContext().getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, time);
        Uri uri = Settings.System
                .getUriFor(Settings.System.SCREEN_OFF_TIMEOUT);
        MyApplication.getContext().getContentResolver().notifyChange(uri, null);
    }

    /**
     * 获得当前屏幕亮度的模式
     *
     * @return 1 为自动调节屏幕亮度,0 为手动调节屏幕亮度,-1 获取失败
     */
    public static int getScreenMode() {
        int mode = -1;
        try {
            mode = Settings.System.getInt(MyApplication.getContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return mode;
    }

    /**
     * 获得当前屏幕亮度值
     *
     * @return 0--255
     */
    public static int getScreenBrightness() {
        int screenBrightness = -1;
        try {
            screenBrightness = Settings.System.getInt(MyApplication.getContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return screenBrightness;
    }

    /**
     * 设置当前屏幕亮度的模式
     *
     * @param mode 1 为自动调节屏幕亮度,0 为手动调节屏幕亮度
     */
    public static void setScreenMode(int mode) {
        try {
            Settings.System.putInt(MyApplication.getContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
            Uri uri = Settings.System
                    .getUriFor("screen_brightness_mode");
            MyApplication.getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存当前的屏幕亮度值，并使之生效
     *
     * @param paramInt 0-255
     */
    public static void setScreenBrightness(int paramInt) {
        Settings.System.putInt(MyApplication.getContext().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, paramInt);
        Uri uri = Settings.System
                .getUriFor("screen_brightness");
        MyApplication.getContext().getContentResolver().notifyChange(uri, null);
    }

}
