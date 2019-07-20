package com.parsroyal.solutiontablet.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import butterknife.BindView
import butterknife.ButterKnife

import com.parsroyal.solutiontablet.R
import com.parsroyal.solutiontablet.constants.StatusCodes
import com.parsroyal.solutiontablet.data.event.ActionEvent
import com.parsroyal.solutiontablet.data.event.Event
import com.parsroyal.solutiontablet.data.event.UpdateBadgerEvent
import com.parsroyal.solutiontablet.data.model.FeatureList
import com.parsroyal.solutiontablet.service.VisitService
import com.parsroyal.solutiontablet.service.impl.VisitServiceImpl
import com.parsroyal.solutiontablet.ui.activity.MainActivity
import com.parsroyal.solutiontablet.ui.adapter.FeaturesAdapter
import com.parsroyal.solutiontablet.util.MultiScreenUtility
import com.parsroyal.solutiontablet.util.PreferenceHelper
import com.parsroyal.solutiontablet.util.RtlGridLayoutManager
import kotlinx.android.synthetic.main.fragment_features.*

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FeaturesFragment : BaseFragment() {

  private var adapter: FeaturesAdapter? = null
  private var mainActivity: MainActivity? = null
  private var visitService: VisitService? = null

  private val visitLineSize: Int
    get() = visitService!!.allVisitLinesListModel.size

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_features, container, false)

    mainActivity = activity as MainActivity
    visitService = VisitServiceImpl(mainActivity)
    mainActivity!!.changeTitle(getString(R.string.features_list))
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpRecyclerView()
  }

  private fun badgerBtnVisibility() {
    if (PreferenceHelper.getBadger() == 0) {
      mainActivity!!.setBadgerVisibility(View.GONE)
    } else {
      mainActivity!!.setBadgerVisibility(View.VISIBLE)
    }
  }

  //set up recycler view
  private fun setUpRecyclerView() {
    val featureList = FeatureList.getFeatureList(activity!!)
    featureList[0].badger = visitLineSize
    adapter = FeaturesAdapter(activity, featureList)
    val gridLayoutManager: RtlGridLayoutManager
    if (MultiScreenUtility.isTablet(mainActivity!!)) {
      gridLayoutManager = RtlGridLayoutManager(activity, 3)
    } else {
      gridLayoutManager = RtlGridLayoutManager(activity, 2)
    }
    recyclerView.layoutManager = gridLayoutManager
    recyclerView.adapter = adapter
  }

  override fun getFragmentId(): Int {
    return MainActivity.FEATURE_FRAGMENT_ID
  }

  override fun onResume() {
    super.onResume()
    EventBus.getDefault().register(this)
    mainActivity!!.showMenu()
    badgerBtnVisibility()
  }

  override fun onPause() {
    super.onPause()
    EventBus.getDefault().unregister(this)
  }


  @Subscribe(threadMode = ThreadMode.MAIN)
  fun getMessage(event: Event) {
    if (event is ActionEvent) {
      if (event.getStatusCode() == StatusCodes.ACTION_REFRESH_DATA) {
        val featureList = FeatureList.getFeatureList(activity!!)
        featureList[0].badger = visitLineSize
        adapter!!.update(featureList)
      }
    } else if (event is UpdateBadgerEvent) {
      badgerBtnVisibility()
    }
  }

  companion object {

    fun newInstance(): FeaturesFragment {
      return FeaturesFragment()
    }
  }
}// Required empty public constructor
