package com.parsroyal.storemanagement.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.biz.impl.StoreRestServiceImpl;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.event.ActionEvent;
import com.parsroyal.storemanagement.data.event.ErrorEvent;
import com.parsroyal.storemanagement.data.event.Event;
import com.parsroyal.storemanagement.data.event.PackerEvent;
import com.parsroyal.storemanagement.data.model.GoodDetail;
import com.parsroyal.storemanagement.data.model.Packer;
import com.parsroyal.storemanagement.data.model.SelectOrderRequest;
import com.parsroyal.storemanagement.ui.adapter.CustomerDetailViewPagerAdapter;
import com.parsroyal.storemanagement.ui.fragment.CustomBottomSheet;
import com.parsroyal.storemanagement.ui.fragment.PackerDetailFragment;
import com.parsroyal.storemanagement.ui.fragment.PackerInfoFragment;
import com.parsroyal.storemanagement.ui.fragment.bottomsheet.PackerAddGoodBottomSheet;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.PackerAddGoodDialogFragment;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.PackerScanGoodDialogFragment;
import com.parsroyal.storemanagement.util.DialogUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.MultiScreenUtility;
import com.parsroyal.storemanagement.util.PreferenceHelper;
import com.parsroyal.storemanagement.util.ToastUtil;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import timber.log.Timber;

public class PackerActivity extends AppCompatActivity implements
    PackerScanGoodDialogFragment.OnGoodFoundListener {


  @BindView(R.id.toolbar_title_tv)
  TextView toolbarTitleTv;
  @BindView(R.id.tabs)
  TabLayout tabs;
  @BindView(R.id.viewpager)
  ViewPager viewpager;
  @BindView(R.id.fab_select_order)
  FloatingActionButton fabSelectOrder;
  @BindView(R.id.register_btn)
  RelativeLayout registerBtn;
  @BindView(R.id.filter_tv)
  TextView filterTv;
  @BindView(R.id.filter2_tv)
  TextView filter2Tv;
  @BindView(R.id.bottom_bar)
  LinearLayout bottomBar;
  @BindView(R.id.search_img)
  ImageView searchImg;
  @BindView(R.id.barcode_img)
  ImageView barcodeImg;
  private CustomerDetailViewPagerAdapter viewPagerAdapter;
  private PackerDetailFragment detailFragment;
  private PackerInfoFragment infoFragment;
  private long orderBy = 0;
  private StoreRestServiceImpl storeService;
  private Packer packer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_packer);
    ButterKnife.bind(this);
    this.storeService = new StoreRestServiceImpl();
    tabs.setupWithViewPager(viewpager);
    setUpViewPager();
  }

  private void setUpViewPager() {
    viewPagerAdapter = new CustomerDetailViewPagerAdapter(getSupportFragmentManager());
    detailFragment = PackerDetailFragment.newInstance();
    viewPagerAdapter.add(detailFragment, getString(R.string.title_goods_list));
    infoFragment = PackerInfoFragment.newInstance();
    viewPagerAdapter.add(infoFragment, getString(R.string.title_order_detail));
    viewpager.setAdapter(viewPagerAdapter);
    viewpager.setCurrentItem(viewPagerAdapter.getCount());
    tabs.addOnTabSelectedListener(new OnTabSelectedListener() {
      @Override
      public void onTabSelected(Tab tab) {
        if (tab.getPosition() == 0) {
          fabSelectOrder.setVisibility(View.GONE);
          searchImg.setVisibility(View.GONE);
          barcodeImg.setVisibility(View.VISIBLE);

        } else {
          fabSelectOrder.setVisibility(View.VISIBLE);
          searchImg.setVisibility(View.VISIBLE);
          barcodeImg.setVisibility(View.GONE);
        }
      }

      @Override
      public void onTabUnselected(Tab tab) {

      }

      @Override
      public void onTabReselected(Tab tab) {

      }
    });
  }

  @Override
  protected void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
    DialogUtil.dismissProgressDialog();
  }

  @Override
  protected void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void getMessage(Event event) {
    DialogUtil.dismissProgressDialog();
    if (event instanceof ErrorEvent) {
      switch (event.getStatusCode()) {
        case NO_NETWORK:
          ToastUtil.toastError(this, R.string.error_no_network);
          break;
        case NO_DATA_ERROR:
          ToastUtil.toastMessage(this, R.string.message_no_data_received);
          break;
        default:
          ToastUtil.toastError(this, getString(R.string.error_connecting_server));
      }
    } else if (event instanceof PackerEvent) {
      packer = ((PackerEvent) event).getDetailList();
      setData();
    } else if (event instanceof ActionEvent) {
      if (event.getStatusCode() == StatusCodes.DELETE_ORDER_SUCCESS) {
        ToastUtil.toastMessage(this, "سفارش با موفقیت ثبت شد");
      }
    }
  }

  private void setData() {
    Timber.i(packer.toString());

    if (detailFragment != null) {
      detailFragment.update(packer);
    }
    if (infoFragment != null) {
      infoFragment.update(packer);
    }

    if (Empty.isNotEmpty(packer.getGoodDetails())) {
      this.orderBy = packer.getGoodDetails().get(0).getOrderBy();
    }
//      recyclerView.hideShimmerAdapter();
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
  }

  @OnClick({R.id.back_img, R.id.fab_select_order, R.id.register_btn, R.id.filter_lay,
      R.id.filter2_lay, R.id.search_img, R.id.barcode_img})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.back_img:
        onBackPressed();
        break;
      case R.id.fab_select_order:
        showChooserDialog();
        break;
      case R.id.register_btn:
        submitOrder();
        break;
      case R.id.filter_lay:
        selectNextOrder();
        break;
      case R.id.filter2_lay:
        selectPrevOrder();
        break;
      case R.id.search_img:
        openSearchialog();
        break;
      case R.id.barcode_img:
        showScannerDialog();
        break;
    }
  }

  private void selectPrevOrder() {
    if (orderBy == 0) {
      return;
    }
    DialogUtil.showProgressDialog(this, R.string.message_please_wait);
    storeService.selectOrder(this, new SelectOrderRequest(3, orderBy - 1, null));
  }

  private void selectNextOrder() {
    DialogUtil.showProgressDialog(this, R.string.message_please_wait);
    storeService.selectOrder(this, new SelectOrderRequest(3, orderBy + 1, null));
  }

  private void submitOrder() {
    DialogUtil.showConfirmDialog(this, "ثبت سفارش", "آیا مطمئن هستید؟", (dialog, which) -> {
      dialog.cancel();
      DialogUtil.showProgressDialog(PackerActivity.this, R.string.message_please_wait);

      storeService
          .selectOrder(PackerActivity.this, new SelectOrderRequest(4, orderBy, null));
    });
  }

  private void openSearchialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("کد سفارش:");

