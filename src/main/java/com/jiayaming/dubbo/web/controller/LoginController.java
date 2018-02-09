package com.jiayaming.dubbo.web.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.jiayaming.dubbo.customer.CustomerService;

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
	 * @param httpSession
	 * @return
	 */
	public boolean loginValidationCode(String valiCode,String valuuid,HttpServletRequest request,HttpSession httpSession) {
		try{
			String sessionValidateCode = "";
			boolean valFlag = false;
			if(httpSession.getAttribute("sessionValidateCode"+valuuid)!=null){
				sessionValidateCode = httpSession.getAttribute("sessionValidateCode"+valuuid).toString();
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
			httpSession.removeAttribute("sessionValidateCode"+valuuid);
		}
	}
	/**
	 * 获取验证码
	 * @param request
	 * @param valuuid
	 * @param httpSession
	 * @return
	 */
	@RequestMapping(value = "loginGetValiCode", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getValidationCode(HttpServletRequest request,HttpSession httpSession) {
		Map<String, Object> returnMap = new HashMap<>();
		String valuuid = UUID.randomUUID().toString();
		try{
			Map<String, Object> map = new HashMap<String, Object>();
			map = customerService.getValidateCodePicture();
			httpSession.setAttribute("sessionValidateCode"+valuuid, map.get("validateCode"));
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
	 * @param httpSession
	 * @return
	 */
	public Map<String,Object> login(String username,String password,String valuuid,String valiCode,HttpServletRequest request,HttpSession httpSession) {
		boolean restr = true;
		int errorCount = httpSession.getAttribute("sessionValidateCode"+username)==null ? 0:(int)httpSession.getAttribute("sessionValidateCode"+username);
		if(errorCount > 2){
			restr = loginValidationCode(valiCode,valuuid,request,httpSession);
		}
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if(restr){
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("onCheckInfo", username);
			param.put("password", password);
			
			Map<String, Object> map = new HashMap<>();
			try {
				map = customerService.validatePasswordByUserInfo(param);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(map!=null&&(boolean)map.get("userValidateFlag")){
				String token =UUID.randomUUID().toString();
				httpSession.setAttribute(token, map);
				if(errorCount>0){
					httpSession.removeAttribute("sessionValidateCode"+username);
				}
				return returnMap;
			}else{
				errorCount += 1;
				httpSession.setAttribute("sessionValidateCode"+username, errorCount);
				returnMap.put("errorCount", errorCount);
			}
			return returnMap;
		}else{
			returnMap.put("vcflag", "false");
			return returnMap;
		}
	}
}
