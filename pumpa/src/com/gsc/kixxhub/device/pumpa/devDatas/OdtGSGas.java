package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.device.pumpa.common.DataStruct;


/*
 * �������� : 2016.03.28 ������
 * ���� : TCP/IP ��Ź�� ������ ODT�� DataStruct
 *
 */

/**
 * @author yd
 *
 */
class C1_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public C1_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("timeInfo", 12);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end C1_NewDataStruct


/**
 * @author yd
 *
 */
class C2_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public C2_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("cardNo", 18);		// ī���ȣ
		this.setString("carNo", 18);		// ������ȣ
		this.setString("driverName", 30);	// �����ڸ�
		this.setString("liter", 7);			// ���� ��뷮
		this.setString("basePrice", 6);		// �ǸŴܰ�
		this.setString("jpLiter", 7);		// ��ǥ��
		this.setString("transType", 1);		// �ŷ�����
		this.setString("cusType", 1);		// ������ 
		this.setString("transStatus", 1);	// �ŷ�����	
		this.setString("printBase", 1);		// �ܰ���¿���
		this.setString("depositST", 1);		// ���������࿩��
		this.setString("floatTR", 1);		// �Ҽ���ó�����
		this.setString("receiptType", 1);	// ��꼭�ŷ�����
		this.setString("monthLimit", 10);	// �� �ѵ�����
		this.setString("saveLimit", 10);	// ������뷮 
		this.setString("limitType", 1);		// �ѵ����� 
		this.setString("cusNo", 6);		    // �ŷ�ó��ȣ 
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end C2_NewDataStruct



/**
 * @author yd
 *
 */
class C3_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public C3_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("repNo", 2);
		this.setString("carNo", 18);
		this.setString("cardNo", 18);
		this.setString("driverName", 30);
		this.setString("dataFinish", 1);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end C3_NewDataStruct


/**
 * @author yd
 *
 */
class C4_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public C4_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end C4_NewDataStruct



/**
 * @author yd
 *
 */
class C5_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public C5_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("result", 1);
		this.setString("trans", 8);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end C5_NewDataStruct


/**
 * @author yd
 *
 */
class C7_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public C7_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("result", 1);		// ó�����
		this.setString("bonusRep", 2);		// ���ʽ�ī�������ڵ�
		this.setString("bonusNumber", 16);	// ���ʽ�ī���ȣ
		this.setString("gsScore", 6);		// GS �Ϲ�����
		this.setString("gsSpecialScore", 6);// GS Ư������
		this.setString("ssScore", 6);		// SS ����
		this.setString("birth", 4);			// ���λ���
		this.setString("partnerBirth", 4);	// ����ڻ���
		this.setString("weddingDay", 4);	// ��ȥ�����
		this.setString("babyBirth1", 4);	// �ڳ�1 ����
		this.setString("babyBirth2", 4);	// �ڳ�2 ����
		this.setString("plan", 1);			// ���Ժ��� �÷�
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end C7_NewDataStructs


/**
 * @author yd
 *
 */
class CB_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CB_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CB_NewDataStruct


/**
 * @author yd
 *
 */
class CC_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CC_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CC_NewDataStruct



/**
 * @author yd
 *
 */
class CD_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CD_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("storeName", 40);
		this.setString("regiNum", 12);
		this.setString("repNM", 30);
		this.setString("addr", 50);
		this.setString("tel", 16);
		this.setString("goodsType", 30);
		this.setString("basePrice", 6);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CC_NewDataStruct


/**
 * @author yd
 *
 */
class CE_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CE_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CE_NewDataStruct


/**
 * @author yd
 *
 */
class CF_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CF_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CF_NewDataStruct



/**
 * @author yd
 *
 */
class CG_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CG_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CG_NewDataStruct



/**
 * @author yd
 *
 */
class CH_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CH_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CH_NewDataStruct



/**
 * @author yd
 *
 */
class CI_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CI_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CI_NewDataStruct



/**
 * @author yd
 *
 */
class CJ_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CJ_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("liter", 7);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CJ_NewDataStruct



