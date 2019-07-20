package com.parsroyal.solutiontablet.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.recyclerview.widget.LinearLayoutManager
import com.parsroyal.solutiontablet.R
import com.parsroyal.solutiontablet.SolutionTabletApplication
import com.parsroyal.solutiontablet.biz.impl.GiftDataTransferBizImpl
import com.parsroyal.solutiontablet.biz.impl.InvoicedOrdersDataTransfer
import com.parsroyal.solutiontablet.biz.impl.OrdersDataTransferBizImpl
import com.parsroyal.solutiontablet.constants.*
import com.parsroyal.solutiontablet.data.dao.impl.SaleOrderDaoImpl
import com.parsroyal.solutiontablet.data.entity.Customer
import com.parsroyal.solutiontablet.data.entity.SaleOrderItem
import com.parsroyal.solutiontablet.data.entity.VisitInformationDetail
import com.parsroyal.solutiontablet.data.event.DataTransferSuccessEvent
import com.parsroyal.solutiontablet.data.event.ErrorEvent
import com.parsroyal.solutiontablet.data.event.Event
import com.parsroyal.solutiontablet.data.event.SendOrderEvent
import com.parsroyal.solutiontablet.data.model.LabelValue
import com.parsroyal.solutiontablet.data.model.SaleOrderDto
import com.parsroyal.solutiontablet.exception.BusinessException
import com.parsroyal.solutiontablet.exception.UnknownSystemException
import com.parsroyal.solutiontablet.service.*
import com.parsroyal.solutiontablet.service.impl.*
import com.parsroyal.solutiontablet.ui.activity.MainActivity
import com.parsroyal.solutiontablet.ui.adapter.PaymentMethodAdapter
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.GiftResultDialogFragment
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.PaymentMethodDialogFragment
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.SmsDialogFragment
import com.parsroyal.solutiontablet.util.*
import com.parsroyal.solutiontablet.util.constants.ApplicationKeys
import kotlinx.android.synthetic.main.fragment_new_order_info.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.*

/**
 * @author Shakib
 */
class OrderInfoFragment : BaseFragment() {

  private val isRequestReject: Boolean
    get() = SaleUtil.isRequestReject(orderStatus)
  private val isRejected: Boolean
    get() = SaleUtil.isRejected(orderStatus)
  private var selectedItem: LabelValue? = null
  private var mainActivity: MainActivity? = null
  private var orderId: Long = 0
  private var saleOrderService: SaleOrderService? = null
  private var order: SaleOrderDto? = null
  private var orderStatus: Long = 0
  private var visitId: Long? = null
  private var customerService: CustomerService? = null
  private var customer: Customer? = null
  private var baseInfoService: BaseInfoService? = null
  private var settingService: SettingService? = null
  private var visitService: VisitService? = null
  private var pageStatus: String? = null
  private var adapter: PaymentMethodAdapter? = null
  private var giftRequestSent: Boolean = false
  private var orderBackendId: Long? = null
  private var visitlineBackendId: Long = 0
  private var newOrderId: Long? = null
  private var rejectType: Long = 0
  private var isCashOrder: Boolean = false
  private var rand: Int = 0
  private var typeId: Long? = null
  private var creditRemained: Double? = null
  private var isComplimentary: Boolean = false

  private
  val detailType: VisitInformationDetailType?
    get() {
      if (isRejected) {
        return VisitInformationDetailType.CREATE_REJECT
      }
      if (isRequestReject) {
        return VisitInformationDetailType.CREATE_REQUEST_REJECT
      }
      when (PreferenceHelper.getSaleType()) {
        ApplicationKeys.SALE_COLD -> return if (isComplimentary) {
          VisitInformationDetailType.DELIVER_FREE_ORDER
        } else {
          VisitInformationDetailType.CREATE_ORDER
        }
        ApplicationKeys.SALE_HOT -> return VisitInformationDetailType.CREATE_INVOICE
        ApplicationKeys.SALE_DISTRIBUTER -> return VisitInformationDetailType.DELIVER_ORDER
      }
      return null
    }

  /*
   @return Proper title for which could be "Rejected", "Order" or "Invoice"
   */
  private val properTitle: String
    get() = if (isRejected || isRequestReject) {
      getString(R.string.title_reject)
    } else if (PreferenceHelper.isVisitor() || isDelivery) {
      getString(R.string.title_order)
    } else {
      getString(R.string.title_factor)
    }

  private val isDelivery: Boolean
    get() = SaleOrderStatus.DELIVERABLE.id == orderStatus || SaleOrderStatus.DELIVERED
        .id == orderStatus

