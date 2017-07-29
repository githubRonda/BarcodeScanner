package com.ronda.barcodescanner;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/07
 * Version: v1.0
 */

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

/**
 * 扫码枪事件解析类
 * <p>
 * 使用说明：
 * 1. 在Activity中先创建BarcodeScannerResolver对象，并设置扫码成功监听器: setScanSuccessListener() [一般在onCreate()方法中初始化]
 * 2. 接着在Activity#dispatchKeyEvent() 或者 Activity#onKeyDown() 中调用本类中的resolveKeyEvent()方法。当扫码结束之后，会自动回调第一步设置的监听器中的方法
 *
 * 原理分析：
 * 1. 扫码枪就是一个外部的输入设备（和键盘一样）。扫码的时候，就是在极短的时间内输入了一系列的数字或字母
 * 2. 这样就可以在键盘事件中抓捕这些输入的字符，但是又会产生一个问题（快速扫两次的情形）：在键盘事件中应该抓捕多少个字符呢？即一个条码应该在哪个位置结束呢？ （有的扫码枪会以一个回车或者换行作为一次扫码的结束符，但是有的就纯粹的是一系列的条码。这个得需要设置）
 * 所以为了兼容性，应当是当短时间内不再输入字符的时候，就表示扫码已结束。这样只能定性描述，不能定量，只能自己在程序中用一个具体的数字来表示这个“短时间”，eg:500ms。（如果每个条码结束的时候都有一个结束符那该多好，直接判断这个结束符，就可以知道当前扫码已完成）
 *
 * 接下来就产生了BarcodeScannerResolver这个类。
 * 核心原理就一句话：在Activity的键盘监听事件中，每抓捕到一个字符的时候，就先向 Handler 一次一个runnable对象，再延迟500ms发送一个runnable. 这样若两个输入字符的间隔时间超过了500ms，则视为两次扫码
 *
 */
public class BarcodeScannerResolver {

    private static final String TAG = BarcodeScannerResolver.class.getSimpleName();

    // 若500ms之内无字符输入，则表示扫码完成. (若觉得时间还长，则可以设置成更小的值)
    private final static long MESSAGE_DELAY = 500;

    private boolean mCaps;//大写或小写
    private StringBuilder mResult = new StringBuilder();//扫码内容

    private OnScanSuccessListener mOnScanSuccessListener;
    private Handler mHandler = new Handler();

    private final Runnable mScanningEndRunnable = new Runnable() {
        @Override
        public void run() {
            performScanSuccess();
        }
    };

    //调用回调方法
    private void performScanSuccess() {
        String barcode = mResult.toString();
        //Log.i(TAG, "performScanSuccess -> barcode: "+barcode);
        if (mOnScanSuccessListener != null) {
            mOnScanSuccessListener.onScanSuccess(barcode);
        }
        mResult.setLength(0);
    }

    //key事件处理
    public void resolveKeyEvent(KeyEvent event) {

        int keyCode = event.getKeyCode();

        checkLetterStatus(event);//字母大小写判断

        Log.w(TAG, "keyEvent:" + event + "keyCode: " + keyCode + "char: " + KeyEvent.keyCodeToString(keyCode));
        if (event.getAction() == KeyEvent.ACTION_DOWN) {


            char aChar = getInputCode(event);
            Log.w(TAG, "aChar: " + aChar);

            if (aChar != 0) {
                mResult.append(aChar);
            }

            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //若为回车键，直接返回
                mHandler.removeCallbacks(mScanningEndRunnable);
                mHandler.post(mScanningEndRunnable);
            } else {
                //延迟post，若500ms内，有其他事件
                mHandler.removeCallbacks(mScanningEndRunnable);
                mHandler.postDelayed(mScanningEndRunnable, MESSAGE_DELAY);
            }

        }
    }

    //检查shift键
    private void checkLetterStatus(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                //按着shift键，表示大写
                mCaps = true;
            } else {
                //松开shift键，表示小写
                mCaps = false;
            }
        }
    }

    //获取扫描内容
    private char getInputCode(KeyEvent event) {
        int keyCode = event.getKeyCode();

        char aChar;
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            //字母
            aChar = (char) ((mCaps ? 'A' : 'a') + keyCode - KeyEvent.KEYCODE_A);
        } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            //数字
            aChar = (char) ('0' + keyCode - KeyEvent.KEYCODE_0);
        } else {
            //其他符号
            switch (keyCode) {
                case KeyEvent.KEYCODE_PERIOD:
                    aChar = '.';
                    break;
                case KeyEvent.KEYCODE_MINUS:
                    aChar = mCaps ? '_' : '-';
                    break;
                case KeyEvent.KEYCODE_SLASH:
                    aChar = '/';
                    break;
                case KeyEvent.KEYCODE_BACKSLASH:
                    aChar = mCaps ? '|' : '\\';
                    break;
                default:
                    aChar = 0;
                    break;
            }
        }
        return aChar;
    }


    public interface OnScanSuccessListener {
        void onScanSuccess(String barcode);
    }

    public void setScanSuccessListener(OnScanSuccessListener onScanSuccessListener) {
        mOnScanSuccessListener = onScanSuccessListener;
    }

    public void removeScanSuccessListener() {
        mHandler.removeCallbacks(mScanningEndRunnable);
        mOnScanSuccessListener = null;
    }

}