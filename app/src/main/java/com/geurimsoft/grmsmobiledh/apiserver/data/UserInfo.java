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
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("userinfo")
    private UserInfoData userinfo;

    @SerializedName("userright")
    private ArrayList<UserRightData> userright;

    public UserInfo(String status, String message, UserInfoData userinfo, ArrayList<UserRightData> userright)
    {

        this.status = status;
        this.message = message;
        this.userinfo = userinfo;
        this.userright = userright;

    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserinfo(UserInfoData userinfo) {
        this.userinfo = userinfo;
    }

    public UserInfoData getUserinfo() {
        return userinfo;
    }

    public boolean isUserInfoNull()
    {
        return (this.userinfo == null) ? true : false;
    }

    public UserRightData getUserRightData(int ind) { return userright.get(ind); }
    public ArrayList<UserRightData> getUserright() {
        return userright;
    }
    public void setUserright(ArrayList<UserRightData> userright) {
        this.userright = userright;
    }

    public boolean isUserRightNull()
    {
        return (this.userright == null || this.userright.size() == 0) ? true : false;
    }

    public ArrayList<UserRightData> getUserRightOthers()
    {

        ArrayList<UserRightData> results = new ArrayList<UserRightData>();

        for(UserRightData ur : userright)
        {

            if (ur.branID != GSConfig.CURRENT_BRANCH.branchID)
                results.add(ur);

        }

        return results;

    }

    public UserRightData getCurrentUserRight(int branchID)
    {

        for(UserRightData ur : userright)
        {

            if (ur.branID == GSConfig.CURRENT_BRANCH.branchID)
                return ur;

        }

        return null;

    }

    public void print()
    {

        for(UserRightData ur : userright)
        {
            ur.print();
        }

    }

}
