package com.jiayaming.dubbo.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("customerInfoController")
public class CustomerInfoController {
	
	@RequestMapping(value = "onlineState", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> onlineState(HttpServletRequest request) throws Exception {
		Map<String, Object> returnMap = new HashMap<>();
		String token = request.getHeader("Authorization");
		String customerInfoStr = request.getSession().getAttribute(token).toString();
		JSONObject customerInfo = JSONObject.parseObject(customerInfoStr);
		returnMap.put("state", "successe");
		returnMap.put("code", "0");//没有错误
		returnMap.put("token", token);
    	returnMap.put("customerInfo", customerInfo);
		return returnMap;
	}
}
