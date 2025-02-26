package com.gsc.kixxhub.common.data;


public interface IPumpConstant {

	public static String CB_CUSTOMER_TYPE_CUS_CASH 			= "2"	;		//현금 거래처
	public static String CB_CUSTOMER_TYPE_CUS_FIX_AMT 		= "4"	;		//정량 입력 고객
	public static String CB_CUSTOMER_TYPE_CUS_OISANG 		= "3"	;		//외상 거래처
	public static String CB_CUSTOMER_TYPE_GENERAL 			= "1"	;		//일반 고객
	public static final String COMMANDID_ACK = "ACK" ;	// 충전기 - 현금결제 응답
	public static final String COMMANDID_AH = "AH" ;	// 표준셀프 - 단말기 무결 성 체크 요청
	public static final String COMMANDID_AI = "AI" ;	// 표준셀프 - 단말기 무결 성 체크 응답
	/* 신규 COMMAND 추가 시작 끝*/
	
	public static final String COMMANDID_BA = "BA" ;	// 충전기 - 보너스 점수 누적 요청
	public static final String COMMANDID_BB = "BB" ;	// 충전기 - 보너스 점수 누적 응답
	public static final String COMMANDID_BC = "BC" ;	// 다쓰노셀프 - 지폐투입 취소요청
	/* 
	 * Command ID for Pump/ODT Device between Pump Module and Pump Adapter
	 */
	public static final String COMMANDID_BI = "BI" ;
	public static final String COMMANDID_BR = "BR" ;	// GSC셀프 - 세차권 바코드 요청      
	public static final String COMMANDID_BS = "BS" ;	// GSC셀프 - 세차권 바코드 요청  응답
	public static final String COMMANDID_CA = "CA" ;	// 다쓰노셀프 - 고객유형확인 요청
	public static final String COMMANDID_CB = "CB" ;	// 다쓰노셀프 - 고객유형확인 응답
	public static final String COMMANDID_CL = "CL" ;
	public static final String COMMANDID_CP = "CP" ;	// 충전기 - 보너스 점수 조회 응답
	public static final String COMMANDID_D0 = "D0" ;	// 충전기 - 환경정보 설정
	public static final String COMMANDID_D1 = "D1" ;
	public static final String COMMANDID_D2 = "D2" ;
	public static final String COMMANDID_D3 = "D3" ;
	public static final String COMMANDID_D4 = "D4" ;
	public static final String COMMANDID_D5 = "D5" ;
	public static final String COMMANDID_D6 = "D6" ;

	public static final String COMMANDID_D7 = "D7" ;
	public static final String COMMANDID_E0 = "E0" ;
	public static final String COMMANDID_EX = "EX" ;
	public static final String COMMANDID_F0 = "F0" ;	// 정액/정량 멈춤
	public static final String COMMANDID_FAIL = "FAIL" ;
	public static final String COMMANDID_FC = "FC" ;	// 표준셀프 - 가득주유 통제
	/** ChoWonIk, PI2, 2015-10-28, 신규 ODT(GSC Self) 신규 코드 추가 */
	public static final String COMMANDID_GA = "GA" ;	// GSC셀프 - 카드결제 승인요청 & 취소    
	public static final String COMMANDID_GB = "GB" ;	// GSC셀프 - 카드결제 승인응답      
	public static final String COMMANDID_GL = "GL" ;	// GSC셀프 - 영수증 출력처리 승인 요청      
	public static final String COMMANDID_GR = "GR" ;	// GSC셀프 - 취소 카드(신규추가)
	public static final String COMMANDID_GT = "GT" ;	// GSC셀프 - 판매완료 전송          
	public static final String COMMANDID_HA = "HA" ;	// 소모셀프 - 승인 요청
	public static final String COMMANDID_HB = "HB" ;	// 소모셀프 - 외상 요청
	
