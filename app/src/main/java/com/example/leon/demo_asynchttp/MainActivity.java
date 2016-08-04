package com.example.leon.demo_asynchttp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import EncryptTokenTools.AES128;
import EncryptTokenTools.EncryptToken;

public class MainActivity extends AppCompatActivity {

    private LoggerUtils myLogger;
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constant.MSG_LOGIN_SUCCESS:
                //getHistoryData(myHandler,0,0,"");
                    for (int i=0 ;i<10;i++) {
                        changePassword(myHandler, "sxf185716538", "123456");
                    }
            }

        }
    };
    private EncryptToken encryptToken;
    private String newToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        encryptToken=EncryptToken.getInstance(this);

        String pwd = MD5.getMD5Code("sxf185716538");

        login(myHandler, "m13532402402@163.com", pwd);
    }


    public void login(final Handler handler, String account, final String pwd) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userKey", account);
            jsonObject.put("password", pwd);
            String phonePlatform = "Android-" + Util.getPhoneName();
            jsonObject.put("phonePlatform", phonePlatform);
            jsonObject.put("merName", Constant.SOFTWARE_ID);
            jsonObject.put("keyType", Util.getKeyType(account));

            myLogger = LoggerUtils.getInstance(this);
            myLogger.deleteLatestLog();
            myLogger.v("sxf", "login()--->jsonObject.toString()" + jsonObject.toString() + "Constant.LOGIN" + Constant.LOGIN);
            //Trace.d("sxf", "login()--->jsonObject.toString()"+jsonObject.toString());

            AsyncHttpUtil.post(Constant.LOGIN, jsonObject.toString(),
                    new TextHttpResponseHandler() {

                        @Override
                        public void onFailure(int arg0,
                                              org.apache.http.Header[] arg1, String arg2,
                                              Throwable arg3) {
                            // TODO Auto-generated method stub

                            myLogger.v("sxf", "login()--->onFailure()-->arg2=" + arg2);
                            // 传递登录失败的信息
                            handler.sendEmptyMessage(Constant.MSG_LOGIN_FAILED);
                        }

                        @Override
                        public void onSuccess(int arg0,
                                              org.apache.http.Header[] arg1, String s) {
                            // TODO Auto-generated method stub




                            myLogger.v("sxf", "login()--->onSuccess()-->MakeSure=" + s + "arg1=" + arg1.toString());

                            if (!StringUtils.isEmpty(s)) {
                                try {
                                    JSONObject object = new JSONObject(s);
                                    int code = object.getInt(CodeNum.ERR_CODE);
                                     initToken(object);

                                    switch (code) {
                                        case CodeNum.OK:
                                            // 传递登录成功的消息
                                            sendNormalMessage(handler, s, Constant.MSG_LOGIN_SUCCESS);
                                            break;
                                        case CodeNum.ACCOUNT_NOT_ACTIVATE:
                                            // 传递账号未激活的消息
                                            handler.sendEmptyMessage(Constant.MSG_ACCOUNT_NOT_ACTIVITY);
                                            break;
                                        case CodeNum.PASSWORD_ERR:
                                            handler.sendEmptyMessage(Constant.MSG_PASSWORD_ERR);
                                            break;
                                        case CodeNum.NOT_REG:
                                            handler.sendEmptyMessage(Constant.MSG_NOT_REG);
                                            break;
                                        default:
                                            break;
                                    }
                                } catch (JSONException e) {
                                    handler.sendEmptyMessage(Constant.MSG_LOGIN_FAILED);
                                }
                            }
                        }


                    });
        } catch (Exception e) {
            // 容错处理
            handler.sendEmptyMessage(Constant.MSG_NET_ERROR);
        }
    }

    private Boolean initToken(JSONObject object) {
        String tokenUid="";
        String tokenBase="";
        try {
            String token=object.getString("token");
           // myLogger.v("sxf", "initToken-->token" + token);
            String decodeStr = AES128.getInstance().decrypt(token);//解密
            if (decodeStr == null || "".equals(decodeStr) ) {
                return false;
            }
            //myLogger.v("sxf", "getUidAandBase()--->"+"decodeStr="+decodeStr);
            //拆分比较
            tokenUid = decodeStr.substring(0, decodeStr.indexOf("@") );
            tokenBase = decodeStr.substring(decodeStr.indexOf("@")+1, decodeStr.length());
           // myLogger.v("sxf", "getUidAandBase()--->"+"tokenUid="+tokenUid+"tokenBase="+tokenBase);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(!("".equals(tokenBase)) && !("".equals(tokenUid))){
            long longTokenBase = Long.parseLong(tokenBase);
            myLogger.v("sxf", "tokenUid" + tokenUid+"--longTokenBase"+longTokenBase);
            // Log.i("sxf2", "login()--->onSuccess()-->s=" + s + "arg1=" + arg1.toString());
            encryptToken.initToken(tokenUid,longTokenBase);
        }
        return true;
    }

    private void sendNormalMessage(Handler handler, String s, int what) {
        Message msg = new Message();
        msg.obj = s;
        msg.what = what;
        handler.sendMessage(msg);
    }
    public void changePassword(final Handler handler, String oldPwd, final String newPwd) {
        //encryptToken.init(tokenUid,Integer.getInteger(tokenBase));
        newToken=encryptToken.getToken();
       // newToken=encryptToken.getToken();
        //factor = encryptToken.getFactor();
        //  myLogger.v("sxf", "newToken" + newToken+"--facetor"+factor);
        try {
            JSONObject jsonObject = new JSONObject();
            /**
             * "opt": "activate", "userKey": " tangzhulong@spark-designer.com ",
             * "keyType": "email", "code": "123456"
             */
            jsonObject.put("oldPwd",oldPwd);
            jsonObject.put("newPwd", newPwd);
            jsonObject.putOpt("token", newToken);
            AsyncHttpUtil.post(Constant.EDITPED, jsonObject.toString(), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    handler.sendEmptyMessage(Constant.MSG_CHANGE_PWD_FAILED);
                }
                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    if (!StringUtils.isEmpty(s)) {
                        try {
                            myLogger.v("changePassword-->onSuccess()", "s="+s);
                            JSONObject object = new JSONObject(s);
                            int code = object.getInt(CodeNum.ERR_CODE);
                            switch (code) {
                                case CodeNum.OK:
                                    handler.sendEmptyMessage(Constant.MSG_CHANGE_PWD_SUCCESS);
                                    break;
                                default:
                                    handler.sendEmptyMessage(Constant.MSG_CHANGE_PWD_FAILED);
                                    break;
                            }
                        } catch (Exception e) {
                            handler.sendEmptyMessage(Constant.MSG_CHANGE_PWD_FAILED);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Constant.MSG_NET_ERROR);
        }
    }
}
