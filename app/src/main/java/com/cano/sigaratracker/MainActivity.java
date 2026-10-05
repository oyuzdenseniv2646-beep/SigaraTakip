package com.cano.sigaratracker;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView countText;
    private TextView dateText;
    private TextView lastText;
    private SharedPreferences prefs;

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable background(String color, float radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor(color));
        d.setCornerRadius(dp(radius));
        return d;
    }

    private String today() {
        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());
    }

    private String currentTime() {
        return new SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
        ).format(new Date());
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        prefs = getSharedPreferences("sigara_data", MODE_PRIVATE);

        getWindow().setStatusBarColor(Color.parseColor("#101114"));
        getWindow().setNavigationBarColor(Color.parseColor("#101114"));

        createInterface();

        if (android.os.Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS
                    },
                    100
            );
        }

        NotificationHelper.createChannel(this);
        refresh();
        NotificationHelper.show(this);
    }

    private void createInterface() {

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.parseColor("#101114"));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(24), dp(24), dp(32));

        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("Sigara Takip");
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setTypeface(null, 1);

        root.addView(title);

        dateText = new TextView(this);
        dateText.setTextColor(Color.parseColor("#9297A3"));
        dateText.setTextSize(15);

        LinearLayout.LayoutParams dateParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        dateParams.topMargin = dp(6);

        root.addView(dateText, dateParams);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(
                dp(20),
                dp(30),
                dp(20),
                dp(30)
        );

        card.setBackground(
                background("#1A1C21", 24)
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        cardParams.topMargin = dp(28);

        root.addView(card, cardParams);

        TextView todayLabel = new TextView(this);
        todayLabel.setText("BUGÜN");
        todayLabel.setTextColor(
                Color.parseColor("#9297A3")
        );
        todayLabel.setTextSize(14);
        todayLabel.setTypeface(null, 1);

        card.addView(todayLabel);

        countText = new TextView(this);
        countText.setText("0");
        countText.setTextColor(Color.WHITE);
        countText.setTextSize(72);
        countText.setGravity(Gravity.CENTER);
        countText.setTypeface(null, 1);

        card.addView(countText);

        TextView cigaretteLabel = new TextView(this);
        cigaretteLabel.setText("sigara");
        cigaretteLabel.setTextColor(
                Color.parseColor("#9297A3")
        );
        cigaretteLabel.setTextSize(18);

        card.addView(cigaretteLabel);

        Button add = new Button(this);
        add.setText("🚬  +1 İÇTİM");
        add.setTextColor(Color.WHITE);
        add.setTextSize(18);
        add.setTypeface(null, 1);
        add.setBackground(
                background("#526DCE", 20)
        );

        LinearLayout.LayoutParams addParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(70)
                );

        addParams.topMargin = dp(24);

        root.addView(add, addParams);

        Button undo = new Button(this);
        undo.setText("↶  GERİ AL");
        undo.setTextColor(
                Color.parseColor("#D3D6DF")
        );
        undo.setTextSize(15);
        undo.setBackground(
                background("#24262D", 18)
        );

        LinearLayout.LayoutParams undoParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(55)
                );

        undoParams.topMargin = dp(12);

        root.addView(undo, undoParams);

        lastText = new TextView(this);
        lastText.setTextColor(
                Color.parseColor("#D3D6DF")
        );
        lastText.setTextSize(16);
        lastText.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        lastText.setBackground(
                background("#1A1C21", 20)
        );

        LinearLayout.LayoutParams lastParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        lastParams.topMargin = dp(25);

        root.addView(lastText, lastParams);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCigarette();
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoCigarette();
            }
        });

        setContentView(scroll);
    }

    private void addCigarette() {

        String day = today();

        int count =
                prefs.getInt(
                        "count_" + day,
                        0
                ) + 1;

        prefs.edit()
                .putInt(
                        "count_" + day,
                        count
                )
                .putString(
                        "last_" + day,
                        currentTime()
                )
                .apply();

        refresh();
        NotificationHelper.show(this);
    }

    private void undoCigarette() {

        String day = today();

        int count =
                prefs.getInt(
                        "count_" + day,
                        0
                );

        if (count > 0) {

            prefs.edit()
                    .putInt(
                            "count_" + day,
                            count - 1
                    )
                    .apply();

            refresh();
            NotificationHelper.show(this);
        }
    }

    private void refresh() {

        String day = today();

        int count =
                prefs.getInt(
                        "count_" + day,
                        0
                );

        String last =
                prefs.getString(
                        "last_" + day,
                        ""
                );

        countText.setText(
                String.valueOf(count)
        );

        dateText.setText(
                new SimpleDateFormat(
                        "dd MMMM yyyy",
                        Locale.getDefault()
                ).format(new Date())
        );

        if (last == null || last.length() == 0) {
            lastText.setText(
                    "Son sigara: Henüz kayıt yok"
            );
        } else {
            lastText.setText(
                    "Son sigara: " + last
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs != null && countText != null) {
            refresh();
        }
    }
}
