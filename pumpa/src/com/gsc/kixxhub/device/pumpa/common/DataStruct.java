package com.gsc.kixxhub.device.pumpa.common;
import java.util.Hashtable;
import java.util.Vector;

import com.gsc.kixxhub.common.utility.log.LogUtility;


public class DataStruct{ 
	/**
	 * 
	 */
	private Vector<String> keyVector 					= 
								new Vector<String>();	// Vector���� �̸��� ����
	/**
	 * 
	 */
	private Hashtable<String, Integer> stringSizeTable = 
								new Hashtable<String, Integer>();
	// Hashtable�� ����ϴ� ���� ����Ǵ� ������ �� �� �����Ƿ�
	// ������� ����Ǵ� Vector�� �Բ� �����Ͽ� ����Ѵ�
	/**
	 * 
	 */
	private Hashtable<String, Object> table 							= 
								new Hashtable<String, Object>();		// Hashtable���� �̸��� �� ����

	
	
	/**
	 * byte Ÿ�� ������ ����
	 * 
	 * @param key		: key �̸� 
	 * @param value		: byte Ÿ�� ������ 
	 */
	public void addByte(String key, byte value) throws Exception {
		table.put(key, value);
		keyVector.add(key);
		
	}	// end addByte
	
	
	
	/**
	 * char Ÿ�� ������ ����
	 * 
	 * @param key		: key �̸� 
	 * @param value		: char Ÿ�� ������ 
	 */
	public void addChar(String key, char value) throws Exception {
		table.put(key, value);
		keyVector.add(key);
		
	}	// end addChar
	
	
	
	/**
	 * int Ÿ�� ������ ����
	 * 
	 * @param key		: key �̸� 
	 * @param value		: int Ÿ�� ������ 
	 */
	public void addInt(String key, int value) throws Exception {
		table.put(key, value);
		keyVector.add(key);
		
	}	// end addInt
	
	
	
	/**
	 * short Ÿ�� ������ ����
	 * 
	 * @param key		: key �̸� 
	 * @param value		: short Ÿ�� ������ 
	 */
	public void addShort(String key, short value) throws Exception {
		table.put(key, value);
		keyVector.add(key);
		
	}	// end addShort
	
	
	
