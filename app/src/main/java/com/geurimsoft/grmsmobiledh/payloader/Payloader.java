package com.geurimsoft.grmsmobiledh.payloader;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 품목, 새로고침 시간 수정 가능
 * 1열/2열로 전체데이터, 1개 데이터 보기 구분 가능
 * 준비/완료 데이터 각각 볼 수 있음
 */
public class Payloader extends AppCompatActivity
{

    // 전역 컨텍스트
    public static Context CONTEXT;

    // RecyclerView
    private RecyclerView recyclerView;

    // Image view 왼쪽 버튼
    private ImageView check, TD, listv, card, one;

    // 리사이클 어댑터
    public RecyclerAdapter adapter;

    // 품목과 새로고침 시간 나타내는 목록
    private Spinner spinner_product, spinner_time;

    // 핸들러
    private static Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.payloader_list);

        // Context 설정
        CONTEXT = this;

        // 레이아웃 객체 받아오기
        check = findViewById(R.id.button_check);
        TD = findViewById(R.id.button_TD);
        listv = findViewById(R.id.button_list);
        card = findViewById(R.id.button_card);
        one = findViewById(R.id.button_One);
        spinner_product = findViewById(R.id.spinner_product);
        spinner_time = findViewById(R.id.spinner_time);
        recyclerView = findViewById(R.id.recyclerView);

        // recyclerView용 어댑터 설정
        adapter = new RecyclerAdapter();

        // Last_Item의 액티비티가 활성화 되어 있는지 나타내는 변수
        GSConfig.last_item_activity_use = false;

        // 액션바 Title 수정
        getSupportActionBar().setTitle("준비 목록");

        // 액션바 뒤로가기 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 준비 버튼, list 형식 버튼 활성화
        TD.setImageResource(R.drawable.ready_1);
        listv.setImageResource(R.drawable.list_1);

        // 환경 변수 설정
        GSConfig.setting = getSharedPreferences("setting",0);
        GSConfig.editor = GSConfig.setting.edit();

        // 타이머를 위한 핸들러
        mHandler = new Handler();

        // 버튼 클릭 이벤트 적용
        // 데이터 조회 타이머 실행
        this.initBasic();

        // 품목 조회
        this.loadProuctList();

    }

    // payloader 의 창이 재시작될때마다 선택된 1열 / 2열 의 방식으로 최신화된 데이터 나열
    public void onResume()
    {

        super.onResume();

        // 리스트 뷰 갱신
        this.initListView();

    }

    /**
     * GSConfig.list_view에 따라 1열 또는 2열로 배치
     */
    public void initListView()
    {

        if(GSConfig.list_view)
        {
            initListOne();
        }
        else
        {
            initListTwo();
        }

        adapter.notifyDataSetChanged();

    }

    // RecyclerView 의 화면 나열 설정(1열)
    public void initListOne()
    {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    // RecyclerView 의 화면 나열 설정(2열)
    public void initListTwo()
    {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.payloader_menu, menu);
        return true;
    }

    /**
     * 품목, 새로고침 시간, 해당내용 적용 의 옵션을 선택했을때의 이벤트
     * @param item
     * @return true
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            // 액션바 뒤로가기 버튼
            case android.R.id.home:
                onBackPressed();
                return true;

            // 품목 선택
            case R.id.product_pick:

                spinner_product.performClick();
                spinner_product.setVisibility(View.VISIBLE);
                spinner_time.setVisibility(View.GONE);

                // 선택된 품목으로 GSConfig product_pick_use 변경
                spinner_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {

                        GSConfig.product_pick_use = (String) parent.getItemAtPosition(position);

                        GSConfig.editor.putString("product", GSConfig.product_pick_use);
                        GSConfig.editor.commit();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
                        return;
                    }

                });

                return true;

            // 새로고침 시간 선택
            case R.id.time_pick:

                spinner_time.performClick();
                spinner_time.setVisibility(View.VISIBLE);
                spinner_product.setVisibility(View.GONE);

                // 선택된 품목으로 GSConfig time_pick_use 변경
                spinner_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {

                        String cTime = (String)parent.getItemAtPosition(position);
                        cTime = cTime.replace("분", "");

                        int time = Integer.parseInt(cTime);

                        GSConfig.time_pick_use = time * 60000;
                        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "onOptionsItemSelected()") + GSConfig.time_pick_use);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
                        return;
                    }

                });

                return true;

            // 적용 선택
            case R.id.select:

                // 품목, 새로고침 시간 시야에서 제거
                spinner_product.setVisibility(View.GONE);
                spinner_time.setVisibility(View.GONE);

                // 선택된 품목이 없으면 메시지 출력
                if(GSConfig.product_pick_use.equals(""))
                {
                    Toast.makeText(CONTEXT,"품목 선택",Toast.LENGTH_SHORT).show();
                    return false;
                }

                // 데이터 조회
                this.loadData();

        }

        return super.onOptionsItemSelected(item);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onBackPressed()
    {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    // 품목 리스트
    private void setProductSpinner(List<String> productList)
    {

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, productList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinner_product.setAdapter(spinnerAdapter);

    }

    // 시간 리스트
    private void setTimeSpinner(List<String> timeList)
    {

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinner_time.setAdapter(spinnerAdapter);

    }

    /**
     * 데이터 로드 모듈
     * 준비된 데이터 혹은 완료 데이터 조회
     */
    public void loadData()
    {

        // 준비 데이터 조회
        if(GSConfig.all_view)
        {
            loadPayloader(1, GSConfig.product_pick_use, 0);
        }
        // 완료 데이터 조회
        else
        {
            loadPayloader(1, GSConfig.product_pick_use, 1);
        }

    }

    /**
     * 품목 리스트 받아오기
     * @return
     */
    private boolean loadProuctList()
    {

        String functionName = "loadProuctList()";

        String url = GSConfig.API_SERVER_ADDR;
        RequestQueue requestQueue = Volley.newRequestQueue(GSConfig.context);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                //응답을 잘 받았을 때 이 메소드가 자동으로 호출
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {

                        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "응답 -> " + response);

                        Gson gson = new Gson();
                        GSPayloaderList loader = gson.fromJson(response, GSPayloaderList.class);

                        if (loader != null && loader.getData() != null)
                        {

                            GSPayloaderProductTime pt = loader.getData();

                            if (pt != null && pt.getProductArray() != null && pt.getProductArray().size() > 0)
                                setProductSpinner(pt.getProductArray());

                            if (pt != null && pt.getTimeArray() != null && pt.getTimeArray().size() > 0)
                                setTimeSpinner(pt.getTimeArray());

                        }

                    }

                },
                //에러 발생시 호출될 리스너 객체
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "에러 -> " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("GSType", "PAYLOADER_PRODUCT_LIST");
                params.put("GSQuery", "{ \"BranchID\" : \"" + GSConfig.CURRENT_BRANCH.getBranchID() + "\", \"ServiceType\" : \"1\"}");
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "요청 보냄.");

        return true;

    }

    /**
     * 리스트 받아오기
     * @return
     */
    public void loadPayloader(int serviceType, String product, int isLoaded)
    {

        String functionName = "loadPayloader()";

        String date = GSConfig.getCurrentDate();
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG("Palyloader", functionName) + date);

        String url = GSConfig.API_SERVER_ADDR;
        RequestQueue requestQueue = Volley.newRequestQueue(GSConfig.context);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                //응답을 잘 받았을 때 이 메소드가 자동으로 호출
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {

                        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "응답 -> " + response);

                        Gson gson = new Gson();
                        GSPayloaderServiceDataTop top = gson.fromJson(response, GSPayloaderServiceDataTop.class);

                        if (top != null)
                        {

                            GSConfig.vehicleList = top;
//                            GSConfig.vehicleList.print();

                            // 리스트 뷰 갱신
                            initListView();

                        }

                    }

                },
                //에러 발생시 호출될 리스너 객체
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "에러 -> " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("GSType", "PAYLOADER_DAILY");
                params.put("GSQuery", "{ \"BranchID\" : \"" + GSConfig.CURRENT_BRANCH.getBranchID()
                        + "\", \"ServiceType\" : \"" + serviceType
                        + "\", \"ServiceDate\" : \"" + date
                        + "\", \"Product\" : \"" + product
                        + "\", \"IsLoaded\" : \"" + isLoaded + "\" }");
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "요청 보냄.");

    }

    /**
     * 버튼 클릭 이벤트 적용
     * 데이터 조회 타이머 실행
     */
    private void initBasic()
    {

        // 완료 버튼 클릭이벤트
        check.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                getSupportActionBar().setTitle("완료 목록");

                check.setImageResource(R.drawable.check_1);
                TD.setImageResource(R.drawable.ready_2);

                // 준비데이터에서 완료데이터로 변경
                GSConfig.all_view = false;

                // 품목 선택이 안되어있을경우
                if( GSConfig.product_pick_use.equals("") )
                {
                    Toast.makeText(CONTEXT,"품목 선택",Toast.LENGTH_SHORT).show();
                    return;
                }

                // 데이터 조회
                loadData();

            }

        });

        // 준비 버튼 클릭 이벤트
        TD.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                getSupportActionBar().setTitle("준비 목록");

                check.setImageResource(R.drawable.check_2);
                TD.setImageResource(R.drawable.ready_1);

                // 준비데이터로 변경
                GSConfig.all_view = true;

                // 품목 선택이 안되어있을경우
                if( GSConfig.product_pick_use.equals("") )
                {
                    Toast.makeText(CONTEXT,"품목 선택",Toast.LENGTH_SHORT).show();
                    return;
                }

                // 데이터 조회
                loadData();

            }

        });

        // 1열로 나열
        listv.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                listv.setImageResource(R.drawable.list_1);
                card.setImageResource(R.drawable.card_2);
                one.setImageResource(R.drawable.one_2);

                // 1열로 보기 전환
                GSConfig.list_view = true;

                initListView();

            }

        });

        // 2열로 나열
        card.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                listv.setImageResource(R.drawable.list_2);
                card.setImageResource(R.drawable.card_1);
                one.setImageResource(R.drawable.one_2);

                // 2열로 보기 전환
                GSConfig.list_view = false;

                initListView();

            }

        });

        // 1개씩 보기 클릭 이벤트
        one.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                // 클릭했을때, 품목 선택이 안되어있을경우
                if(GSConfig.product_pick_use.equals(""))
                {
                    Toast.makeText(CONTEXT,"품목 선택",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Intent intent = null;

                    // 클릭했을때, 품목이 있으나,
                    // Data가 없을 경우
                    // Last_Item 액티비티 활성화
                    if(GSConfig.vehicleList.size() == 0)
                    {
                        intent = new Intent(getBaseContext(), LastItem.class);
                    }
                    // 클릭했을때, Data와 품목 모두 있을경우,
                    // 해당 품목의 첫번째 ID를 넘겨주며
                    // ItemActivity 활성화
                    else
                    {
                        intent = new Intent(getBaseContext(), ItemActivity.class);
                        intent.putExtra("ID", GSConfig.vehicleList.get(0).ID);
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);

                    startActivity(intent);

                }

            }

        });

        // 핸들러로 전달할 runnable 객체, 수신 스레드 실행
        final Runnable runnable = new Runnable()
        {

            @Override
            public void run()
            {

                // 사용자가 품목을 선택하면 최신화
                if(!GSConfig.product_pick_use.equals(""))
                {
                    loadData();
                }

                // 현재 Last_item 실행중 여부
                if(GSConfig.last_item_activity_use)
                {

                    // 최신화로 Datalist의 개수가 0개에서 늘어났을 경우
                    if( GSConfig.vehicleList.size() > 0 )
                    {

                        // last_item 액티비티 종료
                        LastItem last = (LastItem)LastItem.Last_item_activity;
                        last.finish();

                        // ItemActivity 활성화 시작
                        Intent intent = new Intent(getBaseContext(), ItemActivity.class);
                        intent.putExtra("ID", GSConfig.vehicleList.get(0).ID);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);

                        startActivity(intent);

                    }

                }

            }

        };

        /**
         * 사용자가 정의한 새로고침 시간만큼 대기하는 함수
         */
        class NewRunnable implements Runnable
        {

            @Override
            public void run()
            {

                while(true)
                {

                    try
                    {
                        // 기본 설정된 1분마다 최신화 시행,
                        // 이후 액션바에서 선택한 주기로 설정값(time_pick_use) 변경
                        Thread.sleep(GSConfig.time_pick_use);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    mHandler.post(runnable);

                }

            }

        }

        NewRunnable nr = new NewRunnable();
        Thread t = new Thread(nr);
        t.start();

    }

}