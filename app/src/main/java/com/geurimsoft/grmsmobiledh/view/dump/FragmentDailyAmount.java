/**
 * 일보 수량
 *
 * 2021. 11. 17.
 *
 *  Written by jcm5758
 *
 */

package com.geurimsoft.grmsmobiledh.view.dump;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDumpDay;
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.geurimsoft.grmsmobiledh.util.GSUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class FragmentDailyAmount extends Fragment
{

    private LinearLayout income_empty_layout, release_empty_layout, petosa_empty_layout;
    private TextView stats_daily_date, daily_income_title, daily_release_title, daily_petosa_title;

    private LinearLayout loading_indicator, loading_fail;

    public FragmentDailyAmount() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.statviewdetaildaily, container, false);
        return v;
    }

    @Override
    public void onResume()
    {

        super.onResume();

        String functionName = "onResume()";

        View view = this.getView();

        if(view == null)
        {
            Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + " StatsDailyAmountFragment onResume View is null");
            return;
        }

        this.income_empty_layout = (LinearLayout)view.findViewById(R.id.income_empty_layout);
        this.release_empty_layout = (LinearLayout)view.findViewById(R.id.release_empty_layout);
        this.petosa_empty_layout = (LinearLayout)view.findViewById(R.id.petosa_empty_layout);

        this.loading_indicator = (LinearLayout)view.findViewById(R.id.loading_indicator);
        this.loading_fail = (LinearLayout)view.findViewById(R.id.loading_fail);

        this.stats_daily_date = (TextView)view.findViewById(R.id.stats_daily_date);

        this.daily_income_title = (TextView) view.findViewById(R.id.daily_income_title);
        this.daily_release_title = (TextView) view.findViewById(R.id.daily_release_title);
        this.daily_petosa_title = (TextView) view.findViewById(R.id.daily_petosa_title);

        // 일일 입고/출고/토사 수량 조회
        makeDailyAmountData(GSConfig.DAY_STATS_YEAR, GSConfig.DAY_STATS_MONTH,GSConfig.DAY_STATS_DAY);

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    /**
     * 일일 입고/출고/토사 수량 조회
     *
     * @param _year				연도
     * @param _monthOfYear		월
     * @param _dayOfMonth		일
     */
    public void makeDailyAmountData(int _year, int _monthOfYear, int _dayOfMonth)
    {

        String functionName = "makeDailyAmountData()";

        try
        {

            String str = _year + "년 " + _monthOfYear + "월 " + _dayOfMonth + "일";

            if (GSConfig.IsDebugging)
                Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + _year + "년 " + _monthOfYear + "월 " + _dayOfMonth + "일");

            this.stats_daily_date.setText(str);

            String queryDate = GSUtil.makeStringFromDate(_year, _monthOfYear, _dayOfMonth);
            String qryContent = "Unit";

            this.getData(queryDate, qryContent);

        }
        catch(Exception ex)
        {
            Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
            return;
        }

    }

    private void getData(String searchDate, String qryContent)
    {

        String functionName = "getData()";

        if (GSConfig.IsDebugging)
            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "searchDate : " + searchDate + ", qryContent : " + qryContent);

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
                params.put("GSType", "DUMP_DAY");
                params.put("GSQuery", "{ \"VehicleNum\" : " + GSConfig.CURRENT_USER.userinfo.VehicleNum + ", \"ServiceDate\": " + searchDate + "}");
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

    public void parseData(String msg)
    {

        String functionName = "parseData()";

        if (GSConfig.IsDebugging)
            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + msg);

        Gson gson = new Gson();
        GSDumpDay dumpDay = gson.fromJson(msg, GSDumpDay.class);

        this.setDisplayData(dumpDay);

    }

    private void setDisplayData(GSDumpDay data)
    {

        if(getActivity() == null)
        {
            Log.e(GSConfig.APP_DEBUG, "ERROR : " + this.getClass().getName() + " : setDisplayData() : Activity is null.");
            return;
        }

        if (data == null)
        {
            Log.e(GSConfig.APP_DEBUG, "ERROR : " + this.getClass().getName() + " : setDisplayData() : data is null.");
            return;
        }

        income_empty_layout.removeAllViews();
        release_empty_layout.removeAllViews();
        petosa_empty_layout.removeAllViews();

        // 전체 데이터가 없으면 표출 안함
        if (data.isNullOrEmptyAll())
        {

            daily_income_title.setVisibility(View.VISIBLE);
            daily_income_title.setText("조회된 데이터가 없습니다.");

            income_empty_layout.setVisibility(View.GONE);

            daily_release_title.setVisibility(View.GONE);
            release_empty_layout.setVisibility(View.GONE);

            daily_petosa_title.setVisibility(View.GONE);
            petosa_empty_layout.setVisibility(View.GONE);

            return;

        }

        StatsView statsView = new StatsView(getActivity(), data);

        // 입고 내역이 없으면 텍스트 감추고 표 감추기
        // 전체는 있는데 입고는 있으면
        if (!data.isNullOrEmptyAll() && !data.isNullOrEmptyInput())
        {

            daily_income_title.setVisibility(View.VISIBLE);
            income_empty_layout.setVisibility(View.VISIBLE);

            statsView.makeTableView(income_empty_layout, data.serviceInput);

        }
        // 전체는 있는데 입고는 없으면
        else if (!data.isNullOrEmptyAll() && data.isNullOrEmptyInput())
        {
            daily_income_title.setVisibility(View.GONE);
            income_empty_layout.setVisibility(View.GONE);
        }

        // 출고 내역이 없으면 텍스트 감추고 표 감추기
        // 전체는 있는데 출고는 있으면
        if (!data.isNullOrEmptyAll() && !data.isNullOrEmptyOutput())
        {

            daily_release_title.setVisibility(View.VISIBLE);
            release_empty_layout.setVisibility(View.VISIBLE);

            statsView.makeTableView(release_empty_layout, data.serviceOutput);

        }
        // 전체는 있는데 출고는 없으면
        else if (!data.isNullOrEmptyAll() && data.isNullOrEmptyOutput())
        {
            daily_release_title.setVisibility(View.GONE);
            release_empty_layout.setVisibility(View.GONE);
        }

        // 토사 내역이 없으면 텍스트 감추고 표 감추기
        // 전체는 있는데 토사는 있으면
        if (!data.isNullOrEmptyAll() && !data.isNullOrEmptySluge())
        {

            daily_petosa_title.setVisibility(View.VISIBLE);
            petosa_empty_layout.setVisibility(View.VISIBLE);

            statsView.makeTableView(petosa_empty_layout, data.serviceSluge);

        }
        // 전체는 있는데 토사는 있으면
        else if (!data.isNullOrEmptyAll() && data.isNullOrEmptySluge())
        {
            daily_petosa_title.setVisibility(View.GONE);
            petosa_empty_layout.setVisibility(View.GONE);
        }

    }

}