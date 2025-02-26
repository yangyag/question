package com.gsc.kixxhub.device.pumpa.common;
import java.util.Hashtable;
import java.util.Vector;

import com.gsc.kixxhub.common.utility.log.LogUtility;


public class DataStruct{ 
	/**
	 * 
	 */
	private Vector<String> keyVector 					= 
								new Vector<String>();	// Vector에는 이름만 저장
	/**
	 * 
	 */
	private Hashtable<String, Integer> stringSizeTable = 
								new Hashtable<String, Integer>();
	// Hashtable만 사용하는 경우는 저장되는 순서를 알 수 없으므로
	// 순서대로 저장되는 Vector와 함께 연동하여 사용한다
	/**
	 * 
	 */
	private Hashtable<String, Object> table 							= 
								new Hashtable<String, Object>();		// Hashtable에는 이름과 값 저장

	
	
	/**
	 * byte 타입 데이터 저장
	 * 
	 * @param key		: key 이름 
	 * @param value		: byte 타입 데이터 
	 */
	public void addByte(String key, byte value) throws Exception {
		table.put(key, value);
		keyVector.add(key);
		
	}	// end addByte
	
	
	
	/**
	 * char 타입 데이터 저장
	 * 
	 * @param key		: key 이름 
	 * @param value		: char 타입 데이터 
	 */
	public void addChar(String key, char value) throws Exception {
		table.put(key, value);
		keyVector.add(key);
		
	}	// end addChar
	
	
	
	/**
	 * int 타입 데이터 저장
	 * 
	 * @param key		: key 이름 
	 * @param value		: int 타입 데이터 
	 */
	public void addInt(String key, int value) throws Exception {
		table.put(key, value);
		keyVector.add(key);
		
	}	// end addInt
	
	
	
	/**
	 * short 타입 데이터 저장
	 * 
	 * @param key		: key 이름 
	 * @param value		: short 타입 데이터 
	 */
	public void addShort(String key, short value) throws Exception {
		table.put(key, value);
		keyVector.add(key);
		
	}	// end addShort
	
	
	
	/**
	 * String 타입 데이터 저장. 전체 길이를 따로 입력 받아 
	 * String 타입 데이터에 공백을 추가할 수 있다.
	 * 
	 * @param key		: key 이름
	 * @param value		: String 타입 데이터 
	 * @param size		: 전체 길이
	 */
	public void addString(String key, String value, int size) throws Exception {
		int valueLength = value.getBytes().length;
		
		// size는 value 길이보다 길어야 한다.
		// size가 value 길이보다 작다면 size 값을 value 길이로 설정한다.
		if (valueLength > size) {
			LogUtility.getPumpALogger().debug("### DataStruct Error : String = " + 
										value + ", size = " + size + " ###");
			size = valueLength;
		
		}	// end if
		
		table.put(key, value);
		stringSizeTable.put(key, size);
		keyVector.add(key);
		
	}	// end addString
	

	
	/**
	 * 프로젝트명 : PI2
	 * 추가내용 : 
	 * String 타입 데이터를 value 값 끝에 0x1C값을 덧붙여 저장. 
	 * 최대 길이(mLength)를  입력받아 입력된 데이터 길이(dLength)를 비교하여 
	 * 더 작은 길이로 size를 설정한다.
	 * 실제 데이터가 저장되는 공간은 dLength+1이며 	 
	 * 데이터가 최대길이보다 크면 최대길이초과데이터가 절삭후 저장된다.
	 * 변경일자 : 2016.03.15
	 * 변경자 : 정혜정
	 * 
	 * @param key		: key 이름
	 * @param value		: String 타입 데이터 
	 * @param size		: 데이터 영역중 key 항목이 가질수 있는 최대 길이
	 */
	public void addVString(String key, String value, int size) throws Exception {
		int valueLength = value.getBytes().length;
		
		// size와 비교하여 
		// size가 value 길이보다 작다면 size 값을 value 길이로 설정한다.
		if (valueLength > size) {
			
			LogUtility.getPumpALogger().debug("### DataStruct Error : String = " + 
					value + ", size = " + size + " ###");
			
			//size = valueLength;
			// 최대길이보다 데이터가 많이 입력될경우는 절삭한다.
			value = value.substring(0, size);
		}
		//데이터가 최대길이보다 작을 경우
		else
		{
			size = valueLength;			
		}
		
		// end if
		
		
		// 지정한 길이와 실제 들어온 길이를 비교하여 더짧은 값을 길이로 잡아주고 FS입력공간을 더 할당준다.
		size++;			
		
		char hex1C = (char)0x1C;
		// value에 FS(0x1C)를 덧붙인다.
		table.put(key, value+ hex1C);
		stringSizeTable.put(key, size);
		keyVector.add(key);
		
	}	// end addVString
	
