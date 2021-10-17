package com.geurimsoft.bokangnew.view.kwangju;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.geurimsoft.bokangnew.R;
import com.geurimsoft.bokangnew.apiserver.data.UserRightData;
import com.geurimsoft.bokangnew.data.GSBranch;
import com.geurimsoft.bokangnew.data.GSConfig;
import com.geurimsoft.bokangnew.view.util.YearDatePickerDialog;

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
		
		fragments = new ArrayList<Fragment>();
		
		fragments.add(new FragmentYearAmount());
		fragments.add(new FragmentYearPrice());
		fragments.add(new FragmentYearCustomerAmount());
		fragments.add(new FragmentYearCustomerPrice());
		fragments.add(new FragmentYearGraph());

	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		 inflater.inflate(R.menu.stats_menu2, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		 switch (item.getItemId())
		 {

			 case R.id.stats_change_branchjoomyung:

				 int which = 1;

				 if (GSConfig.CURRENT_USER.getUserRightData(which).getUr01() != 1)
				 {
					 Toast.makeText(context, "지점에 로그인 권한이 없습니다.", Toast.LENGTH_SHORT).show();
					 return false;
				 }

				 ArrayList<UserRightData> urData = GSConfig.CURRENT_USER.getUserright();

				 GSConfig.CURRENT_BRANCH = new GSBranch(urData.get(which).getBranID(), urData.get(which).getBranName(), urData.get(which).getBranShortName());

				 Intent intent = new Intent(context, GSConfig.Activity_LIST[which]);
				 intent.putExtra("branName", GSConfig.CURRENT_BRANCH.getBranchShortName());

				 startActivity(intent);

				 return true;

		 	case R.id.stats_change_date_menu:
		    	   
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

		 		return true;

		 	default:
		 		return super.onOptionsItemSelected(item);

		 }

	}
	
	public class StatsPagerAdapter extends FragmentPagerAdapter
	{

		private final String[] TITLES;

		public StatsPagerAdapter(FragmentManager fm)
		{
			super(fm);
			TITLES = getResources().getStringArray(R.array.stats_year_tab_array1);
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
