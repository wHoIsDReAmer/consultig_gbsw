package com.consulting.request.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.work.impl.model.Preference;

import com.consulting.request.Core.ConsultingResult;
import com.consulting.request.R;
import com.consulting.request.RecentActivity;
import com.consulting.request.RequestActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

public class MainWorker {
    public static boolean isRunning = false;
    public static boolean started = false;

    public static Context ctx;

    private static SharedPreferences preferences;

    public static void start() {
        if (started) return;

        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);

        isRunning = true;
        started = true;

        new Thread(() -> {
            final int delay = 1000;

            while (isRunning) {
                try {
                    String email = preferences.getString("email", "");

                    Thread.sleep(delay);

                    if (email.isEmpty())
                        continue;

                    String result = Jsoup.connect("http://goalsdhkdwk.cafe24app.com/api/get/userRequestList")
                            .header("content-type", "application/json")
                            .header("accept", "application/json")
                            .ignoreContentType(true)
                            .requestBody("{\"mail\": \"" + preferences.getString("email", "hehe") + "\"}")
                            .post()
                            .text();

                    if (result.contains("false")) {
                        continue;
                    }

                    JSONObject json = new JSONObject(result).getJSONObject("data");

                    String success = preferences.getString("success", "");
                    String deny = preferences.getString("deny", "");
                    String nowSuccess = json.getJSONArray("success").toString();
                    String nowDeny = json.getJSONArray("deny").toString();

                    if (!success.equals(nowSuccess) && !success.isEmpty()) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "alarm");
                        builder.setContentTitle("상담이 수락되었습니다.");
                        builder.setContentText("자세한 내용을 보려면 클릭해주세요.");
                        builder.setSmallIcon(R.mipmap.consulting_logo);

                        Intent i = new Intent(ctx, RequestActivity.class);
                        PendingIntent pdi = PendingIntent.getActivity(ctx, 0, i, PendingIntent.FLAG_IMMUTABLE);

                        builder.setContentIntent(pdi);

                        Notification notify = builder.build();

                        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            manager.createNotificationChannel(new NotificationChannel("alarm", "알람 채널", NotificationManager.IMPORTANCE_HIGH));
                        }

                        manager.notify(10, notify);
                    }
                    if (!deny.equals(nowDeny) && !deny.isEmpty()) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "alarm");
                        builder.setContentTitle("상담이 거절되었습니다.");
                        builder.setContentText("자세한 내용을 보려면 클릭해주세요.");
                        builder.setSmallIcon(R.mipmap.ic_launcher);

                        Intent i = new Intent(ctx, RequestActivity.class);
                        PendingIntent pdi = PendingIntent.getActivity(ctx, 0, i, PendingIntent.FLAG_IMMUTABLE);

                        builder.setContentIntent(pdi);

                        Notification notify = builder.build();

                        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            manager.createNotificationChannel(new NotificationChannel("alarm", "알람 채널", NotificationManager.IMPORTANCE_HIGH));
                        }

                        manager.notify(20, notify);
                    }

                    preferences.edit().putString("success", nowSuccess).apply();
                    preferences.edit().putString("deny", nowDeny).apply();
                } catch (Exception ignore) {}
            }
        }).start();
    }
}