/**
 * @author yd
 *
 */
class CK_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CK_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("liter", 7);
		this.setString("basePrice", 6);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end _NewDataStruct



/**
 * @author yd
 *
 */
class CN_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CN_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("receiptMin", 5); // �����������ּ�ġ(�̻��)
		this.setString("depositMin", 5); // �����������ּ�ġ(�̻��)
		this.setString("minSale", 5);	 // �Ǹ����� �ּ�ġ
		this.setString("loanRemain", 1); // �뿩�� �ܾ�ǥ�� ���� 
		this.setString("termWait", 2);	 // �Ǹ�������ð� 
		this.setString("emergStop", 3); // ������� ���� 
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CN_NewDataStruct



/**
 * @author yd
 *
 */
class CO_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public CO_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("depositNumber", 10);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end CO_NewDataStruct



public class OdtGSGas {

	/**
	 * ODT �������� ���� DataStruct�� ��´�. 
	 * 
	 * @param command	: ��� �ڵ� 
	 * @return
	 */
	public static DataStruct getDS(String command) throws Exception {
		DataStruct returnData = null;
		
		if (command.equals("C1")) {
			returnData = new C1_NewDataStruct();
			
		} else if (command.equals("C2")) {
			returnData = new C2_NewDataStruct();
			
		} else if (command.equals("C3")) {
			returnData = new C3_NewDataStruct();
			
		} else if (command.equals("C4")) {
			returnData = new C4_NewDataStruct();
			
		} else if (command.equals("C5")) {
			returnData = new C5_NewDataStruct();
			
		} else if (command.equals("C7")) {
			returnData = new C7_NewDataStruct();
			
		} else if (command.equals("CB")) {
			returnData = new CB_NewDataStruct();
			
		} else if (command.equals("CC")) {
			returnData = new CC_NewDataStruct();
			
		} else if (command.equals("CD")) {
			returnData = new CD_NewDataStruct();
			
		} else if (command.equals("CE")) {
			returnData = new CE_NewDataStruct();
			
		} else if (command.equals("CF")) {
			returnData = new CF_NewDataStruct();
			
		} else if (command.equals("CG")) {
			returnData = new CG_NewDataStruct();
			
		} else if (command.equals("CH")) {
			returnData = new CH_NewDataStruct();
			
		} else if (command.equals("CI")) {
			returnData = new CI_NewDataStruct();
			
		} else if (command.equals("CJ")) {
			returnData = new CJ_NewDataStruct();
			
		} else if (command.equals("CK")) {
			returnData = new CK_NewDataStruct();
			
		} else if (command.equals("CN")) {
			returnData = new CN_NewDataStruct();
			
		} else if (command.equals("CO")) {
			returnData = new CO_NewDataStruct();
			
		} else if (command.equals("T1")) {
			returnData = new T1_NewDataStruct();
			
		} else if (command.equals("T2")) {
			returnData = new T2_NewDataStruct();
			
		} else if (command.equals("T3")) {
			returnData = new T3_NewDataStruct();
			
		} else if (command.equals("T5")) {
			returnData = new T5_NewDataStruct();
			
		} else if (command.equals("TA")) {
			returnData = new TA_NewDataStruct();
			
		} else if (command.equals("TB")) {
			returnData = new TB_NewDataStruct();
			
		} else if (command.equals("TC")) {
			returnData = new TC_NewDataStruct();
		/**
		 * ���泻�� : �ű�(������ ODT �ſ�, �ſ�+���ʽ�, ���ݿ����� ���� ��û, ķ���������Ϸ� ���� ��û)
		 * �������� : 2015.12.23 ������
		 */	
		} else if (command.equals("SK")) {
			returnData = new SK_NewDataStruct();
			
		} else {
			returnData = null;
		}	// end if
		
		return returnData;
	}	// end getDS
	
	
	
