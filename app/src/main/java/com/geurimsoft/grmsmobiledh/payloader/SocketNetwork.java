package com.geurimsoft.grmsmobiledh.payloader;

import android.util.Log;

import com.geurimsoft.grmsmobiledh.data.GSConfig;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class SocketNetwork
{

    /**
     * 품목 리스트
     * @return
     */
    public static List<String> getProductList()
    {

        String msg = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><GEURIMSOFT><GCType>PRODUCT_LIST</GCType><GCQuery>3,1</GCQuery></GEURIMSOFT>\n";

        SocketClientTest sc = new SocketClientTest(GSConfig.SOCKET_SERVER_IP, GSConfig.SOCKET_SERVER_PORT, msg);

        try
        {
            sc.start();
            sc.join();

        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        if(!sc.isState())
        {
            return null;
        }

        String mss = sc.getReturnString();

        if(mss.equals("Fail"))
        {
            return null;
        }

        List<String> resultArray = null;

        try
        {

            resultArray = new ArrayList<String>();

            JSONArray jsonArray = new JSONArray(mss);

            for(int i = 0; i < jsonArray.length(); i++)
                resultArray.add(jsonArray.getString(i));

        }
        catch(Exception ex)
        {
            Log.e(GSConfig.APP_DEBUG, ex.toString());
        }

        return resultArray;

    }

    /***
     *
     * @return
     */
    public static String getDatalist ()
    {

        String msg = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><GEURIMSOFT><GCType>Daily_ReleaseBYProduct</GCType><GCQuery>3,1," +
                GSConfig.date + GSConfig.product_pick_use + "</GCQuery></GEURIMSOFT>\n";

        SocketClientTest sc = new SocketClientTest(GSConfig.SOCKET_SERVER_IP,GSConfig.SOCKET_SERVER_PORT, msg);

        try
        {
            sc.start();
            sc.join();

        }

        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        if(!sc.isState())
        {
            return null;
        }

        String mss = sc.getReturnString();

        if(mss.equals("Fail"))
        {
            return null;
        }

        return mss;
    }

    /**
     * 완료된 데이터를 서버에 요청하는 통신
     * @return
     */
    public static String getDatalist_check ()
    {

        String msg = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><GEURIMSOFT><GCType>Daily_ReleaseBYProductLoaded</GCType><GCQuery>3,1," +
                GSConfig.date + GSConfig.product_pick_use + "</GCQuery></GEURIMSOFT>\n";

        SocketClientTest sc = new SocketClientTest(GSConfig.SOCKET_SERVER_IP,GSConfig.SOCKET_SERVER_PORT, msg);

        try
        {
            sc.start();
            sc.join();

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if(!sc.isState())
        {
            return null;
        }

        String mss = sc.getReturnString();

        if(mss.equals("Fail"))
        {
            return null;
        }

        return mss;
    }

    /**
     * 완료 처리
     * @param id
     * @return
     */
    public static String getAccept (String id)
    {

        // 완료를 누른 Data의 id값으로 처리 Query를 보낸다.
        String msg = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><GEURIMSOFT><GCType>Daily_LoadProduct</GCType><GCQuery>3,1,"+id+"</GCQuery></GEURIMSOFT>\n";

        SocketClientTest sc = new SocketClientTest(GSConfig.SOCKET_SERVER_IP,GSConfig.SOCKET_SERVER_PORT, msg);

        try
        {
            sc.start();
            sc.join();

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if(!sc.isState())
        {
            return null;
        }

        String mss = sc.getReturnString();

        if(mss.equals("Fail"))
        {
            return null;
        }

        return mss;
    }

    /**
     * 사용자가 입력한 ID와 PW 값을 소켓을 통하여 서버와 통신
     * @return 로그인 성공시, 해당 데이터에 관한 정보를 String형식으로 return / 실패시 null 값 return
     */
    public static String Login ()
    {

        // 완료를 누른 Data의 id, pw 값으로 처리 Query를 보낸다.
        String msg = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><GEURIMSOFT><GCType>Login</GCType><GCQuery>"+GSConfig.ID+","+GSConfig.PW+",APA91bF9p3peqkbJKM7vkfBfrxGtuzAGwLV_NZkYQvVHp5AUtminszqZFwas0486FwG1t31wX0SXmTMR7qIf2psOs13RnrLXSxDUfOrfi6yXNWYQsPzOWNEIKn2st7vovwgSLatBaTuD</GCQuery></GEURIMSOFT>\n";

        SocketClientTest sc = new SocketClientTest(GSConfig.SOCKET_SERVER_IP,GSConfig.SOCKET_SERVER_PORT, msg);

        try
        {
            sc.start();
            sc.join();

        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        if(!sc.isState())
        {
            return null;
        }

        String mss = sc.getReturnString();


        if(mss.equals("Fail"))
        {
            return null;
        }

        return mss;

    }

}