package com.parsroyal.storemanagement.ui.adapter;

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
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.data.model.Message;
import com.parsroyal.storemanagement.service.impl.SettingServiceImpl;
import com.parsroyal.storemanagement.ui.adapter.ChatAdapter.ViewHolder;
import com.parsroyal.storemanagement.util.CharacterFixUtil;
import com.parsroyal.storemanagement.util.DateUtil;
import com.parsroyal.storemanagement.util.NumberUtil;
import com.parsroyal.storemanagement.util.constants.ApplicationKeys;
import java.util.List;

/**
 * Created by shkbhbb on 11/24/18.
 */

public class ChatAdapter extends Adapter<ViewHolder> {

  private final SettingServiceImpl settingService;
  private Context context;
  private List<Message> messages;
  private LayoutInflater inflater;

  public ChatAdapter(Context context, List<Message> messages) {
    this.context = context;
    this.messages = messages;
    this.inflater = LayoutInflater.from(context);
    settingService = new SettingServiceImpl();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view;
    if (viewType == 0) {
      //sender and user are the same
      view = inflater.inflate(R.layout.item_message_me, parent, false);
    } else {
      //message is from other users
      view = inflater.inflate(R.layout.item_message, parent, false);
    }
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.setData(position);
  }

  public void addItem(Message message) {
    messages.add(message);
    notifyDataSetChanged();
  }

  @Override
  public int getItemViewType(int position) {
    if (messages.get(position).getSender() == Long
        .parseLong(settingService.getSettingValue(ApplicationKeys.SALESMAN_ID))) {
      return 0;
    } else {
      return 1;
    }
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
      messageTv
          .setText(NumberUtil.digitsToPersian(CharacterFixUtil.fixString(message.getPushData())));
      dateTv.setText(NumberUtil.digitsToPersian(DateUtil.getTime(message.getPushDate(), context)));
      if (position != 0) {
        if (DateUtil.isSameDay(message.getPushDate(), messages.get(position - 1).getPushDate())) {
          messageDateTv.setVisibility(View.GONE);
        } else {
          messageDateTv.setText(
              NumberUtil.digitsToPersian(DateUtil.getChatDividerDate(message.getPushDate())));
          messageDateTv.setVisibility(View.VISIBLE);
        }
      } else {
        messageDateTv.setVisibility(View.VISIBLE);
        messageDateTv.setText(
            NumberUtil.digitsToPersian(DateUtil.getChatDividerDate(message.getPushDate())));
      }
    }
  }
}
