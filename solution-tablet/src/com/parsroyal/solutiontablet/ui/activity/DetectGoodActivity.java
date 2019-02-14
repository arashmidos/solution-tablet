package com.parsroyal.solutiontablet.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.biz.impl.StoreRestServiceImpl;
import com.parsroyal.solutiontablet.data.event.DetectGoodEvent;
import com.parsroyal.solutiontablet.data.event.ErrorEvent;
import com.parsroyal.solutiontablet.data.event.Event;
import com.parsroyal.solutiontablet.data.model.DetectGoodDetail;
import com.parsroyal.solutiontablet.ui.adapter.DetectGoodDetailAdapter;
import com.parsroyal.solutiontablet.util.CameraManager;
import com.parsroyal.solutiontablet.util.DialogUtil;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import com.parsroyal.solutiontablet.util.ToastUtil;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class DetectGoodActivity extends AppCompatActivity {

  @BindView(R.id.scanner)
  DecoratedBarcodeView scanner;
  @BindView(R.id.good_barcode_edt)
  EditText goodBarcodeEdt;
  @BindView(R.id.good_barcode_lay)
  TextInputLayout goodBarcodeLay;
  @BindView(R.id.title)
  TextView title;
  @BindView(R.id.list)
  RecyclerView list;
  @BindView(R.id.list_layout)
  CardView listLayout;
  @BindView(R.id.detect_good_submit)
  Button detectGoodSubmit;
  private DetectGoodDetailAdapter listAdapter;
  private String reportType;
  private long customerBackendId;
  private BeepManager beepManager;
  private String lastText;

  private BarcodeCallback callback = new BarcodeCallback() {
    @Override
    public void barcodeResult(BarcodeResult result) {
      if (result.getText() == null || result.getText().equals(lastText)) {
        // Prevent duplicate scans
        return;
      }

      lastText = result.getText();

      scanner.setStatusText("");
      goodBarcodeEdt.setText(NumberUtil.digitsToPersian(result.getText()));

      beepManager.playBeepSoundAndVibrate();
      sendBarcode();
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detect_good);
    ButterKnife.bind(this);
    initScanner();
    setUpRecyclerView();

    //TODO:REMOVE
    goodBarcodeEdt.setText(NumberUtil.digitsToPersian("7610939002407"));

    if (!CameraManager.checkPermissions(this)) {
      CameraManager.requestPermissions(this);
    }
  }

  private void setUpRecyclerView() {
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
        list.getContext(), layoutManager.getOrientation());
    list.addItemDecoration(dividerItemDecoration);
    list.setLayoutManager(layoutManager);
//    recyclerView.showShimmerAdapter();
  }

  private void sendBarcode() {
    if (Empty.isNotEmpty(goodBarcodeEdt.getText().toString())) {

      DialogUtil.showProgressDialog(this, R.string.message_please_wait);
      new StoreRestServiceImpl()
          .detectGood(this, NumberUtil.digitsToEnglish(goodBarcodeEdt.getText().toString()));
    }
  }

  private void initScanner() {
    Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
    scanner.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
    scanner.initializeFromIntent(getIntent());
    scanner.decodeContinuous(callback);
    scanner.setStatusText(getString(R.string.scan_good_barcode));

    beepManager = new BeepManager(this);
  }

  @OnClick(R.id.back_img)
  public void onViewClicked(View view) {
    onBackPressed();
  }


  @Override
  protected void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
    scanner.pause();
    DialogUtil.dismissProgressDialog();
  }

  @Override
  protected void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
    scanner.resume();
  }

  @Subscribe
  public void getMessage(Event event) {
    DialogUtil.dismissProgressDialog();
    if (event instanceof ErrorEvent) {
      switch (event.getStatusCode()) {
        case NO_NETWORK:
          ToastUtil.toastError(this, R.string.error_no_network);
          break;
        case NO_DATA_ERROR:
//          recyclerView.hideShimmerAdapter();
          ToastUtil.toastMessage(this, R.string.message_no_data_received);
          break;
        default:
          ToastUtil.toastError(this, getString(R.string.error_connecting_server));
      }
    } else if (event instanceof DetectGoodEvent) {
      List<DetectGoodDetail> detailList = ((DetectGoodEvent) event).getDetailList();
      setData(detailList);

    }
  }

  private void setData(List<DetectGoodDetail> detailList) {
    DetectGoodDetail header = detailList.remove(1);
    if (Empty.isNotEmpty(header.getColumn1())) {
      title.setText(NumberUtil.digitsToPersian(header.getColumn1()));
    }
    listAdapter = new DetectGoodDetailAdapter(this, detailList);
    list.setAdapter(listAdapter);
//      recyclerView.hideShimmerAdapter();
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
  }

  @OnClick(R.id.detect_good_submit)
  public void onViewClicked() {
    sendBarcode();
  }
}
