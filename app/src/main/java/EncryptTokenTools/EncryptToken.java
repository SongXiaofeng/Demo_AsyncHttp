package EncryptTokenTools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/**
 * @param
 * @return AES
 * @throws Exception
 */

public class EncryptToken {
    private static final String TAG = "EncryptToken";
    private static EncryptToken instance = null;


    public static final String KEY_PREFERENCE_FILE = "token_info";
    public static final String KEY_BASE = "mBase";
    public static final String KEY_FACTOR = "mFactor";
    public static final String KEY_REQUEST_TIMES = "mRequestTimes";
    public static final String KEY_ORIGINAL_TOKEN = "mOriginalToken";
    public static final String KEY_USER_ID = "mUserId";
    // the newest token
    public static final String KEY_PREFERENCE_TOKE = "mPreferenceToken";


    private static long mFactor;
    private static long mRequestTimes;
    private static long mBase;
    private String mUserId;
    private String mEncryptedToken;


    private Context mContext;

    private SharedPreferences mPreference;
    private SharedPreferences.Editor mEditor;


    public EncryptToken(Context context) {
        mBase = 0;
        mRequestTimes = 0;
        mFactor = 0;
        mRequestTimes = 0;


        mContext =context;
        mPreference = mContext.getSharedPreferences(KEY_PREFERENCE_FILE,
                mContext.MODE_PRIVATE);

        mEditor = mPreference.edit();
    }

    public static EncryptToken getInstance(Context context) {
        if (instance == null)
            instance = new EncryptToken(context);
        return instance;
    }

    public void init(String userID, int base) {
        this.mUserId = userID;
        this.mBase = base;
    }

    public void reset() {
        setRequestTimes(0);
        setmBase(getOriginalBase());
        setFactor(getOriginalFactor());
        setmUserId(getOriginalUserId());
    }

    public String recoveryToken() {
        String recToken = null;

        setmBase(getOriginalBase());
        setFactor(getOriginalFactor());
        setmUserId(getOriginalUserId());
        setRequestTimes(getPreferenceRequestTimes());

        recToken = getPreferenceToken();

        setmEncryptedToken(recToken);

        return recToken;
    }


    public long getFactor() {
        return mFactor;
    }

