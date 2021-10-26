package com.geurimsoft.grmsmobiledh.data;

import android.util.Log;

public class GSBranch
{

    public int branchID;
    public String branchName;
    public String branchShortName;

    public GSBranch(int branchID, String branchName, String branchShortName)
    {
        this.branchID = branchID;
        this.branchName = branchName;
        this.branchShortName = branchShortName;
    }

    public void print()
    {
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG("UserRigthData", "print()") + " branchID : " + branchID + ", branchName : " + branchName + ", branchShortName : " + branchShortName);
    }

}
