package com.geurimsoft.grmsmobiledh.view.dump;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.apiserver.data.ResultStatus;
import com.geurimsoft.grmsmobiledh.apiserver.data.ServicePredict;
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.geurimsoft.grmsmobiledh.util.GSUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class DumpActivity  extends Activity
{

    private TextView tvServiceType;
    private TextView tvVehicleNum;
    private TextView tvCustomerName;
    private TextView tvCustomerSiteName;
    private TextView tvLogisticCompany;
    private TextView tvProduct;
    private TextView tvUnit;
    private TextView tvServiceDate;
    private Button btCanel;
    private Button btOK;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dump_main);

        GSConfig.activities.add(DumpActivity.this);

        // TextView 객체 생성
        this.initTextViewObject();

        // TextView 초기화
        this.initTextView();

        // TextView 채우기
        this.fillTextView();

    }

    /**
     * TextView 객체 생성
     */
    private void initTextViewObject()
    {

        this.tvServiceType = (TextView)findViewById(R.id.tvDumpServiceType);
        this.tvVehicleNum = (TextView)findViewById(R.id.tvDumpVehicleNum);
        this.tvCustomerName = (TextView)findViewById(R.id.tvDumpCustomerName);
        this.tvCustomerSiteName = (TextView)findViewById(R.id.tvDumpCustomerSiteName);
        this.tvLogisticCompany = (TextView)findViewById(R.id.tvDumpLogisticCompany);
        this.tvProduct = (TextView)findViewById(R.id.tvDumpProduct);
        this.tvUnit = (TextView)findViewById(R.id.tvDumpUnit);
        this.tvServiceDate = (TextView)findViewById(R.id.tvDumpServiceDate);
        this.btCanel = (Button)findViewById(R.id.btDumpCancel);
        this.btOK = (Button)findViewById(R.id.btDumpOK);

        // 취소 버튼 이벤트
        this.btCanel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });

        // 확인 버튼 이벤트
        this.btOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendData();
            }

        });

    }

    /**
     * TextView 초기화
     */
    private void initTextView()
    {

        this.tvServiceType.setText("");
        this.tvVehicleNum.setText("");
        this.tvCustomerName.setText("");
        this.tvCustomerSiteName.setText("");
        this.tvLogisticCompany.setText("");
        this.tvProduct.setText("");
        this.tvUnit.setText("");
        this.tvServiceDate.setText("");

    }

    /**
     * TextView 값 채우기
     */
    private void fillTextView()
    {

        String fn = "fillTextView()";

        ServicePredict sp = GSConfig.CURRENT_USER.getSerivcePredict(0);

        if (sp == null)
        {
            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), fn) + ": ServicePredict is null");
            finish();
        }

        this.tvServiceType.setText(GSConfig.MODE_NAMES[sp.ServiceType]);
        this.tvVehicleNum.setText(sp.VehicleNum);
        this.tvCustomerName.setText(sp.CustomerName);
        this.tvCustomerSiteName.setText(sp.CustomerSiteName);
        this.tvLogisticCompany.setText(sp.LogisticCompany);
        this.tvProduct.setText(sp.Product);
        this.tvUnit.setText(Integer.toString(sp.Unit));
        this.tvServiceDate.setText(GSUtil.makeDashStringFromInt(sp.ServiceDate));

    }

    /**
     * 입력 값 확인
     */
    private void sendData()
    {

        String functionName = "sendData()";

        String url = GSConfig.API_SERVER_ADDR;
        RequestQueue requestQueue = Volley.newRequestQueue(GSConfig.context);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                //응답을 잘 받았을 때 이 메소드가 자동으로 호출
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "응답 -> " + response);

                        Gson gson = new Gson();
                        ResultStatus rs = gson.fromJson(response, ResultStatus.class);

                        if (rs.getStatus().equals("OK"))
                        {
                            Toast.makeText(getApplicationContext(), "정상 등록되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
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
                params.put("GSType", "DUMP_ADD");
                params.put("GSQuery", "{ \"Name\" : \"" + GSConfig.CURRENT_USER.userinfo.Name + "\", \"ID\" : \"" + GSConfig.CURRENT_USER.getSerivcePredict(0).ID + "\"}");
                return params;
            }
        };

        // 이전 결과 있어도 새로 요청하여 응답을 보여준다.
        request.setShouldCache(false);
        requestQueue.add(request);

		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "요청 보냄.");

    }

}
