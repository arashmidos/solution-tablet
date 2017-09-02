package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.BaseInfoTypes;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.SaleOrderStatus;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.data.listmodel.CustomerListModel;
import com.parsroyal.solutiontablet.data.model.CustomerDto;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.BaseInfoService;
import com.parsroyal.solutiontablet.service.CustomerService;
import com.parsroyal.solutiontablet.service.LocationService;
import com.parsroyal.solutiontablet.service.SaleOrderService;
import com.parsroyal.solutiontablet.service.SettingService;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.CustomerServiceImpl;
import com.parsroyal.solutiontablet.service.impl.LocationServiceImpl;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.observer.FindLocationListener;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.LocationUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.Collections;
import java.util.List;

/**
 * @author Shakib
 */
public class PathDetailAdapter extends RecyclerView.Adapter<PathDetailAdapter.ViewHolder> {

  private static final String TAG = PathDetailAdapter.class.getName();
  private final boolean distanceServiceEnabled;
  private final CustomerService customerService;
  private final VisitService visitService;
  private final LocationService locationService;
  private final SaleOrderService orderService;
  private final long visitlineBackendId;
  private SettingService settingService;
  private LayoutInflater inflater;
  private MainActivity mainActivity;
  private List<CustomerListModel> customers;

  public PathDetailAdapter(Context mainActivity, List<CustomerListModel> customers,
      long visitlineBackendId) {
    this.mainActivity = (MainActivity) mainActivity;
    this.customers = customers;
    inflater = LayoutInflater.from(mainActivity);
    settingService = new SettingServiceImpl(mainActivity);
    customerService = new CustomerServiceImpl(mainActivity);
    visitService = new VisitServiceImpl(mainActivity);
    locationService = new LocationServiceImpl(mainActivity);
    orderService = new SaleOrderServiceImpl(mainActivity);

    this.visitlineBackendId = visitlineBackendId;

    distanceServiceEnabled = Boolean.valueOf(settingService
        .getSettingValue(ApplicationKeys.SETTING_CALCULATE_DISTANCE_ENABLE));
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_customers_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    CustomerListModel model = customers.get(position);
    setMargin(position == customers.size() - 1, holder.customerLay);

    holder.setData(model, position);
    holder.setListeners();
  }

