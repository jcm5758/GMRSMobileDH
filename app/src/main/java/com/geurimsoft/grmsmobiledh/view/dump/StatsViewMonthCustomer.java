/**
 * 월별 거래처별 통계용
 *
 * 2021. 11. 19.
 *
 * Written by jcm5758
 *
 */
package com.geurimsoft.grmsmobiledh.view.dump;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDumpDayDetail;
import com.geurimsoft.grmsmobiledh.data.GSConfig;

import java.util.ArrayList;

public class StatsViewMonthCustomer
{

    private Activity mActivity;
    private String[] header;

    private Context mContext;

    private LinearLayout stock_layout, release_layout, petosa_layout;


    /**
     * 월별-거래처별-일별 통계
     * @param _activity     Activity
     * @param header        테이블 제목
     */
    public StatsViewMonthCustomer(Activity _activity, String[] header)
    {

        this.mContext = _activity;
        this.mActivity = _activity;
        this.header = header;

    }

    /**
     * 뷰 생성
     * @param _layout
     * @param serviceData
     * @param serviceType
     */
    public void makeStatsView(LinearLayout _layout, final int serviceType, ArrayList<GSDumpDayDetail> serviceData)
    {

        String functionName = "makeStatsView()";

        try
        {

            if (serviceData == null)
            {
                Log.e(GSConfig.APP_DEBUG, "ERROR : " + this.getClass().getName() + " : makeStatsView() : serviceData is null.");
                return;
            }

            // 헤더 지정
            String[] header_titles = this.header;

            // 레이아웃 파라미터 지정
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

            // 헤더 레이아웃 지정
            LinearLayout header_layout = new LinearLayout(mContext);
            header_layout.setLayoutParams(params);
            header_layout.setOrientation(LinearLayout.HORIZONTAL);

            // 헤더 설정
            for(int header_index = 0; header_index < this.header.length; header_index++)
            {
                TextView title_textview = makeMenuTextView(mContext, header_titles[header_index], "#ffffff", Gravity.CENTER);
                header_layout.addView(title_textview);
            }

            _layout.addView(header_layout);

            TextView stock_item_textview;

            for(int stock_index = 0; stock_index < serviceData.size(); stock_index++)
            {

                GSDumpDayDetail detail = serviceData.get(stock_index);

                LinearLayout stock_row_layout = new LinearLayout(mContext);
                stock_row_layout.setLayoutParams(params);
                stock_row_layout.setOrientation(LinearLayout.HORIZONTAL);

                int gravity = Gravity.CENTER;

                if(stock_index == serviceData.size() - 1)
                {

                    gravity = Gravity.CENTER;

                    // 거래처명
                    stock_item_textview = makeMenuTextView(mContext, detail.CustomerName, "#000000", gravity);
                    stock_row_layout.addView(stock_item_textview);

                    // 현장명
                    stock_item_textview = makeMenuTextView(mContext, detail.CustomerSiteName, "#000000", gravity);
                    stock_row_layout.addView(stock_item_textview);

                    // 품목
                    stock_item_textview = makeMenuTextView(mContext, detail.Product, "#000000", gravity);
                    stock_row_layout.addView(stock_item_textview);

                    gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

                    // 횟수
                    stock_item_textview = makeMenuTextView(mContext, GSConfig.changeToCommanString(detail.CountUnit), "#000000", gravity);
                    stock_row_layout.addView(stock_item_textview);

                    // 수량
                    stock_item_textview = makeMenuTextView(mContext, GSConfig.changeToCommanString(detail.SumUnit), "#000000", gravity);
                    stock_row_layout.addView(stock_item_textview);

                }
                else
                {

                    gravity = Gravity.CENTER;

                    // 거래처명
                    stock_item_textview = makeRowTextView(mContext, detail.CustomerName, gravity);
                    stock_row_layout.addView(stock_item_textview);

                    // 현장명
                    stock_item_textview = makeRowTextView(mContext, detail.CustomerSiteName, gravity);
                    stock_row_layout.addView(stock_item_textview);

                    // 품목
                    stock_item_textview = makeRowTextView(mContext, detail.Product, gravity);
                    stock_row_layout.addView(stock_item_textview);

                    gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

                    // 횟수
                    stock_item_textview = makeRowTextView(mContext, GSConfig.changeToCommanString(detail.CountUnit), gravity);
                    stock_row_layout.addView(stock_item_textview);

                    // 수량
                    stock_item_textview = makeRowTextView(mContext, GSConfig.changeToCommanString(detail.SumUnit), gravity);
                    stock_row_layout.addView(stock_item_textview);

                }

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
            Log.d(GSConfig.APP_DEBUG, "ERROR : " + this.getClass().getName() + "." + functionName + " : " + ex.toString());
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

}