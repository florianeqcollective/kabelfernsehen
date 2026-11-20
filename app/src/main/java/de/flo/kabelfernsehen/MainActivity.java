package de.flo.kabelfernsehen;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

/** Huelle um den Web-Player: Vollbild, Fernbedienungstasten an den Player weiterreichen. */
public class MainActivity extends Activity {
    private WebView web;
    private boolean loaded = false;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        web = new WebView(this);
        web.setBackgroundColor(Color.BLACK);
        setContentView(web);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setAllowFileAccess(true);
        s.setAllowUniversalAccessFromFileURLs(true);
        web.loadUrl("file:///android_asset/index.html");
        web.requestFocus();
    }

    private void js(String code) {
        web.evaluateJavascript("try{" + code + "}catch(e){}", null);
    }

    private static String action(int k) {
        if (k >= KeyEvent.KEYCODE_0 && k <= KeyEvent.KEYCODE_9) return String.valueOf(k - KeyEvent.KEYCODE_0);
        if (k >= KeyEvent.KEYCODE_NUMPAD_0 && k <= KeyEvent.KEYCODE_NUMPAD_9) return String.valueOf(k - KeyEvent.KEYCODE_NUMPAD_0);
        switch (k) {
            case KeyEvent.KEYCODE_DPAD_UP: return "up";
            case KeyEvent.KEYCODE_DPAD_DOWN: return "down";
            case KeyEvent.KEYCODE_DPAD_LEFT: return "left";
            case KeyEvent.KEYCODE_DPAD_RIGHT: return "right";
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER: return "ok";
            case KeyEvent.KEYCODE_BACK: return "back";
            case KeyEvent.KEYCODE_CHANNEL_UP:
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_NEXT: return "next";
            case KeyEvent.KEYCODE_CHANNEL_DOWN:
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS: return "prev";
            case KeyEvent.KEYCODE_MENU: return "help";
            case KeyEvent.KEYCODE_INFO:
            case KeyEvent.KEYCODE_GUIDE: return "info";
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_MUTE: return "mute";
            default: return null;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        int k = e.getKeyCode();
        String a = action(k);
        if (e.getAction() == KeyEvent.ACTION_DOWN) {
            js("tvKey('" + KeyEvent.keyCodeToString(k).replace("KEYCODE_", "") + "')");
            if (a != null && e.getRepeatCount() % 3 == 0) js("tvAct('" + a + "')");
        }
        return a != null || super.dispatchKeyEvent(e);
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
        if (loaded) js("tvResume()");
        loaded = true;
    }

    @Override
    protected void onPause() {
        js("tvStop()");
        web.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        web.destroy();
        super.onDestroy();
    }
}