	/**
	 * byte 타입 데이터 수정 
	 * 
	 * @param key		: key 이름 
	 * @param value		: byte 타입 데이터 
	 */
	public void editByte(String key, byte value) throws Exception {
		if (table.containsKey(key)) {
			table.remove(key);
			table.put(key, value);
			
		} else {
			LogUtility.getPumpALogger().debug("##### Can't edit Byte \"" + 
										 key + "\" in Data struct #####");
			
		}	// end if
		
	}	// end editByte
	
	
	/**
	 * 프로젝트명 : PI2
	 * 추가내용 : 
	 * String 타입 가변 데이터 수정 데이터 끝에 0x1C를 덧붙인다.
	 * @param key		: key 이름 
	 * @param value		: String 타입 데이터 
	 * @param size		: 전체 길이 
	 * 변경일자 : 2015.12.04
	 * 변경자 : 정혜정
	 */
	public void editVString(String key, String value, int size) throws Exception {
		if (table.containsKey(key) && stringSizeTable.containsKey(key)) {
			table.remove(key);
			stringSizeTable.remove(key);
			
			int valueLength = value.getBytes().length;
			
			// size와 비교하여 
			// size가 value 길이보다 작다면 size 값을 value 길이로 설정한다.
			if (valueLength > size) {
				LogUtility.getLogger().debug("### DataStruct Error : String = " + 
											value + ", size = " + size + " ###");
				value = value.substring(0, size);
			
			}
			else
			{
				size = valueLength;	
			}
			// end if
			
			// FS(0x1C) 덧붙인 자리를 늘려준다.
			size++;
			char hex1C = (char)0x1C;
			// value에 FS(0x1C)를 덧붙인다.
			table.put(key, value+ hex1C);
			stringSizeTable.put(key, size);
			
		} else {
			LogUtility.getLogger().debug("##### Can't edit String \"" + key + 
												"\" in Data struct #####");
			
		}	// end if
		
	}	// end editVString
	
	/**
	 * char 타입 데이터 수정 
	 * 
	 * @param key		: key 이름 
	 * @param value		: char 타입 데이터 
	 */
	public void editChar(String key, char value) throws Exception {
		if (table.containsKey(key)) {
			table.remove(key);
			table.put(key, value);
			
		} else {
			LogUtility.getPumpALogger().debug("##### Can't edit Char \"" + key + 
												"\" in Data struct #####");
			
		}	// end if
		
	}	// end editChar
	
	

	/**
	 * int 타입 데이터 수정 
	 * 
	 * @param key		: key 이름 
	 * @param value		: int 타입 데이터 
	 */
	public void editInt(String key, int value) throws Exception {
		if (table.containsKey(key)) {
			table.remove(key);
			table.put(key, value);
			
		} else {
			LogUtility.getPumpALogger().debug("##### Can't edit Int \"" + 
										 key + "\" in Data struct #####");
			
		}	// end if
		
	}	// end editInt
	
	
	/**
	 * short 타입 데이터 수정 
	 * 
	 * @param key		: key 이름 
	 * @param value		: short 타입 데이터 
	 */
	public void editShort(String key, short value) throws Exception {
		if (table.containsKey(key)) {
			table.remove(key);
			table.put(key, value);
			
		} else {
			LogUtility.getPumpALogger().debug("##### Can't edit Short \"" + key + 
										 "\" in Data struct #####");
			
		}	// end if
		
	}	// end editShort
	
	

