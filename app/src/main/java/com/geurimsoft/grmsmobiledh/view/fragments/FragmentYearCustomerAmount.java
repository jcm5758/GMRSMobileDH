package com.geurimsoft.grmsmobiledh.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDailyInOutGroupNew;
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class FragmentYearCustomerAmount extends Fragment
{

	private LinearLayout yi_month_enterprise_amount_income_empty_layout, yi_month_enterprise_amount_release_empty_layout, yi_month_enterprise_amount_petosa_empty_layout;
	private LinearLayout yi_month_enterprise_amount_income_outside_empty_layout, yi_month_enterprise_amount_release_outside_empty_layout;
	private LinearLayout yi_month_enterprise_amount_loading_indicator, yi_month_enterprise_amount_loading_fail;

	private TextView yi_month_enterprise_amount_date, yi_month_enterprise_amount_income_title, yi_month_enterprise_amount_release_title, yi_month_enterprise_amount_petosa_title;
	private TextView yi_month_enterprise_amount_income_outside_title, yi_month_enterprise_amount_release_outside_title;

	EnterpriseYearStatsView statsView;
	String unit;

	public FragmentYearCustomerAmount() {}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.yi_month_enterprise_amount, container, false);
		return v;
	}

	@Override
	public void onResume()
	{

		super.onResume();

		View view = this.getView();

		this.yi_month_enterprise_amount_income_empty_layout = (LinearLayout)view.findViewById(R.id.yi_month_enterprise_amount_income_empty_layout);
		this.yi_month_enterprise_amount_release_empty_layout = (LinearLayout)view.findViewById(R.id.yi_month_enterprise_amount_release_empty_layout);
		this.yi_month_enterprise_amount_petosa_empty_layout = (LinearLayout)view.findViewById(R.id.yi_month_enterprise_amount_petosa_empty_layout);

		this.yi_month_enterprise_amount_loading_indicator = (LinearLayout)view.findViewById(R.id.yi_month_enterprise_amount_loading_indicator);
		this.yi_month_enterprise_amount_loading_fail = (LinearLayout)view.findViewById(R.id.yi_month_enterprise_amount_loading_fail);

		this.yi_month_enterprise_amount_date = (TextView)view.findViewById(R.id.yi_month_enterprise_amount_date);
		this.yi_month_enterprise_amount_income_title = (TextView)view.findViewById(R.id.yi_month_enterprise_amount_income_title);
		this.yi_month_enterprise_amount_release_title = (TextView)view.findViewById(R.id.yi_month_enterprise_amount_release_title);
		this.yi_month_enterprise_amount_petosa_title = (TextView)view.findViewById(R.id.yi_month_enterprise_amount_petosa_title);

		makeMonthEnterpriseAmountData();

	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	private void makeMonthEnterpriseAmountData()
	{

		String functionName = "makeYearAmountData()";

		try
		{

			String dateStr = GSConfig.CURRENT_YEAR + "년  입출고 현황";

			if (GSConfig.IsDebugging)
				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + GSConfig.CURRENT_YEAR + "년");

			yi_month_enterprise_amount_date.setText(dateStr);

			yi_month_enterprise_amount_income_empty_layout.removeAllViews();
			yi_month_enterprise_amount_release_empty_layout.removeAllViews();
			yi_month_enterprise_amount_petosa_empty_layout.removeAllViews();

			this.statsView = new EnterpriseYearStatsView(getActivity(), GSConfig.CURRENT_BRANCH.branchID, GSConfig.STATE_AMOUNT, GSConfig.CURRENT_YEAR);

			this.unit = getString(R.string.unit_lube);

			this.getData("Unit", GSConfig.MODE_STOCK);
			this.getData("Unit", GSConfig.MODE_RELEASE);
			this.getData("Unit", GSConfig.MODE_PETOSA);

		}
		catch(Exception ex)
		{
			Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
			return;
		}

	}

	private void getData(String qryContent, int serviceType)
	{

		String functionName = "getData()";

		if (GSConfig.IsDebugging)
			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "searchYear : " + GSConfig.CURRENT_YEAR + ", qryContent : " + qryContent);

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

						Gson gson = new Gson();
						GSDailyInOutGroupNew dataGroup = null;

						if (serviceType == GSConfig.MODE_STOCK)
						{

							dataGroup = gson.fromJson(response, GSDailyInOutGroupNew.class);

							if (dataGroup != null)
							{
								statsView.makeStatsView(yi_month_enterprise_amount_income_empty_layout, dataGroup, GSConfig.MODE_STOCK, GSConfig.STATE_AMOUNT);
								yi_month_enterprise_amount_income_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_STOCK] + "(" + GSConfig.changeToCommanString(dataGroup.totalUnit) + unit + ")");
							}

						}
						else if (serviceType == GSConfig.MODE_RELEASE)
						{

							dataGroup = gson.fromJson(response, GSDailyInOutGroupNew.class);

							if (dataGroup != null)
							{
								statsView.makeStatsView(yi_month_enterprise_amount_release_empty_layout, dataGroup, GSConfig.MODE_RELEASE, GSConfig.STATE_AMOUNT);
								yi_month_enterprise_amount_release_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_RELEASE] + "(" + GSConfig.changeToCommanString(dataGroup.totalUnit) + unit + ")");
							}

						}
						else if (serviceType == GSConfig.MODE_PETOSA)
						{

							dataGroup = gson.fromJson(response, GSDailyInOutGroupNew.class);

							if (dataGroup != null)
							{
								statsView.makeStatsView(yi_month_enterprise_amount_petosa_empty_layout, dataGroup, GSConfig.MODE_PETOSA, GSConfig.STATE_AMOUNT);
								yi_month_enterprise_amount_petosa_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_PETOSA] + "(" + GSConfig.changeToCommanString(dataGroup.totalUnit) + unit + ")");
							}

						}
						else if (serviceType == GSConfig.MODE_OUTSIDE_STOCK)
						{

							dataGroup = gson.fromJson(response, GSDailyInOutGroupNew.class);

							if (dataGroup != null)
							{
								statsView.makeStatsView(yi_month_enterprise_amount_income_outside_empty_layout, dataGroup, GSConfig.MODE_OUTSIDE_STOCK, GSConfig.STATE_AMOUNT);
								yi_month_enterprise_amount_income_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_OUTSIDE_STOCK] + "(" + GSConfig.changeToCommanString(dataGroup.totalUnit) + unit + ")");
							}

						}
						else if (serviceType == GSConfig.MODE_OUTSIDE_RELEASE)
						{

							dataGroup = gson.fromJson(response, GSDailyInOutGroupNew.class);

							if (dataGroup != null)
							{
								statsView.makeStatsView(yi_month_enterprise_amount_release_outside_empty_layout, dataGroup, GSConfig.MODE_OUTSIDE_RELEASE, GSConfig.STATE_AMOUNT);
								yi_month_enterprise_amount_release_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_OUTSIDE_RELEASE] + "(" + GSConfig.changeToCommanString(dataGroup.totalUnit) + unit + ")");
							}

						}

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

				params.put("GSType", "YEAR_CUSTOMER");
				params.put("GSQuery", "{ \"branchID\" : " + GSConfig.CURRENT_BRANCH.branchID + ", \"searchYear\": " + GSConfig.CURRENT_YEAR + ", \"qryContent\" : \"" + qryContent + "\",  \"serviceType\" : " + serviceType + " }");

				if (GSConfig.IsDebugging)
					Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + params.get("GSQuery"));

				return params;

			}
		};

		request.setRetryPolicy(new DefaultRetryPolicy(
				0,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		request.setRetryPolicy(new DefaultRetryPolicy(
				0,
				-1,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
		));

		// 이전 결과 있어도 새로 요청하여 응답을 보여준다.
		request.setShouldCache(false);
		requestQueue.add(request);

		if (GSConfig.IsDebugging)
			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "요청 보냄.");

	}

}
