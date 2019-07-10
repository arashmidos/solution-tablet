package com.parsroyal.solutiontablet.ui.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.ui.adapter.VisitActivityAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Arash on 11/13/2017.
 */

public class VisitActivityAdapter extends Adapter<ViewHolder> {

  private ArrayList<VisitInformationDetailType> model = new ArrayList<>();
  private LayoutInflater inflater;
  private Activity context;

  public VisitActivityAdapter(Activity context, HashSet<VisitInformationDetailType> details) {
    this.context = context;
    this.model.addAll(details);
    inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_visit_activity, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    holder.setData(position);
    if (!MultiScreenUtility.isTablet(context)) {
      lastItem(position == model.size() - 1, holder);
    }
  }


  private void lastItem(boolean isLastItem, ViewHolder holder) {
    LayoutParams parameter = new LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    if (isLastItem) {
      parameter.setMargins(0, 0, 0, 160);
    } else {
      parameter.setMargins(0, 0, 0, 0);
    }
    holder.mainLay.setLayoutParams(parameter);
  }

  @Override
  public int getItemCount() {
    return model.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.data_type_tv)
    TextView dataTypeTv;
    @BindView(R.id.main_lay)
    RelativeLayout mainLay;
    @BindView(R.id.bottom_line)
    View bottomLine;
    @BindView(R.id.img)
    ImageView img;

    private int position;
    private VisitInformationDetailType activityDetail;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(int position) {
      this.position = position;
      this.activityDetail = model.get(position);
      dataTypeTv
          .setText(String.format("ثبت %s", context.getString(activityDetail.getVisitTitle())));
      img.setImageResource(activityDetail.getDrawable());
    }
  }
}
