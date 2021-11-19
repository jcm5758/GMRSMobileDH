/**
 * 덤프트럭 월별 데이터
 *
 * 2021. 11. 19.
 *
 * Written by jcm5758
 */

package com.geurimsoft.grmsmobiledh.apiserver.data;

import java.util.ArrayList;

public class GSDumpMonth
{

    public String[] header;
    public ArrayList<GSDumpMonthDetail> ServiceData;
    public GSDumpMonthDetail FinalData;

    public boolean isNullOrEmptyData()
    {
        return (this.ServiceData == null || this.ServiceData.size() == 0) ? true : false;
    }

    public GSDumpMonthDetail getFirst()
    {

        if (this.ServiceData == null || this.ServiceData.size() == 0)
            return null;

        return this.ServiceData.get(0);

    }

    public GSDumpMonthDetail getLast()
    {

        if (this.ServiceData == null || this.ServiceData.size() == 0)
            return null;

        return this.ServiceData.get(this.ServiceData.size() - 1);

    }

}