  private void setMargin(boolean isLastItem, CardView cardView) {
    CardView.LayoutParams parameter = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    float scale = mainActivity.getResources().getDisplayMetrics().density;
    int paddingInPx = (int) (8 * scale + 0.5f);
    if (isLastItem) {
      parameter.setMargins(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
    } else {
      parameter.setMargins(paddingInPx, paddingInPx, paddingInPx, 0);
    }
    cardView.setLayoutParams(parameter);
  }

  @Override
  public int getItemCount() {
    return customers.size();
  }

  public List<CustomerListModel> getDataModel() {
    return customers;
  }

  public void setDataModel(List<CustomerListModel> dataModel) {
    this.customers = dataModel;
  }

  public List<CustomerListModel> getFilteredData(CharSequence constraint) {
    try {
      String keyword = "";
      if (constraint.length() != 0 && !constraint.toString().equals("")) {
        keyword = CharacterFixUtil.fixString(constraint.toString());
      }
      return customerService.getFilteredCustomerList(visitlineBackendId, keyword);

    } catch (final Exception ex) {
      Crashlytics
          .log(Log.ERROR, "Filter data", "Error in filtering customer list" + ex.getMessage());
      mainActivity
          .runOnUiThread(() -> ToastUtil.toastError(mainActivity, new UnknownSystemException(ex)));
      return Collections.emptyList();
    }
  }

  /**
   * @author Arash
   */
  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.customer_name_tv)
    TextView customerNameTv;
    @BindView(R.id.customer_id_tv)
    TextView customerIdTv;
    @BindView(R.id.customer_shop_name_tv)
    TextView customerShopNameTv;
    @BindView(R.id.has_location_img)
    ImageView hasLocationImg;
    @BindView(R.id.visit_today_img)
    ImageView visitTodayImg;
    @BindView(R.id.has_order_img)
    ImageView hasOrderImg;
    @BindView(R.id.customer_lay)
    CardView customerLay;
    private CustomerListModel model;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(CustomerListModel model, int position) {
      this.model = model;
      this.position = position;
      customerShopNameTv.setText(model.getShopName());
      customerNameTv.setText(model.getTitle());
      String customerCode = "کد : " + model.getCode();
      customerIdTv.setText(customerCode);
      //set location icon
      if (model.hasLocation()) {
        hasLocationImg.setImageResource(R.drawable.ic_gps_fixed_black_18dp);
        hasLocationImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.primary));
      } else {
        hasLocationImg.setImageResource(R.drawable.ic_gps_off_black_18dp);
        hasLocationImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
      }
      //set visit icon
      if (model.isVisited()) {
        visitTodayImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.log_in_enter_bg));
      } else {
        visitTodayImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
      }
      if (model.hasRejection()) {
        visitTodayImg
            .setColorFilter(ContextCompat.getColor(mainActivity, R.color.badger_background));
      }

      hasOrderImg.setVisibility(model.hasOrder() ? View.VISIBLE : View.GONE);

    }

    public void setListeners() {
      customerLay.setOnClickListener(v -> showCustomerDetailDialog());
    }

    private void showCustomerDetailDialog() {
      AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity);
      LayoutInflater inflater = (mainActivity).getLayoutInflater();
      View dialogView = inflater.inflate(R.layout.dialog_customer_detail, null);
      dialogBuilder.setView(dialogView);

      TextView customerNameTv = (TextView) dialogView.findViewById(R.id.customer_name_tv);
      TextView customerShopNameTv = (TextView) dialogView.findViewById(R.id.customer_shop_name_tv);
      TextView customerIdTv = (TextView) dialogView.findViewById(R.id.customer_id_tv);
      TextView customerLocationTv = (TextView) dialogView.findViewById(R.id.location_tv);
      TextView customerMobileTv = (TextView) dialogView.findViewById(R.id.mobile_tv);
      TextView customerPhoneTv = (TextView) dialogView.findViewById(R.id.phone_tv);
      TextView errorMessageTv = (TextView) dialogView.findViewById(R.id.error_msg);
      TextView cancelTv = (TextView) dialogView.findViewById(R.id.cancel_btn);
      Button enterBtn = (Button) dialogView.findViewById(R.id.enter_btn);
      Button noVisitBtn = (Button) dialogView.findViewById(R.id.no_visit_btn);
      ImageView hasLocationImg = (ImageView) dialogView.findViewById(R.id.has_location_img);
      ImageView visitTodayImg = (ImageView) dialogView.findViewById(R.id.visit_today_img);
      ImageView hasOrderImg = (ImageView) dialogView.findViewById(R.id.has_order_img);
      customerShopNameTv.setText(model.getShopName());
      customerNameTv.setText(model.getTitle());
      String customerCode = "کد : " + model.getCode();
      customerIdTv.setText(customerCode);
      //set location icon
      if (model.hasLocation()) {
        hasLocationImg.setImageResource(R.drawable.ic_gps_fixed_black_18dp);
        hasLocationImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.primary));

      } else {
        hasLocationImg.setImageResource(R.drawable.ic_gps_off_black_18dp);
        hasLocationImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
      }
      //set visit icon
      if (model.isVisited()) {
        visitTodayImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.log_in_enter_bg));
      } else {
        visitTodayImg.setColorFilter(ContextCompat.getColor(mainActivity, R.color.login_gray));
      }
      if (model.hasRejection()) {
        visitTodayImg
            .setColorFilter(ContextCompat.getColor(mainActivity, R.color.badger_background));
      }

      hasOrderImg.setVisibility(model.hasOrder() ? View.VISIBLE : View.GONE);

      customerLocationTv.setText(model.getAddress());
      customerMobileTv.setText(model.getCellPhone());
      customerPhoneTv.setText(model.getPhoneNumber());

      AlertDialog alertDialog = dialogBuilder.create();
      alertDialog.show();
      cancelTv.setOnClickListener(v -> alertDialog.cancel());
      enterBtn.setOnClickListener(v -> {

        CustomerDto customer = customerService.getCustomerDtoById(model.getPrimaryKey());
        if (!distanceServiceEnabled || hasAcceptableDistance(customer, errorMessageTv)) {
          doEnter(customer);
          alertDialog.dismiss();
        } else {
          errorMessageTv.setText(R.string.error_distance_too_far_for_action);
          errorMessageTv.setVisibility(View.VISIBLE);
        }
      });
      noVisitBtn.setOnClickListener(v -> {
        showWantsDialog(errorMessageTv);
        alertDialog.cancel();
      });
    }

    private void showWantsDialog(TextView errorMessageTv) {
      AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivity);
      BaseInfoService baseInfoService = new BaseInfoServiceImpl(mainActivity);
      List<LabelValue> wants = baseInfoService
          .getAllBaseInfosLabelValuesByTypeId(BaseInfoTypes.WANT_TYPE.getId());
      if (Empty.isEmpty(wants)) {
        errorMessageTv.setText(R.string.message_found_no_wants_information);
        errorMessageTv.setVisibility(View.VISIBLE);
        return;
      }
