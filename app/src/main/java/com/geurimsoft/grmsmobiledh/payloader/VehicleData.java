package com.geurimsoft.grmsmobiledh.payloader;

import java.io.Serializable;

/**
 * 어플에서 사용자가 원하는 정보를 서버에서 받아와 저장하는 Class
 */
public class VehicleData implements Serializable
{

    public String ID;
    public String VehicleNum;
    public String CustomerName;
    public String CustomerSiteName;
    public String LogisticCompany;
    public String Product;
    public String Unit;
    public String ServiceHour;


}