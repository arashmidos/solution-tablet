package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.ui.MainActivity;
import java.util.List;

public class VisitLineListAdapter extends Adapter<VisitLineListAdapter.ViewHolder> {

  private List<VisitLineListModel> visitLineList;
  private LayoutInflater inflater;
  private Context context;

  public VisitLineListAdapter(Context context, List<VisitLineListModel> visitLineList) {
    this.context = context;
    this.visitLineList = visitLineList;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_visitline_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    VisitLineListModel model = visitLineList.get(position);
    holder.setData(model, position);
  }

  @Override
  public int getItemCount() {
    return visitLineList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.map_view)
    View mapView;
    @BindView(R.id.visitline_name)
    TextView visitlineName;
    @BindView(R.id.visitline_detail)
    TextView visitlineDetail;
    @BindView(R.id.customer_count)
    TextView customerCount;
    private VisitLineListModel model;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.visitline_lay)
    public void onClick(View view) {
      Bundle bundle = new Bundle();
      bundle.putLong(Constants.VISITLINE_BACKEND_ID, model.getPrimaryKey());
      ((MainActivity) context).changeFragment(MainActivity.PATH_DETAIL_FRAGMENT_ID, bundle, true);
    }

    public void setData(VisitLineListModel model, int position) {
      this.model = model;
      this.position = position;
      visitlineName.setText(model.getTitle());
      visitlineDetail.setText(model.getCode());
      customerCount.setText(String.format(context.getString(R.string.x_customers),
          model.getCustomerCount()));
    }
  }
}
