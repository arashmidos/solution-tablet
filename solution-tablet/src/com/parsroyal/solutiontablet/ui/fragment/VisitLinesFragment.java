package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.VisitLinesAdapter;
import java.util.List;

/**
 * Created by Mahyar on 7/6/2015.
 */
public class VisitLinesFragment extends BaseListFragment<VisitLineListModel, VisitLinesAdapter> {

  public static final String TAG = VisitLinesFragment.class.getSimpleName();

  private MainActivity context;
  private VisitService visitService;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    context = (MainActivity) getActivity();
    visitService = new VisitServiceImpl(context);
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  protected List<VisitLineListModel> getDataModel() {
    return visitService.getAllVisitLinesListModel();
  }

  @Override
  protected View getHeaderView() {
    return null;
  }

  @Override
  protected VisitLinesAdapter getAdapter() {
    return new VisitLinesAdapter(context, dataModel);
  }

  @Override
  protected AdapterView.OnItemClickListener getOnItemClickListener() {
    return new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VisitLineListModel visitLine = dataModel.get(position);
        Bundle bundle = new Bundle();
        bundle.putLong("visitLineBackendId", visitLine.getPrimaryKey());
        context.changeFragment(MainActivity.CUSTOMERS_FRAGMENT_ID, bundle, true);
      }
    };
  }

  @Override
  protected String getClassTag() {
    return TAG;
  }

  @Override
  protected String getTitle() {
    return context.getString(R.string.visit_lines);
  }

  @Override
  public void onResume() {
    super.onResume();
    context.changeSidebarItem(MainActivity.CUSTOMER_LIST_FRAGMENT_ID);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.CUSTOMER_LIST_FRAGMENT_ID;
  }
}
