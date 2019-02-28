package com.parsroyal.storemanagement.biz.impl;

import android.content.Context;
import android.util.Log;
import com.parsroyal.storemanagement.R;
import com.parsroyal.storemanagement.constants.SaleOrderStatus;
import com.parsroyal.storemanagement.constants.StatusCodes;
import com.parsroyal.storemanagement.data.dao.SaleOrderDao;
import com.parsroyal.storemanagement.data.dao.impl.SaleOrderDaoImpl;
import com.parsroyal.storemanagement.data.entity.SaleOrder;
import com.parsroyal.storemanagement.data.event.DataTransferErrorEvent;
import com.parsroyal.storemanagement.data.model.BaseSaleDocument;
import com.parsroyal.storemanagement.data.model.SaleInvoiceDocument;
import com.parsroyal.storemanagement.service.PostDataRestService;
import com.parsroyal.storemanagement.service.ServiceGenerator;
import com.parsroyal.storemanagement.service.VisitService;
import com.parsroyal.storemanagement.util.Empty;
import com.parsroyal.storemanagement.util.NetworkUtil;
import com.parsroyal.storemanagement.util.NumberUtil;
import java.io.IOException;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Arash on 28/12/2017
 */
public class CanceledOrdersDataTransfer {

  private static final String TAG = CanceledOrdersDataTransfer.class.getSimpleName();
  private final SaleOrderDao saleOrderDao;
  protected BaseSaleDocument order;
  protected VisitService visitService;
  protected int success = 0;
  protected int total = 0;
  private Context context;

  public CanceledOrdersDataTransfer(Context context) {
    this.context = context;
    this.saleOrderDao = new SaleOrderDaoImpl(context);
  }

  public String getSuccessfulMessage() {
    return NumberUtil.digitsToPersian(String
        .format(Locale.getDefault(), context.getString(R.string.data_transfered_result),
            String.valueOf(success),
            String.valueOf(total - success)));
  }

  public int getSuccess() {
    return success;
  }

  public BaseSaleDocument getOrder() {
    return order;
  }

  public void setOrder(BaseSaleDocument order) {
    this.order = order;
    this.total++;
  }

  public void exchangeData() {
    if (!NetworkUtil.isNetworkAvailable(context)) {
      EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.NO_NETWORK));
    }

    PostDataRestService restService = ServiceGenerator.createService(PostDataRestService.class);

    SaleInvoiceDocument invoiceDocument = (SaleInvoiceDocument) this.order;
    Call<String> call = restService.cancelOrders(invoiceDocument.getSaleOrderId(),invoiceDocument.getRejectType());

    try {
      Response<String> response = call.execute();
      if (response.isSuccessful()) {
        String cancelResponse = response.body();
        if (Empty.isNotEmpty(cancelResponse) && "1".equals(cancelResponse)) {
          SaleOrder saleOrder = saleOrderDao.retrieve(this.order.getId());
          saleOrder.setStatus(SaleOrderStatus.DELIVERABLE_SENT.getId());
          saleOrderDao.update(saleOrder);
          success++;
//            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.SUCCESS));
        } else {
//            EventBus.getDefault().post(new DataTransferSuccessEvent("", StatusCodes.NO_DATA_ERROR));
        }
      } else {
        try {
          Log.d("TAG", response.errorBody().string());
        } catch (IOException e) {
          e.printStackTrace();
        }
//          EventBus.getDefault().post(new DataTransferErrorEvent(StatusCodes.SERVER_ERROR));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
