package com.parsroyal.solutiontablet.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alirezaafkar.sundatepicker.DatePicker.Builder;
import com.alirezaafkar.sundatepicker.interfaces.DateSetListener;
import com.parsroyal.solutiontablet.R;
import com.parsroyal.solutiontablet.constants.QuestionType;
import com.parsroyal.solutiontablet.ui.adapter.QuestionDetailAdapter.ViewHolder;
import com.parsroyal.solutiontablet.ui.fragment.dialogFragment.QuestionDetailDialogFragment;
import com.parsroyal.solutiontablet.util.Empty;
import com.parsroyal.solutiontablet.util.NumberUtil;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by shkbhbb on 10/16/17.
 */

public class QuestionDetailAdapter extends Adapter<ViewHolder> {

  private QuestionType questionType;
  private Context context;
  private List<String> answers;
  private Boolean[] userAnswer;
  private LayoutInflater inflater;
  private String submittedAnswer;
  private ViewHolder viewHolder;
  private Long amountValue;
  private QuestionDetailDialogFragment questionDetailDialogFragment;

  public QuestionDetailAdapter(Context context, List<String> answers, QuestionType questionType,
      String answer, QuestionDetailDialogFragment questionDetailDialogFragment) {
    this.context = context;
    this.questionType = questionType;
    this.answers = answers;
    this.submittedAnswer = answer;
    this.questionDetailDialogFragment = questionDetailDialogFragment;
    this.userAnswer = new Boolean[answers.size()];
    this.inflater = LayoutInflater.from(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    int layout = 0;
    switch (questionType) {
      case SIMPLE:
        layout = R.layout.item_questionnaire_detail_simple;
        break;
      case CHOICE_SINGLE:
        layout = R.layout.item_questionnaire_detail_radio;
        break;
      case CHOICE_MULTIPLE:
        layout = R.layout.item_questionnaire_detail_check_box;
        break;
      case SIMPLE_NUMERIC:
        layout = R.layout.item_questionnaire_detail_simple_numeric;
        break;
      case DATE:
        layout = R.layout.item_questionnaire_detail_date;
        break;
    }
    View view = inflater.inflate(layout, parent, false);
    viewHolder = new ViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setData(position);
  }

  public void updateList(List<String> answers, QuestionType questionType, String answer) {
    this.questionType = questionType;
    this.answers = answers;
    this.submittedAnswer = answer;
    userAnswer = new Boolean[answers.size()];
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return answers.size();
  }

  public void removeAllSelections() {
    for (int i = 0; i < userAnswer.length; i++) {
      userAnswer[i] = false;
    }
    notifyDataSetChanged();
    submittedAnswer = "";
  }

  public void removeDate() {
    submittedAnswer = "";
    notifyDataSetChanged();
  }

  public String getUserAnswers() {
    String answer = "";
    switch (questionType) {
      case SIMPLE:
        if (viewHolder != null) {
          answer = viewHolder.simpleEdit.getText().toString();
        }
        break;
      case SIMPLE_NUMERIC:
        if (Empty.isNotEmpty(amountValue)) {
          answer = String.valueOf(amountValue);
        } else {
          answer = "";
        }
        break;
      case CHOICE_SINGLE:
        for (int i = 0; i < userAnswer.length; i++) {
          if (userAnswer[i] != null && userAnswer[i]) {
            answer = answers.get(i);
            break;
          }
        }
        break;
      case CHOICE_MULTIPLE:
        for (int i = 0; i < userAnswer.length; i++) {
          if (userAnswer[i] != null && userAnswer[i]) {
            answer = answer.concat(answers.get(i) + "*");
          }
        }
        if (answer.endsWith("*")) {
          answer = answer.substring(0, answer.length() - 1);
        }
        break;
      case DATE:
        if (viewHolder != null) {
          answer = viewHolder.dateEdit.getText().toString();
          if (answer.equals(context.getString(R.string.choose_date))) {
            answer = "";
          }
        }
    }
    return answer;
  }


  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener,
      DateSetListener {

    @Nullable
    @BindView(R.id.answer_radio)
    RadioButton answerRadio;
    @Nullable
    @BindView(R.id.answer_box)
    CheckBox answerBox;
    @Nullable
    @BindView(R.id.simple_edit)
    EditText simpleEdit;
    @Nullable
    @BindView(R.id.simple_numeric_edt)
    EditText simpleNumericEdt;
    @Nullable
    @BindView(R.id.date_edit)
    EditText dateEdit;

    private String answer;
    private int position;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      if (answerRadio != null) {
        answerRadio.setOnClickListener(this);
      } else if (answerBox != null) {
        answerBox.setOnClickListener(this);
      } else if (dateEdit != null) {
        dateEdit.setOnClickListener(this);
      }
    }

    public void setData(int position) {
      this.position = position;
      this.answer = answers.get(position);
      if (answerRadio != null) {
        answerRadio.setText(answer);
        setAnsweredData(answerRadio);
      } else if (answerBox != null) {
        answerBox.setText(answer);
        setAnsweredData(answerBox);
      } else if (simpleEdit != null && Empty.isNotEmpty(submittedAnswer)) {
        simpleEdit.setText(submittedAnswer);
      } else if (simpleNumericEdt != null) {
        setUpSimpleNumericTextWatcher();
        if (Empty.isNotEmpty(submittedAnswer)) {
          simpleNumericEdt
              .setText(String.format(Locale.US, "%,d", Integer.parseInt(submittedAnswer)));
        }
      } else if (dateEdit != null) {
        dateEdit.setFocusable(false);
        dateEdit.setText(Empty.isEmpty(submittedAnswer) ? context.getString(R.string.choose_date)
            : submittedAnswer);
      }
    }

    private void setUpSimpleNumericTextWatcher() {
      simpleNumericEdt.addTextChangedListener(new TextWatcher() {
        boolean isEditing = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
          if (isEditing) {
            return;
          }
          isEditing = true;
          try {
            if (Empty.isNotEmpty(s.toString()) && !s.toString().replaceAll(",", "").trim()
                .equals("")) {

              amountValue = Long
                  .parseLong(NumberUtil.digitsToEnglish(s.toString().replaceAll(",", "")));
              String number = String.format(Locale.US, "%,d", amountValue);
              s.replace(0, s.length(), number);
            } else {
              amountValue = null;
            }
          } catch (NumberFormatException e) {
            e.printStackTrace();
          }
          isEditing = false;
        }
      });
    }

