/**
 * @Title: GameService.java
 * @Package: com.xx.xx.util
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:shanghai
 * 

 * @author Comsys-ryan.wang

 * @date 2016年3月21日 下午5:59:41

 * @version V1.0

 */

package com.kc.ihaigo.util;

import java.util.HashMap;
import java.util.Map;

import com.kc.ihaigo.util.HttpAsyncTask.HttpReqCallBack;

/**
 * @ClassName: GameService
 * @Description: TODO
 * @author: ryan.wang
 * @date: 2016年3月21日 下午5:59:41
 * 
 */

public class GameService {
	public void loadData(int param1, long param2, String param3,
			HttpReqCallBack backCall) {
		Map<String, Object> reparams = new HashMap<String, Object>();
		reparams.put("param1", param1);
		reparams.put("param2", param2);
		reparams.put("param3", param3);
		HttpUtil.postData(Constants.SERVER_URL, reparams, backCall);
	}
}
