package com.gsc.kixxhub.device.pumpa.common;

public class Protocol { 

	/**
	 * 
	 */
	public static int GasODT 		  = 75; // 충전기
	/**
	 * 
	 */
	public static int newGasODT 	  = 76; // 충전기 ODT TCP/IP 통신방식 적용으로 인한 신규 생성 , 2016.03.28 양일준
	/**
	 GS 주유기/셀프 표준 프로토콜 정의
	 */
	public static int GSLubr		  = 80; // GS 표준 주유기 프로토콜
	/**
	 * 
	 */
	public static int GSSelfLubr	  = 81; // GS 표준 셀프주유기 프로토콜
	/**
	 * 
	 */
	public static int GSSelfODT	  	  = 90; // GS 표준 셀프ODT 프로토콜(시험개발- Serial용, 사용않음)
	/**
	 * PI2-박동화, 2105-11-18 추가
	 */
	public static int GSSelfODTi 	  = 91; // GS 표준 셀프ODT 프로토콜(LAN용) - ODT가 주유기 제어함.
	/**
	 * PI2-박동화, 2105-11-18 추가
	 */
	public static int GSSelfVNoz 	  = 92; // GS 표준 셀프주유기 프로토콜 - 가상노즐(통신하지 않음)
	/**
	 * 
	 */
	public static int GSSelfVODT  	  = 95; // GS 표준 셀프 가상ODT 프로토콜(시험개발 - Serial용, 사용않음)
	/**
	 * 
	 */
	public static int PrimeSelf		  = 50; // 동화프라임 셀프ODT
	/**
	 * 
	 */
	public static int PrimeSelfODT	  = 55; // 동화프라임 셀프 가상ODT
	/**
	 * 
	 */
	public static int SK	  		  = 60; // SK 주유기
	/**
	 * 
	 */
	public static int SomoSelf		  = 40; // 소모 셀프ODT
	/**
	 * 
	 */
	public static int SomoSelfN		  = 41; // 소모 셀프ODT 신형
	/**
	 * 
	 */
	public static int Tatsuno	  	  = 10; // 다쓰노 구형
	/**
	 * 
	 */
	public static int TatsunoMPP4     = 12; // 다쓰노 4복식
	/**
	 * 
	 */
	public static int TatsunoMPP6     = 13; // 다쓰노 6복식
	/**
	 * 
	 */
	public static int TatsunoMPPL_vODT= 35; // 다쓰노 MPP형 일반 주유기용 가상ODT
	/**
	 * 
	 */
	public static int TatsunoN   	  = 11; // 다쓰노 신형
	
	/**
	 * 
	 */
	public static int TatsunoSelfHS	  = 37; // 신형 다쓰노셀프(현성) - 추가 2012.08, dhp
	/**
	 * 
	 */
	public static int TatsunoSelfMPP4 = 30; // 다쓰노 4복식 셀프ODT
	/**
	 * 
	 */
	public static int TatsunoSelfMPP6 = 31; // 다쓰노 6복식 셀프ODT
	/**
	 * 
	 */
	public static int Tokico          = 20; // 도끼꼬 구형
	
	/**
	 * 
	 */
	public static int TokicoN         = 21; // 도끼꼬 신형
	
	/**
	 * 
	 */
	public static int WooJoo		  = 1;  // 우주
	
}
