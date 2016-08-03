package com.example.leon.demo_asynchttp;

import android.app.Activity;
import android.content.Intent;

public class CodeNum {
	public static final String ERR_CODE = "errcode";
	public static final String ERR_MSG = "errmsg";
	public static final String REGISTER_MSG = "isRegister";
	public static final String SUBSCRIBE_MSG = "subscribe";
	
	public static final int SYS_ERR = -1;
	public static final int OK = 0;
	public static final int PARAMS_ERR = 1000;// 请求的参数格式不正确
	public static final int PARAM_VAL_ERR = 1001;// 请求参数值不正确
	public static final int OPT_OFTEN = 1002;// 请求操作过于频繁

	public static final int NOT_AUTH_ERR = 2000;// 没有授权
	public static final int USERNAME_ERR = 2001;// 用户名不正确
	public static final int PASSWORD_ERR = 2002;// 密码不正确
	public static final int NONCE_ERR = 2003;// 验证码不正确
	public static final int EMAIL_ERR = 2004;// 邮箱地址格式不正确
	public static final int MOBILE_ERR = 2005;// 手机号码格式不正确
	public static final int NO_PERMISSION = 2006;// 没有权限
	public static final int ACCOUNT_NOT_ACTIVATE = 2007;// 帐号未激活
	public static final int NOT_REG = 2008;// 帐号没有注册
	public static final int NOT_NEW_DATA = 2009;// 没有新的数据

	public static final int UID_EXIST_ERR = 3000;// Uid已存在
	public static final int EMAIL_EXIST_ERR = 3001;// 邮箱已存在
	public static final int MOBILE_EXIST_ERR = 3002;// 手机号码已存在
	public static final int RESOURCE_EXIST = 3003;// 资源已存在
	public static final int RESOURCE_NOT_EXIST = 3004;// 资源不存在
	public static final int FIND_RES_IMPERFECT = 3005;// 发现断点资源文件
	public static final int FILE_SERVER_ERR = 3006;// 文件服务器出现错误
	public static final int MSG_SERVER_ERR = 100401;// 电信短信服务问题

	public static void executeCode(Activity context, int code, Intent intent, boolean finish) {
		if (null != context) {
		}
		switch (code) {
		case OK:
			if (null != intent) {
				context.startActivity(intent);
			} else {

			}
			if (finish) {
				context.finish();
			}
			break;
		}
	}
}
