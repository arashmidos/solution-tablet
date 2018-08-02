package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.model.FeatureList;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.activity.MobileReportListActivity;
import com.parsroyal.solutiontablet.ui.activity.TabletReportListActivity;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
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
    saleType = new SettingServiceImpl(context).getSettingValue(ApplicationKeys.SETTING_SALE_TYPE);
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
      holder.badgerBtn.setText(NumberUtil.digitsToPersian(String.valueOf(feature.getBadger())));
      holder.badgerBtn.setVisibility(View.VISIBLE);
    } else {
      holder.badgerBtn.setVisibility(View.GONE);
    }
    holder.featureImg.setImageResource(feature.getImageId());
    holder.featureTitleTv.setText(feature.getTitle());
    holder.featureLay.setOnClickListener(v -> {
      if (baseInfoService.getAllProvinces().size() == 0) {

        ToastUtil.toastError(context, R.string.error_message_no_data);
        return;
      }
      switch (position) {
        case 0://Paths
          if (features.get(0).getBadger() == 0) {
            ToastUtil.toastError(context, ApplicationKeys.SALE_DISTRIBUTER.equals(saleType)
                ? R.string.error_no_request_line
                : R.string.error_no_visitline);
            return;
          }
          context.changeFragment(MainActivity.PATH_FRAGMENT_ID, true);
          break;
        case 1://Customers
          context.changeFragment(MainActivity.CUSTOMER_FRAGMENT, true);
          break;
        case 2://reports
          context.changeFragment(MainActivity.REPORT_FRAGMENT, true);
          break;
        case 3://Goods
          Bundle args = new Bundle();
          args.putBoolean(Constants.READ_ONLY, true);
          context.changeFragment(MainActivity.GOODS_LIST_FRAGMENT_ID, args, true);
          break;
        case 4: //Map
          context.changeFragment(MainActivity.USER_TRACKING_FRAGMENT_ID, true);
          break;
        case 5://Questionnaire
          context.changeFragment(MainActivity.ANONYMOUS_QUESTIONNAIRE_FRAGMENT_ID, true);
          break;
        case 6://Report
          Intent intent = new Intent(context,
              MultiScreenUtility.isTablet(context) ? TabletReportListActivity.class
                  : MobileReportListActivity.class);
          intent.putExtra(Constants.REPORT_TYPE, Constants.REPORT_SALESMAN);
          context.startActivity(intent);
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
