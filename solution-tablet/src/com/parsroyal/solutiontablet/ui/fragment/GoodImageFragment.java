package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parsroyal.solutiontablet.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GoodImageFragment extends Fragment {

  @BindView(R.id.good) ImageView goodImg;
  private int imageId;

  public GoodImageFragment() {
    // Required empty public constructor
  }

  public static GoodImageFragment newInstance(int imageId) {
    GoodImageFragment fragment = new GoodImageFragment();
    fragment.imageId = imageId;
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_good_image, container, false);
    ButterKnife.bind(this, view);
    goodImg.setImageResource(imageId);
    return view;
  }
}