	public static final String COMMANDID_HC = "HC" ;	// 소모셀프 - 승인 응답
	public static final String COMMANDID_HD = "HD" ;	// 소모셀프 - 외상 응답
	public static final String COMMANDID_HE = "HE" ;	// 다쓰노셀프 - 카드결제 승인요청
	public static final String COMMANDID_HF = "HF" ;	// Preset 자료 전송
	public static final String COMMANDID_HG = "HG" ;
	public static final String COMMANDID_M1 = "M1" ;	// 주유기 / ODT 마스터 정보 설정
	public static final String COMMANDID_NAK = "NAK" ;	// 충전기 - 존재하지 않는 경우
	public static final String COMMANDID_P1 = "P1" ;	// 셀프 주유기 사업자 정보 설정
	public static final String COMMANDID_P2 = "P2" ;	// 셀프 주유기 머리말/꼬리말 설정
	public static final String COMMANDID_P3 = "P3" ;	// 주유기 환경 정보 설정
	public static final String COMMANDID_P3_1 = "P3_1" ;
	public static final String COMMANDID_P5 = "P5" ;	// ODT 환경정보 설정 (셀프, 충전기)
	public static final String COMMANDID_P5_1 = "P5_1";
	public static final String COMMANDID_PC = "PC" ;	// 매장 환경정보 설정
	public static final String COMMANDID_P6 = "P6" ;	// 영업일 및 시간 설정
	public static final String COMMANDID_P7 = "P7" ;	// 주유기 / ODT 파라미터 설정
	public static final String COMMANDID_P8 = "P8" ;	// 충전기 - ODT 토털게이지 자료 요청
	public static final String COMMANDID_P9 = "P9" ;	// 충전기 - ODT 영업마감 처리 요청	
	public static final String COMMANDID_PA = "PA" ;	// 노즐 제어 명령 (주유기, 셀프, 충전기)
	public static final String COMMANDID_PB = "PB" ;	// Preset 설정 (주유기,셀프,충전기)
	public static final String COMMANDID_PE = "PE" ;	// 주유기/충전기 상태 요청
	public static final String COMMANDID_PF = "PF" ;	// 충전기 - 중복차량이 존재하는 경우
	
	public static final String COMMANDID_PG = "PG" ;	// 충전기 - 고객카드 승인 응답 (중복 차량이 아닌 경우)
	public static final String COMMANDID_PI = "PI" ;	// 충전기 - 카드결제 승인 응답
	/*프로젝트명 : PL2
	 * 변경일자 : 2015.11.23
	 * 변경사항 : 표준 셀프 전문 추가에 따른 명령 처리 변수 추가
	 * 변경자 : 정혜정
	 * */
	/* 신규 COMMAND 추가 시작*/
	public static final String COMMANDID_PM = "PM" ;	// 표준셀프 - ODT 모드정보전송
	public static final String COMMANDID_PP = "PP" ;	// 충전기 - 보관증 번호부여 응답
	public static final String COMMANDID_PQ = "PQ" ;	// 충전기 - 보관량 조회 응답
	public static final String COMMANDID_PT = "PT" ;
	public static final String COMMANDID_PU = "PU" ;	// 표준셀프 - ODT Update 요청
	public static final String COMMANDID_PV = "PV" ;	// 표준셀프 - ODT 버전정보전송

	public static final String COMMANDID_QD = "QD" ;	// 소모셀프 - 보너스 매장 점수	
	public static final String COMMANDID_QF = "QF" ;	// Preset 자료 요청
	public static final String COMMANDID_QL = "QL" ;	// 다쓰노셀프 - 영수증 출력처리
	public static final String COMMANDID_QM = "QM" ;	// 다쓰노셀프 - 카드결제 승인응답
	public static final String COMMANDID_S3 = "S3" ;	// 주유중 자료 전송 (주유기, 셀프, 충전기)
	public static final String COMMANDID_S4 = "S4" ;	// 주유완료 전송 (주유기, 셀프)
	
	public static final String COMMANDID_S5 = "S5" ;	// 충전기 - 토털게이즈 전송
	public static final String COMMANDID_S8 = "S8" ;	// 주유기/충전기 상태 응답
	public static final String COMMANDID_S9 = "S9" ;	// 충전기 - 고객카드 승인 요청
	public static final String COMMANDID_SB = "SB" ;	// 충전기 - 카드결제 승인 요청
	public static final String COMMANDID_SE = "SE" ;	// 주유 장비 이상정보 전송
	public static final String COMMANDID_SF = "SF" ;	// 충전기 - 보관증 번호부여 요청
	public static final String COMMANDID_SG = "SG" ;	// 충전기 - 보관량 조회 요청
	
	public static final String COMMANDID_SH = "SH" ;	// 충전기 - ODT 최종판매데이터 전송
	public static final String COMMANDID_SI = "SI" ;	// 주유 시작 자료 요청
	public static final String COMMANDID_SJ = "SJ" ;	// 주유 시작 자료 응답
	/*
	 * 변경내용 : 충전기 ODT 내 승인 통합전문 생성에 따른 추가
	 * 변경일자 : 2016.03.28
	 * 변경자 : 양일준 
	 */
	public static final String COMMANDID_SK = "SK" ;  // 충전기 ODT 승인요청
	public static final String COMMANDID_SL = "SL" ;  // 충전기 ODT 승인응답
	
	public static final String COMMANDID_ST = "ST" ;	// 소모셀프 - 판매완료 전송
	public static final String COMMANDID_SY = "SY" ;	// 노즐의 사용여부 설정 (주유기,셀프,충전기)
	
