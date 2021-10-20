package com.geurimsoft.grmsmobiledh.payloader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.data.GSConfig;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>
{

    public Context context;
    public Intent intent;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payloader_item, parent, false);

        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position)
    {
        holder.onBind(GSConfig.vehicleList.get(position));
    }

    /**
     * 데이터 수
     * @return
     */
    @Override
    public int getItemCount()
    {

        if ( GSConfig.vehicleList == null)
            return 0;

        return GSConfig.vehicleList.size();

    }

    public void addItem(GSVehicleData data)
    {
        GSConfig.vehicleList.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public class ItemViewHolder extends RecyclerView.ViewHolder
    {

        private LinearLayout linearitem;

        private TextView textView_vehicleNum;
        private TextView textView_product;
        private TextView textView_content;
        private TextView textView_unit;
        private GSVehicleData data;

        ItemViewHolder(View itemView)
        {

            super(itemView);

            linearitem = itemView.findViewById(R.id.linearitem);

            textView_vehicleNum = itemView.findViewById(R.id.textView_vehicleNum);
            textView_product = itemView.findViewById(R.id.textView_product);
            textView_unit = itemView.findViewById(R.id.textView_unit);
            textView_content = itemView.findViewById(R.id.textView_content);

        }

        public void onBind(GSVehicleData data)
        {

            this.data = data;

            String content_text = data.getText();

            textView_vehicleNum.setText(data.VehicleNum);
            textView_product.setText(data.Product);
            textView_content.setText(content_text);
            textView_unit.setText( String.valueOf(data.Unit) );

            itemView.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    // 해당 데이터에 관한 창 띄우기
                    intent = new Intent(context, ItemActivity.class);
                    intent.putExtra("ID", GSConfig.vehicleList.get(getAdapterPosition()).ID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);

                    context.startActivity(intent);

                }

            });

        }

    }

}
