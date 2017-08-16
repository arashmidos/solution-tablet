package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.BuildConfig;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.exception.BackendIsNotReachableException;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.receiver.TrackerAlarmReceiver;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.impl.DataTransferServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

/**
 * Created by Mahyar on 6/3/2015.
 */
public class SettingFragment extends BaseFragment implements ResultObserver, OnFocusChangeListener {

  public static final String TAG = SettingFragment.class.getSimpleName();
  @BindView(R.id.cancelBtn)
  Button cancelBtn;
  @BindView(R.id.saveBtn)
  Button saveBtn;
  @BindView(R.id.serverAdress1Txt)
  EditText serverAddress1Txt;
  @BindView(R.id.usernameTxt)
  EditText usernameTxt;
  @BindView(R.id.passwordTxt)
  EditText passwordTxt;
  @BindView(R.id.userCodeTxt)
  EditText userCodeTxt;
  @BindView(R.id.saleTypeSp)
  Spinner saleTypeSp;
  @BindView(R.id.enableCalculateDistanceCb)
  CheckBox enableCalculateDistanceCb;
  @BindView(R.id.branchSerialTxt)
  EditText branchSerialTxt;
  @BindView(R.id.stockSerialTxt)
  EditText stockSerialTxt;
  @BindView(R.id.invoiceTypeTxt)
  EditText invoiceTypeTxt;
  @BindView(R.id.orderTypeTxt)
  EditText orderTypeTxt;
  @BindView(R.id.rejectTypeTxt)
  EditText rejectTypeTxt;
  @BindView(R.id.enable_sale_rate)
  CheckBox enableSaleRateCb;

