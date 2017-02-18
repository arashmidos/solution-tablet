package com.conta.comer.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.conta.comer.BuildConfig;
import com.conta.comer.R;
import com.conta.comer.constants.Constants;
import com.conta.comer.data.event.UpdateEvent;
import com.conta.comer.data.response.UpdateResponse;
import com.conta.comer.service.ServiceGenerator;
import com.conta.comer.service.UpdaterService;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 16/02/2017.
 */
public class Updater
{

    private static DownloadManager downloadManager;
    private static long downloadReference;
    private static BroadcastReceiver receiverDownloadComplete;

    public static void checkAppUpdate(final Context context)
    {
        if (!NetworkUtil.isNetworkAvailable(context))
        {
            return;
        }

        UpdaterService updaterService = ServiceGenerator.createService(UpdaterService.class,
                Constants.UPDATE_USER, Constants.UPDATE_PASS);

        Call<UpdateResponse> call = updaterService.getUpdate();
        call.enqueue(new Callback<UpdateResponse>()
        {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response)
            {
                if (response.body() != null)
                {
                    UpdateResponse updateResponse = response.body();
                    if (updateResponse.isSuccess() && updateResponse.getVersion() > BuildConfig.VERSION_CODE)
                    {
                        doUpdate(context, updateResponse.getDownloadUrl(), updateResponse.getVersion());
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t)
            {
                Log.d("Updater", "Update failed");
            }
        });
    }

    private static void doUpdate(Context context, String publicUrl, final Integer version)
    {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri Download_Uri = Uri.parse(publicUrl);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        String appName = context.getResources().getString(R.string.app_name);
        request.setTitle(appName + context.getString(R.string.new_version));

        request.setDestinationInExternalFilesDir(context.getApplicationContext(),
                Environment.DIRECTORY_DOWNLOADS, appName + ".apk");
        downloadReference = downloadManager.enqueue(request);

        receiverDownloadComplete = new BroadcastReceiver()
        {

            @Override
            public void onReceive(Context context, Intent intent)
            {
                long reference = intent.getLongExtra(DownloadManager
                        .EXTRA_DOWNLOAD_ID, -1);
                if (downloadReference == reference)
                {
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Log.d("Updater", "Downloading of the new app version complete");
                    //start the installation of the latest version
                    PreferenceHelper.setLatestVersion(version);
                    Uri uriForDownloadedFile = downloadManager.getUriForDownloadedFile(downloadReference);
                    PreferenceHelper.setUpdateUri(uriForDownloadedFile.toString());
                    EventBus.getDefault().post(new UpdateEvent(uriForDownloadedFile, false));
                }
            }
        };

        context.registerReceiver(receiverDownloadComplete, filter);
    }

    public static boolean updateExist()
    {
        return PreferenceHelper.getLatestVersion() > BuildConfig.VERSION_CODE;
    }
}
