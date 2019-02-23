package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alirezaafkar.sundatepicker.DatePicker;
import com.crashlytics.android.Crashlytics;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.PaymentType;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.Payment;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.PaymentService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PaymentServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.util.BarcodeUtil;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import timber.log.Timber;

/**
 * @author Shakib
 */
public class RegisterPaymentFragment extends BaseFragment {

  @BindView(R.id.e_payment_tv)
  TextView ePaymentTv;
  @BindView(R.id.e_payment_bottom_line)
  View ePaymentBottomLine;
  @BindView(R.id.e_payment_lay)
  RelativeLayout ePaymentLay;
  @BindView(R.id.cheque_tv)
  TextView chequeTv;
  @BindView(R.id.cheque_bottom_line)
  View chequeBottomLine;
  @BindView(R.id.cheque_lay)
  RelativeLayout chequeLay;
  @BindView(R.id.cash_tv)
  TextView cashTv;
  @BindView(R.id.cash_bottom_line)
  View cashBottomLine;
  @BindView(R.id.cash_lay)
  RelativeLayout cashLay;
  @BindView(R.id.payment_price_edt)
  EditText paymentPriceEdt;
  @BindView(R.id.tracking_num_edt)
  EditText trackingNumEdt;
  @BindView(R.id.tracking_num_lay)
  TextInputLayout trackingNumLay;
  @BindView(R.id.cheque_num_edt)
  EditText chequeNumEdt;
  @BindView(R.id.cheque_date_edt)
  EditText chequeDateEdt;
  @BindView(R.id.cheque_owner_edt)
  EditText chequeOwnerEdt;
  @BindView(R.id.account_num_edt)
  EditText accountNumEdt;
  @BindView(R.id.branch_edt)
  EditText branchEdt;
  @BindView(R.id.spinner)
  Spinner bankSpinner;
  @BindView(R.id.cheque_detail_lay)
  LinearLayout chequeDetailLay;
  @BindView(R.id.payment_price_lay)
  TextInputLayout paymentPriceLay;
  @BindView(R.id.cheque_num_lay)
  TextInputLayout chequeNumLay;
  @BindView(R.id.cheque_date_lay)
  TextInputLayout chequeDateLay;
  @BindView(R.id.cheque_owner_lay)
  TextInputLayout chequeOwnerLay;
  @BindView(R.id.account_num_lay)
  TextInputLayout accountNumLay;
  @BindView(R.id.branch_lay)
  TextInputLayout branchLay;
  @BindView(R.id.register_btn)
  Button registerBtn;
  @BindView(R.id.scanner)
  DecoratedBarcodeView scanner;

  private MainActivity mainActivity;
  private CustomerService customerService;
  private BaseInfoService baseInfoService;
  private PaymentService paymentService;
  private VisitService visitService;
  private Customer customer;
  private boolean dateModified = false;
  private Payment payment;
  private List<LabelValue> bankList;
  private List<LabelValue> paymentTypes;
  private long amountValue;
  private int paymentTypePos = 0;
  private long visitId;
  private long visitlineBackendId;
  private BeepManager beepManager;
  private String lastText;

  private BarcodeCallback callback = new BarcodeCallback() {
    @Override
    public void barcodeResult(BarcodeResult result) {
      if (result.getText() == null || result.getText().equals(lastText)) {
        // Prevent duplicate scans
        return;
      }

      lastText = result.getText();

      scanner.setStatusText(result.getText());

      Map<String, String> extractedData = BarcodeUtil.extractCheckQR(lastText);
      if (Empty.isNotEmpty(extractedData)) {
        fillCheckData(extractedData);
      }
      beepManager.playBeepSoundAndVibrate();
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
    }
  };

  public RegisterPaymentFragment() {
    // Required empty public constructor
  }

