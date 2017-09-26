package com.ryx.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.SsdataDataserviceRiskRainscoreQueryRequest;
import com.alipay.api.response.SsdataDataserviceRiskRainscoreQueryResponse;

public class Test {
	
	@org.junit.Test
	public void testString(){
		JSONObject jsonObject=new JSONObject();
		JSONArray jsonArray=new JSONArray();
		jsonArray.add("aaa");
		jsonArray.add("bbb");
		jsonArray.add("cccdddddd");
		jsonObject.put("label", jsonArray);
		JSONArray label = jsonObject.getJSONArray("label");
		//用户的身份标签,如黄牛   可能为空
		if(!label.isEmpty()){
			StringBuffer buffer=new StringBuffer();
			for (int i = 0; i < label.size(); i++) {
				buffer.append("".equals(label.get(i))||label.get(i)==null?"":(String)label.get(i)+",");
			}
			boolean endsWith = buffer.toString().endsWith(",");
			System.out.println(label.toString());
			int lastIndexOf = buffer.lastIndexOf(",");
		}
	}
	@org.junit.Test
	public void testJson(){
		JSONObject jsonObject=new JSONObject();
		JSONArray jsonArray=new JSONArray();
		jsonArray.add("aaa");
		jsonArray.add("bbb");
		jsonArray.add("ccc");
		jsonObject.put("label", jsonArray);
		System.out.println(jsonObject);
		
		JSONArray jsonArray2 = jsonObject.getJSONArray("label");
		for (int i = 0; i < jsonArray2.size(); i++) {
			Object object = jsonArray2.get(i);
			System.out.println(object);
		}
		
	}
	

	@org.junit.Test
	public void testUserRisk() throws AlipayApiException{
		
		AlipayClient alipayClient = new DefaultAlipayClient(
    			"https://openapi.alipaydev.com/gateway.do",
    			"2016080600180828",
    			"KLMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCOYzs1sYSZh6DLeEATXfHTqNDSP8aCSrKnGSqmd4nvagUjZqCf2nxWg6cgT0Ecz8JitGlulZGJD9Hj4JGfN28yEAHuhpiJ83csXYXl0wxokxJdzXChDcdI1Ibcm55CASkvjZpfeuhNFRFXitMI+b3H8pb7YTitthjIgBsrg07kpXIZgVeIHVjP2lxGW7wwuJMBckoEMynaKLq/VoE8imxgq6EDvBUk5J+G+EEF4ijOZcEMn6eo9ua4UdzEHEtJ0ifYFnG93wxgZdtkdj2A3TtFR+qQF1nYU/MuxBrL/dtiG6mvKoRST8YdSqUzqDYKTqEUc2TN6ab8bzWKeAVI0E+VAgMBAAECggEAVZl0VBA3XjE6SngtVTdKFWvMtkke5t9PTcOEb3U32FWNZz3gXJl13HDjPTpIM7D29G3vYErMpnB7RxqPkP87ewZCkwoZk/1o7KQAxiy9rfSUuw2xozDsWG1j8iYf2yBliTAA2VvDYevDdDSCMEgrfpPpjpBhCZWKiqbxLpSPDkc+G66cZuakUriZdapxxEmB1cyx59hcflRqIfZLRxaCDVQZzyh3WuGiLQqwOR7TgFMw3Wsi3qaihgEPTtBCazVB8LUWpcADASX/vIAi0gw134O0JmNv4UYTNJGJJP1gBKATGxMJMWE5XgJAQ5jWosaWku3ES5pyT6T+sAeygQh6QQKBgQDMcMNDTDaddmOYL5cyaBNn6C4TNemY2xqyftH/0N4lRpOgbUnAoqslk6h2JiNptW3V0MXxxuTIVueeWHsFvs4dIH4jpJtghZk/1TBRyDOLBIn3+U7Tv9OTGLfRH8ccaeOgGGrMbqHDTz2mT4/tjhbUdFZwEoIASCiW3asKhqCXZQKBgQCyTCqHxyh8gjEJ7HgDSoftoRA8KAY8XDfoDB4y6EQvUllmZ2BBwsJPPLWzFLhNpDiYU5y8IPHtpOiYO0Be/BVFyY3fw3cj/qjdbepGsy272LEVoU4+RC3GPn9WLV7MG5kOJjPcldQ2T2Ahizg6/6gxaCJDWdaUSGgh5qIyGBXMcQKBgQCUjiP1VP4isZJxVbOidyoSyQ4h+30BC8V8itcB8r5Zi81eN8vt5MZEieVR5Si6eZGUWx5bL9GzGiM4VsceR6XyWFgQgl49ZxcS4VE5PCboD6ZJwKldCPku3DlGBrdimDdFcDi60HFPQNdlcE7/qc5BF0BwM6Kvnb+Q0QamoWk0WQKBgCixA1/bjxzjZae7JSSzJNSR1q+z/sBV15iI83dw1Obr52VcoHZBqbEFzP4i83Ec0qHJK1MwPaab5U2g8C79Xyl/N3bjGFTJsOZgSIB8MirV6IxkNQQ28S92Yf9b+W+huMum4l0sgLMoaGsfZ+TnJg6oknFGlpde8w0RouDF6GuxAoGBAJ7l3diMa5GZJRiypufkKQdhMxMgGVs95rqp/wFPsC4Ni9/DGpmepUzXqfIFeNO6iex0VvG2CTugjP71AO0Y3DHMuw1Kd/oMV84hkRfhFIZ2wm4O+hWvTIz6q32cH9KIVd1b44qqb7nn9xPrug+hNa21OXSoFpBYWCNBpvGFHIaW",
    			"json",
    			"utf-8",
    			"KLMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3Tr6eRR9UekElghsHvBSHtnJzCUrLKg78aUxzmO4XIJFQ4Kyd5Gmbk4sUFMPd5NS+zwS87iJ8N1e2m/T3K+TMddFkOpvWrztKmPra3Ue20i/egYTTcP6LkQoKy8LAs7Boj5vcjtwla5qWHqs89Jp1JLq74/rwmP56MS6kSuaC0d1YUkygLGzEktBepKp97ZA97gBkrbZ2H3oYigI+uBk6BF2/Dhx0Xmyg8pOjvoVzCRZXM7YwvflhDjsO0iKB6MNx4piz+CcPbvkcB32Eho7L/SXLgNQise6jke6kF2Lk+LSysnQvbhqSmwq/l0Gw4udSySWGtbFap0eemSDmQHpDQIDAQAB",
    			"RSA2");
    	SsdataDataserviceRiskRainscoreQueryRequest request = new SsdataDataserviceRiskRainscoreQueryRequest();
    	request.setBizContent("{\"account_type\":\"MOBILE_NO\",\"account\":\"13341148666\",\"version\":\"2.0\"}");
    	SsdataDataserviceRiskRainscoreQueryResponse response = alipayClient.execute(request);
    	if(response.isSuccess()){
    		System.out.println(response.getBody());
    	} else {
    	System.out.println("调用失败");
    	}
	}
}
