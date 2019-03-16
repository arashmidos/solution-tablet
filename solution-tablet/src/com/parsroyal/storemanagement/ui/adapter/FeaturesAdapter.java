package com.parsroyal.storemanagement.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.data.model.FeatureList;
import com.parsroyal.storemanagement.service.impl.BaseInfoServiceImpl;
import com.parsroyal.storemanagement.service.impl.SettingServiceImpl;
import com.parsroyal.storemanagement.ui.activity.DetectGoodActivity;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.activity.PackerActivity;
import com.parsroyal.storemanagement.ui.activity.WarehouseHandling;
import com.parsroyal.storemanagement.util.NumberUtil;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;
import java.util.List;

/**
 * Created by ShakibIsTheBest on 8/4/2017.
 */

public class FeaturesAdapter extends RecyclerView.Adapter<FeaturesAdapter.ViewHolder> {

  private final BaseInfoServiceImpl baseInfoService;
  private final String saleType;
  private LayoutInflater inflater;
  private MainActivity context;
  private List<FeatureList> features;

  public FeaturesAdapter(Context context, List<FeatureList> features) {
    this.context = (MainActivity) context;
    this.features = features;
    inflater = LayoutInflater.from(context);
    baseInfoService = new BaseInfoServiceImpl(context);
    saleType = new SettingServiceImpl().getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_features_list, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    FeatureList feature = features.get(position);
    if (feature.getBadger() != 0) {
      holder.badgerBtn.setText(NumberUtil.digitsToPersian(String.valueOf(feature.getBadger())));
      holder.badgerBtn.setVisibility(View.VISIBLE);
    } else {
      holder.badgerBtn.setVisibility(View.GONE);
    }
    holder.featureImg.setImageResource(feature.getImageId());
    holder.featureTitleTv.setText(feature.getTitle());
    if (position > 2) {
      holder.featureTitleTv.setTextColor(ContextCompat.getColor(context, R.color.gray));
    } else {
      holder.featureTitleTv.setTextColor(ContextCompat.getColor(context, R.color.primary_dark));
    }
    holder.featureLay.setOnClickListener(v -> {

      switch (position) {
        case 0://DetectGood
          context.startActivity(new Intent(context, DetectGoodActivity.class));
          break;
        case 1://Packers
          context.startActivity(new Intent(context, PackerActivity.class));
          break;
        case 2://انبار گردانی
          context.startActivity(new Intent(context, WarehouseHandling.class));
          break;
        case 3://Goods
//          Bundle args = new Bundle();
//          args.putBoolean(Constants.READ_ONLY, true);
//          context.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, true);
          break;
        case 4: //Map
//          context.changeFragment(MainActivity.USER_TRACKING_FRAGMENT_ID, true);
          break;
        case 5://Questionnaire
//          if (SolutionTabletApplication.getInstance().hasAccess(Authority.ADD_SUB_QUESTIONNAIRE)) {
//            context.changeFragment(MainActivity.ANONYMOUS_QUESTIONNAIRE_FRAGMENT_ID, true);
//          }
          break;
        case 6://Report
//          Intent intent = new Intent(context,
//              MultiScreenUtility.isTablet(context) ? TabletReportListActivity.class
//                  : MobileReportListActivity.class);
//          intent.putExtra(Constants.REPORT_TYPE, Constants.REPORT_SALESMAN);
//          context.startActivity(intent);
          break;
        case 7://Settings
//          context.changeFragment(MainActivity.SETTING_FRAGMENT, true);

          break;
      }
    });
  }

  @Override
  public int getItemCount() {
    return features.size();
  }

  public void update(List<FeatureList> featureList) {
    this.features = featureList;
    notifyDataSetChanged();
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
