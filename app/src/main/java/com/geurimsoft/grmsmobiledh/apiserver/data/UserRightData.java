/**
 * 사용자 권한 정보 저장 객체
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

public class UserRightData implements Serializable {

    // 모바일 로그인 uRight.UR65 AS ur01
    @SerializedName("ur01")
    public int ur01;

    // 일일 입출고 조회 uRight.UR66 AS ur02
    @SerializedName("ur02")
    public int ur02;

    // 일일 입출고 조회(금액) uRight.UR67 AS ur03
    @SerializedName("ur03")
    public int ur03;

    // 월 입출고 조회 uRight.UR86 AS ur04
    @SerializedName("ur04")
    public int ur04;

    // 월 입출고 조회(금액) uRight.UR87 AS ur05
    @SerializedName("ur05")
    public int ur05;

    // 월 거래처 조회 uRight.UR90 AS ur06
    @SerializedName("ur06")
    public int ur06;

    // 월 거래처 조회(금액) uRight.UR69 AS ur07
    @SerializedName("ur07")
    public int ur07;

    // 연 입출고 조회 uRight.UR88 AS ur08
    @SerializedName("ur08")
    public int ur08;

    // 연 입출고 조회(금액) uRight.UR89 AS ur09
    @SerializedName("ur09")
    public int ur09;

    // 연 거래처 조회 uRight.UR91 AS ur10
    @SerializedName("ur10")
    public int ur10;

    // 연 거래처 조회(금액) uRight.UR70 AS ur11
    @SerializedName("ur11")
    public int ur11;

    // 페이로더 상차 uRight.UR93 AS ur12
    @SerializedName("ur12")
    public int ur12;

    @SerializedName("branID")
    public int branID;

    @SerializedName("branName")
    public String branName;

    @SerializedName("branShortName")
    public String branShortName;

    @SerializedName("payloader")
    public int payloader;

    public void print()
    {
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG("UserRigthData", "print()") + " branchID : " + branID + ", branName : " + branName);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG("UserRigthData", "print()") + " ur01 : " + ur01 + ", ur02 : " + ur02  + ", ur03 : " + ur03 + ", ur04 : " + ur04  + ", ur05 : " + ur05  + ", ur06 : " + ur06);
        Log.d(GSConfig.APP_DEBUG, GSConfig.LOG_MSG("UserRigthData", "print()") + " ur07 : " + ur07 + ", ur08 : " + ur08  + ", ur09 : " + ur09 + ", ur10 : " + ur10  + ", ur11 : " + ur11  + ", ur12 : " + ur12);
    }

}
