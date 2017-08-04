package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parsroyal.solutiontablet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.parsroyal.solutiontablet.constants.Constants.CHART;
import static com.parsroyal.solutiontablet.constants.Constants.DELIVERY;
import static com.parsroyal.solutiontablet.constants.Constants.SALE_MAN;


public class LoginFragment extends BaseFragment {

  @BindView(R.id.company_code_edt) EditText companyCodeEdt;
  @BindView(R.id.user_name_edt) EditText userNameEdt;
  @BindView(R.id.distributor_icon) ImageView distributorIcon;
  @BindView(R.id.distributor_tv) TextView distributorTv;
  @BindView(R.id.distributor_bottom_line) View distributorBottomLine;
  @BindView(R.id.distributor_lay) RelativeLayout distributorLay;
  @BindView(R.id.merchandiser_icon) ImageView merchandiserIcon;
  @BindView(R.id.merchandiser_tv) TextView merchandiserTv;
  @BindView(R.id.merchandiser_bottom_line) View merchandiserBottomLine;
  @BindView(R.id.merchandiser_lay) RelativeLayout merchandiserLay;
  @BindView(R.id.sales_man_icon) ImageView salesManIcon;
  @BindView(R.id.sales_man_tv) TextView salesManTv;
  @BindView(R.id.sales_man_bottom_line) View salesManBottomLine;
  @BindView(R.id.sales_man_lay) RelativeLayout salesManLay;
  @BindView(R.id.company_code_icon) ImageView companyCodeIcon;
  @BindView(R.id.user_name_icon) ImageView userNameIcon;

  private String selectedRole;

  public LoginFragment() {
    // Required empty public constructor
  }


  public static LoginFragment newInstance() {
    LoginFragment fragment = new LoginFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_login, container, false);
    ButterKnife.bind(this, view);
    onEditTextFocus();
    onSalesManTapped();
    return view;
  }

  @Override public int getFragmentId() {
    return 0;
  }

  @OnClick({R.id.distributor_lay, R.id.merchandiser_lay, R.id.sales_man_lay, R.id.log_in_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.distributor_lay:
        onDistributorTapped();
        break;
      case R.id.merchandiser_lay:
        onMerchandiserTapped();
        break;
      case R.id.sales_man_lay:
        onSalesManTapped();
        break;
      case R.id.log_in_btn:
        break;
    }
  }

  private void onDistributorTapped() {
    selectedRole = CHART;
    onRoleSelected(distributorLay, distributorIcon, distributorTv, distributorBottomLine);
    onRoleDeSelected(salesManLay, salesManIcon, salesManTv, salesManBottomLine);
    onRoleDeSelected(merchandiserLay, merchandiserIcon, merchandiserTv, merchandiserBottomLine);
  }

  private void onSalesManTapped() {
    selectedRole = SALE_MAN;
    onRoleDeSelected(distributorLay, distributorIcon, distributorTv, distributorBottomLine);
    onRoleSelected(salesManLay, salesManIcon, salesManTv, salesManBottomLine);
    onRoleDeSelected(merchandiserLay, merchandiserIcon, merchandiserTv, merchandiserBottomLine);
  }

  private void onMerchandiserTapped() {
    selectedRole = DELIVERY;
    onRoleDeSelected(distributorLay, distributorIcon, distributorTv, distributorBottomLine);
    onRoleDeSelected(salesManLay, salesManIcon, salesManTv, salesManBottomLine);
    onRoleSelected(merchandiserLay, merchandiserIcon, merchandiserTv, merchandiserBottomLine);
  }

  private void onEditTextFocus() {
    userNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus)
          userNameIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.primary));
        else
          userNameIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.login_gray));
      }
    });
    companyCodeEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus)
          companyCodeIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.primary));
        else
          companyCodeIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.login_gray));
      }
    });
  }

  private void onRoleSelected(RelativeLayout selectedLay, ImageView selectedIcon, TextView selectedTextView, View selectedView) {
    selectedLay.setBackgroundResource(R.drawable.role_selected);
    selectedIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.primary));
    selectedTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));
    selectedView.setVisibility(View.VISIBLE);
  }

  private void onRoleDeSelected(RelativeLayout selectedLay, ImageView selectedIcon, TextView selectedTextView, View selectedView) {
    selectedLay.setBackgroundResource(R.drawable.role_default);
    selectedIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.login_gray));
    selectedTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.login_gray));
    selectedView.setVisibility(View.GONE);
  }
}
