package com.parsroyal.solutiontablet.util;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.ui.fragment.BaseFragment;
import timber.log.Timber;

/**
 * Created by arash on 1/2/18.
 */

public class CameraManager {

  private static final int REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA_STORAGE = 35;

  public static boolean checkPermissions(Context context) {
    return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context,
        permission.WRITE_EXTERNAL_STORAGE) && PackageManager.PERMISSION_GRANTED == ActivityCompat
        .checkSelfPermission(context, permission.CAMERA);
  }

  public static void requestPermissions(Activity mainActivity) {
    boolean cameraShouldProvideRationale =
        ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, permission.CAMERA);
    boolean storageShouldProvideRationale =
        ActivityCompat
            .shouldShowRequestPermissionRationale(mainActivity, permission.WRITE_EXTERNAL_STORAGE);

    // Provide an additional rationale to the user. This would happen if the user denied the
    // request previously, but didn't check the "Don't ask again" checkbox.
    if (cameraShouldProvideRationale && storageShouldProvideRationale) {
      Timber.i("Displaying permission rationale to provide additional context.");
      ToastUtil.toastError(mainActivity,
          mainActivity.getString(R.string.permission_rationale_camera_storage),
          view -> {
            // Request permission
            ActivityCompat.requestPermissions(mainActivity,
                new String[]{permission.CAMERA, permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA_STORAGE);
          });
    } else if (cameraShouldProvideRationale) {
      Timber.i("Displaying permission rationale to provide additional context.");
      ToastUtil.toastError(mainActivity,
          mainActivity.getString(R.string.permission_rationale_camera_storage),
          view -> {
            // Request permission
            ActivityCompat.requestPermissions(mainActivity,
                new String[]{permission.CAMERA},
                REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA_STORAGE);
          });
    } else if (storageShouldProvideRationale) {
      Timber.i("Displaying permission rationale to provide additional context.");
      ToastUtil.toastError(mainActivity,
          mainActivity.getString(R.string.permission_rationale_camera_storage),
          view -> {
            // Request permission
            ActivityCompat.requestPermissions(mainActivity,
                new String[]{permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA_STORAGE);
          });
    } else {
      Timber.i("Requesting permission");
      // Request permission. It's possible this can be auto answered if device policy
      // sets the permission in a given state or the user denied the permission
      // previously and checked "Never ask again".
      ActivityCompat.requestPermissions(mainActivity,
          new String[]{permission.CAMERA, permission.WRITE_EXTERNAL_STORAGE},
          REQUEST_PERMISSIONS_REQUEST_CODE_CAMERA_STORAGE);
    }
  }

  public static void startCameraActivity(Activity mainActivity, Uri fileUri,
      BaseFragment baseFragment) {
    try {
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      // Create a media file name

      intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
      if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
        baseFragment.startActivityForResult(intent, Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
      }
    } catch (Exception e) {
      Logger.sendError("General Exception", "Error in opening camera " + e.getMessage());
      e.printStackTrace();
    }
  }
}
