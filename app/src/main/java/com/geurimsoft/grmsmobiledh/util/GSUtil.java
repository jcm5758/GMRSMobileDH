package com.geurimsoft.grmsmobiledh.util;

import android.util.Log;

import com.geurimsoft.grmsmobiledh.data.GSConfig;

public class GSUtil
{

    /**
     * 입력받은 문자열이 null 이나 empty인지
     * @param str 입력 문자열
     * @return result  null 이나 empty인지
     */
    public static boolean isNullOrEmpty(String str)
    {

        String functionName = "isNullOrEmpty()";

        try
        {

            if (str == null)
                return true;

            if (str.equals(""))
                return true;

            return false;

        }
        catch(Exception ex)
        {
            Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(GSUtil.class.getName(), functionName) + ex.toString());
            return true;
        }

    }

    /**
     * 연월일을 입력받어 yyyymmdd 로 만들기
     * @param iYear 연도
     * @param iMonth 월
     * @param iDay 일
     * @return String yyyymmdd
     */
    public static String makeStringFromDate(int iYear, int iMonth, int iDay)
    {

        String functionName = "makeStringFromDate()";

        try
        {

            String result = String.valueOf(iYear);

            if (iMonth < 10)
                result += "0" + iMonth;
            else
                result += iMonth;

            if (iDay < 10)
                result += "0" + iDay;
            else
                result += iDay;

            return result;

        }
        catch(Exception ex)
        {
            Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(GSUtil.class.getName(), functionName) + ex.toString());
            return null;
        }

    }

    /**
     * 연월일을 입력받어 yyyymmdd 로 만들기
     * @param iYear 연도
     * @param iMonth 월
     * @param iDay 일
     * @return int yyyymmdd
     *
     */
    public static int makeIntFromDate(int iYear, int iMonth, int iDay)
    {

        String functionName = "makeIntFromDate()";

        try
        {

            String result = makeStringFromDate(iYear, iMonth, iDay);

            if(GSUtil.isNullOrEmpty(result))
                return -1;

            return Integer.parseInt(result);

        }
        catch(Exception ex)
        {
            Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(GSUtil.class.getName(), functionName) + ex.toString());
            return -1;
        }

    }

    public static String makeDashStringFromInt(int date)
    {

        String functionName = "makeDashStringFromInt()";

        try
        {

            String sDate = Integer.toString(date);

            if(GSUtil.isNullOrEmpty(sDate))
                return "";

            if(sDate.length() != 8)
                return "";

            String result = sDate.substring(0, 4) + "-" + sDate.substring(4, 6) + "-" + sDate.substring(6);

            return result;

        }
        catch(Exception ex)
        {
            Log.e(GSConfig.APP_DEBUG, GSConfig.LOG_MSG(GSUtil.class.getName(), functionName) + ex.toString());
            return "";
        }

    }

}
