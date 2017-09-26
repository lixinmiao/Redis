package com.ryx.verify.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ryx.verify.mapper.pojo.User_Risk;
import com.ryx.verify.mapper.pojo.User_Risk_Detail;

public interface VerifyUserRiskMapper {

	int insertIntoTb_user_risk(User_Risk user_Risk);

	int insertIntoTb_user_risk_detail(List<User_Risk_Detail> user_Risk_Details);

	int selectTb_user_riskIsExist(@Param("userID")String phoneNumber);

	int updateIntoTb_user_risk(User_Risk user_Risk);

	int deleteTb_user_risk_detail(@Param("userID")String phoneNumber);


}
