package com.geurimsoft.grmsmobiledh.payloader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;
import java.util.Map;

/**
 * 1개의 데이터를 상세보기
 * 해당 데이터를 완료처리 가능
 * 화면에서 전/후 데이터 열람 가능
 * 마지막 데이터를 처리하거나 마지막데이터에서 다음 데이터를 불러오려는 경우 Last_Itme 액티비티 활성화
 */
public class ItemActivity extends AppCompatActivity
{

    private TextView vehicleNum, product, unit, content;
    private Button cancel, accept;
    private ImageView left, right;

    // 선택된 데이터의 ID값을 받아와 해당 데이터가 몇번째 데이터인지 알아내기 위해 선언
    private int id;

    // 현재 Datalist의 몇번째 값인지 나타내는 정수형 데이터 (초기값 = 0 으로 설정)
    private int value_num = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.payloader_detail);

        getSupportActionBar().setTitle("상세 정보");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GSConfig.last_item_activity_use = false;

        // 다른 액티비티에서 넘어온 intent와 같이 넘어온 값을 불러오기 위해 선언
        Intent intent = getIntent();

        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "onCreate()") + "id : " + intent.getIntExtra("ID", 0) );

        id = intent.getIntExtra("ID", 0);

        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "onCreate()") + "id : " + id);

        // ID값을 받아와 사용하여 몇번째 데이터인지 알아내는 함수 (value_num 에 저장)
        value_num = find_value_num( id );

        vehicleNum = findViewById(R.id.textView_vehicleNum_detail);
        product = findViewById(R.id.textView_product_detail);
        unit = findViewById(R.id.textView_unit_detail);
        content = findViewById(R.id.textView_content_detail);

        cancel = findViewById(R.id.button_cancel);
        accept = findViewById(R.id.button_accept);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);

        // 전체 Datalist 중 몇번째 데이터를 setText 하는지 정하여 문자 나열
        setText(value_num);

        if(!GSConfig.all_view)
        {
            cancel.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
        }

        // 취소 버튼
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 완료 버튼
        accept.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                // 현재 완료 처리할 Data의 id값을 소켓에 보내 처리
                payloaderMakeDone(1, GSConfig.vehicleList.get(value_num).ID);

            }

        });

        // 이전 버튼 클릭
        left.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                // 첫 항목이 아닐경우 이전 항목을 setText
                if(value_num > 0)
                {
                    value_num -= 1;
                    setText(value_num);
                }
                // 첫 항목일 경우 Toast를 이용하여 알림
                else
                {
                    setText(value_num);
                    Toast.makeText(getBaseContext(),"첫 항목입니다.",Toast.LENGTH_SHORT).show();
                }

            }

        });

        // 다음 버튼 클릭
        right.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                // 마지막 항목이 아닐 경우 다음 항목을 setText
                if(value_num != (GSConfig.vehicleList.size()-1))
                {
                    value_num+=1;
                    setText(value_num);
                }
                // 마지막 항목일 경우 새로운 창을 띄워 알림
                else
                {

                    Intent intent2 = new Intent(getApplication(), LastItem.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);

                    startActivity(intent2);

                    finish();

                }

            }

        });

    }

    // 액션바의 뒤로가기 버튼 클릭 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

        }

        return super.onOptionsItemSelected(item);

    }

    // Datalist에서 해당하는 num값에 있는 정보를 setText
    public void setText(int num)
    {

        GSPayloaderServiceDataDetail data = GSConfig.vehicleList.get(num);

        String content_text = data.getText();

        vehicleNum.setText(data.VehicleNum);
        product.setText(data.Product);
        unit.setText( String.valueOf(data.Unit) );
        content.setText(content_text);

    }

    /**
     * 현재 넘어온 ID 값을 전체데이터와 비교하여 index값을 찾아내는 함수
     * @param id
     * @return 선택된 데이터의 index값
     */
    public int find_value_num(int id)
    {

        for(int i = 0; i < GSConfig.vehicleList.size(); i++)
        {
            if(GSConfig.vehicleList.get(i).ID == id)
            {
                return i;
            }
        }

        return -1;

    }

    public void onBackPressed()
    {

        super.onBackPressed();

        GSConfig.last_item_activity_use = false;

        finish();

    }

    /**
     * 완료 처리
     * @return
     */
    public void payloaderMakeDone(int serviceType, int id)
    {

        String functionName = "payloaderMakeDone()";

        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG("Palyloader", functionName) + "ServiceType : " + serviceType + ", id : " + id);

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
                        GSPayloaderStatus result = gson.fromJson(response, GSPayloaderStatus.class);

                        if (result.status.equals("OK"))
                        {

                            if((Payloader)Payloader.CONTEXT!=null)
                            {

//                                if (GSConfig.all_view == true)
//                                {
//                                    Payloader.loadPayloader(1, GSConfig.product_pick_use, 0);
//                                }
//                                else
//                                {
//                                    Payloader.loadPayloader(1, GSConfig.product_pick_use, 1);
//                                }

                            }
                            else
                            {
                                Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "CONTEXT is null");
                            }

                            // 처리가 되었다면 총 개수가 1개씩 줄고 다음 데이터의 번호도 줄어있기때문에 value_num 값을 -1하여 다음 데이터값을 나타냄
                            value_num -= 1;
                            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "value_num : " + value_num);

                            // 처리 후, 현재 Data가 마지막이 아닐경우 다음 Data를 setText(해당 데이터가 처리되었으면 그 다음데이터가 해당번호로 나옴)
                            if(value_num != (GSConfig.vehicleList.size()-1))
                            {
                                value_num += 1;
                                setText(value_num);
                            }
                            // 처리 후, 현재 데이터가 마지막이었고, 이전 데이터가 있을경우 이전테이터를 setText
                            else if(value_num == (GSConfig.vehicleList.size()-1) && GSConfig.vehicleList.size()!=0)
                            {
                                setText(value_num);
                            }
                            // 마지막 Data를 완료하였을 경우 새로운 창을 띄워 알림
                            else
                            {

                                Intent intent1 = new Intent(getApplication(), LastItem.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);

                                startActivity(intent1);

                                finish();

                            }

//                            데이터를 처리한 후, 소켓통신으로 최신화된 데이터를 수신하여 setDatalist()로 저장
//                            Payloader.loadPayloader(1, GSConfig.product_pick_use, 0);

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
                params.put("GSType", "PAYLOADER_PROCESSING_FINISH");
                params.put("GSQuery", "{ \"BranchID\" : \"" + GSConfig.CURRENT_BRANCH.getBranchID()
                        + "\", \"ServiceType\" : \"" + serviceType
                        + "\", \"ServiceID\" : \"" + id + "\" }");
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "요청 보냄.");

    }

}