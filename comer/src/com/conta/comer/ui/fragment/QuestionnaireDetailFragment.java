package com.conta.comer.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.conta.comer.R;
import com.conta.comer.constants.Constants;
import com.conta.comer.data.entity.Customer;
import com.conta.comer.data.entity.QAnswer;
import com.conta.comer.data.listmodel.QuestionListModel;
import com.conta.comer.data.model.QuestionDto;
import com.conta.comer.data.searchobject.QuestionSo;
import com.conta.comer.exception.UnknownSystemException;
import com.conta.comer.service.CustomerService;
import com.conta.comer.service.GoodsService;
import com.conta.comer.service.QuestionnaireService;
import com.conta.comer.service.impl.CustomerServiceImpl;
import com.conta.comer.service.impl.GoodsServiceImpl;
import com.conta.comer.service.impl.QuestionnaireServiceImpl;
import com.conta.comer.ui.MainActivity;
import com.conta.comer.ui.adapter.QuestionListAdapter;
import com.conta.comer.util.DateUtil;
import com.conta.comer.util.Empty;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Mahyar on 7/25/2015.
 */
public class QuestionnaireDetailFragment extends BaseListFragment<QuestionListModel, QuestionListAdapter>
{

    public static final String TAG = QuestionnaireDetailFragment.class.getSimpleName();

