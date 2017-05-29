package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.crashlytics.android.Crashlytics;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.BaseListModel;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.BaseListAdapter;
import com.parsroyal.solutiontablet.ui.component.TabContainer;
import com.parsroyal.solutiontablet.util.Empty;
import java.util.List;

/**
 * Created by Mahyar on 7/6/2015.
 */
public abstract class BaseListFragment<T extends BaseListModel, AD extends BaseListAdapter> extends
    BaseFragment {

  protected ListView dataModelLv;
  protected List<T> dataModel;
  protected EditText searchTxt;
  protected BaseListAdapter adapter;
  protected TabContainer tabContainer;
  protected LinearLayout headerViewLayout;
  protected LinearLayout buttonPanel;
  private MainActivity context;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    try {
      context = (MainActivity) getActivity();

      View view = inflater.inflate(R.layout.fragment_base, null);
      final HorizontalScrollView view1 = (HorizontalScrollView) view.findViewById(R.id.scroll);
      view1.postDelayed(() -> view1.fullScroll(HorizontalScrollView.FOCUS_RIGHT), 100);

      buttonPanel = (LinearLayout) view.findViewById(R.id.button_panel);
      searchTxt = (EditText) view.findViewById(R.id.searchTxt);
      tabContainer = (TabContainer) view.findViewById(R.id.tabContainer);
      dataModelLv = (ListView) view.findViewById(R.id.dataModelLv);
      dataModelLv.setEmptyView(view.findViewById(R.id.emptyElement));
      headerViewLayout = (LinearLayout) view.findViewById(R.id.headerViewLayout);

      View headerView = getHeaderView();
      if (Empty.isNotEmpty(headerView)) {
        headerViewLayout.addView(headerView);
      }

      dataModel = getDataModel();
      if (Empty.isNotEmpty(dataModel)) {
        adapter = getAdapter();
        dataModelLv.setAdapter(adapter);
      }
      dataModelLv.setOnItemClickListener(getOnItemClickListener());

      searchTxt.addTextChangedListener(new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
          if (Empty.isNotEmpty(adapter)) {
            adapter.getFilter().filter(s);
          }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
      });

      return view;
    } catch (Exception e) {
      Crashlytics.log(Log.ERROR, "UI Exception", "Error in creating BaseListFragment " + e.getMessage());
      Log.e(getClassTag(), e.getMessage(), e);
      return getErrorPageView(inflater);
    }
  }

  protected void updateList() {
    adapter = getAdapter();
    dataModelLv.setAdapter(adapter);
  }

  protected abstract List<T> getDataModel();

  protected abstract View getHeaderView();

  protected abstract AD getAdapter();

  protected abstract ListView.OnItemClickListener getOnItemClickListener();

  protected abstract String getClassTag();

  protected abstract String getTitle();

}
