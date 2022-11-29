package com.consulting.request.Receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.consulting.request.Services.MainService;
import com.consulting.request.Services.RestartService;
import com.consulting.request.Services.RestartWorker;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(RestartWorker.class).addTag("RESTART_WORK_TAG").build();
            WorkManager.getInstance(ctx).enqueue(request);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(ctx, RestartService.class);
            ctx.startForegroundService(in);
        } else {
            Intent in = new Intent(ctx, MainService.class);
            ctx.startService(in);
        }
    }
}