	public static final String COMMANDID_TD = "TD" ;	// 충전기 - 보너스 점수 조회 요청
	public static final String COMMANDID_TJ = "TJ" ;	// 충전기 - 현금 영수증 요청
	public static final String COMMANDID_TR = "TR" ;	// 다쓰노셀프 - 판매완료 전송 
	
	public static final String COMMANDID_XA = "XA" ;	// 충전기 - 현금 영수증 응답
	public static final String COMPLETED	= "COMPLETED" ;

	/*차량인지기반결제자동화, 2017-04-11, 양일준 */
	public static final String COMMANDID_J0 = "J0" ;    // 비콘매장여부 전문 
	public static final String COMMANDID_JA = "JA" ;    // ControlArk 통신 전문 (pumpA->pumpM)
	public static final String COMMANDID_JB = "JB" ;	// ControlArk 통신 전문 (pumpM->pumpA)
	public static final String COMMANDID_JC = "JC" ;	// ControlArk 통신 전문 (pumpA->pumpM)
	public static final String COMMANDID_JD = "JD" ;	// ControlArk 통신 전문 (pumpM->pumpA)
	public static final String COMMANDID_JZ = "JZ" ;	// ControlArk ARK 전문 
	
	public static final String COMMANDID_JK = "JK" ;	// ControlArk 취소요청전송
	public static final String COMMANDID_JI = "JI" ;	// ControlArk 차량이탈
	public static final String COMMANDID_JQ = "JQ" ;	// ControlArk 결제요청
	
	public static final String COMMANDID_JF = "JF" ;	// ControlArk 주유시작
	public static final String COMMANDID_JG = "JG" ;	// ControlArk 주유완료
	
	// 전문의 delimiter
	public static byte DELIMETER_0x01 = 0x01;	// SOH
	public static byte DELIMITER_0X02 = 0x02 ;	// STX
	public static byte DELIMITER_0X03 = 0x03 ;	// ETX
	public static byte DELIMITER_0X0F = 0x0f ;	// SI
	public static byte DELIMITER_0X1C = 0x1c ;	// FS
	public static byte DELIMITER_0X1D = 0x1d ;	// GS
	public static byte DELIMITER_0X1E = 0x1e ; 	// RS
	public static byte DELIMITER_0X1F = 0x1f ;	// US
	
	public static String DELIMITER_ETX_STRING = Character.toString((char)DELIMITER_0X03) ;
	public static String DELIMITER_FS_STRING = Character.toString((char)DELIMITER_0X1C) ;
	public static String DELIMITER_GS_STRING = Character.toString((char)DELIMITER_0X1D) ;
	public static String DELIMITER_RS_STRING = Character.toString((char)DELIMITER_0X1E) ;	
	public static String DELIMITER_SI_STRING = Character.toString((char)DELIMITER_0X0F) ;
	public static String DELIMITER_STX_STRING = Character.toString((char)DELIMITER_0X02) ;
	public static String DELIMITER_US_STRING = Character.toString((char)DELIMITER_0X1F) ;
	public static final String DIRECTION_FROM_ADAPTOR 	= "FROM_ADAPTOR";

	// DIRECTION
	public static final String DIRECTION_FROM_MODULE 	= "FROM_MODULE";
	
	public static final String DIRECTION_FROM_ODT		= "FROM_ODT";	
	public static final int KH_PUMP_DEFAULT 			= 0 ;
	public static final int KH_PUMP_COMPLETED 			= 1 ;
	public static final int KH_PUMP_PRESET 				= 2 ;
	public static final int KH_PUMP_START 				= 3 ;
	public static final int KH_ODT_PAID_REQ				= 4 ;
	public static final int KH_PUMPING					= 5 ;
	public static final int KH_ODT_TR_SH_ST_COMPLETED	= 6 ;	// 다쓰노, 소모셀프와 충전기에서 사용된다.
	public static final int KH_STATE_RESET				= 7 ;	// 신규 KH 처리번호를 생성하고 상태를 Reset
	
	public static final String[] KH_PUMP_STATE_STRING = new String[] {
		"KH_PUMP_DEFAULT",
		"KH_PUMP_COMPLETED" , 
		"KH_PUMP_PRESET" , 
		"KH_PUMP_START" , 
		"KH_ODT_PAID_REQ",
		"KH_PUMPING",
		"KH_ODT_TR_SH_ST_COMPLETED",
		"KH_STATE_RESET"} ;
	
	public static final int PRESET_FROM_NONE		= 0 ;
	public static final int PRESET_FROM_PUMP		= 1 ;		// 사용 하지 않음	
	public static final int PRESET_FROM_POS			= 2 ;	
	public static final int PRESET_FROM_ODT			= 3 ;		// 사용 하지 않음
	public static final int PRESET_FROM_CAT			= 4 ;
	
	
	public static final String PUMP_KEEP_NUMBER		= "keepNum" ;
	public static final int PUMP_PROCESS_WAITTING_TIME_FROM_ADAPTOR = 40000;
	
