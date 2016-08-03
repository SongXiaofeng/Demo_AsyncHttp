package com.example.leon.demo_asynchttp;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LoggerUtils {
    private String TAG = "sxfLoggerUtils";
    private static LoggerUtils mSleep = null;
    private static boolean mDebug = true;
    private static boolean mIsWriteToConsole = true;
    private static boolean mIsWriteToFile = true;
    private static Context mcontext;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private String mLogName = "_log.log";

    // private String LOG_PATH_SDCARD_DIR = "/sdcard/sxfTestLog/";
    private String LOG_FILE_NAME = getCurrentDate() + mLogName;
    private boolean isScaned = false;
    private File LOG_PATH_SDCARD_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/log");
    private static final int SDCARD_LOG_FILE_SAVE_DAYS = 3;

    LoggerUtils() {

        // sxf add  0723
        //	deleteLatestLog();

        if (mIsWriteToFile) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                Log.d(TAG, "SD card is not avaiable/writeable right now.");
                return;
            }

            Log.d(TAG,
                    "ExternalStorageDirectory=" + Environment.getExternalStorageDirectory().toString() + "AbsolutePath="
                            + LOG_PATH_SDCARD_DIR.getAbsolutePath() + "Name=" + LOG_PATH_SDCARD_DIR.getName());
        }

        this.setDebug(true);
        this.setDebugConsole(true);
        this.setDebugFile(true);

    }

    public void deleteLatestLog() {
        File file = LOG_PATH_SDCARD_DIR;
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            for (File logFile : allFiles) {
                String fileName = logFile.getName();
                Log.d(TAG, "filename=" + fileName);
                Log.d(TAG, "getCurrentDate()" + getCurrentDate());
                if (fileName.contains(getCurrentDate())) {
                    //	Log.d("sxf",fileName+"="+"begin deleted");
                    if (logFile.delete()) {
                        Log.d(TAG, fileName + "=" + "was deleted");
                    }
                }
            }
        }
    }
    /*private String getCurrentFormatDay(){
		SimpleDateFormat DATE_FORMAT_DATE    = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
		return TimeUtils.getCurrentTimeInString(DATE_FORMAT_DATE);
	}*/

    public void setDebug(boolean debug) {
        this.mDebug = debug;
    }

    public void setDebugConsole(boolean isConsole) {
        this.mIsWriteToConsole = isConsole;
    }

    public void setDebugFile(boolean isWrite) {
        this.mIsWriteToFile = isWrite;
    }

    public void setTag(String sTag) {
        this.TAG = sTag;
    }

    public static LoggerUtils getInstance(Context context) {
        if (null == mSleep)
            mSleep = new LoggerUtils();
        mcontext = context;
        return mSleep;
    }

    public static LoggerUtils getInstance() {
        if (null == mSleep)
            mSleep = new LoggerUtils();
        return mSleep;
    }

    public String getTAG() {
        return TAG;
    }

    public boolean canDeleteSDLog(String createDateStr) {
        boolean canDel = false;
        Calendar calendar = Calendar.getInstance();
        // delete the logs before n days
        calendar.add(Calendar.DAY_OF_MONTH, -1 * SDCARD_LOG_FILE_SAVE_DAYS);
        Date expiredDate = calendar.getTime();
        try {
            Date createDate = sdf.parse(createDateStr);
            canDel = createDate.before(expiredDate);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
            canDel = false;
        }
        return canDel;
    }

    public void deleteSDcardExpiredLog() {
        File file = LOG_PATH_SDCARD_DIR;
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            for (File logFile : allFiles) {
                String fileName = logFile.getName();
                if (mLogName.equals(fileName)) {
                    continue;
                }
                String createDateInfo = getFileNameWithoutExtension(fileName);

                if (canDeleteSDLog(createDateInfo)) {
                    logFile.delete();
                    Log.d(TAG, "delete expired log success,the log path is:" + logFile.getAbsolutePath());
                }
            }
        }
    }

    private String getFileNameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.indexOf("."));
    }

    public String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();

        if (sts == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }

            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }

            return "[" + Thread.currentThread().getName() + "(" + Thread.currentThread().getId() + "): "
                    + st.getFileName() + ":" + st.getMethodName() + ":" + st.getLineNumber() + "]";
        }

        return null;
    }

    private String createMessage(String msg) {
        String functionName = getFunctionName();
        String message = (functionName == null ? msg : (functionName + " - " + msg));
        return message;
    }

    public void i(String tag, String msg) {
        if (mDebug) {
            String message = createMessage(msg);
            Log.i(tag, message);
        }
    }

    public void v(String tag, String msg) {
        if (!mDebug)
            return;

        String message = createMessage(msg);
        if (mIsWriteToConsole) {
            Log.v(tag, message);
        }

        if (mIsWriteToFile) {
            writeToSdcard(getCurrentDateMinute() + ":" + tag + "/" + msg + "\r\n");
        }
    }

    public void v(String msg) {
        if (!mDebug)
            return;

        String message = createMessage(msg);
        if (mIsWriteToConsole) {
            Log.v(TAG, message);
        }

        if (mIsWriteToFile) {
            writeToSdcard(getCurrentDateMinute() + ":" + TAG + "/" + msg + "\r\n");
        }
    }

    public void d(String tag, String msg) {
        if (mDebug) {
            String message = createMessage(msg);
            Log.d(tag, message);
        }
    }

    public void e(String tag, String msg) {
        if (!mDebug)
            return;
        String message = createMessage(msg);

        if (mIsWriteToConsole) {
            Log.e(tag, msg);
        }

        if (mIsWriteToFile) {
            writeToSdcard(getCurrentDateMinute() + ":" + tag + "/" + msg + "\r\n");
        }
    }

    public void e(String msg) {
        if (!mDebug)
            return;
        String message = createMessage(msg);

        if (mIsWriteToConsole) {
            Log.e(TAG, msg);
        }

        if (mIsWriteToFile) {
            writeToSdcard(getCurrentDateMinute() + ":" + TAG + "/" + msg + "\r\n");
        }
    }

    public void error(String tag, Exception e) {
        if (mDebug) {
            StringBuffer sb = new StringBuffer();
            String name = getFunctionName();
            StackTraceElement[] sts = e.getStackTrace();

            if (name != null) {
                sb.append(name + " - " + e + "\r\n");
            } else {
                sb.append(e + "\r\n");
            }
            if (sts != null && sts.length > 0) {
                for (StackTraceElement st : sts) {
                    if (st != null) {
                        sb.append("[ " + st.getFileName() + ":" + st.getLineNumber() + " ]\r\n");
                    }
                }
            }
            Log.e(tag, sb.toString());
        }
    }

    public void w(String tag, String msg) {
        if (mDebug) {
            String message = createMessage(msg);
            Log.w(tag, message);
        }
    }

    public void setmDebug(boolean d) {
        mDebug = d;

    }

    public String getCurrentDateMinute() {
        String date;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = sDateFormat.format(new Date());
        return date;
    }

    public String getCurrentDate() {
        String date;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = sDateFormat.format(new Date());
        return date;
    }

    public void writeToSdcard(String context) {
        try {
            File path = LOG_PATH_SDCARD_DIR;
            File file = new File(LOG_PATH_SDCARD_DIR, LOG_FILE_NAME);
            if (!path.exists()) {
                Log.d(TAG, "Create the path:" + LOG_PATH_SDCARD_DIR);
                if (!path.mkdir()) {
                    Log.d(TAG, "Create the path failure:" + LOG_PATH_SDCARD_DIR);
                } else {
                    Log.d(TAG, "Create the path success:" + LOG_PATH_SDCARD_DIR);
                }
            } else {
                Log.d(TAG, "path is existed,path=" + path.getAbsolutePath());
            }
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    Log.e(TAG, "Create the file failure:" + LOG_FILE_NAME);
                } else {
                    Log.d(TAG, "Create the file success:" + LOG_FILE_NAME);
                }
            } else {
                Log.d(TAG, "path is existed,path=" + file.getAbsolutePath());
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(context.getBytes());
            // raf.writeBytes("\r\n");
            // raf.write(System.getProperty("line.separator").getBytes());
            raf.close();

            // sxf add 20160708
            if (!isScaned) {
                fileScan(file.getAbsolutePath());
                isScaned = true;
            }
        } catch (Exception e) {
            Log.e("TestFile", "Error on writeFilToSD.");
        }

    }

    public void fileScan(String filePath) {
		/*Uri data = Uri.parse("file://" + filePath);
		mcontext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));*/
        Uri localUri = Uri.fromFile(new File(filePath));
        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
        mcontext.sendBroadcast(localIntent);
    }
};
