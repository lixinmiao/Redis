package com.ryx.verify.mapper.pojo;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
/**
 * 用户风险
 * @author broadthinking
 *
 */
public class User_Risk {

	private Long id;
	private String userID;//用户ID
	private String score;//风险评分  范围为[0,100]，评分越高风险越大
	private String labelcode;//标签代码 可能是多个 多个标签以“,”分隔
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
	private Date updateTime;//更新时间
	
	private String note;//备注
	private List<User_Risk_Detail> userRiskDetails;//一个用户对应多个 风险解释
	
	
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
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getLabelcode() {
		return labelcode;
	}
	public void setLabelcode(String labelcode) {
		this.labelcode = labelcode;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public List<User_Risk_Detail> getUserRiskDetails() {
		return userRiskDetails;
	}
	public void setUserRiskDetails(List<User_Risk_Detail> userRiskDetails) {
		this.userRiskDetails = userRiskDetails;
	}
	
	
	
}
