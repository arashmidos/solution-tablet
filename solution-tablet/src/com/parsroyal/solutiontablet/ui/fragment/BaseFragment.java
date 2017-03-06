package com.parsroyal.solutiontablet.ui.fragment;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.util.ResourceUtil;

/**
 * Created by Mahyar on 6/3/2015.
 */
public abstract class BaseFragment extends Fragment
{
    protected ProgressDialog progressDialog;

    public String getErrorString(Exception ex)
    {
        return ResourceUtil.getString(getContext(), ex.getClass().getCanonicalName());
    }

    public void showProgressDialog(CharSequence message)
    {
        if (this.progressDialog == null)
        {
            this.progressDialog = new ProgressDialog(getActivity());
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.setCancelable(Boolean.FALSE);
        }
        this.progressDialog.setIcon(R.drawable.ic_action_info);
        this.progressDialog.setTitle(R.string.message_please_wait);
        this.progressDialog.setMessage(message);
        this.progressDialog.show();
    }

    public void dismissProgressDialog()
    {
        if (this.progressDialog != null)
        {
            this.progressDialog.dismiss();
        }
    }

    protected void runOnUiThread(Runnable action)
    {
        if (getActivity() != null)
        {
            getActivity().runOnUiThread(action);
        }
    }

    protected View getErrorPageView(LayoutInflater inflater)
    {
        View view = inflater.inflate(R.layout.view_error_page, null);
        return view;
    }

    public String getFragmentTag()
    {
        return this.getClass().getSimpleName();
    }

    public abstract int getFragmentId();
}
