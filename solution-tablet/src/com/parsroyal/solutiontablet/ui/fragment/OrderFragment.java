package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
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
import android.widget.LinearLayout;

import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.entity.Goods;
import com.parsroyal.solutiontablet.data.searchobject.GoodsSo;
import com.parsroyal.solutiontablet.service.GoodsService;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.GoodsAdapter;
import com.parsroyal.solutiontablet.util.Analytics;
import com.parsroyal.solutiontablet.util.CharacterFixUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OrderFragment extends BaseFragment {

  @BindView(R.id.search_img)
  ImageView searchImg;
  @BindView(R.id.search_edt)
  EditText searchEdt;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.no_good_lay)
  LinearLayout noGoodLay;
  @BindView(R.id.bottom_bar)
  LinearLayout bottomBar;

  private boolean isClose = false;
  private List<Goods> goodsList;
  private GoodsService goodsService;
  private GoodsAdapter adapter;
  private GoodsSo goodsSo = new GoodsSo();
  private MainActivity mainActivity;
  private boolean readOnly;

  public OrderFragment() {
    // Required empty public constructor
  }

  public static OrderFragment newInstance() {
    return new OrderFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_order, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    mainActivity.changeTitle(getString(R.string.title_goods_list));
    goodsService = new GoodsServiceImpl(getActivity());
    readOnly = getArguments().getBoolean(Constants.READ_ONLY);
    if (readOnly) {
      bottomBar.setVisibility(View.GONE);
    }
    setUpRecyclerView();
    addSearchListener();
    return view;
  }

  //set up recycler view
  private void setUpRecyclerView() {
    goodsSo.setConstraint("");
    adapter = new GoodsAdapter(mainActivity, goodsService.searchForGoodsList(goodsSo), readOnly);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  private void addSearchListener() {
    searchEdt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString())) {
          isClose = false;
          searchImg.setImageResource(R.drawable.ic_search);
        } else {
          isClose = true;
          searchImg.setImageResource(R.drawable.ic_close_24dp);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
        String constraint = CharacterFixUtil.fixString("%" + s.toString() + "%");
        goodsSo.setConstraint(constraint);
        goodsList = goodsService.searchForGoodsList(goodsSo);
        if (goodsList.size() > 0) {
          recyclerView.setVisibility(View.VISIBLE);
          noGoodLay.setVisibility(View.GONE);
          adapter.update(goodsList);
        } else {
          recyclerView.setVisibility(View.GONE);
          noGoodLay.setVisibility(View.VISIBLE);
        }
        Analytics.logSearch(constraint, "Type", "Goods");

      }
    });
  }

  @Override
  public int getFragmentId() {
    return MainActivity.GOODS_LIST_FRAGMENT_ID;
  }

  @OnClick({R.id.search_img, R.id.bottom_bar})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.search_img:
        if (isClose) {
          searchEdt.setText("");
        }
        break;
      case R.id.bottom_bar:
        mainActivity.changeFragment(MainActivity.ORDER_INFO_FRAGMENT, true);
        break;
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }
}
