package com.gsc.kixxhub.device.pumpa.controller;

import java.util.Hashtable;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.FAIL_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.AdaptorServiceImp;
import com.gsc.kixxhub.device.pumpa.define.Processor;
import com.gsc.kixxhub.device.pumpa.processor.P101_ODTProcess;
import com.gsc.kixxhub.device.pumpa.processor.P101_Process;
import com.gsc.kixxhub.device.pumpa.processor.P102_Process;
import com.gsc.kixxhub.device.pumpa.processor.P201_Process;
import com.gsc.kixxhub.device.pumpa.processor.P202_Process;
import com.gsc.kixxhub.device.pumpa.processor.P203_Process;
import com.gsc.kixxhub.device.pumpa.processor.P204_Process;
import com.gsc.kixxhub.device.pumpa.processor.P205_Process;
import com.gsc.kixxhub.device.pumpa.processor.P206_Process;
import com.gsc.kixxhub.device.pumpa.processor.P207_Process;
import com.gsc.kixxhub.device.pumpa.processor.P208_Process;
import com.gsc.kixxhub.device.pumpa.processor.P209_Process;
import com.gsc.kixxhub.device.pumpa.processor.P210_Process;
import com.gsc.kixxhub.device.pumpa.processor.P211_Process;
import com.gsc.kixxhub.device.pumpa.processor.P212_Process;
import com.gsc.kixxhub.device.pumpa.processor.P213_Process;
import com.gsc.kixxhub.device.pumpa.processor.P214_Process;
import com.gsc.kixxhub.device.pumpa.processor.P216_Process;
import com.gsc.kixxhub.device.pumpa.processor.P217_Process;
import com.gsc.kixxhub.device.pumpa.processor.P218_Process;
import com.gsc.kixxhub.device.pumpa.processor.P219_Process;
import com.gsc.kixxhub.device.pumpa.processor.P220_Process;
import com.gsc.kixxhub.device.pumpa.processor.P221_Process;
import com.gsc.kixxhub.device.pumpa.processor.P222_Process;
import com.gsc.kixxhub.device.pumpa.processor.P223_Process;
import com.gsc.kixxhub.device.pumpa.processor.P224_Process;
import com.gsc.kixxhub.device.pumpa.processor.P225_Process;
import com.gsc.kixxhub.device.pumpa.processor.P226_Process;
import com.gsc.kixxhub.device.pumpa.processor.P227_Process;
import com.gsc.kixxhub.device.pumpa.processor.P301_Process;
import com.gsc.kixxhub.device.pumpa.processor.P302_Process;
import com.gsc.kixxhub.device.pumpa.processor.P401_Process;
import com.gsc.kixxhub.device.pumpa.processor.P402_Process;
import com.gsc.kixxhub.device.pumpa.processor.P501_Process;
import com.gsc.kixxhub.device.pumpa.processor.P502_Process;
import com.gsc.kixxhub.device.pumpa.processor.P503_Process;
import com.gsc.kixxhub.device.pumpa.processor.P504_Process;
import com.gsc.kixxhub.device.pumpa.processor.P505_Process;
import com.gsc.kixxhub.device.pumpa.processor.P506_Process;
import com.gsc.kixxhub.device.pumpa.processor.P507_Process;
import com.gsc.kixxhub.device.pumpa.processor.P510_Process;
import com.gsc.kixxhub.device.pumpa.processor.P511_Process;
import com.gsc.kixxhub.device.pumpa.processor.P512_Process;
import com.gsc.kixxhub.device.pumpa.processor.P513_Process;

/*
 * Process를 새로 생성하는 전문이 두번 중복되는 경우. 
 * -> Process를 생성하는 최초의 요청 전문을 수신하였을 때 Table에서 해당 Nozzle의 Process가 있는지 체크하여 Process 생성.  
 * 하나의 Process가 응답을 기다리는 상황에서 응답이 필요없는 전문을 받는다면 새로운 Process가 생성되어 처리 가능.
 * -> isContainsKey를 확인하여 Process를 생성.
 * Process가 지워지지 않고 남아있는 경우
 * -> 요청과 응답의 구조를 갖는 Process의 TimerTask와 ProcessSelector.removeProcess() 확인.
 */


