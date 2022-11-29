package com.consulting.request.Services;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class RestartWorker extends Worker {

    public RestartWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        MainWorker.ctx = context;
        MainWorker.start();
    }

    @NonNull
    @Override
    public Result doWork() {

        return Result.success();
    }
}
