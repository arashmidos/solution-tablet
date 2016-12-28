package com.conta.comer.data.entity;

import com.conta.comer.constants.SaleOrderStatus;
import com.conta.comer.data.model.SaleOrderItemDto;
import com.conta.comer.util.Empty;

/**
 * Created with IntelliJ IDEA.
 * User: m.sefidi
 * Date: 4/11/13
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class SaleOrderItem extends BaseEntity<Long>
{

    public static final String TABLE_NAME = "COMMER_SALE_ORDER_ITEM";
    public static final String COL_ID = "_id";
    public static final String COL_GOODS_BACKEND_ID = "GOODS_BACKEND_ID";
    public static final String COL_GOODS_COUNT = "GOODS_COUNT";
    public static final String COL_AMOUNT = "AMOUNT";
    public static final String COL_SALE_ORDER_ID = "SALE_ORDER_ID";
    public static final String COL_SALE_ORDER_BACKEND_ID = "SALE_ORDER_BACKEND_ID";
    public static final String COL_SELECTED_UNIT = "SELECTED_UNIT";
    public static final String COL_BACKEND_ID = "BACKEND_ID";
    public static final String COL_INVOICE_BACKEND_ID = "INVOICE_BACKEND_ID";

    public static final String CREATE_TABLE_SCRIPT = "CREATE TABLE " + SaleOrderItem.TABLE_NAME + " (" +
            " " + SaleOrderItem.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + SaleOrderItem.COL_GOODS_BACKEND_ID + " INTEGER," +
            " " + SaleOrderItem.COL_GOODS_COUNT + " INTEGER," +
            " " + SaleOrderItem.COL_AMOUNT + " INTEGER," +
            " " + SaleOrderItem.COL_SALE_ORDER_ID + " INTEGER," +
            " " + SaleOrderItem.COL_SALE_ORDER_BACKEND_ID + " INTEGER," +
            " " + SaleOrderItem.COL_SELECTED_UNIT + " INTEGER," +
            " " + SaleOrderItem.COL_BACKEND_ID + " INTEGER," +
            " " + SaleOrderItem.COL_CREATE_DATE_TIME + " TEXT," +
            " " + SaleOrderItem.COL_UPDATE_DATE_TIME + " TEXT," +
            " " + SaleOrderItem.COL_INVOICE_BACKEND_ID + " INTEGER" +
            " );";

    private Long id;
    private Long goodsBackendId;
    private Long goodsCount;
    private Long amount;
    private Long saleOrderId;
    private Long saleOrderBackendId;
    private Long selectedUnit;
    private Long goodsUnit2Count;
    private Long backendId;
    private Long invoiceItemBackendId;
    private Long invoiceBackendId;

    public static String createString(SaleOrderItemDto saleOrderItemDto, Long status)
    {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isRejected = status.equals(SaleOrderStatus.REJECTED_DRAFT.getId()) ||
                status.equals(SaleOrderStatus.REJECTED.getId()) ||
                status.equals(SaleOrderStatus.REJECTED_SENT.getId());
        Goods goods = null;
        if (!isRejected)
        {
            goods = saleOrderItemDto.getGoods();
        }

        stringBuilder.append(saleOrderItemDto.getId());
        stringBuilder.append("#");

        if (isRejected)
        {
            stringBuilder.append(saleOrderItemDto.getGoodsBackendId());
        } else
        {
            stringBuilder.append(goods.getBackendId());
        }
        stringBuilder.append("#");

        stringBuilder.append(saleOrderItemDto.getGoodsCount());
        stringBuilder.append("#");

        stringBuilder.append(saleOrderItemDto.getAmount());
        stringBuilder.append("#");
        if (!isRejected)
        {
            Long unit1Count = goods.getUnit1Count();
            if (Empty.isNotEmpty(unit1Count) && unit1Count != 0)
            {
                Double goodsUnit2Count = Double.valueOf(saleOrderItemDto.getGoodsCount()) / 1000D;
                goodsUnit2Count = goodsUnit2Count / Double.valueOf(unit1Count);
                goodsUnit2Count *= 1000D;
                stringBuilder.append(goodsUnit2Count.longValue());
            } else
            {
                stringBuilder.append("NULL");
            }
        } else
        {
            stringBuilder.append("NULL");
        }
        stringBuilder.append("#");

        stringBuilder.append(saleOrderItemDto.getInvoiceBackendId()).append("#");

        return stringBuilder.toString();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getGoodsBackendId()
    {
        return goodsBackendId;
    }

    public void setGoodsBackendId(Long goodsBackendId)
    {
        this.goodsBackendId = goodsBackendId;
    }

    public Long getGoodsCount()
    {
        return goodsCount;
    }

    public void setGoodsCount(Long goodsCount)
    {
        this.goodsCount = goodsCount;
    }

    public Long getAmount()
    {
        return amount;
    }

    public void setAmount(Long amount)
    {
        this.amount = amount;
    }

    public Long getSaleOrderId()
    {
        return saleOrderId;
    }

    public void setSaleOrderId(Long saleOrderId)
    {
        this.saleOrderId = saleOrderId;
    }

    public Long getSaleOrderBackendId()
    {
        return saleOrderBackendId;
    }

    public void setSaleOrderBackendId(Long saleOrderBackendId)
    {
        this.saleOrderBackendId = saleOrderBackendId;
    }

    public Long getSelectedUnit()
    {
        return selectedUnit;
    }

    public void setSelectedUnit(Long selectedUnit)
    {
        this.selectedUnit = selectedUnit;
    }

    public Long getBackendId()
    {
        return backendId;
    }

    public void setBackendId(Long backendId)
    {
        this.backendId = backendId;
    }

    public Long getGoodsUnit2Count()
    {
        return goodsUnit2Count;
    }

    public void setGoodsUnit2Count(Long goodsUnit2Count)
    {
        this.goodsUnit2Count = goodsUnit2Count;
    }

    public Long getInvoiceBackendId()
    {
        return invoiceBackendId;
    }

    public void setInvoiceBackendId(Long invoiceBackendId)
    {
        this.invoiceBackendId = invoiceBackendId;
    }

    public Long getInvoiceItemBackendId()
    {
        return invoiceItemBackendId;
    }

    public void setInvoiceItemBackendId(Long invoiceItemBackendId)
    {
        this.invoiceItemBackendId = invoiceItemBackendId;
    }

    @Override
    public Long getPrimaryKey()
    {
        return id;
    }
}
