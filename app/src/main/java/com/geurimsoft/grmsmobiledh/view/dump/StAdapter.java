package com.geurimsoft.grmsmobiledh.view.dump;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDumpMonth;
import com.geurimsoft.grmsmobiledh.apiserver.data.GSDumpMonthDetail;
import com.geurimsoft.grmsmobiledh.data.GSConfig;

public class StAdapter extends BaseAdapter
{

    private Context mContext;
    private GSDumpMonth serviceData;

    private LayoutInflater mInflater;

    public StAdapter(Context _context, GSDumpMonth serviceData)
    {

        this.mContext = _context;
        this.serviceData = serviceData;

        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount()
    {
        return this.serviceData.ServiceData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return this.serviceData.ServiceData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * 통계 테이블의 중간 내용
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        StAdapter.ViewHolder holder;

        if (convertView == null)
        {

            holder = new StAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.st_list_row, null);

            holder.st_list_row_container = (LinearLayout) convertView.findViewById(R.id.st_list_row_container);
            holder.textview_List = new ArrayList<TextView>();

            for(int i = 0; i < this.serviceData.header.length; i++)
            {

                TextView textview = null;

                if(i == 0)
                    textview = makeTextView(mContext, Gravity.CENTER);
                else
                    textview = makeTextView(mContext, Gravity.CENTER_VERTICAL | Gravity.RIGHT);

                holder.textview_List.add(textview);

            }

            convertView.setTag(holder);

        }
        else
        {
            holder = (StAdapter.ViewHolder) convertView.getTag();
        }

        holder.st_list_row_container.removeAllViews();

        GSDumpMonthDetail detail = (GSDumpMonthDetail)this.getItem(position);

        // 일
        holder.textview_List.get(0).setText(detail.SDate);
        holder.st_list_row_container.addView(holder.textview_List.get(0));

        // 입고(횟수)
        holder.textview_List.get(1).setText(GSConfig.changeToCommanString(detail.InputCount));
        holder.st_list_row_container.addView(holder.textview_List.get(1));

        // 입고(수량)
        holder.textview_List.get(2).setText(GSConfig.changeToCommanString(detail.InputSum));
        holder.st_list_row_container.addView(holder.textview_List.get(2));

        // 출고(횟수)
        holder.textview_List.get(3).setText(GSConfig.changeToCommanString(detail.OutputCount));
        holder.st_list_row_container.addView(holder.textview_List.get(3));

        // 출고(수량)
        holder.textview_List.get(4).setText(GSConfig.changeToCommanString(detail.OutputSum));
        holder.st_list_row_container.addView(holder.textview_List.get(4));

        // 토사(횟수)
        holder.textview_List.get(5).setText(GSConfig.changeToCommanString(detail.SlugeCount));
        holder.st_list_row_container.addView(holder.textview_List.get(5));

        // 토사(수량)
        holder.textview_List.get(6).setText(GSConfig.changeToCommanString(detail.SlugeSum));
        holder.st_list_row_container.addView(holder.textview_List.get(6));

        return convertView;

    }

    class ViewHolder
    {
        LinearLayout st_list_row_container;
        ArrayList<TextView> textview_List;
    }

    private TextView makeTextView(Context context, int gravity)
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

        return tv;

    }

}