package com.parsroyal.storemanagement.ui.fragment.dialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.annimon.stream.Stream;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.data.model.LabelValue;
import com.parsroyal.storemanagement.ui.activity.MainActivity;
import com.parsroyal.storemanagement.ui.adapter.SelectableSearchListAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class SearchListDialogFragment extends DialogFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.search_edt)
  EditText searchEdt;
  @BindView(R.id.search_img)
  ImageView searchImg;

  private MainActivity mainActivity;
  private int selectedPosition;
  private FragmentActivity context;
  private ArrayList<LabelValue> model;
  private SelectableSearchListAdapter adapter;
  private LabelValue selectedItem;
  private OnItemSelectedListener parent;

  public SearchListDialogFragment() {
    // Required empty public constructor
  }

  public static SearchListDialogFragment newInstance(ArrayList<LabelValue> model,
      LabelValue selectedItem) {
    SearchListDialogFragment fragment = new SearchListDialogFragment();
    fragment.selectedItem = selectedItem;
    fragment.model = model;
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_dialog_search_list, container, false);
    ButterKnife.bind(this, view);
    context = getActivity();
    setUpRecyclerView();
    loadData();
    onSearch();
    return view;
  }

  private void loadData() {

  }

  private void onSearch() {
    searchEdt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString())) {
          searchImg.setVisibility(View.VISIBLE);
          adapter.updateList(model);
        } else {
          searchImg.setVisibility(View.GONE);

          List<LabelValue> filteredModel = Stream.of(model)
              .filter(l -> l.getLabel().contains(s.toString())).collect(
                  com.annimon.stream.Collectors.toList());
          adapter.updateList(filteredModel);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

  }

  //set up recycler view
  private void setUpRecyclerView() {
    adapter = new SelectableSearchListAdapter(context, getModel(), selectedItem, this, true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private List<LabelValue> getModel() {
    return model;
  }

  public void setSelectedItem(long selectedItem) {
    parent.itemSelected(selectedItem);
    getDialog().dismiss();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      this.parent = (OnItemSelectedListener) context;
    } catch (Exception ex) {
      throw new IllegalArgumentException("You should implement OnItemSelectedListener");
    }
  }

  @OnClick({R.id.close_btn})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.close_btn:
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

  public interface OnItemSelectedListener {

    void itemSelected(long selectedItem);
  }
}
