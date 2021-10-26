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

import androidx.annotation.Dimension;
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

    // 차량 번호 텍스트 뷰
    private TextView tvVehicleNum;

    // 품목 텍스트 뷰
    private TextView tvProduct;

    // 수량 텍스트 뷰
    private TextView tvUnit;

    // m3 텍스트 뷰
    private TextView tvUnit2;

    // 콘텐츠 텍스트 뷰
    private TextView tvCustomer;

    // 콘텐츠 텍스트 뷰
    private TextView tvLogisticCompany;

    // 콘텐츠 텍스트 뷰
    private TextView tvServiceTime;

    // 취소 버튼
    private Button btCancel;

    // 완료 버튼
    private Button btAccept;

    // 왼쪽 화살표
    private ImageView ivLeft;

    // 오른쪽 화살표
    private ImageView ivRight;

    // 넘어온 데이터 ID
    private int vehicleID;

    // 현재 Datalist의 몇번째 값인지 나타내는 정수형 데이터 (초기값 = 0 으로 설정)
    private GSVehicleData vehicleData = null;

    private int currentIter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.payloader_detail);

        // 타이틀 설정
        getSupportActionBar().setTitle("상세 정보");

        // 홈 버튼 사용 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 마지막 데이터 사용 여부
        GSConfig.last_item_activity_use = false;

        // 다른 액티비티에서 넘어온 intent와 같이 넘어온 값을 불러오기 위해 선언
        Intent intent = getIntent();

        // 넘어온 데이터 ID 값 파싱
        vehicleID = intent.getIntExtra("ID", 0);

        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "onCreate()") + "id : " + vehicleID);

        // Text view
        tvVehicleNum = findViewById(R.id.tvDetailVehicleNum);
        tvProduct = findViewById(R.id.tvDetailProduct);
        tvUnit = findViewById(R.id.tvDetailUnit);
        tvUnit2 = findViewById(R.id.tvDetailUnit2);
        tvCustomer = findViewById(R.id.tvDetailCustomer);
        tvLogisticCompany = findViewById(R.id.tvDetailLogisticCompany);
        tvServiceTime = findViewById(R.id.tvDetailServiceTime);

        // Button
        btCancel = findViewById(R.id.button_cancel);
        btAccept = findViewById(R.id.button_accept);

        // Image View
        ivLeft = findViewById(R.id.left);
        ivRight = findViewById(R.id.right);

        tvVehicleNum.setTextSize(Dimension.DP, GSConfig.FontSizeDetailVehicle);
        tvProduct.setTextSize(Dimension.DP, GSConfig.FontSizeDetailProduct);
        tvUnit.setTextSize(Dimension.DP, GSConfig.FontSizeDetailUnit);
        tvUnit2.setTextSize(Dimension.DP, GSConfig.FontSizeDetailUnit);
        tvCustomer.setTextSize(Dimension.DP, GSConfig.FontSizeDetailCustomer);
        tvLogisticCompany.setTextSize(Dimension.DP, GSConfig.FontSizeDetailLogisticCompany);
        tvServiceTime.setTextSize(Dimension.DP, GSConfig.FontSizeDetailServiceTime);

        btCancel.setTextSize(Dimension.DP, GSConfig.FontSizeDetailButton);
        btAccept.setTextSize(Dimension.DP, GSConfig.FontSizeDetailButton);

        // 현재 데이터 화면 표출
        setText();

        // 준비 데이터 아니면 취소, 확인 버튼 감추기
        if(!GSConfig.all_view)
        {
            btCancel.setVisibility(View.GONE);
            btAccept.setVisibility(View.GONE);
        }

        // 취소 버튼
        btCancel.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
            
        });

        // 완료 버튼
        btAccept.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                payloaderMakeDone(1, vehicleData.ID);
            }

        });

        // 이전 버튼 클릭
        ivLeft.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

//                Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "onCreate()") + "Left before : currentIter : " + currentIter);

                currentIter--;

                // 첫 항목이 아닐경우 이전 항목을 setText
                if( currentIter >= 0 )
                {
                    vehicleData = GSConfig.vehicleList.get(currentIter);
                    setText();
                }
                // 첫 항목일 경우 Toast를 이용하여 알림
                else
                {
                    currentIter++;
                    Toast.makeText(getBaseContext(),"첫 항목입니다.",Toast.LENGTH_SHORT).show();
                }

//                Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "onCreate()") + "Left after : currentIter : " + currentIter);

            }

        });

        // 다음 버튼 클릭
        ivRight.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

//                Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "onCreate()") + "Right before : currentIter : " + currentIter);

                currentIter++;

                // 마지막 항목이 아닐 경우 다음 항목을 setText
                if(currentIter >= 0 && currentIter < GSConfig.vehicleList.size())
                {
                    vehicleData = GSConfig.vehicleList.get(currentIter);
                    setText();
                }
                // 마지막 항목일 경우 새로운 창을 띄워 알림
                else
                {
                    currentIter--;
                    Toast.makeText(getBaseContext(),"마지막 항목입니다.",Toast.LENGTH_SHORT).show();
                }

//                Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "onCreate()") + "Right after : currentIter : " + currentIter);

            }

        });

        if ( vehicleData == null && vehicleID >= 0)
            findVehicleData( vehicleID );

    }

    /**
     * 현재 데이터의 정보를 화면에 출력
     */
    public void setText()
    {

        if (vehicleData == null)
        {
            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "setText()") + "vehicleData is null.");
            return;
        }

        tvVehicleNum.setText( vehicleData.VehicleNum );
        tvProduct.setText( vehicleData.Product );
        tvUnit.setText( String.valueOf(vehicleData.Unit) );
        tvCustomer.setText( vehicleData.CustomerName + "(" + vehicleData.CustomerSiteName + ")" );
        tvLogisticCompany.setText( vehicleData.LogisticCompany );
        tvServiceTime.setText( vehicleData.getServiceHour() );

    }

    /**
     * 현재 넘어온 ID 값을 전체데이터와 비교하여 index값을 찾아내는 함수
     * @param id 데이터 ID
     * @return 선택된 데이터의 index값
     */
    public void findVehicleData(int id)
    {

        for(int i = 0; i < GSConfig.vehicleList.size(); i++)
        {

            if(GSConfig.vehicleList.get(i).ID == id)
            {

                currentIter = i;

                vehicleData = GSConfig.vehicleList.get(i);

                setText();

                return;

            }

        }

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

                            GSConfig.vehicleList.remove(currentIter);

                            currentIter = 0;

                            if ( GSConfig.vehicleList.size() == 0 )
                            {

                                Intent intent1 = new Intent(getApplication(), Payloader.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);

                                startActivity(intent1);

                                finish();

                            }

                            vehicleData = GSConfig.vehicleList.get(currentIter);
                            setText();

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
                params.put("GSQuery", "{ \"BranchID\" : \"" + GSConfig.CURRENT_BRANCH.branchID
                        + "\", \"ServiceType\" : \"" + serviceType
                        + "\", \"ServiceID\" : \"" + id + "\" }");
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "요청 보냄.");

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

    /**
     * 뒤로 가기 이벤트
     */
    public void onBackPressed()
    {

        super.onBackPressed();

        GSConfig.last_item_activity_use = false;

        finish();

    }

}