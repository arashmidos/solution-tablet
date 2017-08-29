package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parsroyal.solutiontablet.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GoodImageFragment extends Fragment {

  @BindView(R.id.good) ImageView goodImg;

  private String imagePath;

  public GoodImageFragment() {
    // Required empty public constructor
  }

  public static GoodImageFragment newInstance(String imagePath) {
    GoodImageFragment fragment = new GoodImageFragment();
    fragment.imagePath = imagePath;

    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_good_image, container, false);
    ButterKnife.bind(this, view);
    Glide.with(getActivity())
        .load(imagePath)
        .error(R.drawable.goods_default)
        .into(goodImg);
    return view;
  }
}
