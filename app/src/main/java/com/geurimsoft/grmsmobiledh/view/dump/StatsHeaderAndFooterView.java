package com.geurimsoft.grmsmobiledh.view.dump;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDumpMonthDetail;
import com.geurimsoft.grmsmobiledh.data.GSConfig;

import java.util.ArrayList;

public class StatsHeaderAndFooterView
{

    private Context mContext;

    private LinearLayout header_layout, footer_layout;

    public StatsHeaderAndFooterView(Context _context)
    {
        this.mContext = _context;
    }

    /**
     * 통계 테이블의 헤더 부분
     * @param _header_layout
     */
    public void makeHeaderView(LinearLayout _header_layout, String[] headers)
    {

        this.header_layout = _header_layout;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        // Header Layout
        for(int header_index = 0; header_index < headers.length; header_index++)
        {
            TextView stock_title_textview = makeMenuTextView(mContext, headers[header_index], "#ffffff", Gravity.CENTER);
            layout.addView(stock_title_textview);
        }

        header_layout.addView(layout);

    }

    /**
     * 통계 테이블의 맨마지막 합계 부분
     * @param _footer_layout
     */
    public void makeFooterView(LinearLayout _footer_layout, GSDumpMonthDetail detail)
    {

        this.footer_layout = _footer_layout;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textview = null;
        int gravity = Gravity.CENTER;

        // 일
        gravity = Gravity.CENTER;
        textview = makeMenuTextView(mContext, detail.SDate, "#000000",  gravity);
        layout.addView(textview);

        gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

        // 입고(횟수)
        textview = makeMenuTextView(mContext, GSConfig.changeToCommanString(detail.InputCount), "#000000",  gravity);
        layout.addView(textview);

        // 입고(수량)
        textview = makeMenuTextView(mContext, GSConfig.changeToCommanString(detail.InputSum), "#000000",  gravity);
        layout.addView(textview);

        // 출고(횟수)
        textview = makeMenuTextView(mContext, GSConfig.changeToCommanString(detail.OutputCount), "#000000",  gravity);
        layout.addView(textview);

        // 출고(수량)
        textview = makeMenuTextView(mContext, GSConfig.changeToCommanString(detail.OutputSum), "#000000",  gravity);
        layout.addView(textview);

        // 토사(횟수)
        textview = makeMenuTextView(mContext, GSConfig.changeToCommanString(detail.SlugeCount), "#000000",  gravity);
        layout.addView(textview);

        // 토사(수량)
        textview = makeMenuTextView(mContext, GSConfig.changeToCommanString(detail.SlugeSum), "#000000",  gravity);
        layout.addView(textview);

        footer_layout.addView(layout);

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

}