    public void setFactor(long factor) {
        this.mFactor = factor;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public long getmBase() {
        return mBase;
    }

    public void setmBase(long mBase) {
        this.mBase = mBase;
    }

    public static long getRequestTimes() {
        return mRequestTimes;
    }

    public static void setRequestTimes(long mRequestTimes) {
        EncryptToken.mRequestTimes = mRequestTimes;
    }

    public void saveOriginalToken(String token) {
        mEditor.putString(KEY_ORIGINAL_TOKEN, token).commit();
    }

    public String getOriginalToken() {
        return mPreference.getString(KEY_ORIGINAL_TOKEN, null);
    }

    public String getPreferenceToken() {
        return mPreference.getString(KEY_PREFERENCE_TOKE, null);
    }

    public void savePreferenceToken(String token) {
        mEditor.putString(KEY_PREFERENCE_TOKE, token).commit();
    }
    public long getPreferenceRequestTimes() {
        return mPreference.getLong(KEY_REQUEST_TIMES, 0);
    }

    public void savePreferenceRequestTimes(long times) {
        mEditor.putLong(KEY_REQUEST_TIMES, times).commit();
    }

    public void saveOriginalBase(long base) {
        mEditor.putLong(KEY_BASE, base).commit();
    }

    public long getOriginalBase() {
        return mPreference.getLong(KEY_BASE, 0);
    }

    public void saveOriginalUserId(String userId) {
        mEditor.putString(KEY_USER_ID, userId).commit();
    }

    public String getOriginalUserId() {
        return mPreference.getString(KEY_USER_ID, null);
    }

    public void saveOriginalFactor(long factor) {
        mEditor.putLong(KEY_FACTOR, factor).commit();
    }

    public long getOriginalFactor() {
        return mPreference.getLong(KEY_FACTOR, 0);
    }

    private long getIndex(long base) {
        return (((base >> 0x18) & 0xFF) ^ ((base >> 0x00) & 0xFF)) % 10;
    }

    private long getOperator(long base) {
        return (((base >> 0x10) & 0xFF) ^ ((base >> 0x08) & 0xFF)) % 4;
    }

    private long calculateFactor(long index, long operator) {
        long originalFactor = 0;

        if ((0 == index) && (0 == operator)) {
            originalFactor = 0x4321;
        } else {
            for (int n = 0; n < 4; n++) {
                if (0 == operator) {
                    originalFactor |= (index) << (12 - n * 4);
                } else {
                    originalFactor |= (((index + n % (operator + 1)) % 10) << (12 - n * 4));
                }
            }
        }

        return originalFactor;
    }

    private String getEncryptedToken(String userId, long base, long factor) {
        String encryptedToken = null;

        long iterFactor = base + factor + mRequestTimes * factor;
        Log.v(TAG, "requetTimes = " + mRequestTimes);
        Log.v(TAG, "iterFactor = " + iterFactor);
        String newToken = userId + "@" + iterFactor;

        try {
            encryptedToken = AES128.getInstance().encrypt(newToken);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        mRequestTimes++;

        return encryptedToken;
    }

    public String getToken() {
        String token = null;

        if ((null == mUserId) || (0 == mBase)) {
            return null;
        }

        token = getEncryptedToken(this.mUserId,
                this.getmBase(), this.mFactor);

        savePreferenceToken(token);
        savePreferenceRequestTimes(this.mRequestTimes);

        return token;
    }

    public String initToken(String userID, long base) {
        if ((null == userID) || (0 == base)) {
            return null;
        }
        String token = null;

        saveOriginalBase(base);
        saveOriginalUserId(userID);

        this.mUserId = userID;
        this.mBase = base;

        long factor = calculateFactor(getIndex(base), getOperator(base));

        this.mFactor = factor;

        saveOriginalFactor(factor);

        token = getEncryptedToken(userID, base, factor);

        saveOriginalToken(token);

        return token;
    }

    public String refreshToken(String userID, long base) throws Exception {

        if ((0 == base) || (null == userID)) {
            Log.e(TAG, "base of userId is null");
            return null;
        }

        this.mBase = base;
        this.mUserId = userID;

        long base0 = (base >> 0x18) & 0xFF;
        long base1 = (base >> 0x10) & 0xFF;
        long base2 = (base >> 0x08) & 0xFF;
        long base3 = (base >> 0x00) & 0xFF;

        long index = (base0 ^ base3) % 10;
        long operator = (base1 ^ base2) % 4;

        long originalFactor = 0;

        if ((0 == index) && (0 == operator)) {
            originalFactor = 0x4321;
        } else {
            for (int n = 0; n < 4; n++) {
                if (0 == operator) {
                    originalFactor |= (index) << (12 - n * 4);
                } else {
                    originalFactor |= (((index + n % (operator + 1)) % 10) << (12 - n * 4));
                }
            }
        }

        this.mFactor = originalFactor;

        long factor = mFactor;
        long factorBase = factor + base;
        String token = userID + "@" + factorBase;

        Log.e(TAG, "mFactor = " + mFactor);

        this.mEncryptedToken = AES128.getInstance().encrypt(token);
        Log.e(TAG, "tmOriginalToken = " + mEncryptedToken);
        if (null == mEncryptedToken) {
            Log.e(TAG, "mEncryptedToken is null;");
            return null;
        }

        return mEncryptedToken;
    }


    public String getmEncryptedToken() {
        return mEncryptedToken;
    }

    public void setmEncryptedToken(String mEncryptedToken) {
        this.mEncryptedToken = mEncryptedToken;
    }

    public int generateTokenFactor() {

        return 0;
    }

}