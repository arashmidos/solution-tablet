package com.parsroyal.solutiontablet.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.StoreRestServiceImpl;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.PackerEvent;
import com.parsroyal.solutiontablet.data.model.Packer;
import com.parsroyal.solutiontablet.data.model.SelectOrderRequest;
import com.parsroyal.solutiontablet.ui.adapter.CustomerDetailViewPagerAdapter;
import com.parsroyal.solutiontablet.ui.fragment.PackerDetailFragment;
import com.parsroyal.solutiontablet.ui.fragment.PackerInfoFragment;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

public class PackerActivity extends AppCompatActivity {


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
  private CustomerDetailViewPagerAdapter viewPagerAdapter;
  private PackerDetailFragment detailFragment;
  private PackerInfoFragment infoFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_packer);
    ButterKnife.bind(this);

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

        } else {
          fabSelectOrder.setVisibility(View.VISIBLE);
          searchImg.setVisibility(View.VISIBLE);
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

  @Subscribe
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
      Packer packer = ((PackerEvent) event).getDetailList();
      setData(packer);
    }
  }

  private void setData(Packer packer) {
    Timber.i(packer.toString());

    if (detailFragment != null) {
      detailFragment.update(packer);
    }
//      recyclerView.hideShimmerAdapter();
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
  }

  @OnClick({R.id.back_img, R.id.fab_select_order, R.id.register_btn, R.id.filter_lay,
      R.id.filter2_lay})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.back_img:
        onBackPressed();
        break;
      case R.id.fab_select_order:
        showChooserDialog();
        break;
      case R.id.register_btn:
        Toast.makeText(this, "ثبت سفارش", Toast.LENGTH_SHORT).show();
        break;
      case R.id.filter_lay:
        Toast.makeText(this, "بعدی", Toast.LENGTH_SHORT).show();
        break;
      case R.id.filter2_lay:
        Toast.makeText(this, "قبلی", Toast.LENGTH_SHORT).show();
        break;
    }
  }

  private void showChooserDialog() {
    Builder dialogBuilder = new Builder(this);
    LayoutInflater inflater1 = getLayoutInflater();
    View dialogView = inflater1.inflate(R.layout.bottom_sheet_order_chooser, null);
    LinearLayout orderLayout = dialogView.findViewById(R.id.order_layout);
    LinearLayout requestLayout = dialogView.findViewById(R.id.request_layout);
    dialogBuilder.setView(dialogView);
    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
    orderLayout.setOnClickListener(v -> {
      new StoreRestServiceImpl().selectOrder(PackerActivity.this, new SelectOrderRequest());
      alertDialog.dismiss();
    });
    requestLayout.setOnClickListener(v -> {
      Toast.makeText(this, "درخواست", Toast.LENGTH_SHORT).show();
      alertDialog.dismiss();
    });
  }

  @OnClick(R.id.search_img)
  public void onViewClicked() {
  }
}
