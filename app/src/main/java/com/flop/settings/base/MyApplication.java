package com.flop.settings.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.flop.settings.R;
import com.flop.settings.util.ThemeHelper;

/**
 * Created by Flop on 2020/2/10.
 */
public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    public static volatile Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        // 切换主题
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean(getString(R.string.prefs_dark_key), false)) {
            ThemeHelper.applyTheme(ThemeHelper.DARK_MODE);
        }

        // 保存全局上下文
        if (mContext == null) {
            mContext = getApplicationContext();
        }
    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        return mContext;
    }
}
