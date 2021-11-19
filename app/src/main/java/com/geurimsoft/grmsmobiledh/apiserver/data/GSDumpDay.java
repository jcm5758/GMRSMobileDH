/**
 * 덤프트럭 일별 데이터
 *
 * 2021. 11. 19.
 *
 * Written by jcm5758
 */

package com.geurimsoft.grmsmobiledh.apiserver.data;

import java.util.ArrayList;

public class GSDumpDay
{

    public String[] header;
    public ArrayList<GSDumpDayDetail> serviceInput;
    public ArrayList<GSDumpDayDetail> serviceOutput;
    public ArrayList<GSDumpDayDetail> serviceSluge;

    public boolean isNullOrEmptyInput()
    {
        return (this.serviceInput == null || this.serviceInput.size() == 0) ? true : false;
    }

    public boolean isNullOrEmptyOutput()
    {
        return (this.serviceOutput == null || this.serviceOutput.size() == 0) ? true : false;
    }

    public boolean isNullOrEmptySluge()
    {
        return (this.serviceSluge == null || this.serviceSluge.size() == 0) ? true : false;
    }

    public boolean isNullOrEmptyAll()
    {

        if (!isNullOrEmptyInput())
            return false;

        if (!isNullOrEmptyOutput())
            return false;

        if (!isNullOrEmptySluge())
            return false;

        return true;

    }

}
