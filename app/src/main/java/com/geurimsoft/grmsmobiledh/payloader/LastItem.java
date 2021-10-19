package com.geurimsoft.grmsmobiledh.payloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.geurimsoft.grmsmobiledh.R;
import com.geurimsoft.grmsmobiledh.data.GSConfig;

/**
 * 마지막 데이터에서 처리가 되어 남은 데이터가 없거나, 마지막 데이터의 다음데이터를 열람하려는 경우 발생하는 액티비티
 * 백그라운드에서 주기적으로 서버에서 데이터를 받아와 새로 데이터가 생긴다면 해당 데이터를 띄우고, 없다면 액티비티 유지
 * 새로고침 버튼으로 사용자가 직접 데이터를 받아와 화면 전환 가능
 */
public class LastItem extends AppCompatActivity
{

    Button button_last;
    Intent intent;

    // 현재 액티비티 선언 (새로 데이터가 넘어온다면 외부에서 해당 액티비티 종료시킬 수 있도록)
    public static Activity Last_item_activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.payloader_last_item);

        intent = new Intent(this.getIntent());

        Last_item_activity = this;

        // last_Item 액티비티가 활성화 됨을 AppConfig의 last_item_activity_use로 저장 (payloader 클래스에서 해당값이 true일 경우 처리가능하도록)
        GSConfig.last_item_activity_use = true;

        button_last = findViewById(R.id.button_last);

        // 액션바 Title 수정
        getSupportActionBar().setTitle("상세 정보");

        // 액션바 뒤로가기 버튼 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * 새로고침 버튼 이벤트
         * 클릭 즉시 소켓을 통하여 데이터를 받아와 새로 생긴다면 액티비티를 종료하고 해당 데이터를 화면에 띄움
         * 새로운 데이터가 없다면 현상 유지
         */
        button_last.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                if((Payloader)Payloader.CONTEXT!=null)
                {

                    // 서버에서 데이터를 받아와 최신화
                    if (GSConfig.all_view == true)
                    {
                        Payloader.loadPayloader(1, GSConfig.product_pick_use, 0);
                    }
                    else
                    {
                        Payloader.loadPayloader(1, GSConfig.product_pick_use, 1);
                    }

                    // 새로운 데이터가 존재할 경우
                    if(GSConfig.vehicleList.size() !=0 )
                    {

                        intent = new Intent(getBaseContext(), ItemActivity.class);
                        intent.putExtra("ID",GSConfig.vehicleList.get(0).ID);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);

                        GSConfig.last_item_activity_use = false;

                        startActivity(intent);

                        finish();

                    }

                }

            }

        });

    }

    // 뒤로가기 키를 입력 할 경우 최신화 시행
    public void onBackPressed()
    {

        super.onBackPressed();

        GSConfig.last_item_activity_use = false;

        // 준비 데이터 수신
        if(GSConfig.all_view == true)
        {
            Payloader.loadPayloader(1, GSConfig.product_pick_use, 0);
        }
        // 완료 데이터 수신
        else
        {
            Payloader.loadPayloader(1, GSConfig.product_pick_use, 1);
        }

        finish();

    }

    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            // 액션바 뒤로가기 버튼
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

}