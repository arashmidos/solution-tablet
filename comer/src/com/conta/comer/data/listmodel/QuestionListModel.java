package com.conta.comer.data.listmodel;

import com.conta.comer.data.model.BaseListModel;

/**
 * Created by Mahyar on 7/25/2015.
 */
public class QuestionListModel extends BaseListModel
{

    private String question;
    private String answer;
    private Integer qOrder;

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    public Integer getqOrder()
    {
        return qOrder;
    }

    public void setqOrder(Integer qOrder)
    {
        this.qOrder = qOrder;
    }
}
