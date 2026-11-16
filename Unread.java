package com.jalaliclock;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.Settings;

/** وضعیت تماس‌های بی‌پاسخ و پیام‌های خوانده‌نشده برای نمایش روی ساعت */
public final class Unread {
    private Unread() {}

    static volatile int msgCount = 0;
    static volatile String msgSender = "", msgText = "";
    static volatile int missed = -1;          // -1 = مجوز داده نشده
    static volatile String lastCaller = "";

    static void setMessages(int n, String sender, String text) {
        msgCount = n;
        msgSender = nz(sender);
        msgText = nz(text);
    }

    /** تماس‌های بی‌پاسخ خوانده‌نشده را می‌شمارد (فقط با مجوز READ_CALL_LOG) */
    public static void refreshCalls(Context c) {
        if (c.checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            missed = -1;
            lastCaller = "";
            return;
        }
        Cursor cur = null;
        try {
            cur = c.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER},
                    CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE + " AND " + CallLog.Calls.IS_READ + "=0",
                    null, CallLog.Calls.DATE + " DESC");
            if (cur == null) return;
            missed = cur.getCount();
            if (cur.moveToFirst()) {
                String nm = cur.getString(0);
                lastCaller = (nm != null && nm.length() > 0) ? nm : nz(cur.getString(1));
            } else {
                lastCaller = "";
            }
        } catch (Exception e) {
            missed = -1;
        } finally {
            if (cur != null) cur.close();
        }
    }

    /** آیا کاربر دسترسی «اعلان‌ها» را به برنامه داده است؟ */
    public static boolean hasNotifAccess(Context c) {
        String s = Settings.Secure.getString(c.getContentResolver(), "enabled_notification_listeners");
        return s != null && s.contains(c.getPackageName());
    }

    /**
     * متن نمایش: وقتی صفحه قفل است فقط شمارش نشان داده می‌شود (بدون نام و متن پیام)،
     * وقتی قفل باز است نام فرستنده و متن کوتاه هم نشان داده می‌شود.
     */
    public static String summary(Context c) {
        KeyguardManager km = (KeyguardManager) c.getSystemService(Context.KEYGUARD_SERVICE);
        boolean locked = km != null && km.isKeyguardLocked();
        StringBuilder sb = new StringBuilder();
        if (missed > 0) sb.append("تماس بی‌پاسخ: ").append(missed);
        if (msgCount > 0) {
            if (sb.length() > 0) sb.append("     ");
            sb.append("پیام خوانده‌نشده: ").append(msgCount);
        }
        if (!locked) {
            if (missed > 0 && lastCaller.length() > 0) sb.append("\nاز: ").append(cut(lastCaller));
            if (msgCount > 0) sb.append("\n").append(cut(msgSender)).append(": ").append(cut(msgText));
        }
        return sb.toString();
    }

    private static String cut(String s) {
        s = nz(s).trim();
        return s.length() > 40 ? s.substring(0, 40) + "…" : s;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
