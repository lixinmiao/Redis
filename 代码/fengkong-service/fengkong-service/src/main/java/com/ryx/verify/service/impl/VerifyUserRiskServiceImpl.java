package com.ryx.verify.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.SsdataDataserviceRiskRainscoreQueryRequest;
import com.alipay.api.response.SsdataDataserviceRiskRainscoreQueryResponse;
import com.ryx.global.ReturnResult;
import com.ryx.global.util.MathUtil;
import com.ryx.global.util.RedisUtil;
import com.ryx.verify.mapper.VerifyUserRiskMapper;
import com.ryx.verify.mapper.pojo.User_Risk;
import com.ryx.verify.mapper.pojo.User_Risk_Detail;
import com.ryx.verify.service.VerifyUserRiskService;

@Service
public class VerifyUserRiskServiceImpl implements VerifyUserRiskService {

	@Autowired
	private VerifyUserRiskMapper verifyUserRiskMapper;
	
	@Autowired
	private RedisUtil redisUtil;
	
	/**
	 * 验证用户风险
	 */
	@Override
	public ReturnResult verifyUserRisk(String phoneNumber) {
		
		ReturnResult result = verifyUserRiskQuery(phoneNumber);
		return result;
	}
	
	public ReturnResult verifyUserRiskQuery(String phoneNumber){
		
		String json=verifyUserRiskSDK(phoneNumber);
		if(json==null){
			return ReturnResult.build(500, "调用阿里蚁盾出错");
		}
		
		JSONObject parseObject = JSON.parseObject(json);
		
		JSONObject query_response = parseObject.getJSONObject("ssdata_dataservice_risk_rainscore_query_response");
		String code = (String) query_response.get("code");
		
		if("10000".equals(code)){//10000证明响应成功
			//风险分数
			String score=query_response.get("score")==null?null:MathUtil.round(query_response.get("score").toString(), 2);
			
			JSONArray infocode = query_response.getJSONArray("infocode");
			//风险解释   一个用户会对应多个风险解释
			List<User_Risk_Detail> User_Risk_Details=new ArrayList<>();
			for (int i = 0; i < infocode.size(); i++) {
				JSONObject jsonObject = infocode.getJSONObject(i);
				String risk_factor_code=(String) jsonObject.get("risk_factor_code");//风险因素编码
				String risk_factor_name=(String) jsonObject.get("risk_factor_name");//风险因素名称
				String risk_description=(String) jsonObject.get("risk_description");//风险描述
				User_Risk_Detail user_Risk_Detail=new User_Risk_Detail();
				user_Risk_Detail.setUserID(phoneNumber);
				user_Risk_Detail.setRiskFactorCode(risk_factor_code);
				user_Risk_Detail.setRiskFactor(risk_factor_name);
				user_Risk_Detail.setRiskDescription(risk_description);
				user_Risk_Detail.setUpdateTime(new Date());
				User_Risk_Details.add(user_Risk_Detail);
			}
			
			JSONArray label = query_response.getJSONArray("label");
			//用户的身份标签,如黄牛   可能为空
			String labelcode=label==null||label.isEmpty()?null:label.toString();
			
			User_Risk user_Risk=new User_Risk();
			user_Risk.setUserID(phoneNumber);
			user_Risk.setScore(score);
			user_Risk.setLabelcode(labelcode);
			user_Risk.setUpdateTime(new Date());
			
			//后续这些操作可以放到消息队列中执行
			//先去数据库查询用户信息是否存在,如果存在更新,不存在插入, 用户详细信息 每次都是直接删除,然后重新插入
			/*int isExist=verifyUserRiskMapper.selectTb_user_riskIsExist(phoneNumber);
			if(isExist>0){
				//如果存在,先删除后插入
				verifyUserRiskMapper.updateIntoTb_user_risk(user_Risk);
				verifyUserRiskMapper.deleteTb_user_risk_detail(phoneNumber);
				verifyUserRiskMapper.insertIntoTb_user_risk_detail(User_Risk_Details);
				
			}else{
				//如果不存在,插入
				verifyUserRiskMapper.insertIntoTb_user_risk(user_Risk);
				verifyUserRiskMapper.insertIntoTb_user_risk_detail(User_Risk_Details);
			}*/
			
			user_Risk.setUserRiskDetails(User_Risk_Details);
			return ReturnResult.build(Integer.parseInt(code), String.valueOf(query_response.get("msg")), user_Risk);
		}
		
		return ReturnResult.build(Integer.parseInt(code), String.valueOf(query_response.get("msg")));
	}
	