	/**
	 * String Ÿ�� ������ ����. ��ü ���̸� ���� �Է� �޾� 
	 * String Ÿ�� �����Ϳ� ������ �߰��� �� �ִ�.
	 * 
	 * @param key		: key �̸�
	 * @param value		: String Ÿ�� ������ 
	 * @param size		: ��ü ����
	 */
	public void addString(String key, String value, int size) throws Exception {
		int valueLength = value.getBytes().length;
		
		// size�� value ���̺��� ���� �Ѵ�.
		// size�� value ���̺��� �۴ٸ� size ���� value ���̷� �����Ѵ�.
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
	 * ������Ʈ�� : PI2
	 * �߰����� : 
	 * String Ÿ�� �����͸� value �� ���� 0x1C���� ���ٿ� ����. 
	 * �ִ� ����(mLength)��  �Է¹޾� �Էµ� ������ ����(dLength)�� ���Ͽ� 
	 * �� ���� ���̷� size�� �����Ѵ�.
	 * ���� �����Ͱ� ����Ǵ� ������ dLength+1�̸� 	 
	 * �����Ͱ� �ִ���̺��� ũ�� �ִ�����ʰ������Ͱ� ������ ����ȴ�.
	 * �������� : 2016.03.15
	 * ������ : ������
	 * 
	 * @param key		: key �̸�
	 * @param value		: String Ÿ�� ������ 
	 * @param size		: ������ ������ key �׸��� ������ �ִ� �ִ� ����
	 */
	public void addVString(String key, String value, int size) throws Exception {
		int valueLength = value.getBytes().length;
		
		// size�� ���Ͽ� 
		// size�� value ���̺��� �۴ٸ� size ���� value ���̷� �����Ѵ�.
		if (valueLength > size) {
			
			LogUtility.getPumpALogger().debug("### DataStruct Error : String = " + 
					value + ", size = " + size + " ###");
			
			//size = valueLength;
			// �ִ���̺��� �����Ͱ� ���� �Էµɰ��� �����Ѵ�.
			value = value.substring(0, size);
		}
		//�����Ͱ� �ִ���̺��� ���� ���
		else
		{
			size = valueLength;			
		}
		
		// end if
		
		
		// ������ ���̿� ���� ���� ���̸� ���Ͽ� ��ª�� ���� ���̷� ����ְ� FS�Է°����� �� �Ҵ��ش�.
		size++;			
		
		char hex1C = (char)0x1C;
		// value�� FS(0x1C)�� �����δ�.
		table.put(key, value+ hex1C);
		stringSizeTable.put(key, size);
		keyVector.add(key);
		
	}	// end addVString
	
	/**
	 * byte Ÿ�� ������ ���� 
	 * 
	 * @param key		: key �̸� 
	 * @param value		: byte Ÿ�� ������ 
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
	 * ������Ʈ�� : PI2
	 * �߰����� : 
	 * String Ÿ�� ���� ������ ���� ������ ���� 0x1C�� �����δ�.
	 * @param key		: key �̸� 
	 * @param value		: String Ÿ�� ������ 
	 * @param size		: ��ü ���� 
	 * �������� : 2015.12.04
	 * ������ : ������
	 */
	public void editVString(String key, String value, int size) throws Exception {
		if (table.containsKey(key) && stringSizeTable.containsKey(key)) {
			table.remove(key);
			stringSizeTable.remove(key);
			
			int valueLength = value.getBytes().length;
			
			// size�� ���Ͽ� 
			// size�� value ���̺��� �۴ٸ� size ���� value ���̷� �����Ѵ�.
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
			
			// FS(0x1C) ������ �ڸ��� �÷��ش�.
			size++;
			char hex1C = (char)0x1C;
			// value�� FS(0x1C)�� �����δ�.
			table.put(key, value+ hex1C);
			stringSizeTable.put(key, size);
			
		} else {
			LogUtility.getLogger().debug("##### Can't edit String \"" + key + 
												"\" in Data struct #####");
			
		}	// end if
		
	}	// end editVString
	
	/**
	 * char Ÿ�� ������ ���� 
	 * 
	 * @param key		: key �̸� 
	 * @param value		: char Ÿ�� ������ 
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
	 * int Ÿ�� ������ ���� 
	 * 
	 * @param key		: key �̸� 
	 * @param value		: int Ÿ�� ������ 
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
	 * short Ÿ�� ������ ���� 
	 * 
	 * @param key		: key �̸� 
	 * @param value		: short Ÿ�� ������ 
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
	 * String Ÿ�� ������ ���� 
	 * 
	 * @param key		: key �̸� 
	 * @param value		: String Ÿ�� ������ 
	 * @param size		: ��ü ���� 
	 */
	public void editString(String key, String value, int size) throws Exception {
		if (table.containsKey(key) && stringSizeTable.containsKey(key)) {
			table.remove(key);
			stringSizeTable.remove(key);
			
			int valueLength = value.getBytes().length;
			
			// size�� value ���̺��� ���� �Ѵ�.
			// size�� value ���̺��� �۴ٸ� size ���� value ���̷� �����Ѵ�.
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
	 * ���� ����� �����͸� byte �迭 Ÿ������ ��´�.
	 * 
	 * @return	: byte �迭 Ÿ�� ������ 
	 */
	public byte[] getByteStream() throws Exception {
		int arraySize 		= this.getLength();
		byte[] returnData 	= null;
		
		returnData = new byte[arraySize];
		int vectorCounter = 0;		// keyVector index
		
		// returnData �迭�� �� �Է� 
		// i = returnData index
		for (int i = 0; i < arraySize; i++){
			// ������� ������ �б�
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
				
				// data ä���
				for (int j = 0; j < target.length; j++) {
					returnData[i] = target[j];
					i++;
					
				}	// end for
				
				// blank ä���
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
	 * ���� ����� �������� �迭 ���̸� ��´�.
	 * 
	 * @return	: �迭 ���� 
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
	 * �־��� key �̸����� ����Ǿ� �ִ� �����͸� ��´�.
	 * String Ÿ�� �����ʹ� ��ü ���̿� ������� �����ߴ� ������ ���̸�ŭ ��´�.
	 * 
	 * @param key	: key �̸� 
	 * @return		: Object Ÿ�� ������. �ش� �����Ͱ� ���� ��� null ����.
	 */
	public Object getValue(String key) throws Exception {
		Object returnData = table.get(key);
		
		/*
		 * String ���� �����Ϳ� ������ �����Ͽ� �����͸� ��´�
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
	 * Vector, Hashtable �ʱ�ȭ
	 * 
	 */
	public void init() throws Exception {
		table.clear();
		stringSizeTable.clear();
		keyVector.clear();
		
	}	// end init
	
	

	/**
	 * byte Ÿ�� ���� ����  
	 * 
	 * @param key		: key �̸� 
	 */ 
	public void setByte(String key) throws Exception {
		table.put(key, (byte) 0x20);
		keyVector.add(key);
		
	}	// end setByte
	
	

	/**
	 * byte[] ���� �����͸� �Է��Ѵ�.
	 * setByteStream() �Ŀ� getValue()�� ����ϰ��� �Ѵٸ� 
	 * data ����� ���� ������
	 * add �Ǵ� set ���� �޼ҵ带 ���� �����ؾ� �Ѵ�.
	 * data�� ���̴� DataStruct ���̿� ���ų� Ŀ�� �Ѵ�.
	 * data ���̰� DataStruct ���̺��� ū ��� DataStruct ���� ���� �����ʹ� �����ȴ�.
	 * 
	 * @param data
	 */
	public void setByteStream(byte[] data) throws Exception {
		// �ӽ÷� �����ϱ� ���� Hashtable�� Vector. 
		// setBytes() �������� ���� Hashtable�� Vector������ �����ȴ�.
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
					stringValue 		= stringValue.trim();	// blank ����
					
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
	 * char Ÿ�� ���� ����  
	 * 
	 * @param key		: key �̸� 
	 */ 
	public void setChar(String key) throws Exception {
		table.put(key, ' ');
		keyVector.add(key);
		
	}	// end setChar
	
	
	/**
	 * int Ÿ�� ���� ����  
	 * 
	 * @param key		: key �̸� 
	 */ 
	public void setInt(String key) throws Exception {
		table.put(key, 0x20);
		keyVector.add(key);
		
	}	// end setInt
	
	
	
	/**
	 * short Ÿ�� ���� ����  
	 * 
	 * @param key		: key �̸� 
	 */ 
	public void setShort(String key) throws Exception {
		table.put(key, (short) 0x20);
		keyVector.add(key);
		
	}	// end setShort
	
	
	/**
	 * String Ÿ�� ���� ����  
	 * 
	 * @param key		: key �̸� 
	 * @param size		: ��ü ���� 
	 */ 
	public void setString(String key, int size) throws Exception {
		table.put(key, " ");
		stringSizeTable.put(key, size);
		keyVector.add(key);
		
	}	// end setString
	

	
	/**
	 * ������Ʈ�� : PI2
	 * �߰����� : 
	 * byte[] ���� �����͸� �Է��Ѵ�.
	 * setVByteStream() �Ŀ� getValue()�� ����ϰ��� �Ѵٸ� 
	 * data ����� ���� ������
	 * addVString �Ǵ� setVString ���� �޼ҵ带 ���� �����ؾ� �Ѵ�.
	 * data�� ���̴� DataStruct ���̿� ���ų� Ŀ�� �Ѵ�.
	 * data read�� fs�� ������ ���� �׸����� �Ѿ��.
	 * data ���̰� DataStruct ���̺��� ū ��� DataStruct max ���� ���� �����ʹ� �������� 
	 * ������ ���� 0x1C�� �ٿ��ش�.
	 * �������� : 2015.12.04
	 * ������ : ������
	 * 
	 * @param key		: key �̸�
	 * @param value		: String Ÿ�� ������ 
	 * @param size		: ������ ������ key �׸��� ������ �ִ� �ִ� ����
	 */
	public void setVByteStream(byte[] data) throws Exception {

		// �ӽ÷� �����ϱ� ���� Hashtable�� Vector. 
		// setBytes() �������� ���� Hashtable�� Vector������ �����ȴ�.
		Hashtable<String, Object> tempTable 			= new Hashtable<String, Object>();
		Hashtable<String, Integer> tempStringSizeTable 	= new Hashtable<String, Integer>();
		Vector<String> tempVector 		= new Vector<String>();
		
		// control ������ ���̸� ����
		int controlSize = 6;
		int vectorCounter 	= 0;
			// i = data index
		for (int i = 0; i < data.length; i++){
			
			String key 			= keyVector.get(vectorCounter);
			Object tableData 	= table.get(key);
			
			//	UPOS MESSAGE �� �������� üũ�Ѵ�.(ù������ SOH(0x01)�� �´�.)
			if(data[i] == 0x01)
			{
				// upos ���� �������� 0x1C�� ������ �����͸� �����Ѵ�.
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
				
				// ������ �߶� �ű��
				for (int j = 0; j < stringSize; j++) {
					
					// ������ ������ ���۵Ǵ� �� ���ڰ� 0x1C���� Ȯ���Ѵ�.
					tempData[j] = data[i++];
					//
					// ���� �����Ͱ� ������ �˻��Ѵ�.
					// FS �� ������ ���� key���� �Ѿ��.
					if(tempData[j] == 0x1C)
					{
						stringSize = j;
						break;
					}
					else if(j+1 == stringSize && controlSize < i )
					{
						// MAX���̰� �Ǿ�  �ش� �ڸ����� FS(0x1C)���� ������ ������ FS�� �־��ش�.
						// value�� ���� FS�� �����Ѵ�.
						tempData[j] = (char)0x1C;
						while(data[i-1] != 0x1C)
						{	
							i++;
						}
					}
					
				}	// end for
				
				i--;
				
				String stringValue 	= new String(tempData);
				stringValue 		= stringValue.trim();	// blank ����
				
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

		// �ӽ÷� �����ϱ� ���� Hashtable�� Vector. 
		// setBytes() �������� ���� Hashtable�� Vector������ �����ȴ�.
		Hashtable<String, Object> tempTable 			= new Hashtable<String, Object>();
		Hashtable<String, Integer> tempStringSizeTable 	= new Hashtable<String, Integer>();
		Vector<String> tempVector 		= new Vector<String>();
		
		int vectorCounter 	= 0;
			// i = data index
		for (int i = 0; i < data.length; i++){
			
			String key 			= keyVector.get(vectorCounter);
			Object tableData 	= table.get(key);
			
			//	UPOS MESSAGE �� �������� üũ�Ѵ�.(ù������ SOH(0x01)�� �´�.)
			
			
			if(data[i] == 0x01 && i !=0)
			{
				// upos ���� �������� 0x1C�� ������ �����͸� �����Ѵ�.
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
				
				// ������ �߶� �ű��
				for (int j = 0; j < stringSize; j++) {
					
					// ������ ������ ���۵Ǵ� �� ���ڰ� 0x1C���� Ȯ���Ѵ�.
					tempData[j] = data[i++];
					//
					// ���� �����Ͱ� ������ �˻��Ѵ�.
					// FS �� ������ ���� key���� �Ѿ��.
					if(tempData[j] == 0x1C)
					{
						stringSize = j;
						break;
					}
					else if(j+1 == stringSize && controlSize < i )
					{
						// MAX���̰� �Ǿ�  �ش� �ڸ����� FS(0x1C)���� ������ ������ FS�� �־��ش�.
						// value�� ���� FS�� �����Ѵ�.
						tempData[j] = (char)0x1C;
						while(data[i-1] != 0x1C)
						{	
							i++;
						}
					}
					
				}	// end for
				
				i--;
				
				String stringValue 	= new String(tempData);
				stringValue 		= stringValue.trim();	// blank ����
				
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
	 * ������Ʈ�� : PI2
	 * �߰����� : 
	 * ���������� datas���� �׸� size�� �����Ͽ� hashtable�� �����Ѵ�.
	 * size�� maxSize+1 ũ��� �����ȴ�.
	 * �������� : 2016.03.15
	 * ������ : ������
	 * 
	 * @param key	: key �̸�
	 * @param size	: key �׸��� �ִ� ������ ũ�� ����
	 * @param seq	: datas ������ key �׸� ����
	 * @param bytes	: workingmassage�� ������ byteStream  
	 * 
	 */
	public void setVString(String key, int size) throws Exception {

		table.put(key, " ");
		stringSizeTable.put(key, size+1);
		keyVector.add(key);
		
	}	// end setVString

}