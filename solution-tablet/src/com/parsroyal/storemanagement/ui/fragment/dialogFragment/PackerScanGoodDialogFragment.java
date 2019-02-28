package com.parsroyal.storemanagement.ui.fragment.dialogFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.ui.activity.PackerActivity;
import com.parsroyal.storemanagement.util.DialogUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PackerScanGoodDialogFragment extends DialogFragment {

  public final String TAG = PackerScanGoodDialogFragment.class.getSimpleName();

  protected PackerActivity parent;
  @BindView(R.id.close_btn)
  ImageView closeBtn;
  @BindView(R.id.scanner)
  DecoratedBarcodeView scanner;
  @BindView(R.id.root)
  LinearLayout root;
  @BindView(R.id.barcode_et)
  TextInputEditText barcodeEt;
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
      barcodeEt.setText(result.getText());
      beepManager.playBeepSoundAndVibrate();
      sendBarcode();
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {
    }
  };
  private PackerActivity packerActivity;
  private Unbinder unbinder;

  public PackerScanGoodDialogFragment() {
    // Required empty public constructor
  }

  public static PackerScanGoodDialogFragment newInstance(PackerActivity parent) {
    PackerScanGoodDialogFragment dialogFragment = new PackerScanGoodDialogFragment();
    dialogFragment.parent = parent;
    return dialogFragment;
  }

  private void sendBarcode() {
    parent.findGoodsByBarcode(barcodeEt.getText().toString().trim());
    dismiss();
  }

  @Override
  public void onPause() {
    super.onPause();
    scanner.pause();
    DialogUtil.dismissProgressDialog();
  }

  @Override
  public void onResume() {
    super.onResume();
    scanner.resume();
  }

  private void initScanner() {
    Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
    scanner.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
    scanner.initializeFromIntent(parent.getIntent());
    scanner.decodeContinuous(callback);
    scanner.setStatusText(getString(R.string.scan_good_barcode));

    beepManager = new BeepManager(parent);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);

    setRetainInstance(true);
  }

  protected String getTAG() {
    return TAG;
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    // Inflate the layout for this fragment
    View view = inflater.inflate(getLayout(), container, false);
    unbinder = ButterKnife.bind(this, view);
    packerActivity = (PackerActivity) getActivity();
    initScanner();
    setData();
    return view;
  }

  private void setData() {

  }

  protected int getLayout() {
    if (!getTAG().contains("Sheet")) {
//      setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
      return R.layout.fragment_dialog_scan_good;
    }

//    return R.layout.fragment_dialog_scan_good_bottom_sheet;
    return R.layout.fragment_dialog_scan_good;

  }

  @OnClick({R.id.close_btn, R.id.submit})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.close_btn:
        dismiss();
        break;
      case R.id.submit:
        sendBarcode();

    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}
