package com.ryx.verify.service;

import java.util.List;

import com.ryx.global.ReturnResult;
import com.ryx.verify.mapper.pojo.User_Risk;


public interface VerifyUserRiskService {

	ReturnResult verifyUserRisk(String phoneNumber);


}
