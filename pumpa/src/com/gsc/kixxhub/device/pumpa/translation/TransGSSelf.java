package com.gsc.kixxhub.device.pumpa.translation;

import java.util.Iterator;
import java.util.Vector;

import com.gsc.kixxhub.common.data.IPumpConstant;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.GlobalUtility;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Command;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;
import com.gsc.kixxhub.device.pumpa.datas.E0_WorkingMessage;

/**
 * @author yd
 * 
 * Self 표준 프로토콜
 *
 */
public class TransGSSelf extends Translation {
	
	private final String DEFAULT_SEQUENCE = "1";
	/**
	 * 공통정보
	 */
	private final String GROUP_01 = "01";
	/**
	 * 결제방법 정보
	 */
	private final String GROUP_02 = "02";
	/**
	 * 금액/리터 정보
	 */
	private final String GROUP_03 = "03";
	/**
	 * 노즐정보
	 */
	private final String GROUP_04 = "04";
	/**
	 * 보너스카드 정보
	 */
	private final String GROUP_06 = "06";
	/**
	 * 현금영수증카드 정보
	 */
	private final String GROUP_07 = "07";
	/**
	 * 캠페인 정보
	 */
	private final String GROUP_08 = "08";
	/**
	 * 동영상 정보
	 */
	private final String GROUP_21 = "21";
	
	/**
	 * 히스토리뷰 정보
	 */
	private final String GROUP_22 = "22";
	
	

