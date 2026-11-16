package com.jalaliclock;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;

/** نوار کوچک زیر ساعت: تماس‌های بی‌پاسخ و پیام‌های خوانده‌نشده، هر ۳ ثانیه به‌روز می‌شود */
public class InfoView extends TextView {
    private final Handler h = new Handler();
    private final Runnable tick = new Runnable() {
        public void run() {
            Context c = getContext();
            Unread.refreshCalls(c);
            setText(Unread.summary(c));
            h.postDelayed(this, 3000);
        }
    };

    public InfoView(Context c) {
        super(c);
        setGravity(Gravity.CENTER);
        setTextColor(0xFFDDDDDD);
        setTextSize(16);
        setPadding(16, 12, 16, 12);
    }

    public void start() {
        h.removeCallbacks(tick);
        tick.run();
    }

    public void stop() {
        h.removeCallbacks(tick);
    }
}
