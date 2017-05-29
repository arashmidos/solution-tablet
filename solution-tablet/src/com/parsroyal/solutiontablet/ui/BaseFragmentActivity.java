package com.parsroyal.solutiontablet.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

/**
 * Created by Mahyar on 6/3/2015.
 */
public class BaseFragmentActivity extends AppCompatActivity {

  public static final String TAG = BaseFragmentActivity.class.getSimpleName();
  protected ImageView menuIv;
  protected ProgressDialog progressDialog;
  protected TextView userFullNameTxt;
  protected TextView companyNameTxt;
  private BaseInfoService baseInfoService;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    baseInfoService = new BaseInfoServiceImpl(this);
  }

  protected void setupActionbar() {
    View actionBarView = getLayoutInflater().inflate(R.layout.activity_main_custom_actionbar, null);
    userFullNameTxt = (TextView) actionBarView.findViewById(R.id.userFullNameTxt);
    companyNameTxt = (TextView) actionBarView.findViewById(R.id.companyNameTxt);
    menuIv = (ImageView) actionBarView.findViewById(R.id.menuIv);

    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    getSupportActionBar().setCustomView(actionBarView);
    getSupportActionBar().setDisplayShowCustomEnabled(true);

    updateActionbar();
  }

  public void updateActionbar() {
    try {
      KeyValue fullName = baseInfoService.getKeyValue(ApplicationKeys.USER_FULL_NAME);
      if (Empty.isNotEmpty(fullName)) {
        userFullNameTxt.setText(fullName.getValue());
      }

      KeyValue userCompanyName = baseInfoService.getKeyValue(ApplicationKeys.USER_COMPANY_NAME);
      if (Empty.isNotEmpty(userCompanyName)) {
        companyNameTxt.setText(userCompanyName.getValue());
      }

    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(this, ex);
    } catch (Exception ex) {
      Crashlytics.log(Log.ERROR, "UI Exception", "Error in updating actionbar " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(this, new UnknownSystemException(ex));
    }
  }

  public void changeMessageDialog(final String message) {
    if (progressDialog != null) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          progressDialog.setMessage(message);
        }
      });
    }
  }

  public void showProgressDialog(CharSequence message) {
    if (this.progressDialog == null) {
      this.progressDialog = new ProgressDialog(this);
      this.progressDialog.setIndeterminate(true);
      this.progressDialog.setCancelable(Boolean.FALSE);
    }
    this.progressDialog.setIcon(R.drawable.ic_action_info);
    this.progressDialog.setTitle(R.string.message_please_wait);
    this.progressDialog.setMessage(message);
    this.progressDialog.show();
  }

  public void dismissProgressDialog() {
    if (this.progressDialog != null) {
      this.progressDialog.dismiss();
    }
  }
}
