package com.example.leon.demo_asynchttp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private LoggerUtils myLogger;
    private Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String  pwd = MD5.getMD5Code("sxf185716538");
        login(myHandler,"m13532402402@163.com",pwd);
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
            myLogger.v("sxf", "login()--->jsonObject.toString()" + jsonObject.toString());
            //Trace.d("sxf", "login()--->jsonObject.toString()"+jsonObject.toString());

            AsyncHttpUtil.post(Constant.LOGIN, jsonObject.toString(),
                    new TextHttpResponseHandler() {

                        @Override
                        public void onFailure(int arg0,
                                              org.apache.http.Header[] arg1, String arg2,
                                              Throwable arg3) {
                            // TODO Auto-generated method stub

                            myLogger.v("sxf", "login()--->onFailure()-->" + arg2);
                            // 传递登录失败的信息
                            handler.sendEmptyMessage(Constant.MSG_LOGIN_FAILED);
                        }

                        @Override
                        public void onSuccess(int arg0,
                                              org.apache.http.Header[] arg1, String s) {
                            // TODO Auto-generated method stub
                            myLogger.v("sxf", "login()--->onSuccess()-->" + s);
                            if (!StringUtils.isEmpty(s)) {
                                try {
                                    JSONObject object = new JSONObject(s);
                                    int code = object.getInt(CodeNum.ERR_CODE);
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

    private void sendNormalMessage(Handler handler, String s, int what) {
        Message msg = new Message();
        msg.obj = s;
        msg.what = what;
        handler.sendMessage(msg);
    }
}
