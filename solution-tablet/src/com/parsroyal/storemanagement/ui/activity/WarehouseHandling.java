package com.parsroyal.storemanagement.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.biz.impl.StoreRestServiceImpl;
import com.parsroyal.storemanagement.data.dao.impl.StockGoodDaoImpl;
import com.parsroyal.storemanagement.data.event.ErrorEvent;
import com.parsroyal.storemanagement.data.event.Event;
import com.parsroyal.storemanagement.data.event.StockEvent;
import com.parsroyal.storemanagement.data.event.StockGoodsEvent;
import com.parsroyal.storemanagement.data.model.GoodDetail;
import com.parsroyal.storemanagement.data.model.Packer;
import com.parsroyal.storemanagement.data.model.SelectOrderRequest;
import com.parsroyal.storemanagement.data.model.StockGood;
import com.parsroyal.storemanagement.ui.adapter.CustomerDetailViewPagerAdapter;
import com.parsroyal.storemanagement.ui.adapter.StockGoodsAdapter;
import com.parsroyal.storemanagement.ui.fragment.PackerDetailFragment;
import com.parsroyal.storemanagement.ui.fragment.PackerInfoFragment;
import com.parsroyal.storemanagement.ui.fragment.bottomsheet.PackerAddGoodBottomSheet;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.PackerAddGoodDialogFragment;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.PackerScanGoodDialogFragment;
import com.parsroyal.storemanagement.util.DialogUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.MultiScreenUtility;
import com.parsroyal.storemanagement.util.PreferenceHelper;
import com.parsroyal.storemanagement.util.RtlGridLayoutManager;
import com.parsroyal.storemanagement.util.ToastUtil;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WarehouseHandling extends AppCompatActivity {

  @BindView(R.id.toolbar_title_tv)
  TextView toolbarTitleTv;

  @BindView(R.id.fab_receive_data)
  FloatingActionButton fab;
  @BindView(R.id.register_btn)
  RelativeLayout registerBtn;
  @BindView(R.id.list)
  RecyclerView list;

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
  private List<StockGood> goods;
  private StockGoodsAdapter adapter;
  private StockGoodDaoImpl stockGoodDaoImpl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_warehouse_handling);
    ButterKnife.bind(this);
    this.storeService = new StoreRestServiceImpl();
    this.stockGoodDaoImpl = new StockGoodDaoImpl(this);
    DialogUtil.showProgressDialog(this, R.string.message_please_wait);
    storeService.checkStockStatus(this, PreferenceHelper.getSelectedStockAsn());
    toolbarTitleTv.setText(String.format("%s : %s", getString(R.string.warehouse_handling),
        PreferenceHelper.getSelectedStockName()));
    setUpRecyclerView();
    loadData();
  }

  private void loadData() {
    goods = stockGoodDaoImpl.retrieveAll();
    if (Empty.isNotEmpty(goods)) {
      updateList();
    }
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
    } else if (event instanceof StockEvent) {
      handleStockEvent((StockEvent) event);
    } else if (event instanceof StockGoodsEvent) {
      this.goods = ((StockGoodsEvent) event).getGoods();
      updateList();
    }
  }

  private void updateList() {
    adapter = new StockGoodsAdapter(this, goods);
    list.setAdapter(adapter);
    list.setVisibility(View.VISIBLE);
  }

  private void setUpRecyclerView() {
    if (MultiScreenUtility.isTablet(this)) {
      RtlGridLayoutManager rtlGridLayoutManager = new RtlGridLayoutManager(this, 2);
      list.setLayoutManager(rtlGridLayoutManager);
    } else {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
      list.setLayoutManager(linearLayoutManager);
    }
  }

  private void handleStockEvent(StockEvent event) {
    switch (event.getStatusCode()) {
      case STOCK_CHECK_STATUS:
        if (!event.isActive()) {
          ToastUtil.toastError(this, R.string.error_stock_not_active);
          fab.setVisibility(View.GONE);
        } else {
          fab.setVisibility(View.VISIBLE);
        }
    }
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
  }

  @OnClick({R.id.back_img, R.id.fab_receive_data, R.id.register_btn, R.id.search_img,
      R.id.barcode_img})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.back_img:
        onBackPressed();
        break;
      case R.id.fab_receive_data:
        showNewCountingDialog();
        break;
      case R.id.register_btn:
        submitOrder();
        break;
      case R.id.search_img:
        openSearchialog();
        break;
      case R.id.barcode_img:
        showScannerDialog();
        break;
    }
  }

  private void showNewCountingDialog() {

    if (Empty.isNotEmpty(goods)) {
      DialogUtil.showConfirmDialog(this, "اخطار",
          "با دریافت اطلاعات جدید، شمارش قبلی حذف خواهد شد. مطمئن هستید؟", (dialog, which) -> {
            stockGoodDaoImpl.deleteAll();
            receiveData();
          });
    } else {
      receiveData();
    }
  }


  private void submitOrder() {
    DialogUtil.showConfirmDialog(this, "ثبت سفارش", "آیا مطمئن هستید؟", (dialog, which) -> {
      dialog.cancel();
      DialogUtil.showProgressDialog(WarehouseHandling.this, R.string.message_please_wait);

      storeService
          .selectOrder(WarehouseHandling.this, new SelectOrderRequest(4, orderBy, null));
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

          DialogUtil.showProgressDialog(WarehouseHandling.this, R.string.message_please_wait);
          storeService.selectOrder(WarehouseHandling.this,
              new SelectOrderRequest(5, PreferenceHelper.getStockKey(), order));
        }
      } catch (NumberFormatException ex) {
        ToastUtil.toastError(WarehouseHandling.this, R.string.msg_wrong_input_order_code);
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
//    fragment = PackerScanGoodDialogFragment.newInstance(this);
//    }
//    fragment.show(ft, "scan_good");
  }

  private void receiveData() {
    DialogUtil.showProgressDialog(this, R.string.message_please_wait);
    storeService.getStockGoods(this, PreferenceHelper.getSelectedStockAsn());
  }

  public void findGoodsByBarcode(String barcode) {
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
      ToastUtil.toastMessage(this, R.string.good_not_exist);
    }
  }
}

