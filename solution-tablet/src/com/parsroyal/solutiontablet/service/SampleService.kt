package com.parsroyal.solutiontablet.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ISTrueTimeSyncService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private var jobCurrentlyRunning = false

    // ...

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // ^ Warning: By default executed on main thread

        if (jobCurrentlyRunning) return Service.START_STICKY

     /*   Single
                .fromCallable {
                    jobCurrentlyRunning = true
                    Timber.d("Running TrueTime init now")
                }
                .flatMap<LongArray> {
                    TrueTimeRx.initializeNtp("time.google.com")
                }
                .doFinally { jobCurrentlyRunning = false }
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { Timber.v("TrueTime was initialized at ${it!!.contentToString()}") },
                        { Timber.v("TrueTime init fail ${it}")  }
                )*/

        return Service.START_STICKY
    }

    // ...
}