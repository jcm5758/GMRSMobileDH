/**
 * 연보 메인
 *
 * 2021. 11. 17.
 *
 *  Written by jcm5758
 *
 */

package com.geurimsoft.grmsmobiledh.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

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
import com.geurimsoft.grmsmobiledh.view.util.YearDatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class FragmentYearMain extends Fragment
{
	
	private Calendar calendar = Calendar.getInstance();
	private int currentYear;
	
	private PagerTabStrip statsTabStrip;
	private ViewPager statsPager;
	private StatsPagerAdapter statsPagerAdapter;
	
	private ArrayList<Fragment> fragments;

	Context context;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.currentYear =  calendar.get(Calendar.YEAR);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View v = inflater.inflate(R.layout.stats_pager_layout, container, false);
		this.context = container.getContext();
		
		if(GSConfig.DAY_STATS_YEAR == 0 || GSConfig.DAY_STATS_MONTH == 0 ||GSConfig.DAY_STATS_DAY == 0)
		{
			GSConfig.DAY_STATS_YEAR = this.currentYear;
		}
		
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

		this.statsPager = (ViewPager)view.findViewById(R.id.stats_pager);

		this.statsTabStrip = (PagerTabStrip)view.findViewById(R.id.stats_tab);
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

		if (urData.ur08 == 1 || urData.ur09 == 1)
			fragments.add(new FragmentYearAmount());

		if (urData.ur09 == 1)
			fragments.add(new FragmentYearPrice());

		if (urData.ur10 == 1 || urData.ur11 == 1)
			fragments.add(new FragmentYearCustomerAmount());

		if (urData.ur11 == 1)
			fragments.add(new FragmentYearCustomerPrice());

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

			YearDatePickerDialog yearDatePickerDialog = new YearDatePickerDialog(getActivity(), GSConfig.DAY_STATS_YEAR, GSConfig.DAY_STATS_YEAR+10, new YearDatePickerDialog.DialogListner() {

				@Override
				public void OnConfirmButton(Dialog dialog, int selectYear) {

					if(GSConfig.LIMIT_YEAR > selectYear || selectYear > currentYear)
					{
						Toast.makeText(getActivity(), getString(R.string.change_date_year_error), Toast.LENGTH_SHORT).show();
						return;
					}

					if(GSConfig.DAY_STATS_YEAR != selectYear)
					{
						GSConfig.DAY_STATS_YEAR = selectYear;
						statsPagerAdapter.notifyDataSetChanged();
					}

					dialog.dismiss();

				}

			});

			yearDatePickerDialog.show();

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

			if (urData.ur08 == 1 || urData.ur09 == 1)
				titleList.add("수량");

			if (urData.ur09 == 1)
				titleList.add("금액");

			if (urData.ur10 == 1 || urData.ur11 == 1)
				titleList.add("수량(업체별)");

			if (urData.ur11 == 1)
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
			return fragments.get(position);
		}

		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}

	}

}
