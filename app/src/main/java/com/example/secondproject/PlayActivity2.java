package com.example.secondproject;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

//트루먼쇼
public class PlayActivity2 extends AppCompatActivity  {

    //프로그레스바 구간
    private float totalSpan = 1000;
    private float redSpan = 190; //~10:16까지
    private float orangeSpan = 50; //~13:38까지
    private float yellowSpan = 47; //~16:21까지
    private float blueSpan = 33; //~18:15까지
    private float greenSpan = 67 ; //~23:02까지
    private float purpleSpan = 100 ; //~29:28
    private float coralSpan = 82 ; //~34:12
    private float beigeSpan = 46 ; //~37:05
    private float brownSpan = 150 ; //~46:25
    private float oliveSpan= 83 ; //~51:28
    private float lightblueSpan = 40;
    private float tomatoSpan = 40;





    private ArrayList<ProgressItem> progressItemList;
    private ProgressItem mProgressItem;
    CustomSeekBar seekBar;


    //슬라이딩패널
    private ArrayList<SlidingList> arrayList;
    private SlidingAdapter slidingAdapter;
    private RecyclerView recyclerView;

    int Time; //이미지 매핑 범위

    MediaPlayer mp;
//    SeekBar seekBar;
    boolean isPlaying = false; //재생중인지 확인할 변수
    TextView timeText; //재생바의 타임스탬프

    SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

    //txt파일 추출
    public String line = null;
    String rLine[] = new String[1000];
    int line_count = 0;
    String timeStamp[] = new String[1000]; //타임스탬프 배열

    //자동 스크롤
    ObjectAnimator AutoScroll;
    boolean touchScroll; //ScrollView를 직접 터치했는지의 여부

    //슬라이딩패널
    int imgNum;

    //검색 - 다음, 이전
    int[] start = new int[100]; //탐색 시작 위치 저장
    int s=1; //탐색위치 저장할 start[]의 인덱스
    int store[] = new int[100]; //이전 탐색 위치 저장
    int i;

    //타이머
    private Menu menu;
    //    MenuItem menu_timer = menu.findItem(R.id.menu_timer);
    String recTime;
    //상태를 표시하는 '상수' 지정
    //- 각각의 숫자는 독립적인 개별 '상태' 의미
    public static final int INIT = 0;//처음
    public static final int RUN = 1;//실행중
    public static final int PAUSE = 2;//정지

    //상태값을 저장하는 변수
    //- INIT은 초기값임, 그걸 status 안에 넣는다.(0을 넣은거다)
    public static int status = INIT;

    //타이머 시간 값을 저장할 변수
    private long baseTime,pauseTime;
    TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //음악 뷰
        timeText = (TextView) findViewById(R.id.timeText);
        ImageButton play_button = (ImageButton) findViewById(R.id.play_button);
        ImageButton stop_button = (ImageButton) findViewById(R.id.stop_button);
        ImageButton pause_button = (ImageButton) findViewById(R.id.pause_button);

        //전사
        textView = (TextView) findViewById(R.id.textView);
        ImageView figureImage = (ImageView) findViewById(R.id.figureImage);
        TextView keyword = (TextView) findViewById(R.id.keyword);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        //음원
        mp = MediaPlayer.create(PlayActivity2.this, R.raw.trumanshow_mp);

        //상단바----------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back버튼
        //제목은 img번호와 intent구별 위해 밑에서 받음

//        //음원시간 표시
//        TextView toolbar_time = (TextView) findViewById(R.id.toolbar_time);
//        toolbar_time.setText(timeFormat.format(mp.getDuration()));
//        //날짜 표시
//        TextView toolbar_date = (TextView) findViewById(R.id.toolbar_date);
//        toolbar_date.setText("Oct. 13. 2021");
        TextView length = (TextView) findViewById(R.id.length);
        length.setText(timeFormat.format(mp.getDuration()));


        //시크바
        seekBar = ((CustomSeekBar) findViewById(R.id.seekbar));
        initDataToSeekbar();

//        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setVisibility(ProgressBar.VISIBLE);
        seekBar.setMax(mp.getDuration());

        //검색
        EditText editText = (EditText) findViewById(R.id.editText);
        Button next = (Button) findViewById(R.id.next);
        Button previous = (Button) findViewById(R.id.previous);

        Spinner spinner = (Spinner)findViewById(R.id.spinner);


