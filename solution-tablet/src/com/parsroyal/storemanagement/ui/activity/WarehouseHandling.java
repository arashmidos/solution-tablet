package com.parsroyal.storemanagement.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.biz.impl.StoreRestServiceImpl;
import com.parsroyal.storemanagement.constants.SendStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.dao.impl.StockGoodDaoImpl;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.event.DataTransferSuccessEvent;
import com.parsroyal.storemanagement.data.event.ErrorEvent;
import com.parsroyal.storemanagement.data.event.Event;
import com.parsroyal.storemanagement.data.event.StockEvent;
import com.parsroyal.storemanagement.data.event.StockGoodsEvent;
import com.parsroyal.storemanagement.data.model.Packer;
import com.parsroyal.storemanagement.data.model.StockGood;
import com.parsroyal.storemanagement.service.impl.DataTransferServiceImpl;
import com.parsroyal.storemanagement.ui.adapter.CustomerDetailViewPagerAdapter;
import com.parsroyal.storemanagement.ui.adapter.StockGoodsAdapter;
import com.parsroyal.storemanagement.ui.fragment.PackerDetailFragment;
import com.parsroyal.storemanagement.ui.fragment.PackerInfoFragment;
import com.parsroyal.storemanagement.ui.fragment.bottomsheet.PackerScanGoodBottomSheet;
import com.parsroyal.storemanagement.ui.fragment.bottomsheet.StockGoodCountBottomSheet;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.PackerScanGoodDialogFragment;
import com.parsroyal.storemanagement.ui.fragment.dialogFragment.StockGoodCountDialogFragment;
import com.parsroyal.storemanagement.util.DialogUtil;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.MultiScreenUtility;
import com.parsroyal.storemanagement.util.PreferenceHelper;
import com.parsroyal.storemanagement.util.RtlGridLayoutManager;
import com.parsroyal.storemanagement.util.ToastUtil;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.springframework.util.CollectionUtils;

public class WarehouseHandling extends AppCompatActivity implements
    StockGoodCountDialogFragment.OnCountStockGoods,
    PackerScanGoodDialogFragment.OnGoodFoundListener {

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
    storeService.checkStockStatus(this, PreferenceHelper.getSelectedStock());
    toolbarTitleTv.setText(String.format("%s : %s", getString(R.string.warehouse_handling),
        PreferenceHelper.getSelectedStockName()));
    setUpRecyclerView();
    loadData();
    fab.setVisibility(View.VISIBLE);
  }

  private void loadData() {
    String selection = StockGood.COL_ASN + "=?";
    String[] args = {String.valueOf(PreferenceHelper.getSelectedStock())};
    goods = stockGoodDaoImpl.retrieveAll(selection, args, null, null, null);
    if (Empty.isNotEmpty(goods)) {
      updateList();
      bottomBar.setVisibility(View.VISIBLE);
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
    } else if (event instanceof DataTransferSuccessEvent) {
      handleSuccess((DataTransferSuccessEvent) event);
    } else if (event instanceof DataTransferErrorEvent) {
      handleFailed((DataTransferErrorEvent) event);
    }
  }

  private void handleFailed(DataTransferErrorEvent event) {
    ToastUtil.toastError(this, event.getMessage());
  }

  private void handleSuccess(DataTransferSuccessEvent event) {
    if (event.getStatusCode() == StatusCodes.SUCCESS) {
      ToastUtil.toastMessage(this, R.string.data_transfered_successfully);
    } else if (event.getStatusCode() == StatusCodes.UPDATE) {
      //Show update
    } else if (event.getStatusCode() == StatusCodes.NO_DATA_ERROR) {
      ToastUtil.toastMessage(this, "هیچ اطلاعات ارسال نشده ای وجود ندارد");
    }
  }

  @Override
  public void update(StockGood good) {
    good.setStatus(SendStatus.NEW.getId());
    stockGoodDaoImpl.update(good);
    loadData();
  }

  private void sortByCounted() {
    Collections.sort(goods, (g1, g2) -> ObjectUtils.compare(g1.getCounted(), g2.getCounted()));
  }

  private void updateList() {

    sortByCounted();
    adapter = new StockGoodsAdapter(this, goods);
    list.setAdapter(adapter);
    list.setVisibility(View.VISIBLE);
    bottomBar.setVisibility(View.VISIBLE);
  }

  private void setUpRecyclerView() {
    if (MultiScreenUtility.isTablet(this)) {
      RtlGridLayoutManager rtlGridLayoutManager = new RtlGridLayoutManager(this, 2);
      list.setLayoutManager(rtlGridLayoutManager);
    } else {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
      list.setLayoutManager(linearLayoutManager);
    }

    list.addOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0) {
          fab.setVisibility(View.GONE);
        } else {
          fab.setVisibility(View.VISIBLE);
        }
      }
    });
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
//        openSearchialog();
        break;
      case R.id.barcode_img:
        showScannerDialog();
        break;
    }
  }

  private void showNewCountingDialog() {

    if (Empty.isNotEmpty(goods)) {
      DialogUtil.showConfirmDialog(this, getString(R.string.warning),
          "با دریافت اطلاعات جدید، شمارش قبلی حذف خواهد شد. مطمئن هستید؟", (dialog, which) -> {
            stockGoodDaoImpl.deleteAll();
            receiveData();
          });
    } else {
      stockGoodDaoImpl.deleteAll();
      receiveData();
    }
  }


  private void submitOrder() {
    DialogUtil.showConfirmDialog(this, "ثبت شمارش", "آیا مطمئن هستید؟", (dialog, which) -> {
      dialog.cancel();
      startDataTransfer();
    });
  }

  private void startDataTransfer() {
    DialogUtil.showProgressDialog(this, R.string.message_please_wait);
    Thread t = new Thread(
        () -> new DataTransferServiceImpl(WarehouseHandling.this).sendAllStockGoods());
    t.start();
  }

  public void showScannerDialog() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    PackerScanGoodDialogFragment fragment;
    if (MultiScreenUtility.isTablet(this)) {
      fragment = PackerScanGoodBottomSheet.newInstance();
    } else {
      fragment = PackerScanGoodDialogFragment.newInstance();
    }
    fragment.show(ft, "scan_good");
  }

  private void receiveData() {
    DialogUtil.showProgressDialog(this, R.string.message_please_wait);
    storeService.getStockGoods(this, PreferenceHelper.getSelectedStock());
  }

  @Override
  public void found(String goodCode) {

    boolean found = false;
    for (int i = 0; i < goods.size(); i++) {
      StockGood good = goods.get(i);
      if (goodCode.equals(good.getBarcode())) {
        found = true;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        StockGoodCountDialogFragment goodsFilterDialogFragment;
        if (MultiScreenUtility.isTablet(this)) {
          goodsFilterDialogFragment = StockGoodCountBottomSheet.newInstance(good);
        } else {
          goodsFilterDialogFragment = StockGoodCountDialogFragment.newInstance(good);
        }
        goodsFilterDialogFragment.show(ft, "add_good_count");
        break;
      }
    }
    if (!found) {
      ToastUtil.toastMessage(this, "کالا موجود نیست");
    }
  }
}

