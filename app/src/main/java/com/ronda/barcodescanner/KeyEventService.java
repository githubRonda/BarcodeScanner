package com.ronda.barcodescanner;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
/*
后台无障碍服务AccessibilityService（也是Service的一种）实现按键监听的功能：
可以一直在后台运行，监听按键功能。

对于扫码输入监听，使用 AccessibilityService 则有点大材小用了。因为我们的扫码功能只在特定的某些页面才有，所以只需要在这些Activity中的按键事件的回调方法中捕获输入的字符即可。
而无障碍服务 AccessibilityService 来实现按键监听，则一般用于机顶盒或者智能电视，也就是AndroidTv。针对遥控器某些特殊按键，实现按键的监听，并实现相应的功能。 当然 AccessibilityService 的功能很强大，远不止按键监听这一项。

参考：http://blog.csdn.net/w815878564/article/details/53331086

启动：AccessibilityService 的方法：（要注意：用户应用的话，下面这种方式是启动不了的，只用系统级的应用才可以用下面这种方式启动。若系统是自己开发的话，可以直接把签名应用(要和系统的签名是一样的)放到/system/framework/目录下即可成为系统级应用）
用户级应用只用这样启动：设置 --> 辅助功能 --> 选择服务进行开启/关闭

private void startKeyEventService(){
    //注意 这里可能为空（也就是如果当前没有任何一个无障碍服务被授权的时候 就为空了 ）
    String enabledServicesSetting = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

    ComponentName selfComponentName = new ComponentName(getPackageName(), "com.ronda.barcodescanner.KeyEventService");
    String flattenToString = selfComponentName.flattenToString();
    if (enabledServicesSetting == null || !enabledServicesSetting.contains(flattenToString)) {
        enabledServicesSetting += flattenToString;
    }
    Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, enabledServicesSetting);
    Settings.Secure.putInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 1);
}

*/

public class KeyEventService extends AccessibilityService {
    private static final String TAG = KeyEventService.class.getSimpleName();

    public KeyEventService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i("Liu", "onAccessibilityEvent --> " + event);

    }

    @Override
    public void onInterrupt() {
        Log.i("Liu", "onInterrupt");
    }

    /**
     * 复写这个方法可以捕获按键事件
     *
     * @param event
     * @return
     */
    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        Log.w(TAG, "keyEvent:" + event + "keyCode: " + keyCode + "char: " + KeyEvent.keyCodeToString(keyCode));

        return super.onKeyEvent(event);
    }
}
