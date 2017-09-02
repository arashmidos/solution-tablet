package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
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
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.List;
import java.util.Locale;


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
  Spinner spinner;
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
  private int ref;
  private int paymentTypePos = 0;
  private long visitId;

  public RegisterPaymentFragment() {
    // Required empty public constructor
  }


  public static RegisterPaymentFragment newInstance() {
    RegisterPaymentFragment fragment = new RegisterPaymentFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
    checkForExistPayment();
    onAmountTextChange();
    setUpDatePicker();
    return view;
  }

  private void setUpDatePicker() {
    chequeDateEdt.setOnClickListener(v -> {
      DatePicker.Builder builder = new DatePicker.Builder()
          .id(1);
      if (Empty.isNotEmpty(payment) && Empty.isNotEmpty(payment.getChequeDate())) {
        String[] date = payment.getChequeDate().split("/");
        builder.date(Integer.parseInt(date[2]),
            Integer.parseInt(date[1]),
            Integer.parseInt("13" + date[0]));
      }
      builder.build((id, calendar, day, month, year) ->
      {
        chequeDateEdt.setHint(
            String.format(Locale.ENGLISH, "%02d/%02d/%02d", year % 100, month, day));
        dateModified = true;
      }).show(getFragmentManager(), "");
    });
  }

  private void checkForExistPayment() {
    try {
      Bundle arguments = getArguments();
      if (Empty.isNotEmpty(arguments)) {
        payment = paymentService.getPaymentById(arguments.getLong(Constants.PAYMENT_ID));
        customer = customerService
            .getCustomerByBackendId(arguments.getLong(Constants.CUSTOMER_BACKEND_ID));
        visitId = arguments.getLong(Constants.VISIT_ID);
        ref = arguments.getInt(Constants.PARENT);
      } else {
        ToastUtil.toastError(getActivity(), R.string.message_error_in_loading_or_creating_customer);
        mainActivity.changeFragment(MainActivity.FEATURE_FRAGMENT_ID, true);
      }
    } catch (BusinessException ex) {
      Log.e(getFragmentTag(), ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), ex);
    } catch (Exception ex) {
      Crashlytics.log(Log.ERROR, "UI Exception",
          "Error in creating PaymentDetailFragment " + ex.getMessage());
      Log.e(getFragmentTag(), ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
    }

    if (Empty.isEmpty(customer)) {
      ToastUtil.toastError(getActivity(), R.string.message_error_in_loading_or_creating_customer);
      mainActivity.changeFragment(MainActivity.FEATURE_FRAGMENT_ID, true);
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
    spinner.setEnabled(false);
    branchEdt.setEnabled(false);
    registerBtn.setEnabled(false);
  }

  private void loadSpinnersData() {
    bankList = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.BANK_NAME_TYPE.getId());
    if (Empty.isNotEmpty(bankList)) {
      spinner.setAdapter(new LabelValueArrayAdapter(mainActivity, bankList));
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
          chequeDateEdt.setHint(payment.getChequeDate());
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
              spinner.setSelection(position);
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

  private void onAmountTextChange() {
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
        .setTextColor(ContextCompat.getColor(getActivity(), R.color.payment_bottom_line));
    selectedView.setVisibility(View.VISIBLE);
  }

  private void onItemDeSelected(RelativeLayout selectedLay, TextView selectedTextView,
      View selectedView) {
    selectedLay.setBackgroundResource(R.drawable.role_default);
    selectedTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_75));
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
    onItemSelected(chequeLay, chequeTv, chequeBottomLine);
    onItemDeSelected(cashLay, cashTv, cashBottomLine);
    onItemDeSelected(ePaymentLay, ePaymentTv, ePaymentBottomLine);
  }

  private void onCashSelected() {
    paymentTypePos = 0;
    trackingNumLay.setVisibility(View.GONE);
    chequeDetailLay.setVisibility(View.GONE);
    onItemDeSelected(chequeLay, chequeTv, chequeBottomLine);
    onItemSelected(cashLay, cashTv, cashBottomLine);
    onItemDeSelected(ePaymentLay, ePaymentTv, ePaymentBottomLine);
  }

  private void onEPaySelected() {
    paymentTypePos = 1;
    trackingNumLay.setVisibility(View.VISIBLE);
    chequeDetailLay.setVisibility(View.GONE);
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
      ToastUtil.toastError(getActivity(), R.string.message_amount_is_required);
      paymentPriceEdt.requestFocus();
      return false;
    }

    if (paymentTypePos == 1 && Empty
        .isEmpty(trackingNumEdt.getText().toString())) {
      ToastUtil.toastError(getActivity(), R.string.message_tracking_no_is_required);
      trackingNumEdt.requestFocus();
      return false;
    }

    if (paymentTypePos == 2) {
      if (!dateModified) {
        ToastUtil.toastError(getActivity(), R.string.message_cheque_date_is_required);
        chequeDateEdt.requestFocus();
        return false;
      }
      if (Empty.isEmpty(chequeNumEdt.getText().toString())) {
        ToastUtil.toastError(getActivity(), R.string.message_cheque_number_is_required);
        chequeNumEdt.requestFocus();
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
          payment.setChequeBank(spinner.getSelectedItemId());
          payment.setChequeBranch(NumberUtil
              .digitsToEnglish(CharacterFixUtil.fixString(branchEdt.getText().toString())));
          payment.setChequeDate(chequeDateEdt.getHint().toString());
          payment.setChequeNumber(NumberUtil
              .digitsToEnglish(CharacterFixUtil.fixString(chequeNumEdt.getText().toString())));
        }
        payment.setStatus(SendStatus.NEW.getId());
        long paymentId = paymentService.savePayment(payment);

        VisitInformationDetail visitDetail = new VisitInformationDetail(visitId,
            VisitInformationDetailType.CASH, paymentId);

        visitService.saveVisitDetail(visitDetail);

        ToastUtil.toastSuccess(getActivity(), R.string.message_payment_save_successfully);
        mainActivity.hideKeyboard();
        mainActivity.removeFragment(RegisterPaymentFragment.this);
      }
    } catch (BusinessException ex) {
      Log.e(getFragmentTag(), ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), ex);
    } catch (Exception e) {
      Crashlytics.log(Log.ERROR, "Data Storage Exception",
          "Error in saving new payment " + e.getMessage());
      Log.e(getFragmentTag(), e.getMessage(), e);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(e));
    }
  }
}
