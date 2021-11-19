/**
 * 연보 수량
 *
 * 2021. 11. 17.
 *
 *  Written by jcm5758
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

public class FragmentYearAmount extends Fragment
{

    private int branchID = 0;

    private ListView yi_year_amount_listview;
    private TextView yi_year_amount_date;
    private LinearLayout yi_year_amount_header_container, yi_year_amount_loading_indicator, yi_year_amount_loading_fail;

    public FragmentYearAmount(int branchID)
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
        View v = inflater.inflate(R.layout.yi_year_amount, container,false);
        return v;
    }

    @Override
    public void onResume()
    {

        super.onResume();

        View view = this.getView();

        this.yi_year_amount_listview = (ListView)view.findViewById(R.id.yi_year_amount_listview);
        this.yi_year_amount_date = (TextView)view.findViewById(R.id.yi_year_amount_date);

        this.yi_year_amount_header_container = (LinearLayout)view.findViewById(R.id.yi_year_amount_header_container);

        this.yi_year_amount_loading_indicator = (LinearLayout)view.findViewById(R.id.yi_year_amount_loading_indicator);
        this.yi_year_amount_loading_fail = (LinearLayout)view.findViewById(R.id.yi_year_amount_loading_fail);

        this.yi_year_amount_listview.setDividerHeight(0);

        makeData(GSConfig.DAY_STATS_YEAR);

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    /**
     * 데이터 조회
     * @param _year 연도
     */
    private void makeData(int _year)
    {

        String functionName = "makeData()";

        try
        {

            String dateStr = _year + "년  입출고 현황(단위:루베)";

            if (GSConfig.IsDebugging)
                Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + _year + "년");

            yi_year_amount_date.setText(dateStr);

            this.getData(_year);

        }
        catch(Exception ex)
        {
            Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
            return;
        }

    }

    /**
     * 서버에 데이터 요청
     * @param searchYear 연도
     */
    private void getData(int searchYear)
    {

        String functionName = "getData()";

        if (GSConfig.IsDebugging)
            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "searchYear : " + searchYear);

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
                params.put("GSType", "DUMP_YEAR");
                params.put("GSQuery", "{ \"BranchID\" : " + branchID + ", \"SearchYear\": " + searchYear + ", \"VehicleNum\" : \"" + GSConfig.CURRENT_USER.userinfo.VehicleNum + "\" }");
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
     * 서버에서 받은 데이터를 클래스로 변환
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
            GSDumpMonth data = gson.fromJson(msg, GSDumpMonth.class);

            this.setDisplayData(data);

        }
        catch(Exception ex)
        {
            Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
            return;
        }

    }

    /**
     * 클래스 데이터를 테이블로 표출
     * @param data
     */
    private void setDisplayData(GSDumpMonth data)
    {

        yi_year_amount_header_container.removeAllViews();

        if (data != null && data.ServiceData != null)
        {

            StAdapterMonth adapter = new StAdapterMonth(getActivity(), data);

            StatsHeaderAndFooterView sVeiw = new StatsHeaderAndFooterView(getActivity());
            sVeiw.makeHeaderView(yi_year_amount_header_container, data.header);

            View foot = View.inflate(getActivity(), R.layout.stats_foot, null);
            LinearLayout footer_layout = (LinearLayout)foot.findViewById(R.id.stats_footer_container);
            sVeiw.makeFooterView(footer_layout, data.FinalData);

            yi_year_amount_listview.addFooterView(foot);
            yi_year_amount_listview.setAdapter(adapter);

        }

    }

}