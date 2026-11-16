package com.jalaliclock;

import android.service.dreams.DreamService;
import android.widget.LinearLayout;

/** محافظ صفحه (Daydream): هنگام شارژ/داک، ساعت شمسی نمایش داده می‌شود */
public class JalaliDream extends DreamService {
    private ClockView cv;
    private InfoView info;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Cfg c = Cfg.load(this);
        setInteractive(false);
        setFullscreen(true);
        setScreenBright(c.dim >= 60);
        cv = new ClockView(this, c, false);
        cv.setOnCall(new Runnable() {
            public void run() {
                JalaliDream.this.finish();      // با تماس، محافظ صفحه بسته می‌شود
            }
        });
        info = c.showInfo ? new InfoView(this) : null;
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF000000);
        root.addView(cv, new LinearLayout.LayoutParams(-1, 0, 1f));
        if (info != null) root.addView(info, new LinearLayout.LayoutParams(-1, -2));
        setContentView(root);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        if (cv != null) cv.start();
        if (info != null) info.start();
    }

    @Override
    public void onDreamingStopped() {
        if (info != null) info.stop();
        if (cv != null) cv.stop();
        super.onDreamingStopped();
    }
}
