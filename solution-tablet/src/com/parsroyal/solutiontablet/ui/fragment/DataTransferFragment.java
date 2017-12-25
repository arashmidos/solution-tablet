package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.constants.StatusCodes;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.event.GPSEvent;
import com.parsroyal.solutiontablet.exception.BusinessException;
import com.parsroyal.solutiontablet.exception.UnknownSystemException;
import com.parsroyal.solutiontablet.service.DataTransferService;
import com.parsroyal.solutiontablet.service.impl.DataTransferServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.observer.ResultObserver;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.Logger;
import com.parsroyal.solutiontablet.util.ToastUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Mahyar on 6/15/2015.
 */
public class DataTransferFragment extends BaseFragment implements ResultObserver {

  public static final String TAG = DataTransferFragment.class.getSimpleName();
  @BindView(R.id.getDataBtn)
  Button getDataBtn;
  @BindView(R.id.sendDataBtn)
  Button sendDataBtn;
  @BindView(R.id.get_images_button)
  Button getImagesBtn;
  @BindView(R.id.transferLogTxtV)
  TextView transferLogTxtV;
  @BindView(R.id.transferSv)
  ScrollView transferSv;
  @BindView(R.id.dataTransferPB)
  ProgressBar dataTransferPB;

  private MainActivity mainActivity;
  private DataTransferService dataTransferService;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View dataTransferView = inflater.inflate(R.layout.fragment_data_transfer, null);

    ButterKnife.bind(this, dataTransferView);

    mainActivity = (MainActivity) getActivity();
    dataTransferService = new DataTransferServiceImpl(mainActivity);

    transferSv.fullScroll(View.FOCUS_DOWN);
    transferLogTxtV.setMovementMethod(new ScrollingMovementMethod());

    Bundle args = getArguments();
    mainActivity.changeTitle("دریافت اطلاعات");
    if (Empty.isNotEmpty(args)) {
      String action = args.getString(Constants.DATA_TRANSFER_ACTION);
      switch (action) {
        case Constants.DATA_TRANSFER_GET:
          checkForUnsentData();
          break;
        case Constants.DATA_TRANSFER_SEND_DATA:
          invokeSendData();
          break;
      }
    }
    return dataTransferView;
  }

  private void checkForUnsentData() {
    if (dataTransferService.hasUnsentData()) {
      DialogUtil.showCustomDialog(mainActivity, getString(R.string.warning),
          "شما اطلاعات ارسال نشده دارید که با دریافت دیتای جدید حذف می شوند. آیا میخواهید آنها را ارسال کنید؟",
          getString(R.string.yes), (dialog, which) -> invokeSendData(), getString(R.string.no),
          (dialog, which) -> invokeGetData(), Constants.ICON_WARNING);
    }else{
      invokeGetData();
    }
  }

  private void invokeSendData() {
    mainActivity.changeTitle(getString(R.string.send_data));
    dataTransferPB.setVisibility(View.VISIBLE);
    Analytics.logContentView("Send Data", "Data Transfer");
    enableButtons(false);
    Thread thread = new Thread(() ->
    {
      try {
        dataTransferService.sendAllData(DataTransferFragment.this);
      } catch (final BusinessException ex) {
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), ex));
      } catch (final Exception ex) {
        Logger.sendError("Data transfer", "Error in send data " + ex.getMessage());
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), new UnknownSystemException(ex)));
      }
    });

    thread.start();
  }

  private void enableButtons(boolean status) {
    getDataBtn.setClickable(status);
    getDataBtn.setEnabled(status);
    sendDataBtn.setClickable(status);
    sendDataBtn.setEnabled(status);
    getImagesBtn.setClickable(status);
    getImagesBtn.setEnabled(status);
  }

  private void invokeGetData() {
    mainActivity.changeTitle(getString(R.string.get_data));
    dataTransferPB.setVisibility(View.VISIBLE);
    Analytics.logContentView("Get Data", "Data Transfer");
    enableButtons(false);
    Thread thread = new Thread(() ->
    {
      try {
        dataTransferService.getAllData();
      } catch (final BusinessException ex) {
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), ex));
      } catch (final Exception ex) {
        Logger.sendError("Data Transfer", "Error in get data" + ex.getMessage());
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), new UnknownSystemException(ex)));
      }
    });

    thread.start();
    publishResult(getString(R.string.message_transferring_goods_images_data));
    getGoodsImages();
  }

  @Override
  public void publishResult(final BusinessException ex) {
    runOnUiThread(() ->
    {
      transferLogTxtV.append(getErrorString(ex) + "\n\n");
      transferSv.scrollTo(0, transferSv.getBottom());
    });
  }

  @Override
  public void publishResult(final String message) {
    runOnUiThread(() ->
    {
      transferLogTxtV.append(message + "\n\n");
      transferSv.scrollTo(0, transferSv.getBottom());
    });
  }

  @Override
  public void finished(boolean result) {
    runOnUiThread(() ->
    {
      dataTransferPB.setVisibility(View.INVISIBLE);
      enableButtons(true);
      transferSv.scrollTo(0, transferSv.getBottom());
    });
  }

  private void getGoodsImages() {
    dataTransferPB.setVisibility(View.VISIBLE);
    Analytics.logContentView("Get Images", "Data Transfer");
    enableButtons(false);
    Thread thread = new Thread(() ->
    {
      try {
        dataTransferService.getGoodsImages(DataTransferFragment.this);
      } catch (final BusinessException ex) {
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), ex));
      } catch (final Exception ex) {
        Logger.sendError("Data Transfer", "Error in get images" + ex.getMessage());
        Log.e(TAG, ex.getMessage(), ex);
        runOnUiThread(() -> ToastUtil.toastError(getActivity(), new UnknownSystemException(ex)));
      }
    });

    thread.start();
  }

  @Override
  public void onResume() {
    super.onResume();

    if (getView() == null) {
      return;
    }

    getView().setFocusableInTouchMode(true);
    getView().requestFocus();
    getView().setOnKeyListener((v, keyCode, event) ->
    {

      /*if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
        if (mainActivity.isMenuEnabled()) {
          mainActivity.onBackPressed();
        }
        return true;
      }*/
      return false;

    });
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(Event event) {
    if (!(event instanceof GPSEvent)) {
      if (event.getStatusCode().equals(StatusCodes.SUCCESS)) {
        publishResult(getString(R.string.goods_images_data_transferred_successfully));
      } else {
        publishResult(getString(R.string.message_exception_in_data_transfer));
        finished(true);
      }
    }
  }

  @Override
  public int getFragmentId() {

    return 0;
  }
}
