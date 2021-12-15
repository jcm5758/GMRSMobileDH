/**
 * 일보 금액
 *
 * 2021. 05. 28. 리뉴얼
 *
 *  Written by jcm5758
 *
 */

package com.geurimsoft.grmsmobiledh.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.geurimsoft.grmsmobiledh.util.GSUtil;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FragmentDailyPrice extends Fragment
{

	private LinearLayout income_empty_layout, release_empty_layout, petosa_empty_layout;
	private TextView stats_daily_date, daily_income_title, daily_release_title, daily_petosa_title;

	private DecimalFormat df = new DecimalFormat("#,###");

	private LinearLayout loading_indicator, loading_fail;

	public FragmentDailyPrice() {}

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

		View view = this.getView();

		this.income_empty_layout = (LinearLayout)view.findViewById(R.id.income_empty_layout);
		this.release_empty_layout = (LinearLayout)view.findViewById(R.id.release_empty_layout);
		this.petosa_empty_layout = (LinearLayout)view.findViewById(R.id.petosa_empty_layout);

		this.loading_indicator = (LinearLayout)view.findViewById(R.id.loading_indicator);
		this.loading_fail = (LinearLayout)view.findViewById(R.id.loading_fail);

		this.stats_daily_date = (TextView)view.findViewById(R.id.stats_daily_date);

		this.daily_income_title = (TextView) view.findViewById(R.id.daily_income_title);
		this.daily_release_title = (TextView) view.findViewById(R.id.daily_release_title);
		this.daily_petosa_title = (TextView) view.findViewById(R.id.daily_petosa_title);

		makeDailyPriceData();

	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	/**
	 * 일보 금액 조회
	 */
	public void makeDailyPriceData()
	{

		String functionName = "makeDailyAmountData()";

		try
		{

			String str = GSConfig.CURRENT_YEAR + "년 " + GSConfig.CURRENT_MONTH + "월 " + GSConfig.CURRENT_DAY + "일 입출고 현황";

			if (GSConfig.IsDebugging)
				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + GSConfig.CURRENT_YEAR + "년 " + GSConfig.CURRENT_MONTH + "월 " + GSConfig.CURRENT_DAY + "일");

			this.stats_daily_date.setText(str);

			String queryDate = GSUtil.makeStringFromDate(GSConfig.CURRENT_YEAR, GSConfig.CURRENT_MONTH, GSConfig.CURRENT_DAY);
			String qryContent = "TotalPrice";

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

						if (GSConfig.IsDebugging)
							Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "응답 -> " + response);

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
				params.put("GSQuery", "{ \"branchID\" : " + GSConfig.CURRENT_BRANCH.branchID + ", \"searchDate\": " + searchDate + ", \"qryContent\" : \"" + qryContent + "\" }");
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

		GSDailyInOut dio = new GSDailyInOut();

		Gson gson = new Gson();
		GSDailyInOutGroup[] diog = gson.fromJson(msg, GSDailyInOutGroup[].class);

		dio.list = new ArrayList<>(Arrays.asList(diog));

		this.setDisplayData(dio);

	}

	/**
	 * 검색 결과로 채우기
	 *
	 * @param dio 입출고 내역
	 */
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

		// 레이아웃 초기화
		income_empty_layout.removeAllViews();
		release_empty_layout.removeAllViews();
		petosa_empty_layout.removeAllViews();

		// 제목 숨기기
		daily_income_title.setVisibility(View.INVISIBLE);
		daily_release_title.setVisibility(View.INVISIBLE);
		daily_petosa_title.setVisibility(View.INVISIBLE);

		// 데이터 파싱 결과
		GSDailyInOutGroup inputGroup = dio.findByServiceType("입고");
		GSDailyInOutGroup outputGroup = dio.findByServiceType("출고");
		GSDailyInOutGroup slugeGroup = dio.findByServiceType("토사");

		// 1개라도 데이터가 있는지 여부 파악
		int iCount = 0;

		// 통계 표를 위한 인터페이스
		StatsView statsView = new StatsView(getActivity(), dio, 0);

		// 입고 데이터가 있으면
		if (inputGroup != null && inputGroup.headerCount > 0 && inputGroup.recordCount > 0)
		{
			statsView.makeStockStatsView(income_empty_layout);
			daily_income_title.setVisibility(View.VISIBLE);
			daily_income_title.setText(inputGroup.getTitleUnit());
			iCount++;
		}

		// 출고 데이터가 있으면
		if (outputGroup != null && outputGroup.headerCount > 0 && outputGroup.recordCount > 0)
		{
			statsView.makeReleaseStatsView(release_empty_layout);
			daily_release_title.setVisibility(View.VISIBLE);
			daily_release_title.setText(outputGroup.getTitleUnit());
			iCount++;
		}

		// 토사 데이터가 있으면
		if (slugeGroup != null && slugeGroup.headerCount > 0 && slugeGroup.recordCount > 0)
		{
			statsView.makePetosaStatsView(petosa_empty_layout);
			daily_petosa_title.setVisibility(View.VISIBLE);
			daily_petosa_title.setText(slugeGroup.getTitleUnit());
			iCount++;
		}

		if (iCount == 0)
		{
			daily_income_title.setVisibility(View.VISIBLE);
			daily_income_title.setText("서비스 내역이 없습니다.");
		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.stats_menu, menu);
	}

}