  private SettingService settingService;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_setting, null);
    ButterKnife.bind(this, rootView);

    settingService = new SettingServiceImpl(getActivity());

    String address1 = settingService.getSettingValue(ApplicationKeys.SETTING_SERVER_ADDRESS_1);
    String username = settingService.getSettingValue(ApplicationKeys.SETTING_USERNAME);
    String userCode = settingService.getSettingValue(ApplicationKeys.SETTING_USER_CODE);
    String password = settingService.getSettingValue(ApplicationKeys.SETTING_PASSWORD);
    String saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);
    String distanceEnabled = settingService
        .getSettingValue(ApplicationKeys.SETTING_CALCULATE_DISTANCE_ENABLE);
    String branchSerial = settingService.getSettingValue(ApplicationKeys.SETTING_BRANCH_CODE);
    String stockSerial = settingService.getSettingValue(ApplicationKeys.SETTING_STOCK_CODE);
    String invoiceType = settingService.getSettingValue(ApplicationKeys.SETTING_INVOICE_TYPE);
    String orderType = settingService.getSettingValue(ApplicationKeys.SETTING_ORDER_TYPE);
    String rejectType = settingService.getSettingValue(ApplicationKeys.SETTING_REJECT_TYPE);
    String saleRateEnabled = settingService
        .getSettingValue(ApplicationKeys.SETTING_SALE_RATE_ENABLE);

    serverAddress1Txt.setText(Empty.isNotEmpty(address1) ? address1 : "");
    usernameTxt.setText(Empty.isNotEmpty(username) ? username : "");
    passwordTxt.setText(Empty.isNotEmpty(password) ? password : "");
    userCodeTxt.setText(Empty.isNotEmpty(userCode) ? userCode : "");
    saleTypeSp.setSelection(Empty.isNotEmpty(saleType) ? Integer.parseInt(saleType) - 1 : 1);
    enableCalculateDistanceCb
        .setChecked(Empty.isNotEmpty(distanceEnabled) && distanceEnabled.equals("1"));
    branchSerialTxt.setText(Empty.isNotEmpty(branchSerial) ? branchSerial : "");
    stockSerialTxt.setText(Empty.isNotEmpty(stockSerial) ? stockSerial : "");
    invoiceTypeTxt.setText(Empty.isNotEmpty(invoiceType) ? invoiceType : "");
    orderTypeTxt.setText(Empty.isNotEmpty(orderType) ? orderType : "");
    rejectTypeTxt.setText(Empty.isNotEmpty(rejectType) ? rejectType : "");
    enableSaleRateCb.setChecked(Empty.isNotEmpty(saleRateEnabled) && saleRateEnabled.equals("1"));

    if (BuildConfig.DEBUG && Empty.isEmpty(serverAddress1Txt.getText().toString())) {
      setTestData();
    }
    serverAddress1Txt.requestFocus();
    saleTypeSp.setFocusableInTouchMode(true);
    saleTypeSp.setOnFocusChangeListener(this);
    return rootView;
  }

  private void setTestData() {
    serverAddress1Txt.setText("http://173.212.199.107:50000/tabletbackend");
    usernameTxt.setText("tablet");
    passwordTxt.setText("123");
    userCodeTxt.setText("1016");
    enableCalculateDistanceCb.setChecked(true);
    enableSaleRateCb.setChecked(true);
    branchSerialTxt.setText("100101");
    stockSerialTxt.setText("100101");
    invoiceTypeTxt.setText("1");
    orderTypeTxt.setText("1");
    rejectTypeTxt.setText("2");
  }

  private void invokeGetInformationService(int updateType) {
    showProgressDialog(getActivity().getString(R.string.message_connecting_to_server_please_wait));
    new Thread(() ->
    {
      try {
        if (updateType != Constants.NO_UPDATE) {
          settingService.saveSetting(ApplicationKeys.USER_FULL_NAME, "");
          settingService.saveSetting(ApplicationKeys.USER_COMPANY_NAME, "");
          new DataTransferServiceImpl(getActivity()).clearData(updateType);
        }
        if (canSave()) {
          settingService.getUserInformation(SettingFragment.this);
        }
      } catch (BackendIsNotReachableException ex) {
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), ex));
      } catch (BusinessException ex) {
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), ex));
      } catch (final Exception ex) {
        Crashlytics.log(Log.ERROR, "Data transfer",
            "Error in getting user information " + ex.getMessage());
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), new UnknownSystemException(ex)));
      }
      runOnUiThread(() -> dismissProgressDialog());
    }).start();
  }

  public boolean canSave() {
    if (validate()) {
      String serverAddress1 = serverAddress1Txt.getText().toString();
      String username = usernameTxt.getText().toString();
      String password = passwordTxt.getText().toString();
      String userCode = userCodeTxt.getText().toString();
      String saleType = String.valueOf(saleTypeSp.getSelectedItemPosition() + 1);
      String distanceEnabled = enableCalculateDistanceCb.isChecked() ? "1" : "0";
      String stockSerial = stockSerialTxt.getText().toString();
      String branchSerial = branchSerialTxt.getText().toString();
      String invoiceType = invoiceTypeTxt.getText().toString();
      String orderType = orderTypeTxt.getText().toString();
      String rejectType = rejectTypeTxt.getText().toString();
      String saleRateEnabled = enableSaleRateCb.isChecked() ? "1" : "0";

      settingService.saveSetting(ApplicationKeys.SETTING_SERVER_ADDRESS_1, serverAddress1);
      settingService.saveSetting(ApplicationKeys.SETTING_USERNAME, username);
      settingService.saveSetting(ApplicationKeys.SETTING_USER_CODE, userCode);
      settingService.saveSetting(ApplicationKeys.SETTING_PASSWORD, password);
      settingService.saveSetting(ApplicationKeys.SETTING_SALE_TYPE, saleType);
      settingService
          .saveSetting(ApplicationKeys.SETTING_CALCULATE_DISTANCE_ENABLE, distanceEnabled);
      settingService.saveSetting(ApplicationKeys.SETTING_STOCK_CODE, stockSerial);
      settingService.saveSetting(ApplicationKeys.SETTING_BRANCH_CODE, branchSerial);
      settingService.saveSetting(ApplicationKeys.SETTING_INVOICE_TYPE, invoiceType);
      settingService.saveSetting(ApplicationKeys.SETTING_ORDER_TYPE, orderType);
      settingService.saveSetting(ApplicationKeys.SETTING_REJECT_TYPE, rejectType);
      settingService.saveSetting(ApplicationKeys.SETTING_SALE_RATE_ENABLE, saleRateEnabled);
      return true;
    }
    return false;
  }

  private boolean validate() {
    if (Empty.isEmpty(serverAddress1Txt.getText().toString())) {
      runOnUiThread(() ->
      {
        ToastUtil.toastError(getActivity(), R.string.error_serverAddress1_is_required);
        serverAddress1Txt.requestFocus();
      });
      return false;
    } else if (Empty.isEmpty(usernameTxt.getText().toString())) {
      runOnUiThread(() -> runOnUiThread(() ->
      {
        ToastUtil.toastError(getActivity(), R.string.error_serverAddress1_is_required);
        serverAddress1Txt.requestFocus();
      }));
      return false;
    } else if (Empty.isEmpty(passwordTxt.getText().toString())) {
      runOnUiThread(() ->
      {
        ToastUtil.toastError(getActivity(), R.string.error_password_is_required);
        passwordTxt.requestFocus();
      });
      return false;
    } else if (Empty.isEmpty(userCodeTxt.getText().toString())) {
      runOnUiThread(() ->
      {
        ToastUtil.toastError(getActivity(), R.string.error_user_code_is_required);
        userCodeTxt.requestFocus();
      });
      return false;
    } else if (Empty.isEmpty(branchSerialTxt.getText().toString())) {
      runOnUiThread(() ->
      {
        ToastUtil.toastError(getActivity(), R.string.error_branch_serial_is_required);
        branchSerialTxt.requestFocus();
      });
      return false;
    } else if (Empty.isEmpty(stockSerialTxt.getText().toString())) {
      runOnUiThread(() ->
      {
        ToastUtil.toastError(getActivity(), R.string.error_stock_serial_is_required);
        stockSerialTxt.requestFocus();
      });
      return false;
    } else if (Empty.isEmpty(invoiceTypeTxt.getText().toString())) {
      runOnUiThread(() ->
      {
        ToastUtil.toastError(getActivity(), R.string.error_invoice_type_is_required);
        invoiceTypeTxt.requestFocus();
      });
      return false;
    } else if (Empty.isEmpty(orderTypeTxt.getText().toString())) {
      runOnUiThread(() ->
      {
        ToastUtil.toastError(getActivity(), R.string.error_order_type_is_required);
        orderTypeTxt.requestFocus();
      });
      return false;
    } else if (Empty.isEmpty(rejectTypeTxt.getText().toString())) {
      runOnUiThread(() ->
      {
        ToastUtil.toastError(getActivity(), R.string.error_reject_type_is_required);
        rejectTypeTxt.requestFocus();
      });
      return false;
    }
    try {
      long serialBranch = Long.parseLong(branchSerialTxt.getText().toString());
    } catch (NumberFormatException ignore) {
      runOnUiThread(() ->
      {
        ToastUtil.toastError(getActivity(), R.string.error_branch_serial_is_not_valid);
        branchSerialTxt.requestFocus();
      });
      return false;
    }
    try {
      long stockSerial = Long.parseLong(stockSerialTxt.getText().toString());
    } catch (NumberFormatException ignore) {
      runOnUiThread(() ->
      {
        ToastUtil.toastError(getActivity(), R.string.error_stock_serial_is_not_valid);
        stockSerialTxt.requestFocus();
      });
      return false;
    }
    return true;
  }

  @Override
  public void publishResult(final BusinessException ex) {
    runOnUiThread(() -> ToastUtil.toastError(getActivity(), ex));
  }

  @Override
  public void publishResult(final String message) {
    runOnUiThread(() -> ToastUtil.toastMessage(getActivity(), message));
  }

  @Override
  public void finished(boolean result) {
    if (result) {
      runOnUiThread(() ->
      {
        runOnUiThread(() -> ToastUtil
            .toastSuccess(getActivity(), R.string.message_setting_saved_successfully));
        OldMainActivity oldMainActivity = (OldMainActivity) getActivity();
        oldMainActivity.removeFragment(this);
        oldMainActivity.updateActionbar();
        oldMainActivity.startGpsService();
        new TrackerAlarmReceiver().setAlarm(getContext());
      });
    }
  }

  @Override
  public int getFragmentId() {
    return OldMainActivity.SETTING_FRAGMENT_ID;
  }

  @OnClick({R.id.cancelBtn, R.id.saveBtn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.cancelBtn:
        ((OldMainActivity) getActivity()).removeFragment(this);
        break;
      case R.id.saveBtn:
        try {
          String userFullName = settingService.getSettingValue(ApplicationKeys.USER_FULL_NAME);
          //It there is some data
          if (Empty.isNotEmpty(userFullName)) {
            //Did he change essential info?
            boolean newSalesman = isNewSalesman();
            boolean saleInfoChanged = isSaleInfoChanged();
            if (newSalesman || saleInfoChanged) {
              DialogUtil.showCustomDialog(getActivity(), getString(R.string.warning),
                  getString(R.string.warning_delete_data_after_setting_change),
                  getString(R.string.yes),
                  (dialog, which) -> {
                    invokeGetInformationService(
                        newSalesman ? Constants.FULL_UPDATE : Constants.PARTIAL_UPDATE);
                  }, getString(R.string.no_send_data), (dialog, which) -> {
                    ((OldMainActivity) getActivity())
                        .changeFragment(OldMainActivity.DATA_TRANSFER_FRAGMENT_ID, true);
                  }, Constants.ICON_WARNING
              );
            } else {
              invokeGetInformationService(Constants.NO_UPDATE);
            }
          } else {
            //Its first use of the app
            invokeGetInformationService(Constants.FULL_UPDATE);
          }
        } catch (BusinessException ex) {
          Log.e(TAG, ex.getMessage(), ex);
          ToastUtil.toastError(getActivity(), ex);
        } catch (Exception ex) {
          Crashlytics.log(Log.ERROR, "UI Exception",
              "Error in settingFragment.onClick " + ex.getMessage());
          Log.e(TAG, ex.getMessage(), ex);
          ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
        }
        break;
    }
  }

  private boolean isSaleInfoChanged() {
    String saleType = String.valueOf(saleTypeSp.getSelectedItemPosition() + 1);
    String stockSerial = stockSerialTxt.getText().toString();
    String branchSerial = branchSerialTxt.getText().toString();

    String oldSaleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);
    String oldBranchSerial = settingService.getSettingValue(ApplicationKeys.SETTING_BRANCH_CODE);
    String oldStockSerial = settingService.getSettingValue(ApplicationKeys.SETTING_STOCK_CODE);

    return !(saleType.equals(oldSaleType) && stockSerial.equals(oldStockSerial) && branchSerial
        .equals(oldBranchSerial));
  }

  private boolean isNewSalesman() {
    String address1 = serverAddress1Txt.getText().toString();
    String username = usernameTxt.getText().toString();
    String userCode = userCodeTxt.getText().toString();

    String oldAddress1 = settingService.getSettingValue(ApplicationKeys.SETTING_SERVER_ADDRESS_1);
    String oldUsername = settingService.getSettingValue(ApplicationKeys.SETTING_USERNAME);
    String oldUserCode = settingService.getSettingValue(ApplicationKeys.SETTING_USER_CODE);

    return !(address1.equals(oldAddress1) && username.equals(oldUsername) && userCode
        .equals(oldUserCode));
  }

  @Override
  public void onFocusChange(View v, boolean hasFocus) {
    if (hasFocus) {
      v.performClick();
    }
  }
}