  public static RegisterPaymentFragment newInstance() {
    return new RegisterPaymentFragment();
  }


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_register_payment, container, false);
    ButterKnife.bind(this, view);

    mainActivity = (MainActivity) getActivity();
    mainActivity.changeTitle(getString(R.string.register_payment));

    customerService = new CustomerServiceImpl(mainActivity);
    baseInfoService = new BaseInfoServiceImpl(mainActivity);
    paymentService = new PaymentServiceImpl(mainActivity);
    visitService = new VisitServiceImpl(mainActivity);
    paymentTypes = baseInfoService.getAllPaymentType();

    loadSpinnersData();
    loadPaymentData();
    addListener();
    setUpDatePicker();
    initScanner();
    return view;
  }


  private void fillCheckData(Map<String, String> extractedData) {

    chequeOwnerEdt.setText(NumberUtil.digitsToPersian(extractedData.get(Constants.NATIONAL_CODE)));

    accountNumEdt.setText(extractedData.get(Constants.SHABA));
    chequeNumEdt.setText(NumberUtil.digitsToPersian(extractedData.get(Constants.CHECK_SERIAL)));
    branchEdt.setText(NumberUtil.digitsToPersian(extractedData.get(Constants.BANK_BRANCH_CODE)));
    String bankCode = extractedData.get(Constants.BANK_CODE);
    if (Empty.isNotEmpty(bankCode)) {
      bankCode = bankCode.substring(1);
    }
    int position = 0;
    for (LabelValue labelValue : bankList) {
      if (labelValue.getCode().equals(bankCode)) {
        bankSpinner.setSelection(position);
        break;
      }
      position++;
    }
//    data.put(Constants.BANK_CODE, bankDetails[0]);
  }

  @Override
  public void onResume() {
    super.onResume();
    scanner.resume();
  }

  @Override
  public void onPause() {
    super.onPause();
    scanner.pause();
  }

  public void pause(View view) {
    scanner.pause();
  }

  public void resume(View view) {
    scanner.resume();
  }
