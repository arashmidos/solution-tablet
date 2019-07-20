package com.parsroyal.solutiontablet.ui.adapter;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.constants.Authority;
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
    @BindView(R.id.navigate_btn)
    TextView navImg;
    @BindView(R.id.select_customer_btn)
    TextView customerList;
    @BindView(R.id.visitline_detail)
    TextView visitlineDetail;
    @BindView(R.id.customer_count)
    TextView customerCount;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.visitline_lay)
    View root;

    private VisitLineListModel model;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @OnClick({ R.id.visitline_lay, R.id.visitline_layout,R.id.select_customer_btn,R.id.navigate_btn})
    public void onClick(View view) {
      switch (view.getId()) {
        case R.id.visitline_lay:
        case R.id.visitline_layout:
          Bundle bundle = new Bundle();
          bundle.putLong(Constants.VISITLINE_BACKEND_ID, model.getPrimaryKey());
          mainActivity.changeFragment(MainActivity.VISITLINE_DETAIL_FRAGMENT_ID, bundle, true);
          break;
        case R.id.select_customer_btn:
          if (SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_PHONE_CUSTOMER)) {
            Bundle clickBundle = new Bundle();
            clickBundle.putBoolean(Constants.IS_CLICKABLE, true);
            mainActivity.changeFragment(MainActivity.CUSTOMER_SEARCH_FRAGMENT, clickBundle, true);
          }
          break;
        case R.id.navigate_btn:
          if (model.getCustomerCount() > 100) {
            Toast.makeText(mainActivity, "تعداد مشتریان بیش از حد مجاز است", Toast.LENGTH_LONG)
                .show();
          } else {
            Bundle args = new Bundle();
            args.putLong(Constants.VISITLINE_BACKEND_ID, model.getPrimaryKey());
            args.putString(Constants.VISITLINE_NAME, model.getTitle());
            mainActivity.changeFragment(MainActivity.NAVIGATE_BASE_FRAGMENT,args,true);
          }
      }
    }

    public void setData(VisitLineListModel model, int position) {
      this.model = model;
      this.position = position;
      //Manual VisitLine
      if (model.getPrimaryKey().equals(0L)) {
        customerList.setVisibility(View.VISIBLE);
        navImg.setVisibility(View.GONE);
        root.setBackgroundColor(ContextCompat.getColor(mainActivity,R.color.navigation_card));
        visitlineName.setTextColor(ContextCompat.getColor(mainActivity,R.color.white));
        customerCount.setTextColor(ContextCompat.getColor(mainActivity,R.color.white70));
        if (!SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_PHONE_CUSTOMER)) {
          customerList.setEnabled(false);
        }
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
        customerCount.setTextColor(ContextCompat.getColor(mainActivity,R.color.gray_75));
        customerList.setVisibility(View.GONE);
        root.setBackgroundColor(ContextCompat.getColor(mainActivity,R.color.white));
        visitlineName.setTextColor(ContextCompat.getColor(mainActivity,R.color.black));

        navImg.setVisibility(View.VISIBLE);
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
