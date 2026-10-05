package com.cano.sigaratracker;

import android.content.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationReceiver extends BroadcastReceiver {

    private String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
    }

    private String currentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date());
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs =
                context.getSharedPreferences(
                        "sigara_data",
                        Context.MODE_PRIVATE
                );

        String day = today();
        int count = prefs.getInt("count_" + day, 0);

        if ("ADD".equals(intent.getAction())) {

            count++;

            prefs.edit()
                    .putInt("count_" + day, count)
                    .putString("last_" + day, currentTime())
                    .apply();

        } else if ("UNDO".equals(intent.getAction())) {

            if (count > 0) {
                count--;

                prefs.edit()
                        .putInt("count_" + day, count)
                        .apply();
            }
        }

        NotificationHelper.show(context);
    }
}
