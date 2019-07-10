package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.Message;
import com.parsroyal.solutiontablet.service.impl.SettingServiceImpl;
import com.parsroyal.solutiontablet.ui.adapter.ChatAdapter.ViewHolder;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;
import com.parsroyal.solutiontablet.util.DateUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys;
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
