package com.consulting.request.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.consulting.request.Receiver.AlarmReceiver;
import com.consulting.request.RequestActivity;

public class MainService extends Service {
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        MainWorker.start();

        return START_REDELIVER_INTENT;
    }

    public void onDestroy() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pdi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10, pdi);
        super.onDestroy();
    }
}
