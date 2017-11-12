package com.parsroyal.solutiontablet.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.UpdateEvent;
import com.parsroyal.solutiontablet.data.response.UpdateResponse;
import com.parsroyal.solutiontablet.service.ServiceGenerator;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.UpdaterService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import okhttp3.ResponseBody;
import org.apache.commons.io.IOUtils;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arash on 16/02/2017.
 */
public class Updater {

  private static final String TAG = Updater.class.getName();
  private static DownloadManager downloadManager;
  private static long downloadReference;
  private static BroadcastReceiver receiverDownloadComplete;
  private static final String API_UPDATE_URL = "http://173.212.199.107:50004/appcenter/app/latest/solution-mobile";

  public static void checkAppUpdate(final Context context) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      return;
    }

    SettingService settingService = new SettingServiceImpl(context);
    if (Empty.isEmpty(settingService.getSettingValue(ApplicationKeys.BACKEND_URI))) {
      return;
    }

    UpdaterService updaterService = null;
    try {
      updaterService = ServiceGenerator.createService(UpdaterService.class,
          Constants.UPDATE_USER, Constants.UPDATE_PASS);
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "Service Generator", "can not create update service " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      return;
    }

    Call<UpdateResponse> call = updaterService.getUpdate(API_UPDATE_URL);
    call.enqueue(new Callback<UpdateResponse>() {
      @Override
      public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
        if (response.body() != null) {
          UpdateResponse updateResponse = response.body();
          if (updateResponse != null && updateResponse.isSuccess()
              && updateResponse.getVersion() > BuildConfig.VERSION_CODE) {
            doUpdate(context, updateResponse.getDownloadUrl(), updateResponse.getVersion());
          }
        }
      }

      @Override
      public void onFailure(Call<UpdateResponse> call, Throwable t) {
        Log.d("Updater", "Update failed");
      }
    });
  }

  public static void downloadGoodsImages(final Context context) {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new ErrorEvent(StatusCodes.NO_NETWORK));
      return;
    }

    UpdaterService updaterService = ServiceGenerator.createService(UpdaterService.class);

    Call<ResponseBody> call = updaterService.downloadGoodsImages();
    call.enqueue(new Callback<ResponseBody>() {
      @Override
      public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
          if (response.body() != null) {

            InputStream in = response.body().byteStream();
            File path = context.getCacheDir();
            File file = new File(path, "images.zip");
            try {
              FileOutputStream fileOutputStream = new FileOutputStream(file);
              IOUtils.write(response.body().bytes(), fileOutputStream);
              MediaUtil.unpackZip(file);
              EventBus.getDefault()
                  .post(new Event(StatusCodes.SUCCESS, "Good images downloaded successfully"));

            } catch (java.io.IOException e) {
              e.printStackTrace();
              EventBus.getDefault().post(new ErrorEvent(StatusCodes.DATA_STORE_ERROR));
            }
          } else {
            EventBus.getDefault().post(new ErrorEvent(StatusCodes.INVALID_DATA));
          }
        } else {
          EventBus.getDefault().post(new ErrorEvent(StatusCodes.SERVER_ERROR));
        }
      }

      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t) {
        EventBus.getDefault().post(new ErrorEvent(StatusCodes.NETWORK_ERROR));
      }
    });
  }

  private static void doUpdate(Context context, String publicUrl, final Integer version) {
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

    receiverDownloadComplete = new BroadcastReceiver() {

      @Override
      public void onReceive(Context context, Intent intent) {
        long reference = intent.getLongExtra(DownloadManager
            .EXTRA_DOWNLOAD_ID, -1);
        if (downloadReference == reference) {
          DownloadManager downloadManager = (DownloadManager) context
              .getSystemService(Context.DOWNLOAD_SERVICE);
          Log.d("Updater", "Downloading of the new app version complete");
          //start the installation of the latest version
          PreferenceHelper.setLatestVersion(version);
          Uri uriForDownloadedFile = downloadManager.getUriForDownloadedFile(downloadReference);
          if (Empty.isNotEmpty(uriForDownloadedFile)) {
            PreferenceHelper.setUpdateUri(uriForDownloadedFile.toString());
            PreferenceHelper.setForceExit(false);
            EventBus.getDefault().post(new UpdateEvent(uriForDownloadedFile, false));
          }
        }
      }
    };

    context.registerReceiver(receiverDownloadComplete, filter);
  }

  public static boolean updateExist() {
    return PreferenceHelper.getLatestVersion() > BuildConfig.VERSION_CODE;
  }
}
