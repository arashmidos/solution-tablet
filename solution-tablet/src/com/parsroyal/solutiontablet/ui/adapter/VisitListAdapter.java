package com.parsroyal.solutiontablet.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.VisitListModel;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.VisitListAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.Date;
import java.util.List;

/**
 * Created by Arash on 10/23/17.
 */

public class VisitListAdapter extends Adapter<ViewHolder> {

  private final List<VisitListModel> visitListModels;
  private LayoutInflater inflater;
  private MainActivity mainActivity;

  public VisitListAdapter(MainActivity context, List<VisitListModel> visitListModels) {
    this.mainActivity = context;
    this.visitListModels = visitListModels;
    this.inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_all_visit, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.setData(position);
  }

  @Override
  public int getItemCount() {
    return visitListModels.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.date_tv)
    TextView dateTv;
    @BindView(R.id.time_tv)
    TextView timeTv;
    @BindView(R.id.customer_tv)
    TextView customerTv;
    @BindView(R.id.main_lay_rel)
    RelativeLayout mainLayRel;
    @BindView(R.id.status_tv)
    TextView statusTv;

    private VisitListModel listModel;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(int position) {
      this.position = position;
      listModel = visitListModels.get(position);

      customerTv.setText(listModel.getCustomer());
      Date createdDate = DateUtil
          .convertStringToDate(listModel.getVisitDate(), DateUtil.GLOBAL_FORMATTER, "FA");
      String dateString = DateUtil.getFullPersianDate(createdDate);

      dateTv.setText(NumberUtil.digitsToPersian(dateString));

      timeTv.setText(listModel.getFormattedTime());

      statusTv.setText(listModel.isSent() ? R.string.sent : R.string.status_new);
    }
  }
}