	/**
	 * String 타입 데이터 수정 
	 * 
	 * @param key		: key 이름 
	 * @param value		: String 타입 데이터 
	 * @param size		: 전체 길이 
	 */
	public void editString(String key, String value, int size) throws Exception {
		if (table.containsKey(key) && stringSizeTable.containsKey(key)) {
			table.remove(key);
			stringSizeTable.remove(key);
			
			int valueLength = value.getBytes().length;
			
			// size는 value 길이보다 길어야 한다.
			// size가 value 길이보다 작다면 size 값을 value 길이로 설정한다.
			if (valueLength > size) {
				LogUtility.getPumpALogger().debug("### DataStruct Error : String = " + 
											value + ", size = " + size + " ###");
				size = valueLength;
			
			}	// end if
			
			table.put(key, value);
			stringSizeTable.put(key, size);
			
		} else {
			LogUtility.getPumpALogger().debug("##### Can't edit String \"" + key + 
												"\" in Data struct #####");
			
		}	// end if
		
	}	// end editString
	
	

	/**
	 * 현재 저장된 데이터를 byte 배열 타입으로 얻는다.
	 * 
	 * @return	: byte 배열 타입 데이터 
	 */
	public byte[] getByteStream() throws Exception {
		int arraySize 		= this.getLength();
		byte[] returnData 	= null;
		
		returnData = new byte[arraySize];
		int vectorCounter = 0;		// keyVector index
		
		// returnData 배열에 값 입력 
		// i = returnData index
		for (int i = 0; i < arraySize; i++){
			// 순서대로 데이터 읽기
			Object data = table.get(keyVector.get(vectorCounter));			

			if (data instanceof Byte) {
				returnData[i] = (Byte) data;
				
			// short = 2 byte
			} else if (data instanceof Short){
				short target 	= (Short) data;
				byte first   	= (byte) (target >> 8);
				byte second  	= (byte) target;
				returnData[i]   = first;
				returnData[++i] = second;
				
			// int = 4 byte
			} else if (data instanceof Integer){
				int target  	= (Integer) data;
				byte first  	= (byte) (target >> 24);
				byte second 	= (byte) (target >> 16);
				byte third  	= (byte) (target >> 8);
				byte forth  	= (byte) target;
				returnData[i]   = first;
				returnData[++i] = second;
				returnData[++i] = third;
				returnData[++i] = forth;
				
			// char = 2 byte
			} else if (data instanceof Character){
				char target 	= (Character) data;
				byte first  	= (byte) (target >> 8);
				byte second 	= (byte) target;
				returnData[i]   = first;
				returnData[++i] = second;
				
			} else if (data instanceof String){
				int totalSize = stringSizeTable.get(
												keyVector.get(vectorCounter));
				byte[] target = ((String)data).getBytes();
				int blankSize = totalSize - target.length;
				
				// data 채우기
				for (int j = 0; j < target.length; j++) {
					returnData[i] = target[j];
					i++;
					
				}	// end for
				
				// blank 채우기
				for (int j = 0; j < blankSize; j++) {
					returnData[i] = 32;
					i++;					
					
				}	// end for
				
				i--;
				
			} else {
				LogUtility.getPumpALogger().debug("###### Unknown instance ######");
				
			}	// end if
			
			vectorCounter++;
			
		}	// end for
		
		return returnData;
		
	}	// end getByteStream
	
	

