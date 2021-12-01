/**
 * 월별 메인
 *
 * 2021. 11. 19.
 *
 * Written by jcm5758
 *
 */

package com.geurimsoft.grmsmobiledh.view.dump;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.data.GSConfig;
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

    /**
     * 프래그먼트 리스트 만들기
     */
    private void makeFragmentList()
    {

        String fn = "makeFragmentList()";

        fragments = new ArrayList<Fragment>();
        fragments.add(new FragmentMonthAmount(1));
        fragments.add(new FragmentMonthAmount(2));
        fragments.add(new FragmentMonthCustomerAmount(1));
        fragments.add(new FragmentMonthCustomerAmount(2));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {

        super.onCreateOptionsMenu(menu, inflater);

        menu.add(0, 1, 0, "날짜 변경");
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        String fn = "onOptionsItemSelected()";

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

        return true;

    }

    public class StatsPagerAdapter extends FragmentPagerAdapter
    {

        private String[] TITLES;

        public StatsPagerAdapter(FragmentManager fm)
        {
            super(fm);
            TITLES = new String[]{ "풍년가", "디에이치", "풍년가(거래처별)", "디에이치(거래처별)" };
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
