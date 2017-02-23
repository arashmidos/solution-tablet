package com.parsroyal.solutiontablet.ui.fragment;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ResourceUtil;

import java.text.MessageFormat;

/**
 * Created by Mahyar on 6/3/2015.
 */
public abstract class BaseFragment extends Fragment
{

    protected ProgressDialog progressDialog;

    protected void toastMessage(int messageResource)
    {
        String message = getActivity().getString(messageResource);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected void toastMessage(String message)
    {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected void toastError(int messageResource)
    {
        String message = getActivity().getString(messageResource);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected void toastError(String message)
    {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected void toastError(BusinessException ex)
    {
        String message = getErrorString(ex);
        if (Empty.isNotEmpty(message))
        {
            message = MessageFormat.format(message, ex.getArgs());
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public String getErrorString(Exception ex)
    {
        return ResourceUtil.getString(getActivity(), ex.getClass().getCanonicalName());
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

    public void showProgressDialog(View view, CharSequence message)
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
