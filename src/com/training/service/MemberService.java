package com.training.service;



public class MemberService {
	//private static FrontendService frontendService = new FrontendService();
	private  static MemberService memberService = new MemberService();
	private MemberService(){};
	public static MemberService getInstance() {
		
		return memberService;
	}
	
}
