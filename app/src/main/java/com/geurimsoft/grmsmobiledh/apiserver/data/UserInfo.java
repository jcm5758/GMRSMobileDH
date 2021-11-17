/**
 * 사용자 정보
 * 로그인시 사용자 정보와 권한 정보를 저장
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
import java.util.ArrayList;

public class UserInfo implements Serializable
{

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("userinfo")
    public UserInfoData userinfo;

    @SerializedName("userright")
    public ArrayList<UserRightData> userright;

    @SerializedName("servicepredict")
    public ArrayList<ServicePredict> servicepredict;

    public boolean isUserInfoNull()
    {
        return (this.userinfo == null) ? true : false;
    }
    public boolean isUserRightNull(){ return (this.userright == null || this.userright.size() == 0) ? true : false; }
    public boolean isServicePredictNull()
    {
        return (this.servicepredict == null) ? true : false;
    }

    public ArrayList<UserRightData> getUserRightOthers()
    {

        ArrayList<UserRightData> results = new ArrayList<UserRightData>();

        for(UserRightData ur : this.userright)
        {

            if (ur.branID != GSConfig.CURRENT_BRANCH.branchID)
                results.add(ur);

        }

        return results;

    }

    public UserRightData getCurrentUserRight(int branchID)
    {

        for(UserRightData ur : this.userright)
        {

            if (ur.branID == GSConfig.CURRENT_BRANCH.branchID)
                return ur;

        }

        return null;

    }

    public ServicePredict getSerivcePredict(int ind)
    {

        if (this.servicepredict == null || this.servicepredict.size() == 0)
            return null;

        return this.servicepredict.get(0);

    }

    public void printUserRight()
    {

        for(UserRightData ur : this.userright)
        {
            ur.print();
        }

    }

    public void printServicePredict()
    {

        for(ServicePredict ur : this.servicepredict)
        {
            ur.print();
        }

    }

}