/*
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
  }*/

  public void triggerScan(View view) {
    scanner.decodeSingle(callback);
  }

  private void initScanner() {
    Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
    scanner.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
    scanner.initializeFromIntent(mainActivity.getIntent());
    scanner.decodeContinuous(callback);
    scanner.setStatusText("بارکد چک را اسکن کنید");

    beepManager = new BeepManager(mainActivity);
  }

  private void setUpDatePicker() {
    chequeDateEdt.setOnClickListener(v -> {
      if (isDisabled()) {
        return;
      }
      DatePicker.Builder builder = new DatePicker.Builder().id(1);
      if (Empty.isNotEmpty(payment) && Empty.isNotEmpty(payment.getChequeDate())) {
        String[] date = payment.getChequeDate().split("/");
        builder.date(Integer.parseInt(date[2]),
            Integer.parseInt(date[1]),
            Integer.parseInt("13" + date[0]));
      }
      builder.build((id, calendar, day, month, year) -> {
        chequeDateEdt.setText(
            String.format(Locale.ENGLISH, "%02d/%02d/%02d", year % 100, month, day));
        dateModified = true;
      }).show(getFragmentManager(), "");
    });
  }

  private void loadPaymentData() {
    try {
      Bundle arguments = getArguments();
      if (Empty.isNotEmpty(arguments)) {
        payment = paymentService.getPaymentById(arguments.getLong(Constants.PAYMENT_ID));
        customer = customerService
            .getCustomerByBackendId(arguments.getLong(Constants.CUSTOMER_BACKEND_ID));
        visitId = arguments.getLong(Constants.VISIT_ID);
        visitlineBackendId = arguments.getLong(Constants.VISITLINE_BACKEND_ID);
      } else {
        ToastUtil.toastError(mainActivity, R.string.message_error_in_loading_data);
        mainActivity.removeFragment(RegisterPaymentFragment.this);
      }
    } catch (BusinessException ex) {
      Timber.e(ex);
      ToastUtil.toastError(getActivity(), ex);
    } catch (Exception ex) {
      Timber.e(ex);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
      Logger
          .sendError("UI Exception", "Error in creating PaymentDetailFragment " + ex.getMessage());
    }

    if (Empty.isEmpty(customer)) {
      ToastUtil.toastError(mainActivity, R.string.message_error_in_loading_data);
      mainActivity.removeFragment(RegisterPaymentFragment.this);
    }
    if (Empty.isNotEmpty(payment)) {
      loadData();
      if (isDisabled()) {
        makeFormDisable();
      }
    } else {
      onCashSelected();
    }
  }

  private void makeFormDisable() {
    paymentPriceEdt.setEnabled(false);
    trackingNumEdt.setEnabled(false);
    chequeNumEdt.setEnabled(false);
    accountNumEdt.setEnabled(false);
    chequeOwnerEdt.setEnabled(false);
    bankSpinner.setEnabled(false);
    branchEdt.setEnabled(false);
    registerBtn.setEnabled(false);
    scanner.setVisibility(View.GONE);
  }

  private void loadSpinnersData() {
    bankList = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.BANK_NAME_TYPE.getId());
    if (Empty.isNotEmpty(bankList)) {
      bankSpinner.setAdapter(new LabelValueArrayAdapter(mainActivity, bankList));
    }
  }

  private boolean isDisabled() {
    return (payment != null && payment.getStatus().equals(SendStatus.SENT.getId()));
  }

  private void loadData() {
    try {
      amountValue = payment.getAmount() / 1000;
      paymentPriceEdt.setText(String.format(Locale.US, "%,d", amountValue));
      dateModified = true;
      switch (payment.getPaymentTypeId().intValue()) {
        case 2:
          onEPaySelected();
          trackingNumEdt.setText(payment.getTrackingNo());
          break;
        case 6:
          onChequeSelected();
          chequeDateEdt.setText(payment.getChequeDate());
          chequeNumEdt.setText(payment.getChequeNumber());
          String chequeNo = payment.getChequeAccountNumber();
          accountNumEdt.setText(Empty.isNotEmpty(chequeNo) ? chequeNo : "--");
          String chequeOwnerString = payment.getChequeOwner();
          chequeOwnerEdt.setText(Empty.isNotEmpty(chequeOwnerString) ? chequeOwnerString : "--");
          String branch = payment.getChequeBranch();
          branchEdt.setText(Empty.isNotEmpty(branch) ? branch : "--");
          int position = 0;
          for (LabelValue labelValue : bankList) {
            if (labelValue.getValue().equals(payment.getChequeBank())) {
              bankSpinner.setSelection(position);
              break;
            }
            position++;
          }

          break;
        default:
          onCashSelected();
          break;
      }
    } catch (Exception e) {
      Crashlytics
          .log(Log.ERROR, "Data retrieval", "Error in loading payment data " + e.getMessage());
      e.printStackTrace();
      mainActivity.removeFragment(this);
    }
  }

  private void addListener() {
    paymentPriceEdt.addTextChangedListener(new TextWatcher() {
      boolean isEditing = false;

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if (isEditing) {
          return;
        }
        isEditing = true;
        try {
          amountValue = Long
              .parseLong(NumberUtil.digitsToEnglish(s.toString().replaceAll(",", "")));
          String number = String.format(Locale.US, "%,d", amountValue);
          s.replace(0, s.length(), number);
        } catch (NumberFormatException e) {
          e.printStackTrace();
        }
        isEditing = false;
      }
    });
  }

  private void onItemSelected(RelativeLayout selectedLay, TextView selectedTextView,
      View selectedView) {
    selectedLay.setBackgroundResource(R.drawable.role_selected);
    selectedTextView
        .setTextColor(ContextCompat.getColor(mainActivity, R.color.payment_bottom_line));
    selectedView.setVisibility(View.VISIBLE);
  }

  private void onItemDeSelected(RelativeLayout selectedLay, TextView selectedTextView,
      View selectedView) {
    selectedLay.setBackgroundResource(R.drawable.role_default);
    selectedTextView.setTextColor(ContextCompat.getColor(mainActivity, R.color.gray_75));
    selectedView.setVisibility(View.GONE);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.REGISTER_PAYMENT_FRAGMENT;
  }

  private void onChequeSelected() {
    paymentTypePos = 2;
    trackingNumLay.setVisibility(View.GONE);
    chequeDetailLay.setVisibility(View.VISIBLE);
    scanner.setVisibility(View.VISIBLE);
    onItemSelected(chequeLay, chequeTv, chequeBottomLine);
    onItemDeSelected(cashLay, cashTv, cashBottomLine);
    onItemDeSelected(ePaymentLay, ePaymentTv, ePaymentBottomLine);
  }

  private void onCashSelected() {
    paymentTypePos = 0;
    trackingNumLay.setVisibility(View.GONE);
    chequeDetailLay.setVisibility(View.GONE);
    scanner.setVisibility(View.GONE);
    onItemDeSelected(chequeLay, chequeTv, chequeBottomLine);
    onItemSelected(cashLay, cashTv, cashBottomLine);
    onItemDeSelected(ePaymentLay, ePaymentTv, ePaymentBottomLine);
  }

  private void onEPaySelected() {
    paymentTypePos = 1;
    trackingNumLay.setVisibility(View.VISIBLE);
    chequeDetailLay.setVisibility(View.GONE);
    scanner.setVisibility(View.GONE);
    onItemDeSelected(chequeLay, chequeTv, chequeBottomLine);
    onItemDeSelected(cashLay, cashTv, cashBottomLine);
    onItemSelected(ePaymentLay, ePaymentTv, ePaymentBottomLine);
  }

  @OnClick({R.id.e_payment_lay, R.id.cheque_lay, R.id.cash_lay, R.id.register_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.e_payment_lay:
        if (!isDisabled()) {
          onEPaySelected();
        }
        break;
      case R.id.cheque_lay:
        if (!isDisabled()) {
          onChequeSelected();
        }
        break;
      case R.id.cash_lay:
        if (!isDisabled()) {
          onCashSelected();
        }
        break;
      case R.id.register_btn:
        save();
        break;
    }
  }

  private boolean validate() {
    if (Empty.isEmpty(customer)) {
      return false;
    }

    if (Empty.isEmpty(paymentPriceEdt.getText().toString())) {
      ToastUtil.toastError(mainActivity, R.string.message_amount_is_required);
      paymentPriceEdt.requestFocus();
      return false;
    }

    if (paymentTypePos == 1 && Empty
        .isEmpty(trackingNumEdt.getText().toString())) {
      ToastUtil.toastError(mainActivity, R.string.message_tracking_no_is_required);
      trackingNumEdt.requestFocus();
      return false;
    }

    if (paymentTypePos == 2) {
      if (!dateModified) {
        ToastUtil.toastError(mainActivity, R.string.message_cheque_date_is_required);
        chequeDateEdt.requestFocus();
        return false;
      }
      if (Empty.isEmpty(chequeNumEdt.getText().toString())) {
        ToastUtil.toastError(mainActivity, R.string.message_cheque_number_is_required);
        chequeNumEdt.requestFocus();
        return false;
      }
      if (Empty.isEmpty(paymentPriceEdt.getText().toString())) {
        ToastUtil.toastError(mainActivity, R.string.message_cheque_amount_is_required);
        paymentPriceEdt.requestFocus();
        return false;
      }
      if (Empty.isEmpty(accountNumEdt.getText().toString())) {
        ToastUtil.toastError(mainActivity, R.string.message_cheque_account_number_is_required);
        accountNumEdt.requestFocus();
        return false;
      }
      if (Empty.isEmpty(chequeOwnerEdt.getText().toString())) {
        ToastUtil.toastError(mainActivity, R.string.message_cheque_account_owner_is_required);
        chequeOwnerEdt.requestFocus();
        return false;
      }
    }

    return true;
  }

  private void save() {
    try {
      if (validate()) {
        if (Empty.isEmpty(payment)) {
          payment = new Payment();
        }
        payment.setCustomerBackendId(customer.getBackendId());
        payment.setAmount(amountValue * 1000);
        long paymentType = paymentTypes.get(paymentTypePos).getValue();
        payment.setPaymentTypeId(paymentType);

        if (PaymentType.POS.getId().equals(paymentType)) {
          payment.setTrackingNo(NumberUtil.digitsToEnglish(trackingNumEdt.getText().toString()));
        }

        if (PaymentType.CHEQUE.getId().equals(paymentType)) {
          payment.setChequeOwner(NumberUtil
              .digitsToEnglish(CharacterFixUtil.fixString(chequeOwnerEdt.getText().toString())));
          payment
              .setChequeAccountNumber(NumberUtil
                  .digitsToEnglish(CharacterFixUtil.fixString(accountNumEdt.getText().toString())));
          payment.setChequeBank(bankSpinner.getSelectedItemId());
          payment.setChequeBranch(NumberUtil
              .digitsToEnglish(CharacterFixUtil.fixString(branchEdt.getText().toString())));
          payment.setChequeDate(chequeDateEdt.getText().toString());
          payment.setChequeNumber(NumberUtil
              .digitsToEnglish(CharacterFixUtil.fixString(chequeNumEdt.getText().toString())));
        }
        payment.setStatus(SendStatus.NEW.getId());
        payment.setVisitBackendId(visitId);
        payment.setVisitlineBackendId(visitlineBackendId);
        long paymentId = paymentService.savePayment(payment);

        VisitInformationDetail visitDetail = new VisitInformationDetail(visitId,
            VisitInformationDetailType.CASH, paymentId);

        visitService.saveVisitDetail(visitDetail);

        ToastUtil.toastSuccess(mainActivity, R.string.message_payment_save_successfully);
        mainActivity.hideKeyboard();
        mainActivity.removeFragment(RegisterPaymentFragment.this);
      }
    } catch (BusinessException ex) {
      Timber.e(ex);
      ToastUtil.toastError(mainActivity, ex);
    } catch (Exception e) {
      Logger.sendError("Data Storage Exception", "Error in saving new payment " + e.getMessage());
      Timber.e(e);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(e));
    }
  }
}
