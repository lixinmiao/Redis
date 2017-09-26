package com.ryx.global;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author 王赛超
 * 返回json数据时,使用该类包装
 *
 */
public class ReturnResult {

	// 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    // 响应业务状态
    private Integer code;
    
    // 响应中的数据
    private Object data;

    // 响应消息
    private String msg;
    
    public static ReturnResult build(Integer code, String msg, Object data) {
        return new ReturnResult(code, msg, data);
    }

    public static ReturnResult ok(Object data) {
        return new ReturnResult(data);
    }

    public static ReturnResult ok() {
        return new ReturnResult(null);
    }

    public ReturnResult() {

    }

    public static ReturnResult build(Integer code, String msg) {
        return new ReturnResult(code, msg, null);
    }

    public ReturnResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ReturnResult(Object data) {
        this.code = 200;
        this.msg = "OK";
        this.data = data;
    }

    
    

    public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
     * 将json结果集转化为ReturnResult对象
     * 
     * @param jsonData json数据
     * @param clazz ReturnResult中的object类型
     * @return
     */
    public static ReturnResult formatToPojo(String jsonData, Class<?> clazz) {
        try {
            if (clazz == null) {
                return MAPPER.readValue(jsonData, ReturnResult.class);
            }
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (clazz != null) {
                if (data.isObject()) {
                    obj = MAPPER.readValue(data.traverse(), clazz);
                } else if (data.isTextual()) {
                    obj = MAPPER.readValue(data.asText(), clazz);
                }
            }
            return build(jsonNode.get("code").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 没有object对象的转化
     * 
     * @param json
     * @return
     */
    public static ReturnResult format(String json) {
        try {
            return MAPPER.readValue(json, ReturnResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Object是集合转化
     * 
     * @param jsonData json数据
     * @param clazz 集合中的类型
     * @return
     */
    public static ReturnResult formatToList(String jsonData, Class<?> clazz) {
        try {
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (data.isArray() && data.size() > 0) {
                obj = MAPPER.readValue(data.traverse(),
                        MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            return build(jsonNode.get("code").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }

    
}
