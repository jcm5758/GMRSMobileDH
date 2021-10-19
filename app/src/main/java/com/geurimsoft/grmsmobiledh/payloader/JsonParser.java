package com.geurimsoft.grmsmobiledh.payloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 서버로부터 Json으로 정보를 변형(JsonParsing) 하여 dataList 라는 이름의 변수로 저장
 * dataList는 ArrayList<Data> 로 저장되며 id, vehicleNum, customerName 등 8개의 분야로 각각 저장되어
 * 서버로부터 받아온 정보를 ArrayList<Data> 의 형식으로 저장하여 Return
 */
public class JsonParser
{

    public static ArrayList<VehicleData> parsing(String json)
    {

        ArrayList<VehicleData> dataList = new ArrayList<>();

        try
        {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray listArray = jsonObject.getJSONArray("list");

            for(int i=0 ; i<listArray.length() ; i++)
            {
                JSONObject listObject = listArray.getJSONObject(i);

                VehicleData data = new VehicleData();

                data.ID = listObject.getString("ID");
                data.VehicleNum = listObject.getString("VehicleNum");
                data.CustomerName = listObject.getString("CustomerName");
                data.CustomerSiteName = listObject.getString("CustomerSiteName");
                data.LogisticCompany = listObject.getString("LogisticCompany");
                data.Product = listObject.getString("Product");
                data.Unit = listObject.getString("Unit");
                data.ServiceHour = listObject.getString("ServiceHour");

                dataList.add(data);

            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return dataList;

    }

}