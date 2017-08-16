package com.parsroyal.solutiontablet.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.SendStatus;
import com.parsroyal.solutiontablet.data.entity.Position;
import com.parsroyal.solutiontablet.service.PositionService;
import com.parsroyal.solutiontablet.service.impl.PositionServiceImpl;
import com.parsroyal.solutiontablet.ui.OldMainActivity;
import com.parsroyal.solutiontablet.util.LocationUtil;
import java.util.List;

/**
 * Created by Arash on 2016-09-14.
 */
public class TestFragment extends BaseFragment {

  public static final String TAG = TestFragment.class.getSimpleName();
  @BindView(R.id.msg)
  TextView msg;
  private PositionService positionService;
  private List<Position> allPositions;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    FragmentActivity context = getActivity();

    View view = inflater.inflate(R.layout.fragment_test, null);

    ButterKnife.bind(this, view);

    positionService = new PositionServiceImpl(context);
    allPositions = positionService.getAllPositionByStatus(SendStatus.SENT.getId());

    Position lastLocation = null;
    Float bestAccuracy = 1000000.0F;
    Float worstAccuracy = 0.0F;
    Float maxSpeed = 0.0F;
    Float minSpeed = 1000000.0F;
    Float maxDistance = 0.0F;
    Float minDistance = 1000000.0F;
    for (int i = 0; i < allPositions.size(); i++) {
      Position position = allPositions.get(i);
      if (i == 0) {
        addMsg("Total positions: " + allPositions.size());
        addMsg(String
            .format("Start.acc:%s spd:%s, ", position.getAccuracy(), position.getSpeed()));
        lastLocation = position;
      } else {
        Float dista = LocationUtil
            .distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                position.getLatitude(), position.getLongitude());
        addMsg(String
            .format("acc:%s spd:%s, dstnce:%s", position.getAccuracy(), position.getSpeed(),
                dista));

        if (dista > maxDistance) {
          maxDistance = dista;
        }
        if (dista < minDistance) {
          minDistance = dista;
        }
        lastLocation = position;
      }
      if (position.getAccuracy() < bestAccuracy) {
        bestAccuracy = position.getAccuracy();
      }
      if (position.getAccuracy() > worstAccuracy) {
        worstAccuracy = position.getAccuracy();
      }
      if (position.getSpeed() > maxSpeed) {
        maxSpeed = position.getSpeed();
      }
      if (position.getSpeed() < minSpeed && position.getSpeed() != 0.0) {
        minSpeed = position.getSpeed();
      }
    }
    msg.setText(
        "Best accuracy:" + bestAccuracy + " worst accuracy:" + worstAccuracy + "\nMax speed:"
            + maxSpeed + " min speed:" + minSpeed + "\n Max distance:" + maxDistance
            + " Min Distance:" + minDistance + "\n" + msg.getText());
    return view;
  }

  private void addMsg(String format) {
    msg.setText(msg.getText() + format + "\n");
  }

  @Override
  public int getFragmentId() {
    return OldMainActivity.USER_TRACKING_FRAGMENT_ID;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

  }
}
