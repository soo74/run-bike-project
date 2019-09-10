package com.teamrun.runbike.user.service;


import java.io.File;
import java.io.IOException;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.teamrun.runbike.user.dao.UserDao;
import com.teamrun.runbike.user.domain.RegisterInfo;
import com.teamrun.runbike.user.domain.UserInfo;

@Service("regService")
public class RegisterService implements UserService{
	
	@Autowired
	private SqlSessionTemplate template;
	
	@Autowired
	private MailSenderService mailService;
	
	private UserDao dao;
	
	public int regService(MultipartHttpServletRequest request, RegisterInfo regInfo) {
		int result = 0;
		String newFileName = null;
		
		String path = "/uploadfile/userphoto";
		String dir = request.getSession().getServletContext().getRealPath(path);
		
		UserInfo userInfo = regInfo.toUserInfo();

		dao = template.getMapper(UserDao.class);
		
		System.out.println(regInfo.toString());
		
		try {
			if(regInfo.getU_photo()!=null) {
				newFileName = userInfo.getU_id()+"_"+regInfo.getU_photo().getOriginalFilename();
				userInfo.setU_photo(newFileName);
				regInfo.getU_photo().transferTo(new File(dir, newFileName));
			} else {
				userInfo.setU_photo("noImg.jpg");
			}
			
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		
		System.out.println(userInfo.toString());
		
		mailService.mailSend(userInfo.getU_id(), userInfo.getU_code(), userInfo.getU_name());
		
		result = dao.insertUser(userInfo);
		
		return result;
	}
}
