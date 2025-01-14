package com.teamrun.runbike.party.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.teamrun.runbike.party.domain.PartyInfo;
import com.teamrun.runbike.party.domain.RequestParticipationInsert;
import com.teamrun.runbike.party.domain.RequestPartyCreate;
import com.teamrun.runbike.party.service.PartyCreateService;
import com.teamrun.runbike.party.service.PartyInfoService;
import com.teamrun.runbike.party.service.PartyJoinService;
import com.teamrun.runbike.party.service.PartyListService;

@Controller
@RequestMapping("/party")
public class PartyMainContoller {

	
	@Autowired
	private PartyCreateService createService;

	@Autowired
	private PartyJoinService joinService;
	
	@Autowired
	private PartyListService listService;
	
	@Autowired
	private PartyInfoService partyInfoService;
	
	
	// 인덱스에서 함께하기로 갈 때 분기처리(참여한 방이 있냐없냐 따라서)
	@RequestMapping(method = RequestMethod.GET)
	public String getMain(HttpServletRequest request, Model model) {
		String view = "party/partyLobby";
		int count = 0;
		int p_num = 0;
		int u_idx = 72;
		// 세션에서 u_idx 가져옴
		// HttpSession session = request.getSession(false);
		// LoginInfo loginInfo = session.getAttribute("loginInfo");
		// u_idx = loginInfo.getU_idx();
		
		// 로그인 안 되어있으면 로그인 페이지로 보냄
		// view="";
		
		// 로그인 되었다는 전제
		count = partyInfoService.hasParty(u_idx);
		
		//System.out.println("hasParty count : "+count);
		
		if(count>0) {
			p_num = partyInfoService.getPartyNum(u_idx); // 그 방번호 얻어오기

			view = "redirect:/party/"+p_num; // 그 방 페이지로 넘어감
		}
		
		return view;
	}
	
	// 방을 보여줌, 방 넘버를 가지고 간다
	@RequestMapping(value="/{p_num}", method = RequestMethod.GET)
	public String getPartyPage(@PathVariable int p_num, Model model) {
		model.addAttribute("p_num",p_num);
		String view = "party/partyRoom";
		return view;
	}
	
	
	// ajax로 가져올 때 사용할 방의 정보
	@CrossOrigin
	@ResponseBody
	@RequestMapping(value="/room/{p_num}", method = RequestMethod.GET)
	public PartyInfo getPartyInfo(@PathVariable int p_num, Model model) {
		PartyInfo partyInfo = partyInfoService.getPartyInfoOne(p_num);
		model.addAttribute("partyInfo",partyInfo);
		return partyInfo;
	}
	
	
	// 방 만들기
	@CrossOrigin
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public int createParty(@RequestBody RequestPartyCreate createRequest) {
		System.out.println(createRequest);
		int resultCnt=-1;
		int key = createService.partyInsert(createRequest);
		RequestParticipationInsert participationRequest = new RequestParticipationInsert(createRequest.getU_idx(), key, 'Y');
		resultCnt = joinService.participationInsertAsMaster(participationRequest);
		return resultCnt;
	}
	
	// 방 리스트
	@CrossOrigin
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<PartyInfo> getAllListClosedN(){ 
		return listService.getAllListClosedN(); // 열려있는 방만 보여주기
	} 
	
	
}
