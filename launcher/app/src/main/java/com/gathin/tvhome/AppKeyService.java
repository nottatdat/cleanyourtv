package com.gathin.tvhome;

import android.accessibilityservice.AccessibilityService;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

/**
 * Opens an app from a dedicated remote button, from any app. Google TV remotes send BUTTON_3 for YouTube and
 * BUTTON_4 for Netflix (Vendor_18d1_Product_9451.kl); the stock launcher handles those keys, so with it disabled
 * the buttons do nothing. The mapping is `app_buttons` in config.xml. Only mapped keys whose app is installed are
 * consumed; every other key passes through untouched.
 * Opt-in: APP_KEY_SERVICE=1 build-and-install.sh (or -AppKeyService on Windows) enables it.
 */
public class AppKeyService extends AccessibilityService {

    private final SparseArray<String> apps = new SparseArray<>();

    @Override
    protected void onServiceConnected() {
        apps.clear();
        for (String entry : getResources().getStringArray(R.array.app_buttons)) {
            int sep = entry.indexOf('=');
            if (sep <= 0) continue;
            int code = KeyEvent.keyCodeFromString(entry.substring(0, sep).trim());
            if (code != KeyEvent.KEYCODE_UNKNOWN) apps.put(code, entry.substring(sep + 1).trim());
        }
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        String pkg = apps.get(event.getKeyCode());
        if (pkg == null) return false;
        PackageManager pm = getPackageManager();
        Intent launch = pm.getLeanbackLaunchIntentForPackage(pkg);
        if (launch == null) launch = pm.getLaunchIntentForPackage(pkg);
        if (launch == null) return false;
        if (event.getAction() == KeyEvent.ACTION_UP && event.getRepeatCount() == 0) {
            try {
                startActivity(launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (ActivityNotFoundException ignored) {
            }
        }
        return true;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }
}
