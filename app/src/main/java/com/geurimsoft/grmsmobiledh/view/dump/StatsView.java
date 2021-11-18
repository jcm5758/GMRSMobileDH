package com.geurimsoft.grmsmobiledh.view.dump;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDailyInOut;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDailyInOutDetail;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDailyInOutGroup;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDumpDay;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDumpDetail;
import com.geurimsoft.grmsmobiledh.data.GSConfig;

import java.util.ArrayList;

public class StatsView
{

    private LinearLayout stock_layout, release_layout, petosa_layout;
    private LinearLayout stock_layout_outside, release_layout_outside;

    private Context mContext;

    private GSDumpDay dumpData;

    private String[] header;
//    private ArrayList<GSDumpDetail> serviceInput;
//    private ArrayList<GSDumpDetail> serviceOutput;
//    private ArrayList<GSDumpDetail> serviceSluge;

    private int stock_count;
    private int release_count;
    private int petosa_count;
    private int stock_outside_count;
    private int release_outside_count;

    public StatsView(Context _context, GSDumpDay dumpData)
    {

        this.mContext = _context;
        this.dumpData = dumpData;

        this.header = this.dumpData.header;
//        this.serviceInput = this.data.serviceInput;
//        this.serviceOutput = this.data.serviceOutput;
//        this.serviceSluge = this.data.serviceSluge;

    }

    /**
     * 입고/출고/토사 데이터 테이블로 표출
     * @param _stock_layout
     */
    public void makeTableView(LinearLayout _stock_layout, ArrayList<GSDumpDetail> serviceData)
    {

        String functionName = "makeTableView()";

        // 입고 리스트 미존재시 패스
        if(serviceData == null || serviceData.size() <= 0)
        {

            if (GSConfig.IsDebugging)
                Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(),functionName) + " : serviceData is null.");

            return;

        }

        // 입고 레이아웃 지정
        this.stock_layout = _stock_layout;

        // Layout parameter
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout header_layout = new LinearLayout(mContext);
        header_layout.setLayoutParams(params);
        header_layout.setOrientation(LinearLayout.HORIZONTAL);

        // Header Layout
        for(int stock_header_index = 0; stock_header_index < this.header.length; stock_header_index++)
        {
            TextView stock_title_textview = makeMenuTextView(mContext, this.header[stock_header_index], "#ffffff", Gravity.CENTER);
            header_layout.addView(stock_title_textview);
        }

        stock_layout.addView(header_layout);

        // 본문 레이아웃
        TextView stock_item_textview;

        for(int stock_index = 0; stock_index < serviceData.size(); stock_index++)
        {

            // 상세 정보
            GSDumpDetail detail = serviceData.get(stock_index);

            // 레이아웃
            LinearLayout stock_row_layout = new LinearLayout(mContext);
            stock_row_layout.setLayoutParams(params);
            stock_row_layout.setOrientation(LinearLayout.HORIZONTAL);

            // 가운데 정렬
            int gravity = Gravity.CENTER;

            //-------------------------------------------
            // 거래처명
            //-------------------------------------------

            if (detail.CustomerName.equals("소계"))
                stock_item_textview = makeMenuTextView(mContext, detail.CustomerName, "#000000", gravity);
            else
                stock_item_textview = makeRowTextView(mContext, detail.CustomerName, gravity);

            stock_row_layout.addView(stock_item_textview);

            //-------------------------------------------
            // 현장명
            //-------------------------------------------

            if (detail.CustomerName.equals("소계"))
                stock_item_textview = makeMenuTextView(mContext, detail.CustomerSiteName, "#000000", gravity);
            else
                stock_item_textview = makeRowTextView(mContext, detail.CustomerSiteName, gravity);

            stock_row_layout.addView(stock_item_textview);

            //-------------------------------------------
            // 품명
            //-------------------------------------------

            if (detail.CustomerName.equals("소계"))
                stock_item_textview = makeMenuTextView(mContext, detail.Product, "#000000", gravity);
            else
                stock_item_textview = makeRowTextView(mContext, detail.Product, gravity);

            stock_row_layout.addView(stock_item_textview);

            //-------------------------------------------
            // 횟수
            //-------------------------------------------

            gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

            if (detail.CustomerName.equals("소계"))
                stock_item_textview = makeMenuTextView(mContext, GSConfig.changeToCommanString(detail.CountUnit), "#000000", gravity);
            else
                stock_item_textview = makeRowTextView(mContext, GSConfig.changeToCommanString(detail.CountUnit), gravity);

            stock_row_layout.addView(stock_item_textview);

            //-------------------------------------------
            // 수량
            //-------------------------------------------

            if (detail.CustomerName.equals("소계"))
                stock_item_textview = makeMenuTextView(mContext, GSConfig.changeToCommanString(detail.SumUnit), "#000000", gravity);
            else
                stock_item_textview = makeRowTextView(mContext, GSConfig.changeToCommanString(detail.SumUnit), gravity);

            stock_row_layout.addView(stock_item_textview);

            stock_layout.addView(stock_row_layout);

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
        tv.setTypeface(null, Typeface.BOLD);
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