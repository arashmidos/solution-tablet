package com.parsroyal.solutiontablet.ui.adapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.KPIDetail;
import com.parsroyal.solutiontablet.ui.adapter.ReportDetailAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.ArrayList;
import java.util.List;

public class ReportDetailAdapter extends Adapter<ViewHolder> {

  private final String reportType;

  private KPIDetail kpi;
  private AppCompatActivity context;
  private List<KPIDetail> list = new ArrayList<>();
  private LayoutInflater layoutInflater;

  public ReportDetailAdapter(AppCompatActivity context, List<KPIDetail> list, String reportType,
      KPIDetail kpi) {
    this.context = context;
    this.list = list;
    this.reportType = reportType;
    this.kpi = kpi;
    this.layoutInflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = layoutInflater.inflate(R.layout.layout_item_report_detail, parent, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    if (MultiScreenUtility.isTablet(context)) {
      holder.setTabletData(position);
    } else {
      holder.setData(position);
    }
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

    @Nullable
    @BindView(R.id.column1_value)
    TextView column1Value;
    @BindView(R.id.column1)
    TextView column1;
    @Nullable
    @BindView(R.id.layout_1)
    LinearLayout layout1;
    @Nullable
    @BindView(R.id.column2_value)
    TextView column2Value;
    @BindView(R.id.column2)
    TextView column2;
    @Nullable
    @BindView(R.id.layout_2)
    LinearLayout layout2;
    @Nullable
    @BindView(R.id.column3_value)
    TextView column3Value;
    @BindView(R.id.column3)
    TextView column3;
    @Nullable
    @BindView(R.id.layout_3)
    LinearLayout layout3;
    @Nullable
    @BindView(R.id.column4_value)
    TextView column4Value;
    @BindView(R.id.column4)
    TextView column4;
    @Nullable
    @BindView(R.id.layout_4)
    LinearLayout layout4;
    @BindView(R.id.title)
    TextView title;
    private KPIDetail kpiDetail;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(int position) {
      this.kpiDetail = list.get(position);
      if (Empty.isNotEmpty(kpiDetail.getTitle())) {
        title.setText(NumberUtil.digitsToPersian(kpiDetail.getTitle()));
      }
      if (Empty.isEmpty(kpi.getColumn1())) {
        layout1.setVisibility(View.GONE);
      } else {
        column1.setText(kpi.getColumn1());
        column1Value.setText(NumberUtil.digitsToPersian(kpiDetail.getColumn1()));
      }
      if (Empty.isEmpty(kpi.getColumn2())) {
        layout2.setVisibility(View.GONE);
      } else {
        column2.setText(kpi.getColumn2());
        column2Value.setText(NumberUtil.digitsToPersian(kpiDetail.getColumn2()));
      }
      if (Empty.isEmpty(kpi.getColumn3())) {
        layout3.setVisibility(View.GONE);
      } else {
        column3.setText(kpi.getColumn3());
        column3Value.setText(NumberUtil.digitsToPersian(kpiDetail.getColumn3()));
      }
      if (Empty.isEmpty(kpi.getColumn4())) {
        layout4.setVisibility(View.GONE);
      } else {
        column4.setText(kpi.getColumn4());
        column4Value.setText(NumberUtil.digitsToPersian(kpiDetail.getColumn4()));
      }
    }

    public void setTabletData(int position) {
      this.kpiDetail = list.get(position);
      if (Empty.isNotEmpty(kpiDetail.getTitle())) {
        title.setText(NumberUtil.digitsToPersian(kpiDetail.getTitle()));
      }
      if (Empty.isNotEmpty(kpi.getColumn1())) {
        column1.setText(NumberUtil.digitsToPersian(kpiDetail.getColumn1()));
      }
      if (Empty.isNotEmpty(kpi.getColumn2())) {
        column2.setText(NumberUtil.digitsToPersian(kpiDetail.getColumn2()));
      }
      if (Empty.isNotEmpty(kpi.getColumn3())) {
        column3.setText(NumberUtil.digitsToPersian(kpiDetail.getColumn3()));
      }
      if (Empty.isNotEmpty(kpi.getColumn4())) {
        column4.setText(NumberUtil.digitsToPersian(kpiDetail.getColumn4()));
      }
    }
  }
}
