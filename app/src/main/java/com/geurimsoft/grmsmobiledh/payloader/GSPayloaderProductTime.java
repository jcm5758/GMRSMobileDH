package com.geurimsoft.grmsmobiledh.payloader;

import android.util.Log;

import com.geurimsoft.grmsmobiledh.data.GSConfig;

import java.util.ArrayList;
import java.util.List;

public class GSPayloaderProductTime
{

    private String[] TimeTable;
    private String[] ProductTable;
    public int[] FontSize;

    public List<String> getProductArray()
    {

        if (this.ProductTable == null || this.ProductTable.length == 0)
        {
            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "getArray()") + "productList is null");
            return null;
        }

        List<String> result = new ArrayList<>();

        for(int i = 0; i < this.ProductTable.length; i++)
        {
            result.add( this.ProductTable[i] );
        }

        return result;

    }

    public List<String> getTimeArray()
    {

        if (this.TimeTable == null || this.TimeTable.length == 0)
        {
            Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "getArray()") + "TimeTable is null");
            return null;
        }

        List<String> result = new ArrayList<>();

        for(int i = 0; i < this.TimeTable.length; i++)
        {
            result.add( this.TimeTable[i] );
        }

        return result;

    }

}
