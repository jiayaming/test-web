package com.jiayaming.dubbo.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.jiayaming.dubbo.customer.CustomerService;

@Controller
@RequestMapping("customerInfoController")
public class CustomerInfoController {
	
	@Resource
	CustomerService customerService;
	
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
	
	@RequestMapping(value = "saveCustomerInfo", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveCustomerInfo(@RequestBody JSONObject jsonObject,HttpServletRequest request) throws Exception {
		Map<String, Object> returnMap = new HashMap<>();
		
		if(!jsonObject.containsKey("name")) {
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
			returnMap.put("message", "参数不可以为空");
			return returnMap;
		}
		if(!jsonObject.containsKey("sex")) {
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
			returnMap.put("message", "参数不可以为空");
			return returnMap;
		}
		if(!jsonObject.containsKey("solarOrLunar")) {
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
			returnMap.put("message", "参数不可以为空");
			return returnMap;
		}
		if(!jsonObject.containsKey("birthday")) {
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
			returnMap.put("message", "参数不可以为空");
			return returnMap;
		}
		if(!jsonObject.containsKey("birthdayTime")) {
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
			returnMap.put("message", "参数不可以为空");
			return returnMap;
		}
		if(!jsonObject.containsKey("adressProvince")) {
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
			returnMap.put("message", "参数不可以为空");
		}
		if(!jsonObject.containsKey("adressCity")) {
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
			returnMap.put("message", "参数不可以为空");
			return returnMap;
		}
		if(!jsonObject.containsKey("adressCounty")) {
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
			returnMap.put("message", "参数不可以为空");
			return returnMap;
		}
		if(!jsonObject.containsKey("uuid")) {
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
			returnMap.put("message", "参数不可以为空");
			return returnMap;
		}
		
		String birthdayStr = jsonObject.getString("birthday");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date birthday = sdf.parse(birthdayStr);
		
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("uuid", jsonObject.getString("uuid"));
		requestMap.put("name", jsonObject.getString("name"));
		requestMap.put("sex", jsonObject.getString("sex"));
		requestMap.put("solarOrLunar", jsonObject.getString("solarOrLunar"));
		requestMap.put("birthday", birthday);
		requestMap.put("birthdayTime", jsonObject.getString("birthdayTime"));
		requestMap.put("adressProvince", jsonObject.getString("adressProvince"));
		requestMap.put("adressCity", jsonObject.getString("adressCity"));
		requestMap.put("adressCounty", jsonObject.getString("adressCounty"));
		int isSava = customerService.saveCustomerInfo(requestMap);
		if(isSava == 1) {
			//更新缓存
			String token = request.getHeader("Authorization");
			Object customerInfo = request.getSession().getAttribute(token);
			JSONObject customerInfo1 = JSONObject.parseObject(customerInfo.toString());
			customerInfo1.put("name", jsonObject.getString("name"));
			customerInfo1.put("sex", jsonObject.getString("sex"));
			customerInfo1.put("solarOrLunar", jsonObject.getString("solarOrLunar"));
			customerInfo1.put("birthday", birthday);
			customerInfo1.put("birthdayTime", jsonObject.getString("birthdayTime"));
			customerInfo1.put("adressProvince", jsonObject.getString("adressProvince"));
			customerInfo1.put("adressCity", jsonObject.getString("adressCity"));
			customerInfo1.put("adressCounty", jsonObject.getString("adressCounty"));
			request.getSession().setAttribute(token, customerInfo1.toString());
			
			returnMap.put("state", "successe");
			returnMap.put("code", "0");//没有错误
	    	returnMap.put("message", "保存成功");
		}else {
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
			returnMap.put("message", "保存失败");
		}
		
		
		return returnMap;
	}
}
