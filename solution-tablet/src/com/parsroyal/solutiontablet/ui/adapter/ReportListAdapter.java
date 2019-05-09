package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.model.KPIDetail;
import com.parsroyal.solutiontablet.ui.activity.MobileReportDetailActivity;
import com.parsroyal.solutiontablet.ui.activity.TabletReportDetailActivity;
import com.parsroyal.solutiontablet.ui.adapter.ReportListAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.ArrayList;
import java.util.List;

public class ReportListAdapter extends Adapter<ViewHolder> {

  private final String reportType;
  private long customerBackendId;
  private AppCompatActivity context;
  private List<KPIDetail> list = new ArrayList<>();
  private LayoutInflater layoutInflater;

  public ReportListAdapter(AppCompatActivity context, List<KPIDetail> list, String reportType,
      long customerBackendId) {
    this.context = context;
    this.list = list;
    this.reportType = reportType;
    this.customerBackendId = customerBackendId;
    this.layoutInflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = layoutInflater.inflate(R.layout.item_report_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setData(position);
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  public void updateList(List<KPIDetail> kpiDetailList) {
    this.list = kpiDetailList;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.report_name_tv)
    TextView reportName;

    private KPIDetail kpiDetail;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(int position) {
      this.kpiDetail = list.get(position);
      if (!TextUtils.isEmpty(kpiDetail.getTitle())) {
        reportName.setText(NumberUtil.digitsToPersian(kpiDetail.getTitle().trim()));
      }
    }

    @OnClick({R.id.root_layout, R.id.report_name_tv})
    public void onViewClicked(View view) {
      switch (view.getId()) {
        case R.id.report_name_tv:
        case R.id.root_layout:
          Intent intent = new Intent(context, MultiScreenUtility.isTablet(context) ?
              TabletReportDetailActivity.class : MobileReportDetailActivity.class);
          intent.putExtra(Constants.REPORT_ITEM, kpiDetail);
          intent.putExtra(Constants.REPORT_TYPE, reportType);
          intent.putExtra(Constants.REPORT_CUSTOMER_ID, customerBackendId);
          context.startActivity(intent);
          break;
      }
    }
  }
}