	/**
	 * 현재 저장된 데이터의 배열 길이를 얻는다.
	 * 
	 * @return	: 배열 길이 
	 */
	private int getLength() throws Exception {
		int arraySize = 0;
		
		for (int i = 0; i < keyVector.size(); i++){
			Object data = table.get(keyVector.get(i));
			
			if (data instanceof Byte) {
				arraySize += 1;
				
			} else if (data instanceof Short){
				arraySize += 2;
				
			} else if (data instanceof Integer){
				arraySize += 4;
				
			} else if (data instanceof Character){
				arraySize += 2;
				
			} else if (data instanceof String){
				arraySize += stringSizeTable.get(keyVector.get(i));
				
			} else {
				LogUtility.getPumpALogger().debug("###### Unknown instance ######");
				
			}	// end if
			
		}	// end for
		
		return arraySize;
		
	}	// end getLength
	
	

	/**
	 * 주어진 key 이름으로 저장되어 있는 데이터를 얻는다.
	 * String 타입 데이터는 전체 길이와 상관없이 저장했던 데이터 길이만큼 얻는다.
	 * 
	 * @param key	: key 이름 
	 * @return		: Object 타입 데이터. 해당 데이터가 없을 경우 null 리턴.
	 */
	public Object getValue(String key) throws Exception {
		Object returnData = table.get(key);
		
		/*
		 * String 형태 데이터에 공백을 포함하여 데이터를 얻는다
		if (returnData instanceof String){
			String tempData = (String) returnData;
			int totalSize 	= (Integer) stringSizeTable.get(key);
			int blankSize 	= totalSize - tempData.getBytes().length;
			
			for (int i = 0; i < blankSize; i++) {
				tempData = tempData + " ";
				
			}	// end for
			
			returnData = tempData;
			
		}	// end if
		*/
		
		return returnData;
		
	}	// end getValue
	
	

	/**
	 * Vector, Hashtable 초기화
	 * 
	 */
	public void init() throws Exception {
		table.clear();
		stringSizeTable.clear();
		keyVector.clear();
		
	}	// end init
	
	

	/**
	 * byte 타입 공백 저장  
	 * 
	 * @param key		: key 이름 
	 */ 
	public void setByte(String key) throws Exception {
		table.put(key, (byte) 0x20);
		keyVector.add(key);
		
	}	// end setByte
	
	

	/**
	 * byte[] 형태 데이터를 입력한다.
	 * setByteStream() 후에 getValue()를 사용하고자 한다면 
	 * data 내용과 같은 순서로
	 * add 또는 set 관련 메소드를 먼저 수행해야 한다.
	 * data의 길이는 DataStruct 길이와 같거나 커야 한다.
	 * data 길이가 DataStruct 길이보다 큰 경우 DataStruct 길이 이후 데이터는 생략된다.
	 * 
	 * @param data
	 */
	public void setByteStream(byte[] data) throws Exception {
		// 임시로 저장하기 위한 Hashtable과 Vector. 
		// setBytes() 마지막에 기존 Hashtable과 Vector값으로 설정된다.
		Hashtable<String, Object> tempTable 			= new Hashtable<String, Object>();
		Hashtable<String, Integer> tempStringSizeTable 	= new Hashtable<String, Integer>();
		Vector<String> tempVector 		= new Vector<String>();
		
		byte[] currentData 	= this.getByteStream();
		int vectorCounter 	= 0;
		
			// i = data index
			for (int i = 0; i < currentData.length; i++){
				String key 			= keyVector.get(vectorCounter);
				Object tableData 	= table.get(key);
				
				if (tableData instanceof Byte) {
					tempTable.put(key, data[i]);
					tempVector.add(key);
					
				} else if (tableData instanceof Short){
					short tempData = (short) ((data[i] & 0xff) << 8 
											   | (data[++i] & 0xff));
					tempTable.put(key, tempData);
					tempVector.add(key);
					
				} else if (tableData instanceof Integer){
					int tempData = ((data[i] & 0xff)<<24 
									| (data[++i] & 0xff) <<16 
									| (data[++i] & 0xff) << 8 
									| (data[++i] & 0xff));
					tempTable.put(key, tempData);
					tempVector.add(key);
					
				} else if (tableData instanceof Character){
					char tempData = (char) (((char)data[i] & 0xff) << 8 
							   				| ((char)data[++i] & 0xff));
	
					tempTable.put(key, tempData);
					tempVector.add(key);
					
				} else if (tableData instanceof String){
					int stringSize 		= stringSizeTable.get(key);
					byte[] tempData 	= new byte[stringSize];
					
					for (int j = 0; j < stringSize; j++) {
						tempData[j] = data[i++];
						
					}	// end for
					
					i--;
					
					String stringValue 	= new String(tempData);
					stringValue 		= stringValue.trim();	// blank 제거
					
					tempTable.put(key, stringValue);
					tempStringSizeTable.put(key, stringSize);
					tempVector.add(key);
					
				} else {
					LogUtility.getPumpALogger().debug("### Unknown instance ###");
					
				}	// end inner if
				
				vectorCounter++;
				
			}	// end for
		
		table 			= tempTable;
		stringSizeTable = tempStringSizeTable;
		keyVector	 	= tempVector;
		
	}	// end setByteStream
	

