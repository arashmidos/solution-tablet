package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.ui.fragment.AddCustomerFragment;
import com.parsroyal.solutiontablet.ui.fragment.OrderInfoFragment;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.CityDialogFragment;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;

/**
 * Created by ShakibIsTheBest on 8/27/2017.
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

  private LayoutInflater inflater;
  private Context context;
  private List<LabelValue> cities;
  private CityDialogFragment cityDialogFragment;

  public CityAdapter(Context context, List<LabelValue> cities,
      CityDialogFragment cityDialogFragment) {
    this.context = context;
    this.cities = cities;
    this.cityDialogFragment = cityDialogFragment;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_city, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setData(position);
  }

  @Override
  public int getItemCount() {
    return cities.size();
  }

  public void updateList(List<LabelValue> cities) {
    this.cities = cities;
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    @BindView(R.id.city_tv)
    TextView cityTv;

    private LabelValue model;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      cityTv.setOnClickListener(this);
    }

    public void setData(int position) {
      this.model = cities.get(position);
      this.position = position;
      cityTv.setText(model.getLabel());
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.city_tv:
          cityDialogFragment.setSelectedItem(model);
          break;
      }
    }
  }
}
