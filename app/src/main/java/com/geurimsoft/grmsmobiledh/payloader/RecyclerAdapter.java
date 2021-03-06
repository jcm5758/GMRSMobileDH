package com.geurimsoft.grmsmobiledh.payloader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.data.GSConfig;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>
{

    public Context context;
    public Intent intent;
    public ItemViewHolder viewHolder;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payloader_item, parent, false);

        viewHolder = new ItemViewHolder(view);

        return viewHolder;

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

        private TextView tvVehicleNum;
        private TextView tvProduct;
        private TextView tvUnit;
        private TextView tvUnit2;
        private TextView tvCustomer;
        private TextView tvLogisticCompany;
        private TextView tvServiceTime;

        private GSVehicleData data;

        ItemViewHolder(View itemView)
        {

            super(itemView);

            linearitem = itemView.findViewById(R.id.linearitem);

            tvVehicleNum = itemView.findViewById(R.id.tvVehicleNum);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvLogisticCompany = itemView.findViewById(R.id.tvLogisticCompany);
            tvServiceTime = itemView.findViewById(R.id.tvServiceTime);

            tvVehicleNum.setTextSize(Dimension.DP, GSConfig.FontSizeVehicle);
            tvProduct.setTextSize(Dimension.DP, GSConfig.FontSizeProduct);
            tvUnit.setTextSize(Dimension.DP, GSConfig.FontSizeUnit);
            tvUnit.setTextSize(Dimension.DP, GSConfig.FontSizeUnit);
            tvCustomer.setTextSize(Dimension.DP, GSConfig.FontSizeUnit);
            tvLogisticCompany.setTextSize(Dimension.DP, GSConfig.FontSizeUnit);
            tvServiceTime.setTextSize(Dimension.DP, GSConfig.FontSizeUnit);

        }

        public void onBind(GSVehicleData data)
        {

            this.data = data;

            String content_text = data.getText();

            tvVehicleNum.setText( data.VehicleNum );
            tvProduct.setText( data.Product );
            tvUnit.setText( String.valueOf(data.Unit) );
            tvCustomer.setText( data.CustomerName + "(" + data.CustomerSiteName + ")" );
            tvLogisticCompany.setText( data.LogisticCompany );
            tvServiceTime.setText( data.getServiceHour() );


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
