/**
 * 월별 수량
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
import android.widget.ListView;
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
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDumpMonth;
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class FragmentMonthAmount extends Fragment
{

    private int branchID = 0;

    private ListView yi_month_amount_listview;
    private TextView yi_month_amount_date;
    private LinearLayout yi_month_amount_header_container;
    private LinearLayout yi_month_amount_loading_indicator;
    private LinearLayout yi_month_amount_loading_fail;

    private String dateStr;

    public FragmentMonthAmount(int branchID)
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
        View v = inflater.inflate(R.layout.yi_month_amount, container,false);
        return v;
    }

    @Override
    public void onResume()
    {

        super.onResume();

        View view = this.getView();

        this.yi_month_amount_listview = (ListView)view.findViewById(R.id.yi_month_amount_listview);
        this.yi_month_amount_date = (TextView)view.findViewById(R.id.yi_month_amount_date);
        this.yi_month_amount_header_container = (LinearLayout)view.findViewById(R.id.yi_month_amount_header_container);

        this.yi_month_amount_loading_indicator = (LinearLayout)view.findViewById(R.id.yi_month_amount_loading_indicator);
        this.yi_month_amount_loading_fail = (LinearLayout)view.findViewById(R.id.yi_month_amount_loading_fail);

        this.yi_month_amount_listview.setDividerHeight(0);

        makeMonthData(GSConfig.DAY_STATS_YEAR, GSConfig.DAY_STATS_MONTH);

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    private void makeMonthData(int _year, int _monthOfYear)
    {

        String functionName = "makeMonthData()";

        try
        {

            dateStr = _year + "년 " + _monthOfYear + "월  입출고 현황(단위:루베)";

            if (GSConfig.IsDebugging)
                Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + _year + "년 " + _monthOfYear + "월");

            yi_month_amount_date.setText(dateStr);

            this.getData(_year, _monthOfYear);

        }
        catch(Exception ex)
        {
            Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
            return;
        }

    }

    /**
     * 데이터 불러오기
     * @param searchYear 검색 연도
     * @param searchMonth 검색 월
     */
    private void getData(int searchYear, int searchMonth)
    {

        String functionName = "getData()";

        if (GSConfig.IsDebugging)
            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "searchYear : " + searchYear + ", searchMonth : " + searchMonth);

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
                params.put("GSType", "DUMP_MONTH");
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
     * 수신 데이터 파싱하여 클래스화
     * @param msg 수신 데이터
     */
    public void parseData(String msg)
    {

        String functionName = "parseData()";

        if (GSConfig.IsDebugging)
            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + msg);

        try
        {

            Gson gson = new Gson();
            GSDumpMonth dumpData = gson.fromJson(msg, GSDumpMonth.class);

            this.setDisplayData(dumpData);

        }
        catch(Exception ex)
        {
            Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
            return;
        }

    }

    /**
     * 파싱 데이터를 테이블에 출력
     * @param data
     */
    private void setDisplayData(GSDumpMonth data)
    {

        yi_month_amount_header_container.removeAllViews();

        if (data != null && data.ServiceData != null)
        {

            StAdapterMonth adapter = new StAdapterMonth(getActivity(), data);

            StatsHeaderAndFooterView sVeiw = new StatsHeaderAndFooterView(getActivity());
            sVeiw.makeHeaderView(yi_month_amount_header_container, data.header);

            View foot = View.inflate(getActivity(), R.layout.stats_foot, null);
            LinearLayout footer_layout = (LinearLayout)foot.findViewById(R.id.stats_footer_container);
            sVeiw.makeFooterView(footer_layout, data.FinalData);

            yi_month_amount_listview.addFooterView(foot);
            yi_month_amount_listview.setAdapter(adapter);

        }

    }

}