	/**
	 * 调用蚂蚁金服sdk
	 * @param phoneNumber 手机号
	 */
	public String verifyUserRiskSDK(String phoneNumber){
		
		try {
			AlipayClient alipayClient = new DefaultAlipayClient(
					"https://openapi.alipaydev.com/gateway.do",
					"2016080600180828",
					"MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCOYzs1sYSZh6DLeEATXfHTqNDSP8aCSrKnGSqmd4nvagUjZqCf2nxWg6cgT0Ecz8JitGlulZGJD9Hj4JGfN28yEAHuhpiJ83csXYXl0wxokxJdzXChDcdI1Ibcm55CASkvjZpfeuhNFRFXitMI+b3H8pb7YTitthjIgBsrg07kpXIZgVeIHVjP2lxGW7wwuJMBckoEMynaKLq/VoE8imxgq6EDvBUk5J+G+EEF4ijOZcEMn6eo9ua4UdzEHEtJ0ifYFnG93wxgZdtkdj2A3TtFR+qQF1nYU/MuxBrL/dtiG6mvKoRST8YdSqUzqDYKTqEUc2TN6ab8bzWKeAVI0E+VAgMBAAECggEAVZl0VBA3XjE6SngtVTdKFWvMtkke5t9PTcOEb3U32FWNZz3gXJl13HDjPTpIM7D29G3vYErMpnB7RxqPkP87ewZCkwoZk/1o7KQAxiy9rfSUuw2xozDsWG1j8iYf2yBliTAA2VvDYevDdDSCMEgrfpPpjpBhCZWKiqbxLpSPDkc+G66cZuakUriZdapxxEmB1cyx59hcflRqIfZLRxaCDVQZzyh3WuGiLQqwOR7TgFMw3Wsi3qaihgEPTtBCazVB8LUWpcADASX/vIAi0gw134O0JmNv4UYTNJGJJP1gBKATGxMJMWE5XgJAQ5jWosaWku3ES5pyT6T+sAeygQh6QQKBgQDMcMNDTDaddmOYL5cyaBNn6C4TNemY2xqyftH/0N4lRpOgbUnAoqslk6h2JiNptW3V0MXxxuTIVueeWHsFvs4dIH4jpJtghZk/1TBRyDOLBIn3+U7Tv9OTGLfRH8ccaeOgGGrMbqHDTz2mT4/tjhbUdFZwEoIASCiW3asKhqCXZQKBgQCyTCqHxyh8gjEJ7HgDSoftoRA8KAY8XDfoDB4y6EQvUllmZ2BBwsJPPLWzFLhNpDiYU5y8IPHtpOiYO0Be/BVFyY3fw3cj/qjdbepGsy272LEVoU4+RC3GPn9WLV7MG5kOJjPcldQ2T2Ahizg6/6gxaCJDWdaUSGgh5qIyGBXMcQKBgQCUjiP1VP4isZJxVbOidyoSyQ4h+30BC8V8itcB8r5Zi81eN8vt5MZEieVR5Si6eZGUWx5bL9GzGiM4VsceR6XyWFgQgl49ZxcS4VE5PCboD6ZJwKldCPku3DlGBrdimDdFcDi60HFPQNdlcE7/qc5BF0BwM6Kvnb+Q0QamoWk0WQKBgCixA1/bjxzjZae7JSSzJNSR1q+z/sBV15iI83dw1Obr52VcoHZBqbEFzP4i83Ec0qHJK1MwPaab5U2g8C79Xyl/N3bjGFTJsOZgSIB8MirV6IxkNQQ28S92Yf9b+W+huMum4l0sgLMoaGsfZ+TnJg6oknFGlpde8w0RouDF6GuxAoGBAJ7l3diMa5GZJRiypufkKQdhMxMgGVs95rqp/wFPsC4Ni9/DGpmepUzXqfIFeNO6iex0VvG2CTugjP71AO0Y3DHMuw1Kd/oMV84hkRfhFIZ2wm4O+hWvTIz6q32cH9KIVd1b44qqb7nn9xPrug+hNa21OXSoFpBYWCNBpvGFHIaW",
					"json",
					"utf-8",
					"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3Tr6eRR9UekElghsHvBSHtnJzCUrLKg78aUxzmO4XIJFQ4Kyd5Gmbk4sUFMPd5NS+zwS87iJ8N1e2m/T3K+TMddFkOpvWrztKmPra3Ue20i/egYTTcP6LkQoKy8LAs7Boj5vcjtwla5qWHqs89Jp1JLq74/rwmP56MS6kSuaC0d1YUkygLGzEktBepKp97ZA97gBkrbZ2H3oYigI+uBk6BF2/Dhx0Xmyg8pOjvoVzCRZXM7YwvflhDjsO0iKB6MNx4piz+CcPbvkcB32Eho7L/SXLgNQise6jke6kF2Lk+LSysnQvbhqSmwq/l0Gw4udSySWGtbFap0eemSDmQHpDQIDAQAB",
					"RSA2");
			SsdataDataserviceRiskRainscoreQueryRequest request = new SsdataDataserviceRiskRainscoreQueryRequest();
			request.setBizContent("{\"account_type\":\"MOBILE_NO\",\"account\":\""+phoneNumber+"\",\"version\":\"2.0\"}");
			SsdataDataserviceRiskRainscoreQueryResponse response = alipayClient.execute(request);
			if(response.isSuccess()){
				return response.getBody();
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


}