	public byte[] generateByteStream(WorkingMessage workingMessage) throws Exception {
		byte[] returnMessage = null;
		
		String workingMessageCommand = workingMessage.getCommand();
		
		if (workingMessageCommand.equals(IPumpConstant.COMMANDID_E0)) {
			// WorkingMessage E0 : 환경설정 정보  
			// 표준 E0	 		 : 환경설정 정보 
			E0_WorkingMessage e0WorkingMessage = (E0_WorkingMessage) workingMessage;
			String group = e0WorkingMessage.getGroup();
			Vector<Object> e0DataVector = e0WorkingMessage.getDatas();
			String e0DataVectorSize 	= String.valueOf(e0DataVector.size());
			Iterator<Object> iterator 			= e0DataVector.iterator();
			int iteratorCounter 		= 0;
			
			DataStruct e0Ds = new DataStruct();
			
			// Command
			e0Ds.addString("command", IPumpConstant.COMMANDID_E0, 2);
			// Sequence
			e0Ds.addString("sequence", DEFAULT_SEQUENCE, 1);
			// Group
			e0Ds.addString("group", group, 2);
			// 데이터 개수 
			e0Ds.addString("dataSize", 
					GlobalUtility.appending0Pre(e0DataVectorSize, 2), 
					2);
			
			// 환경설정 정보 
			// GROUP 01 : 공통 정보 
			if (group.equals(GROUP_01)) {
				// 매장명
				e0Ds.addString("storeName", 
						(String) iterator.next(), 
						20);
				// 인사말
				e0Ds.addString("introduce", 
						(String) iterator.next(), 
						50);
				// 공지사항 
				e0Ds.addString("notice", 
						(String) iterator.next(), 
						200);
				// 매장코드
				e0Ds.addString("storeCode", 
						(String) iterator.next(), 
						10);
				// 사업자 번호
				e0Ds.addString("regiNo", 
						(String) iterator.next(), 
						12);
				// 상호
				e0Ds.addString("corpName", 
						(String) iterator.next(), 
						100);
				// 대표자명
				e0Ds.addString("repName", 
						(String) iterator.next(), 
						50);
				// 사업장 우편번호
				e0Ds.addString("zipCode", 
						(String) iterator.next(), 
						7);
				// 사업장 주소1
				e0Ds.addString("address1", 
						(String) iterator.next(), 
						100);
				// 사업장 주소 2
				e0Ds.addString("address2", 
						(String) iterator.next(), 
						100);
				// 전화번호
				e0Ds.addString("tel", 
						(String) iterator.next(), 
						20);
				// 기본 머리말
				e0Ds.addString("headPrint", 
						(String) iterator.next(), 
						50);
				// 기본 꼬리말 1
				e0Ds.addString("footPrint1", 
						(String) iterator.next(), 
						50);
				// 기본 꼬리말 2
				e0Ds.addString("footPrint2", 
						(String) iterator.next(), 
						50);
				// 정액/정량 멈춤 사용여부
				e0Ds.addString("presetStop", 
						(String) iterator.next(), 
						1);
				
				// GROUP 02 : 결제방법 정보 
			} else if (group.equals(GROUP_02)) {
				while (iterator.hasNext()) {
					// ID
					e0Ds.addString("id" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// 구분 코드
					e0Ds.addString("code" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// 결제수단 명 
					e0Ds.addString("method" + iteratorCounter, 
							(String) iterator.next(), 
							12);
					
					iteratorCounter++;
					
				}	// end while
				
			// GROUP 04 : 노즐 정보 
			} else if (group.equals(GROUP_04)) {
				while (iterator.hasNext()) {
					// ID
					e0Ds.addString("id" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// 노즐번호
					e0Ds.addString("nozzleNo" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// 유종코드
					e0Ds.addString("productCode" + iteratorCounter, 
							(String) iterator.next(), 
							4);
					// 유종명
					e0Ds.addString("productName" + iteratorCounter, 
							(String) iterator.next(), 
							14);
					// 단가 
					e0Ds.addString("basePrice" + iteratorCounter, 
							(String) iterator.next(), 
							6);
					
					iteratorCounter++;
					
				}	// end while
			
				
			// GROUP 06 : 보너스카드 정보 
			} else if (group.equals(GROUP_06)) {
				while (iterator.hasNext()) {
					// ID
					e0Ds.addString("id" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// 구분 코드
					e0Ds.addString("code" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// 카드 명 
					e0Ds.addString("cardName" + iteratorCounter, 
							(String) iterator.next(), 
							12);
					
					iteratorCounter++;
					
				}	// end while
			
			// GROUP 07 : 현금영수증카드 정보 
			} else if (group.equals(GROUP_07)) {
				while (iterator.hasNext()) {
					// ID
					e0Ds.addString("id" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// 구분 코드
					e0Ds.addString("code" + iteratorCounter, 
							(String) iterator.next(), 
							4);
					// 카드 명 
					e0Ds.addString("cardName" + iteratorCounter, 
							(String) iterator.next(), 
							12);
					
					iteratorCounter++;
					
				}	// end while
				
			// GROUP 08 : 캠페인 정보
			} else if (group.equals(GROUP_08)) {
				while (iterator.hasNext()) {
					// ID
					e0Ds.addString("id" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// 구분 코드
					e0Ds.addString("code" + iteratorCounter, 
							(String) iterator.next(), 
							4);
					// 캡페인 명 
					e0Ds.addString("campaignName" + iteratorCounter, 
							(String) iterator.next(), 
							12);
					
					iteratorCounter++;
					
				}	// end while
				
			// GROUP 21 : 동영상 정보
			} else if (group.equals(GROUP_21)) {
				while (iterator.hasNext()) {
					// ID
					e0Ds.addString("id" + iteratorCounter, 
							(String) iterator.next(), 
							2);
					// 구분 코드
					e0Ds.addString("code" + iteratorCounter, 
							(String) iterator.next(), 
							4);
					// 동영상 명 
					e0Ds.addString("movieName" + iteratorCounter, 
							(String) iterator.next(), 
							12);
					// 파일 크기
					e0Ds.addString("fileSize", 
							(String) iterator.next(), 
							6);
					
					// 동영상 데이터
					String movieData = (String) iterator.next();
					e0Ds.addString("movieData", 
							movieData, 
							movieData.length());
					
					iteratorCounter++;
					
				}	// end while
				
			// GROUP 22 : 히스토리뷰 정보
			} else if (group.equals(GROUP_22)) {
				// ID
				e0Ds.addString("id", 
						(String) iterator.next(), 
						2);
				// 히스토리 화면명
				e0Ds.addString("historyName", 
						(String) iterator.next(), 
						100);
				
			}	// end if
			
			byte[] tempArray = e0Ds.getByteStream();
			
			returnMessage = this.makeProtocol(tempArray, 
								e0WorkingMessage.getNozzleNo());
			
		} else {
			LogUtility.getPumpALogger().error(
					"# [TransSelf] # " +
					"Not Supported command(" + 
					workingMessageCommand +
					"#");
			returnMessage = null;
		
		}	// end if
		
		// 로그 출력 
		if (returnMessage != null) {
			LogUtility.getPumpALogger().debug(
					workingMessageCommand + "WorkingMessage -> " +  
					"[" + new String(returnMessage) + "]"); 
			
		} else {
			LogUtility.getPumpALogger().debug(
					workingMessageCommand + "WorkingMessage -> " + "null");
		}	// end if
		
		return returnMessage;
		
	}	// end genearateByteArray
	
	
	
	public WorkingMessage generateWorkingMessage(byte[] message, 
													String command) throws Exception {
		WorkingMessage returnMessage = null;	
		
//		LogUtility.getPumpALogger().debug(this.printResultLog(message, returnMessage));
	
		return returnMessage;
	
	}	// end generateWorkingMessage
	


	/**
	 * byte 배열의 data를  완전한 소모 셀프 전문 형태로 변환
	 * 
	 * @param data		: data
	 * @param nozzleNo	: 노즐 번호 
	 * @return			: 소모 셀프 전문 
	 */
	private byte[] makeProtocol(byte[] data, String nozzleNo) throws Exception {
		if (data == null) {
			return null;
		}	// end if
		
		byte[] returnData 		= null;
		byte blank 				= ' ';
		int returnDataCounter 	= 0;
		byte nozzleByte 		= Byte.parseByte(nozzleNo);
		
		// STX, SA, UA, ETX, BCC 만큼의 길이를 더한다.
		int arrayLength = data.length + 5;
		
		returnData = new byte[arrayLength];
		
		returnData[returnDataCounter++] = Command.STX;					// STX
		returnData[returnDataCounter++] = (byte) (nozzleByte + 0x40);	// SA
		returnData[returnDataCounter++] = blank;						// UA
		
		for (int i = 0; i < data.length; i++) {
			returnData[returnDataCounter++] = data[i];
			
		}	// end for
		
		returnData[returnDataCounter++] = Command.ETX;					// ETX
		returnData[returnDataCounter++] = blank;						// BCC		
		
		return returnData;
		
	}	// end makeProtocol
	
}
