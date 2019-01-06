package com.parsroyal.solutiontablet.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants.TransferGetOrder;
import com.parsroyal.solutiontablet.constants.Constants.TransferStatus;
import com.parsroyal.solutiontablet.data.model.DataTransferList;
import com.parsroyal.solutiontablet.ui.adapter.DataTransferAdapter.ViewHolder;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.DataTransferDialogFragment;
import com.parsroyal.solutiontablet.util.MultiScreenUtility;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;

/**
 * Created by Arash on 11/13/2017.
 */

public class DataTransferAdapter extends Adapter<ViewHolder> {

  private final List<DataTransferList> model;
  private final DataTransferDialogFragment parent;
  private LayoutInflater inflater;
  private Activity context;
  private int currentPosition;
  private int current = -1;
  private long currentType;
  private int currentService;
  private boolean hasError;

  public DataTransferAdapter(Activity context, DataTransferDialogFragment parent,
      List<DataTransferList> model) {
    this.context = context;
    this.model = model;
    this.parent = parent;
    inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_data_transfer, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    holder.setData(position);
    if (!MultiScreenUtility.isTablet(context)) {
      lastItem(position == model.size() - 1, holder);
    }
    if (model.get(position).getId() == TransferGetOrder.GOODS_IMAGES) {
      holder.extraIcon.setVisibility(View.VISIBLE);
    } else {
      holder.extraIcon.setVisibility(View.GONE);
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
//    holder.bottomLine.setVisibility(isLastItem ? View.GONE : View.VISIBLE);
    holder.mainLay.setLayoutParams(parameter);
  }

  @Override
  public int getItemCount() {
    return model.size();
  }

  public void setCurrent(int current) {
    this.current = current;
    if (model.get(current).getStatus() != TransferStatus.DONE) {
      model.get(current).setStatus(TransferStatus.IN_PROGRESS);
      context.runOnUiThread(this::notifyDataSetChanged);
    }
  }

  public void setDefault(int current) {
    this.current = current;
    if (model.get(current).getStatus() != TransferStatus.DONE) {
      model.get(current).setStatus(TransferStatus.CANCELED);
      context.runOnUiThread(this::notifyDataSetChanged);
    }
  }

  public void setFinished(int currentPosition) {
    this.current = currentPosition;
    model.get(current).setStatus(TransferStatus.DONE);
    context.runOnUiThread(this::notifyDataSetChanged);
  }

  public void setError(int currentPosition) {
    hasError = true;
    model.get(currentPosition).setStatus(TransferStatus.ERROR);
//    model.get(currentPosition).setResult(detail);
    context.runOnUiThread(this::notifyDataSetChanged);
  }

  public void setFinished(int currentPosition, String message) {
    this.current = currentPosition;
    model.get(current).setStatus(TransferStatus.DONE);
    model.get(current).setResult(message);
    context.runOnUiThread(this::notifyDataSetChanged);
  }

  public void setImageFinished() {
    model.get(model.size() - 1).setStatus(TransferStatus.DONE);

    context.runOnUiThread(this::notifyDataSetChanged);
  }

  public void setUpdate(int currentPosition, String message) {
    this.current = currentPosition;
    model.get(current).setResult(message);
    context.runOnUiThread(() -> notifyItemChanged(current));
  }

  public void setError(int currentPosition, String message) {
    hasError = true;
    model.get(currentPosition).setStatus(TransferStatus.ERROR);
    model.get(currentPosition).setResult(message);
    context.runOnUiThread(() -> notifyItemChanged(currentPosition));
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.data_type_tv)
    TextView dataTypeTv;
    @BindView(R.id.message_tv)
    TextView messageTv;
    @BindView(R.id.main_lay)
    RelativeLayout mainLay;
    @BindView(R.id.bottom_line)
    View bottomLine;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.image_progress_bar)
    ProgressBar imageProgressBar;
    @BindView(R.id.extra_icon)
    ImageView extraIcon;

    private int position;
    private DataTransferList transferDetail;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.extra_icon)
    public void onClick(View view) {
//      extraIcon.setVisibility(View.GONE);
//      imageProgressBar.setVisibility(View.VISIBLE);
      model.get(model.size() - 1).setStatus(TransferStatus.IN_PROGRESS);
      notifyDataSetChanged();
      parent.downloadImage();
    }

    public void setData(int position) {
      this.position = position;

      this.transferDetail = model.get(position);

      dataTypeTv.setText(transferDetail.getTitle());

      switch (transferDetail.getStatus()) {
        case TransferStatus.READY:
          progressBar.setVisibility(View.INVISIBLE);
          img.setVisibility(View.VISIBLE);
          img.setImageResource(transferDetail.getImageId());
          break;
        case TransferStatus.IN_PROGRESS:
          progressBar.setVisibility(View.VISIBLE);
          img.setImageResource(transferDetail.getImageId());
          img.setVisibility(View.INVISIBLE);
          messageTv.setText(NumberUtil.digitsToPersian(transferDetail.getResult()));
          break;
        case TransferStatus.ERROR:
          progressBar.setVisibility(View.INVISIBLE);
          img.setImageResource(R.drawable.ic_check_warning_24_dp);
          messageTv.setText(NumberUtil.digitsToPersian(transferDetail.getResult()));
          break;
        case TransferStatus.DONE:
          progressBar.setVisibility(View.INVISIBLE);
          img.setVisibility(View.VISIBLE);
          img.setImageResource(R.drawable.ic_check_circle_24_dp);
          messageTv.setText(NumberUtil.digitsToPersian(transferDetail.getResult()));
          break;
        case TransferStatus.CANCELED:
          progressBar.setVisibility(View.INVISIBLE);
          img.setVisibility(View.VISIBLE);
          img.setImageResource(transferDetail.getImageId());
          messageTv.setText(NumberUtil.digitsToPersian(transferDetail.getResult()));
      }
    }
  }
}
