/**
 * 월별 수량
 *
 * 2021. 05. 30. 리뉴얼
 *
 * Written by jcm5758
 *
 */
package com.geurimsoft.grmsmobiledh.view.fragments;

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
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.geurimsoft.grmsmobiledh.data.GSMonthInOut;
import com.geurimsoft.grmsmobiledh.data.StAdapter;
import com.geurimsoft.grmsmobiledh.view.util.StatsHeaderAndFooterView;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class FragmentMonthAmount extends Fragment
{

	private ListView yi_month_amount_listview;
	private TextView yi_month_amount_date;
	private String dateStr;
	private LinearLayout yi_month_amount_header_container, yi_month_amount_loading_indicator, yi_month_amount_loading_fail;

	public FragmentMonthAmount() {}
	
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
		
		makeMonthAmountData();

	}
	
	@Override
	public void onPause()
	{
		super.onPause();
	}

	/**
	 * 월 수량 조회
	 */
	private void makeMonthAmountData()
	{

		String functionName = "makeMonthAmountData()";

		try
		{

			dateStr = GSConfig.CURRENT_YEAR + "년 " + GSConfig.CURRENT_MONTH  + "월  입출고 현황(단위:루베)";

			if (GSConfig.IsDebugging)
				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + GSConfig.CURRENT_YEAR + "년 " + GSConfig.CURRENT_MONTH  + "월");

			yi_month_amount_date.setText(dateStr);

			String qryContent = "Unit";

			this.getData(qryContent);

		}
		catch(Exception ex)
		{
			Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
			return;
		}

	}

	/**
	 * 서버에 데이터 요청
	 * @param qryContent 수량 or 금액
	 */
	private void getData(String qryContent)
	{

		String functionName = "getData()";

		if (GSConfig.IsDebugging)
			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "qryContent : " + qryContent);

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
				params.put("GSType", "MONTH");
				params.put("GSQuery", "{ \"branchID\" : " + GSConfig.CURRENT_BRANCH.branchID + ", \"searchYear\": " + GSConfig.CURRENT_YEAR + ", \"searchMonth\": " + GSConfig.CURRENT_MONTH + ", \"qryContent\" : \"" + qryContent + "\" }");
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

		try
		{

			Gson gson = new Gson();
			GSMonthInOut data = gson.fromJson(msg, GSMonthInOut.class);

			this.setDisplayData(data);

		}
		catch(Exception ex)
		{
			Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
			return;
		}

	}
	
	private void setDisplayData(GSMonthInOut data)
	{
		
		yi_month_amount_header_container.removeAllViews();
		
		StatsHeaderAndFooterView statsHeaderAndFooterView = new StatsHeaderAndFooterView(getActivity(), data, GSConfig.STATE_AMOUNT);
		statsHeaderAndFooterView.makeHeaderView(yi_month_amount_header_container);
		
		StAdapter adapter = new StAdapter(getActivity(), data, GSConfig.STATE_AMOUNT);

		View foot = View.inflate(getActivity(), R.layout.stats_foot, null);
		LinearLayout footer_layout = (LinearLayout)foot.findViewById(R.id.stats_footer_container);
		statsHeaderAndFooterView.makeFooterView(footer_layout);

		yi_month_amount_listview.addFooterView(foot);
		yi_month_amount_listview.setAdapter(adapter);

	}

}
