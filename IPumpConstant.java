package com.gsc.kixxhub.common.data;


public interface IPumpConstant {

	public static String CB_CUSTOMER_TYPE_CUS_CASH 			= "2"	;		//���� �ŷ�ó
	public static String CB_CUSTOMER_TYPE_CUS_FIX_AMT 		= "4"	;		//���� �Է� ��
	public static String CB_CUSTOMER_TYPE_CUS_OISANG 		= "3"	;		//�ܻ� �ŷ�ó
	public static String CB_CUSTOMER_TYPE_GENERAL 			= "1"	;		//�Ϲ� ��
	public static final String COMMANDID_ACK = "ACK" ;	// ������ - ���ݰ��� ����
	public static final String COMMANDID_AH = "AH" ;	// ǥ�ؼ��� - �ܸ��� ���� �� üũ ��û
	public static final String COMMANDID_AI = "AI" ;	// ǥ�ؼ��� - �ܸ��� ���� �� üũ ����
	/* �ű� COMMAND �߰� ���� ��*/
	
	public static final String COMMANDID_BA = "BA" ;	// ������ - ���ʽ� ���� ���� ��û
	public static final String COMMANDID_BB = "BB" ;	// ������ - ���ʽ� ���� ���� ����
	public static final String COMMANDID_BC = "BC" ;	// �پ��뼿�� - �������� ��ҿ�û
	/* 
	 * Command ID for Pump/ODT Device between Pump Module and Pump Adapter
	 */
	public static final String COMMANDID_BI = "BI" ;
	public static final String COMMANDID_BR = "BR" ;	// GSC���� - ������ ���ڵ� ��û      
	public static final String COMMANDID_BS = "BS" ;	// GSC���� - ������ ���ڵ� ��û  ����
	public static final String COMMANDID_CA = "CA" ;	// �پ��뼿�� - ������Ȯ�� ��û
	public static final String COMMANDID_CB = "CB" ;	// �پ��뼿�� - ������Ȯ�� ����
	public static final String COMMANDID_CL = "CL" ;
	public static final String COMMANDID_CP = "CP" ;	// ������ - ���ʽ� ���� ��ȸ ����
	public static final String COMMANDID_D0 = "D0" ;	// ������ - ȯ������ ����
	public static final String COMMANDID_D1 = "D1" ;
	public static final String COMMANDID_D2 = "D2" ;
	public static final String COMMANDID_D3 = "D3" ;
	public static final String COMMANDID_D4 = "D4" ;
	public static final String COMMANDID_D5 = "D5" ;
	public static final String COMMANDID_D6 = "D6" ;

	public static final String COMMANDID_D7 = "D7" ;
	public static final String COMMANDID_E0 = "E0" ;
	public static final String COMMANDID_EX = "EX" ;
	public static final String COMMANDID_F0 = "F0" ;	// ����/���� ����
	public static final String COMMANDID_FAIL = "FAIL" ;
	public static final String COMMANDID_FC = "FC" ;	// ǥ�ؼ��� - �������� ����
	/** ChoWonIk, PI2, 2015-10-28, �ű� ODT(GSC Self) �ű� �ڵ� �߰� */
	public static final String COMMANDID_GA = "GA" ;	// GSC���� - ī����� ���ο�û & ���    
	public static final String COMMANDID_GB = "GB" ;	// GSC���� - ī����� ��������      
	public static final String COMMANDID_GL = "GL" ;	// GSC���� - ������ ���ó�� ���� ��û      
	public static final String COMMANDID_GR = "GR" ;	// GSC���� - ��� ī��(�ű��߰�)
	public static final String COMMANDID_GT = "GT" ;	// GSC���� - �ǸſϷ� ����          
	public static final String COMMANDID_HA = "HA" ;	// �Ҹ��� - ���� ��û
	public static final String COMMANDID_HB = "HB" ;	// �Ҹ��� - �ܻ� ��û
	
