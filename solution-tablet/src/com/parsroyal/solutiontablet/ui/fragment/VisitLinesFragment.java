package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.listmodel.VisitLineListModel;
import com.parsroyal.solutiontablet.service.VisitService;
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl;
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.ui.adapter.VisitLinesAdapter;
import java.util.List;

/**
 * Created by Mahyar on 7/6/2015.
 */
public class VisitLinesFragment extends BaseListFragment<VisitLineListModel, VisitLinesAdapter> {

  public static final String TAG = VisitLinesFragment.class.getSimpleName();

  private OldMainActivity context;
  private VisitService visitService;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    context = (OldMainActivity) getActivity();
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
    return (parent, view, position, id) -> {
      VisitLineListModel visitLine = dataModel.get(position);
      Bundle bundle = new Bundle();
      bundle.putLong(Constants.VISITLINE_BACKEND_ID, visitLine.getPrimaryKey());
      context.changeFragment(OldMainActivity.CUSTOMERS_FRAGMENT_ID, bundle, true);
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
    context.changeSidebarItem(OldMainActivity.CUSTOMER_LIST_FRAGMENT_ID);
  }

  @Override
  public int getFragmentId() {
    return OldMainActivity.CUSTOMER_LIST_FRAGMENT_ID;
  }
}
