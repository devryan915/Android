package com.kc.ihaigo.util;

import android.os.Environment;

/**
 * 
 * @ClassName: Constants
 * @Description: 常量池
 * @author: ryan.wang
 * @date: 2014年6月24日 下午5:07:49
 * 
 */
public class Constants {
	public static boolean Debug = true;
	/**
	 * 支付宝支付
	 */
	//
	// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl
	// RSA密钥生成工具，生成一套RSA公私钥。
	// 这里签名时，只需要使用生成的RSA私钥。
	// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
	public static final String ALIPAY_NOTIFY_URL = "http://notify.java.jpxx.org/index.jsp";
	public static final String ALIPAY_RETURN_URL = "http://m.alipay.com";
	// 合作身份者id，以2088开头的16位纯数字
	public static final String ALIPAY_PARTNER = "2088511207854713";
	// 收款支付宝账号
	public static final String ALIPAY__SELLER = "kunchuang0430@163.com";
	// 商户私钥，自助生成
	public static final String ALIPAY_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALwtxqCoGj23vow7TwN6sH0+5NUyB3PNuqMPtBWGXnQ/DB5ezqQDv/TD5XA6XktN3XlxlqimpNytq7ykUrFNVNYN8VcIU1pJ7+cf0LRL6q1yEE/SqaHjmo2SuJk2YDA8xDq9eKiemwMw5ORuSaRzG9hdV2LoO1eGG0drF51qfL5/AgMBAAECgYAa9jwVXMCVwzPsB7tkdjm/WDmVSlvC/eEV5/QXeW8jHL1xxIe6/EaOnVrMX7CO/ZMUCXBarbXFYKTQnIK0oK3c1rDANVtw4QjZzkH183DFh2fr3quFPIqK6B2fmoko+cD0PrOXktlQINzIWsuMZUx7+cA7IkxkyobUqUitXJkoQQJBAN1DtAHtR75XycLXLkaiAOAHqhg04wQu7LGrBIk0lQCopQxO+39cLAha5NKkTGjM6G6C1ayFwMgcTlnxUdOFEBECQQDZuGiR8MfG7QsfChgA2PptyfZNQcQo+23FLP7Vm53Fyb25PLLH3tkP3lEwxwIxbyCeSiPtze6q7BMSo8v2wXWPAkEAjcaY6/cnVk6YCFGq5DVgDCy9D7+riv2qSnmDcYsQwphNEWL2gXgE+uGK53HBSBGsCUuqMF6P1WlVxwn63WbQkQJAV9U8Ynv3rHnevbPtwRHH3djXQ42fnETqoNwpJnW0LaHYp00kdtuhR/SRXpM6gETrrNAONJaajVvVyfRIiZC8YwJASa46UdEMqL1gBB/AorSaQPqmMwJorzlCTXNUZoeJd5mJiSgqZdKUmklr3XDrLQ0URwsRZPES0ccL5JI4LvpO3g==";
	// 支付宝公钥
	public static final String ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	// 未付款交易的超时时间,取值范围：1m～15d。
	// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都 在0点关闭）。
	// 该参数数值不接受小数点，如
	// 1.5h，可转换为90m。
	public static final String ALIPAY_TIMEOUT = "30m";
	/**
	 * 使用第三方登录
	 */
	/**
	 * qq登录
	 */
	public static final String APP_ID_QQ_LOGIN = "1101689112";//
	public static final String APP_ID_WEIXIN = "wx4b30c1bcce638ee7";// wx4b30c1bcce638ee7
	public static final String APP_KEY_SINA_LOGIN = "1470689039";
	public static final String APP_SINA_LOGIN_REDIRECT_URL = "http://www.ihaigo.com";
	/**
	 * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
	 * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利 选择赋予应用的功能。
	 * 
	 * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的 使用权限，高级权限需要进行申请。
	 * 
	 * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
	 * 
	 * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
	 * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
	 */
	public static final String APP_SINA_LOGIN_SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
	public static final String SERVER_URL = "http://192.168.1.3:8080/";// http://api.ihaigo.com/ihaigo/

