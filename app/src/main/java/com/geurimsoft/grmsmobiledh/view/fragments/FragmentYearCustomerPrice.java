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

public class FragmentYearCustomerPrice extends Fragment
{

	private LinearLayout yi_month_enterprise_amount_income_empty_layout, yi_month_enterprise_amount_release_empty_layout, yi_month_enterprise_amount_petosa_empty_layout;
	private LinearLayout yi_month_enterprise_amount_income_outside_empty_layout, yi_month_enterprise_amount_release_outside_empty_layout;
	private LinearLayout yi_month_enterprise_amount_loading_indicator, yi_month_enterprise_amount_loading_fail;

	private TextView yi_month_enterprise_amount_date, yi_month_enterprise_amount_income_title, yi_month_enterprise_amount_release_title, yi_month_enterprise_amount_petosa_title;
	private TextView yi_month_enterprise_amount_income_outside_title, yi_month_enterprise_amount_release_outside_title;

	EnterpriseYearStatsView statsView;
	String unit;

	public FragmentYearCustomerPrice() {}

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

			String dateStr = GSConfig.CURRENT_YEAR + "???  ????????? ??????";

			yi_month_enterprise_amount_date.setText(dateStr);

			String unit = getString(R.string.unit_won);

			yi_month_enterprise_amount_income_empty_layout.removeAllViews();
			yi_month_enterprise_amount_release_empty_layout.removeAllViews();
			yi_month_enterprise_amount_petosa_empty_layout.removeAllViews();

			this.statsView = new EnterpriseYearStatsView(getActivity(), GSConfig.CURRENT_BRANCH.branchID, GSConfig.STATE_PRICE, GSConfig.CURRENT_YEAR);

			this.unit = getString(R.string.unit_won);

			this.getData("TotalPrice", GSConfig.MODE_STOCK);
			this.getData("TotalPrice", GSConfig.MODE_RELEASE);
			this.getData("TotalPrice", GSConfig.MODE_PETOSA);

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

						Gson gson = new Gson();
						GSDailyInOutGroupNew dataGroup = null;

						if (serviceType == GSConfig.MODE_STOCK)
						{

							dataGroup = gson.fromJson(response, GSDailyInOutGroupNew.class);

							if (dataGroup != null)
							{
								statsView.makeStatsView(yi_month_enterprise_amount_income_empty_layout, dataGroup, GSConfig.MODE_STOCK, GSConfig.STATE_PRICE);
								yi_month_enterprise_amount_income_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_STOCK] + "(" + GSConfig.changeToCommanString(dataGroup.totalUnit) + unit + ")");
							}

						}
						else if (serviceType == GSConfig.MODE_RELEASE)
						{

							dataGroup = gson.fromJson(response, GSDailyInOutGroupNew.class);

							if (dataGroup != null)
							{
								statsView.makeStatsView(yi_month_enterprise_amount_release_empty_layout, dataGroup, GSConfig.MODE_RELEASE, GSConfig.STATE_PRICE);
								yi_month_enterprise_amount_release_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_RELEASE] + "(" + GSConfig.changeToCommanString(dataGroup.totalUnit) + unit + ")");
							}

						}
						else if (serviceType == GSConfig.MODE_PETOSA)
						{

							dataGroup = gson.fromJson(response, GSDailyInOutGroupNew.class);

							if (dataGroup != null)
							{
								statsView.makeStatsView(yi_month_enterprise_amount_petosa_empty_layout, dataGroup, GSConfig.MODE_PETOSA, GSConfig.STATE_PRICE);
								yi_month_enterprise_amount_petosa_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_PETOSA] + "(" + GSConfig.changeToCommanString(dataGroup.totalUnit) + unit + ")");
							}

						}
						else if (serviceType == GSConfig.MODE_OUTSIDE_STOCK)
						{

							dataGroup = gson.fromJson(response, GSDailyInOutGroupNew.class);

							if (dataGroup != null)
							{
								statsView.makeStatsView(yi_month_enterprise_amount_income_outside_empty_layout, dataGroup, GSConfig.MODE_OUTSIDE_STOCK, GSConfig.STATE_PRICE);
								yi_month_enterprise_amount_income_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_OUTSIDE_STOCK] + "(" + GSConfig.changeToCommanString(dataGroup.totalUnit) + unit + ")");
							}

						}
						else if (serviceType == GSConfig.MODE_OUTSIDE_RELEASE)
						{

							dataGroup = gson.fromJson(response, GSDailyInOutGroupNew.class);

							if (dataGroup != null)
							{
								statsView.makeStatsView(yi_month_enterprise_amount_release_outside_empty_layout, dataGroup, GSConfig.MODE_OUTSIDE_RELEASE, GSConfig.STATE_PRICE);
								yi_month_enterprise_amount_release_title.setText(GSConfig.MODE_NAMES[GSConfig.MODE_OUTSIDE_RELEASE] + "(" + GSConfig.changeToCommanString(dataGroup.totalUnit) + unit + ")");
							}

						}

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
				params.put("GSType", "YEAR_CUSTOMER");
				params.put("GSQuery", "{ \"branchID\" : " + GSConfig.CURRENT_BRANCH.branchID + ", \"searchYear\": " + GSConfig.CURRENT_YEAR + ", \"qryContent\" : \"" + qryContent + "\",  \"serviceType\" : " + serviceType + " }");
				return params;
			}
		};

		request.setRetryPolicy(new DefaultRetryPolicy(
				0,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		request.setRetryPolicy(new DefaultRetryPolicy(20000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		// ?????? ?????? ????????? ?????? ???????????? ????????? ????????????.
		request.setShouldCache(false);
		requestQueue.add(request);

		if (GSConfig.IsDebugging)
			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "?????? ??????.");

	}

}