    private MainActivity mainActivity;
    private QuestionnaireService questionnaireService;
    private CustomerService customerService;
    private GoodsService goodsService;
    private Long questionnaireBackendId;
    private Long customerId;
    private Long goodsBackendId;
    private Long visitId;
    private Customer customer;
    private RadioGroup radioGroup;
    private EditText answerEt;
    private LinearLayout answerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        try
        {
            mainActivity = (MainActivity) getActivity();
            questionnaireService = new QuestionnaireServiceImpl(mainActivity);
            customerService = new CustomerServiceImpl(mainActivity);
            goodsService = new GoodsServiceImpl(mainActivity);

            questionnaireBackendId = getArguments().getLong("qnId");
            visitId = getArguments().getLong(Constants.VISIT_ID);
            customerId = getArguments().getLong(Constants.CUSTOMER_ID);
            goodsBackendId = getArguments().getLong("goodsBackendId");

            customer = customerService.getCustomerById(customerId);

            View view = super.onCreateView(inflater, container, savedInstanceState);
            buttonPanel.setVisibility(View.VISIBLE);
            Button canclButton = (Button) buttonPanel.findViewById(R.id.cancelBtn);
            canclButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mainActivity.removeFragment(QuestionnaireDetailFragment.this);
                }
            });
            return view;
        } catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage(), ex);
            return inflater.inflate(R.layout.view_error_page, null);
        }
    }

    @Override
    protected List<QuestionListModel> getDataModel()
    {
        QuestionSo questionSo = new QuestionSo();
        questionSo.setQuestionnaireBackendId(questionnaireBackendId);
        questionSo.setVisitId(visitId);
        questionSo.setGoodsBackendId(goodsBackendId);
        return questionnaireService.searchForQuestions(questionSo);
    }

    @Override
    protected View getHeaderView()
    {
        return null;
    }

    @Override
    protected QuestionListAdapter getAdapter()
    {
        return new QuestionListAdapter(mainActivity, dataModel, questionnaireBackendId, visitId);
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener()
    {
        if (customer != null && customer.getBackendId() != 0 && !customer.isApproved())
        {
            return null;
        }

        return new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                QuestionDto questionDto = questionnaireService.getQuestionDto(dataModel.get(position).getPrimaryKey(), visitId, goodsBackendId);
                openQuestionDialog(questionDto);
            }
        };
    }

    private void openQuestionDialog(final QuestionDto questionDto)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mainActivity);
        View view = mainActivity.getLayoutInflater().inflate(R.layout.dialg_layout_question, null);

        TextView questionnaireTitleTv = (TextView) view.findViewById(R.id.questionnaireTitleTv);
        TextView questionTv = (TextView) view.findViewById(R.id.questionTv);
        TextView answerTv = (TextView) view.findViewById(R.id.textView39);
        answerLayout = (LinearLayout) view.findViewById(R.id.answerLayout);
        answerEt = (EditText) view.findViewById(R.id.answerEt);
        switch (questionDto.getType())
        {
            case SIMPLE:
                answerEt.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case SIMPLE_NUMERIC:
                answerEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case CHOICE_SINGLE:
                answerEt.setVisibility(View.GONE);
                answerTv.setVisibility(View.GONE);
                createRadioButtons(questionDto.getqAnswers(), questionDto.getAnswer());
                break;
            case CHOICE_MULTIPLE:
                answerEt.setVisibility(View.GONE);
                answerTv.setVisibility(View.GONE);
                createCheckBox(questionDto.getqAnswers(), questionDto.getAnswer());
        }
        final Button prvBtn = (Button) view.findViewById(R.id.prvBtn);
        Button nextBtn = (Button) view.findViewById(R.id.nextBtn);
        Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        Button saveBtn = (Button) view.findViewById(R.id.saveBtn);

        questionnaireTitleTv.setText(questionDto.getQuestionnaireTitle());
        questionTv.setText(questionDto.getQuestion());
        answerEt.setText(Empty.isEmpty(questionDto.getAnswer()) ? "" : questionDto.getAnswer().replaceAll("[*]", " - "));

        alertBuilder.setView(view);

        final AlertDialog alert = alertBuilder.create();
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        prvBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    String answer = getAnswer(questionDto);
                    if (Empty.isNotEmpty(answer))
                    {
                        questionDto.setAnswerId(saveAnswer(questionDto, answer));
                    }
                    adapter.setDataModel(dataModel);
                    adapter.notifyDataSetChanged();
                    adapter.notifyDataSetInvalidated();
                    QuestionDto prvQuestionDto = questionnaireService.getQuestionDto(questionnaireBackendId, visitId, questionDto.getqOrder() - 1, goodsBackendId);
                    if (Empty.isNotEmpty(prvQuestionDto))
                    {
                        alert.dismiss();
                        openQuestionDialog(prvQuestionDto);
                    }

                } catch (Exception ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    toastError(new UnknownSystemException(ex));
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    String answer = getAnswer(questionDto);
                    if (Empty.isNotEmpty(answer))
                    {
                        questionDto.setAnswerId(saveAnswer(questionDto, answer));
                    }
                    QuestionDto nextQuestionDto = questionnaireService.getQuestionDto(questionnaireBackendId, visitId, questionDto.getqOrder() + 1, goodsBackendId);
                    adapter.setDataModel(dataModel);
                    adapter.notifyDataSetChanged();
                    adapter.notifyDataSetInvalidated();
                    if (Empty.isNotEmpty(nextQuestionDto))
                    {
                        alert.dismiss();
                        openQuestionDialog(nextQuestionDto);
                    }
                } catch (Exception ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    toastError(new UnknownSystemException(ex));
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dataModel = getDataModel();
                adapter.setDataModel(dataModel);
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetInvalidated();
                alert.dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    String answer = getAnswer(questionDto);
                    Long answerId = saveAnswer(questionDto, answer);
                    questionDto.setAnswerId(answerId);
                    dataModel = getDataModel();
                    adapter.setDataModel(dataModel);
                    adapter.notifyDataSetChanged();
                    adapter.notifyDataSetInvalidated();
                } catch (Exception ex)
                {
                    Log.e(TAG, ex.getMessage(), ex);
                    toastError(new UnknownSystemException(ex));
                }
                alert.dismiss();
            }
        });

        alert.show();
    }

    private void createCheckBox(String answer, String selectedAnswer)
    {
        answerLayout.removeAllViews();

        String[] answerList = answer.split("[*]");

        List<String> selectedAnswers = null;
        if (Empty.isNotEmpty(selectedAnswer))
        {
            selectedAnswers = Arrays.asList(selectedAnswer.split("[*]"));
        }
        for (int i = answerList.length - 1; i >= 0; i--)
        {
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setId(i);
            checkBox.setPadding(5, 0, 5, 0);
            checkBox.setGravity(Gravity.RIGHT);
            checkBox.setText(answerList[i]);
            if (selectedAnswers != null && selectedAnswers.contains(answerList[i]))
            {
                checkBox.setChecked(true);
            }
            answerLayout.addView(checkBox);
        }
    }

    private String getAnswer(QuestionDto questionDto)
    {
        String answer = "";
        switch (questionDto.getType())
        {
            case SIMPLE:
            case SIMPLE_NUMERIC:
                answer = answerEt.getText().toString();
                break;
            case CHOICE_SINGLE:
                answer = radioGroup.getCheckedRadioButtonId() == -1 ? "" : questionDto.getqAnswers().split("[*]")[radioGroup.getCheckedRadioButtonId()];
                break;
            case CHOICE_MULTIPLE:
                for (int i = 0; i < answerLayout.getChildCount(); i++)
                {
                    CheckBox checkBox = (CheckBox) answerLayout.getChildAt(i);
                    if (checkBox.isChecked())
                    {
                        answer = answer.concat(checkBox.getText().toString() + "*");
                    }
                }
                if (answer.endsWith("*"))
                {
                    answer = answer.substring(0, answer.length() - 1);
                }
        }
        return answer;
    }

    private void createRadioButtons(String answer, String selectedAnswer)
    {
        answerLayout.removeAllViews();
        radioGroup = new RadioGroup(getActivity());
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        String[] answerList = answer.split("[*]");
        for (int i = answerList.length - 1; i >= 0; i--)
        {
            RadioButton rdbtn = new RadioButton(getActivity());
            rdbtn.setId(i);
            rdbtn.setPadding(5, 0, 5, 0);
            rdbtn.setText(answerList[i]);
            if (answerList[i].equals(selectedAnswer))
            {
                rdbtn.setChecked(true);
            }
            radioGroup.addView(rdbtn);
        }
        answerLayout.addView(radioGroup);
    }

    private Long saveAnswer(QuestionDto questionDto, String answer)
    {
        Customer customer = customerService.getCustomerById(customerId);
        QAnswer qAnswer = questionnaireService.getAnswerById(questionDto.getAnswerId());
        if (Empty.isEmpty(qAnswer))
        {
            qAnswer = new QAnswer();
            qAnswer.setId(questionDto.getAnswerId());
            qAnswer.setAnswer(answer);
            qAnswer.setDate(DateUtil.convertDate(new Date(), DateUtil.GLOBAL_FORMATTER, "FA"));
            qAnswer.setCustomerBackendId(customer.getBackendId() == 0 ? customerId : customer.getBackendId());
            if (Empty.isNotEmpty(goodsBackendId))
            {
                qAnswer.setGoodsBackendId(goodsBackendId);
            }
            qAnswer.setVisitId(visitId);
            qAnswer.setQuestionBackendId(questionDto.getQuestionBackendId());
        } else
        {
            qAnswer.setAnswer(answer);
        }
        return questionnaireService.saveAnswer(qAnswer);
    }

    @Override
    protected String getClassTag()
    {
        return null;
    }

    @Override
    protected String getTitle()
    {
        return null;
    }

    @Override
    public int getFragmentId()
    {
        return MainActivity.QUESTIONNAIRE_DETAIL_FRAGMENT_ID;
    }
}
