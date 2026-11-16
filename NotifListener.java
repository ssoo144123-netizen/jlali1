package com.jalaliclock;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/** فقط پیام‌ها و ایمیل‌های خوانده‌نشده را می‌شمارد؛ اعلان‌های دائمی و خود برنامه نادیده گرفته می‌شوند */
public class NotifListener extends NotificationListenerService {
    @Override
    public void onListenerConnected() {
        scan();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        scan();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        scan();
    }

    private void scan() {
        int n = 0;
        String sender = "", text = "";
        StatusBarNotification[] list = getActiveNotifications();
        if (list != null) {
            for (StatusBarNotification s : list) {
                if (s.isOngoing() || s.getPackageName().equals(getPackageName())) continue;
                Notification no = s.getNotification();
                String cat = no.category;
                if (!(Notification.CATEGORY_MESSAGE.equals(cat) || Notification.CATEGORY_EMAIL.equals(cat)
                        || Notification.CATEGORY_SOCIAL.equals(cat))) continue;
                n++;
                Bundle e = no.extras;
                CharSequence t = e.getCharSequence(Notification.EXTRA_TITLE);
                CharSequence x = e.getCharSequence(Notification.EXTRA_TEXT);
                sender = t == null ? "" : t.toString();
                text = x == null ? "" : x.toString();
            }
        }
        Unread.setMessages(n, sender, text);
    }
}
