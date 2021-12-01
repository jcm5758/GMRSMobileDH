/**
 * 월별 메인
 *
 * 2021. 05. 30 리뉴얼
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.apiserver.data.UserRightData;
import com.geurimsoft.grmsmobiledh.data.GSBranch;
import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.geurimsoft.grmsmobiledh.view.util.DayDatePickerDialog;
import com.geurimsoft.grmsmobiledh.view.util.MonthDatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class FragmentMonthMain extends Fragment
{
	
	private PagerTabStrip statsTabStrip;
	private ViewPager statsPager;
	private StatsPagerAdapter statsPagerAdapter;
	
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

		makeFragmentList();
		
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
		this.statsTabStrip.setPadding(10, 10, 10, 10);

		this.statsPagerAdapter = new StatsPagerAdapter(getChildFragmentManager());
		this.statsPager.setAdapter(statsPagerAdapter);

	}

	private void makeFragmentList()
	{
		
		String fn = "makeFragmentList()";

		fragments = new ArrayList<Fragment>();

		UserRightData urData = GSConfig.CURRENT_USER.getCurrentUserRight(GSConfig.CURRENT_BRANCH.branchID);

		if (urData == null) return;

//		Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(getActivity().getClass().getName(), fn) + " branchID : " + GSConfig.CURRENT_BRANCH.branchID + ", ur02 : " + urData.ur02 + ", ur03 : " + urData.ur03 );

		if (urData.ur04 == 1 || urData.ur05 == 1)
			fragments.add(new FragmentMonthAmount());

		if (urData.ur05 == 1)
			fragments.add(new FragmentMonthPrice());

		if (urData.ur06 == 1 || urData.ur07 == 1)
			fragments.add(new FragmentMonthCustomerAmount());

		if (urData.ur07 == 1)
			fragments.add(new FragmentMonthCustomerPrice());

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

			MonthDatePickerDialog monthDatePickerDialog = new MonthDatePickerDialog(getActivity(), GSConfig.CURRENT_YEAR, GSConfig.CURRENT_YEAR+10,  GSConfig.CURRENT_MONTH, new MonthDatePickerDialog.DialogListner() {

				@Override
				public void OnConfirmButton(Dialog dialog, int selectYear, int selectMonth) {

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

					if(GSConfig.CURRENT_YEAR != selectYear || GSConfig.CURRENT_MONTH != selectMonth)
					{
						GSConfig.setDate(selectYear, selectMonth, 1);
						statsPagerAdapter.notifyDataSetChanged();
					}

					dialog.dismiss();

				}
			});

			monthDatePickerDialog.show();

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

		private String[] TITLES;
		
		public StatsPagerAdapter(FragmentManager fm)
		{

			super(fm);

			ArrayList<String> titleList = new ArrayList<String>();

			UserRightData urData = GSConfig.CURRENT_USER.getCurrentUserRight(GSConfig.CURRENT_BRANCH.branchID);

			if (urData == null) return;

			if (urData.ur04 == 1 || urData.ur05 == 1)
				titleList.add("수량");

			if (urData.ur05 == 1)
				titleList.add("금액");

			if (urData.ur06 == 1 || urData.ur07 == 1)
				titleList.add("수량(업체별)");

			if (urData.ur07 == 1)
				titleList.add("금액(업체별)");

			TITLES = titleList.toArray(new String[titleList.size()]);

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
			Fragment newFragment = fragments.get(position);
			return newFragment;
		}

		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}

	}

}
