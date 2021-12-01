/**
 * Main Activity
 *
 * 2021. 05. 28 리뉴얼
 *
 * Written by jcm5758
 *
 */

package com.geurimsoft.grmsmobiledh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.geurimsoft.grmsmobiledh.apiserver.data.UserInfo;
import com.geurimsoft.grmsmobiledh.apiserver.data.UserRightData;
import com.geurimsoft.grmsmobiledh.data.GSBranch;
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.geurimsoft.grmsmobiledh.data.GSUserRight;
import com.geurimsoft.grmsmobiledh.payloader.Payloader;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AppMain extends Activity
{

	// User Layout 변수
	EditText edtLogin, edtPasswd;
	CheckBox chkAutoLogin;
	Button btnlogin;

	public View dialogView;

	private long backKeyPressedTime = 0;
	private Toast appFinishedToast;

	private AlertDialog alertDialog;

	private RadioGroup rgBranch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		// 날짜 관련
		GSConfig.calendar = Calendar.getInstance();

		GSConfig.CURRENT_YEAR = 0;
		GSConfig.CURRENT_MONTH = 0;
		GSConfig.CURRENT_DAY = 0;

		GSConfig.activities.add(AppMain.this);

		GSConfig.context = this;

		// 날짜 지정
		GSConfig.setDateCurrent();

		setUserInterface();

		// 자동 로그인 체크시
		autoCheck();

		// 앱 버전 확인
		appVersionCheck();

	}

	/**
	 * 위젯 받아오기
	 */
	public void setUserInterface()
	{

		GSConfig.gPreference = getSharedPreferences("user_account", Context.MODE_PRIVATE);

		// 아이디
		edtLogin = (EditText) this.findViewById(R.id.edtlogin);
		edtLogin.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		edtLogin.setPrivateImeOptions("defaultInputmode=english");

		// 비밀번호
		edtPasswd = (EditText) this.findViewById(R.id.edtpasswd);
		edtPasswd.setImeOptions(EditorInfo.IME_ACTION_DONE);
		edtPasswd.setPrivateImeOptions("defaultInputmode=english");

		// 자동 로그인
		chkAutoLogin = (CheckBox) this.findViewById(R.id.chkAutoLogin);

		// 로그인 버튼
		btnlogin = (Button) this.findViewById(R.id.btnlogin);
		
		// 이벤트 등록
		btnlogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtPasswd.getWindowToken(), 0);

				checkUser();

			}

		});

	}

	/**
	 * 자동 로그인 확인 시 기본 정보 저장
	 */
	private void autoCheck()
	{

		// 기본 정보 가져오기
		GSConfig.gPreference = getSharedPreferences("GRMS_DH", Context.MODE_PRIVATE);

		// ID
		String sId = GSConfig.gPreference.getString("userID", null);

		// Password
		String sPass = GSConfig.gPreference.getString("userPWD", null);

		// 자동 저장
		boolean auto_chcek = GSConfig.gPreference.getBoolean("outo_chcek", false);

		chkAutoLogin.setChecked(auto_chcek);
		
		if (auto_chcek)
		{
			edtLogin.setText(sId);
			edtPasswd.setText(sPass);
		}
		else
		{
			SharedPreferences.Editor removeEditor = GSConfig.gPreference.edit();
			removeEditor.remove("userID");
			removeEditor.remove("userPWD");
			removeEditor.remove("auto_chcek");
			removeEditor.commit();
		}

	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}

	/**
	 * 로그인 입력 정보 확인
	 * @return
	 */
	public boolean checkUser()
	{

		String sId = edtLogin.getText().toString();
		String sPass = edtPasswd.getText().toString();

		if (sId == null || sId.trim().length() == 0)
		{
			Toast.makeText(getApplicationContext(), "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (sPass == null || sPass.trim().length() == 0)
		{
			Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
			return false;
		}

		return LoginCheck(sId, sPass);

	}

	@Override
	public void onBackPressed()
	{

		if(System.currentTimeMillis() > backKeyPressedTime + 2000)
		{
			backKeyPressedTime = System.currentTimeMillis();
			appFinishedToast = Toast.makeText(this, getString(R.string.app_finished_msg), Toast.LENGTH_LONG);
			appFinishedToast.show();
			return;
		}

		if(System.currentTimeMillis() <= backKeyPressedTime + 2000)
		{

			if(GSConfig.activities.size() > 0)
			{
				for(int actIndex = 0; actIndex < GSConfig.activities.size(); actIndex++)
					GSConfig.activities.get(actIndex).finish();
			}

			appFinishedToast.cancel();

		}
	}

	/**
	 * 로그인 권한 확인 및 지점 정보 조회
	 * @param userID    	User ID
	 * @param userPWD		User PWD
	 * @return
	 */
	private boolean LoginCheck(String userID, String userPWD)
	{

		String functionName = "getLogin()";

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
						parseData(userID, userPWD, response);
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
				params.put("GSType", "LOGIN");
				params.put("GSQuery", "{ \"UserID\" : \"" + userID + "\", \"UserPWD\" : \"" + userPWD + "\"}");
				return params;
			}
		};

		request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
		requestQueue.add(request);

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "요청 보냄.");

		return true;

	}

	/**
	 * 데이터 파싱 후 UserInfo로 변환
	 * @param userID	사용자 ID
	 * @param userPWD	비밀번호
	 * @param msg		수신 json
	 */
	public void parseData(String userID, String userPWD, String msg)
	{

		String functionName = "parseData()";

		if (GSConfig.IsDebugging)
			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ": msg : " + msg);

		Gson gson = new Gson();

		// 데이터 변환
		UserInfo userInfo = gson.fromJson(msg, UserInfo.class);

		// 사용자 정보가 없으면 접속 제한
		if (userInfo == null)
		{
			Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + "userInfo is null.");
			return;
		}

		if (GSConfig.IsDebugging)
		{
			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ": status : " + userInfo.status);
			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), functionName) + ": message : " + userInfo.message);
		}

		if (userInfo.status.equals("FAIL"))
		{
			Toast.makeText(getApplicationContext(), "사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		// 사용자 정보 설정
		GSConfig.CURRENT_USER = userInfo;

		if (GSConfig.IsDebugging)
		{

			GSConfig.CURRENT_USER.userinfo.print();

			if (GSConfig.CURRENT_USER.userright != null)
			{
				GSConfig.CURRENT_USER.printUserRight();
			}

			if (GSConfig.CURRENT_USER.servicepredict != null)
			{
				GSConfig.CURRENT_USER.printServicePredict();
			}

		}

		SharedPreferences.Editor editor = GSConfig.gPreference.edit();
		editor.putString("userID", userID);
		editor.putString("userPWD", userPWD);
		editor.putBoolean("outo_chcek", chkAutoLogin.isChecked());
		editor.commit();

		Toast.makeText(getApplicationContext(),GSConfig.CURRENT_USER.userinfo.Name + getString(R.string.login_success), Toast.LENGTH_SHORT).show();

		if (GSConfig.CURRENT_USER.message.equals(GSConfig.LOGIN_SUCESS_MESSAGE))
		{
			// 현장 선택
			showBranch();
		}
		else if (GSConfig.CURRENT_USER.message.equals(GSConfig.LOGIN_FOREIGN_SUCESS_MESSAGE))
		{

			// 덤프트럭 통계로 이동
			Intent intent = new Intent(AppMain.this, GSConfig.Activity_LIST[3]);
			startActivity(intent);

//			// 배차 정보가 없습니다.
//			if (GSConfig.CURRENT_USER.isServicePredictNull())
//			{
//				Toast.makeText(getApplicationContext(),"배차 정보가 없습니다.", Toast.LENGTH_SHORT).show();
//				return;
//			}

//			// 덤프트럭 배차로 이동
//			Intent intent = new Intent(AppMain.this, GSConfig.Activity_LIST[2]);
//			startActivity(intent);

		}

	}

	/**
	 * 현장 선택
	 */
	private void showBranch()
	{

		if (GSConfig.CURRENT_USER == null || GSConfig.CURRENT_USER.isUserInfoNull())
		{
			Toast.makeText(getApplicationContext(),"사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		if (GSConfig.CURRENT_USER.isUserRightNull())
		{
			Toast.makeText(getApplicationContext(),"사용자 권한 정보가 없습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		String fn = "showBranch()";

		// 뷰 레이아웃 생성
		dialogView = (View) View.inflate(getApplicationContext(), R.layout.branch_layout, null);

		// 라디오 그룹
		rgBranch = (RadioGroup)dialogView.findViewById(R.id.rgBranch);

		// 상차 버튼
		Button btPayload = (Button)dialogView.findViewById(R.id.btPayload);

		// 통계 버튼
		Button btStat = (Button)dialogView.findViewById(R.id.btStat);

		// 사용자 권한
		ArrayList<UserRightData> urData = GSConfig.CURRENT_USER.userright;

		//---------------------------------------------------------------------------
		// 지점 리스트 만들기
		//---------------------------------------------------------------------------

		for(int i = 0; i < urData.size(); i++)
		{

			//Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), fn) + ": branShortName : " + urData.get(i).branShortName);

			RadioButton radioButton = new RadioButton(this);
			radioButton.setText( urData.get(i).branShortName );
			radioButton.setId(i + 101);
			radioButton.setTextColor(Color.BLACK);

			int finalIndex = i;

			radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{

					//Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), fn) + ": Id : " + buttonView.getId());
					//Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), fn) + ": isChecked : " + isChecked);

					if (!isChecked) return;

					// 사용자 권한
					ArrayList<UserRightData> urData = GSConfig.CURRENT_USER.userright;

					for(int i = 0; i < urData.size(); i++)
					{

						if (buttonView.getId() == (i + 101))
						{

							if ( urData.get(finalIndex).ur12 == 0 )
							{
								btPayload.setVisibility(View.INVISIBLE);
							}
							else
							{
								btPayload.setVisibility(View.VISIBLE);
							}

						}

					}

				}

			});

			rgBranch.addView(radioButton);

		}

		//---------------------------------------------------------------------------
		// 상차 버튼
		//---------------------------------------------------------------------------

		btPayload.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View view)
			{

				// 지점 설정
				setBranch();

				Intent intent = new Intent(AppMain.this, GSConfig.Activity_LIST[1]);
				intent.putExtra("branID", GSConfig.CURRENT_BRANCH.branchID);
				intent.putExtra("branName", GSConfig.CURRENT_BRANCH.branchShortName);

				startActivity(intent);

				alertDialog.dismiss();

			}

		});

		//---------------------------------------------------------------------------
		// 통계 버튼 이벤트
		//---------------------------------------------------------------------------

		btStat.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View view)
			{

				// 지점 설정
				setBranch();

//				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), fn) + "------------------------------------------");

//				GSConfig.CURRENT_BRANCH.print();

//				GSConfig.CURRENT_USER.getCurrentUserRight(GSConfig.CURRENT_BRANCH.branchID).print();

				Intent intent = new Intent(AppMain.this, GSConfig.Activity_LIST[0]);

				startActivity(intent);

				alertDialog.dismiss();

			}

		});

		//---------------------------------------------------------------------------
		// 다이얼로그 생성
		//---------------------------------------------------------------------------

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.site_msg));
		builder.setView(dialogView);

		alertDialog = builder.create();
		alertDialog.show();

	}

	/**
	 * 지점 설정
	 */
	public void setBranch()
	{

		String fn = "setBranch()";

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), fn) + ": rgBranch.getCheckedRadioButtonId() : " + rgBranch.getCheckedRadioButtonId());

		// 사용자 권한
		ArrayList<UserRightData> urData = GSConfig.CURRENT_USER.userright;

		for(int i = 0; i < urData.size(); i++)
		{

			if (rgBranch.getCheckedRadioButtonId() == (i + 101))
			{
				GSConfig.CURRENT_BRANCH = new GSBranch(urData.get(i).branID, urData.get(i).branName, urData.get(i).branShortName);
				return;
			}

		}

	}
	
	private void appVersionCheck()
	{
		new VersionCheckTask().execute();
	}
	
	public int getVersionCode(Context context)
	{
		 
		try
		{
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		}
		catch (NameNotFoundException e)
		{
			return 0;
		}

	}

	/**
	 * 앱 업데이트를 위해 Gooegle Market으로 이동 혹은 취소
	 */
	private void showUpdateDialog()
	{

		AlertDialog.Builder successDia = new AlertDialog.Builder(this);
		successDia.setMessage(this.getString(R.string.update_msg));

		successDia.setPositiveButton(this.getString(R.string.update_yesbutton), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.geurimsoft.bokangnew"));
				startActivity(intent);
			}
		});

		successDia.setNegativeButton(this.getString(R.string.update_canclebutton), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		successDia.show();

	}

	/**
	 * 앱 버전 확인
	 */
	public class VersionCheckTask extends AsyncTask<String, String, String>
	{

		private ProgressDialog progress;
		private String newVersionCode;

		public VersionCheckTask() {}

		@Override
		protected void onPreExecute()
		{

			super.onPreExecute();

			progress = new ProgressDialog(AppMain.this);
			progress.setMessage(getString(R.string.update_check));
			progress.show();

		}

		@Override
		protected String doInBackground(String... params)
		{

			try
			{

				URL url = new URL(GSConfig.WEB_SERVER_ADDR);
//				Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "doInBackground") + " url : " + url.toString());

				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				
				if (conn != null)
				{

					conn.setConnectTimeout(10000);
					conn.setUseCaches(false);
					
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
					{

						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						newVersionCode = br.readLine();

//						Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "doInBackground") + " newVersionCode : " + newVersionCode);
							
						br.close();
					}

					conn.disconnect();

				}

			}
			catch (Exception ex)
			{
				Log.e("BOKANG", "newVersionCode ex : "+ex.getMessage());
			}

			return newVersionCode;

		}

		@Override
		protected void onPostExecute(String result)
		{

			super.onPostExecute(result);
			
			progress.dismiss();
			
			int versionCode = getVersionCode(AppMain.this);

//			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "doInBackground") + " versionCode : " + versionCode);
//			Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "doInBackground") + " result : " + result);

			// 서버 버전보다 오래된 것이면 구글 마켓으로 이동 물어보기
			if(versionCode < Integer.parseInt(result))
			{
				showUpdateDialog();
			}

		}

	}

}
