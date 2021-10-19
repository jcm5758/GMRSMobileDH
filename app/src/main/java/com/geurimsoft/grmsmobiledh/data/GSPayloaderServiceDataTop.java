package com.geurimsoft.grmsmobiledh.data;

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

    public void add(GSPayloaderServiceDataDetail dd)
    {

        if ( data == null || data.size() == 0 )
            return;

        data.record.add(dd);

    }

    public GSPayloaderServiceDataDetail get(int ind)
    {

        if ( data == null || data.size() == 0 )
            return null;

        return data.get(ind);

    }

    public void print()
    {

        if ( data == null || data.size() == 0 )
            return;

        for(GSPayloaderServiceDataDetail dd : data.record)
        {
            dd.print();
        }

    }

}