	/**
	 * char 타입 공백 저장  
	 * 
	 * @param key		: key 이름 
	 */ 
	public void setChar(String key) throws Exception {
		table.put(key, ' ');
		keyVector.add(key);
		
	}	// end setChar
	
	
	/**
	 * int 타입 공백 저장  
	 * 
	 * @param key		: key 이름 
	 */ 
	public void setInt(String key) throws Exception {
		table.put(key, 0x20);
		keyVector.add(key);
		
	}	// end setInt
	
	
	
	/**
	 * short 타입 공백 저장  
	 * 
	 * @param key		: key 이름 
	 */ 
	public void setShort(String key) throws Exception {
		table.put(key, (short) 0x20);
		keyVector.add(key);
		
	}	// end setShort
	
	
	/**
	 * String 타입 공백 저장  
	 * 
	 * @param key		: key 이름 
	 * @param size		: 전체 길이 
	 */ 
	public void setString(String key, int size) throws Exception {
		table.put(key, " ");
		stringSizeTable.put(key, size);
		keyVector.add(key);
		
	}	// end setString
	

	
	/**
	 * 프로젝트명 : PI2
	 * 추가내용 : 
	 * byte[] 형태 데이터를 입력한다.
	 * setVByteStream() 후에 getValue()를 사용하고자 한다면 
	 * data 내용과 같은 순서로
	 * addVString 또는 setVString 관련 메소드를 먼저 수행해야 한다.
	 * data의 길이는 DataStruct 길이와 같거나 커야 한다.
	 * data read중 fs를 만나면 다음 항목으로 넘어간다.
	 * data 길이가 DataStruct 길이보다 큰 경우 DataStruct max 길이 이후 데이터는 생략한후 
	 * 데이터 끝에 0x1C를 붙여준다.
	 * 변경일자 : 2015.12.04
	 * 변경자 : 정혜정
	 * 
	 * @param key		: key 이름
	 * @param value		: String 타입 데이터 
	 * @param size		: 데이터 영역중 key 항목이 가질수 있는 최대 길이
	 */
	public void setVByteStream(byte[] data) throws Exception {

		// 임시로 저장하기 위한 Hashtable과 Vector. 
		// setBytes() 마지막에 기존 Hashtable과 Vector값으로 설정된다.
		Hashtable<String, Object> tempTable 			= new Hashtable<String, Object>();
		Hashtable<String, Integer> tempStringSizeTable 	= new Hashtable<String, Integer>();
		Vector<String> tempVector 		= new Vector<String>();
		
		// control 영역의 길이를 설정
		int controlSize = 6;
		int vectorCounter 	= 0;
			// i = data index
		for (int i = 0; i < data.length; i++){
			
			String key 			= keyVector.get(vectorCounter);
			Object tableData 	= table.get(key);
			
			//	UPOS MESSAGE 의 시작인지 체크한다.(첫문자자 SOH(0x01)가 온다.)
			if(data[i] == 0x01)
			{
				// upos 전문 마직막의 0x1C를 제외한 데이터를 추출한다.
				int uposMessageSize = data.length-i-1;
				byte[] uposMessage = new byte[uposMessageSize];
				
				System.arraycopy(data, i, uposMessage, 0, uposMessageSize);
				
				tempTable.put(key, uposMessage);
				tempStringSizeTable.put(key, uposMessageSize);
				tempVector.add(key);
				break;
			}

			if (tableData instanceof Byte) {
				tempTable.put(key, data[i]);
				tempVector.add(key);
				
			} else if (tableData instanceof Short){
				short tempData = (short) ((data[i] & 0xff) << 8 
										   | (data[++i] & 0xff));
				tempTable.put(key, tempData);
				tempVector.add(key);
				
			} else if (tableData instanceof Integer){
				int tempData = ((data[i] & 0xff)<<24 
								| (data[++i] & 0xff) <<16 
								| (data[++i] & 0xff) << 8 
								| (data[++i] & 0xff));
				tempTable.put(key, tempData);
				tempVector.add(key);
				
			} else if (tableData instanceof Character){
				char tempData = (char) (((char)data[i] & 0xff) << 8 
						   				| ((char)data[++i] & 0xff));

				tempTable.put(key, tempData);
				tempVector.add(key);
				
			} else if (tableData instanceof String){
				int stringSize 		= stringSizeTable.get(key);
				
				byte[] tempData 	= new byte[stringSize];
				
				// 데이터 잘라서 옮기기
				for (int j = 0; j < stringSize; j++) {
					
					// 데이터 영역이 시작되는 전 문자가 0x1C인지 확인한다.
					tempData[j] = data[i++];
					//
					// 현재 데이터가 끝인지 검사한다.
					// FS 를 만나면 다음 key으로 넘어간다.
					if(tempData[j] == 0x1C)
					{
						stringSize = j;
						break;
					}
					else if(j+1 == stringSize && controlSize < i )
					{
						// MAX길이가 되어  해당 자리수에 FS(0x1C)값이 없으면 강제로 FS를 넣어준다.
						// value의 끝에 FS를 삽입한다.
						tempData[j] = (char)0x1C;
						while(data[i-1] != 0x1C)
						{	
							i++;
						}
					}
					
				}	// end for
				
				i--;
				
				String stringValue 	= new String(tempData);
				stringValue 		= stringValue.trim();	// blank 제거
				
				tempTable.put(key, stringValue);
				tempStringSizeTable.put(key, stringSize);
				tempVector.add(key);
				
			} else {
				LogUtility.getPumpALogger().debug("### Unknown instance ###");
				
			}	// end inner if
			
			vectorCounter++;
			
		}	// end for
		
		table 			= tempTable;
		stringSizeTable = tempStringSizeTable;
		keyVector	 	= tempVector;
		
		
	}	// end setVByteStream
	
