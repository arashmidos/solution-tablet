package com.parsroyal.solutiontablet.ui.fragment;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Customer;
import com.parsroyal.solutiontablet.data.entity.VisitInformation;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.LocationServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.LabelValueArrayAdapter;
import com.parsroyal.solutiontablet.ui.observer.FindLocationListener;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerInfoFragment extends Fragment {

  private static final String TAG = CustomerInfoFragment.class.getName();
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
  private long customerId;
  private long visitId;
  private CustomerServiceImpl customerService;
  private SettingServiceImpl settingService;
  private VisitServiceImpl visitService;
  private BaseInfoServiceImpl baseInfoService;
  private LocationServiceImpl locationService;
  private CustomerDto customer;
  private String saleType;
  private MainActivity mainActivity;
  private SaleOrderServiceImpl saleOrderService;

  public CustomerInfoFragment() {
    // Required empty public constructor
  }

  public static CustomerInfoFragment newInstance(Bundle arguments) {
    CustomerInfoFragment fragment = new CustomerInfoFragment();
    fragment.setArguments(arguments);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_customer_info, container, false);
    ButterKnife.bind(this, view);
    Bundle args = getArguments();
    customerId = args.getLong(Constants.CUSTOMER_ID);
    visitId = args.getLong(Constants.VISIT_ID);
    mainActivity = (MainActivity) getActivity();
    customerService = new CustomerServiceImpl(mainActivity);
    settingService = new SettingServiceImpl(mainActivity);
    visitService = new VisitServiceImpl(mainActivity);
    baseInfoService = new BaseInfoServiceImpl(mainActivity);
    locationService = new LocationServiceImpl(mainActivity);
    saleOrderService = new SaleOrderServiceImpl(mainActivity);

    customer = customerService.getCustomerDtoById(customerId);

    saleType = settingService.getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);

    setData();
    return view;
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
//    if (isVisibleToUser && getView() == null)
  }

  private void setData() {
    phoneTv.setText(customer.getPhoneNumber());
    mobileTv.setText(customer.getCellPhone());
    locationTv.setText(customer.getAddress());
    storeTv.setText(customer.getFullName());
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
        finishVisiting();
        break;
      case R.id.no_activity_lay:
        showWantsDialog();
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

  private void finishVisiting() {
    try {
      List<VisitInformationDetail> detailList = visitService.getAllVisitDetailById(visitId);
      if (detailList.size() == 0) {//TODO shakib old style
        DialogUtil.showConfirmDialog(mainActivity, getString(R.string.title_attention),
            getString(R.string.message_error_no_visit_detail_found),
            (dialogInterface, i) -> showWantsDialog());
        return;
      }
      VisitInformation visitInformation = visitService.getVisitInformationById(visitId);
      if (Empty.isEmpty(visitInformation.getxLocation()) || Empty
          .isEmpty(visitInformation.getyLocation())) {
        showDialogForEmptyLocation();
      } else {
        doFinishVisiting();
      }
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "General Exception", "Error in finishing visit " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
    }
  }

  private void showWantsDialog() {//TODO shakib old style
    List<VisitInformationDetail> detailList = visitService.getAllVisitDetailById(visitId);
    if (detailList.size() > 0) {
      ToastUtil.toastError(mainActivity, R.string.message_error_wants_denied);
      return;
    }
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity);

    List<LabelValue> wants = baseInfoService
        .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.WANT_TYPE.getId());
    if (Empty.isEmpty(wants)) {
      ToastUtil.toastError(mainActivity, R.string.message_found_no_wants_information);
      return;
    }

    View wantsDialogView = mainActivity.getLayoutInflater().inflate(R.layout.dialog_wants, null);
    final Spinner wantsSpinner = (Spinner) wantsDialogView.findViewById(R.id.wantsSP);
    CheckBox notVisitedCb = (CheckBox) wantsDialogView.findViewById((R.id.not_visited_cb));

    notVisitedCb.setOnCheckedChangeListener(
        (compoundButton, isChecked) -> wantsSpinner.setEnabled(!isChecked));
    LabelValueArrayAdapter labelValueArrayAdapter = new LabelValueArrayAdapter(mainActivity,
        wants);
    wantsSpinner.setAdapter(labelValueArrayAdapter);

    dialogBuilder.setView(wantsDialogView);
    dialogBuilder.setPositiveButton(R.string.button_ok, (dialog, which) ->
    {
      LabelValue selectedItem = (LabelValue) wantsSpinner.getSelectedItem();
      updateVisitResult(selectedItem, notVisitedCb.isChecked());
    });
    dialogBuilder.setNegativeButton(R.string.button_cancel, (dialog, which) ->
    {
    });
    dialogBuilder.setTitle(R.string.title_save_want);
    dialogBuilder.create().show();
  }

  private void updateVisitResult(LabelValue selectedItem, boolean notVisited) {
    try {
      VisitInformationDetail visitInformationDetail = new VisitInformationDetail(
          visitId,
          notVisited ? VisitInformationDetailType.NONE : VisitInformationDetailType.NO_ORDER,
          selectedItem.getValue());
      visitService.saveVisitDetail(visitInformationDetail);
    } catch (Exception ex) {
      Crashlytics.log(Log.ERROR, "Data Storage Exception",
          "Error in updating visit result " + ex.getMessage());
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
      Log.e(TAG, ex.getMessage(), ex);
    }
  }

  private void showDialogForEmptyLocation() {//TODO Shakib old style
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(getString(R.string.visit_location));
    builder.setMessage(getString(R.string.visit_empty_location_message));
    builder.setPositiveButton(getString(R.string.visit_empty_location_dialog_try_again),
        (dialogInterface, i) ->
        {
          dialogInterface.dismiss();
          tryFindingLocation();
        });

    builder.setNegativeButton(getString(R.string.visit_empty_location_dialog_finish),
        (dialogInterface, i) ->
        {
          dialogInterface.dismiss();
          doFinishVisiting();
        });

    builder.create().show();
  }

  private void tryFindingLocation() {
    try {
      final ProgressDialog progressDialog = new ProgressDialog(mainActivity);
      progressDialog.setIndeterminate(true);
      progressDialog.setCancelable(Boolean.FALSE);
      progressDialog.setIcon(R.drawable.ic_action_info);
      progressDialog.setTitle(R.string.message_please_wait);
      progressDialog.setMessage(getString(R.string.message_please_wait_finding_your_location));
      progressDialog.show();

      locationService.findCurrentLocation(new FindLocationListener() {
        @Override
        public void foundLocation(Location location) {
          progressDialog.dismiss();
          try {
            visitService.updateVisitLocation(visitId, location);
          } catch (Exception e) {
            Crashlytics.log(Log.ERROR, "Data Storage Exception",
                "Error in updating visit location " + e.getMessage());
            Log.e(TAG, e.getMessage(), e);
          }

          mainActivity.runOnUiThread(() -> showFoundLocationDialog());
        }

        @Override
        public void timeOut() {
          progressDialog.dismiss();
          mainActivity.runOnUiThread(() ->
          {
            ToastUtil.toastError(getActivity(), getString(R.string.visit_found_no_location));
            showDialogForEmptyLocation();
          });
        }
      });

    } catch (BusinessException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), ex);
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "Location Service", "Error in finding location " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(getActivity(), new UnknownSystemException(ex));
    }
  }

  private void showFoundLocationDialog() {//TODO shakib old style
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(getString(R.string.visit_location));
    builder.setMessage(getString(R.string.visit_found_location_message));
    builder.setPositiveButton(getString(R.string.finish_visit),
        (dialogInterface, i) -> doFinishVisiting());

    builder.create().show();
  }

  private void doFinishVisiting() {
    try {
      visitService.finishVisiting(visitId);
      Customer customer = customerService.getCustomerById(customerId);
      saleOrderService.deleteForAllCustomerOrdersByStatus(customer.getBackendId(),
          SaleOrderStatus.DRAFT.getId());
      mainActivity.removeFragment(this);
    } catch (Exception ex) {
      Crashlytics
          .log(Log.ERROR, "General Exception", "Error in finishing visit " + ex.getMessage());
      Log.e(TAG, ex.getMessage(), ex);
      ToastUtil.toastError(mainActivity, new UnknownSystemException(ex));
    }
  }
}
