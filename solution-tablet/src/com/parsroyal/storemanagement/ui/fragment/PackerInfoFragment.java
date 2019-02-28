package com.parsroyal.storemanagement.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.SolutionTabletApplication;
import com.parsroyal.storemanagement.constants.Authority;
import com.parsroyal.storemanagement.data.model.Packer;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.activity.PackerActivity;
import com.parsroyal.storemanagement.util.NumberUtil;

public class PackerInfoFragment extends BaseFragment {

  @BindView(R.id.customer_name_tv)
  TextView customerNameTv;
  @BindView(R.id.date_tv)
  TextView dateTv;
  @BindView(R.id.order_code_tv)
  TextView orderCodeTv;
  @BindView(R.id.radif_value_tv)
  TextView radifValueTv;
  @BindView(R.id.aghlam_value_tv)
  TextView aghlamValueTv;
  @BindView(R.id.description_value)
  TextView descriptionValue;
  private PackerActivity activity;
  private Packer packer;
  private Unbinder unbinder;

  public PackerInfoFragment() {
    // Required empty public constructor
  }

  public static PackerInfoFragment newInstance() {
    PackerInfoFragment fragment = new PackerInfoFragment();
    return fragment;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_packer_info, container, false);
    unbinder = ButterKnife.bind(this, view);
    activity = (PackerActivity) getActivity();

//    checkPermissions();
    return view;
  }

  private void checkPermissions() {
    if (!SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_NEW_CUSTOMER)) {
    }
  }

  @Override
  public int getFragmentId() {
    return MainActivity.PACKER_INFO_FRAGMENT_ID;
  }

  public void update(Packer packer) {
    this.packer = packer;
    customerNameTv.setText(packer.getNameCST());
    orderCodeTv.setText(NumberUtil.digitsToPersian(packer.getCustomerCodeCST()));
    dateTv.setText(NumberUtil.digitsToPersian(packer.getOrderDate()));
    radifValueTv.setText(NumberUtil.digitsToPersian(packer.getRadif()));
    aghlamValueTv.setText(NumberUtil.digitsToPersian(packer.getGhalam()));
    descriptionValue.setText(NumberUtil.digitsToPersian(packer.getOtherDesc()));
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
