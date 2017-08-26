package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.ui.MainActivity;


public class CustomerInfoFragment extends Fragment {

  @BindView(R.id.store_tv)
  TextView storeTv;
  @BindView(R.id.drop_img)
  ImageView dropImg;
  @BindView(R.id.show_more_tv)
  TextView showMoreTv;
  @BindView(R.id.location_tv)
  TextView locationTv;
  @BindView(R.id.mobile_tv)
  TextView mobileTv;
  @BindView(R.id.phone_tv)
  TextView phoneTv;
  @BindView(R.id.customer_detail_lay)
  LinearLayout customerDetailLay;

  private boolean isShowMore = true;

  public CustomerInfoFragment() {
    // Required empty public constructor
  }


  public static CustomerInfoFragment newInstance() {
    CustomerInfoFragment fragment = new CustomerInfoFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_customer_info, container, false);
    ButterKnife.bind(this, view);
    setData();
    return view;
  }

  //TODO:set customer data here
  private void setData() {
    phoneTv.setText("22893450");
    mobileTv.setText("09301780245");
    locationTv.setText("شیراز/خونه شکیب اینا");
    storeTv.setText("شکیب ایز د بست");
  }

  @OnClick({R.id.show_more_tv, R.id.register_order_lay, R.id.register_payment_lay,
      R.id.register_questionnaire_lay, R.id.register_image_lay, R.id.end_and_exit_visit_lay,
      R.id.no_activity_lay})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.register_order_lay:
        ((MainActivity) getActivity()).changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, true);
        break;
      case R.id.register_payment_lay:
        Toast.makeText(getActivity(), "Payment", Toast.LENGTH_SHORT).show();
        break;
      case R.id.register_questionnaire_lay:
        Toast.makeText(getActivity(), "Questionnaire", Toast.LENGTH_SHORT).show();
        break;
      case R.id.register_image_lay:
        Toast.makeText(getActivity(), "Image", Toast.LENGTH_SHORT).show();
        break;
      case R.id.end_and_exit_visit_lay:
        Toast.makeText(getActivity(), "Exit", Toast.LENGTH_SHORT).show();
        break;
      case R.id.no_activity_lay:
        Toast.makeText(getActivity(), "No", Toast.LENGTH_SHORT).show();
        break;
      case R.id.show_more_tv:
        onShowMoreTapped();
        break;
    }
  }

  private void onShowMoreTapped() {
    if (isShowMore) {
      dropImg.setImageResource(R.drawable.ic_arrow_drop_up);
      showMoreTv.setText(getString(R.string.show_less));
      customerDetailLay.setVisibility(View.VISIBLE);
    } else {
      dropImg.setImageResource(R.drawable.ic_arrow_drop_down);
      showMoreTv.setText(getString(R.string.show_more));
      customerDetailLay.setVisibility(View.GONE);
    }
    isShowMore = !isShowMore;
  }
}