	public static final String HOST_URL = "http://192.168.1.3:8080/";// http://api.ihaigo.com/ihaigo/
	public static final int APP_DATA_LENGTH = 5;// 定义异步加载数据的长度默认五条
	public static final int CacheTime = Integer.MAX_VALUE;// 缓存时间(秒)
	public static final String CacheDir = "ihaigo";// 缓存目录
	public static final String REC_URL = "http://192.168.1.3:8080/";
	public static final String REC_URLL = "http://192.168.1.3:8080/";
	public static final String REC_IMAGE_URL = "http://192.168.1.4:3000";
	public static final String REC_HOME = HOST_URL + "home/";// 定义首页
	public static final String REC_GOODS = REC_URL + "home/goods";// 定义首页商品列表
	public static final String REC_SEARCH_HOTS = REC_URL + "";
	/**
	 * 汇率
	 */
	public static final String REC_RATE = HOST_URL + "home/exchangeRate";
	/**
	 * 广告列表
	 */
	public static final String REC_ADS = HOST_URL + "home/ads";
	/**
	 * 商品详情
	 */
	public static final String REC_GOODS_INFO = HOST_URL + "goods/";
	/**
	 * 库存
	 */
	public static final String REC_STOCKS = REC_URL + "goods/";
	/**
	 * 购物车列表 加入购物车 修改购物车商品信息
	 */
	public static final String REC_CARTS = HOST_URL + "cart/";
	/**
	 * 代购商列表
	 */
	public static final String SHOPCAR_AGENT = REC_URLL + "agents/";
	/**
	 * 订单
	 */
	public static final String REC_ORDERS = REC_URL + "orders/";
	/**
	 * 转运订单
	 */
	public static final String REC_TRANSPORTS = REC_URL + "transports/";
	/**
	 * 海淘话题
	 */
	public static final String REC_TOPICS = REC_URL + "topics/";
	/**
	 * 配置参数
	 */
	public static final String REC_CONFIG = REC_URL + "app/";

	/**
	 * 运单详情
	 */
	public static final String REC_LOGISTICS_INFO = REC_URL + "orders/";

	/**
	 * 用户id
	 */

	public static String USER_ID = "21";

	/**
	 * 设备唯一标识码
	 */
	public static String MYUUID = "";
	/**
	 *
	 */
	public static String LOGIN_USER_NAME = null;
	/**
	 * 用户昵称
	 */
	public static String LOGIN_NICKNAME = null;
	/**
	 * 几句话介绍
	 */
	public static String LOGIN_INTRODUCE = null;
	/**
	 * 用户头像url
	 */
	public static String LOGIN_HEAD_URL = null;

	/**
	 * 
	 */
	public static final String REC_PERSONAL_LOGIN = REC_URLL + "user/";

	/**
	 * 用户头像上传
	 */
	public static final String AVATAR_URL = REC_IMAGE_URL + "/upload/avatar";
	/**
	 * 身份信息图片
	 */
	public static final String ID_CARD_URL = REC_IMAGE_URL + "/upload/id_card";
	/**
	 * 用户激活
	 */
	public static final String INSERTACTIVE_URL = REC_PERSONAL_LOGIN
			+ "insertActive";
	/**
	 * 根据用户名查询用户是否注册
	 */
	public static final String CHECKUSER_URL = REC_PERSONAL_LOGIN + "checkUser";
	/**
	 * 获取验证码
	 */
	public static final String GETMSGCODE_URL = REC_PERSONAL_LOGIN
			+ "getMsgCode";
	/**
	 * 验证验证码
	 */
	public static final String CHECKMSGCODE_URL = REC_PERSONAL_LOGIN
			+ "checkMsgCode";
	/**
	 * 用户注册
	 */
	public static final String REGUSER_URL = REC_PERSONAL_LOGIN + "regUser";
	/**
	 * 用户设置密码
	 */
	public static final String RESETPASSWORD_URL = REC_PERSONAL_LOGIN
			+ "resetPassword";
	/**
	 * 用户登录
	 */
	public static final String LOGINUSER_URL = REC_PERSONAL_LOGIN + "loginUser";
	/**
	 * 找回密码
	 */
	public static final String FORGETPWD_URL = REC_PERSONAL_LOGIN + "forgetPwd";
	/**
	 * 第三方登录
	 */
	public static final String FASTLOGIN_URL = REC_PERSONAL_LOGIN + "fastLogin";
	/**
	 * 绑定手机
	 */
	public static final String BINDINGMOBILE_URL = REC_PERSONAL_LOGIN
			+ "bindingMobile";
	/**
	 * 查询用户信息
	 */
	public static final String FINDUSER_URL = REC_PERSONAL_LOGIN + "findUser";
	/**
	 * 修改编辑用户信息
	 */
	public static final String UPDATEUSER_URL = REC_PERSONAL_LOGIN
			+ "updateUser";

