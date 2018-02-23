package com.jiayaming.dubbo.web.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.jiayaming.dubbo.customer.CustomerService;
import com.jiayaming.dubbo.sdk.PatternUtil;

@Controller
@RequestMapping("loginController")
public class LoginController {
	
	@Resource
	CustomerService customerService;
	
	
	/**
	 * 将所有字母转为大写字母
	 * @param s
	 * @return
	 */
	public String conversionLetter(String s){
		int i;
		char c;
		if(!"".equals(s)&&null!=s){
			for(i=1;i<=s.length();i++){
				c=s.charAt(i-1);
				if(c>='a'&&c<='z'){
					s=s.replace(s.charAt(i-1), (char)(s.charAt(i-1)-32));
				}
			}
		}else{
			s="error";
		}
		return s;
	}
	
	/**
	 * 校验验证码是否正确
	 * @param valiCode
	 * @param valuuid
	 * @param request
	 * @return
	 */
	public boolean loginValidationCode(String valiCode,String valuuid,HttpServletRequest request) {
		try{
			String sessionValidateCode = "";
			boolean valFlag = false;
			if(request.getSession().getAttribute("sessionValidateCode"+valuuid)!=null){
				sessionValidateCode = request.getSession().getAttribute("sessionValidateCode"+valuuid).toString();
				if(!"".equals(valiCode)&&null!=valiCode){
					String cv = conversionLetter(valiCode);
					String scv = conversionLetter(sessionValidateCode);
					if(!"error".equals(cv)&&!"error".equals(scv)){
						if(cv.equals(scv)){
							valFlag = true;
						}
					}
				}
			}
			return valFlag;
		}catch (Exception e) {
			e.printStackTrace();
			
			return false;
		}finally {
			//每次校验后验证码都删除
			request.getSession().removeAttribute("sessionValidateCode"+valuuid);
		}
	}
	/**
	 * 获取验证码
	 * @param request
	 * @param valuuid
	 * @return
	 */
	@RequestMapping(value = "loginGetValiCode", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getValidationCode(HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<>();
		String valuuid = UUID.randomUUID().toString();
		try{
			Map<String, Object> map = new HashMap<String, Object>();
			map = customerService.getValidateCodePicture();
			request.getSession().setAttribute("sessionValidateCode"+valuuid, map.get("validateCode"));
			returnMap.put("state", "successe");
			returnMap.put("valuuid", valuuid);
			returnMap.put("validateCodePicture", map.get("validateCodePicture").toString());
			return returnMap;
		}catch (Exception e) {
			e.printStackTrace();
			returnMap.put("state", "fail");
			return returnMap;
		}
	}
	/**
	 * 客户登录接口
	 * @param username
	 * @param password
	 * @param valuuid
	 * @param valiCode
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> login(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(!jsonObject.containsKey("username")||jsonObject.getString("username").equals("")) {
			return returnMap;
		}
		if(!jsonObject.containsKey("password")||jsonObject.getString("password").equals("")) {
			return returnMap;
		}
		
		String username = jsonObject.getString("username");
		String password = jsonObject.getString("password");
		
		try {
			boolean restr = true;
			int errorCount = request.getSession().getAttribute("errorCount"+username)==null ? 0:(int)request.getSession().getAttribute("errorCount"+username);
			if(errorCount > 2){
				if(!jsonObject.containsKey("valuuid")||jsonObject.getString("valuuid").equals("")) {
					return returnMap;
				}
				if(!jsonObject.containsKey("valiCode")||jsonObject.getString("valiCode").equals("")) {
					return returnMap;
				}
				
				String valuuid = jsonObject.getString("valuuid");
				String valiCode = jsonObject.getString("valiCode");
				restr = loginValidationCode(valiCode,valuuid,request);
				if(!restr){
					returnMap.put("state", "failed");
					returnMap.put("code", "3");//验证码错误
					returnMap.put("message", "亲，验证码输错了，请重新输入验证码。");
					return returnMap;
				}
			}
			
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("customerLoginName", username);
			param.put("password", password);
			
			Map<String, Object> map = new HashMap<>();
		    map = customerService.validatePasswordByUserInfo(param);
			
			if(map.get("state").toString().equals("failed")) {
				errorCount++;
				request.getSession().setAttribute("errorCount"+username, errorCount);
				
				if(errorCount > 2) {
					returnMap.put("state", "failed");
					returnMap.put("code", "2");//信息验证多次错误。
			    	returnMap.put("message","亲，您又登录错误了。为了保护我们的系统下次登录需要您输入验证码。");
				}else {
					returnMap.put("state", "failed");
					returnMap.put("code", "1");//普通错误
			    	returnMap.put("message",map.get("message"));
				}
		    	return returnMap;
			}
			
			String token =UUID.randomUUID().toString();
			JSONObject customerInfo = new JSONObject();
			customerInfo.put("uuid", map.get("uuid"));
			customerInfo.put("nickName", map.get("nickName"));
			customerInfo.put("name", map.get("name"));
			customerInfo.put("sex", map.get("sex"));
			customerInfo.put("solarOrLunar", map.get("solarOrLunar"));
			customerInfo.put("birthday", map.get("birthday"));
			customerInfo.put("birthdayTime", map.get("birthdayTime"));
			customerInfo.put("adressProvince", map.get("adressProvince"));
			customerInfo.put("adressCity", map.get("adressCity"));
			customerInfo.put("adressCounty", map.get("adressCounty"));
			String customerInfoStr = customerInfo.toString();
			request.getSession().setAttribute(token, customerInfoStr);
			
			if(errorCount>0){
				request.getSession().removeAttribute("errorCount"+username);
			}
			
			returnMap.put("state", "successe");
			returnMap.put("code", "0");//没有错误
	    	returnMap.put("message", "亲，您的信息已经验证通过，请开始尽情的玩耍吧！");
	    	returnMap.put("token", token);
	    	returnMap.put("customerInfo", customerInfo);
			return returnMap;
			
		} catch (Exception e) {
			e.printStackTrace();
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
	    	returnMap.put("message", "亲，现在登录玩耍我们系统的人太多了，实在太拥挤了！您稍等一会儿再登录玩耍吧。让您等待非常的抱歉！");
			return returnMap;
		}
		
	}
	/**
	 * 游客注册接口
	 * @param jsonObject
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "registerInfo", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> registerInfo(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(!jsonObject.containsKey("username")||jsonObject.getString("username").equals("")) {
			return returnMap;
		}
		if(!jsonObject.containsKey("password")||jsonObject.getString("password").equals("")) {
			return returnMap;
		}
		if(!jsonObject.containsKey("checkPass")||jsonObject.getString("checkPass").equals("")) {
			return returnMap;
		}
		if(!jsonObject.containsKey("valuuid")||jsonObject.getString("valuuid").equals("")) {
			return returnMap;
		}
		if(!jsonObject.containsKey("valiCode")||jsonObject.getString("valiCode").equals("")) {
			return returnMap;
		}
		
		String username = jsonObject.getString("username");
		String password = jsonObject.getString("password");
		String checkPass = jsonObject.getString("checkPass");
		String valuuid = jsonObject.getString("valuuid");
		String valiCode = jsonObject.getString("valiCode");
		try {
			
			if (!PatternUtil.isEmail(username)){
				returnMap.put("state", "failed");
				returnMap.put("code", "2");
				returnMap.put("message", "亲，请输入正确的邮箱。");
				return returnMap;
			}
			
			if(!password.equals(checkPass)) {
				returnMap.put("state", "failed");
				returnMap.put("code", "2");
				returnMap.put("message", "亲，两次输入的密码不一致");
				return returnMap;
			}
			
			if(!PatternUtil.isPassword(password)) {
				returnMap.put("state", "failed");
				returnMap.put("code", "2");
				returnMap.put("message", "亲，密码不要太简单。要由6到16位的英文字符或数字组成哦。");
				return returnMap;
			}
			
			boolean restr = true;
			
			restr = loginValidationCode(valiCode,valuuid,request);
			if(!restr){
				returnMap.put("state", "failed");
				returnMap.put("code", "2");//验证码错误
				returnMap.put("message", "亲，验证码输错了，请重新输入验证码。");
				return returnMap;
			}
			
			String token =UUID.randomUUID().toString().replaceAll("-", "");
			
			Random random = new Random();
			int start = random.nextInt(token.length()-6);
			String randomStr6 = token.substring(start,start+6);
		
			JSONObject customerInfo = new JSONObject(); 
			customerInfo.put("username", username);
			customerInfo.put("password", password);
			
			request.getSession().setAttribute("registerInfo"+randomStr6, customerInfo.toString());
			
			//邮箱发送randomStr6开始
			System.out.println(randomStr6);
			//邮箱发送randomStr6结束
			
			returnMap.put("state", "successe");
			returnMap.put("code", "0");//没有错误
			String tip = "亲，我们向您的邮箱发送了一条验证码。请您半个小时之内输入我们的验证码验证您的信息。";
	    	returnMap.put("message", tip);
	    	return returnMap;
		
			
		} catch (Exception e) {
			e.printStackTrace();
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
	    	returnMap.put("message", "亲，现在注册我们系统的人实在太多了，很是拥挤！您稍等一会儿再注册吧。让您等待非常的抱歉！");
			return returnMap;
		}
		
	}
	
	@RequestMapping(value = "validRegisterInfo", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> validRegisterInfo(@RequestBody JSONObject jsonObject,HttpServletRequest request) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(!jsonObject.containsKey("validCode")||jsonObject.getString("validCode").equals("")) {
			return returnMap;
		}
		String validCode = jsonObject.getString("validCode");

		try {
			Object object = request.getSession().getAttribute("registerInfo"+validCode);
			if(object == null) {
				returnMap.put("state", "failed");
				returnMap.put("code", "1");//普通错误
		    	returnMap.put("message", "请输入正确的验证码");
				return returnMap;
			}
			String  registerInfoStr = object.toString();
			JSONObject registerInfoJson = JSONObject.parseObject(registerInfoStr);
			String username = registerInfoJson.getString("username");
			String password = registerInfoJson.getString("password");
			
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("username", username);
			requestMap.put("password", password);
			
			Map<String, Object> resposeMap = customerService.saveRegisterInfo(requestMap);
			int state = (int)resposeMap.get("state");
			
			if(state == 1) {
				returnMap.put("state", "successe");
				returnMap.put("code", "0");//没有错误
		    	returnMap.put("message", "亲，您已经注册成功。");
			}else {
				returnMap.put("state", "failed");
				returnMap.put("code", "2");//普通错误
		    	returnMap.put("message", resposeMap.get("message"));
			}
			
	    	return returnMap;

		} catch (Exception e) {
			e.printStackTrace();
			returnMap.put("state", "failed");
			returnMap.put("code", "1");//普通错误
	    	returnMap.put("message", "亲，现在注册我们系统的人实在太多了，很是拥挤！您稍等一会儿再注册吧。让您等待非常的抱歉！");
			return returnMap;
		}
		
	}
	
	@RequestMapping(value = "outLogin", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> outLogin(HttpServletRequest request) throws Exception{
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String token = request.getHeader("Authorization");
		Object customerInfo = request.getSession().getAttribute(token);
		if(customerInfo != null && token != null) {
			request.getSession().removeAttribute(token);
		}
		returnMap.put("state", "successe");
		returnMap.put("code", "0");//没有错误
		return returnMap;
	}
	
	@RequestMapping(value = "onlineState", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> onlineState(HttpServletRequest request) throws Exception {
		Map<String, Object> returnMap = new HashMap<>();
		String token = request.getHeader("Authorization");
		Object customerInfo = request.getSession().getAttribute(token);
		
		if(customerInfo != null && token != null) {
			JSONObject customerInfo1 = JSONObject.parseObject(customerInfo.toString());
			returnMap.put("state", "successe");
			returnMap.put("code", "0");//没有错误
			returnMap.put("token", token);
	    	returnMap.put("customerInfo", customerInfo1);
		}else {
			returnMap.put("state", "failed");
			returnMap.put("code", "0");//没有错误
		}
		
		return returnMap;
	}
}
