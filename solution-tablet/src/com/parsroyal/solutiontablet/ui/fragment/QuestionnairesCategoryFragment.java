package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.Constants;
import com.parsroyal.solutiontablet.ui.MainActivity;

public class QuestionnairesCategoryFragment extends BaseFragment {

  Unbinder unbinder;
  private MainActivity mainActivity;

  public QuestionnairesCategoryFragment() {
    // Required empty public constructor
  }

  public static QuestionnairesCategoryFragment newInstance() {
    return new QuestionnairesCategoryFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_questionnaires_category, container, false);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();
    mainActivity.changeTitle(getString(R.string.register_questionnaire));
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick({R.id.good_questionnaire_lay, R.id.general_questionnaire_lay})
  public void onViewClicked(View view) {
    Bundle bundle = getArguments();

    switch (view.getId()) {
      case R.id.good_questionnaire_lay:
        bundle.putString(Constants.QUESTIONNAIRE_CATEGORY, Constants.GOOD_QUESTIONNAIRE);
        break;
      case R.id.general_questionnaire_lay:
        bundle.putString(Constants.QUESTIONNAIRE_CATEGORY, Constants.GENERAL_QUESTIONNAIRE);
        break;
    }
    mainActivity.changeFragment(MainActivity.QUESTIONNAIRE_LIST_FRAGMENT_ID, bundle, true);
  }

  @Override
  public int getFragmentId() {
    return MainActivity.QUESTIONNAIRE_CATEGORY_FRAGMENT_ID;
  }
}
