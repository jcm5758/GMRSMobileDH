package com.geurimsoft.grmsmobiledh.payloader;

import android.util.Log;

import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.geurimsoft.grmsmobiledh.util.GSUtil;

public class GSVehicleData
{

    public int ID;
    public String VehicleNum;
    public String CustomerName;
    public String CustomerSiteName;
    public String LogisticCompany;
    public String Product;
    public double Unit;
    public String ServiceHour;

    public String getServiceHour()
    {

        if (GSUtil.isNullOrEmpty(ServiceHour) )
            return "";

//        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "getServiceHour()") + "ServiceHour : " + ServiceHour);

        return ServiceHour.substring(0, 2) + ":" + ServiceHour.substring(2, 4) + ":" + ServiceHour.substring(4);

    }

    public String getText()
    {

        if ( GSUtil.isNullOrEmpty(LogisticCompany) )
            return getTextWithoutLogistic();
        else
            return getTextWithLogistic();

    }

    public String getTextWithoutLogistic()
    {
        return CustomerName + ", " + CustomerSiteName + ", " + getServiceHour();
    }

    public String getTextWithLogistic()
    {
        return CustomerName + ", " + CustomerSiteName + ", " + LogisticCompany + ", " + getServiceHour();
    }

    public void print()
    {

        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + "----------------------------------");

        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + "ID : " + ID);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + "VehicleNum : " + VehicleNum);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + "CustomerName : " + CustomerName);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + "CustomerSiteName : " + CustomerSiteName);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + "LogisticCompany : " + LogisticCompany);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + "Product : " + Product);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + "Unit : " + Unit);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + "ServiceHour : " + getServiceHour());

        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + "----------------------------------");

    }

}
