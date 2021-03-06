package com.geurimsoft.grmsmobiledh.payloader;

import java.util.ArrayList;

public class GSPayloaderServiceData
{

    public String[] header;
    public int headerCount;
    public int recordCount;
    public ArrayList<GSVehicleData> record;

    public int size()
    {

        if ( record == null )
            return 0;

        return record.size();

    }

    public GSVehicleData get(int ind)
    {

        if ( record == null )
            return null;

        return record.get(ind);

    }

    public void remove(int ind)
    {

        if ( record == null )
            return;

        record.remove(ind);

    }

}
