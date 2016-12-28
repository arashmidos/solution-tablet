package com.conta.comer.ui.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.conta.comer.R;
import com.conta.comer.exception.UnknownSystemException;
import com.conta.comer.service.SettingService;
import com.conta.comer.service.impl.SettingServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.util.Empty;
import com.conta.comer.util.ToastUtil;
import com.conta.comer.util.constants.ApplicationKeys;

/**
 * Created by mahyar on 12/14/15.
 */
public class LoginDialogFragment extends DialogFragment
{

    public static final String TAG = LoginDialogFragment.class.getSimpleName();

    private SettingService settingService;

    public static LoginDialogFragment newInstance()
    {
        return new LoginDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        try
        {

            settingService = new SettingServiceImpl(getActivity());

            View view = inflater.inflate(R.layout.dialog_setting_login, null);
            final EditText usernameTxt = (EditText) view.findViewById(R.id.usernameTxt);
            final EditText passwordTxt = (EditText) view.findViewById(R.id.passwordTxt);
            Button loginBtn = (Button) view.findViewById(R.id.loginBtn);
            Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);

            loginBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    String username = settingService.getSettingValue(ApplicationKeys.SETTING_USERNAME);
                    String password = settingService.getSettingValue(ApplicationKeys.SETTING_PASSWORD);

                    String usernameValue = usernameTxt.getText().toString();
                    String passwordValue = passwordTxt.getText().toString();

                    if (Empty.isEmpty(usernameValue))
                    {
                        ToastUtil.toastError(getActivity(), R.string.error_username_is_empty);
                        usernameTxt.requestFocus();
                        return;
                    }

                    if (Empty.isEmpty(passwordTxt.getText().toString()))
                    {
                        ToastUtil.toastError(getActivity(), R.string.error_password_is_empty);
                        passwordTxt.requestFocus();
                        return;
                    }

                    if (username.equals(usernameValue) && password.equals(passwordValue))
                    {
                        ((MainActivity) getActivity()).changeFragment(MainActivity.SETTING_FRAGMENT_ID, false);
                        LoginDialogFragment.this.dismiss();
                    } else
                    {
                        ToastUtil.toastError(getActivity(), R.string.error_invalid_login);
                    }

                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    LoginDialogFragment.this.dismiss();
                }
            });

            getDialog().setTitle(R.string.login_to_setting);

            return view;

        } catch (Exception e)
        {
            ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
            Log.e(TAG, e.getMessage(), e);
            return inflater.inflate(R.layout.view_error_page, null);
        }
    }
}
