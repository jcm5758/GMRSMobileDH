package com.geurimsoft.grmsmobiledh.view.fragments;

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
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDailyInOut;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDailyInOutGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FragmentMonthCustomerPrice extends Fragment
{

	private LinearLayout yi_month_enterprise_amount_income_empty_layout, yi_month_enterprise_amount_release_empty_layout, yi_month_enterprise_amount_petosa_empty_layout;
	private LinearLayout yi_month_enterprise_amount_outside_income_empty_layout, yi_month_enterprise_amount_outside_release_empty_layout;
	private LinearLayout yi_month_enterprise_amount_loading_indicator, yi_month_enterprise_amount_loading_fail;

	private TextView yi_month_enterprise_amount_date, yi_month_enterprise_amount_income_title, yi_month_enterprise_amount_release_title, yi_month_enterprise_amount_petosa_title;
	private TextView yi_month_enterprise_amount_outside_income_title, yi_month_enterprise_amount_outside_release_title;

	public FragmentMonthCustomerPrice() { }

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
//		this.yi_month_enterprise_amount_outside_income_empty_layout = (LinearLayout)view.findViewById(R.id.month_enterprise_amount_outside_income_empty_layout);
//		this.yi_month_enterprise_amount_outside_release_empty_layout = (LinearLayout)view.findViewById(R.id.month_enterprise_amount_outside_release_empty_layout);
		this.yi_month_enterprise_amount_petosa_empty_layout = (LinearLayout)view.findViewById(R.id.month_enterprise_amount_petosa_empty_layout);

		this.yi_month_enterprise_amount_loading_indicator = (LinearLayout)view.findViewById(R.id.month_enterprise_amount_loading_indicator);
		this.yi_month_enterprise_amount_loading_fail = (LinearLayout)view.findViewById(R.id.month_enterprise_amount_loading_fail);

		this.yi_month_enterprise_amount_date = (TextView)view.findViewById(R.id.month_enterprise_amount_date);
		this.yi_month_enterprise_amount_income_title = (TextView)view.findViewById(R.id.month_enterprise_amount_income_title);
		this.yi_month_enterprise_amount_release_title = (TextView)view.findViewById(R.id.month_enterprise_amount_release_title);
//		this.yi_month_enterprise_amount_outside_income_title = (TextView)view.findViewById(R.id.month_enterprise_amount_outside_income_title);
//		this.yi_month_enterprise_amount_outside_release_title = (TextView)view.findViewById(R.id.month_enterprise_amount_outside_release_title);
		this.yi_month_enterprise_amount_petosa_title = (TextView)view.findViewById(R.id.month_enterprise_amount_petosa_title);

		makeMonthEnterprisepriceData();

	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	private void makeMonthEnterprisepriceData()
	{

		String functionName = "makeMonthAmountData()";

		try
		{

			String dateStr = GSConfig.CURRENT_YEAR + "??? " + GSConfig.CURRENT_MONTH + "???  ????????? ??????";
//			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + _year + "??? " + _monthOfYear + "???");

			yi_month_enterprise_amount_date.setText(dateStr);

			String qryContent = "TotalPrice";

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
				params.put("GSType", "MONTH_CUSTOMER");
				params.put("GSQuery", "{ \"branchID\" : " + GSConfig.CURRENT_BRANCH.branchID + ", \"searchYear\": " + GSConfig.CURRENT_YEAR + ", \"searchMonth\": " + GSConfig.CURRENT_MONTH + ", \"qryContent\" : \"" + qryContent + "\" }");
				return params;
			}
		};

		request.setRetryPolicy(new DefaultRetryPolicy(
				0,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		request.setShouldCache(false); //?????? ?????? ????????? ?????? ???????????? ????????? ????????????.
		requestQueue.add(request);

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "?????? ??????.");

	}

	public void parseData(String msg)
	{

		String functionName = "parseData()";
//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + msg);

		try
		{

			GSDailyInOut dio = new GSDailyInOut();

			Gson gson = new Gson();
			GSDailyInOutGroup[] diog = gson.fromJson(msg, GSDailyInOutGroup[].class);

			dio.list = new ArrayList<>(Arrays.asList(diog));

			this.setDisplayData(dio);

		}
		catch(Exception ex)
		{
			Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
			return;
		}

	}

	private void setDisplayData(GSDailyInOut data)
	{

		if (data == null)
			return;

		String functionName = "setDisplayData()";

		try
		{

			GSDailyInOutGroup inputGroup = data.findByServiceType(GSConfig.MODE_NAMES[GSConfig.MODE_STOCK]);
			GSDailyInOutGroup outputGroup = data.findByServiceType(GSConfig.MODE_NAMES[GSConfig.MODE_RELEASE]);
//			GSDailyInOutGroup inputOutsideGroup = data.findByServiceType("??????(??????)");
//			GSDailyInOutGroup outputOutsideGroup = data.findByServiceType("??????(??????)");
			GSDailyInOutGroup slugeGroup = data.findByServiceType(GSConfig.MODE_NAMES[GSConfig.MODE_PETOSA]);

//			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "inputGroup.totalUnit : " + inputGroup.totalUnit);
//			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "outputGroup.totalUnit : " + outputGroup.totalUnit);

			String unit = getString(R.string.unit_won);

			yi_month_enterprise_amount_income_empty_layout.removeAllViews();
			yi_month_enterprise_amount_release_empty_layout.removeAllViews();
//			yi_month_enterprise_amount_outside_income_empty_layout.removeAllViews();
//			yi_month_enterprise_amount_outside_release_empty_layout.removeAllViews();
			yi_month_enterprise_amount_petosa_empty_layout.removeAllViews();

			MonthCustomerStatsView statsView = new MonthCustomerStatsView(getActivity(), 3, GSConfig.STATE_PRICE, GSConfig.CURRENT_YEAR, GSConfig.CURRENT_MONTH);

			if (inputGroup != null)
			{
				statsView.makeStatsView(yi_month_enterprise_amount_income_empty_layout, inputGroup, GSConfig.MODE_STOCK, GSConfig.STATE_PRICE);
				yi_month_enterprise_amount_income_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_STOCK] + "(" + GSConfig.changeToCommanString(inputGroup.totalUnit) + unit + ")");
			}

			if (outputGroup != null)
			{
				statsView.makeStatsView(yi_month_enterprise_amount_release_empty_layout, outputGroup, GSConfig.MODE_RELEASE, GSConfig.STATE_PRICE);
				yi_month_enterprise_amount_release_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_RELEASE] + "(" + GSConfig.changeToCommanString(outputGroup.totalUnit) + unit + ")");
			}