	/**
	 * ODT �������� ����ڵ� "T4" ���� DataStruct�� ��´�.
	 * 
	 * @param command	: ��� �ڵ� 
	 * @param repNo		: ���� ���� ���� 
	 * @return
	 */
	public static DataStruct getDS(String command, int repNo)  {
		DataStruct returnData = null;
		
		try {
			if (command.equals("T4")) {
				returnData = new T4_NewDataStruct(repNo);

			} else {
				returnData = null;

			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}	// getDS
	
}

/**
 * @author yd
 *
 */
class SK_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public SK_NewDataStruct() throws Exception {
		/* * control ����*/
		this.setByte("soh");
		this.setString("DeviceNo", 2);
		this.setByte("stx");
		/* control ��*/
		
		this.setString("Command", 2);
		this.setString("ConnectDevNo", 2);     //���� ����̽� ��ȣ, ������ ODT�� ��� 01

		/* datas ���� ���� 
		 * ���׸��� �������̸� ���´�.*/
		this.setVString("MessageType", 4);     //�޽��� Ÿ��
		this.setVString("CardNumber", 100);    //ī���ȣ
		this.setVString("BonusCard", 100);     //���ʽ�ī���ȣ
		this.setVString("CustCardNo", 100);    //�ŷ�óī���ȣ
		this.setVString("CashReceiptNo", 100); //���ݿ����� ��ȣ
		this.setVString("Amount", 7);          //����
		this.setVString("BasePrice", 6);       //�ܰ�
		this.setVString("Price", 8);           //�ݾ�
		this.setVString("LedCode", 1);         //LED �ڵ�
		this.setVString("CreatedTime", 14);    //��������
		//this.setVString("CashCount", 8);
		this.setVString("UPosByte", 0);        //UposMessage
		this.setByte("etx");                   //ETX
		
	}
}	// end SK_NewDataStruct



/**
 * @author yd
 *
 */
class T1_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public T1_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("screen", 1);		// ����ǥ��
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end T1_NewDataStruct



/**
 * @author yd
 *
 */
class T2_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public T2_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("liter", 7);
		this.setString("price", 8);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end T2_NewDataStruct



/**
 * @author yd
 *
 */
class T3_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public T3_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("carNumber", 16);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end _NewDataStruct



/**
 * @author yd
 *
 */
class T4_NewDataStruct extends DataStruct {
	/**
	 * @param repNo
	 */
	public T4_NewDataStruct(int repNo) throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("cardNumber", 16);	// ī���ȣ
		this.setString("boyNumber", 4);		// ������ ��ȣ
		this.setString("totalLiter", 7);	// ������
		this.setString("up1", 6);			// ���ܰ�
		this.setString("totalAMT1", 8);		// �Ѱ��ݾ�
		this.setString("up2", 6);			// �ǸŴܰ�
		this.setString("totalAMT2", 8);		// �� �Ǹűݾ�
		this.setString("totalGauge", 10);	// ��Ż������
		this.setString("repNo", 1);			// ������������
		
		for (int i = 0; i < repNo; i++) {
			this.setString("flag" + i, 1);			// ��������
			this.setString("liter" + i, 7);			// �������
			this.setString("amt" + i, 8);			// ���ñݾ�
			this.setString("keepNumber" + i, 10);	// ������ ��ȣ �Ǵ� �Ž�����
			
		}	// end for
		
//		this.setString("keyFlag", 1);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
	
}	// end _NewDataStruct

/**
 * @author yd
 *
 */
class T5_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public T5_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("boyNumber", 4);
		this.setString("cardNumber", 18);
		this.setString("price", 8);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end T5_NewDataStruct


/**
 * @author yd
 *
 */
class TA_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public TA_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("totalGauge", 10);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end TA_NewDataStruct



/**
 * @author yd
 *
 */
class TB_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public TB_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("keepNumber", 10);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end TB_NewDataStruct



/**
 * @author yd
 *
 */
class TC_NewDataStruct extends DataStruct {
	/**
	 * 
	 */
	public TC_NewDataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		//this.setByte("etb");
		//this.setString("crc16", 4);
		this.setByte("etx");
		//this.setByte("sum");
		
	}
}	// end TC_NewDataStruct
