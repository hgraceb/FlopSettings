package com.flop.settings.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.flop.settings.R;
import com.flop.settings.base.BaseActivity;
import com.flop.settings.util.EdgeUtil;
import com.flop.settings.util.SettingUtil;
import com.flop.settings.util.ThemeHelper;
import com.flop.settings.util.TimeUtils;

public class MainActivity extends BaseActivity {

    private static final String TAG = "FLOP";

    private static AppCompatActivity mActivity;

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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_root, rootKey);

            // 初始化设置页面
            init();
        }

        /**
         * 初始化设置页面
         */
        private void init() {
            // 设置“自动休眠”设置项的标题
            setDormantTitle();
            // 初始化“自动休眠”的设置
            SeekBarPreference prefDormant = findPreference(getString(R.string.prefs_dormant_key));
            if (prefDormant != null) {
                prefDormant.setOnPreferenceClickListener(preference -> {
                    // 创建并显示设置“自动休眠”的对话框
                    showDormantDialog();

                    // 返回 true 屏蔽原生事件
                    return true;
                });

                prefDormant.setOnPreferenceChangeListener((preference, newValue) -> {
                    // 设置自动休眠时长
                    SettingUtil.setDormant((int) newValue);
                    // 更新“自动休眠”设置的标题
                    setDormantTitle();
                    // 返回 true 调用原生事件
                    return true;
                });
            }

            // 初始化“调整亮度”的设置
            SeekBarPreference prefBrightness = findPreference(getString(R.string.prefs_brightness_key));
            if (prefBrightness != null) {
                // 更新亮度设置
                prefBrightness.setOnPreferenceChangeListener((preference, newValue) -> {
                    // 设置亮度
                    SettingUtil.setScreenBrightness((int) newValue);
                    // 返回 true 调用原生事件
                    return true;
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
         * 设置“自动休眠”设置项的标题
         */
        private void setDormantTitle() {
            SeekBarPreference prefDormantFormat = findPreference(getString(R.string.prefs_dormant_key));
            if (prefDormantFormat != null) {
                prefDormantFormat.setTitle(getString(R.string.prefs_dormant_title)
                        + "(" + TimeUtils.formatMillis((long) SettingUtil.getDormant()) + ")");
            }
        }

        /**
         * 创建并显示设置“自动休眠”的对话框
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
                                // 最小值
                                int min = getResources().getInteger(R.integer.prefs_dormant_min);
                                // 最大值
                                int max = getResources().getInteger(R.integer.prefs_dormant_max);
                                // 如果输入值小于最小值则使用最小值
                                SettingUtil.setDormant(input <= min ? min : input > max ? max : input);
                                prefDormant.setValue(SettingUtil.getDormant());
                            }
                            // 更新“自动休眠”设置的标题
                            setDormantTitle();
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
            }

            // 在内容设置完成后获取焦点，保证指针在最后面
            editText.requestFocus();
            // 添加 editText 布局
            linearLayout.addView(editText);
            return linearLayout;
        }
    }
}