package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.Message;
import com.parsroyal.solutiontablet.ui.adapter.ChatAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.List;

/**
 * Created by shkbhbb on 11/24/18.
 */

public class ChatAdapter extends Adapter<ViewHolder> {

  private Context context;
  private List<Message> messages;
  private LayoutInflater inflater;

  public ChatAdapter(Context context,
      List<Message> messages) {
    this.context = context;
    this.messages = messages;
    this.inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_message, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.setData(position);
  }

  @Override
  public int getItemCount() {
    return messages.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.message_tv)
    TextView messageTv;
    @BindView(R.id.date_tv)
    TextView dateTv;
    @BindView(R.id.message_date_tv)
    TextView messageDateTv;

    private Message message;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(int position) {
      message = messages.get(position);
      messageTv.setText(message.getPushData());
      dateTv.setText(NumberUtil.digitsToPersian(DateUtil.getTime(message.getPushDate(), context)));
      if (position != 0) {
        if (DateUtil.isSameDay(message.getPushDate(), messages.get(position - 1).getPushDate())) {
          messageDateTv.setVisibility(View.GONE);
        } else {
          messageDateTv.setText(DateUtil.getChatDividerDate(message.getPushDate()));
          messageDateTv.setVisibility(View.VISIBLE);
        }
      } else {
        messageDateTv.setVisibility(View.VISIBLE);
        messageDateTv.setText(DateUtil.getChatDividerDate(message.getPushDate()));
      }

    }
  }
}
