package com.ronda.barcodescanner;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private BarcodeScannerResolver mBarcodeScannerResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    /**
     * 点击开始扫码监听按钮
     *
     * @param view
     */
    public void startScanListen(View view) {
        mBarcodeScannerResolver = new BarcodeScannerResolver();
        mBarcodeScannerResolver.setScanSuccessListener(new BarcodeScannerResolver.OnScanSuccessListener() {
            @Override
            public void onScanSuccess(String barcode) {
                //TODO 显示扫描内容
                Log.w(TAG, "barcode: " + barcode);
                Toast.makeText(MainActivity.this, "barcode: " + barcode, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 点击移除扫码监听按钮
     *
     * @param view
     */
    public void removeScanListen(View view) {
        mBarcodeScannerResolver.removeScanSuccessListener();
        mBarcodeScannerResolver = null;
    }


    /**
     * 扫码枪是输入设备，检测是否有外接输入设备.(这样判断其实并不严格)
     *
     * @return
     */
    private boolean hasScanGun() {
        Configuration cfg = getResources().getConfiguration();
        return cfg.keyboard != Configuration.KEYBOARD_NOKEYS;
    }

//

    /**
     * Activity截获按键事件.发给 BarcodeScannerResolver
     * dispatchKeyEvent() 和 onKeyDown() 方法均可
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        Log.i(TAG, "dispatchKeyEvent");

        if (hasScanGun()) {
            mBarcodeScannerResolver.resolveKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (mBarcodeScannerResolver != null) {
//            mBarcodeScannerResolver.resolveKeyEvent(event);
//        }

        return super.onKeyDown(keyCode, event);
    }
}
