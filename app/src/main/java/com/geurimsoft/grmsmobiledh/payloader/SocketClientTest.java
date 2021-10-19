package com.geurimsoft.grmsmobiledh.payloader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketClientTest extends Thread
{

    private String serverIP;
    private int port;
    private String msg;
    private String encryptCode = "1234567890123456";
    private String returnString = null;
    private boolean isState = false;

    public SocketClientTest(String serverIP, int port, String msg)
    {

        this.serverIP = serverIP;
        this.port = port;
        this.msg = msg;


    }
    public String getReturnString()
    {
        return this.returnString;
    }

    public boolean isState()
    {
        return this.isState;
    }

    public void run()
    {

        this.connect();

        super.run();

    }

    private void connect()
    {

        Socket soc = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        String str;


        try
        {
            // 소켓 연결
            soc = new Socket(this.serverIP, this.port);

            // 타임 아웃 설정
            soc.setSoTimeout(10000);

            // 서버로부터의 입력 모드
            in = new BufferedReader(new InputStreamReader(soc.getInputStream(), "UTF-8"));

            // 서버로부터의 전송 모드
            out = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream(), "UTF-8"));

            out.write(AES.encrypt(msg, encryptCode) + "\n");
            out.flush();

            // 서버로부터의 입력 받아오기
            str = in.readLine();

            if (str != null)
            {
                returnString = AES.decrypt(str, encryptCode);
            }

            // 종료 메시지
            out.write("exit\n");
            out.flush();

            // 접속 끊기
            in.close();
            out.close();
            soc.close();

            isState = true;

        }

        catch(IOException ex1)
        {
            System.err.println(this.getClass().getName() + " : connect() : " + ex1.toString());
        }
        catch (Exception ex2)
        {
            System.err.println(this.getClass().getName() + " : connect() : " + ex2.toString());
        }
        finally
        {
            try
            {
                if(soc != null)
                    soc.close();

                if(in != null)
                    in.close();

                if(out != null)
                    out.close();

                interrupt();
            } catch (IOException fe)
            {

            }
        }

    }

    /**
     * @param args
     * @throws Exception
     */
    /*
    public static void main(String[] args) throws Exception
    {

        String[] messages = new String[20];

        //messages[0] = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><GEURIMSOFT><GCType>Login</GCType><GCQuery>admin,1234,APA91bF9p3peqkbJKM7vkfBfrxGtuzAGwLV_NZkYQvVHp5AUtminszqZFwas0486FwG1t31wX0SXmTMR7qIf2psOs13RnrLXSxDUfOrfi6yXNWYQsPzOWNEIKn2st7vovwgSLatBaTuD</GCQuery></GEURIMSOFT>\n";
        //messages[1] = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><GEURIMSOFT><GCType>Product_List</GCType><GCQuery>3,1</GCQuery></GEURIMSOFT>\n";

        // 전체 리스트 받아오는 message
        messages[2] = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><GEURIMSOFT><GCType>Daily_ReleaseBYProduct</GCType><GCQuery>3,1,2020,05,06,25mm</GCQuery></GEURIMSOFT>\n";

        //messages[3] = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><GEURIMSOFT><GCType>Daily_LoadProduct</GCType><GCQuery>3,1,2255</GCQuery></GEURIMSOFT>\n";

        new SocketClientTest("211.253.8.254", 8401, messages[2]);


    }*/

}
