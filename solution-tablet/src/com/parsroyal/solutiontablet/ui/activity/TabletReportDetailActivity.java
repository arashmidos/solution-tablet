package com.parsroyal.solutiontablet.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.KpiEvent;
import com.parsroyal.solutiontablet.data.model.KPIDetail;
import com.parsroyal.solutiontablet.service.impl.KPIServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.adapter.ReportDetailAdapter;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TabletReportDetailActivity extends AppCompatActivity {

  @Nullable
  @BindView(R.id.column1)
  TextView column1;
  @Nullable
  @BindView(R.id.column2)
  TextView column2;
  @Nullable
  @BindView(R.id.column3)
  TextView column3;
  @Nullable
  @BindView(R.id.column4)
  TextView column4;
  @Nullable
  @BindView(R.id.title)
  TextView title;
  @BindView(R.id.recycler_view)
  ShimmerRecyclerView recyclerView;
  private ReportDetailAdapter listAdapter;
  private KPIDetail kpiDetail;
  private String reportType;
  private long customerBackendId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_report_detail);
    ButterKnife.bind(this);
    setUpRecyclerView();

    Intent intent = getIntent();
    if (intent != null) {
      kpiDetail = (KPIDetail) intent.getSerializableExtra(Constants.REPORT_ITEM);
      reportType = intent.getStringExtra(Constants.REPORT_TYPE);
      customerBackendId = intent.getLongExtra(Constants.REPORT_CUSTOMER_ID, -1);
      if (kpiDetail == null || Empty.isEmpty(reportType)) {
        setContentView(R.layout.view_error_page);
        return;
      }
    }
  }

  private void setUpRecyclerView() {
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.showShimmerAdapter();
  }

  @OnClick(R.id.back_img)
  public void onViewClicked(View view) {
    onBackPressed();
  }


  @Override
  protected void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
    KPIServiceImpl kpiService = new KPIServiceImpl();
    SettingServiceImpl settingService = new SettingServiceImpl(this);
    if (reportType != null && reportType.equals(Constants.REPORT_SALESMAN)) {
      kpiService.getReport(kpiDetail.getId(),
          Long.parseLong(settingService.getSettingValue(ApplicationKeys.SALESMAN_ID)));
    } else if (Constants.REPORT_CUSTOMER.equals(reportType) && customerBackendId != -1
        && customerBackendId != 0) {
      kpiService.getReport(kpiDetail.getId(), customerBackendId);
    }
  }

  @Subscribe
  public void getMessage(Event event) {
    if (event instanceof ErrorEvent) {
      switch (event.getStatusCode()) {
        case NO_NETWORK:
          ToastUtil.toastError(this, R.string.error_no_network);
          break;
        case NO_DATA_ERROR:
          recyclerView.hideShimmerAdapter();
          ToastUtil.toastMessage(this, R.string.message_no_data_received);
          break;
        default:
          ToastUtil.toastError(this, getString(R.string.error_connecting_server));
      }
    } else if (event instanceof KpiEvent) {
      List<KPIDetail> detailList = ((KpiEvent) event).getDetailList();
      listAdapter = new ReportDetailAdapter(this, detailList, reportType, kpiDetail);
      recyclerView.setAdapter(listAdapter);
      recyclerView.hideShimmerAdapter();
      if (MultiScreenUtility.isTablet(this)) {
        column1.setText(kpiDetail.getColumn1());
        column2.setText(kpiDetail.getColumn2());
        column3.setText(kpiDetail.getColumn3());
        column4.setText(kpiDetail.getColumn4());
        title.setText("عنوان");
      }
    }

  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }
}