	public void setVByteStream_Gas(byte[] data, int controlSize) throws Exception {

		// 임시로 저장하기 위한 Hashtable과 Vector. 
		// setBytes() 마지막에 기존 Hashtable과 Vector값으로 설정된다.
		Hashtable<String, Object> tempTable 			= new Hashtable<String, Object>();
		Hashtable<String, Integer> tempStringSizeTable 	= new Hashtable<String, Integer>();
		Vector<String> tempVector 		= new Vector<String>();
		
		int vectorCounter 	= 0;
			// i = data index
		for (int i = 0; i < data.length; i++){
			
			String key 			= keyVector.get(vectorCounter);
			Object tableData 	= table.get(key);
			
			//	UPOS MESSAGE 의 시작인지 체크한다.(첫문자자 SOH(0x01)가 온다.)
			
			
			if(data[i] == 0x01 && i !=0)
			{
				// upos 전문 마직막의 0x1C를 제외한 데이터를 추출한다.
				int uposMessageSize = data.length-i-1;
				byte[] uposMessage = new byte[uposMessageSize];
				
				System.arraycopy(data, i, uposMessage, 0, uposMessageSize);
				
				tempTable.put(key, uposMessage);
				tempStringSizeTable.put(key, uposMessageSize);
				tempVector.add(key);
				break;
			}

			if (tableData instanceof Byte) {
				tempTable.put(key, data[i]);
				tempVector.add(key);
				
			} else if (tableData instanceof Short){
				short tempData = (short) ((data[i] & 0xff) << 8 
										   | (data[++i] & 0xff));
				tempTable.put(key, tempData);
				tempVector.add(key);
				
			} else if (tableData instanceof Integer){
				int tempData = ((data[i] & 0xff)<<24 
								| (data[++i] & 0xff) <<16 
								| (data[++i] & 0xff) << 8 
								| (data[++i] & 0xff));
				tempTable.put(key, tempData);
				tempVector.add(key);
				
			} else if (tableData instanceof Character){
				char tempData = (char) (((char)data[i] & 0xff) << 8 
						   				| ((char)data[++i] & 0xff));

				tempTable.put(key, tempData);
				tempVector.add(key);
				
			} else if (tableData instanceof String){
				int stringSize 		= stringSizeTable.get(key);
				
				byte[] tempData 	= new byte[stringSize];
				
				// 데이터 잘라서 옮기기
				for (int j = 0; j < stringSize; j++) {
					
					// 데이터 영역이 시작되는 전 문자가 0x1C인지 확인한다.
					tempData[j] = data[i++];
					//
					// 현재 데이터가 끝인지 검사한다.
					// FS 를 만나면 다음 key으로 넘어간다.
					if(tempData[j] == 0x1C)
					{
						stringSize = j;
						break;
					}
					else if(j+1 == stringSize && controlSize < i )
					{
						// MAX길이가 되어  해당 자리수에 FS(0x1C)값이 없으면 강제로 FS를 넣어준다.
						// value의 끝에 FS를 삽입한다.
						tempData[j] = (char)0x1C;
						while(data[i-1] != 0x1C)
						{	
							i++;
						}
					}
					
				}	// end for
				
				i--;
				
				String stringValue 	= new String(tempData);
				stringValue 		= stringValue.trim();	// blank 제거
				
				tempTable.put(key, stringValue);
				tempStringSizeTable.put(key, stringSize);
				tempVector.add(key);
				
			} else {
				LogUtility.getPumpALogger().debug("### Unknown instance ###");
				
			}	// end inner if
			
			vectorCounter++;
			
		}	// end for
		
		table 			= tempTable;
		stringSizeTable = tempStringSizeTable;
		keyVector	 	= tempVector;
		
		
	}	// end setVByteStream_Gas
	
	/** 
	 * 프로젝트명 : PI2
	 * 추가내용 : 
	 * 가변길이의 datas영역 항목 size를 설정하여 hashtable에 저장한다.
	 * size는 maxSize+1 크기로 설정된다.
	 * 변경일자 : 2016.03.15
	 * 변경자 : 정혜정
	 * 
	 * @param key	: key 이름
	 * @param size	: key 항목의 최대 데이터 크기 순번
	 * @param seq	: datas 영역의 key 항목 순번
	 * @param bytes	: workingmassage로 변경할 byteStream  
	 * 
	 */
	public void setVString(String key, int size) throws Exception {

		table.put(key, " ");
		stringSizeTable.put(key, size+1);
		keyVector.add(key);
		
	}	// end setVString

}