  private val isDisable: Boolean
    get() = (orderStatus == SaleOrderStatus.CANCELED.id
        || orderStatus == SaleOrderStatus.INVOICED.id
        || orderStatus == SaleOrderStatus.SENT.id
        || orderStatus == SaleOrderStatus.SENT_INVOICE.id
        || orderStatus == SaleOrderStatus.REJECTED_SENT.id
        || orderStatus == SaleOrderStatus.FREE_ORDER_SENT.id)

  private val model: List<LabelValue>
    get() = if (isCashOrder) {
      baseInfoService!!.search(BaseInfoTypes.PAYMENT_TYPE.id, "نقد")
    } else {
      baseInfoService!!.getAllBaseInfosLabelValuesByTypeId(
          if (isRejected || isRequestReject) BaseInfoTypes.DELIVERY_RETURN_TYPE.id else BaseInfoTypes.PAYMENT_TYPE.id)
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setData()
    setClickListeners()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_new_order_info, container, false)
    mainActivity = activity as MainActivity
    initializeServices()

    val args = arguments

    if (Empty.isNotEmpty(args)) {
      orderId = args!!.getLong(Constants.ORDER_ID, -1)
      if (orderId.equals(-1)) {
        return inflater.inflate(R.layout.empty_view, container, false)
      }

      pageStatus = args.getString(Constants.PAGE_STATUS)
      order = saleOrderService!!.findOrderDtoById(orderId)
      customer = customerService!!.getCustomerByBackendId(order!!.customerBackendId)
      creditRemained = customer!!.remainedCredit
      orderStatus = order!!.status
      visitId = args.getLong(Constants.VISIT_ID, -1)
      visitlineBackendId = args.getLong(Constants.VISITLINE_BACKEND_ID)
      rejectType = args.getLong(Constants.REJECT_TYPE_ID)
      isCashOrder = args.getBoolean(Constants.CASH_ORDER, false)
      isComplimentary = args.getBoolean(Constants.COMPLIMENTARY, false)

      setPermissions()
      return view
    } else {
      return inflater.inflate(R.layout.empty_view, container, false)
    }
  }

  private fun setPermissions() {
    if (!SolutionTabletApplication.getInstance().hasAccess(Authority.REQUEST_GIFT)) {
      order_gift_layout.visibility = View.GONE
    }
  }

  private fun initializeServices() {
    saleOrderService = SaleOrderServiceImpl(mainActivity)
    customerService = CustomerServiceImpl(mainActivity)
    baseInfoService = BaseInfoServiceImpl(mainActivity)
    settingService = SettingServiceImpl()
    visitService = VisitServiceImpl(mainActivity)
  }

  private fun setData() {
    //Set Payment type
    setSelectedOption()


    mainActivity!!.changeTitle(
        if (isRejected || isRequestReject) getString(R.string.return_info) else getString(R.string.payment_info))
    customer_name_tv.text = NumberUtil.digitsToPersian(customer!!.fullName)

    val total = java.lang.Double.valueOf(order!!.amount!!.toDouble()) / 1000.0

    cost_tv!!.text = NumberUtil.digitsToPersian(
        String.format(Locale.getDefault(), "%s %s", NumberUtil.getCommaSeparated(total),
            getString(R.string.common_irr_currency)))
    val orderDate = order!!.date
    date_tv.text = if (Empty.isNotEmpty(orderDate)) NumberUtil.digitsToPersian(orderDate) else "--"
    val number = order!!.number
    order_code_tv.text = if (Empty.isNotEmpty(number) && order!!.number != 0L)
      NumberUtil.digitsToPersian(number)
    else
      "--"

    val title = properTitle

    date_title_tv.text = String.format(Locale.US, getString(R.string.date_x), title)
    order_code_title_tv.text = String.format(Locale.US, getString(R.string.number_x), title)

    submit_order_btn.text = String.format(Locale.US, getString(R.string.x_order_submit), title)
    description_lay.hint = String.format(Locale.US, getString(R.string.description_x), title)

    if (isRejected || isRequestReject) {
      amount_tv.setText(R.string.amount_to_return)
      payment_type_tv.setText(R.string.reason_to_return)
      submit_order_btn
          .setBackgroundColor(ContextCompat.getColor(mainActivity!!, R.color.register_return))
      if (payment_type_title != null) {
        payment_type_title.setText(R.string.select_reason_to_return)
      }
      description_lay.hint = getString(R.string.reject_description)
      order_gift_layout.visibility = View.GONE
    }
    if (pageStatus == Constants.VIEW) {
      submit_order_btn.text = getString(R.string.close)
      description_edt.isEnabled = false
      order_gift_layout.visibility = View.GONE
    }
    if (MultiScreenUtility.isTablet(mainActivity!!)) {
      if (!isComplimentary) {
        setUpRecyclerView()
      } else {
        if (payment_layout != null) {
          payment_layout.visibility = View.INVISIBLE
        }
      }

      payment_type_tv.visibility = View.GONE
      payment_type_layout.visibility = View.GONE
    }

    if (Empty.isNotEmpty(order!!.description)) {
      description_edt.setText(NumberUtil.digitsToPersian(order!!.description))
    }

    if (isComplimentary) {
      order_gift_layout.visibility = View.GONE
      payment_type_layout.visibility = View.GONE
      payment_type_tv.visibility = View.GONE
    }
  }

  private fun setSelectedOption() {
    if (isComplimentary) {
      return;
    }

    if (selectedItem != null) {
      setPaymentMethod(selectedItem)
    } else {
      val paymentTypeBackendId = if (isRequestReject|| isRejected ) order!!.rejectType else order!!.paymentTypeBackendId
      if (Empty.isNotEmpty(paymentTypeBackendId) && paymentTypeBackendId != 0L) {
        selectedItem = baseInfoService!!
            .getBaseInfoByBackendId(if (isRequestReject|| isRejected ) BaseInfoTypes.DELIVERY_RETURN_TYPE.id else BaseInfoTypes.PAYMENT_TYPE.id, paymentTypeBackendId)
        payment_method_tv.text = NumberUtil.digitsToPersian(selectedItem!!.label)
      }else {
        if (Empty.isNotEmpty(model)) {
          selectedItem = model[0]
          setPaymentMethod(selectedItem)
        }
      }
    }
  }

  fun setPaymentMethod(paymentMethod: LabelValue?) {
    selectedItem = paymentMethod
    payment_method_tv.text = NumberUtil.digitsToPersian(selectedItem!!.label)
  }

  //  R.id.submit_order_btn, R.id.order_gift_layout, R.id.register_gift_tv)
  fun setClickListeners() {
    payment_method_tv.setOnClickListener {
      if (pageStatus != Constants.VIEW && !MultiScreenUtility.isTablet(mainActivity!!)) {
        showPaymentMethodDialog()
      }
    }
    order_gift_layout.setOnClickListener {
      doGiftAction()
    }
    register_gift_tv.setOnClickListener {
      doGiftAction()
    }
    submit_order_btn.setOnClickListener {
      doSubmit()
    }

  }

  private fun doSubmit() {
    if (rand > 9999) {
      calculateRand()
      checkForSmsPermission()
    } else {
      order = saleOrderService!!.findOrderDtoById(orderId)
      if (validateOrderForSave()) {
        if (orderStatus == SaleOrderStatus.REJECTED_DRAFT.id || orderStatus == SaleOrderStatus.REJECTED.id) {
          showSaveOrderConfirmDialog(SaleOrderStatus.REJECTED.id)
        } else if (isDelivery) {
          showSaveOrderConfirmDialog(SaleOrderStatus.DELIVERED.id)
        } else if (isRequestReject) {
          showSaveOrderConfirmDialog(SaleOrderStatus.REQUEST_REJECTED.id)
        } else {
          if (PreferenceHelper.isVisitor()) {
            if (isComplimentary) {
              showSaveOrderConfirmDialog(SaleOrderStatus.FREE_ORDER_DELIVERED.id)
            } else {
              showSaveOrderConfirmDialog(SaleOrderStatus.READY_TO_SEND.id)
            }
          } else {
            showSaveOrderConfirmDialog(SaleOrderStatus.INVOICED.id)
          }
        }
      }
    }
  }

  private fun doGiftAction() {
    if (!SolutionTabletApplication.getInstance().hasAccess(Authority.REQUEST_GIFT)) {
      return
    }
    if (giftRequestSent) {
      register_gift_tv!!.setText(R.string.getting_info)
      GiftDataTransferBizImpl(mainActivity).exchangeData(orderBackendId,
          if (PreferenceHelper.isVisitor()) "0" else "1")
    } else {
      order = saleOrderService!!.findOrderDtoById(orderId)
      if (validateOrderForSave()) {
        order!!.paymentTypeBackendId = selectedItem!!.value
        order!!.description = NumberUtil.digitsToEnglish(description_edt.text.toString())
        order!!.visitlineBackendId = visitlineBackendId
        if (!isDelivery) {
          order!!.status = SaleOrderStatus.GIFT.id
          order!!.salesmanId = java.lang.Long.valueOf(settingService!!.getSettingValue(ApplicationKeys.SALESMAN_ID))
        } else {
          order!!.id = null
          order!!.status = SaleOrderStatus.INVOICE_GIFT.id
        }
        newOrderId = saleOrderService!!.saveOrder(order)
        //            order.setId(newOrderId);
        //save order items
        if (isDelivery) {
          val items = order!!.orderItems
          for (item in items) {

            saleOrderService!!.saveOrderItem(SaleOrderItem(item.goodsBackendId,
                newOrderId, item.invoiceBackendId, item.goodsCount))
          }
        }

        val saleOrderService = SaleOrderServiceImpl(mainActivity)
        val saleOrder = saleOrderService.findOrderDocumentByOrderId(newOrderId)
        register_gift_tv.setText(R.string.sending)
        if (Empty.isNotEmpty(saleOrder)) {
          saleOrder.statusCode = SaleOrderStatus.GIFT.id
          if (isDelivery) {
            val dataTransfer = InvoicedOrdersDataTransfer(
                mainActivity)
            dataTransfer.sendSingleInvoice(saleOrder)
          } else {
            val dataTransfer = OrdersDataTransferBizImpl(
                mainActivity, false)
            dataTransfer.sendSingleOrder(saleOrder)
          }
        }
      }
    }
  }

  private fun showSaveOrderConfirmDialog(statusId: Long?) {
    if (pageStatus != Constants.VIEW) {
      DialogUtil.showConfirmDialog(mainActivity, getString(R.string.title_save_order),
          getString(R.string.message_are_you_sure)) { dialog, which -> saveOrder(statusId) }
    } else {
      mainActivity!!.navigateToFragment(OrderFragment::class.java.simpleName)
    }
  }

  private fun checkForSmsPermission() {
    if (checkSelfPermission(mainActivity!!,
            Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
      // Permission not yet granted. Use requestPermissions().
      // MY_PERMISSIONS_REQUEST_SEND_SMS is an
      // app-defined int constant. The callback method gets the
      // result of the request.
      this.requestPermissions(arrayOf(Manifest.permission.SEND_SMS),
          MY_PERMISSIONS_REQUEST_SEND_SMS)
    } else {
      sendSMS()
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

  fun sendSMS() {
    try {
      val smsManager = SmsManager.getDefault()
      smsManager.sendTextMessage(customer!!.cellPhone, null,
          String.format(
              "مشتری گرامی"
                  + "\n"
                  + "کد تایید سفارش:%s",
              rand.toString()), null, null)
      val ft = mainActivity!!.fragmentManager.beginTransaction()
      val smsDialogFragment = SmsDialogFragment
          .newInstance(mainActivity, this, rand, customer!!.cellPhone, false)
      smsDialogFragment.show(ft, "sms")
    } catch (ex: Exception) {
      ToastUtil.toastError(mainActivity, getString(R.string.error_in_sms_sending))
      showSmsFailedDialog()
    }

  }

  private fun showSmsFailedDialog() {
    val ft = mainActivity!!.fragmentManager.beginTransaction()
    val smsDialogFragment = SmsDialogFragment
        .newInstance(mainActivity, this, rand, customer!!.cellPhone, true)
    smsDialogFragment.show(ft, "sms")
  }

  private fun saveOrder(statusId: Long?) {
    try {

      order!!.status = statusId

      if (isRejected || isRequestReject) {
        //Add reason or reject to orders
        order!!.rejectType = if (Empty.isNotEmpty(selectedItem)) selectedItem!!.value else 0
      } else {
        order!!.paymentTypeBackendId = if (isComplimentary) 0 else selectedItem!!.value
      }
      order!!.description = NumberUtil.digitsToEnglish(description_edt.text.toString())

      //Distributer should not enter his salesmanId.
      if (!isDelivery) {
        order!!.salesmanId = java.lang.Long.valueOf(settingService!!.getSettingValue(ApplicationKeys.SALESMAN_ID))
      } else {
        //        if (newOrderId != null) {
        //          order.setId(newOrderId);
        order!!.invoiceBackendId = orderBackendId
        //        }
        order!!.rejectType = rejectType
      }
      order!!.visitlineBackendId = visitlineBackendId
      typeId = saleOrderService!!.saveOrder(order)
      order!!.id = typeId
      if (visitId != 0L) {
        val visitDetail = VisitInformationDetail(visitId!!, detailType!!, typeId!!)
        visitService!!.saveVisitDetail(visitDetail)
      }

      if (PreferenceHelper.smsEnabled()) {
        calculateRand()
        checkForSmsPermission()
      } else {
        mainActivity!!.navigateToFragment(OrderFragment::class.java.simpleName)
      }
    } catch (ex: BusinessException) {
      Timber.e(ex)
      ToastUtil.toastError(mainActivity, ex)
    } catch (ex: Exception) {
      Timber.e(ex)
      ToastUtil.toastError(mainActivity, UnknownSystemException(ex))
    }

  }

  fun calculateRand(): Int {
    val random = Random()
    rand = random.nextInt(89999) + 10000
    return rand
  }

  private fun validateOrderForSave(): Boolean {
    if (order!!.orderItems.size == 0) {
      ToastUtil.toastError(mainActivity,
          properTitle + getString(R.string.message_x_has_no_item_for_save))
      return false
    }
    if (isComplimentary) {
      return true
    }
    if (Empty.isEmpty(selectedItem)) {
      if (isRejected || isRequestReject) {
        ToastUtil.toastError(mainActivity, getString(R.string.error_no_reject_reason_selected))
      } else {
        ToastUtil.toastError(mainActivity, getString(R.string.error_no_payment_method_selected))
      }
      return false
    }

    val total = java.lang.Double.valueOf(order!!.amount!!.toDouble())

    if (PreferenceHelper.isCreditEnabled() && creditRemained != null && creditRemained!!.compareTo(total) < 0
        && !selectedItem!!.label.contains("نقد")) {
      DialogUtil.showCustomDialog(mainActivity, getString(R.string.warning),
          "اعتبار کافی نیست. ثبت این سفارش فقط با پرداخت نقدی امکان پذیر است", "تایید",
          { dialogInterface, i -> dialogInterface.dismiss() },
          "", null, Constants.ICON_WARNING)
      return false
    }
    return true
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                          grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      MY_PERMISSIONS_REQUEST_SEND_SMS -> {
        if (permissions[0].equals(Manifest.permission.SEND_SMS, ignoreCase = true) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // Permission was granted. Enable sms button.
          sendSMS()
        } else {
          // Permission denied.
          ToastUtil.toastError(mainActivity, getString(R.string.no_sms_permission))
          showSmsFailedDialog()
        }
      }
    }
  }

  private fun showPaymentMethodDialog() {
    if (isDisable) {
      return
    }
    val ft = mainActivity!!.supportFragmentManager.beginTransaction()
    val paymentMethodDialogFragment = PaymentMethodDialogFragment
        .newInstance(this, selectedItem, isRejected || isRequestReject, isCashOrder)
    paymentMethodDialogFragment.show(ft, "payment method")
  }

  //set up recycler view
  private fun setUpRecyclerView() {
    val dataModel = model
    adapter = PaymentMethodAdapter(mainActivity, dataModel, selectedItem, this,
        pageStatus != Constants.VIEW)
    val linearLayoutManager = LinearLayoutManager(activity)

    if (recycler_view != null) {
      recycler_view.layoutManager = linearLayoutManager
      recycler_view.adapter = adapter
      recycler_view.scrollToPosition(dataModel.indexOf(selectedItem))
    }
  }

  override fun getFragmentId(): Int {
    return MainActivity.ORDER_INFO_FRAGMENT
  }

  @Subscribe
  fun getMessage(event: Event) {
    if (event is ErrorEvent) {
      register_gift_tv.setText(R.string.error_connecting_server)
    } else if (event is SendOrderEvent) {
      if (event.getStatusCode() == StatusCodes.SUCCESS) {
        register_gift_tv.setText(R.string.view_gift)
        giftRequestSent = true
        orderBackendId = event.orderId
      }
    } else if (event is DataTransferSuccessEvent) {
      if (event.getStatusCode() == StatusCodes.SUCCESS) {
        register_gift_tv.setText(R.string.view_gift)
        val ft = mainActivity!!.supportFragmentManager.beginTransaction()
        val paymentMethodDialogFragment = GiftResultDialogFragment
            .newInstance(event.giftData)
        paymentMethodDialogFragment.show(ft, "gift")
      } else if (event.getStatusCode() == StatusCodes.NO_DATA_ERROR) {
        register_gift_tv.setText(R.string.retry)
      }
    }
  }

  fun updateOrderSmsConfirmed(smsConfirm: Long) {
    val saleOrderDao = SaleOrderDaoImpl(mainActivity)
    val targetOrder = saleOrderDao.retrieve(typeId)
    targetOrder.smsConfirm = smsConfirm
    saleOrderDao.update(targetOrder)
  }

  companion object {

    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 56

    fun newInstance(): OrderInfoFragment {
      return OrderInfoFragment()
    }
  }
}// Required empty public constructor