/**
 * 각 Process에 맞는 송수신 전문 순서의 유효성을 판단한다.
 * Process 순서에 올바르지 않는 전문을 수신할 경우 그 전문의 흐름을 멈춘다.
 * 요청과 응답으로 구성되어 있는 Process의 경우 Timeout 기능을 사용하여 자신의 Process를 삭제할 수 있다.
 * 각 Process는 고유의 기능을 가질 수 있지만 공통적인 기능은 다음과 같다.
 * 1. Process가 현재 처리해야할 순서의 WorkingMessage Command를 구분.
 * 2. WorkingMessage를 AdaptorServiceImp로 전송.
 * 3. WorkingMessage를 DeviceSelector로 전송.
 *
 * @author yd
 */
public class ProcessSelector {
	/**
	 * processTable에 해당 nozzle의 process가 있는지 여부.
	 */
	private static boolean isContainsKey = false;
	/**
	 * 현재 진행중인 Procee 관리. 노즐번호를 Key로 사용한다.
	 */
	private static Hashtable<String, Processor> processTable = 
									new Hashtable<String, Processor>();
	/**
	 * AdaptorServiceImp에서 setSelector()를 호출함으로써 DeviceSelector를 정의한다.
	 */
	private static DeviceSelector selector;
	
	
	
	/**
	 * Process Table의 크기 출력. 테스트 용.
	 * 
	 * @throws Exception
	 */
	public static void checkProcess() throws Exception {
		LogUtility.getPumpALogger().debug(new StringBuffer("ProcessTable Size : ").append( processTable.size()).toString());
		
	}	// end putProcess
	
	

