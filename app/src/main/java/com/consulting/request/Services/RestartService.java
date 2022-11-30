package com.consulting.request.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.consulting.request.R;
import com.consulting.request.RequestActivity;

public class RestartService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {

    }

    public int onStartCommand(Intent intent, int flag, int startId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "기본 채널");
        builder.setSmallIcon(R.mipmap.consulting_logo);
        builder.setContentTitle(null);
        builder.setContentText(null);

        Intent noti = new Intent(this, RequestActivity.class);
        PendingIntent pdi = PendingIntent.getActivity(this, 0, noti, 0);
        builder.setContentIntent(pdi);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("기본 채널", "default channel", NotificationManager.IMPORTANCE_NONE));
        }

        Notification notify = builder.build();
        startForeground(9, notify);

        Intent service = new Intent(this, MainService.class);
        startService(service);

        stopForeground(true);
        stopSelf();
        return START_STICKY;
    }

    public void onDestroy() {

    }
}
