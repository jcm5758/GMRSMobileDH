package com.geurimsoft.grmsmobiledh.payloader;

import java.util.ArrayList;

/**
 * Datalist 를 사용할때 접근하는 Class 로
 */
public class VehicleDataList
{

    private ArrayList<VehicleData> list;

    public VehicleDataList()
    {
        list = new ArrayList<VehicleData>();
    }

    public VehicleData get(int ind) { return this.list.get(ind); }

    public ArrayList<VehicleData> getLists() { return this.list; }

    public void add(VehicleData data)
    {
        this.list.add(data);
    }

    public void setList(String json)
    {
        this.list = JsonParser.parsing(json);
    }

    public int size()
    {
        return this.list.size();
    }


}