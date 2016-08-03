package com.example.leon.demo_asynchttp;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.apache.http.HttpEntity;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

/**
 * Created by leon on 2016/7/29.
 */
public class AsyncHttpUtil {

    private static final String TAG = "AsyncHttpUtil";
    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        client.setTimeout(10000); // 设置链接超时，如果不设置，默认为10s
    }

    public static RequestHandle post(String urlString, String body,
                                     AsyncHttpResponseHandler res) throws UnsupportedEncodingException // url里面带参数
    {
        HttpEntity entity = new StringEntity(body, "UTF-8");
        //Trace.e(TAG, "post：" + urlString + "?" + body);
        RequestHandle rqhandle = client.post(null, urlString, entity, null, res);
        return rqhandle;
    }

}