        //Scroll 토글
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggle);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    Log.i("test","ON!!");
                    touchScroll = false;

                }else{
                    Log.i("test","OFF!!");
                    touchScroll = true;
                }
            }
        });

        //참석자 별
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){
                    case 0: //All
                        setTxt(R.raw.trumanshow);
                        ((MyLog)MyLog.mContext).inputLog(((Pid)Pid.context_pid).info_study+"|"+((Pid)Pid.context_pid).info_pid+"|"
                                +((Pid)Pid.context_pid).info_task + "|"+((Pid)Pid.context_pid).info_condition+"|"+"click_AllParticipant");
                        break;
                    case 1:
                        setTxt(R.raw.trumanshow_p1);
                        ((MyLog)MyLog.mContext).inputLog(((Pid)Pid.context_pid).info_study+"|"+((Pid)Pid.context_pid).info_pid+"|"
                                +((Pid)Pid.context_pid).info_task + "|"+((Pid)Pid.context_pid).info_condition+"|"+"click_Participant1");
                        break;
                    case 2:
                        setTxt(R.raw.trumanshow_p2);
                        ((MyLog)MyLog.mContext).inputLog(((Pid)Pid.context_pid).info_study+"|"+((Pid)Pid.context_pid).info_pid+"|"
                                +((Pid)Pid.context_pid).info_task + "|"+((Pid)Pid.context_pid).info_condition+"|"+"click_Participant2");
                        break;
                    case 3:
                        setTxt(R.raw.trumanshow_p3);
                        ((MyLog)MyLog.mContext).inputLog(((Pid)Pid.context_pid).info_study+"|"+((Pid)Pid.context_pid).info_pid+"|"
                                +((Pid)Pid.context_pid).info_task + "|"+((Pid)Pid.context_pid).info_condition+"|"+"click_Participant2");
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        start[0] = 0; //처음엔 인덱스 0부터 검사
        //검색
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                //Enter Key 누르면
                if ((keyEvent.getAction() == keyEvent.ACTION_DOWN) && (keycode == KeyEvent.KEYCODE_ENTER)) {
                    //키패드 숨기기
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


                    String inputText = editText.getText().toString();
                    String fullText = textView.getText().toString();
                    String highlighted = "<font color='red'>" + inputText + "</font>";

                    //인덱스
                    final int[] index1 = {fullText.indexOf(inputText)};
                    final int[] index2 = {textView.getLayout().getLineForOffset(index1[0])};


                    if (inputText.replace(" ", "").equals("")) { //input이 없으면 HTML적용 안 함.
                        editText.setText(null);

                        Toast.makeText(getApplicationContext(), "검색어가 없습니다.", Toast.LENGTH_SHORT).show();

                        SpannableString spannableString = null; //timestamp 색 변경
                        for (int i = 0; i < line_count; i += 2) {
                            spannableString = new SpannableString(rLine[i]);
                            String click_time = rLine[i]; //String형 timeStamp필요

                            //클릭 시 할 동작
                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View view) {
                                    String[] split = click_time.split(":");  //02:10에서 :제거
                                    String M = split[0]; //02
                                    String S = split[1]; //10

                                    int mesc = 0;
                                    int m = Integer.parseInt(M); //int로 변경
                                    int s = Integer.parseInt(S); //int로 변경
                                    m *= 60; //분을 초로 변경
                                    mesc = m + s; //분 초 더해서
                                    mesc *= 1000; //mesc형태로 변경


                                    mp.seekTo(mesc);
                                    seekBar.setProgress(mp.getCurrentPosition());
                                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));


                                }
                            };

                            spannableString.setSpan(clickableSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            textView.append(spannableString); //clickableSpan이 적용된 timeStamp
                            textView.append("\n" + rLine[i + 1] + "\n\n");
                            textView.setMovementMethod(LinkMovementMethod.getInstance());

                        }

                    } else if (fullText.contains(inputText)) { //찾는 단어 있으면
                        editText.setText(null);

                        //처음 검색단어로 이동
                        scrollView.scrollTo(0, textView.getLayout().getLineTop(index2[0]));

                        store[0] = index1[0]; //1번째 키워드의 인덱스 저장

                        //다음 검색단어로 이동
                        next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                start[s++] = index1[0] + inputText.length(); //새 탐색 시작 위치 저장([1]=첫번째 단어 제외하고 부터 )

                                store[i++] = index1[0];

                                index1[0] = fullText.indexOf(inputText, start[s - 1]);
                                index2[0] = textView.getLayout().getLineForOffset(index1[0]);
                                scrollView.scrollTo(0, textView.getLayout().getLineTop(index2[0]));

                                if (index1[0] < 0) { //마지막 단어 이후
                                    index1[0] = fullText.indexOf(inputText, start[s - 2]);
                                    index2[0] = textView.getLayout().getLineForOffset(index1[0]);
                                    scrollView.scrollTo(0, textView.getLayout().getLineTop(index2[0]));
                                    Toast.makeText(getApplicationContext(), "마지막 단어 입니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        previous.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    start[s++] = store[--i] + inputText.length(); //새 탐색 시작 위치 저장(1부터 : [0]=전체, [1]=첫번째 단어 제외하고 부터 )

                                    index1[0] = fullText.indexOf(inputText, store[i]);
                                    index2[0] = textView.getLayout().getLineForOffset(index1[0]);

                                    scrollView.scrollTo(0, textView.getLayout().getLineTop(index2[0]));


                                } catch (IndexOutOfBoundsException e) {
                                    //인덱스 벗어나면 = 더 이상 이전 단어가 없으면
                                    Toast.makeText(getApplicationContext(), "첫번째 단어입니다.", Toast.LENGTH_SHORT).show();
                                    i = 1; //배열 다시 세팅
                                }
                            }
                        });


                        //highlight
                        for (int i = 0; i < line_count; i++) {
                            rLine[i] = rLine[i].replace(inputText, highlighted);

                            textView.setText(""); //리셋
                        }


                        //하이라트된 text 뷰에 붙이기
                        for (int n = 0; n < line_count; n += 2) {//HTML 따로 적용해야지 red로 바뀜
                            //=======================================================
                            SpannableString SearchSpannable = new SpannableString(rLine[n]);
                            String click_time = rLine[n];
                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View view) {
                                    String[] split = click_time.split(":");  //02:10에서 :제거
                                    String M = split[0]; //02
                                    String S = split[1]; //10

                                    int mesc = 0;
                                    int m = Integer.parseInt(M); //int로 변경
                                    int s = Integer.parseInt(S); //int로 변경
                                    m *= 60; //분을 초로 변경
                                    mesc = m + s; //분 초 더해서
                                    mesc *= 1000; //mesc형태로 변경


                                    mp.seekTo(mesc);
                                    seekBar.setProgress(mp.getCurrentPosition());
                                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));


                                }
                            };
                            SearchSpannable.setSpan(clickableSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            textView.append(SearchSpannable);
//                        textView.append(Html.fromHtml(rLine[n])); //타임스탬프
                            textView.append("\n");
                            textView.append(Html.fromHtml(rLine[n + 1])); //내용
                            textView.append("\n\n");
                        }

                        //다시 돌려놓기 : 그래야 다음 검색 시 전에 검색한 highlight 제거 됨
                        for (int i = 0; i < line_count; i++) {
                            rLine[i] = rLine[i].replace(highlighted, inputText);
                        }


                    } else { //찾는 단어 본문에 없
                        editText.setText(null);

                        Toast.makeText(getApplicationContext(), inputText + "없음", Toast.LENGTH_SHORT).show();

                        for (int i = 0; i < line_count; i++) {
                            rLine[i] = rLine[i].replace(highlighted, inputText); //하이라이트 지우기
                            textView.setText(""); //리셋
                        }

                        //새 text 뷰에 붙이기
                        for (int n = 0; n < line_count; n += 2) {//HTML 따로 적용해야지 red로 바뀜
                            //=======================================================
                            SpannableString SearchSpannable = new SpannableString(rLine[n]);
                            String click_time = rLine[n];
                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View view) {
                                    String[] split = click_time.split(":");  //02:10에서 :제거
                                    String M = split[0]; //02
                                    String S = split[1]; //10

                                    int mesc = 0;
                                    int m = Integer.parseInt(M); //int로 변경
                                    int s = Integer.parseInt(S); //int로 변경
                                    m *= 60; //분을 초로 변경
                                    mesc = m + s; //분 초 더해서
                                    mesc *= 1000; //mesc형태로 변경


                                    mp.seekTo(mesc);
                                    seekBar.setProgress(mp.getCurrentPosition());
                                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));

                                }
                            };
                            SearchSpannable.setSpan(clickableSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            textView.append(SearchSpannable);
//                        textView.append(Html.fromHtml(rLine[n])); //타임스탬프
                            textView.append("\n");
                            textView.append(Html.fromHtml(rLine[n + 1])); //내용
                            textView.append("\n\n");

                        }
                    }
                }

                return false;
            }
        });


        //재생
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MyLog)MyLog.mContext).inputLog(((Pid)Pid.context_pid).info_study+"|"+((Pid)Pid.context_pid).info_pid+"|"
                        +((Pid)Pid.context_pid).info_task + "|"+((Pid)Pid.context_pid).info_condition+"|"+"click_playButton");

                mp.start();

                //노래 진행 시간
                new Thread() {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

                    public void run() {
                        seekBar.setMax(mp.getDuration());

                        while (mp.isPlaying()) {
                            //위젯변경위한 Ui쓰레드
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar.setProgress(mp.getCurrentPosition());
                                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                                }
                            });
                            SystemClock.sleep(200); //0.2초마다 진행상태 변경
                        }
                    }
                }.start();

                touchScroll = false;

            }
        });

        //일시정지
        pause_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                mp.pause();
                seekBar.setProgress(mp.getCurrentPosition());

                ((MyLog)MyLog.mContext).inputLog(((Pid)Pid.context_pid).info_study+"|"+((Pid)Pid.context_pid).info_pid+"|"
                        +((Pid)Pid.context_pid).info_task + "|"+((Pid)Pid.context_pid).info_condition+"|"+"click_pauseButton");

            }
        });



        //멈춤
        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MyLog)MyLog.mContext).inputLog(((Pid)Pid.context_pid).info_study+"|"+((Pid)Pid.context_pid).info_pid+"|"
                        +((Pid)Pid.context_pid).info_task +"|"+((Pid)Pid.context_pid).info_condition+"|"+"click_stopButton");

                try {
                    mp.stop();
//                    seekBar.setMax(0);
                    seekBar.setProgress(0);
                    timeText.setText("00:00");
                    mp.prepare();
                    scrollView.scrollTo(0,0);
                } catch (IOException e) {
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mp.seekTo(progress);
                }

                //일시정지 후 seekbar이동 시에도 timeStamp에 변화 있도록
                seekBar.setProgress(mp.getCurrentPosition());
                timeText.setText(timeFormat.format(mp.getCurrentPosition()));

                //자동 스크롤 + 이미지 mapping
                String time = timeText.getText().toString();
                String fullText = textView.getText().toString();
                if (fullText.contains(time)) {  //본문에 timeStamp가 있다면
                    //자동스크롤
                    int index1 = fullText.indexOf(time);
                    int index2 = textView.getLayout().getLineForOffset(index1);

                    AutoScroll = ObjectAnimator.ofInt(scrollView, "scrollY", textView.getLayout().getLineTop(index2)).setDuration(700);
                    if (!touchScroll) { //텍스트뷰 안 만졌으면
                        AutoScroll.start(); //자동 스크롤
                    }

                }

                String[] t = timeText.getText().toString().split(":"); //"00:00"
                String t_M = t[0];//"00"
                String t_S = t[1];//"00"
                String timeee = t_M + t_S;//"0000"
                Time = Integer.parseInt(timeee);//0000

                //이미지 매핑
                if(Time>=0 && Time<1016) {
                    figureImage.setImageResource(R.drawable.trumanshow1);
                    keyword.setText("오늘 소개할 영화는 트루먼쇼로 1998년 개봉해서 골든글로브 아카데미 등 다양한 수상을 한 명작이다.");
                }else if(Time>=1017 && Time<1338) {
                    figureImage.setImageResource(R.drawable.trumanshow2);
                    keyword.setText("트루먼은 자는 장면까지 생중계되는 쇼의 주인공으로 이상한 현상을 겪으며 모든 것이 수상하다고 느끼게 된다.");
                }else if(Time>=1339 && Time<1621) {
                    figureImage.setImageResource(R.drawable.trumanshow3);
                    keyword.setText("아버지와 항해를 갔다가 아버지가 돌아가셔서 트루먼은 물 공포증이 있고 죄책감에 시달린다.");
                }else if(Time>=1622 && Time<1815) {
                    figureImage.setImageResource(R.drawable.trumanshow4);
                    keyword.setText("쇼의 유지를 위해서 트루먼을 조종하는 배우들");
                }else if(Time>=1816 && Time<2302) {
                    figureImage.setImageResource(R.drawable.trumanshow5);
                    keyword.setText("트루먼 위에만 비가 오고, 하늘에서 조명이 떨어지는 등 점점 나타나기 시작하는 쇼의 빈틈으로 의심을 하게 되는 트루먼");
                }else if(Time>=2303 && Time<2928) {
                    figureImage.setImageResource(R.drawable.trumanshow6);
                    keyword.setText("주요 배우들은 현실세계로 돌아가지 못하고 자신의 진짜 삶이 없었을 것이다.");
                }else if(Time>=2929 && Time<3412) {
                    figureImage.setImageResource(R.drawable.trumanshow7);
                    keyword.setText("쇼의 빈틈을 보고 트루먼이 탈출하려고 하자 필사적으로 막는 사람들");
                }else if(Time>=3413 && Time<3705) {
                    figureImage.setImageResource(R.drawable.trumanshow8);
                    keyword.setText("트루먼의 자유의지를 생각해 주지 않는 잔인한 시청자들");
                }else if(Time>=3706 && Time<4625) {
                    figureImage.setImageResource(R.drawable.trumanshow9);
                    keyword.setText("통제력이 강한 감독은 배를 타고 탈출하는 트루먼을 보고 이성을 잃었다.");
                }else if(Time>=4626 && Time<5128) {
                    figureImage.setImageResource(R.drawable.trumanshow10);
                    keyword.setText("고비 끝에 세계의 끝에 도달한 트루먼은 기뻤을 것이다.");
                }else if(Time>=5129 && Time<5405) {
                    figureImage.setImageResource(R.drawable.trumanshow11);
                    keyword.setText("트루먼의 탈출이 끝나고 직원 2명이 아무렇지 않게 다른 채널로 돌리는 마지막 장면이 소름 끼쳤다.");
                }else if(Time>=5406 && Time<5950) {
                    figureImage.setImageResource(R.drawable.trumanshow12);
                    keyword.setText("트루먼쇼의 마지막 장면은 현실세계의 우리와 닮아있다.");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //progressbar 움직이는 것 Log에 기록
                ((MyLog)MyLog.mContext).inputLog(((Pid)Pid.context_pid).info_study+"|"+((Pid)Pid.context_pid).info_pid+"|"
                        +((Pid)Pid.context_pid).info_task +"|"+((Pid)Pid.context_pid).info_condition+"|"+
                        "onProgressChangedStop"+"|"+timeFormat.format(mp.getCurrentPosition()));

            }
        });



        //터치에 의해 스크롤이 변할 때
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        touchScroll = true; //터치가 가해짐
                        ((MyLog)MyLog.mContext).inputLog(((Pid)Pid.context_pid).info_study+"|"+((Pid)Pid.context_pid).info_pid+"|"
                                +((Pid)Pid.context_pid).info_task +"|"+((Pid)Pid.context_pid).info_condition+"|"+
                                "touch_ScrollView");
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

        //제목, 이미지position 받아오기
        Intent intent = getIntent();
        if(intent.hasExtra("제목")){
            String titleIntent = intent.getStringExtra("제목");
            getSupportActionBar().setTitle(titleIntent);

        }





        //슬라이딩패널----------------
        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.slidingPanel);
        recyclerView = (RecyclerView)findViewById(R.id.rv2);
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);//한줄에 4개씩
//        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new GridItemDecoration(30));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);//한줄에 4개씩
        recyclerView.setLayoutManager(gridLayoutManager);

        arrayList = new ArrayList<>();
        arrayList.add(new SlidingList(R.drawable.trumanshow1));
        arrayList.add(new SlidingList(R.drawable.trumanshow2));
        arrayList.add(new SlidingList(R.drawable.trumanshow3));
        arrayList.add(new SlidingList(R.drawable.trumanshow4));
        arrayList.add(new SlidingList(R.drawable.trumanshow5));
        arrayList.add(new SlidingList(R.drawable.trumanshow6));
        arrayList.add(new SlidingList(R.drawable.trumanshow7));
        arrayList.add(new SlidingList(R.drawable.trumanshow8));
        arrayList.add(new SlidingList(R.drawable.trumanshow9));
        arrayList.add(new SlidingList(R.drawable.trumanshow10));
        arrayList.add(new SlidingList(R.drawable.trumanshow11));
        arrayList.add(new SlidingList(R.drawable.trumanshow12));



        slidingAdapter = new SlidingAdapter(arrayList);
        slidingAdapter.setOnItemClickListener(new SlidingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                if(pos == 0){
                    mp.seekTo(0);
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                    seekBar.setProgress(mp.getCurrentPosition());
                }else if(pos == 1){
                    mp.seekTo(617000); //10분17초 == 617
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                    seekBar.setProgress(mp.getCurrentPosition());
                }else if(pos == 2){
                    mp.seekTo(819000); //13분39초
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                    seekBar.setProgress(mp.getCurrentPosition());
                }else if(pos == 3){
                    mp.seekTo(982000); //16분22초
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                    seekBar.setProgress(mp.getCurrentPosition());
                }else if(pos == 4){
                    mp.seekTo(1096000); //18분16초
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                    seekBar.setProgress(mp.getCurrentPosition());
                }else if(pos == 5){
                    mp.seekTo(1383000); //23분3초
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                    seekBar.setProgress(mp.getCurrentPosition());
                }else if(pos == 6){
                    mp.seekTo(1769000); //29분29초
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                    seekBar.setProgress(mp.getCurrentPosition());
                }else if(pos == 7){
                    mp.seekTo(2053000); //34분13초
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                    seekBar.setProgress(mp.getCurrentPosition());
                }else if(pos == 8){
                    mp.seekTo(2226000); //37분6초
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                    seekBar.setProgress(mp.getCurrentPosition());
                }else if(pos == 9){
                    mp.seekTo(2786000); //46분26초
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                    seekBar.setProgress(mp.getCurrentPosition());
                }else if(pos == 10){
                    mp.seekTo(3089000); //51분29초
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                    seekBar.setProgress(mp.getCurrentPosition());
                }else if(pos == 11){
                    mp.seekTo(3246000); //54분6초
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));
                    seekBar.setProgress(mp.getCurrentPosition());
                }
            }
        });

        recyclerView.setAdapter(slidingAdapter);



    } //OnCreate

    private void initDataToSeekbar() {
        progressItemList = new ArrayList<ProgressItem>();
        // red span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = ((redSpan / totalSpan) * 100);

        mProgressItem.color = android.R.color.holo_red_dark;
        progressItemList.add(mProgressItem);
        // blue span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (orangeSpan / totalSpan) * 100;
        mProgressItem.color = android.R.color.holo_orange_dark;
        progressItemList.add(mProgressItem);
        // green span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (yellowSpan / totalSpan) * 100;
        mProgressItem.color = android.R.color.holo_orange_light;
        progressItemList.add(mProgressItem);
        // yellow span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (blueSpan / totalSpan) * 100;
        mProgressItem.color = android.R.color.holo_blue_dark;
        progressItemList.add(mProgressItem);
        // green span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (greenSpan / totalSpan) * 100;
        mProgressItem.color = android.R.color.holo_green_light;
        progressItemList.add(mProgressItem);
        // purple span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (purpleSpan / totalSpan) * 100;
        mProgressItem.color = R.color.purple_200;
        progressItemList.add(mProgressItem);
        // coral span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (coralSpan / totalSpan) * 100;
        mProgressItem.color = R.color.lightcoral;
        progressItemList.add(mProgressItem);
        // beige span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (beigeSpan / totalSpan) * 100;
        mProgressItem.color = R.color.beige;
        progressItemList.add(mProgressItem);
        // brown span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (brownSpan / totalSpan) * 100;
        mProgressItem.color = R.color.brown;
        progressItemList.add(mProgressItem);
        // olive span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (oliveSpan / totalSpan) * 100;
        mProgressItem.color = R.color.olive;
        progressItemList.add(mProgressItem);
        // lightblue span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (lightblueSpan / totalSpan) * 100;
        mProgressItem.color = R.color.lightblue;
        progressItemList.add(mProgressItem);
        // tomato span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (tomatoSpan / totalSpan) * 100;
        mProgressItem.color = R.color.tomato;
        progressItemList.add(mProgressItem);

//        if(seekBar == null){
//            Log.i("test","널널널널");
//        }
        seekBar.initData(progressItemList);
        seekBar.invalidate();
    }



    //키보드 키로 Log타이머 + 재생 하기
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_0) {
                startTimer();
            }
        }
        return super.onKeyDown(keyCode, event);
    }




    //다른 곳 클릭시, search 후 키보드 내리기
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    //디바이스 자체 back버튼
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //노래 계속 재생돼서 추가
        if(mp.isPlaying()){
            mp.stop();
        }
    }



    //상단바 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;

        return true;
    }

    //'설정' 누를 시
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //노래 계속 재생돼서 추가
        if(mp.isPlaying()){
            mp.stop();
        }

        switch (item.getItemId()){
            case R.id.menu_settings:
                Intent settingIntent = new Intent(getApplicationContext(),Pid.class);
                startActivity(settingIntent);
                break;
            case R.id.menu_timer:
                startTimer();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    //타이머 상단바
    private void startTimer(){
        MenuItem menu_timer = menu.findItem(R.id.menu_timer);

        switch (status){
            case INIT:
                //어플리케이션이 실행되고 나서 실제로 경과된 시간...
                baseTime = SystemClock.elapsedRealtime();

                //핸들러 실행
                handler.sendEmptyMessage(0);
                menu_timer.setTitle("멈춤");

                //상태 변환
                status = RUN;
                ((MyLog)MyLog.mContext).inputLog(((Pid)Pid.context_pid).info_study+"|"+((Pid)Pid.context_pid).info_pid+"|"
                        +((Pid)Pid.context_pid).info_task +"|"+((Pid)Pid.context_pid).info_condition+"|"+"click_timerButton_Start");

                break;
            case RUN:
                //핸들러 정지
                handler.removeMessages(0);

                //정지 시간 체크
                pauseTime = SystemClock.elapsedRealtime();
                menu_timer.setTitle("시작");
                //상태변환
                status = PAUSE;

                //기록
                ((MyLog)MyLog.mContext).inputLog(((Pid)Pid.context_pid).info_study+"|"+((Pid)Pid.context_pid).info_pid+"|"
                        +((Pid)Pid.context_pid).info_task +"|"+((Pid)Pid.context_pid).info_condition+"|"+"click_timerButton_Stop"+"|"+getTime());

                baseTime = 0;
                pauseTime = 0;
                status = INIT;
                break;
            case PAUSE:
                long reStart = SystemClock.elapsedRealtime();
                baseTime += (reStart - pauseTime);

                handler.sendEmptyMessage(0);

                menu_timer.setTitle("멈춤");

                status = RUN;

        }

    }

    private String getTime(){
        //경과된 시간 체크

        long nowTime = SystemClock.elapsedRealtime();
        //시스템이 부팅된 이후의 시간?
        long overTime = nowTime - baseTime;

        long m = overTime/1000/60;
        long s = (overTime/1000)%60;
        long ms = overTime % 1000;

        recTime = String.format("%02d:%02d:%02d",m,s,ms);

        return recTime;
    }

    Handler handler = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            handler.sendEmptyMessage(0);
        }
    };


    private void setTxt(int txt){
        line_count = 0;
        textView.setText("");

        //txt추출 : TimeStamp, 내용 나눠서 저장
        try {
            BufferedReader bfRead = new BufferedReader(new InputStreamReader(getResources().openRawResource(txt)));

            // 한줄씩 NULL이 아닐때까지 읽어 rLine 배열에 넣는다
            while ((line = bfRead.readLine()) != null) {
                rLine[line_count] = line;
                line_count++;
            }
            bfRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //TextView의 TimeStamp클릭 시 이동
        SpannableString spannableString = null;
        for (int i = 0; i < line_count; i += 2) {
            Log.i("test",""+rLine[i]);
            spannableString = new SpannableString(rLine[i]);
            String click_time = rLine[i]; //String형 timeStamp필요

            //클릭 시 할 동작
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    String[] split = click_time.split(":");  //02:10에서 :제거
                    String M = split[0]; //02
                    String S = split[1]; //10

                    int mesc = 0;
                    int m = Integer.parseInt(M); //int로 변경
                    int s = Integer.parseInt(S); //int로 변경
                    m *= 60; //분을 초로 변경
                    mesc = m + s; //분 초 더해서
                    mesc *= 1000; //mesc형태로 변경

                    mp.seekTo(mesc);
                    seekBar.setProgress(mp.getCurrentPosition());
                    timeText.setText(timeFormat.format(mp.getCurrentPosition()));

                }
            };

            if(spannableString.toString().trim().length()>0) {
                spannableString.setSpan(clickableSpan, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.append(spannableString); //clickableSpan이 적용된 timeStamp
                textView.append("\n" + rLine[i + 1] + "\n\n"); //내용
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            }else {
                Log.i("test", "length == 0");
            }
        }


    }

}
