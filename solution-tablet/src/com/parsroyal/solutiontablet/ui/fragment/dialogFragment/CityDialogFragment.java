package com.parsroyal.solutiontablet.ui.fragment.dialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.dao.CityDao;
import com.parsroyal.solutiontablet.data.dao.impl.CityDaoImpl;
import com.parsroyal.solutiontablet.data.dao.impl.ProvinceDaoImpl;
import com.parsroyal.solutiontablet.data.model.LabelValue;
import com.parsroyal.solutiontablet.service.impl.BaseInfoServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.CityAdapter;
import com.parsroyal.solutiontablet.ui.fragment.AddCustomerFragment;
import java.util.List;

public class CityDialogFragment extends DialogFragment {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.city_edt)
  EditText cityEdt;
  @BindView(R.id.search_img)
  ImageView searchImg;


  private CityAdapter adapter;
  private MainActivity mainActivity;
  private CityDaoImpl cityDaoImpl;
  private ProvinceDaoImpl provinceDaoImpl;
  private BaseInfoServiceImpl baseInfoService;
  private long provinceId;
  private AddCustomerFragment addCustomerFragment;

  public CityDialogFragment() {
    // Required empty public constructor
  }

  public static CityDialogFragment newInstance(AddCustomerFragment addCustomerFragment,
      long provinceId) {
    CityDialogFragment fragment = new CityDialogFragment();
    fragment.addCustomerFragment = addCustomerFragment;
    fragment.provinceId = provinceId;
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
    View view = inflater.inflate(R.layout.fragment_city_dialog, container, false);
    ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    baseInfoService = new BaseInfoServiceImpl(mainActivity);
    cityDaoImpl = new CityDaoImpl(mainActivity);
    provinceDaoImpl = new ProvinceDaoImpl(mainActivity);
    setUpRecyclerView();
    if (provinceId == -1) {
      cityEdt.setHint(R.string.search_in_provinces);
    } else {
      cityEdt.setHint(R.string.search_in_cities);
    }
    onSearch();
    return view;
  }

  private void onSearch() {
    cityEdt.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString())) {
          searchImg.setVisibility(View.VISIBLE);
          if (provinceId == -1) {
            adapter.updateList(getProvinceModel());
          } else {
            adapter.updateList(getCityModel());
          }
        } else {
          searchImg.setVisibility(View.GONE);
          if (provinceId == -1) {
            adapter.updateList(provinceDaoImpl.searchProvincesLabelValues(s.toString()));
          } else {
            adapter.updateList(
                cityDaoImpl.searchCitiesLabelValuesForProvinceId(provinceId, s.toString()));
          }
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

  }

  //set up recycler view
  private void setUpRecyclerView() {
    if (provinceId == -1) {
      adapter = new CityAdapter(mainActivity, getProvinceModel(), this);
    } else {
      adapter = new CityAdapter(mainActivity, getCityModel(), this);
    }
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setAdapter(adapter);
  }

  public void setSelectedItem(LabelValue selectedItem) {
    addCustomerFragment.setSelectedItem(selectedItem, provinceId != -1L);
    getDialog().dismiss();
  }

  private List<LabelValue> getCityModel() {
    return baseInfoService.getAllCitiesLabelsValues(provinceId);

  }

  private List<LabelValue> getProvinceModel() {
    return baseInfoService.getAllProvincesLabelValues();

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
}
