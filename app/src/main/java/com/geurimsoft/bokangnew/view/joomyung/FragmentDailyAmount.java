/**
 * 일보 수량
 *
 * 2021. 05. 28. 리뉴얼
 *
 *  Written by jcm5758
 *
 */

package com.geurimsoft.bokangnew.view.joomyung;

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
import com.geurimsoft.bokangnew.R;
import com.geurimsoft.bokangnew.data.GSConfig;
import com.geurimsoft.bokangnew.util.GSUtil;
import com.geurimsoft.bokangnew.apiserver.data.GSDailyInOut;
import com.geurimsoft.bokangnew.apiserver.data.GSDailyInOutGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FragmentDailyAmount extends Fragment
{

	private LinearLayout income_empty_layout, release_empty_layout, petosa_empty_layout;
	private TextView stats_daily_date, daily_income_title, daily_release_title, daily_petosa_title;

	private LinearLayout income_empty_layout_outside, release_empty_layout_outside;
	private TextView daily_income_title_outside, daily_release_title_outside;

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

		this.income_empty_layout_outside = (LinearLayout)view.findViewById(R.id.income_empty_layout_outside);
		this.release_empty_layout_outside = (LinearLayout)view.findViewById(R.id.release_empty_layout_outside);
		
		this.loading_indicator = (LinearLayout)view.findViewById(R.id.loading_indicator);
		this.loading_fail = (LinearLayout)view.findViewById(R.id.loading_fail);
		
		this.stats_daily_date = (TextView)view.findViewById(R.id.stats_daily_date);

		this.daily_income_title = (TextView) view.findViewById(R.id.daily_income_title);
		this.daily_release_title = (TextView) view.findViewById(R.id.daily_release_title);
		this.daily_petosa_title = (TextView) view.findViewById(R.id.daily_petosa_title);

		this.daily_income_title_outside = (TextView) view.findViewById(R.id.daily_income_title_outside);
		this.daily_release_title_outside = (TextView) view.findViewById(R.id.daily_release_title_outside);

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

			String str = _year + "년 " + _monthOfYear + "월 " + _dayOfMonth + "일 입출고 현황";
//			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + _year + "년 " + _monthOfYear + "월 " + _dayOfMonth + "일");

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

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "searchDate : " + searchDate + ", qryContent : " + qryContent);

		String url = GSConfig.API_SERVER_ADDR + "API";
		RequestQueue requestQueue = Volley.newRequestQueue(GSConfig.context);

		StringRequest request = new StringRequest(
				Request.Method.POST,
				url,
				//응답을 잘 받았을 때 이 메소드가 자동으로 호출
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
//						Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "응답 -> " + response);
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
				params.put("GSType", "DAY");
				params.put("GSQuery", "{ \"branchID\" : " + GSConfig.CURRENT_BRANCH.getBranchID() + ", \"searchDate\": " + searchDate + ", \"qryContent\" : \"" + qryContent + "\" }");
				return params;
			}
		};

		request.setRetryPolicy(new DefaultRetryPolicy(
				0,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
		requestQueue.add(request);

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "요청 보냄.");

	}

	public void parseData(String msg)
	{

		String functionName = "parseData()";

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + msg);

		GSDailyInOut dio = new GSDailyInOut();

		Gson gson = new Gson();
		GSDailyInOutGroup[] diog = gson.fromJson(msg, GSDailyInOutGroup[].class);

		dio.list = new ArrayList<>(Arrays.asList(diog));

		this.setDisplayData(dio);

	}

	private void setDisplayData(GSDailyInOut dio)
	{

		if(getActivity() == null)
		{
			Log.e(GSConfig.APP_DEBUG, "ERROR : " + this.getClass().getName() + " : setDisplayData() : Activity is null.");
			return;
		}

		if (dio == null)
		{
			Log.e(GSConfig.APP_DEBUG, "ERROR : " + this.getClass().getName() + " : setDisplayData() : dio is null.");
			return;
		}

//		dio.print();

		income_empty_layout.removeAllViews();
		release_empty_layout.removeAllViews();
		petosa_empty_layout.removeAllViews();

		income_empty_layout_outside.removeAllViews();
		release_empty_layout_outside.removeAllViews();

		StatsView statsView = new StatsView(getActivity(), dio, 0);

		statsView.makeStockStatsView(income_empty_layout);
		GSDailyInOutGroup tempGroup = dio.findByServiceType("입고");
		if (tempGroup != null)
			daily_income_title.setText(tempGroup.getTitleUnit());

		statsView.makeReleaseStatsView(release_empty_layout);
		tempGroup = dio.findByServiceType("출고");
		if (tempGroup != null)
			daily_release_title.setText(tempGroup.getTitleUnit());

		statsView.makePetosaStatsView(petosa_empty_layout);
		tempGroup = dio.findByServiceType("토사");
		if (tempGroup != null)
			daily_petosa_title.setText(tempGroup.getTitleUnit());

		tempGroup = dio.findByServiceType("외부입고");
		if (tempGroup != null)
		{
			statsView.makeStockOutsideStatsView(income_empty_layout_outside);
			daily_income_title_outside.setText(tempGroup.getTitleUnit());
		}

		tempGroup = dio.findByServiceType("외부출고");
		if (tempGroup != null)
		{
			statsView.makeReleaseOutsideStatsView(release_empty_layout_outside);
			daily_release_title_outside.setText(tempGroup.getTitleUnit());
		}

	}

}
