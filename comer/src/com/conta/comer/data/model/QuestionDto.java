package com.conta.comer.data.model;

import com.conta.comer.constants.QuestionType;

/**
 * Created by Mahyar on 7/26/2015.
 */
public class QuestionDto extends BaseModel
{
    private Long questionId;
    private Integer qOrder;
    private String questionnaireTitle;
    private String question;
    private String description;
    private Long answerId;
    private String answer;

    public String getqAnswers()
    {
        return qAnswers;
    }

    public void setqAnswers(String qAnswers)
    {
        this.qAnswers = qAnswers;
    }

    private String qAnswers;
    private Long questionBackendId;
    private String createDateTime;
    private QuestionType type;

    public QuestionType getType()
    {
        return type;
    }

    public void setType(QuestionType type)
    {
        this.type = type;
    }

    public Long getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Long questionId)
    {
        this.questionId = questionId;
    }

    public String getQuestionnaireTitle()
    {
        return questionnaireTitle;
    }

    public void setQuestionnaireTitle(String questionnaireTitle)
    {
        this.questionnaireTitle = questionnaireTitle;
    }

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Integer getqOrder()
    {
        return qOrder;
    }

    public void setqOrder(Integer qOrder)
    {
        this.qOrder = qOrder;
    }

    public Long getAnswerId()
    {
        return answerId;
    }

    public void setAnswerId(Long answerId)
    {
        this.answerId = answerId;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    public Long getQuestionBackendId()
    {
        return questionBackendId;
    }

    public void setQuestionBackendId(Long questionBackendId)
    {
        this.questionBackendId = questionBackendId;
    }

    public String getCreateDateTime()
    {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime)
    {
        this.createDateTime = createDateTime;
    }
}