	/*
	 * PUMP PROCESS에서 사용하는 대기 시간 
	 */
	public static final int PUMP_PROCESS_WAITTING_TIME_FROM_MODULE 	= 20000;
	
	/*
	 * 주유기 Protocol 정의
	 */
	public static final int PUMP_PROTOCOL_DEFAULT		= 0 ;
	public static final int PUMP_PROTOCOL_SOMO			= 1 ;
	public static final int PUMP_PROTOCOL_DaSNo			= 2 ;
	public static final int PUMP_PROTOCOL_Recharge		= 3 ;
	public static final int PUMP_PROTOCOL_GSC_SELF		= 4 ;  // TCP/IP 통신방식 셀프ODT(4) 추가
	public static final int PUMP_PROTOCOL_NewDaSNo	    = 5 ;
	public static final int PUMP_PROTOCOL_NewRecharge   = 6 ;  // TCP/IP 통신방식 충전기ODT(6) 추가, 양일준- 2016.03.28
	public static final String PUMP_STATECODE_ERROR = "1" ;
	// State Code for Internal Use in Pump Adapter and Pump Module
	public static final String PUMP_STATECODE_OK 	= "0" ;
	public static final String SK_COMMAND_AP = "AP";
	// SK PROTOCOL
	public static final String SK_COMMAND_AQ = "AQ";
	public static final String SK_COMMAND_CQ = "CQ";
	public static final String SK_COMMAND_CT = "CT";
	public static final String SK_COMMAND_IN = "IN";
	public static final String SK_COMMAND_LK = "LK";
	
	public static final String SK_COMMAND_PC = "PC";
	public static final String SK_COMMAND_PP = "PP";
	public static final String SK_COMMAND_RT = "RT";
	public static final String SK_COMMAND_KH = "SC";
	
	
	public static final String SK_COMMAND_ST = "ST";
	public static final String SK_COMMAND_TQ = "TQ";
	public static final String SK_COMMAND_TR = "TR";
	public static final String SK_COMMAND_UL = "UL";
	// Constant for start and stop
	public static final String START 		= "START" ;
	public static final int STEP_PUMP_DB_COMPLETED 		= 3 ;
	public static final int STEP_PUMP_DD_COMPLETED 		= 5 ;
	public static final int STEP_PUMP_DD_CONTINUING		= 4 ;
	public static final int STEP_PUMP_KH_COMPLETED 		= 7 ;
	public static final int STEP_PUMP_KH_CONTINUING		= 6 ;
	// 주유기 초기화 Step
	public static final int STEP_PUMP_RESOLVED			= 1 ;
	public static final int STEP_PUMP_RUNNING			= 8 ;
	public static final int STEP_PUMP_START				= 2 ;
	public static final String STOP 		= "STOP" ;

	public static final String TH_COMMAND_ES = "ES";
	// tatsuno_hs okdhp7 (2013.02) - 신형다쓰노(현성) PROTOCOL
	public static final String TH_COMMAND_GD = "GD";
	public static final String TH_COMMAND_IQ = "IQ";
	public static final String TH_COMMAND_JP = "JP";
	public static final String TH_COMMAND_KA = "KA";
	public static final String TH_COMMAND_KB = "KB";
	public static final String TH_COMMAND_KC = "KC";
	public static final String TH_COMMAND_KD = "KD";
	public static final String TH_COMMAND_KE = "KE";
	public static final String TH_COMMAND_KF = "KF";
	public static final String TH_COMMAND_KG = "KG";
	public static final String TH_COMMAND_KI = "KI";
	public static final String TH_COMMAND_KJ = "KJ";
	public static final String TH_COMMAND_KK = "KK";
	public static final String TH_COMMAND_KU = "KU";
	public static final String TH_COMMAND_KV = "KV";
	public static final String TH_COMMAND_KX = "KX";
	public static final String TH_COMMAND_KY = "KY";
	public static final String TH_COMMAND_LA = "LA";
	public static final String TH_COMMAND_LD = "LD";
	public static final String TH_COMMAND_LT = "LT";
	public static final String TH_COMMAND_NG = "NG";
	public static final String TH_COMMAND_NR = "NR";
	public static final String TH_COMMAND_OK = "OK";
	public static final String TH_COMMAND_OS = "OS";
	public static final String TH_COMMAND_OT = "OT";
	public static final String TH_COMMAND_PS = "PS";
	public static final String TH_COMMAND_SP = "SP";
	public static final String TH_COMMAND_YL = "YL";
}

