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

public class FragmentYearAmount extends Fragment
{

	private ListView yi_year_amount_listview;
	private TextView yi_year_amount_date;
	private LinearLayout yi_year_amount_header_container, yi_year_amount_loading_indicator, yi_year_amount_loading_fail;

	public FragmentYearAmount() {}
	
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
		
		makeYearAmountData();

	}
	
	@Override
	public void onPause()
	{
		super.onPause();
	}
	
	private void makeYearAmountData()
	{

		String functionName = "makeYearAmountData()";

		try
		{

			String dateStr = GSConfig.CURRENT_YEAR + "???  ????????? ??????(??????:??????)";

			if (GSConfig.IsDebugging)
				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + GSConfig.CURRENT_YEAR + "???");

			yi_year_amount_date.setText(dateStr);

			String qryContent = "Unit";

			this.getData(qryContent);

		}
		catch(Exception ex)
		{
			Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
			return;
		}

	}

	private void getData(String qryContent)
	{

		String functionName = "getData()";

		if (GSConfig.IsDebugging)
			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "searchYear : " + GSConfig.CURRENT_YEAR + ", qryContent : " + qryContent);

		String url = GSConfig.API_SERVER_ADDR;
		RequestQueue requestQueue = Volley.newRequestQueue(GSConfig.context);

		StringRequest request = new StringRequest(
				Request.Method.POST,
				url,
				//????????? ??? ????????? ??? ??? ???????????? ???????????? ??????
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						if (GSConfig.IsDebugging)
							Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "?????? -> " + response);

						parseData(response);

					}
				},
				//?????? ????????? ????????? ????????? ??????
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "?????? -> " + error.getMessage());
					}
				}
		) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> params = new HashMap<String,String>();
				params.put("GSType", "YEAR");
				params.put("GSQuery", "{ \"branchID\" : " + GSConfig.CURRENT_BRANCH.branchID + ", \"searchYear\": " + GSConfig.CURRENT_YEAR + ", \"qryContent\" : \"" + qryContent + "\" }");
				return params;
			}
		};

		request.setRetryPolicy(new DefaultRetryPolicy(
				0,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		// ?????? ?????? ????????? ?????? ???????????? ????????? ????????????.
		request.setShouldCache(false);
		requestQueue.add(request);

		if (GSConfig.IsDebugging)
			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "?????? ??????.");

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

		try
		{

			yi_year_amount_header_container.removeAllViews();

			StatsHeaderAndFooterView statsHeaderAndFooterView = new StatsHeaderAndFooterView(getActivity(), data, GSConfig.STATE_AMOUNT);
			statsHeaderAndFooterView.makeHeaderView(yi_year_amount_header_container);

			StAdapter adapter = new StAdapter(getActivity(), data, GSConfig.STATE_AMOUNT);

			View foot = View.inflate(getActivity(), R.layout.stats_foot, null);
			LinearLayout footer_layout = (LinearLayout)foot.findViewById(R.id.stats_footer_container);

			statsHeaderAndFooterView.makeFooterView(footer_layout);
			yi_year_amount_listview.addFooterView(foot);
			yi_year_amount_listview.setAdapter(adapter);

		}
		catch(Exception ex)
		{
			Log.e(GSConfig.APP_DEBUG, "ERROR : " + this.getClass().getName() + " : setDisplay() : " + ex.toString());
			return;
		}

	}

}
