package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.receiver.TrackerAlarmReceiver;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Mahyar on 6/3/2015.
 * Edited by Arash on 6/29/2016
 */
public class SettingFragment extends BaseFragment implements ResultObserver
{
    public static final String TAG = SettingFragment.class.getSimpleName();
    @BindView(R.id.cancelBtn)
    Button cancelBtn;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.serverAdress1Txt)
    EditText serverAddress1Txt;
    @BindView(R.id.serverAdress2Txt)
    EditText serverAddress2Txt;
    @BindView(R.id.usernameTxt)
    EditText usernameTxt;
    @BindView(R.id.passwordTxt)
    EditText passwordTxt;
    @BindView(R.id.userCodeTxt)
    EditText userCodeTxt;
    @BindView(R.id.saleTypeSp)
    Spinner saleTypeSp;
    @BindView(R.id.gps_interval)
    EditText gpsInterval;
    @BindView(R.id.enableTrackingCb)
    CheckBox enableTrackingCb;
    @BindView(R.id.branchSerialTxt)
    EditText branchSerialTxt;
    @BindView(R.id.stockSerialTxt)
    EditText stockSerialTxt;

    private SettingService settingService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_setting, null);
        ButterKnife.bind(this, rootView);

        settingService = new SettingServiceImpl(getActivity());

        String address1 = settingService.getSettingValue(ApplicationKeys.SETTING_SERVER_ADDRESS_1);
        String address2 = settingService.getSettingValue(ApplicationKeys.SETTING_SERVER_ADDRESS_2);
        String username = settingService.getSettingValue(ApplicationKeys.SETTING_USERNAME);
        String userCode = settingService.getSettingValue(ApplicationKeys.SETTING_USER_CODE);
        String password = settingService.getSettingValue(ApplicationKeys.SETTING_PASSWORD);
        String saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);
        String gpsIntervalValue = settingService.getSettingValue(ApplicationKeys.SETTING_GPS_INTERVAL);
        String gpsEnabled = settingService.getSettingValue(ApplicationKeys.SETTING_GPS_ENABLE);
        String branchSerial = settingService.getSettingValue(ApplicationKeys.SETTING_BRANCH_CODE);
        String stockSerial = settingService.getSettingValue(ApplicationKeys.SETTING_STOCK_CODE);

        serverAddress1Txt.setText(Empty.isNotEmpty(address1) ? address1 : "");
        serverAddress2Txt.setText(Empty.isNotEmpty(address2) ? address2 : "");
        usernameTxt.setText(Empty.isNotEmpty(username) ? username : "");
        passwordTxt.setText(Empty.isNotEmpty(password) ? password : "");
        userCodeTxt.setText(Empty.isNotEmpty(userCode) ? userCode : "");
        saleTypeSp.setSelection(Empty.isNotEmpty(saleType) ? Integer.parseInt(saleType) - 1 : 1);
        gpsInterval.setText(Empty.isNotEmpty(gpsIntervalValue) ? gpsIntervalValue : "");
        enableTrackingCb.setChecked(Empty.isNotEmpty(gpsEnabled) && gpsEnabled.equals("1"));
        branchSerialTxt.setText(Empty.isNotEmpty(branchSerial) ? branchSerial : "");
        stockSerialTxt.setText(Empty.isNotEmpty(stockSerial) ? stockSerial : "");

        serverAddress1Txt.requestFocus();

        return rootView;
    }

    private void invokeGetInformationService()
    {
        showProgressDialog(getActivity().getString(R.string.message_connecting_to_server_please_wait));
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    save();
                    settingService.getUserInformation(SettingFragment.this);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            toastMessage(R.string.message_setting_saved_successfully);
                        }
                    });


                } catch (final BusinessException ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            toastError(ex);
                        }
                    });
                } catch (final Exception ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            toastError(new UnknownSystemException(ex));
                        }
                    });
                }
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        dismissProgressDialog();
                    }
                });
            }
        }).start();
    }

    public void save()
    {
        if (validate())
        {
            String serverAddress1 = serverAddress1Txt.getText().toString();
            String serverAddress2 = serverAddress2Txt.getText().toString();
            String username = usernameTxt.getText().toString();
            String password = passwordTxt.getText().toString();
            String userCode = userCodeTxt.getText().toString();
            String saleType = String.valueOf(saleTypeSp.getSelectedItemPosition() + 1);
            String gpsIntervalValue = gpsInterval.getText().toString();
            String gpsEnabled = enableTrackingCb.isChecked() ? "1" : "0";
            String stockSerial = stockSerialTxt.getText().toString();
            String branchSerial = branchSerialTxt.getText().toString();

            settingService.saveSetting(ApplicationKeys.SETTING_SERVER_ADDRESS_1, serverAddress1);
            settingService.saveSetting(ApplicationKeys.SETTING_SERVER_ADDRESS_2, serverAddress2);
            settingService.saveSetting(ApplicationKeys.SETTING_USERNAME, username);
            settingService.saveSetting(ApplicationKeys.SETTING_USER_CODE, userCode);
            settingService.saveSetting(ApplicationKeys.SETTING_PASSWORD, password);
            settingService.saveSetting(ApplicationKeys.SETTING_SALE_TYPE, saleType);
            settingService.saveSetting(ApplicationKeys.SETTING_GPS_INTERVAL, gpsIntervalValue);
            settingService.saveSetting(ApplicationKeys.SETTING_GPS_ENABLE, gpsEnabled);
            settingService.saveSetting(ApplicationKeys.SETTING_STOCK_CODE, stockSerial);
            settingService.saveSetting(ApplicationKeys.SETTING_BRANCH_CODE, branchSerial);
        }
    }

    private boolean validate()
    {
        if (Empty.isEmpty(serverAddress1Txt.getText().toString()))
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    toastError(R.string.error_serverAddress1_is_required);
                    serverAddress1Txt.requestFocus();
                }
            });
            return false;
        } else if (Empty.isEmpty(serverAddress2Txt.getText().toString()))
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    toastError(R.string.error_serverAddress2_is_required);
                    serverAddress2Txt.requestFocus();
                }
            });
            return false;
        } else if (Empty.isEmpty(usernameTxt.getText().toString()))
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            toastError(R.string.error_serverAddress1_is_required);
                            serverAddress1Txt.requestFocus();
                        }
                    });
                }
            });
            return false;
        } else if (Empty.isEmpty(passwordTxt.getText().toString()))
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    toastError(R.string.error_password_is_required);
                    passwordTxt.requestFocus();
                }
            });
            return false;
        } else if (Empty.isEmpty(userCodeTxt.getText().toString()))
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    toastError(R.string.error_user_code_is_required);
                    userCodeTxt.requestFocus();
                }
            });
            return false;
        } else if (Empty.isEmpty(gpsInterval.getText().toString()))
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    toastError(R.string.error_gps_interval_is_required);
                    gpsInterval.requestFocus();
                }
            });
            return false;
        } else if (Empty.isEmpty(branchSerialTxt.getText().toString()))
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    toastError(R.string.error_branch_serial_is_required);
                    branchSerialTxt.requestFocus();
                }
            });
            return false;
        }else if (Empty.isEmpty(stockSerialTxt.getText().toString()))
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    toastError(R.string.error_stock_serial_is_required);
                    stockSerialTxt.requestFocus();
                }
            });
            return false;
        }
        try
        {
            long serialBranch = Long.parseLong(branchSerialTxt.getText().toString());
        } catch (Exception ignore)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    toastError(R.string.error_branch_serial_is_not_valid);
                    branchSerialTxt.requestFocus();
                }
            });
            return false;
        }try
        {
            long stockSerial = Long.parseLong(stockSerialTxt.getText().toString());
        } catch (Exception ignore)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    toastError(R.string.error_stock_serial_is_not_valid);
                    stockSerialTxt.requestFocus();
                }
            });
            return false;
        }
        return true;
    }

    @Override
    public void publishResult(final BusinessException ex)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                toastError(ex);
            }
        });
    }

    @Override
    public void publishResult(final String message)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                toastMessage(message);
            }
        });
    }

    @Override
    public void finished(boolean result)
    {
        if (result)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.changeFragment(MainActivity.NEW_CUSTOMER_FRAGMENT_ID, false);
                    mainActivity.updateActionbar();
                    //Start GPS Tracker
                    new TrackerAlarmReceiver().setAlarm(getContext());
                }
            });
        }
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.SETTING_FRAGMENT_ID;
    }

    @OnClick({R.id.cancelBtn, R.id.saveBtn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.cancelBtn:
                ((MainActivity) getActivity()).changeFragment(MainActivity.NEW_CUSTOMER_FRAGMENT_ID, false);
                break;
            case R.id.saveBtn:
                try
                {
                    invokeGetInformationService();
                } catch (BusinessException ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    toastError(ex);
                } catch (Exception ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    toastError(new UnknownSystemException(ex));
                }
                break;
        }
    }
}