	/**
	 * 修改密码
	 */
	public static final String UPDATEPASSWORD_URL = REC_PERSONAL_LOGIN
			+ "updatePassword";
	/**
	 * 用户积分
	 */
	public static final String INSERTUSERSCORE_URL = REC_PERSONAL_LOGIN
			+ "insertuserScore";

	/**
	 * 个人中心
	 */
	public static final String PER_USERCENTER = REC_URL + "usercenter/";
	/**
	 * 物流地址详情
	 */
	public static final String REC_LOGISTICS_SYSTEM = REC_URL
			+ "logistics/system";

	public static final String REC_SEARCH = REC_URL + "goods/search";
	/**
	 * 物流列表
	 */
	public static final String REC_LOGISTICS = REC_URL + "logistics/";

	/**
	 * 物流地址详情
	 */
	public static final String REC_LOGISTICS_DTAT = REC_URL + "logistics/";
	/**
	 * 个人中心 我的物流-系统生成
	 */
	public static final String REC_SYSTEM = REC_URL + "logistics/system?";
	/**
	 * 个人中心 我的物流-手动生成
	 */
	public static final String REC_MANUAL = REC_URL + "logistics/manual?";
	/**
	 * 
	 */
	public static final String REC_SHIPPING_INFO = REC_URL + "orders/";
	/**
	 * 物流中心--运单详情
	 */
	public static final String REC_SHIPPING_DATA = REC_URL + "logistics/";

	/**
	 * 物流中心--添加
	 */
	public static final String REC_SHIPPING_ADD = REC_URL + "logistics/";

	/**
	 * 物流隐藏
	 */
	public static final String REC_SHIPPING_HID = REC_URL + "logistics/";
	/**
	 * 个人中心我的收藏列表
	 */
	public static final String REC_COLLECTION = REC_URLL
			+ "usercenter/getCollection/";
	/**
	 * 个人中心我的收藏--删除
	 */
	public static final String REC_DELETE_COLLECTION = REC_URLL
			+ "usercenter/deleteCollection/";
	/**
	 * 收藏商品详情
	 */
	public static final String REC_GOODS_DATAS = REC_URLL + "goods/";
	/**
	 * 对商品评价列表
	 */
	public static final String REC_GOODS_EVALUATION = REC_URLL
			+ "evaluation/getEvaluationGoodList";
	/**
	 * 我要对商品评价
	 */
	public static final String REC_GOODS_INSERT = REC_URLL
			+ "evaluation/insertEvaluationGood";
	/**
	 * 我要对商品评价
	 */
	public static final String REC_GOODS_FINDREMINDT = REC_URLL + "message/";
	/**
	 * 库存--我的收藏
	 */
	public static final String REC_GOODS_STOCKS = REC_URLL + "goods/";
	/**
	 * 我的话题--列表
	 */
	public static final String TOPICS_URL = REC_URLL + "topics/";

