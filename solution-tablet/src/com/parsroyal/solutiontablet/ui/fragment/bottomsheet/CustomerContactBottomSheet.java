package com.parsroyal.solutiontablet.ui.fragment.bottomsheet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.CustomerInfoDialogFragment;

/**
 * Created by Arash on 11/22/18.
 */

public class CustomerContactBottomSheet extends BottomSheetDialogFragment {

  @BindView(R.id.main_lay)
  LinearLayout mainLay;
  @BindView(R.id.call_tv)
  TextView callTv;
  @BindView(R.id.sms_tv)
  TextView smsTv;
  private String phone;
  private CustomerInfoDialogFragment parent;

  public static CustomerContactBottomSheet newInstance(
      CustomerInfoDialogFragment customerInfoDialogFragment, String phoneNumber) {
    CustomerContactBottomSheet contactBottomSheet = new CustomerContactBottomSheet();
    contactBottomSheet.parent = customerInfoDialogFragment;
    contactBottomSheet.phone = phoneNumber;
    return contactBottomSheet;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.bottom_sheet_customer_contact, container, false);
    ButterKnife.bind(this, view);

    return view;
  }

  @OnClick({R.id.call_layout, R.id.sms_layout})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.call_layout:
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phone));
        startActivity(intent);
        dismiss();
        break;
      case R.id.sms_layout:
        Intent intent2 = new Intent(Intent.ACTION_VIEW,
            Uri.parse("sms:" + phone));
        startActivity(intent2);
        dismiss();
        break;
    }
  }
}