//TODO Shakib, OLD STYLE dialog
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
        addNotVisited(selectedItem, notVisitedCb.isChecked());
      });
      dialogBuilder.setNegativeButton(R.string.button_cancel, (dialog, which) ->
      {
      });
      dialogBuilder.setTitle(R.string.title_save_want);
      dialogBuilder.create().show();
    }

    private void addNotVisited(LabelValue selectedItem, boolean notVisited) {
      Long visitId = visitService.startVisiting(model.getBackendId());
      VisitInformationDetail visitInformationDetail = new VisitInformationDetail(
          visitId,
          notVisited ? VisitInformationDetailType.NONE : VisitInformationDetailType.NO_ORDER,
          selectedItem.getValue());
      visitService.saveVisitDetail(visitInformationDetail);
      visitService.finishVisiting(visitId);
      ToastUtil.toastMessage(mainActivity, R.string.none_added_successfully);
      model.setHasRejection(true);
      notifyItemChanged(position);
    }

    private void doEnter(CustomerDto customer) {
      try {
        final Long visitInformationId = visitService.startVisiting(customer.getBackendId());

        locationService.findCurrentLocation(new FindLocationListener() {
          @Override
          public void foundLocation(Location location) {
            try {
              visitService.updateVisitLocation(visitInformationId, location);
            } catch (Exception e) {
              Crashlytics
                  .log(Log.ERROR, "Location Service",
                      "Error in finding location " + e.getMessage());
              Log.e(TAG, e.getMessage(), e);
            }
          }

          @Override
          public void timeOut() {
          }
        });

        orderService.deleteForAllCustomerOrdersByStatus(customer.getBackendId(),
            SaleOrderStatus.DRAFT.getId());
        Bundle args = new Bundle();
        args.putLong(Constants.VISIT_ID, visitInformationId);
        args.putLong(Constants.CUSTOMER_ID, customer.getId());
        mainActivity.changeFragment(MainActivity.VISIT_DETAIL_FRAGMENT_ID, args, false);

      } catch (BusinessException e) {
        Log.e(TAG, e.getMessage(), e);
        ToastUtil.toastError(mainActivity, e);
      } catch (Exception e) {
        Crashlytics
            .log(Log.ERROR, "General Exception", "Error in entering customer " + e.getMessage());
        Log.e(TAG, e.getMessage(), e);
        ToastUtil.toastError(mainActivity, new UnknownSystemException(e));
      }
    }

    private boolean hasAcceptableDistance(CustomerDto customer, TextView errorMessageTv) {

      Position position = new PositionServiceImpl(mainActivity).getLastPosition();
      Float distance;

      double lat2 = customer.getxLocation();
      double long2 = customer.getyLocation();

      if (lat2 == 0.0 || long2 == 0.0) {
        //Location not set for customer
        return true;
      }

      if (Empty.isNotEmpty(position)) {
        distance = LocationUtil
            .distanceBetween(position.getLatitude(), position.getLongitude(), lat2, long2);
      } else {
        errorMessageTv.setText(R.string.error_salesman_location_not_found);
        errorMessageTv.setVisibility(View.VISIBLE);
        return false;
      }
      return distance <= Constants.MAX_DISTANCE;
    }
  }
}
