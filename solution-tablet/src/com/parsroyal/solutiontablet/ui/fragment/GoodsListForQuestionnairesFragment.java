package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.data.listmodel.GoodsListModel;
import com.parsroyal.solutiontablet.data.searchobject.GoodsSo;
import com.parsroyal.solutiontablet.service.GoodsService;
import com.parsroyal.solutiontablet.service.impl.GoodsServiceImpl;
import com.parsroyal.solutiontablet.ui.MainActivity;
import com.parsroyal.solutiontablet.ui.adapter.GoodsListForQuestionnairesAdapter;
import java.util.List;

/**
 * Created by Mahyar on 7/29/2015.
 */
public class GoodsListForQuestionnairesFragment extends
    BaseListFragment<GoodsListModel, GoodsListForQuestionnairesAdapter> {

  public static final String TAG = GoodsListForQuestionnairesFragment.class.getSimpleName();

  private MainActivity mainActivity;
  private GoodsService goodsService;
  private Long goodsGroupBackendId;
  private Long questionnaireBackendId;
  private Long visitId;
  private Long customerId;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    try {
      mainActivity = (MainActivity) getActivity();
      goodsService = new GoodsServiceImpl(mainActivity);

      goodsGroupBackendId = getArguments().getLong("ggBi");
      questionnaireBackendId = getArguments().getLong("qnId");
      visitId = getArguments().getLong(Constants.VISIT_ID);
      customerId = getArguments().getLong(Constants.CUSTOMER_ID);

      View view = super.onCreateView(inflater, container, savedInstanceState);
      buttonPanel.setVisibility(View.VISIBLE);
      Button canclButton = (Button) buttonPanel.findViewById(R.id.cancelBtn);
      canclButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mainActivity.removeFragment(GoodsListForQuestionnairesFragment.this);
        }
      });
      return view;
    } catch (Exception e) {
      Log.e(TAG, e.getMessage(), e);
      return mainActivity.getLayoutInflater().inflate(R.layout.view_error_page, null);
    }
  }

  @Override
  protected List<GoodsListModel> getDataModel() {
    GoodsSo goodsSo = new GoodsSo();
    goodsSo.setGoodsGroupBackendId(goodsGroupBackendId);
    return goodsService.searchForGoods(goodsSo);
  }

  @Override
  protected View getHeaderView() {
    return null;
  }

  @Override
  protected GoodsListForQuestionnairesAdapter getAdapter() {
    return new GoodsListForQuestionnairesAdapter(mainActivity, dataModel, goodsGroupBackendId);
  }

  @Override
  protected AdapterView.OnItemClickListener getOnItemClickListener() {
    return new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Bundle args = new Bundle();
        args.putLong("qnId", questionnaireBackendId);
        args.putLong(Constants.VISIT_ID, visitId);
        args.putLong(Constants.CUSTOMER_ID, customerId);
        args.putLong("goodsBackendId", dataModel.get(position).getGoodsBackendId());
        mainActivity.changeFragment(MainActivity.QUESTIONNAIRE_DETAIL_FRAGMENT_ID, args, false);
      }
    };
  }

  @Override
  protected String getClassTag() {
    return TAG;
  }

  @Override
  protected String getTitle() {
    return null;
  }

  @Override
  public int getFragmentId() {
    return MainActivity.GOODS_LIST_FOR_QUESTIONNAIRES_FRAGMENT_ID;
  }
}