//			statsView.makeStatsView(yi_month_enterprise_amount_outside_income_empty_layout, inputOutsideGroup, GSConfig.MODE_STOCK, GSConfig.STATE_PRICE);
//			yi_month_enterprise_amount_outside_income_title.setText( "????????????(" + GSConfig.changeToCommanString(inputOutsideGroup.totalUnit) + unit + ")");
//
//			statsView.makeStatsView(yi_month_enterprise_amount_outside_release_empty_layout, outputOutsideGroup, GSConfig.MODE_RELEASE, GSConfig.STATE_PRICE);
//			yi_month_enterprise_amount_outside_release_title.setText("????????????(" + GSConfig.changeToCommanString(outputOutsideGroup.totalUnit) + unit + ")");

			if (slugeGroup != null)
			{
				statsView.makeStatsView(yi_month_enterprise_amount_petosa_empty_layout, slugeGroup, GSConfig.MODE_PETOSA, GSConfig.STATE_PRICE);
				yi_month_enterprise_amount_petosa_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_PETOSA] + "(" + GSConfig.changeToCommanString(slugeGroup.totalUnit) + unit + ")");
			}


		}
		catch(Exception ex)
		{
			Log.e(GSConfig.APP_DEBUG, "ERROR : " + this.getClass().getName() + " : setDisplay() : " + ex.toString());
			return;
		}

	}


}
