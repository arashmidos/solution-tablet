package com.parsroyal.solutiontablet.ui.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.maps.GoogleMap;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;

public class VisitLineAdapter extends Adapter<VisitLineAdapter.ViewHolder> {

  private List<VisitLineListModel> visitLineList;
  private LayoutInflater inflater;
  private MainActivity mainActivity;

  public VisitLineAdapter(MainActivity mainActivity, List<VisitLineListModel> visitLineList) {
    this.mainActivity = mainActivity;
    this.visitLineList = visitLineList;
    inflater = LayoutInflater.from(mainActivity);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_visitline_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    VisitLineListModel model = visitLineList.get(position);
    holder.setData(model, position);
  }

  public void update(List<VisitLineListModel> visitLineList) {
    this.visitLineList = visitLineList;
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return visitLineList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.visitline_name)
    TextView visitlineName;
    @BindView(R.id.list_img)
    ImageView customerList;
    @BindView(R.id.visitline_detail)
    TextView visitlineDetail;
    @BindView(R.id.customer_count)
    TextView customerCount;
    @BindView(R.id.divider)
    View divider;
    //    @BindView(R.id.map_item)
//    MapView mapView;
    private VisitLineListModel model;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @OnClick({R.id.list_img, R.id.visitline_lay, R.id.visitline_layout})
    public void onClick(View view) {
      switch (view.getId()) {
        case R.id.visitline_lay:
        case R.id.visitline_layout:
          Bundle bundle = new Bundle();
          bundle.putLong(Constants.VISITLINE_BACKEND_ID, model.getPrimaryKey());
          mainActivity.changeFragment(MainActivity.VISITLINE_DETAIL_FRAGMENT_ID, bundle, true);
          break;
        case R.id.list_img:
          Bundle clickBundle = new Bundle();
          clickBundle.putBoolean(Constants.IS_CLICKABLE, true);
          mainActivity.changeFragment(MainActivity.CUSTOMER_SEARCH_FRAGMENT, clickBundle, true);
          break;
      }
    }

    public void setData(VisitLineListModel model, int position) {
      this.model = model;
      this.position = position;
      //Manual VisitLine
      if (model.getPrimaryKey().equals(0L)) {
        customerList.setVisibility(View.VISIBLE);
        if (model.getCustomerCount() == 0) {
          customerCount.setText(mainActivity.getString(R.string.no_customer_exist));
          divider.setVisibility(View.INVISIBLE);
          visitlineDetail.setVisibility(View.INVISIBLE);
        } else {
          customerCount.setText(NumberUtil.digitsToPersian(String
              .format(mainActivity.getString(R.string.x_customers), model.getCustomerCount())));
          divider.setVisibility(View.VISIBLE);
          visitlineDetail.setVisibility(View.VISIBLE);
        }
      } else {
        customerList.setVisibility(View.GONE);
        customerCount.setText(NumberUtil.digitsToPersian(String
            .format(mainActivity.getString(R.string.x_customers), model.getCustomerCount())));
        divider.setVisibility(View.VISIBLE);
        visitlineDetail.setVisibility(View.VISIBLE);
      }
      visitlineName.setText(NumberUtil.digitsToPersian(model.getTitle()));
      visitlineDetail.setText(NumberUtil.digitsToPersian(model.getCode()));
    }
  }
}
