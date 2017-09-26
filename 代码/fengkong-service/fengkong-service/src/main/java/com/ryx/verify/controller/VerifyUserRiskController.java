package com.ryx.verify.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ryx.amq.ProducerMessageSender;
import com.ryx.global.ReturnResult;
import com.ryx.verify.mapper.pojo.User_Risk;
import com.ryx.verify.service.VerifyUserRiskService;

@Controller
public class VerifyUserRiskController {

	@Autowired
	private VerifyUserRiskService verifyUserRiskService;
	
	//@Autowired
	//private ProducerMessageSender messageSender;
	
	
	@RequestMapping(value="verifyAmq")
	@ResponseBody
	public void verifyAmq(@RequestParam(value="phoneNumber",required=false)String phoneNumber){
		
		try {
			
			
			/*for (int i = 0; i < 1; i++) {
				messageSender.SendMessage(i);
			}*/
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	@RequestMapping(value="verifyUserRisk")
	@ResponseBody
	public ReturnResult verifyUserRisk(@RequestParam(value="phoneNumber",required=false)String phoneNumber){
		//如果手机号码为空,返回提示
		if(phoneNumber==null||"".equals(phoneNumber)){
			return ReturnResult.build(401, "手机号不能为空");
		}
		
		try {
			
			ReturnResult returnResult = verifyUserRiskService.verifyUserRisk(phoneNumber);
			
			return returnResult;
			
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.build(500, "服务器内部错误");
		}
		
	}
}
