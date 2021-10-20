package com.geurimsoft.grmsmobiledh.payloader;

public class GSPayloaderServiceDataTop
{

    public String status;
    public GSPayloaderServiceData data;

    public int size()
    {

        if ( data == null )
            return 0;

        return data.size();

    }

    public void add(GSVehicleData dd)
    {

        if ( data == null || data.size() == 0 )
            return;

        data.record.add(dd);

    }

    public GSVehicleData get(int ind)
    {

        if ( data == null || data.size() == 0 )
            return null;

        return data.get(ind);

    }

    public void remove(int ind)
    {

        if ( data == null || data.size() == 0 )
            return;

        data.remove(ind);

    }

    public void print()
    {

        if ( data == null || data.size() == 0 )
            return;

        for(GSVehicleData dd : data.record)
        {
            dd.print();
        }

    }

}
