package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.MainActivity;
import java.util.ArrayList;
import java.util.List;


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
    setUpSpinner();
    onCashSelected();
    return view;
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
    trackingNumLay.setVisibility(View.GONE);
    chequeDetailLay.setVisibility(View.VISIBLE);
    onItemSelected(chequeLay, chequeTv, chequeBottomLine);
    onItemDeSelected(cashLay, cashTv, cashBottomLine);
    onItemDeSelected(ePaymentLay, ePaymentTv, ePaymentBottomLine);
  }

  private void onCashSelected() {
    trackingNumLay.setVisibility(View.GONE);
    chequeDetailLay.setVisibility(View.GONE);
    onItemDeSelected(chequeLay, chequeTv, chequeBottomLine);
    onItemSelected(cashLay, cashTv, cashBottomLine);
    onItemDeSelected(ePaymentLay, ePaymentTv, ePaymentBottomLine);
  }

  private void onEPaySelected() {
    trackingNumLay.setVisibility(View.VISIBLE);
    chequeDetailLay.setVisibility(View.GONE);
    onItemDeSelected(chequeLay, chequeTv, chequeBottomLine);
    onItemDeSelected(cashLay, cashTv, cashBottomLine);
    onItemSelected(ePaymentLay, ePaymentTv, ePaymentBottomLine);
  }

  private void setUpSpinner() {
    List<String> list = new ArrayList<>();
    list.add("بانک");
    list.add("بانک");
    list.add("بانک");
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mainActivity,
        android.R.layout.simple_spinner_item, list);
    spinner.setAdapter(dataAdapter);
  }

  @OnClick({R.id.e_payment_lay, R.id.cheque_lay, R.id.cash_lay, R.id.register_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.e_payment_lay:
        onEPaySelected();
        break;
      case R.id.cheque_lay:
        onChequeSelected();
        break;
      case R.id.cash_lay:
        onCashSelected();
        break;
      case R.id.register_btn:
        Toast.makeText(mainActivity, "register", Toast.LENGTH_SHORT).show();
        break;
    }
  }
}