	/**
	 * 주어진 WorkingMessage에 적당한 Processor를 리턴한다. 
	 * 
	 * COMMAND 	: 	PROCESSOR ID
	 * 
 	 *	D0		:	101
	 *	BA		:	211
	 *	P1, P2, P3, P4, P5, P6, P7	:	101
	 *	PA		:	201
	 *	S3		:	202
	 *	S4		:	203, 210
	 *	HF, QF	:	204
	 *	PB		:	205
	 *	HB, HD	:	206
	 *	HA, HC, QD	:	207
	 *	HE, QM	:	208
	 *	QL, TR	:	220
	 *	PF, PG, S9 	:	209
	 *	PI, SB	:	210
	 *	BB		:	210, 211
	 *	PP, SF	:	213
	 *	CP, TD	:	217
	 *	P9		:	301
	 *	S5		:	301, 302
	 *	P8		:	302
	 *	PE, S8	:	402
	 *	SE		:	401
	 *	SG, PQ	:	218
	 *	SH		:	210, 216
	 *	SI, SJ	:	214
	 *	ST		:	219
	 *	SY		:	102
	 *	TJ, XA	:	212
	 *  PM		:	501
	 *	GA		:	502
	 *	GB		:	503
	 *	GT		:	504
	 *	BR		:	505
	 *	BS		:	506 
	 *  F0, EX	:	없음(Pump Module로 전송하지 않음)
	 *  PU      :   513
	 * @return Processor
	 * @throws Exception 
	 */
	private static Processor getProcessor(WorkingMessage workingMessage, String nozzleNo, String command) throws Exception{
		Processor returnProcessor = null;
		isContainsKey = processTable.containsKey(nozzleNo);
		
		if (isContainsKey) {
			returnProcessor = processTable.get(nozzleNo);
			
		} else {
			// Module로부터 FAIL 수신 시 Process 삭제 
			if (command.equals(IPumpConstant.COMMANDID_FAIL)) {
				LogUtility.getPumpALogger().debug(
						new StringBuffer("# [ProcessSelector V2]  #")
						.append("ProcessSelector에서 FailWorkingMessage 수신 # ")
						.append("FAIL COMMAND : ")
						.append(((FAIL_WorkingMessage)workingMessage).getFailCommand()).append(" # ")
						.append("NozzleNo : ").append( nozzleNo).append(" # ") 
						.append(processTable.get(nozzleNo).getProcessID()).append(" 삭제 #").toString());
				removeProcess(nozzleNo);
				// 소모셀프 카드결제 승인처리(207) 
			} else if (command.equals(IPumpConstant.COMMANDID_HA)){
				returnProcessor = new P207_Process();
				
				// 소모셀프 외상거래 승인처리(206) 
			} else if (command.equals(IPumpConstant.COMMANDID_HB)){
				returnProcessor = new P206_Process();
				
				// 다쓰노셀프 카드결제 승인처리(208) 
			} else if (command.equals(IPumpConstant.COMMANDID_HE)){
				returnProcessor = new P208_Process();
				
				// 주유기 / 충전기 상태전송 (402) 
			} else if (command.equals(IPumpConstant.COMMANDID_S8)){
				returnProcessor = new P402_Process();
								
				// 주유시작자료 요청/응답(214)
			} else if (command.equals(IPumpConstant.COMMANDID_SI)){
				returnProcessor = new P214_Process();
				
				// 주유시작자료 요청/응답(214)
			} else if (command.equals(IPumpConstant.COMMANDID_SJ)){
				returnProcessor = new P214_Process();
				
				// 판매완료자료 요청/응답(219)
			} else if (command.equals(IPumpConstant.COMMANDID_ST)) {
				returnProcessor = new P219_Process();
												
				// 토털게이지 자료 요청(302) 
			} else if (command.equals(IPumpConstant.COMMANDID_P8)){
				returnProcessor = new P302_Process();
				
				// Preset 자료 요청(204)	
			} else if (command.equals(IPumpConstant.COMMANDID_HF)){
				returnProcessor = new P204_Process();
				
				// 노즐 제어명령(201) 
			} else if (command.equals(IPumpConstant.COMMANDID_PA)){
				returnProcessor = new P201_Process();
				
				// 정액/정량 설정(205) 
			} else if (command.equals(IPumpConstant.COMMANDID_PB)){
				returnProcessor = new P205_Process();
				
				// 주유기 / 충전기 상태전송(402)
			} else if (command.equals(IPumpConstant.COMMANDID_PE)){
				returnProcessor = new P402_Process();
				
				// Preset 자료 요청 (204)
			} else if (command.equals(IPumpConstant.COMMANDID_QF)){
				returnProcessor = new P204_Process();
				
				// 다쓰노셀프 판매완료(220)
			} else if (command.equals(IPumpConstant.COMMANDID_QL)){
				returnProcessor = new P220_Process();
				
				// 주유 / 충전 중 자료 전송 (202)
			} else if (command.equals(IPumpConstant.COMMANDID_S3)){
				returnProcessor = new P202_Process();
				
				// 주유완료 자료 전송 (203)
				// 충전기 카드결제 승인처리(210)
			} else if (command.equals(IPumpConstant.COMMANDID_S4)){
				returnProcessor = new P203_Process();
				
		
				// 환경정보 설정(101) 
			} else if (command.equals(IPumpConstant.COMMANDID_D0)) {
				nozzleNo = "temp";
				returnProcessor = new P101_ODTProcess();
				
				// 충전기 ODT 보너스카드 점수 누적처리(211)
			} else if (command.equals(IPumpConstant.COMMANDID_BA)) {
				returnProcessor = new P211_Process();			
				
				// 환경정보 설정(101)
			} else if (command.equals(IPumpConstant.COMMANDID_P1)){
				nozzleNo = "temp";
				returnProcessor = new P101_Process();
				
				// 환경정보 설정(101)
			} else if (command.equals(IPumpConstant.COMMANDID_P2)){
				nozzleNo = "temp";
				returnProcessor = new P101_Process();
				
				// 환경정보 설정(101)
			} else if (command.equals(IPumpConstant.COMMANDID_P3)){
				nozzleNo = "temp";
				returnProcessor = new P101_Process();
				
				// 환경정보 설정(101)
			} else if (command.equals(IPumpConstant.COMMANDID_P5)){
				nozzleNo = "temp";
				returnProcessor = new P101_Process();
				
				// 환경정보 설정(101)
			} else if (command.equals(IPumpConstant.COMMANDID_P6)){
				nozzleNo = "temp";
				returnProcessor = new P101_Process();
				
				// 환경정보 설정(101)
			} else if (command.equals(IPumpConstant.COMMANDID_P7)){
				nozzleNo = "temp";
				returnProcessor = new P101_Process();			
				
				// 환경정보 설정(101)
			} else if (command.equals(IPumpConstant.COMMANDID_PC)){ 
				returnProcessor = new P101_Process();
				// 영업 마감 처리(301)
			} else if (command.equals(IPumpConstant.COMMANDID_P9)){
				returnProcessor = new P301_Process();
				
				// 충전기 고객카드 승인처리 (209)
			} else if (command.equals(IPumpConstant.COMMANDID_S9)){
				returnProcessor = new P209_Process();
				
				// 충전기 카드결제 승인처리(210) 
			} else if (command.equals(IPumpConstant.COMMANDID_SB)){
				returnProcessor = new P210_Process();
				
				// 주유 디바이스 이상정보 전송(401) 
			} else if (command.equals(IPumpConstant.COMMANDID_SE)){
				returnProcessor = new P401_Process();
				
				// 충전기 보관증 발급  처리(213)
			} else if (command.equals(IPumpConstant.COMMANDID_SF)){
				returnProcessor = new P213_Process();
				
				// 충전기 보관증  조회 처리(218) 
			} else if (command.equals(IPumpConstant.COMMANDID_SG)){
				//returnProcessor = processTable.get(nozzleNo);
				returnProcessor = new P218_Process();
				
				// 충전기 카드결제 승인처리(210)
				// 충전기 현금결제(216)
			} else if (command.equals(IPumpConstant.COMMANDID_SH)){
				returnProcessor = new P216_Process();
			
				// 노즐 사용여부 설정(102) 
			} else if (command.equals(IPumpConstant.COMMANDID_SY)){
				returnProcessor = new P102_Process();
				
				// 충전기 보너스 조회(217)
			} else if (command.equals(IPumpConstant.COMMANDID_TD)) {
				returnProcessor = new P217_Process();
				
				// 충전기 ODT 현금영수증 승인처리(212)
			} else if (command.equals(IPumpConstant.COMMANDID_TJ)){
				returnProcessor = new P212_Process();
				
			} else if (command.equals(IPumpConstant.COMMANDID_CA)){
				returnProcessor = new P221_Process();
				
			} else if (command.equals(IPumpConstant.COMMANDID_BI)){
				returnProcessor = new P222_Process();
				
			} else if (command.equals(IPumpConstant.COMMANDID_BC)){
				returnProcessor = new P223_Process();
				
			} else if (command.equals(IPumpConstant.COMMANDID_E0)){
				returnProcessor = new P224_Process();

			} else if (command.equals(IPumpConstant.COMMANDID_CL)){ // 취소처리(버턴누름)
				returnProcessor = new P225_Process();
								
				// 충전기  전문 추가 시작
				/* 프로젝트명 :PI2
				 * 변경내용 : SK, SL 전문 처리 기능추가 
				 * 변경일자 : 2015.11.25
				 * 변경자 : 양일준*/	
			// 충전기 ODT 승인 요청 
			} else if (command.equals(IPumpConstant.COMMANDID_SK)){
					returnProcessor = new P226_Process();
					
            // 충전기 ODT 승인 응답 		
			} else if (command.equals(IPumpConstant.COMMANDID_SL)){
				returnProcessor = new P227_Process();		
			//표준 셀프 전문 추가 끝
				
			//표준 셀프 전문 추가 시작
				/* 프로젝트명 :PI2
				 * 변경내용 : 표준셀프 PM, PV, GA, GR, GL, GT, FC,BR,BS 전문 처리 기능추가 
				 * 변경일자 : 2015.11.25
				 * 변경자 : 정혜정*/	
			// ODT 모드정보 전송(501)
			} else if (command.equals(IPumpConstant.COMMANDID_PM)){
				returnProcessor = new P501_Process();
			
				// ODT 버전정보 전송
			} else if (command.equals(IPumpConstant.COMMANDID_PV)){
				returnProcessor = new P502_Process();
			
				// 결제승인 요청
			} else if (command.equals(IPumpConstant.COMMANDID_GA)){
				returnProcessor = new P503_Process();
				
				// 결제승인 요청 응
			} else if (command.equals(IPumpConstant.COMMANDID_GB)){
				returnProcessor = new P510_Process();
				
				// ODT 결재수단 입력요청
			} else if (command.equals(IPumpConstant.COMMANDID_GR)){
				returnProcessor = new P504_Process();
				
				// 영수증 출력처리
			} else if (command.equals(IPumpConstant.COMMANDID_GL)){ 
				returnProcessor = new P505_Process();
				
				// 판매완료 처리 (영수증 출력 후 전송)
			} else if (command.equals(IPumpConstant.COMMANDID_GT)){ 
				returnProcessor = new P506_Process();
				
				// 가득주유 통제
			} else if (command.equals(IPumpConstant.COMMANDID_FC)){ 
				returnProcessor = new P507_Process();
				
				// 세차권 바코드 요청 
			} else if (command.equals(IPumpConstant.COMMANDID_BR)){ 
				returnProcessor = new P511_Process();
				
				// 세차권 바코드 요청에 대한 응답 
			} else if (command.equals(IPumpConstant.COMMANDID_BS)){ 
				returnProcessor = new P512_Process();
				
				// ODT P/G Install/Update 요청
			} else if (command.equals(IPumpConstant.COMMANDID_PU)){ 
				returnProcessor = new P513_Process();
				
			}
				// 표준 셀프 전문 추가 끝
			else {
				LogUtility.getPumpALogger().error(new StringBuffer("처리 할 수 없는 WorkingMessage 수신. ").append(	"Command : ").append( command).toString());
				
			}	// end inner if
			
		}	// end if
				
		return returnProcessor;
		
	}	// end getProcessor

	
	
