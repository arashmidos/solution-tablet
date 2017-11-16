package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.VisitInformationDetailType;
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.SingleDataTransferDialogFragment;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import java.util.List;

/**
 * Created by Arash on 11/13/2017.
 */

public class DataTransferAdapter extends RecyclerView.Adapter<DataTransferAdapter.ViewHolder> {

  private final List<VisitInformationDetail> model;
  private final SingleDataTransferDialogFragment parent;
  private LayoutInflater inflater;
  private Context context;
  private int currentPosition;
  private int current;

  public DataTransferAdapter(Context context, SingleDataTransferDialogFragment parent,
      List<VisitInformationDetail> model) {
    this.context = context;
    this.model = model;
    this.parent = parent;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_data_transfer, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    if (position == model.size()) {
      //Visit is always the latest
      holder.dataTypeTv.setText("ارسال اطلاعات بازدید");
      holder.img.setImageResource(R.drawable.ic_visit_24_dp);
    } else {
      holder.setData(model.get(position), position);
      if (!MultiScreenUtility.isTablet(context)) {
        lastItem(position == model.size(), holder);
      }
    }
  }


  private void lastItem(boolean isLastItem, ViewHolder holder) {
    LinearLayout.LayoutParams parameter = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    if (isLastItem) {
      parameter.setMargins(0, 0, 0, 160);
    } else {
      parameter.setMargins(0, 0, 0, 0);
    }
    holder.bottomLine.setVisibility(isLastItem ? View.GONE : View.VISIBLE);
    holder.mainLay.setLayoutParams(parameter);
  }

  @Override
  public int getItemCount() {
    return model.size() + 1;
  }

  public void setCurrent(int current) {
    this.current = current;
    //TODO SHAKIB CURRENT PROGRESS BAR
    //NOTIFY ITEM CHANGED
  }

  public void setFinished(int currentService) {
    //TODO SHAKIB CHANGE ICON TO GREEN CHECK
  }

  public void setError(long currentService) {
    //TODO SHAKIB CHANGE ICON TO RED CROSS
  }

  public void setCurrent(long type) {
    //TODO SHAKIB CURRENT PROGRESS BAR by type
  }

  public void setFinished(long currentModel) {

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
    private VisitInformationDetail visitDetail;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(VisitInformationDetail visitDetail, int position) {
      this.visitDetail = visitDetail;
      this.position = position;

      VisitInformationDetailType type = VisitInformationDetailType
          .getByValue(visitDetail.getType());

      dataTypeTv.setText(String.format(context.getString(R.string.send_x_registered),
          context.getString(type.getTitle())));

      img.setImageResource(type.getDrawable());
    }
  }
}
