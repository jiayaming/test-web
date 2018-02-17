package com.jiayaming.dubbo.web.comm;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class TokenFilter implements HandlerInterceptor{

	private String delFioterURL;
	
	public String getDelFioterURL() {
		return delFioterURL;
	}

	public void setDelFioterURL(String delFioterURL) {
		this.delFioterURL = delFioterURL;
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(!checkChannelAccess(request,response,handler)){
			System.out.println("用户短时间内多次访问被限制！！！");
			return false;
		}
		
		String token = request.getHeader("Authorization");
		Object customerInfo = request.getSession().getAttribute(token);
		
		String url = request.getRequestURI();
		if(url.matches("(/[a-zA-Z0-9\\-]*)*("+delFioterURL.replace(",", ")[a-zA-Z0-9\\-]*(/[a-zA-Z0-9\\-]*)*|(/[a-zA-Z0-9\\-]*)*(")+")[a-zA-Z0-9\\-]*(/[a-zA-Z0-9\\-]*)*")){//过滤不做校验
			if(customerInfo != null && token != null) {//如果用户在线则重新存入一下使session延长30分钟
				request.getSession().setAttribute(token, customerInfo.toString());
			}
			return true;
		}else{//需要做校验
			if(customerInfo != null && token != null) {
				request.getSession().setAttribute(token, customerInfo.toString());
				return true;
			}else {
				response.setStatus(415);
				return false;
			}
		}
	}
	
	/**
	 * 检测用户是否频繁访问某一方法
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	private boolean checkChannelAccess(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		long maxIntervalTime = 1000;//ms
		int maxAccessTimes = 10;
		long limitTime = 5000;//ms
		
		String ip = request.getRemoteAddr();
		String url = request.getServletPath();
		String key = ip+url;
		
		long newTime = new Date().getTime();
		
		if(request.getSession().getAttribute(key+"CreatTime") == null){
			request.getSession().setAttribute(key+"CreatTime", newTime);
			request.getSession().setAttribute(key+"accessTimes", 1);
			return true;
		}else{
			long creatTime = (long)request.getSession().getAttribute(key+"CreatTime");
			
			if(request.getSession().getAttribute(key+"limitTime") != null){
				if((newTime - creatTime) < limitTime){
					request.getSession().setAttribute(key+"CreatTime", newTime);
					return false;
				}
			}
			
			if((newTime - creatTime)>maxIntervalTime){
				request.getSession().setAttribute(key+"CreatTime", newTime);
				request.getSession().setAttribute(key+"accessTimes", 1);
				return true;
			}else{
				int accessTimes = (int)request.getSession().getAttribute(key+"accessTimes");
				accessTimes = accessTimes + 1;
				if(accessTimes > maxAccessTimes){
					request.getSession().setAttribute(key+"limitTime", limitTime);
					request.getSession().setAttribute(key+"CreatTime", newTime);
					return false;
				}else{
					request.getSession().setAttribute(key+"accessTimes", accessTimes);
					return true;
				}
				
			}
		}
		
		
	}

}