	/**
	 * 현재 설정된 DeviceSelector를 얻는다. 
	 * 
	 * @return
	 */
	public static DeviceSelector getSelector() throws Exception {
		return selector;
		
	}	// end getSelector
	
	
	
	
	/**
	 * ProcessTable에 새로운 Process를 추가한다.
	 * 
	 * @param nozzleNo	: 노즐번호 
	 * @param process	: Process
	 */
	public static void putProcess(String nozzleNo, Processor process) throws Exception {
		// 기존 Process가 있다면 대체한다.
		if (processTable.containsKey(nozzleNo)) {
			processTable.remove(nozzleNo);
			
		}	// end if
		
		processTable.put(nozzleNo, process);

//		LogUtility.getPumpALogger().debug("putProcess() -> ProcessTable Size : ").append( processTable.size());

	}	// end putProcess
	
	
	
	/**
	 * ProcessTable의 특정 Process를 제거한다.
	 * 
	 * @param nozzleNo	: 제거하고자 하는 Process의 노즐번호 
	 */
	public static void removeProcess(String nozzleNo) throws Exception {
		if (processTable.containsKey(nozzleNo)) {
			processTable.remove(nozzleNo);
			
		} else {
			LogUtility.getPumpALogger().error(
					"RemoveProcess fail!!");
			
		}	// end if
		
//		LogUtility.getPumpALogger().debug("removeProcess() -> ProcessTable Size : ").append( processTable.size());
	}	// end removeProcess
	

	
	/**
	 * WorkingMessage에 적당한 Process를 찾아 해당 Process를 수행시킨다.
	 * 
	 * @param workingMessage	: WorkingMessage
	 */
	public static void selectProcess (WorkingMessage workingMessage) throws Exception {
		Processor process = null;

		String nozzleNo = workingMessage.getNozzleNo();
		String command = workingMessage.getCommand();
		
		// 해당 WorkingMessage의 Processor를 검색.
		process = getProcessor(workingMessage, nozzleNo, command);
		
		if (process != null) {
			startProcess(process, workingMessage, nozzleNo, command);
			
		} else {
			LogUtility.getPumpALogger().error(
					new StringBuffer("[ProcessSelector V2] Process = null. ")
					.append(" # Command  : ").append( command) 
					.append(" # NozzleNo : ").append( nozzleNo)
					.append(" # isContainsKey : ").append( isContainsKey).toString());
			
		}	// end if
		
	}	// end selectProcess
	
	
	
