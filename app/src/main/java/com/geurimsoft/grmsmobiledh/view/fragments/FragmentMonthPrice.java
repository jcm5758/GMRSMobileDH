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

public class FragmentMonthPrice extends Fragment
{

	private ListView yi_month_price_listview;
	private TextView yi_month_price_date;
	private String dateStr;
	private LinearLayout yi_month_price_header_container, yi_month_price_loading_indicator, yi_month_price_loading_fail;

	public FragmentMonthPrice() {}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.yi_month_price, container,false);
		return v;
	}
	
	@Override
	public void onResume()
	{

		super.onResume();

		View view = this.getView();
	
		this.yi_month_price_listview = (ListView)view.findViewById(R.id.yi_month_price_listview);
		this.yi_month_price_date = (TextView)view.findViewById(R.id.yi_month_price_date);
				
		this.yi_month_price_header_container = (LinearLayout)view.findViewById(R.id.yi_month_price_header_container);
		
		this.yi_month_price_loading_indicator = (LinearLayout)view.findViewById(R.id.yi_month_price_loading_indicator);
		this.yi_month_price_loading_fail = (LinearLayout)view.findViewById(R.id.yi_month_price_loading_fail);
		
		this.yi_month_price_listview.setDividerHeight(0);
		
		makeMonthpriceData();

	}
	
	@Override
	public void onPause()
	{
		super.onPause();
	}

	/**
	 * 데이터 질의문 생성 및 요청
	 */
	private void makeMonthpriceData()
	{

		String functionName = "makeMonthpriceData()";

		try
		{

			dateStr = GSConfig.CURRENT_YEAR + "년 " + GSConfig.CURRENT_MONTH + "월  입출고 현황(단위:천원)";

			if (GSConfig.IsDebugging)
				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + GSConfig.CURRENT_YEAR + "년 " + GSConfig.CURRENT_MONTH + "월");

			yi_month_price_date.setText(dateStr);

			String qryContent = "TotalPrice";

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

		request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
		requestQueue.add(request);

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "요청 보냄.");

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

	/**
	 * 조회 결과 데이터의 뷰 설정
	 * @param data
	 */
	private void setDisplayData(GSMonthInOut data)
	{
		
		yi_month_price_header_container.removeAllViews();

		StatsHeaderAndFooterView statsHeaderAndFooterView = new StatsHeaderAndFooterView(getActivity(), data, GSConfig.STATE_PRICE);
		statsHeaderAndFooterView.makeHeaderView(yi_month_price_header_container);

		StAdapter adapter = new StAdapter(getActivity(), data, GSConfig.STATE_PRICE);

		View foot = View.inflate(getActivity(), R.layout.stats_foot, null);
		LinearLayout footer_layout = (LinearLayout)foot.findViewById(R.id.stats_footer_container);

		statsHeaderAndFooterView.makeFooterView(footer_layout);
		yi_month_price_listview.addFooterView(foot);
		yi_month_price_listview.setAdapter(adapter);

	}

}