	/**
	 * 我的信息
	 */
	public static final String REC_MYINFO = REC_URLL + "usercenter/";
	/**
	 * 添加收货地址
	 */
	public static final String INSERTUSERADDRESS_URL = REC_MYINFO
			+ "insertUserAddress";
	/**
	 * 得到收货地址列表
	 */
	public static final String GETUSERADDRESS_URL = REC_MYINFO
			+ "getUserAddress";
	/**
	 * 修改收货地址信息
	 */
	public static final String UPDATEUSERADDRESS_URL = REC_MYINFO
			+ "updateUserAddress";
	/**
	 * 删除收货地址信息
	 */
	public static final String DELETEUSERADDRESS_URL = REC_MYINFO
			+ "deleteUserAddress";
	/**
	 * 添加用户身份证信息
	 */
	public static final String INSERTUSERCARD_URL = REC_MYINFO
			+ "insertUserCard";
	/**
	 * 得到身份证信息列表
	 */
	public static final String GETUSERCARD_URL = REC_MYINFO + "getUserCard";
	/**
	 * 修改身份证 信息
	 */
	public static final String UPDATEUSERCARD_URL = REC_MYINFO
			+ "updateUserCard";
	/**
	 * 删除身份证信息
	 */
	public static final String DELETEUSERCARD_URL = REC_MYINFO
			+ "deleteUserCard";

	/**
	 * 账单记录
	 */
	public static final String BILLING_URL = REC_URLL + "billing/";

	/**
	 * 修改交易密码
	 */
	public static final String updatePassword_url = REC_PERSONAL_LOGIN
			+ "updatePassword";
	/**
	 * 系统相关
	 */
	public static final String VERSION_URL = REC_URLL + "version/";
	/**
	 * 消息推送
	 */
	public static final String MESSAGE_URL = REC_URLL + "message/";

	/**
	 * 创建头像图片文件
	 */
	public static final String HEAD_URL = Environment
			.getExternalStorageDirectory() + "/" + CacheDir + "/avatarPic";
	/**
	 * 定义上传图片超时时间(s)
	 */
	public static final int UPLOAD_IMAGE_TIMEOUT = 30 * 1000;
	/**
	 * 话题
	 */
	public static final String TOPIC_URL = REC_URLL + "topics/";
	/**
	 * 搜索
	 */
	public static final String TAG_SEARCH = REC_URLL + "goods/search";
	/**
	 * 设置商品预警(商品历史价格)getGoodHistorical
	 */
	public static final String REC_GOODS_HISTORICAL = REC_URLL + "message/";
	/**
	 * 设置商品预警(商品历史价格)getGoodHistorical
	 */
	public static final String REC_GOODS_HISTORICAL_PASE = "1";
	/**
	 * 最大值
	 */
	public static final int MAX_VALUE = Integer.MAX_VALUE;
	/**
	 * 收藏商品数量，以及用户是否收藏 服务名 /usercenter方法：getCount
	 */
	public static final String GET_COUNT = REC_URLL + "usercenter/getCount";
	/**
	 * 取消收藏
	 */
	public static final String GET_COLLECTION = REC_URLL
			+ "usercenter/cancelCollection";
	/**
	 * 添加收藏商品insertCollection
	 */
	public static final String GET_IN_COLLECTION = REC_URLL
			+ "usercenter/insertCollection";
	/**
	 * 登入登出Action
	 */
	public static final String LOGON_LOGOUT_ACTION = "com.kc.ihaigo.logonlogout.action";
	/**
	 * 登录状态，广播时用来标识登录或登出登入true
	 */
	public static final String LOG_STATUS = "log_status";

	/**
	 * "http://192.168.1.4:8080/app/logistics" 物流公司
	 */
	public static final String SHI_APP = REC_URL + "app/logistics";
	/**
	 * "http://192.168.1.4:8080/app/logistics" 转运公司
	 */
	public static final String TRANS_APP = REC_URL + "app/transport";

	/**
	 * 消息推送，服务标志
	 */
	public static final String MESSAGE_ACTION = "com.kc.ihaigo.message.service";
	/**
	 * 手动添加 修改
	 */
	public static final String WAY_SELECT = REC_URL + "logistics/";
	/**
	 * 转运商评价
	 */
	public static final String TRANS_INFO = REC_URL
			+ "evaluation/getEvaluationTransport";
	public static final String TRANS_COMMENT_INFO = REC_URL
			+ "transports/company/1";

}
