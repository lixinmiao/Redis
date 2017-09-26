package com.ryx.verify.mapper.pojo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 用户风险解释
 * @author broadthinking
 *
 */
public class User_Risk_Detail {

	private Long id;
	private String userID;//用户ID
	private String riskFactorCode;//特征码
	private String riskFactor;//特征名
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
	private Date updateTime;//更新时间
	private String riskDescription;//特征描述
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getRiskFactorCode() {
		return riskFactorCode;
	}
	public void setRiskFactorCode(String riskFactorCode) {
		this.riskFactorCode = riskFactorCode;
	}
	public String getRiskFactor() {
		return riskFactor;
	}
	public void setRiskFactor(String riskFactor) {
		this.riskFactor = riskFactor;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getRiskDescription() {
		return riskDescription;
	}
	public void setRiskDescription(String riskDescription) {
		this.riskDescription = riskDescription;
	}
	
	
	
}
