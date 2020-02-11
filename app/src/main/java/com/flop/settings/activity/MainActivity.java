package com.flop.settings.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.flop.settings.R;
import com.flop.settings.base.BaseActivity;
import com.flop.settings.util.EdgeUtil;
import com.flop.settings.util.PreferencesHelper;
import com.flop.settings.util.SettingUtil;
import com.flop.settings.util.ThemeHelper;
import com.flop.settings.util.TimeUtils;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MainActivity extends BaseActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, ViewTreeObserver.OnWindowFocusChangeListener {

    private static final String TAG = "FLOP";

    private static AppCompatActivity mActivity;

    private OnWindowFocusChangedListener mOnWindowFocusChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mActivity = this;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        // 设置返回栈事件监听
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            // 如果是栈底，即设置页面的主页面
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                // 设置主页面标题
                setTitle(getString(R.string.title_activity_main));
            }
        });

        // 设置顶部导航栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * 监听窗口焦点变化
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // 回调窗口焦点变化的接口
        mOnWindowFocusChangeListener.onWindowFocusChanged(hasFocus);
    }

    /**
     * 绑定窗口焦点变化的接口
     */
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        // 只有实现了指定接口的 fragment 才可以用于接口绑定
        try {
            mOnWindowFocusChangeListener = (OnWindowFocusChangedListener) fragment;
        } catch (Exception ignored) {
        }
    }

    /**
     * 窗口焦点变化的接口
     */
    public interface OnWindowFocusChangedListener {
        /**
         * @param hasFocus 当前窗口是否获取焦点
         */
        public void onWindowFocusChanged(boolean hasFocus);
    }

    /**
     * 监听设置页面的Fragment跳转
     */
    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // 如果当前页面不是空页面
        if (!getString(R.string.prefs_empty_title).contentEquals(pref.getTitle())) {
            // Fragment跳转后重新设置标题
            setTitle(pref.getTitle());
            // 显示状态栏
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            // 如果是跳转到空页面，则设置标题为空
            setTitle(null);
            // 隐藏状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        return false;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements OnWindowFocusChangedListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_root, rootKey);

            // 初始化设置页面
            init();
        }

        /**
         * 监听窗口焦点变化
         *
         * @param hasFocus 当前窗口是否获取焦点
         */
        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            // 如果当前窗口获得了焦点
            if (hasFocus) {
                // 刷新所有设置项
                refreshPrefs();
            }
        }

        /**
         * 初始化设置页面
         */
        private void init() {
            // 初始化“自动休眠”的设置
            SeekBarPreference prefDormant = findPreference(getString(R.string.prefs_dormant_key));
            if (prefDormant != null) {
                // 如果选择自动调节自动休眠时长
                if (PreferencesHelper.isDormantAuto()) {
                    // 设置自动休眠时长为最大值
                    setDormant(getResources().getInteger(R.integer.prefs_dormant_max));
                }

                // 监听拖动条控件的点击事件
                prefDormant.setOnPreferenceClickListener(preference -> {
                    // 创建并显示设置“自动休眠”的对话框
                    showDormantDialog();

                    // 返回 true 屏蔽原生事件
                    return true;
                });

                // 监听拖动条控件的数值变化
                prefDormant.setOnPreferenceChangeListener((preference, newValue) -> {
                    // 设置自动休眠时长
                    setDormant((int) newValue);
                    // 返回 false 屏蔽原生事件
                    return false;
                });
            }

            // 初始化“调节亮度”的设置项
            SeekBarPreference prefBrightness = findPreference(getString(R.string.prefs_brightness_key));
            if (prefBrightness != null) {
                // 如果选择自动调节亮度
                if (PreferencesHelper.isBrightnessAuto()) {
                    // 设置亮度为最小值
                    setBrightness(getResources().getInteger(R.integer.prefs_brightness_min));
                }

                // 监听拖动条控件的点击事件
                prefBrightness.setOnPreferenceClickListener(preference -> {
                    // 创建并显示设置“调节亮度”的对话框
                    showBrightnessDialog();

                    // 返回 true 屏蔽原生事件
                    return true;
                });

                // 监听拖动条控件的数值变化
                prefBrightness.setOnPreferenceChangeListener((preference, newValue) -> {
                    // 设置亮度
                    setBrightness((int) newValue);
                    // 返回 false 屏蔽原生事件
                    return false;
                });
            }

            // 初始化“夜间模式”设置项
            SwitchPreferenceCompat prefDark = findPreference(getString(R.string.prefs_dark_key));
            if (prefDark != null) {
                // 切换夜间模式
                prefDark.setOnPreferenceChangeListener((preference, newValue) -> {
                    // 设置夜间模式
                    if ((boolean) newValue) {
                        ThemeHelper.applyTheme(ThemeHelper.DARK_MODE);
                        // 设置日间模式
                    } else {
                        ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE);
                    }
                    // 返回 true 调用原生事件
                    return true;
                });
            }
        }

        /**
         * 刷新所有设置项
         */
        public void refreshPrefs() {
            // 刷新休眠时间相关设置项
            refreshPrefDormant();
            // 刷新亮度相关设置项
            refreshPrefBrightness();
        }

        /**
         * 刷新休眠时间相关设置项
         */
        private void refreshPrefDormant() {
            setDormant(SettingUtil.getDormant());
        }

        /**
         * 刷新亮度相关设置项
         */
        private void refreshPrefBrightness() {
            setBrightness(SettingUtil.getScreenBrightness());
        }

        /**
         * 设置自动休眠时长
         *
         * @param input 自动休眠时长（毫秒）
         */
        private void setDormant(int input) {
            SeekBarPreference prefDormant = findPreference(getString(R.string.prefs_dormant_key));
            // 最小值
            int min = getResources().getInteger(R.integer.prefs_dormant_min);
            // 最大值
            int max = getResources().getInteger(R.integer.prefs_dormant_max);
            // 判断最终取值
            int value = input <= min ? min : input > max ? max : input;
            if (prefDormant != null) {
                // 如果系统自动休眠时长的当前值和目标值不同
                if (SettingUtil.getDormant() != value) {
                    // 设置系统自动休眠时长
                    SettingUtil.setDormant(value);
                }
                // 如果自动休眠拖动条的当前值和目标值不同
                if (prefDormant.getValue() != value) {
                    // 设置自动休眠拖动条值
                    prefDormant.setValue(value);
                }
                // 设置“自动休眠”设置项的标题
                prefDormant.setTitle(getString(R.string.prefs_dormant_title)
                        + "(" + TimeUtils.formatMillis((long) SettingUtil.getDormant()) + ")");
            }
        }

        /**
         * 设置亮度
         *
         * @param input 亮度（0~1023）
         */
        private void setBrightness(int input) {
            SeekBarPreference prefBrightness = findPreference(getString(R.string.prefs_brightness_key));
            // 最小值
            int min = getResources().getInteger(R.integer.prefs_brightness_min);
            // 最大值
            int max = getResources().getInteger(R.integer.prefs_brightness_max);
            // 判断最终取值
            int value = input <= min ? min : input > max ? max : input;
            if (prefBrightness != null) {
                // 如果系统亮度的当前值和目标值不同
                if (SettingUtil.getScreenBrightness() != value) {
                    // 设置系统亮度
                    SettingUtil.setScreenBrightness(value);
                }
                // 如果亮度拖动条的当前值和目标值不同
                if (prefBrightness.getValue() != value) {
                    // 设置亮度拖动条值
                    prefBrightness.setValue(value);
                }
            }
        }

        /**
         * 创建并显示“自动休眠”设置项的对话框
         */
        private void showDormantDialog() {
            // 创建输入框布局
            LinearLayout linearLayout = buildTextLinearLayout(mActivity, R.string.prefs_dormant_key);

            // 创建对话框
            AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                    .setTitle(getString(R.string.prefs_dormant_title))
                    .setPositiveButton("确定", (dialog, which) -> {
                        SeekBarPreference prefDormant = findPreference(getString(R.string.prefs_dormant_key));
                        EditText editText = linearLayout.findViewById(R.id.settings_dialog_tv);
                        // 更新设置
                        if (prefDormant != null && editText != null) {
                            // 如果自动休眠时间的数值不为空，则设置自动休眠时间
                            if (!TextUtils.isEmpty(editText.getText().toString())) {
                                // 输入值
                                int input = Integer.parseInt(editText.getText().toString());
                                // 如果输入值小于最小值则使用最小值
                                setDormant(input);
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .setView(linearLayout).create();

            // 设置软键盘自动弹出
            Window window = alertDialog.getWindow();
            if (window != null) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            // 显示对话框
            alertDialog.show();
        }

        /**
         * 创建并显示“亮度”设置项的对话框
         */
        private void showBrightnessDialog() {
            // 创建输入框布局
            LinearLayout linearLayout = buildTextLinearLayout(mActivity, R.string.prefs_brightness_key);

            // 创建对话框
            AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                    .setTitle(getString(R.string.prefs_brightness_title))
                    .setPositiveButton("确定", (dialog, which) -> {
                        SeekBarPreference prefBrightness = findPreference(getString(R.string.prefs_brightness_key));
                        EditText editText = linearLayout.findViewById(R.id.settings_dialog_tv);
                        // 更新设置
                        if (prefBrightness != null && editText != null) {
                            // 如果亮度的数值不为空，则设置亮度
                            if (!TextUtils.isEmpty(editText.getText().toString())) {
                                // 输入值
                                int input = Integer.parseInt(editText.getText().toString());
                                // 如果输入值小于最小值则使用最小值
                                setBrightness(input);
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .setView(linearLayout).create();

            // 设置软键盘自动弹出
            Window window = alertDialog.getWindow();
            if (window != null) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            // 显示对话框
            alertDialog.show();
        }

        /**
         * 创建对话框的输入框布局
         *
         * @param context 上下文
         * @param resId   preference对应的的keyTimeUtil
         * @return 只包含一个EditText子控件的LinearLayout布局
         */
        private LinearLayout buildTextLinearLayout(Context context, int resId) {
            // 定义LinearLayout布局作为editText的容器
            LinearLayout linearLayout = new LinearLayout(mActivity);
            // linearLayout的父容器是mActivity中的 FrameLayout
            linearLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            // 定义editText
            EditText editText = new EditText(mActivity);
            // 设置editText的ID
            editText.setId(R.id.settings_dialog_tv);
            // editText的父容器是LinearLayout
            editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            // 设置editText的左右边距
            int verticalMargin = getResources().getDimensionPixelSize(R.dimen.dialog_vertical_margin);
            EdgeUtil.setMarginsLeft(verticalMargin, editText);
            EdgeUtil.setMarginsRight(verticalMargin, editText);

            // 如果是设置“自动休眠”页面的
            if (resId == R.string.prefs_dormant_key) {
                // 获取系统自动休眠时间
                editText.setText(String.valueOf(SettingUtil.getDormant()));
                // 设置输入类型为数字
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                // 设置过滤器，限制输入长度为最大值的长度
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                        String.valueOf(getResources().getInteger(R.integer.prefs_dormant_max)).length())});
            } else if (resId == R.string.prefs_brightness_key) {
                // 获取系统自动休眠时间
                editText.setText(String.valueOf(SettingUtil.getScreenBrightness()));
                // 设置输入类型为数字
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                // 设置过滤器，限制输入长度为最大值的长度
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                        String.valueOf(getResources().getInteger(R.integer.prefs_brightness_max)).length())});
            }

            // 在内容设置完成后获取焦点，保证指针在最后面
            editText.requestFocus();
            // 添加 editText 布局
            linearLayout.addView(editText);
            return linearLayout;
        }
    }

    public static class EmptyFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_empty, rootKey);
        }
    }
}