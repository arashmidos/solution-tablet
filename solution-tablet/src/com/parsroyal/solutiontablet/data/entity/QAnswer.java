package com.parsroyal.solutiontablet.data.entity;

import com.parsroyal.solutiontablet.util.Empty;

/**
 * Created by Mahyar on 7/25/2015.
 */
public class QAnswer extends BaseEntity<Long>
{

    public static final String TABLE_NAME = "COMMER_Q_ANSWER";
    public static final String COL_ID = "_id";
    public static final String COL_QUESTION_BACKEND_ID = "QUESTION_BACKEND_ID";
    public static final String COL_BACKEND_ID = "BACKEND_ID";
    public static final String COL_ANSWER = "ANSWER";
    public static final String COL_CUSTOMER_BACKEND_ID = "CUSTOMER_BACKEND_ID";
    public static final String COL_GOODS_BACKEND_ID = "GOODS_BACKEND_ID";
    public static final String COL_VISIT_ID = "VISIT_ID";
    public static final String COL_VISIT_BACKEND_ID = "VISIT_BACKEND_ID";

    public static final String COL_DATE = "DATE";
    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + QAnswer.TABLE_NAME + " (" +
            " " + QAnswer.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + QAnswer.COL_BACKEND_ID + " INTEGER," +
            " " + QAnswer.COL_QUESTION_BACKEND_ID + " INTEGER," +
            " " + QAnswer.COL_ANSWER + " TEXT," +
            " " + QAnswer.COL_CUSTOMER_BACKEND_ID + " INTEGER," +
            " " + QAnswer.COL_GOODS_BACKEND_ID + " INTEGER," +
            " " + QAnswer.COL_VISIT_ID + " INTEGER," +
            " " + QAnswer.COL_VISIT_BACKEND_ID + " INTEGER," +
            " " + QAnswer.COL_DATE + " TEXT," +
            " " + QAnswer.COL_CREATE_DATE_TIME + " TEXT," +
            " " + QAnswer.COL_UPDATE_DATE_TIME + " TEXT" +
            " );";

    private Long id;
    private Long backendId;
    private Long questionBackendId;
    private String answer;
    private Long customerBackendId;
    private Long goodsBackendId;
    private Long visitBackendId;
    private Long visitId;
    private String date;

    public static String createString(QAnswer qAnswer)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(qAnswer.getId());
        sb.append("&");
        sb.append(qAnswer.getQuestionBackendId());
        sb.append("&");
        sb.append(qAnswer.getAnswer());
        sb.append("&");
        sb.append(qAnswer.getCustomerBackendId());
        sb.append("&");
        sb.append(Empty.isEmpty(qAnswer.getGoodsBackendId()) ? "NULL" : qAnswer.getGoodsBackendId());
        sb.append("&");
        sb.append(qAnswer.getCreateDateTime());

        return sb.toString();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getBackendId()
    {
        return backendId;
    }

    public void setBackendId(Long backendId)
    {
        this.backendId = backendId;
    }

    public Long getQuestionBackendId()
    {
        return questionBackendId;
    }

    public void setQuestionBackendId(Long questionBackendId)
    {
        this.questionBackendId = questionBackendId;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    public Long getCustomerBackendId()
    {
        return customerBackendId;
    }

    public void setCustomerBackendId(Long customerBackendId)
    {
        this.customerBackendId = customerBackendId;
    }

    public Long getGoodsBackendId()
    {
        return goodsBackendId;
    }

    public void setGoodsBackendId(Long goodsBackendId)
    {
        this.goodsBackendId = goodsBackendId;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public Long getVisitBackendId()
    {
        return visitBackendId;
    }

    public void setVisitBackendId(Long visitBackendId)
    {
        this.visitBackendId = visitBackendId;
    }

    public Long getVisitId()
    {
        return visitId;
    }

    public void setVisitId(Long visitId)
    {
        this.visitId = visitId;
    }

    @Override
    public Long getPrimaryKey()
    {
        return id;
    }

}
