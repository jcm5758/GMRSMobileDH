/**
 * 일보 메인
 *
 * 2021. 05. 28. 리뉴얼
 *
 * Written by jcm5758
 *
 */
package com.geurimsoft.grmsmobiledh.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.apiserver.data.UserRightData;
import com.geurimsoft.grmsmobiledh.data.GSBranch;
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.geurimsoft.grmsmobiledh.view.util.DayDatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class FragmentDailyMain extends Fragment
{

	// 슬라이딩 구현용
	private PagerTabStrip statsTabStrip;
	private ViewPager statsPager;
	private StatsPagerAdapter statsPagerAdapter;

	// 세부 프로그먼트
	private ArrayList<Fragment> fragments;

	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View v = inflater.inflate(R.layout.stats_pager_layout, container, false);

		this.context = container.getContext();

		// 프래그먼트 리스트 생성
		makeFragmentList();

		// 메뉴 설정
		setHasOptionsMenu(true);
		
		return v;

	}
	
	@Override
	public void onResume()
	{

		super.onResume();

		getActivity().invalidateOptionsMenu();

		View view = this.getView();
		
		this.statsTabStrip = (PagerTabStrip)view.findViewById(R.id.stats_tab);
		this.statsPager = (ViewPager)view.findViewById(R.id.stats_pager);
		
		this.statsTabStrip.setDrawFullUnderline(false);
		this.statsTabStrip.setTabIndicatorColor(Color.WHITE);
		this.statsTabStrip.setBackgroundColor(Color.GRAY);
		this.statsTabStrip.setNonPrimaryAlpha(0.5f);
		this.statsTabStrip.setTextSpacing(25);
		this.statsTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		this.statsTabStrip.setTextColor(Color.WHITE);
		this.statsTabStrip.setPadding(5, 5, 5, 5);

		this.statsPagerAdapter = new StatsPagerAdapter(getChildFragmentManager());
		this.statsPager.setAdapter(statsPagerAdapter);

	}

	/**
	 * 프래그먼트 리스트 만들기
	 * 일보 수량, 금액
	 */
	private void makeFragmentList()
	{

		String fn = "makeFragmentList()";

		fragments = new ArrayList<Fragment>();

		UserRightData urData = GSConfig.CURRENT_USER.getCurrentUserRight(GSConfig.CURRENT_BRANCH.branchID);

		if (urData == null) return;

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(getActivity().getClass().getName(), fn) + " branchID : " + GSConfig.CURRENT_BRANCH.branchID + ", ur02 : " + urData.ur02 + ", ur03 : " + urData.ur03 );

		if (urData.ur02 == 1 || urData.ur03 == 1)
			fragments.add(new FragmentDailyAmount());

		if (urData.ur03 == 1)
			fragments.add(new FragmentDailyPrice());

	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{

		super.onCreateOptionsMenu(menu, inflater);

		menu.add(0, 1, 0, "날짜 변경");
		menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		ArrayList<UserRightData> urd = GSConfig.CURRENT_USER.getUserRightOthers();

		if (urd.size() == 1)
		{
			menu.add(0, 2, 0, urd.get(0).branShortName);
			menu.getItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		else if (urd.size() > 1)
		{

			SubMenu subMenu = menu.addSubMenu("지점선택");

			for(int iter = 0; iter < urd.size(); iter++)
			{
				subMenu.add(1, iter + 2, iter, urd.get(iter).branShortName);
			}

		}

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		String fn = "onOptionsItemSelected()";

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), fn) + " : item.getItemId() : " + item.getItemId());

		// 날짜 변경
		if (item.getItemId() == 1)
		{

			DayDatePickerDialog dayDatePickerDialog = new DayDatePickerDialog(getActivity(), GSConfig.CURRENT_YEAR, GSConfig.CURRENT_YEAR+10,  GSConfig.CURRENT_MONTH, GSConfig.CURRENT_DAY, new DayDatePickerDialog.DialogListner() {

				@Override
				public void OnConfirmButton(Dialog dialog, int selectYear, int selectMonth, int selectDay) {

					if(GSConfig.LIMIT_YEAR > selectYear || selectYear > GSConfig.CURRENT_YEAR)
					{
						Toast.makeText(getActivity(), getString(R.string.change_date_year_error), Toast.LENGTH_SHORT).show();
						return;
					}

					if(GSConfig.LIMIT_YEAR == selectYear && GSConfig.LIMIT_MONTH > selectMonth )
					{
						Toast.makeText(getActivity(), getString(R.string.change_date_month_error), Toast.LENGTH_SHORT).show();
						return;
					}

					if( GSConfig.CURRENT_YEAR == selectYear  && selectMonth > GSConfig.CURRENT_MONTH )
					{
						Toast.makeText(getActivity(), getString(R.string.change_date_month_error), Toast.LENGTH_SHORT).show();
						return;
					}

					if(GSConfig.CURRENT_MONTH ==  selectMonth && selectDay > GSConfig.CURRENT_DAY )
					{
						Toast.makeText(getActivity(), getString(R.string.change_date_day_error), Toast.LENGTH_SHORT).show();
						return;
					}

					if(GSConfig.CURRENT_YEAR != selectYear || GSConfig.CURRENT_MONTH != selectMonth || GSConfig.CURRENT_DAY != selectDay)
					{
						GSConfig.setDate(selectYear, selectMonth, selectDay);
						statsPagerAdapter.notifyDataSetChanged();
					}

					dialog.dismiss();

				}

			});

			dayDatePickerDialog.show();

		}
		else
		{

			int orderNum = item.getOrder();

			ArrayList<UserRightData> urDatas = GSConfig.CURRENT_USER.getUserRightOthers();

			UserRightData urData = urDatas.get(orderNum);

			GSConfig.CURRENT_BRANCH = new GSBranch(urData.branID, urData.branName, urData.branShortName);

			Intent intent = new Intent(context, GSConfig.Activity_LIST[0]);
			intent.putExtra("branID", GSConfig.CURRENT_BRANCH.branchID);
			intent.putExtra("branName", GSConfig.CURRENT_BRANCH.branchShortName);

			startActivity(intent);

		}

		return true;

	}
	
	public class StatsPagerAdapter extends FragmentPagerAdapter
	{

		private final String[] TITLES;

		public StatsPagerAdapter(FragmentManager fm)
		{

			super(fm);

			if (GSConfig.CURRENT_USER.getCurrentUserRight(GSConfig.CURRENT_BRANCH.branchID).ur03 == 1)
				TITLES = getResources().getStringArray(R.array.stats_day_tab_array1);
			else
				TITLES = getResources().getStringArray(R.array.stats_day_tab_array2);

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			if (position != TITLES.length)
				super.destroyItem(container, position, object);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position)
		{

			if (position < 0 || position >= fragments.size())
				return null;

			return fragments.get(position);

		}

		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}

	}

}
