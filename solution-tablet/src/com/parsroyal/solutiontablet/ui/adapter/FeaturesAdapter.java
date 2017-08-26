package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.FeatureList;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.List;

/**
 * Created by ShakibIsTheBest on 8/4/2017.
 */

public class FeaturesAdapter extends RecyclerView.Adapter<FeaturesAdapter.ViewHolder> {

  private LayoutInflater inflater;
  private Context context;
  private List<FeatureList> features;

  public FeaturesAdapter(Context context, List<FeatureList> features) {
    this.context = context;
    this.features = features;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_features_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    FeatureList feature = features.get(position);
    if (feature.getBadger() != 0) {
      holder.badgerBtn.setText(String.valueOf(feature.getBadger()));
      holder.badgerBtn.setVisibility(View.VISIBLE);
    } else {
      holder.badgerBtn.setVisibility(View.GONE);
    }
    holder.featureImg.setImageResource(feature.getImageId());
    holder.featureTitleTv.setText(feature.getTitle());
    holder.featureLay.setOnClickListener(v -> {
      switch (position) {
        case 0://Paths
          ((MainActivity) context).changeFragment(MainActivity.PATH_FRAGMENT_ID, true);
          break;
        case 1://Customers
          Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show();
          break;
        case 2://reports
          Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show();
          break;
        case 3://Map
          Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show();
          break;
        case 4: //Settings
          ((MainActivity) context).changeFragment(MainActivity.SETTING_FRAGMENT_ID, true);//TODO REMOVE THIS FRAGMENT AFTER LOGIN IMPLEMENTED
          break;
      }
    });
  }

  @Override
  public int getItemCount() {
    return features.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.feature_img)
    ImageView featureImg;
    @BindView(R.id.badger_btn)
    Button badgerBtn;
    @BindView(R.id.feature_title_tv)
    TextView featureTitleTv;
    @BindView(R.id.feature_lay)
    CardView featureLay;


    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
