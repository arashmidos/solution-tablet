package com.parsroyal.solutiontablet.ui.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.SolutionTabletApplication;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.dao.impl.PositionDaoImpl;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.data.event.GPSEvent;
import com.parsroyal.solutiontablet.data.helper.CommerDatabaseHelper;
import com.parsroyal.solutiontablet.ui.activity.MainActivity;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AdminFragment extends BaseFragment {

  private static int count = 0;
  @BindView(R.id.setting_btn)
  Button settingBtn;
  @BindView(R.id.database_metadata)
  Button databaseMetadata;
  @BindView(R.id.map_data)
  Button mapData;
  @BindView(R.id.live_map_data)
  Button liveMapData;

  @BindView(R.id.button_4)
  Button button4;
  @BindView(R.id.copy_database)
  Button copyDatabase;
  @BindView(R.id.menu_layout)
  LinearLayout menuLayout;
  @BindView(R.id.result)
  TextView result;
  @BindView(R.id.result_layout)
  LinearLayout resultLayout;
  @BindView(R.id.live_result)
  TextView liveResult;
  @BindView(R.id.live_result_layout)
  LinearLayout liveResultLayout;
  @BindView(R.id.root)
  ScrollView root;
  private MainActivity mainActivity;
  private Unbinder unbinder;

  public AdminFragment() {
    // Required empty public constructor
  }

  public static AdminFragment newInstance() {
    return new AdminFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_admin, container, false);
    unbinder = ButterKnife.bind(this, view);
    mainActivity = (MainActivity) getActivity();

    return view;
  }

  @Override
  public int getFragmentId() {
    return MainActivity.ADMIN_FRAGMENT_ID;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick({R.id.setting_btn, R.id.database_metadata, R.id.map_data, R.id.live_map_data,
      R.id.button_4,
      R.id.copy_database})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.setting_btn:
        openSetting();
        break;
      case R.id.database_metadata:
        showDatabaseData();
        break;
      case R.id.map_data:
        showMapData();
        break;
      case R.id.live_map_data:
        showLiveMapData();
        break;
      case R.id.button_4:
        break;
      case R.id.copy_database:
        copyDatabase();
        break;
    }
  }

  private void showMapData() {
    showResultLayout();
    StringBuilder res = new StringBuilder();

    PositionDaoImpl positionDao = new PositionDaoImpl(mainActivity);
    String selection = " " + Position.COL_STATUS + " = ? ";
    String[] args = {String.valueOf(SendStatus.NEW.getId())};
    String[] args2 = {String.valueOf(SendStatus.SENT.getId())};
    int unsent = positionDao.retrieveAll(selection, args, null, null, null).size();
    int sent = positionDao.retrieveAll(selection, args2, null, null, null).size();

    Position p = SolutionTabletApplication.getInstance().getLastSavedPosition();

    res.append(String.format("<p><font color='blue'><b>Last position:</b></font> : %s</br></p>",
        p != null ? p.toString() : "null"));
    Location l = SolutionTabletApplication.getInstance().getLastKnownLocation();
    res.append(String.format("<p><font color='blue'><b>Raw location:</b></font> : %s</br></p>",
        l != null ? l.toString() : "null"));
    res.append(String.format("<p><font color='blue'><b>Total position:</b></font> : %s</br></p>",
        positionDao.count()));
    res.append(String
        .format("<p><font color='blue'><b>Sent/Unsent position:</b></font> : %s / %s</br></p>",
            sent, unsent));

    showResult(res.toString());
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void getMessage(GPSEvent event) {
    liveResult.setText(liveResult.getText() + "\n" + event.toString());
  }


  private void showLiveMapData() {
    menuLayout.setVisibility(View.GONE);
    resultLayout.setVisibility(View.GONE);
    liveResultLayout.setVisibility(View.VISIBLE);
  }

  private void copyDatabase() {
    showResultLayout();

    result.setText("در حال انتقال داده ها...");
    Thread t = new Thread(() -> {

      boolean copied = CommerDatabaseHelper.getInstance(mainActivity).copyDataBase();
      runOnUiThread(() -> {
        if (copied) {
          result.setText(
              result.getText() + "\n" + String.format("داده ها با موفقیت در مسیر\n %s \nذخیره شد",
                  CommerDatabaseHelper.OUTPUT_PATH));
        } else {
          result.setText(result.getText() + "\n" + "خطا در انتقال داده ها");
        }
      });
    });
    t.start();
  }

  private void showResultLayout() {
    menuLayout.setVisibility(View.GONE);
    resultLayout.setVisibility(View.VISIBLE);
  }

  private void showDatabaseData() {
    showResultLayout();

    StringBuilder res = new StringBuilder();

    SQLiteDatabase db = CommerDatabaseHelper.getInstance(getContext()).getReadableDatabase();
    Cursor cursor = db.rawQuery("select name from sqlite_master", null);
    while (cursor.moveToNext()) {
      String key = cursor.getString(0);
      if (!key.toLowerCase().equals(key)) {
        Cursor c2 = db.rawQuery("select count(*) from " + key, null);
        if (c2.moveToNext()) {
          if (key.startsWith("COMMER_")) {
            key = key.substring(7);
          }
          res.append(String.format("<p><font color='blue'><b>%s</b></font> : %s</br></p>",
              key, c2.getString(0)));
        }
        c2.close();
      }
    }
    cursor.close();
    showResult(res.toString());
  }

  public void showResult(String res) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      result.setText(Html.fromHtml(res, Html.FROM_HTML_MODE_COMPACT));
    } else {
      result.setText(Html.fromHtml(res));
    }
  }

  private void openSetting() {
    showResultLayout();

    Map<String, ?> map = SolutionTabletApplication.getPreference().getAll();
    result.setText(Html.fromHtml("<h2>Settings:</h2>"));
    StringBuilder res = new StringBuilder();
    for (Map.Entry<String, ?> entry : map.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue().toString();
      res.append(String.format("<p><font color='blue'><b>%s</b></font> : %s</br></p>", key, value));
    }
    showResult(res.toString());
    /*Thread t = new Thread(() -> {
      while (count < 100) {
        runOnUiThread(() -> {
          result.setText(result.getText() + "\n" + "Hello" + (count++));
          root.fullScroll(View.FOCUS_DOWN);
        });
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

    t.start();*/
  }

  public void onBackPressed() {

    if (resultLayout.getVisibility() == View.VISIBLE) {
      menuLayout.setVisibility(View.VISIBLE);
      resultLayout.setVisibility(View.GONE);
    } else {
      mainActivity.removeFragment(AdminFragment.this);
    }
  }
}