	/**
	 * DeviceSelector를 설정한다.
	 * 
	 * @param selector	: DeviceSelector
	 */
	public static void setSelector(DeviceSelector selector) throws Exception {
		ProcessSelector.selector = selector;
		
	}	// end setSelector
	

	
	/**
	 * 주어진 Processor를 start한다.
	 * 
	 * @param process
	 * @param workingMessage
	 * @param nozzleNo
	 * @param command
	 * @throws Exception
	 */
	private static void startProcess(Processor process, 
									 WorkingMessage workingMessage, 
									 String nozzleNo, 
									 String command) throws Exception 
	{
		String direction = workingMessage.getDirection();
		
		// 주어진 WorkingMessage가 해당 Process에서 현재 필요로 하는 메세지인지 확인
		if (process.canContinue(workingMessage)) {
			process.startProcess(workingMessage);
		} else {
			LogUtility.getPumpALogger().error(
					new StringBuffer("[ProcessSelector V2]Process 순서와 맞지 않는 Command 수신. ")
					.append("Process  : ").append( process.getProcessID())
					.append(" # Command  : ").append( command)
					.append(" # NozzleNo : ").append( nozzleNo).toString());
			
			// PumpModule로 부터 송신된 WorkingMessage라면, FAIL을 전송한다.
			if (direction.equals(IPumpConstant.DIRECTION_FROM_MODULE)) {
				FAIL_WorkingMessage failMessage = new FAIL_WorkingMessage();
				failMessage.setNozzleNo(nozzleNo);
				failMessage.setFailCommand(command);
				
				AdaptorServiceImp.sendDeviceMsg(failMessage);
				
			}	// end inner if
			
		}	// end if	
		
	}	// end startProcess


	
	private ProcessSelector(){
		
	}

}
