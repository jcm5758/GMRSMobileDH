package com.geurimsoft.grmsmobiledh.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.geurimsoft.grmsmobiledh.apiserver.data.UserInfo;
import com.geurimsoft.grmsmobiledh.payloader.GSPayloaderServiceDataTop;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GSConfig
{

    // 디버깅용 태그
    public static final String APP_DEBUG = "Geurimsoft";

    public static final String WEB_SERVER_ADDR = "http://211.253.8.254/dh/app_version.txt";

    public static final String SERVER_ADDR = "211.253.8.254";

    // API 서버 포트
    public static final int API_SERVER_PORT = 8404;

    // API 서버 주소
    public static final String API_SERVER_ADDR = "http://" + GSConfig.SERVER_ADDR + ":" + GSConfig.API_SERVER_PORT + "/API";

    // 현재 선택한 지점
    public static GSBranch CURRENT_BRANCH = null;

    // 현재 사용자
    public static UserInfo CURRENT_USER = null;

    // Acitivity 리스트 : 지점 수정할 때 수정해야 함
    public static Class[] Activity_LIST = new Class[]{ com.geurimsoft.grmsmobiledh.view.fragments.ActivityMain.class, com.geurimsoft.grmsmobiledh.payloader.Payloader.class };

    public static String LOG_MSG(String className, String funcName)
    {
        return className + "." + funcName + " : ";
    }

    public static ArrayList<Activity> activities = new ArrayList<Activity>();

    public static Context context;

    // Global preference
    public static SharedPreferences gPreference;

    // 현재의 연월일
    public static int DAY_STATS_YEAR = 0;
    public static int DAY_STATS_MONTH = 0;
    public static int DAY_STATS_DAY = 0;

    // 통계에서 날짜 변경 시 년, 월 제한 (2020-05-01 추가)
    public static int LIMIT_YEAR = 2020;
    public static int LIMIT_MONTH = 5;

    public static double moneyDivideNum = 1000;

    public static int STATE_AMOUNT = 0;
    public static int STATE_PRICE = 1;

    public static String[] MODE_NAMES = new String[]{"입고", "출고", "토사", "외부(입고)", "외부(출고)"};

    public static int MODE_STOCK = 0;
    public static int MODE_RELEASE = 1;
    public static int MODE_PETOSA = 2;
    public static int MODE_OUTSIDE_STOCK = 3;
    public static int MODE_OUTSIDE_RELEASE = 4;

    public static int USER_RIGHT_SIZE = 102;

    public static String changeToCommanString(double value)
    {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(value);
    }

    public static String changeToCommanStringWOPoint(double value)
    {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(value);
    }

    //-------------------------------------------------------------
    // Payloader 관련
    //-------------------------------------------------------------

    // 현재 사용하고 있는 서버의 IP와 PORT 값
    public static final String SOCKET_SERVER_IP = "211.253.8.254";
    public static int SOCKET_SERVER_PORT = 8403;

    // 현재 사용자의 setting 을 저장하는데 필요한 SharedPreferences 선언
    // 로그인시 로그인 ID,PW와 해당 ID에 맞는 product_pick_use 값을 저장 및 해제
//    public static SharedPreferences setting;
//    public static SharedPreferences.Editor editor;

    // 현재 사용자가 입력한 ID와 PW값
    public static String ID;
    public static String PW;

    public static GSPayloaderServiceDataTop vehicleList;

    // Last_Item의 액티비티가 활성화 되어 있는지 나타내는 변수
    public static boolean last_item_activity_use = false;

    // 준비데이터 / 완료데이터 중 선택 된 데이터를 나타내는 변수
    // true = 준비데이터 / false = 완료데이터
    public static boolean all_view = true;

    // 1열로 데이터를 볼지, 2열로 데이터를 볼지 선택을 나타내는 변수
    // true = 1열 / false = 2열
    public static boolean list_view = true;

    // 사용자가 선택한 품목 및 새로고침 시간을 나타내는 변수
    // 초기값이 없이 나타나 적용하지 않는다면 데이터가 처음에 나타나지 않음
    public static String gProduct = "전체";

    // 초기값으로 매 1분 데이터를 최신화 할 수 있도록 설정 (이후 사용자가 변경 가능)
    public static int RefreshTime = 60000;

    // 페이로더 폰트 사이즈
    public static int FontSizeVehicle = 50;
    public static int FontSizeProduct = 50;
    public static int FontSizeUnit = 50;
    public static int FontSizeCustomer = 50;
    public static int FontSizeLogisticCompany = 50;
    public static int FontSizeServiceTime = 50;

    public static int FontSizeDetailVehicle = 80;
    public static int FontSizeDetailProduct = 80;
    public static int FontSizeDetailUnit = 80;
    public static int FontSizeDetailCustomer = 80;
    public static int FontSizeDetailLogisticCompany = 80;
    public static int FontSizeDetailServiceTime = 80;
    public static int FontSizeDetailButton = 80;

    // 현재 날짜에 관한 정의 및 형식 선언
    public static String getCurrentDate()
    {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        return format.format(date);

    }

}
