package com.geurimsoft.grmsmobiledh.view.fragments;;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDailyInOutDetailNew;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDailyInOutGroupNew;
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.geurimsoft.grmsmobiledh.data.StAdapter;
import com.geurimsoft.grmsmobiledh.data.GSMonthInOut;
import com.geurimsoft.grmsmobiledh.view.util.CustomProgressDialog;
import com.geurimsoft.grmsmobiledh.view.util.StatsHeaderAndFooterView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EnterpriseYearStatsView
{

	private Activity mActivity;
	private int branchID;
	private int statsType;
	private int iYear;

	private Context mContext;

	private LinearLayout stock_layout, release_layout, petosa_layout;


	/**
	 * 월별-거래처별-일별 통계
	 * @param _activity		Acitivity
	 * @param branchID		지점 ID
	 * @param statsType	Unit / Money
	 */
	public EnterpriseYearStatsView(Activity _activity, int branchID, int statsType, int iYear)
	{

		this.mContext = _activity;
		this.mActivity = _activity;
		this.branchID = branchID;
		this.statsType = statsType;
		this.iYear = iYear;

	}

	/**
	 * 뷰 생성
	 * @param _layout
	 * @param group
	 * @param serviceType
	 */
	public void makeStatsView(LinearLayout _layout, GSDailyInOutGroupNew group, final int serviceType, final int statsType)
	{

		String functionName = "makeStatsView()";

		try
		{

			if (group == null)
			{
				Log.e(GSConfig.APP_DEBUG, "ERROR : " + this.getClass().getName() + " : makeStatsView() : group is null.");
				return;
			}

			int header_count = group.headerCount;
			String[] header_titles = group.header;
			int recordCount = group.recordCount;
			ArrayList<GSDailyInOutDetailNew> detailList = group.list;

			if (header_titles == null)
			{
				Log.e(GSConfig.APP_DEBUG, "ERROR : " + this.getClass().getName() + " : makeStatsView() : header_titles is null.");
				return;
			}

			if(detailList == null || detailList.size() == 0)
			{
				Log.d(GSConfig.APP_DEBUG, "DEBUGGING : " + this.getClass().getName() + " : makeStatsView() : detailList is null.");
				return;
			}

			// 레이아웃 파라미터 지정
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

			LinearLayout header_layout = new LinearLayout(mContext);
			header_layout.setLayoutParams(params);
			header_layout.setOrientation(LinearLayout.HORIZONTAL);

			// 헤더 설정
			for(int header_index = 0; header_index < header_count; header_index++)
			{
				TextView title_textview = makeMenuTextView(mContext, header_titles[header_index], "#ffffff", Gravity.CENTER);
				header_layout.addView(title_textview);
			}

//			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + header_count);

			_layout.addView(header_layout);

			TextView stock_item_textview;

			for(int stock_index = 0; stock_index < detailList.size(); stock_index++)
			{

//				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + stock_index);

				GSDailyInOutDetailNew detail = detailList.get(stock_index);
//				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + detail.customer);

				String[] stock_items = detail.getStringValues();
//				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "001");
				LinearLayout stock_row_layout = new LinearLayout(mContext);

				stock_row_layout.setLayoutParams(params);
				stock_row_layout.setOrientation(LinearLayout.HORIZONTAL);
//				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "002");
				for(int i = 0; i < stock_items.length; i++)
				{

					int gravity = 0;
//					Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "003");

					if(i == 0)
					{

//						Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + stock_items[i]);

						gravity = Gravity.CENTER;

						if(stock_index == recordCount - 1)
							stock_item_textview = makeMenuTextView(mContext, stock_items[i], "#000000", gravity);
						else
						{

							stock_item_textview = makeRowTextView(mContext, stock_items[i], gravity);
							stock_item_textview.setTag(stock_items[i]);

							stock_item_textview.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									String name = (String) v.getTag();
									new YearDetailList(mContext, name, branchID, statsType, serviceType, iYear);
								}
							});

						}

						stock_row_layout.addView(stock_item_textview);

					}
					else
					{

						gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

						if(stock_index == recordCount - 1)
							stock_item_textview = makeMenuTextView(mContext, stock_items[i], "#000000", gravity);
						else
							stock_item_textview = makeRowTextView(mContext, stock_items[i], gravity);

						stock_row_layout.addView(stock_item_textview);

					}

				}

//				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "004");
//				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + stock_index);

				_layout.addView(stock_row_layout);

			}

			// 레이아웃 지정
			if (serviceType == GSConfig.MODE_STOCK)
				this.stock_layout = _layout;
			else if (serviceType == GSConfig.MODE_RELEASE)
				this.release_layout = _layout;
			else if (serviceType == GSConfig.MODE_PETOSA)
				this.petosa_layout = _layout;

		}
		catch(Exception ex)
		{
			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
			return;
		}

	}

	private TextView makeMenuTextView(Context context, String str, String color, int gravity)
	{

		LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
		layout_params.weight = 1.0f;

		TextView tv = new TextView(context);
		tv.setLayoutParams(layout_params);
		tv.setGravity(gravity);
		tv.setBackgroundResource(R.drawable.menu_border);
		tv.setPadding(10, 20, 10, 20);
		tv.setTextColor(Color.parseColor(color));
		tv.setTextSize(13);
		tv.setText(str);

		return tv;

	}

	private TextView makeRowTextView(Context context, String str, int gravity)
	{

		LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
		layout_params.weight = 1.0f;

		TextView tv = new TextView(context);
		tv.setLayoutParams(layout_params);
		tv.setGravity(gravity);
		tv.setBackgroundResource(R.drawable.row_border);
		tv.setPadding(10, 20, 10, 20);
		tv.setTextColor(Color.parseColor("#000000"));
		tv.setTextSize(13);
		tv.setText(str);

		return tv;

	}

	public class YearDetailList
	{

		private String queryDate;
		private String responseMessage;

		// 거래처 full name
		private String customerName;

		// 지점 ID
		private int branchID;

		// Unit / Money
		private int statsType;

		// 입고 / 출고 / 토사
		private int serviceType;

		private int iYear;

		private String qryContent;
		private CustomProgressDialog progressDialog;
		private Context mContext;

		public YearDetailList(Context mContext, String customerName, int branchID, int statsType, int serviceType, int iYear)
		{

			this.mContext = mContext;
			this.customerName = customerName;
			this.branchID = branchID;
			this.statsType = statsType;
			this.serviceType = serviceType;
			this.iYear = iYear;

			if (statsType == GSConfig.STATE_PRICE)
				this.qryContent = "TotalPrice";
			else
				this.qryContent = "Unit";

			progressDialog = new CustomProgressDialog(mContext);
			progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			progressDialog.show();

			this.getData();

		}

		private void getData()
		{

			String functionName = "getData()";

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "searchDate : " + searchDate + ", qryContent : " + qryContent);

			String url = GSConfig.API_SERVER_ADDR;
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
					params.put("GSType", "YEAR_CUSTOMER_MONTH");
					params.put("GSQuery", "{ \"branchID\" : " + GSConfig.CURRENT_BRANCH.branchID + ", \"customerFullName\": \"" + customerName + "\", \"serviceType\": " + serviceType + ", \"searchYear\": " + iYear + ", \"qryContent\" : \"" + qryContent + "\" }");
					return params;
				}
			};

			request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
			requestQueue.add(request);

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "요청 보냄.");

		}

		public void parseData(String msg)
		{

			String functionName = "parseData()";
//			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + msg);

			try
			{

				Gson gson = new Gson();

				GSMonthInOut data = gson.fromJson(msg, GSMonthInOut.class);

				showEnterprisePopup(data, this.customerName, statsType, serviceType);

			}
			catch(Exception ex)
			{
				Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ex.toString());
				return;
			}

		}

	}

	private PopupWindow popupWindow;
    private int mWidthPixels, mHeightPixels;

	private void showEnterprisePopup(GSMonthInOut data, String customerName, int statsType, int serviceType)
	{

		WindowManager w = mActivity.getWindowManager();
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);

		mWidthPixels = metrics.widthPixels;
		mHeightPixels = metrics.heightPixels;

		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
		{

			try
			{
				mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
				mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
			}
			catch (Exception ignored) {}

		}

		// 상태바와 메뉴바의 크기를 포함
		if (Build.VERSION.SDK_INT >= 17)
		{

			try
			{

				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);

				mWidthPixels = realSize.x;
				mHeightPixels = realSize.y;

			}
			catch (Exception ignored) {}

		}

		try
		{

			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View layout = inflater.inflate(R.layout.enterprise_popup, null);

			popupWindow = new PopupWindow(layout, mWidthPixels-20, LayoutParams.MATCH_PARENT, true);
			popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

			TextView popup_date = (TextView)layout.findViewById(R.id.popup_date);
			LinearLayout popup_header_container = (LinearLayout)layout.findViewById(R.id.popup_header_container);

			ListView popup_listview = (ListView)layout.findViewById(R.id.popup_listview);
			popup_listview.setDividerHeight(0);

			Button popup_close_btn = (Button)layout.findViewById(R.id.popup_close_btn);

			popup_header_container.removeAllViews();

			String statsTypeStr = "";

			if(statsType == GSConfig.STATE_AMOUNT)
			{
				statsTypeStr = "(단위:루베)";
			}
			else
			{
				statsTypeStr = "(단위:천원)";
			}

			String modeStr = GSConfig.MODE_NAMES[serviceType] + " 현황";

			String dateStr = iYear + "년 " + customerName + "\n" + modeStr + statsTypeStr;
			popup_date.setText(dateStr);

			StatsHeaderAndFooterView statsHeaderAndFooterView = new StatsHeaderAndFooterView(mActivity, data, statsType);
			statsHeaderAndFooterView.makeHeaderView(popup_header_container);

			StAdapter adapter = new StAdapter(mActivity, data, statsType);

			View foot = View.inflate(mActivity, R.layout.stats_foot, null);
			LinearLayout footer_layout = (LinearLayout)foot.findViewById(R.id.stats_footer_container);

			statsHeaderAndFooterView.makeFooterView(footer_layout);
			popup_listview.addFooterView(foot);
			popup_listview.setAdapter(adapter);

			popup_close_btn.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					popupWindow.dismiss();
				}

			});

		}
		catch(Exception ex)
		{
			Log.e(GSConfig.APP_DEBUG, "ERROR : " + this.getClass().getName() + " : showEnterprisePopup() : " + ex.toString());
			return;
		}

	}
	
	private void showErrorDialog()
	{

		AlertDialog.Builder errorDialog = new AlertDialog.Builder(mContext);
		errorDialog.setMessage(mContext.getString(R.string.loding_fail_msg));
		errorDialog.setPositiveButton(mContext.getString(R.string.dialog_confirm_button), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}

		});
		errorDialog.show();

	}

}
