/**
 * 사용자 정보 저장 객체
 *
 * 2021. 05. 28 최초 생성
 *
 *  Writtend by jcm5758
 *
 */

package com.geurimsoft.grmsmobiledh.apiserver.data;

import android.util.Log;

import com.geurimsoft.grmsmobiledh.data.GSConfig;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserInfoData implements Serializable {

    @SerializedName("ID")
    public int ID = -1;

    @SerializedName("ServiceType")
    public int ServiceType = -1;

    @SerializedName("Name")
    public String Name = "";

    @SerializedName("UserID")
    public String UserID = "";

    @SerializedName("VehicleNum")
    public String VehicleNum = "";

    @SerializedName("Company")
    public String Company = "";

    @SerializedName("Phone")
    public String Phone = "";

    @SerializedName("Mobile")
    public String Mobile = "";

    public void print()
    {
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": ID : " + this.ID);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": ServiceType : " + this.ServiceType);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": Name : " + this.Name);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": UserID : " + this.UserID);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": VehicleNum : " + this.VehicleNum);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": Company : " + this.Company);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": Phone : " + this.Phone);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(this.getClass().getName(), "print()") + ": Mobile : " + this.Mobile);
    }

}