// Set up the input
    final EditText input = new EditText(this);

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
    builder.setView(input);

// Set up the buttons
    builder.setPositiveButton(getString(R.string.search), (dialog, which) -> {
      String orderString = input.getText().toString();
      try {
        Long order = Long.parseLong(orderString);
        if (Empty.isNotEmpty(orderString)) {

          DialogUtil.showProgressDialog(PackerActivity.this, R.string.message_please_wait);
          storeService.selectOrder(PackerActivity.this,
              new SelectOrderRequest(5, PreferenceHelper.getStockKey(), order));
        }
      } catch (NumberFormatException ex) {
        ToastUtil.toastError(PackerActivity.this, R.string.msg_wrong_input_order_code);
      }
    });
    builder.setNegativeButton("لغو", (dialog, which) -> dialog.cancel());

    builder.show();
  }

  public void showScannerDialog() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    PackerScanGoodDialogFragment fragment;
//    if (MultiScreenUtility.isTablet(this)) {
//      fragment = PackerScanGoodBottomSheet.newInstance(this);
//    } else {
    fragment = PackerScanGoodDialogFragment.newInstance();
//    }
    fragment.show(ft, "scan_good");
  }

  private void showChooserDialog() {
    CustomBottomSheet bookingBottomSheet = CustomBottomSheet.getInstance();
    bookingBottomSheet.show(getSupportFragmentManager(), "custom_bottom_sheet");
  }

  public void selectOrder() {
    DialogUtil.showProgressDialog(this, R.string.message_please_wait);
    storeService.selectOrder(PackerActivity.this, new SelectOrderRequest());

  }

  public void selectRequest() {
    Toast.makeText(this, "درخواست", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void found(String barcode) {

    boolean found = false;
    List<GoodDetail> details = packer.getGoodDetails();
    for (int i = 0; i < details.size(); i++) {
      GoodDetail good = details.get(i);
      if (barcode.equals(good.getBarCode())) {
        found = true;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PackerAddGoodDialogFragment goodsFilterDialogFragment;
        if (MultiScreenUtility.isTablet(this)) {
          goodsFilterDialogFragment = PackerAddGoodBottomSheet.newInstance(good);
        } else {
          goodsFilterDialogFragment = PackerAddGoodDialogFragment.newInstance(good);
        }
        goodsFilterDialogFragment.show(ft, "add_good");
        break;
      }
    }
    if (!found) {
      ToastUtil.toastMessage(this, "کالا موجود نیست");
    }
  }

}

