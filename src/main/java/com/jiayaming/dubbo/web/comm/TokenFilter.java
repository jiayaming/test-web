package com.jiayaming.dubbo.web.comm;

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
		// TODO Auto-generated method stub
		/*String token = request.getParameter("token");
		String url = request.getRequestURI();
		if(url.matches("(/[a-zA-Z0-9\\-]*)*("+delFioterURL.replace(",", ")[a-zA-Z0-9\\-]*(/[a-zA-Z0-9\\-]*)*|(/[a-zA-Z0-9\\-]*)*(")+")[a-zA-Z0-9\\-]*(/[a-zA-Z0-9\\-]*)*")){//过滤不做校验
			System.out.println("校验通过");
			return true;
		}else{
			System.out.println("校验没通过");
			return false;
		}*/
		return true;
	}

}