	public static final String COMMANDID_HC = "HC" ;	// �Ҹ��� - ���� ����
	public static final String COMMANDID_HD = "HD" ;	// �Ҹ��� - �ܻ� ����
	public static final String COMMANDID_HE = "HE" ;	// �پ��뼿�� - ī����� ���ο�û
	public static final String COMMANDID_HF = "HF" ;	// Preset �ڷ� ����
	public static final String COMMANDID_HG = "HG" ;
	public static final String COMMANDID_M1 = "M1" ;	// ������ / ODT ������ ���� ����
	public static final String COMMANDID_NAK = "NAK" ;	// ������ - �������� �ʴ� ���
	public static final String COMMANDID_P1 = "P1" ;	// ���� ������ ����� ���� ����
	public static final String COMMANDID_P2 = "P2" ;	// ���� ������ �Ӹ���/������ ����
	public static final String COMMANDID_P3 = "P3" ;	// ������ ȯ�� ���� ����
	public static final String COMMANDID_P3_1 = "P3_1" ;
	public static final String COMMANDID_P5 = "P5" ;	// ODT ȯ������ ���� (����, ������)
	public static final String COMMANDID_P5_1 = "P5_1";
	public static final String COMMANDID_PC = "PC" ;	// ���� ȯ������ ����
	public static final String COMMANDID_P6 = "P6" ;	// ������ �� �ð� ����
	public static final String COMMANDID_P7 = "P7" ;	// ������ / ODT �Ķ���� ����
	public static final String COMMANDID_P8 = "P8" ;	// ������ - ODT ���а����� �ڷ� ��û
	public static final String COMMANDID_P9 = "P9" ;	// ������ - ODT �������� ó�� ��û	
	public static final String COMMANDID_PA = "PA" ;	// ���� ���� ��� (������, ����, ������)
	public static final String COMMANDID_PB = "PB" ;	// Preset ���� (������,����,������)
	public static final String COMMANDID_PE = "PE" ;	// ������/������ ���� ��û
	public static final String COMMANDID_PF = "PF" ;	// ������ - �ߺ������� �����ϴ� ���
	
	public static final String COMMANDID_PG = "PG" ;	// ������ - ��ī�� ���� ���� (�ߺ� ������ �ƴ� ���)
	public static final String COMMANDID_PI = "PI" ;	// ������ - ī����� ���� ����
	/*������Ʈ�� : PL2
	 * �������� : 2015.11.23
	 * ������� : ǥ�� ���� ���� �߰��� ���� ��� ó�� ���� �߰�
	 * ������ : ������
	 * */
	/* �ű� COMMAND �߰� ����*/
	public static final String COMMANDID_PM = "PM" ;	// ǥ�ؼ��� - ODT �����������
	public static final String COMMANDID_PP = "PP" ;	// ������ - ������ ��ȣ�ο� ����
	public static final String COMMANDID_PQ = "PQ" ;	// ������ - ������ ��ȸ ����
	public static final String COMMANDID_PT = "PT" ;
	public static final String COMMANDID_PU = "PU" ;	// ǥ�ؼ��� - ODT Update ��û
	public static final String COMMANDID_PV = "PV" ;	// ǥ�ؼ��� - ODT ������������

	public static final String COMMANDID_QD = "QD" ;	// �Ҹ��� - ���ʽ� ���� ����	
	public static final String COMMANDID_QF = "QF" ;	// Preset �ڷ� ��û
	public static final String COMMANDID_QL = "QL" ;	// �پ��뼿�� - ������ ���ó��
	public static final String COMMANDID_QM = "QM" ;	// �پ��뼿�� - ī����� ��������
	public static final String COMMANDID_S3 = "S3" ;	// ������ �ڷ� ���� (������, ����, ������)
	public static final String COMMANDID_S4 = "S4" ;	// �����Ϸ� ���� (������, ����)
	
	public static final String COMMANDID_S5 = "S5" ;	// ������ - ���а����� ����
	public static final String COMMANDID_S8 = "S8" ;	// ������/������ ���� ����
	public static final String COMMANDID_S9 = "S9" ;	// ������ - ��ī�� ���� ��û
	public static final String COMMANDID_SB = "SB" ;	// ������ - ī����� ���� ��û
	public static final String COMMANDID_SE = "SE" ;	// ���� ��� �̻����� ����
	public static final String COMMANDID_SF = "SF" ;	// ������ - ������ ��ȣ�ο� ��û
	public static final String COMMANDID_SG = "SG" ;	// ������ - ������ ��ȸ ��û
	
	public static final String COMMANDID_SH = "SH" ;	// ������ - ODT �����Ǹŵ����� ����
	public static final String COMMANDID_SI = "SI" ;	// ���� ���� �ڷ� ��û
	public static final String COMMANDID_SJ = "SJ" ;	// ���� ���� �ڷ� ����
	/*
	 * ���泻�� : ������ ODT �� ���� �������� ������ ���� �߰�
	 * �������� : 2016.03.28
	 * ������ : ������ 
	 */
	public static final String COMMANDID_SK = "SK" ;  // ������ ODT ���ο�û
	public static final String COMMANDID_SL = "SL" ;  // ������ ODT ��������
	
