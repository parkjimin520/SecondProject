package com.example.secondproject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import github.hongbeomi.dividerseekbar.DividerSeekBar;

public class Pid extends AppCompatActivity {

    public static Context context_pid;
    public static String info_study,info_pid,info_task,info_condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pid);

        DividerSeekBar dividerSeekBar = (DividerSeekBar)findViewById(R.id.dividerSeekBar_test);
        dividerSeekBar.setOnDividerSeekBarChangeStateListener(new DividerSeekBar.OnDividerSeekBarChangeStateListener() {
            @Override
            public void onProgressEnabled(DividerSeekBar dividerSeekBar, int i) {
                Log.i("test","Enabled");
            }

            @Override
            public void onProgressDisabled(DividerSeekBar dividerSeekBar, int i) {
                Log.i("test","Disabled");
            }
        });


        dividerSeekBar.setTextInterval(30);//텍스트 구간
        dividerSeekBar.setDividerInterval(10);//막대 구간
        dividerSeekBar.setSeaLineColor(github.hongbeomi.
                dividerseekbar.R.color.light_blue_600);//가로막대 색
        dividerSeekBar.setDividerColor(android.R.color.holo_purple); // 세로 막대 색
        dividerSeekBar.setThumbActivatedDrawable(R.drawable.ic_launcher_foreground); // set activated thumb
        dividerSeekBar.setThumbDefaultDrawable(R.drawable.ic_launcher_background);//TARGET

        dividerSeekBar.setActiveMode(DividerSeekBar.ACTIVE_MODE_TARGET);// set target mode
        dividerSeekBar.setActivateTargetValue(60); // set target value
        dividerSeekBar.setActivateTargetValue(30); // set target value




        context_pid = this;

        Button button = (Button)findViewById(R.id.button);

        RadioGroup Task_group = (RadioGroup)findViewById(R.id.Task_group);
        RadioGroup PID_group = (RadioGroup)findViewById(R.id.PID_group);
        RadioGroup Study_group = (RadioGroup)findViewById(R.id.Study_group);
        RadioGroup Condition_group = (RadioGroup)findViewById(R.id.Condition2_group);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast myToast = Toast.makeText(getApplicationContext(),"Log파일에 등록 완료", Toast.LENGTH_SHORT);
                myToast.show();

                int study = Study_group.getCheckedRadioButtonId();
                RadioButton radio_study = (RadioButton) findViewById(study);
                info_study = radio_study.getText().toString();
//                ((MyLog)MyLog.mContext).inputLog(radio_study.getText().toString());

                int pid = PID_group.getCheckedRadioButtonId();
                RadioButton radio_pid = (RadioButton) findViewById(pid);
                info_pid = "P"+radio_pid.getText().toString();
//                ((MyLog)MyLog.mContext).inputLog("P"+radio_pid.getText().toString());

                int task = Task_group.getCheckedRadioButtonId();
                RadioButton radio_task = (RadioButton) findViewById(task);
                info_task = radio_task.getText().toString();
//                ((MyLog)MyLog.mContext).inputLog(radio_task.getText().toString());

                int condition = Condition_group.getCheckedRadioButtonId();
                RadioButton radio_condition = (RadioButton) findViewById(condition);
                info_condition = radio_condition.getText().toString();
//                ((MyLog)MyLog.mContext).inputLog(radio_condition.getText().toString());

                ((MyLog)MyLog.mContext).inputLog("UserStudy|PID|TaskNum|Condition|Event|Remarks");
                ((MyLog)MyLog.mContext).inputLog(info_study+"|"+info_pid+"|"+info_task+"|"+info_condition);


            }
        });

    }
}

