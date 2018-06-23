package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.StringAdapter;
import java.util.List;

public class GiftResultDialogFragment extends DialogFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.toolbar_title)
  TextView toolbarTitle;
  @BindView(R.id.submit_btn)
  Button submitButton;

  private LabelValue selectedItem;
  private StringAdapter adapter;
  private MainActivity mainActivity;
  private List<String> giftResult;

  public GiftResultDialogFragment() {
    // Required empty public constructor
  }

  public static GiftResultDialogFragment newInstance(List<String> giftResult) {
    GiftResultDialogFragment fragment = new GiftResultDialogFragment();
    fragment.giftResult = giftResult;
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_gift_result_dialog, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    setUpRecyclerView();

    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new StringAdapter(mainActivity, getModel());
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<String> getModel() {
    return giftResult;
  }

  @OnClick({R.id.close, R.id.submit_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close:
        getDialog().dismiss();
        break;
      case R.id.submit_btn:
        getDialog().dismiss();
        break;
    }
  }

  @Override
  public void onDestroyView() {
    //workaround for this issue: https://code.google.com/p/android/issues/detail?id=17423 (unable to retain instance after configuration change)
    if (getDialog() != null && getRetainInstance()) {
      getDialog().setDismissMessage(null);
    }
    super.onDestroyView();
  }
}
