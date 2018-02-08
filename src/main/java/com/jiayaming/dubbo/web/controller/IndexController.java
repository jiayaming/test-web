package com.jiayaming.dubbo.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jiayaming.dubbo.customer.CustomerService;

@Controller
@RequestMapping("IndexController")
public class IndexController {
	@Value("#{configProperties['zookeeper']}")
	private String zookeeper;
	@Autowired
	CustomerService customerService;
	
	@RequestMapping(value="shishi",method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> shishi(String aa) throws Exception {
		System.out.println(zookeeper);
		
		Integer id = Integer.valueOf(aa);
		
		Map<String,Object> requestMap = new HashMap<>();
		requestMap.put("id", id);
		Map<String,Object> resultMap=customerService.getCustomerInfoByMap(requestMap);
		return resultMap;
	}
}
