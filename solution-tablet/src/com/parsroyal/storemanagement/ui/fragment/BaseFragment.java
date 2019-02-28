package com.parsroyal.storemanagement.ui.fragment;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.util.ResourceUtil;

/**
 * Created by Arash on 6/3/2015.
 */
public abstract class BaseFragment extends Fragment {

  protected ProgressDialog progressDialog;

  public String getErrorString(Exception ex) {
    return ResourceUtil.getString(getContext(), ex.getClass().getCanonicalName());
  }

  public void showProgressDialog(CharSequence message) {
    this.progressDialog = new ProgressDialog(getActivity());
    this.progressDialog.setIndeterminate(true);
    this.progressDialog.setCancelable(Boolean.FALSE);

    this.progressDialog.setIcon(R.drawable.ic_info_outline_24dp);
    this.progressDialog.setTitle(R.string.message_please_wait);
    this.progressDialog.setMessage(message);
    this.progressDialog.show();
  }

  public void dismissProgressDialog() {
    if (this.progressDialog != null) {
      this.progressDialog.dismiss();
    }
  }

  protected void runOnUiThread(Runnable action) {
    if (getActivity() != null) {
      getActivity().runOnUiThread(action);
    }
  }

  protected View getErrorPageView(LayoutInflater inflater) {
    return inflater.inflate(R.layout.view_error_page, null);
  }

  public String getFragmentTag() {
    return this.getClass().getSimpleName();
  }

  public abstract int getFragmentId();
}