	public static final String COMMANDID_ST = "ST" ;	// �Ҹ��� - �ǸſϷ� ����
	public static final String COMMANDID_SY = "SY" ;	// ������ ��뿩�� ���� (������,����,������)
	
	public static final String COMMANDID_TD = "TD" ;	// ������ - ���ʽ� ���� ��ȸ ��û
	public static final String COMMANDID_TJ = "TJ" ;	// ������ - ���� ������ ��û
	public static final String COMMANDID_TR = "TR" ;	// �پ��뼿�� - �ǸſϷ� ���� 
	
	public static final String COMMANDID_XA = "XA" ;	// ������ - ���� ������ ����
	public static final String COMPLETED	= "COMPLETED" ;

	/*����������ݰ����ڵ�ȭ, 2017-04-11, ������ */
	public static final String COMMANDID_J0 = "J0" ;    // ���ܸ��忩�� ���� 
	public static final String COMMANDID_JA = "JA" ;    // ControlArk ��� ���� (pumpA->pumpM)
	public static final String COMMANDID_JB = "JB" ;	// ControlArk ��� ���� (pumpM->pumpA)
	public static final String COMMANDID_JC = "JC" ;	// ControlArk ��� ���� (pumpA->pumpM)
	public static final String COMMANDID_JD = "JD" ;	// ControlArk ��� ���� (pumpM->pumpA)
	public static final String COMMANDID_JZ = "JZ" ;	// ControlArk ARK ���� 
	
	public static final String COMMANDID_JK = "JK" ;	// ControlArk ��ҿ�û����
	public static final String COMMANDID_JI = "JI" ;	// ControlArk ������Ż
	public static final String COMMANDID_JQ = "JQ" ;	// ControlArk ������û
	
	public static final String COMMANDID_JF = "JF" ;	// ControlArk ��������
	public static final String COMMANDID_JG = "JG" ;	// ControlArk �����Ϸ�
	
	// ������ delimiter
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
	public static final int KH_ODT_TR_SH_ST_COMPLETED	= 6 ;	// �پ���, �Ҹ����� �����⿡�� ���ȴ�.
	public static final int KH_STATE_RESET				= 7 ;	// �ű� KH ó����ȣ�� �����ϰ� ���¸� Reset
	
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
	public static final int PRESET_FROM_PUMP		= 1 ;		// ��� ���� ����	
	public static final int PRESET_FROM_POS			= 2 ;	
	public static final int PRESET_FROM_ODT			= 3 ;		// ��� ���� ����
	public static final int PRESET_FROM_CAT			= 4 ;
	
	
	public static final String PUMP_KEEP_NUMBER		= "keepNum" ;
	public static final int PUMP_PROCESS_WAITTING_TIME_FROM_ADAPTOR = 40000;
	
	/*
	 * PUMP PROCESS���� ����ϴ� ��� �ð� 
	 */
	public static final int PUMP_PROCESS_WAITTING_TIME_FROM_MODULE 	= 20000;
	
	/*
	 * ������ Protocol ����
	 */
	public static final int PUMP_PROTOCOL_DEFAULT		= 0 ;
	public static final int PUMP_PROTOCOL_SOMO			= 1 ;
	public static final int PUMP_PROTOCOL_DaSNo			= 2 ;
	public static final int PUMP_PROTOCOL_Recharge		= 3 ;
	public static final int PUMP_PROTOCOL_GSC_SELF		= 4 ;  // TCP/IP ��Ź�� ����ODT(4) �߰�
	public static final int PUMP_PROTOCOL_NewDaSNo	    = 5 ;
	public static final int PUMP_PROTOCOL_NewRecharge   = 6 ;  // TCP/IP ��Ź�� ������ODT(6) �߰�, ������- 2016.03.28
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
	// ������ �ʱ�ȭ Step
	public static final int STEP_PUMP_RESOLVED			= 1 ;
	public static final int STEP_PUMP_RUNNING			= 8 ;
	public static final int STEP_PUMP_START				= 2 ;
	public static final String STOP 		= "STOP" ;

	public static final String TH_COMMAND_ES = "ES";
	// tatsuno_hs okdhp7 (2013.02) - �����پ���(����) PROTOCOL
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

