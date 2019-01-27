package com.giovanniscieri.EOL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by giovanniscieri on 04/02/17.
 */

public class CustomAdapter extends ArrayAdapter<Question>{
    private View customView;

    public CustomAdapter(Context context, ArrayList<Question> questions){
        super(context, R.layout.row, questions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final Question currentQuestion = getItem(position);
        customView = inflater.inflate(R.layout.question, null, true);
        TextView textView = (TextView) customView.findViewById(R.id.textView_question);
        textView.setText(currentQuestion.getQuestion());
        LinearLayout ll = (LinearLayout) customView.findViewById(R.id.linear_layout_question);
        final ArrayList<Answer> arrayList = currentQuestion.getAnswersList();

        if(currentQuestion.getType().equals("MR")){
            CheckBox checkbox = null;
            for(final Answer a : arrayList){
                checkbox = new CheckBox(getContext());
                checkbox.setText(a.getText());
                checkbox.setId(Integer.parseInt(a.getIdAnswer()));
                checkbox.setChecked(a.isChecked());
                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        a.setChecked(true);
                    } else {
                        a.setChecked(false);
                    }
                    }
                });
                ll.addView(checkbox);
            }
        } else if(currentQuestion.getType().equals("MC")) {
            RadioButton radioButton;
            RadioGroup radioGroup = new RadioGroup(getContext());
            radioGroup.setId(Integer.parseInt(currentQuestion.getIdQuestion()));
            radioGroup.setOrientation(LinearLayout.VERTICAL);
            for(final Answer a : arrayList){
                radioButton = new RadioButton(getContext());
                radioButton.setText(a.getText());
                radioButton.setId(Integer.parseInt(a.getIdAnswer()));
                radioButton.setChecked(a.isChecked());
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked) {
                        a.setChecked(true);
                    }else{
                        a.setChecked(false);
                    }
                    }
                });
                radioGroup.addView(radioButton);
            }
            ll.addView(radioGroup);
        }
        return customView;
    }
}
