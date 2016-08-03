package com.example.leon.demo_asynchttp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/*import com.spark.sleep.DateUtils;
import com.tcl.wristband.R;
import com.tcl.wristband.activity.SplashScreen;*/

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;

public class Util {
	private static long lastClickTime;

	public static int compareVersion(String version1, String version2) {
		int val1, val2, ret = 0, index = 0;
		if (version1.equals(version2)) {
			return ret;
		}

		version1 = version1.replaceAll("[a-zA-Z]+", "");
		version2 = version2.replaceAll("[a-zA-Z]+", "");
		String[] version1Array = version1.split("\\.");
		String[] version2Array = version2.split("\\.");
		int minLen = Math.min(version1Array.length, version2Array.length);

		for (index = 0; index < minLen; index++) {
			val1 = Integer.parseInt(version1Array[index]);
			val2 = Integer.parseInt(version2Array[index]);
			if (val1 != val2) {
				if (val1 > val2) {
					ret = 1;
				} else {
					ret = -1;
				}
				return ret;
			}
		}

		if (version1Array.length == version2Array.length) {
			return ret;
		} else {
			if (version1Array.length > version2Array.length) {
				for (index = minLen; index < version1Array.length; index++) {
					val1 = Integer.parseInt(version1Array[index]);
					if (val1 != 0) {
						ret = 1;
						return ret;
					}
				}
				return ret;
			} else {
				for (index = minLen; index < version2Array.length; index++) {
					val2 = Integer.parseInt(version2Array[index]);
					if (val2 != 0) {
						ret = -1;
						return ret;
					}
				}
				return ret;
			}
		}
	}

	/**
	 * @Description: 时间转string
	 * @param date
	 *            时间
	 * @param format
	 *            时间格式
	 * @return String
	 * @date Dec 4, 2009 10:06:16 AM
	 */
	public static String DateToString(Date date, String format) {

		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
		return formatter.format(date);
	}

	public static String DateToString(Date date) {
		String dateStr = new SimpleDateFormat(Constant.FORMAT_Y_M_D, Locale.getDefault()).format(date);
		return dateStr;
	}

