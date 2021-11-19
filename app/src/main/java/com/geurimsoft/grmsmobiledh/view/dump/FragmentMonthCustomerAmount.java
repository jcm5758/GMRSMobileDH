/**
 * 월별 거래처별 메인
 *
 * 2021. 11. 19.
 *
 * Written by jcm5758
 *
 */

package com.geurimsoft.grmsmobiledh.view.dump;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDailyInOut;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDailyInOutGroup;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDumpDay;
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.geurimsoft.grmsmobiledh.view.fragments.MonthCustomerStatsView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FragmentMonthCustomerAmount extends Fragment
{

    private int branchID = 0;

    private LinearLayout yi_month_enterprise_amount_income_empty_layout, yi_month_enterprise_amount_release_empty_layout, yi_month_enterprise_amount_petosa_empty_layout;
    private LinearLayout yi_month_enterprise_amount_loading_indicator, yi_month_enterprise_amount_loading_fail;

    private TextView yi_month_enterprise_amount_date, yi_month_enterprise_amount_income_title, yi_month_enterprise_amount_release_title, yi_month_enterprise_amount_petosa_title;

    private int iYear, iMonth;

    public FragmentMonthCustomerAmount(int branchID)
    {
        this.branchID = branchID;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.month_customer_amount, container, false);
        return v;
    }

    @Override
    public void onResume()
    {

        super.onResume();

        View view = this.getView();

        this.yi_month_enterprise_amount_income_empty_layout = (LinearLayout)view.findViewById(R.id.month_enterprise_amount_income_empty_layout);
        this.yi_month_enterprise_amount_release_empty_layout = (LinearLayout)view.findViewById(R.id.month_enterprise_amount_release_empty_layout);
        this.yi_month_enterprise_amount_petosa_empty_layout = (LinearLayout)view.findViewById(R.id.month_enterprise_amount_petosa_empty_layout);

        this.yi_month_enterprise_amount_loading_indicator = (LinearLayout)view.findViewById(R.id.month_enterprise_amount_loading_indicator);
        this.yi_month_enterprise_amount_loading_fail = (LinearLayout)view.findViewById(R.id.month_enterprise_amount_loading_fail);

        this.yi_month_enterprise_amount_date = (TextView)view.findViewById(R.id.month_enterprise_amount_date);
        this.yi_month_enterprise_amount_income_title = (TextView)view.findViewById(R.id.month_enterprise_amount_income_title);
        this.yi_month_enterprise_amount_release_title = (TextView)view.findViewById(R.id.month_enterprise_amount_release_title);
        this.yi_month_enterprise_amount_petosa_title = (TextView)view.findViewById(R.id.month_enterprise_amount_petosa_title);

        makeData(GSConfig.DAY_STATS_YEAR, GSConfig.DAY_STATS_MONTH);

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    /**
     * 데이터 조회
     * @param _year 연도
     * @param _monthOfYear 월
     */
    private void makeData(int _year, int _monthOfYear)
    {

        String functionName = "makeData()";

        try
        {

            String dateStr = _year + "년 " + _monthOfYear + "월  입출고 현황";

            if (GSConfig.IsDebugging)
                Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + _year + "년 " + _monthOfYear + "월");

            yi_month_enterprise_amount_date.setText(dateStr);

            this.getData(_year, _monthOfYear);

        }
        catch(Exception ex)
        {
            Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
            return;
        }

    }

    /**
     * 서버에 데이터 요청
     * @param searchYear 년
     * @param searchMonth 월
     */
    private void getData(int searchYear, int searchMonth)
    {

        String functionName = "getData()";

        if (GSConfig.IsDebugging)
            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "searchYear : " + searchYear + ", searchMonth : " + searchMonth);

        this.iYear = searchYear;
        this.iMonth = searchMonth;

        String url = GSConfig.API_SERVER_ADDR;
        RequestQueue requestQueue = Volley.newRequestQueue(GSConfig.context);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                //응답을 잘 받았을 때 이 메소드가 자동으로 호출
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseData(response);
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
                params.put("GSType", "DUMP_MONTH_CUSTOMER");
                params.put("GSQuery", "{ \"BranchID\" : " + branchID + ", \"SearchYear\": " + searchYear + ", \"SearchMonth\": " + searchMonth + ", \"VehicleNum\" : \"" + GSConfig.CURRENT_USER.userinfo.VehicleNum + "\" }");
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // 이전 결과 있어도 새로 요청하여 응답을 보여준다.
        request.setShouldCache(false);
        requestQueue.add(request);

        if (GSConfig.IsDebugging)
            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "요청 보냄.");

    }

    /**
     * 수신 데이터의 클래스화
     * @param msg
     */
    public void parseData(String msg)
    {

        String functionName = "parseData()";

        if (GSConfig.IsDebugging)
            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + msg);

        try
        {

            Gson gson = new Gson();
            GSDumpDay serviceData = gson.fromJson(msg, GSDumpDay.class);

            this.setDisplayData(serviceData);

        }
        catch(Exception ex)
        {
            Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
            return;
        }

    }

    private void setDisplayData(GSDumpDay data)
    {

        if (data == null)
        {
            Log.d(GSConfig.APP_DEBUG, "DEBUGGING : " + this.getClass().getName() + " : setDisplayData() : data is null.");
            return;
        }

        yi_month_enterprise_amount_income_empty_layout.removeAllViews();
        yi_month_enterprise_amount_release_empty_layout.removeAllViews();
        yi_month_enterprise_amount_petosa_empty_layout.removeAllViews();

        // 전체 데이터가 없으면 표출 안함
        if (data.isNullOrEmptyAll())
        {

            yi_month_enterprise_amount_income_title.setVisibility(View.VISIBLE);
            yi_month_enterprise_amount_income_title.setText("조회된 데이터가 없습니다.");

            yi_month_enterprise_amount_income_empty_layout.setVisibility(View.GONE);

            yi_month_enterprise_amount_release_title.setVisibility(View.GONE);
            yi_month_enterprise_amount_release_empty_layout.setVisibility(View.GONE);

            yi_month_enterprise_amount_petosa_title.setVisibility(View.GONE);
            yi_month_enterprise_amount_petosa_empty_layout.setVisibility(View.GONE);

            return;

        }

        StatsViewMonthCustomer statsView = new StatsViewMonthCustomer(getActivity(), 3, iYear, iMonth, data.header);

        // 입고 내역이 없으면 텍스트 감추고 표 감추기
        // 전체는 있는데 입고는 있으면
        if (!data.isNullOrEmptyAll() && !data.isNullOrEmptyInput())
        {

            yi_month_enterprise_amount_income_title.setVisibility(View.VISIBLE);
            yi_month_enterprise_amount_income_empty_layout.setVisibility(View.VISIBLE);

            statsView.makeStatsView(yi_month_enterprise_amount_income_empty_layout, GSConfig.MODE_STOCK, data.serviceInput);

        }
        // 전체는 있는데 입고는 없으면
        else if (!data.isNullOrEmptyAll() && data.isNullOrEmptyInput())
        {
            yi_month_enterprise_amount_income_title.setVisibility(View.GONE);
            yi_month_enterprise_amount_income_empty_layout.setVisibility(View.GONE);
        }

        // 출고 내역이 없으면 텍스트 감추고 표 감추기
        // 전체는 있는데 출고는 있으면
        if (!data.isNullOrEmptyAll() && !data.isNullOrEmptyOutput())
        {

            yi_month_enterprise_amount_release_title.setVisibility(View.VISIBLE);
            yi_month_enterprise_amount_release_empty_layout.setVisibility(View.VISIBLE);

            statsView.makeStatsView(yi_month_enterprise_amount_release_empty_layout, GSConfig.MODE_RELEASE, data.serviceOutput);

        }
        // 전체는 있는데 출고는 없으면
        else if (!data.isNullOrEmptyAll() && data.isNullOrEmptyOutput())
        {
            yi_month_enterprise_amount_release_title.setVisibility(View.GONE);
            yi_month_enterprise_amount_release_empty_layout.setVisibility(View.GONE);
        }

        // 토사 내역이 없으면 텍스트 감추고 표 감추기
        // 전체는 있는데 토사는 있으면
        if (!data.isNullOrEmptyAll() && !data.isNullOrEmptySluge())
        {

            yi_month_enterprise_amount_petosa_title.setVisibility(View.VISIBLE);
            yi_month_enterprise_amount_petosa_empty_layout.setVisibility(View.VISIBLE);

            statsView.makeStatsView(yi_month_enterprise_amount_petosa_empty_layout, GSConfig.MODE_PETOSA, data.serviceSluge);

        }
        // 전체는 있는데 토사는 있으면
        else if (!data.isNullOrEmptyAll() && data.isNullOrEmptySluge())
        {
            yi_month_enterprise_amount_petosa_title.setVisibility(View.GONE);
            yi_month_enterprise_amount_petosa_empty_layout.setVisibility(View.GONE);
        }

    }

}