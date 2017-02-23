package com.parsroyal.solutiontablet.ui;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.entity.KeyValue;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import java.text.MessageFormat;

/**
 * Created by Mahyar on 6/3/2015.
 */
public class BaseFragmentActivity extends FragmentActivity
{

    public static final String TAG = BaseFragmentActivity.class.getSimpleName();
    protected ImageView menuIv;
    protected ProgressDialog progressDialog;
    private TextView userFullNameTxt;
    private TextView companyNameTxt;
    private BaseInfoService baseInfoService;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        baseInfoService = new BaseInfoServiceImpl(this);
    }

    protected void setupActionbar()
    {

        View actionBarView = getLayoutInflater().inflate(R.layout.activity_main_custom_actionbar, null);
        userFullNameTxt = (TextView) actionBarView.findViewById(R.id.userFullNameTxt);
        companyNameTxt = (TextView) actionBarView.findViewById(R.id.companyNameTxt);
        menuIv = (ImageView) actionBarView.findViewById(R.id.menuIv);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(actionBarView);
        getActionBar().setDisplayShowCustomEnabled(true);

        updateActionbar();
    }

    public void updateActionbar()
    {
        try
        {
            {
                KeyValue fullName = baseInfoService.getKeyValue(ApplicationKeys.USER_FULL_NAME);
                if (Empty.isNotEmpty(fullName))
                {
                    userFullNameTxt.setText(fullName.getValue());
                }
            }

            {
                KeyValue userCompanyName = baseInfoService.getKeyValue(ApplicationKeys.USER_COMPANY_NAME);
                if (Empty.isNotEmpty(userCompanyName))
                {
                    companyNameTxt.setText(userCompanyName.getValue());
                }
            }
        } catch (BusinessException ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            toastError(ex);
        } catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            toastError(new UnknownSystemException(ex));
        }
    }

    public void toastMessage(int messageResource)
    {
        String message = this.getString(messageResource);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void toastError(int messageResource)
    {
        String message = this.getString(messageResource);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void toastError(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void toastError(BusinessException ex)
    {
        String message = getStringResourceByName(ex.getClass().getCanonicalName());
        if (Empty.isNotEmpty(message))
        {
            message = MessageFormat.format(message, ex.getArgs());
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String getStringResourceByName(String aString)
    {
//        aString = aString.replace(".", "_");
        String packageName = this.getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return getString(resId);
    }

    public void showProgressDialog(CharSequence message)
    {
        if (this.progressDialog == null)
        {
            this.progressDialog = new ProgressDialog(this);
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

}
