package com.parsroyal.solutiontablet.ui.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.DataTransferErrorEvent;
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.MessageEvent;
import com.parsroyal.solutiontablet.data.event.UpdateBadgerEvent;
import com.parsroyal.solutiontablet.data.model.Message;
import com.parsroyal.solutiontablet.service.impl.MessageServiceImpl;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.ChatAdapter;
import com.parsroyal.solutiontablet.util.BadgerHelper;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;

import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Arash on 8/4/2015.
 */
public class ChatFragment extends BaseFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  Unbinder unbinder;
  @BindView(R.id.message_edt)
  EditText messageEdt;
  @BindView(R.id.send_img)
  ImageView sendImg;
  @BindView(R.id.loading_progress)
  ProgressBar loadingProgress;

  private MainActivity mainActivity;
  private MessageServiceImpl messageService;
  private ChatAdapter chatAdapter;

  public static ChatFragment newInstance() {
    return new ChatFragment();
  }

  @Override
  public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_chat, null);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    BadgerHelper.removeBadge(mainActivity);
    dismissNotif();
    mainActivity.changeTitle(getString(R.string.messages));
    messageService = new MessageServiceImpl(mainActivity);
    messageService.getAllMessages();
    DialogUtil.showProgressDialog(getActivity(), getString(R.string.message_please_wait));
    return view;
  }

  private void dismissNotif() {
    NotificationManager mNotificationManager =
        (NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
    if (mNotificationManager != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        String id = "pars";
        mNotificationManager.deleteNotificationChannel(id);
      } else {
        mNotificationManager.cancel(1);
      }
    }
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
      //get all messages and create message have same event
      //separate them via boolean named isCreateResponse
      //if it's true means the response is result of create message api call
      //else means the response is result of get all message api call
      if (!((MessageEvent) event).isCreateResponse()) {
        DialogUtil.dismissProgressDialog();
        setUpRecyclerView(((MessageEvent) event).getMessages());
      } else {
        loadingProgress.setVisibility(View.GONE);
        sendImg.setVisibility(View.VISIBLE);
        messageEdt.setText("");
        //update adapter via server returned message
        chatAdapter.addItem(((MessageEvent) event).getMessages().get(0));
        recyclerView
            .postDelayed(() -> recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1),
                200);
      }
    } else if (event instanceof DataTransferErrorEvent) {
      DialogUtil.dismissProgressDialog();
      if (loadingProgress.getVisibility() == View.VISIBLE) {
        loadingProgress.setVisibility(View.GONE);
        sendImg.setVisibility(View.VISIBLE);
      }
      if (event.getStatusCode() == StatusCodes.NO_NETWORK) {
        ToastUtil.toastError(mainActivity, getString(R.string.error_no_network));
      } else if (event.getStatusCode() == StatusCodes.SERVER_ERROR) {
        ToastUtil.toastError(mainActivity, getString(R.string.error_connecting_server));
      }
    } else if (event instanceof DataTransferSuccessEvent) {
      DialogUtil.dismissProgressDialog();
      ToastUtil.toastError(mainActivity, getString(R.string.message_no_data_received));
    }
  }

  private void setUpRecyclerView(List<Message> messages) {
    chatAdapter = new ChatAdapter(getActivity(), messages);
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

  @OnClick(R.id.send_img)
  public void onViewClicked() {
    if (!TextUtils.isEmpty(messageEdt.getText().toString().trim())) {
      sendImg.setVisibility(View.GONE);
      loadingProgress.setVisibility(View.VISIBLE);
      String message = NumberUtil.digitsToEnglish(messageEdt.getText().toString().trim());
      messageService.sendMessages(message);
    } else {
      ToastUtil.toastError(mainActivity, getString(R.string.message_is_required));
    }
  }
}
