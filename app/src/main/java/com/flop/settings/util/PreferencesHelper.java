package com.flop.settings.util;

import android.content.Context;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import com.flop.settings.R;
import com.flop.settings.base.MyApplication;

/**
 * 偏好设置工具类
 * <p>
 * Created by Flop on 2020/2/2.
 */
public class PreferencesHelper {
    /**
     * 获取是否自动调节休眠时长
     */
    public static boolean isDormantAuto() {
        Context context = MyApplication.getContext();
        return getBooleanFromResources(context, R.string.prefs_dormant_auto_key, R.bool.prefs_dormant_auto_default);
    }

    /**
     * 获取是否自动调节亮度
     */
    public static boolean isBrightnessAuto() {
        Context context = MyApplication.getContext();
        return getBooleanFromResources(context, R.string.prefs_brightness_auto_key, R.bool.prefs_brightness_auto_default);
    }

    public static int getIntFromResources(Context context, int resKeyId, int resDefValueId) {
        return getInt(context, context.getString(resKeyId), context.getString(resDefValueId));
    }

    public static int getInt(Context context, String key, String defValue) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
        return Integer.parseInt(TextUtils.isEmpty(value) ? defValue : value);
    }

    public static boolean getBooleanFromResources(Context context, int resKeyId, int resDefValueId) {
        return getBoolean(context, context.getString(resKeyId), context.getResources().getBoolean(resDefValueId));
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defValue);
    }
}
