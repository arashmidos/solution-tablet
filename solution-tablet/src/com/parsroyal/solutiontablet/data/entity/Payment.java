package com.parsroyal.solutiontablet.data.entity;

/**
 * Created by Arash on 2016-08-11
 */

public class Payment extends BaseEntity<Long>
{

    public static final String TABLE_NAME = "COMMER_PAYMENT";
    public static final String COL_ID = "_id";
    public static final String COL_CUSTOMER_BACKEND_ID = "CUSTOMER_BACKEND_ID";
    public static final String COL_VISIT_BACKEND_ID = "VISIT_BACKEND_ID";
    public static final String COL_AMOUNT = "AMOUNT";
    public static final String COL_TRACKING_NO = "TRACKING_NO";
    public static final String COL_PAYMENT_TYPE_ID = "PAYMENT_TYPE_ID";
    public static final String COL_CHEQUE_DATE = "CHEQUE_DATE";
    public static final String COL_CHEQUE_ACC_NUMBER = "CHEQUE_ACCOUNT_NUMBER";
    public static final String COL_CHEQUE_NUMBER = "CHEQUE_NUMBER";
    public static final String COL_CHEQUE_BANK = "CHEQUE_BANK";
    public static final String COL_CHEQUE_BRANCH = "CHEQUE_BRANCH";
    public static final String COL_CHEQUE_OWNER = "CHEQUE_OWNER";
    public static final String COL_SALESMAN_ID = "SALESMAN_ID";
    public static final String COL_DESCRIPTION = "DESCRIPTION";
    public static final String COL_BACKEND_ID = "BACKEND_ID";
    public static final String COL_STATUS = "STATUS";

    public static final String CREATE_TABLE_SCRIPT = "CREATE TABLE " + Payment.TABLE_NAME + " (" +
            " " + Payment.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + Payment.COL_CUSTOMER_BACKEND_ID + " INTEGER," +
            " " + Payment.COL_VISIT_BACKEND_ID + " INTEGER," +
            " " + Payment.COL_AMOUNT + " INTEGER," +
            " " + Payment.COL_TRACKING_NO + " TEXT," +
            " " + Payment.COL_PAYMENT_TYPE_ID + " INTEGER," +
            " " + Payment.COL_CHEQUE_DATE + " TEXT," +
            " " + Payment.COL_CHEQUE_ACC_NUMBER + " TEXT," +
            " " + Payment.COL_CHEQUE_NUMBER + " TEXT," +
            " " + Payment.COL_CHEQUE_BANK + " INTEGER," +
            " " + Payment.COL_CHEQUE_BRANCH + " TEXT," +
            " " + Payment.COL_CHEQUE_OWNER + " TEXT," +
            " " + Payment.COL_SALESMAN_ID + " INTEGER," +
            " " + Payment.COL_DESCRIPTION + " TEXT," +
            " " + Payment.COL_BACKEND_ID + " INTEGER," +
            " " + Payment.COL_STATUS + " INTEGER," +
            " " + Payment.COL_CREATE_DATE_TIME + " TEXT," +
            " " + Payment.COL_UPDATE_DATE_TIME + " TEXT" +
            " );";

    private Long id;
    private Long customerBackendId;
    private Long visitBackendId;
    private Long amount;
    private String trackingNo;
    private Long paymentTypeId;
    private String chequeDate;
    private String chequeAccountNumber;
    private String chequeNumber;
    private Long chequeBank;
    private String chequeBranch;
    private Long salesmanId;
    private String description;
    private Long backendId;
    private Long status;
    private String chequeOwner;

    @Override
    public Long getPrimaryKey()
    {
        return id;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getCustomerBackendId()
    {
        return customerBackendId;
    }

    public void setCustomerBackendId(Long customerBackendId)
    {
        this.customerBackendId = customerBackendId;
    }

    public Long getVisitBackendId()
    {
        return visitBackendId;
    }

    public void setVisitBackendId(Long visitBackendId)
    {
        this.visitBackendId = visitBackendId;
    }

    public Long getAmount()
    {
        return amount;
    }

    public void setAmount(Long amount)
    {
        this.amount = amount;
    }

    public Long getPaymentTypeId()
    {
        return paymentTypeId;
    }

    public void setPaymentTypeId(Long paymentTypeId)
    {
        this.paymentTypeId = paymentTypeId;
    }

    public String getChequeDate()
    {
        return chequeDate;
    }

    public void setChequeDate(String chequeDate)
    {
        this.chequeDate = chequeDate;
    }

    public String getChequeAccountNumber()
    {
        return chequeAccountNumber;
    }

    public void setChequeAccountNumber(String chequeAccountNumber)
    {
        this.chequeAccountNumber = chequeAccountNumber;
    }

    public String getChequeNumber()
    {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber)
    {
        this.chequeNumber = chequeNumber;
    }

    public String getChequeBranch()
    {
        return chequeBranch;
    }

    public void setChequeBranch(String chequeBranch)
    {
        this.chequeBranch = chequeBranch;
    }

    public Long getSalesmanId()
    {
        return salesmanId;
    }

    public void setSalesmanId(Long salesmanId)
    {
        this.salesmanId = salesmanId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Long getBackendId()
    {
        return backendId;
    }

    public void setBackendId(Long backendId)
    {
        this.backendId = backendId;
    }

    public Long getStatus()
    {
        return status;
    }

    public void setStatus(Long status)
    {
        this.status = status;
    }

    public String getTrackingNo()
    {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo)
    {
        this.trackingNo = trackingNo;
    }

    public String getChequeOwner()
    {
        return chequeOwner;
    }

    public void setChequeOwner(String chequeOwner)
    {
        this.chequeOwner = chequeOwner;
    }

    public Long getChequeBank()
    {
        return chequeBank;
    }

    public void setChequeBank(Long chequeBank)
    {
        this.chequeBank = chequeBank;
    }
}
