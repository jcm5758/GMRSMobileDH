package com.geurimsoft.grmsmobiledh.apiserver.data;

import android.util.Log;

import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ServicePredict implements Serializable
{

    @SerializedName("ID")
    public int ID;

    @SerializedName("BranchID")
    public int BranchID;

    @SerializedName("ServiceType")
    public int ServiceType;

    @SerializedName("VehicleNum")
    public String VehicleNum;

    @SerializedName("CustomerName")
    public String CustomerName;

    @SerializedName("CustomerSiteName")
    public String CustomerSiteName;

    @SerializedName("Product")
    public String Product;

    @SerializedName("LogisticCompany")
    public String LogisticCompany;

    @SerializedName("Unit")
    public int Unit;

    @SerializedName("Count")
    public int Count;

    @SerializedName("ServiceDate")
    public int ServiceDate;

    public void print()
    {
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": ID : " + this.ID);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": BranchID : " + this.BranchID);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": ServiceType : " + this.ServiceType);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": VehicleNum : " + this.VehicleNum);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": CustomerName : " + this.CustomerName);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": CustomerSiteName : " + this.CustomerSiteName);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": Product : " + this.Product);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": LogisticCompany : " + this.LogisticCompany);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": Unit : " + this.Unit);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": Count : " + this.Count);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": ServiceDate : " + this.ServiceDate);

    }

}