    private void setAnsweredData(View view) {
      if (!TextUtils.isEmpty(submittedAnswer)) {
        String[] selectedAnswers = submittedAnswer.split("[*]");
        for (String selectedAnswer : selectedAnswers) {
          if (answer.equals(selectedAnswer)) {
            userAnswer[position] = true;
            break;
          }
        }
      }
      if (userAnswer[position] != null && userAnswer[position]) {
        if (answerRadio != null) {
          ((RadioButton) view).setChecked(true);
        } else {
          ((CheckBox) view).setChecked(true);
        }
      } else {
        if (answerRadio != null) {
          ((RadioButton) view).setChecked(false);
        } else {
          ((CheckBox) view).setChecked(false);
        }
      }
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.answer_radio:
          int selectedItemPosition = selectedPosition();
          if (selectedItemPosition != -1) {
            userAnswer[selectedItemPosition] = false;
          }
          userAnswer[position] = true;
          submittedAnswer = "";
          notifyDataSetChanged();
          break;
        case R.id.answer_box:
          userAnswer[position] = answerBox.isChecked();
          submittedAnswer = "";
          break;
        case R.id.date_edit:
          Builder builder = new Builder().id(1);
          if (Empty.isNotEmpty(answer)) {
            String[] date = answer.split("/");
            builder.date(Integer.parseInt(date[2]),
                Integer.parseInt(date[1]),
                Integer.parseInt("13" + date[0]));
          }
          builder.build(ViewHolder.this)
              .show(questionDetailDialogFragment.getFragmentManager(), "");
          break;
      }
    }

    private int selectedPosition() {
      for (int i = 0; i < userAnswer.length; i++) {
        if (userAnswer[i] != null && userAnswer[i]) {
          return i;
        }
      }
      return -1;
    }

    @Override
    public void onDateSet(int id, @Nullable Calendar calendar, int day, int month, int year) {
      int tempYear = year % 100;
      dateEdit.setText(tempYear + "/" + month + "/" + day);
    }
  }
}
