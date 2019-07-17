package com.parsroyal.solutiontablet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parsroyal.solutiontablet.R
import com.parsroyal.solutiontablet.constants.Constants
import com.parsroyal.solutiontablet.constants.SaleOrderStatus
import com.parsroyal.solutiontablet.data.event.ErrorEvent
import com.parsroyal.solutiontablet.data.listmodel.SaleOrderListModel
import com.parsroyal.solutiontablet.data.model.GoodsDtoList
import com.parsroyal.solutiontablet.data.searchobject.SaleOrderSO
import com.parsroyal.solutiontablet.service.impl.SaleOrderServiceImpl
import com.parsroyal.solutiontablet.ui.activity.MainActivity
import com.parsroyal.solutiontablet.ui.adapter.RejectAdapter
import com.parsroyal.solutiontablet.util.DialogUtil
import com.parsroyal.solutiontablet.util.Empty
import com.parsroyal.solutiontablet.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_return_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ReturnListFragment : BaseFragment() {

  private var isRequestReject: Boolean? = null
  private var parent: VisitDetailFragment? = null
  private var mainActivity: MainActivity? = null
  private var saleOrderService: SaleOrderServiceImpl? = null
  private var visitId: Long = 0
  private val saleOrderSO = SaleOrderSO()
  private var adapter: RejectAdapter? = null

  private val returnList: List<SaleOrderListModel>
    get() {
      if (parent != null) {
        saleOrderSO.customerBackendId = parent!!.customer.backendId
      }
      saleOrderSO.isIgnoreDraft = true
      saleOrderSO.statusId = if (isRequestReject!!) SaleOrderStatus.REQUEST_REJECTED.id else SaleOrderStatus.REJECTED.id
      return saleOrderService!!.findOrders(saleOrderSO)
    }


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_return_list, container, false)
    mainActivity = activity as MainActivity?
    val args = arguments
    saleOrderService = SaleOrderServiceImpl(mainActivity)

    if (args != null) {
      visitId = args.getLong(Constants.VISIT_ID, -1)
    }

    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (parent == null) {
      fab_add_return.hide()
    }
    setUpRecyclerView()
    setClickListeners()

  }

  //set up recycler view
  private fun setUpRecyclerView() {
    adapter = RejectAdapter(mainActivity, returnList, parent == null, visitId, isRequestReject)
    val linearLayoutManager = LinearLayoutManager(activity)
    recycler_view.layoutManager = linearLayoutManager
    recycler_view.adapter = adapter
    if (!Empty.isEmpty(arguments)) {
      recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          super.onScrolled(recyclerView, dx, dy)
          if (dy > 0) {
            fab_add_return.hide()
          } else {
            fab_add_return.show()
          }
        }
      })
    }
  }

  override fun getFragmentId(): Int {
    return 0
  }


  fun setClickListeners() {
    fab_add_return.setOnClickListener {
      parent!!.openOrderDetailFragment(if (isRequestReject!!) SaleOrderStatus.REQUEST_REJECTED_DRAFT.id else SaleOrderStatus.REJECTED_DRAFT.id, false)
    }
  }

  override fun onResume() {
    super.onResume()
    EventBus.getDefault().register(this)
  }

  override fun onPause() {
    super.onPause()
    EventBus.getDefault().unregister(this)
  }

  @Subscribe
  fun getMessage(goodsDtoList: GoodsDtoList) {
    adapter!!.setRejectGoods(goodsDtoList)
  }

  @Subscribe
  fun getMessage(event: ErrorEvent) {
    if (Empty.isNotEmpty(event.message) && event.message == "reject") {

      DialogUtil.dismissProgressDialog()
      ToastUtil.toastError(mainActivity,
          mainActivity!!.getString(R.string.err_reject_order_not_possible))
    }
  }

  companion object {

    fun newInstance(arguments: Bundle?, visitDetailFragment: VisitDetailFragment?,
                    isRequestReject: Boolean): ReturnListFragment {
      val returnListFragment = ReturnListFragment()
      returnListFragment.parent = visitDetailFragment
      returnListFragment.arguments = arguments
      returnListFragment.isRequestReject = isRequestReject
      return returnListFragment
    }
  }
}// Required empty public constructor
