package com.geurimsoft.grmsmobiledh.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.geurimsoft.grmsmobiledh.data.GSDataStatisticMonthChart;
import com.geurimsoft.grmsmobiledh.data.GSMonthInOut;
import com.google.gson.Gson;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.HashMap;
import java.util.Map;

public class FragmentMonthGraph extends Fragment
{

	private LinearLayout stats_month_graph_container, stats_month_graph_loading_indicator, stats_month_graph_loading_fail;

	public FragmentMonthGraph() { }
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.month_graph, container, false);
		return v;
	}
	
	@Override
	public void onResume()
	{

		super.onResume();
		
		View view = this.getView();
		
		this.stats_month_graph_container = (LinearLayout)view.findViewById(R.id.container_layout);
		this.stats_month_graph_loading_indicator = (LinearLayout)view.findViewById(R.id.chart_loading_indicator);
		this.stats_month_graph_loading_fail = (LinearLayout)view.findViewById(R.id.chart_loading_fail);

		makeMonthGraphData();

	}
	
	@Override
	public void onPause()
	{
		super.onPause();
	}
	
	private void makeMonthGraphData()
	{

		String functionName = "makeMonthGraphData()";

		try
		{

			String dateStr = GSConfig.CURRENT_YEAR + "??? " + GSConfig.CURRENT_MONTH + "???  ????????? ??????";
//			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + _year + "??? " + _monthOfYear + "???");

			String qryContent = "Unit";

			this.getData(qryContent, dateStr);

		}
		catch(Exception ex)
		{
			Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
			return;
		}
		
	}

	private void getData(String qryContent, String dateStr)
	{

		String functionName = "getData()";

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "searchDate : " + searchDate + ", qryContent : " + qryContent);

		String url = GSConfig.API_SERVER_ADDR;
		RequestQueue requestQueue = Volley.newRequestQueue(GSConfig.context);

		StringRequest request = new StringRequest(
				Request.Method.POST,
				url,
				//????????? ??? ????????? ??? ??? ???????????? ???????????? ??????
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
//						Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "?????? -> " + response);
						parseData(response, dateStr);
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
				params.put("GSType", "MONTH");
				params.put("GSQuery", "{ \"branchID\" : " + GSConfig.CURRENT_BRANCH.branchID + ", \"searchYear\": " + GSConfig.CURRENT_YEAR + ", \"searchMonth\": " + GSConfig.CURRENT_MONTH + ", \"qryContent\" : \"" + qryContent + "\" }");
				return params;
			}
		};

		request.setShouldCache(false); //?????? ?????? ????????? ?????? ???????????? ????????? ????????????.
		requestQueue.add(request);

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "?????? ??????.");

	}

	public void parseData(String msg, String dateStr)
	{

		String functionName = "parseData()";
//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + msg);

		try
		{

			Gson gson = new Gson();

			GSMonthInOut data = gson.fromJson(msg, GSMonthInOut.class);

			this.setDisplayData(data, dateStr);

		}
		catch(Exception ex)
		{
			Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
			return;
		}

	}
	
	private void setDisplayData(GSMonthInOut data, String _date)
	{

		try
		{

			stats_month_graph_container.removeAllViews();
			GSDataStatisticMonthChart sy = new GSDataStatisticMonthChart(getActivity(), _date, data);
			final XYMultipleSeriesRenderer multiRenderer = sy.getRenderer();
			final XYMultipleSeriesDataset dataset = sy.getDataSet();

			View mChartLine = ChartFactory.getTimeChartView(getActivity(), dataset, multiRenderer, "dd-mmm-yyyy");
			stats_month_graph_container.addView(mChartLine);

		}
		catch(Exception ex)
		{
			Log.e(GSConfig.APP_DEBUG, "ERROR : " + this.getClass().getName() + " : setDisplay() : " + ex.toString());
			return;
		}

	}

}