	public static Date StringToDate(String dateStr) {
		SimpleDateFormat df = new SimpleDateFormat(Constant.FORMAT_Y_M_D, Locale.getDefault());
		try {
			return df.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String DateToString1(Date date) {
		String dateStr = new SimpleDateFormat(Constant.FORMAT_D, Locale.getDefault()).format(date);
		return dateStr;
	}

	@SuppressWarnings({ "static-access", "deprecation" })
	public static boolean isTopActivity(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = am.getRunningTasks(1);
		String currentTop = null;
		if (null != tasksInfo && tasksInfo.size() > 0) {
			currentTop = tasksInfo.get(0).topActivity.getPackageName();
		}
		return (null == currentTop) ? false : currentTop.equals(context.getPackageName());
	}

	/**
	 * 判断某个界面是否在前台
	 * 
	 * @param context
	 * @param className
	 *            某个界面名称
	 */
	@SuppressWarnings("deprecation")
	public static boolean isForeground(Context context, String className) {
		if (context == null || StringUtils.isEmpty(className)) {
			return false;
		}

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			if (className.equals(cpn.getClassName())) {
				return true;
			}
		}

		return false;
	}

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 2000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	// 判断手机格式是否正确
	public static boolean isPhone(String mobiles) {
		if (null == mobiles) {
			return false;
		}
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[^4,\\D]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	// 判断email格式是否正确
	public static boolean isEmail(String email) {
		if (null == email) {
			return false;
		}
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static String getKeyType(String accout) {
		if (isPhone(accout)) {
			return Constant.USER_TYPE_MOBILE;
		}
		if (isEmail(accout)) {
			return Constant.USER_TYPE_EMAIL;
		}
		return Constant.USER_TYPE_PLATFORM;
	}

    public static String getFormatTime(int d1, int d2,int type){
    	if(type==Constant.DURATION_TIME_FORMAT){
    		if(d1 == 0){
    			return d2+"min";
    		}
			if(d2 == 0){
				return String.format(Locale.getDefault(),"%d"+"h", d1);
			}    		
			return String.format(Locale.getDefault(),"%d"+"h"+"%d"+"min", d1, d2);
    	}
    	return String.format(Locale.getDefault(),"%02d"+":"+"%02d", d1, d2);
    }


//	public static void killAllProcess(Context mContext) {
//		if (Constant.LOG_DEBUG){
//			mContext.stopService(new Intent(mContext, LogService.class));
//		}
////		android.os.Process.killProcess(android.os.Process.myPid());
//		// System.exit(0);
//	}

	public static boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> myList = myAM.getRunningServices(40);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}

	public static String getPhoneName() {
		return android.os.Build.MODEL;
	}
	
	public static long  getTimeTick(int year, int month, int day){
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day,  0,  0,  0);
		return calendar.getTime().getTime();
	}
	
	/*public static int[] calAge(long oldTickTime){
		Calendar calendarNow = Calendar.getInstance();
		Calendar calendarOld = Calendar.getInstance();
		calendarOld.setTime(new Date(oldTickTime));
		int[] difference = DateUtils.getNeturalAge(calendarOld, calendarNow);
		return difference;
	}*/
	
	public static PendingIntent getDefalutIntent(Context context, int flags){
		PendingIntent pendingIntent= PendingIntent.getActivity(context, 1, new Intent(), flags);
		return pendingIntent;
	}
	
/*	@SuppressWarnings("static-access")
	public static void notification(Context context, int soc) {
		RemoteViews view_custom = new RemoteViews(context.getPackageName(), R.layout.view_custom);
		view_custom.setImageViewResource(R.id.custom_icon, R.drawable.powericon);
		view_custom.setTextViewText(R.id.tv_custom_title, context.getString(R.string.powersWarning));
		view_custom.setTextViewText(R.id.tv_custom_content, context.getString(R.string.warning_charge, soc));
		Builder mBuilder = new Builder(context);
		mBuilder.setContent(view_custom)
				.setContentIntent(getDefalutIntent(context, Notification.FLAG_AUTO_CANCEL))
				.setWhen(System.currentTimeMillis())
				.setTicker(context.getString(R.string.powersWarning))
				.setPriority(Notification.PRIORITY_DEFAULT)
				.setOngoing(false)
				.setSmallIcon(R.drawable.stat_notify_gmail);
		Notification notify = mBuilder.build();
		notify.contentView = view_custom;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, notify);
	}*/


	public static void setDefaultLocale(Context context) {
		Resources resource = context.getResources();
		Configuration config = resource.getConfiguration();
		config.locale = Locale.ENGLISH;
		resource.updateConfiguration(config, null);
	}
	
/*	*//**
     * 检查当前网络是否可用
     * 
     * @param context
     * @return
     */
    
/*    @SuppressWarnings("deprecation")
	public static boolean isNetworkAvailable(Activity activity)
    {
        Context context = activity.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0)  {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }*/
    
    
    /*public static void restartAPP(Activity contxet){
        Intent intent = new Intent(contxet.getApplicationContext(), SplashScreen.class);  
        PendingIntent restartIntent = PendingIntent.getActivity(    
        		contxet.getApplicationContext(), 0, intent,    
                Intent.FLAG_ACTIVITY_NEW_TASK);                                                 
        //退出程序                                          
        AlarmManager mgr = (AlarmManager)contxet.getSystemService(Context.ALARM_SERVICE);    
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,    
                restartIntent); // 1秒钟后重启应用   
        contxet.finishActivity(0);  
    }
    
    
    public static boolean isGuestureEable(Context context){
    	String[] mobile_name_arrays = context.getResources().getStringArray(R.array.mobile_name_arrays);
    	String productMode = android.os.Build.MODEL+"";
    	for(int i = 0; i < mobile_name_arrays.length; i++){
    		if(productMode.startsWith(mobile_name_arrays[i])){
    			return true;
    		}
    	}
    	return false;
    }*/
    
/*    public static boolean isOrangePhone(Context context){
    	String[] mobile_name_arrays = context.getResources().getStringArray(R.array.mobile_name_arrays);
    	String productMode = android.os.Build.MODEL+"";
    	for(int i = 0; i < 9; i++){
    		if(productMode.startsWith(mobile_name_arrays[i])){
    			return true;
    		}
    	}
    	return false;
    }*/
    
    
    
    public static final String bytesToHexString(byte[] bArray) {
	  StringBuffer sb = new StringBuffer(bArray.length);
	  String sTemp;
	  for (int i = 0; i < bArray.length; i++) {
	   sTemp = Integer.toHexString(0xFF & bArray[i]);
	   if (sTemp.length() < 2)
	    sb.append(0);
	   sb.append(sTemp.toUpperCase(Locale.getDefault()));
	  }
	  return sb.toString();
	}
    
    
    /** 
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的 
     * @param context 
     * @return true 表示开启 
     */  
    public static final boolean isOPen(final Context context) {  
        LocationManager locationManager   
                                 = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）  
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);  
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）  
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);  
        if (gps || network) {  
            return true;  
        }  
  
        return false;  
    } 
    
/*    public static void timeTrace(int index){
    	float t = System.currentTimeMillis()/1000.0f;
    	Trace.e("loadAPP", index +"."+t);
    }
    */
    public static int correct_distance(int step, int oldDistance){
    	if(step <= 0 || oldDistance <= 0){
    		return oldDistance;
    	}
    	
    	float minDistance =  step*0.6f;
    	float maxDistance =  step*0.9f;
    	if(oldDistance <= (int)minDistance || oldDistance >= (int)maxDistance){
    		return (int)(step*0.77231f);
    	}else{
    		return oldDistance;
    	}
    }
   
    public static int correct_calories(int step, int oldCalories){
    	if(step <= 0 || oldCalories <= 0.0f){
    		return oldCalories;
    	}
    	
    	float minCalories =  step*0.04f;
    	float maxCalories =  step*0.07f;
    	if(oldCalories <= minCalories || oldCalories >= maxCalories){
    		return (int)(step*0.058f);
    	}else{
    		return oldCalories;
    	}
    }    
}
