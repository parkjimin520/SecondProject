package com.example.secondproject;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

//가로모드 - 원격근무
public class Horizontal1 extends AppCompatActivity {

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
    boolean isPlaying = false; //재생중인지 확인할 변수
    TextView timeText; //재생바의 타임스탬프

    SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

    private Menu menu;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        //음악 뷰
        timeText = (TextView) findViewById(R.id.timeText);
        ImageButton play_button = (ImageButton) findViewById(R.id.play_button);
        ImageButton stop_button = (ImageButton) findViewById(R.id.stop_button);
        ImageButton pause_button = (ImageButton) findViewById(R.id.pause_button);

        //썸네일
        ImageView figureImage = (ImageView) findViewById(R.id.figureImage);
        TextView keyword = (TextView) findViewById(R.id.keyword);

        //음원
        mp = MediaPlayer.create(Horizontal1.this, R.raw.trumanshow_mp);
        TextView length = (TextView) findViewById(R.id.length);
        length.setText(timeFormat.format(mp.getDuration()));

        //상단바----------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back버튼

        //제목, 이미지position 받아오기
        Intent intent = getIntent();
        if(intent.hasExtra("제목")){
            String titleIntent = intent.getStringExtra("제목");
            getSupportActionBar().setTitle(titleIntent);
        }

        //시크바
        seekBar = ((CustomSeekBar) findViewById(R.id.seekbar));
        initDataToSeekbar();
        seekBar.setVisibility(ProgressBar.VISIBLE);
        seekBar.setMax(mp.getDuration());

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
//                String time = timeText.getText().toString();
//                String fullText = textView.getText().toString();
//                if (fullText.contains(time)) {  //본문에 timeStamp가 있다면
//                    //자동스크롤
//                    int index1 = fullText.indexOf(time);
//                    int index2 = textView.getLayout().getLineForOffset(index1);
//
//                    AutoScroll = ObjectAnimator.ofInt(scrollView, "scrollY", textView.getLayout().getLineTop(index2)).setDuration(700);
//                    if (!touchScroll) { //텍스트뷰 안 만졌으면
//                        AutoScroll.start(); //자동 스크롤
//                    }
//
//                }

                String[] t = timeText.getText().toString().split(":"); //"00:00"
                String t_M = t[0];//"00"
                String t_S = t[1];//"00"
                String timeee = t_M + t_S;//"0000"
                Time = Integer.parseInt(timeee);//0000

                //이미지 매핑
                if(Time>=0 && Time<1016) {
                    figureImage.setImageResource(R.drawable.work1);
                    keyword.setText("점점 다양해지고 있는 근무형태 들에 대한 이야기");
                }else if(Time>=1017 && Time<1338) {
                    figureImage.setImageResource(R.drawable.work2);
                    keyword.setText("워케이션을 도입하는 회사들");
                }else if(Time>=1339 && Time<1621) {
                    figureImage.setImageResource(R.drawable.work3);
                    keyword.setText("원격 근무와 관련된 갈등과 논란들");
                }else if(Time>=1622 && Time<1815) {
                    figureImage.setImageResource(R.drawable.work4);
                    keyword.setText("원격근무가 늘어나는 이유");
                }else if(Time>=1816 && Time<2302) {
                    figureImage.setImageResource(R.drawable.work5);
                    keyword.setText("사무실 출근과 원격 근무의 비율 선택에 대한 설문 결과");
                }else if(Time>=2303 && Time<2928) {
                    figureImage.setImageResource(R.drawable.work6);
                    keyword.setText("원격 근무시 회사 지원이 가장 필요한 부분은 협업 툴의 개선이다.");
                }else if(Time>=2929 && Time<3412) {
                    figureImage.setImageResource(R.drawable.work7);
                    keyword.setText("원격 근무비를 비용으로 지원을 해달라는 의견에 대한 내용");
                }else if(Time>=3413 && Time<3705) {
                    figureImage.setImageResource(R.drawable.work8);
                    keyword.setText("원격 근무를 할 때 생산성 올리는 팁");
                }else if(Time>=3706 && Time<4625) {
                    figureImage.setImageResource(R.drawable.work9);
                    keyword.setText("사무실의 미래");
                }else if(Time>=4626 && Time<5128) {
                    figureImage.setImageResource(R.drawable.work10);
                    keyword.setText("(유료광고) 네이버 동네시장 장보기 서비스 소개");
                }else if(Time>=5129 && Time<5405) {
                    figureImage.setImageResource(R.drawable.work11);
                    keyword.setText("서비스 이용방법 안내");
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


        //슬라이딩패널----------------
        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.slidingPanel);
        recyclerView = (RecyclerView)findViewById(R.id.rv2);
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);//한줄에 4개씩
//        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new GridItemDecoration(30));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 8);//한줄에 n개씩
        recyclerView.setLayoutManager(gridLayoutManager);

        arrayList = new ArrayList<>();
        arrayList.add(new SlidingList(R.drawable.work1));
        arrayList.add(new SlidingList(R.drawable.work2));
        arrayList.add(new SlidingList(R.drawable.work3));
        arrayList.add(new SlidingList(R.drawable.work4));
        arrayList.add(new SlidingList(R.drawable.work5));
        arrayList.add(new SlidingList(R.drawable.work6));
        arrayList.add(new SlidingList(R.drawable.work7));
        arrayList.add(new SlidingList(R.drawable.work8));
        arrayList.add(new SlidingList(R.drawable.work9));
        arrayList.add(new SlidingList(R.drawable.work10));
        arrayList.add(new SlidingList(R.drawable.work11));


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

}
