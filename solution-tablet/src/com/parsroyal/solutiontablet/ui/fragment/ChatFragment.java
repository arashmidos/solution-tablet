package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.MessageEvent;
import com.parsroyal.solutiontablet.data.model.Message;
import com.parsroyal.solutiontablet.service.impl.MessageServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.ChatAdapter;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Mahyar on 8/4/2015.
 */
public class ChatFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  Unbinder unbinder;
  private MainActivity mainActivity;

  public static ChatFragment newInstance() {
    return new ChatFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_chat, null);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    mainActivity.changeTitle(getString(R.string.messages));
    MessageServiceImpl messageService = new MessageServiceImpl(mainActivity);
    messageService.getAllMessages();
    DialogUtil.showProgressDialog(getActivity(), getString(R.string.message_please_wait));
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(Event event) {
    if (event instanceof MessageEvent) {
      DialogUtil.dismissProgressDialog();
      setUpRecyclerView(((MessageEvent) event).getMessages());
    } else if (event instanceof DataTransferErrorEvent) {
      DialogUtil.dismissProgressDialog();
      ToastUtil.toastError(mainActivity, getString(R.string.error_no_network));
    } else if (event instanceof DataTransferSuccessEvent) {
      DialogUtil.dismissProgressDialog();
      ToastUtil.toastError(mainActivity, getString(R.string.message_no_data_received));
    }
  }

  private void setUpRecyclerView(List<Message> messages) {
    ChatAdapter chatAdapter = new ChatAdapter(getActivity(), messages);
    recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
    recyclerView.setAdapter(chatAdapter);
    recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.CHAT_FRAGMENT;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

}
