package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.device.pumpa.common.DataStruct;

/**
 * @author yd
 *
 */
class BA_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public BA_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("cardNumber", 40);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end BA_DataStruct


/**
 * @author yd
 *
 */
class BB_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public BB_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("success", 1);
		this.setString("display", 64);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end BB_DataStruct



/**
 * @author yd
 *
 */
class C1_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C1_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("timeInfo", 12);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C1_DataStruct


/**
 * @author yd
 *
 */
class C2_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C2_DataStruct() throws Exception {
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
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C2_DataStruct



/**
 * @author yd
 *
 */
class C3_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C3_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("repNo", 2);
		this.setString("carNo", 18);
		this.setString("cardNo", 18);
		this.setString("driverName", 30);
		this.setString("dataFinish", 1);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C3_DataStruct


/**
 * @author yd
 *
 */
class C4_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C4_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C4_DataStruct



/**
 * @author yd
 *
 */
class C5_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C5_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("result", 1);
		this.setString("trans", 8);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C5_DataStruct



/**
 * @author yd
 *
 */
class C6_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C6_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("result", 1);			// ó�����
		this.setString("recogDate", 14);		// �����Ͻ�
		this.setString("recogNumber", 12);		// ���ι�ȣ
		this.setString("cardNumber", 16);		// ī���ȣ
		this.setString("cardCorpName", 20);		// ī����
		this.setString("francNumber", 16);		// ��������ȣ
		this.setString("noteCorpCode", 3);		// ��ǥ���Ի��ڵ�
		this.setString("noteCorpName", 20);		// ��ǥ���Ի��
		this.setString("terminalNumber", 10);	// �ܸ��� ��ȣ
		this.setString("noteNumberTemp", 5);	// ��ǥ��ȣ
		this.setString("notice", 64);			// Notice
		this.setString("noteNumber", 10);		// ��ǥ��ȣ
		this.setString("recogConfid", 5);		// �������ſ���μ���
		this.setString("printContent", 500);	// �μ⳻��
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C6_DataStruct



/**
 * @author yd
 *
 */
class C7_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C7_DataStruct() throws Exception {
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
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C7_DataStruct



/**
 * @author yd
 *
 */
class C8_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public C8_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("result", 1);			// ó�����
		this.setString("recodDate", 14);		// �����Ͻ� 
		this.setString("recodNumber", 12);		// ���ι�ȣ
		this.setString("cardNumber", 16);		// ī���ȣ
		this.setString("cardCorpName", 20);		// ī����
		this.setString("francNumber", 16);		// ��������ȣ
		this.setString("noteCorpCode", 3);		// ��ǥ���Ի��ڵ�
		this.setString("noteCorpName", 20);		// ��ǥ���Ի��
		this.setString("terminalNumber", 10);	// �ܸ��� ��ȣ
		this.setString("noteNumberTemp", 5);	// ��ǥ��ȣ
		this.setString("notice", 64);			// Notice
		this.setString("noteNumber", 10);		// ��ǥ��ȣ
		this.setString("recogConfid", 5);		// �������ſ���μ���
		this.setString("printBuffer", 500);		// �μ⳻��
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end C8_DataStruct



/**
 * @author yd
 *
 */
class CB_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CB_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CB_DataStruct


/**
 * @author yd
 *
 */
class CC_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CC_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CC_DataStruct


/**
 * @author yd
 *
 */
class CD_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CD_DataStruct() throws Exception {
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
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CC_DataStruct



/**
 * @author yd
 *
 */
class CE_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CE_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CE_DataStruct



/**
 * @author yd
 *
 */
class CF_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CF_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CF_DataStruct



/**
 * @author yd
 *
 */
class CG_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CG_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CG_DataStruct



/**
 * @author yd
 *
 */
class CH_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CH_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CH_DataStruct



/**
 * @author yd
 *
 */
class CI_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CI_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("head", 42);
		this.setString("tail1", 42);
		this.setString("tail2", 42);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CI_DataStruct



/**
 * @author yd
 *
 */
class CJ_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CJ_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("liter", 7);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CJ_DataStruct



/**
 * @author yd
 *
 */
class CK_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CK_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("liter", 7);
		this.setString("basePrice", 6);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end _DataStruct



/**
 * @author yd
 *
 */
class CN_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CN_DataStruct() throws Exception {
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
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CN_DataStruct



/**
 * @author yd
 *
 */
class CO_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CO_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("depositNumber", 10);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CO_DataStruct



/**
 * @author yd
 *
 */
class CP_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public CP_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("bonusNumber", 16);
		this.setString("pointScore", 10);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end CP_DataStruct



public class OdtDS {

	/**
	 * ODT �������� ���� DataStruct�� ��´�. 
	 * 
	 * @param command	: ��� �ڵ� 
	 * @return
	 */
	public static DataStruct getDS(String command) throws Exception {
		DataStruct returnData = null;
		
		if (command.equals("C1")) {
			returnData = new C1_DataStruct();
			
		} else if (command.equals("C2")) {
			returnData = new C2_DataStruct();
			
		} else if (command.equals("C3")) {
			returnData = new C3_DataStruct();
			
		} else if (command.equals("C4")) {
			returnData = new C4_DataStruct();
			
		} else if (command.equals("C5")) {
			returnData = new C5_DataStruct();
			
		} else if (command.equals("C6")) {
			returnData = new C6_DataStruct();
			
		} else if (command.equals("C7")) {
			returnData = new C7_DataStruct();
			
		} else if (command.equals("C8")) {
			returnData = new C8_DataStruct();
			
		} else if (command.equals("CB")) {
			returnData = new CB_DataStruct();
			
		} else if (command.equals("CC")) {
			returnData = new CC_DataStruct();
			
		} else if (command.equals("CD")) {
			returnData = new CD_DataStruct();
			
		} else if (command.equals("CE")) {
			returnData = new CE_DataStruct();
			
		} else if (command.equals("CF")) {
			returnData = new CF_DataStruct();
			
		} else if (command.equals("CG")) {
			returnData = new CG_DataStruct();
			
		} else if (command.equals("CH")) {
			returnData = new CH_DataStruct();
			
		} else if (command.equals("CI")) {
			returnData = new CI_DataStruct();
			
		} else if (command.equals("CJ")) {
			returnData = new CJ_DataStruct();
			
		} else if (command.equals("CK")) {
			returnData = new CK_DataStruct();
			
		} else if (command.equals("CN")) {
			returnData = new CN_DataStruct();
			
		} else if (command.equals("CO")) {
			returnData = new CO_DataStruct();
			
		} else if (command.equals("CP")) {
			returnData = new CP_DataStruct();
			
		} else if (command.equals("BB")) {
			returnData = new BB_DataStruct();
			
		} else if (command.equals("T1")) {
			returnData = new T1_DataStruct();
			
		} else if (command.equals("T2")) {
			returnData = new T2_DataStruct();
			
		} else if (command.equals("T3")) {
			returnData = new T3_DataStruct();
			
		} else if (command.equals("T5")) {
			returnData = new T5_DataStruct();
			
		} else if (command.equals("T6")) {
			returnData = new T6_DataStruct();
			
		} else if (command.equals("TA")) {
			returnData = new TA_DataStruct();
			
		} else if (command.equals("TB")) {
			returnData = new TB_DataStruct();
			
		} else if (command.equals("TC")) {
			returnData = new TC_DataStruct();
			
		} else if (command.equals("TD")) {
			returnData = new TD_DataStruct();
			
		} else if (command.equals("TJ")) {
			returnData = new TJ_DataStruct();
			
		} else if (command.equals("BA")) {
			returnData = new BA_DataStruct();
			
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
				returnData = new T4_DataStruct(repNo);

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
class T1_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public T1_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("screen", 1);		// ����ǥ��
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end T1_DataStruct



/**
 * @author yd
 *
 */
class T2_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public T2_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("liter", 7);
		this.setString("price", 8);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end T2_DataStruct



/**
 * @author yd
 *
 */
class T3_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public T3_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("mode", 1);
		this.setString("carNumber", 16);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end _DataStruct



/**
 * @author yd
 *
 */
class T4_DataStruct extends DataStruct {
	/**
	 * @param repNo
	 */
	public T4_DataStruct(int repNo) throws Exception {
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
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
	
}	// end _DataStruct



/**
 * @author yd
 *
 */
class T5_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public T5_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("boyNumber", 4);
		this.setString("cardNumber", 18);
		this.setString("price", 8);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end T5_DataStruct



/**
 * @author yd
 *
 */
class T6_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public T6_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("cardType", 1);	// ī������
		this.setString("mode", 1);		// ó������
		this.setString("content", 40);	// ī�峻��
		this.setString("cDate", 14);	// �����Ͻ�
		this.setString("cNumber", 12);	// ���ι�ȣ 
		this.setString("liter", 7);		// ����
		this.setString("basePrice", 6);	// �ܰ�
		this.setString("price", 8);		// �ݾ� 
		this.setString("bonusString", 5);
		this.setString("bonusCardNo", 40);	// ���ʽ�ī���ȣ
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end T6_DataStruct



/**
 * @author yd
 *
 */
class TA_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public TA_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("totalGauge", 10);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end TA_DataStruct



/**
 * @author yd
 *
 */
class TB_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public TB_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("keepNumber", 10);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end TB_DataStruct



/**
 * @author yd
 *
 */
class TC_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public TC_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end TC_DataStruct



/**
 * @author yd
 *
 */
class TD_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public TD_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("bonusNumber", 16);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end TD_DataStruct



/**
 * @author yd
 *
 */
class TJ_DataStruct extends DataStruct {
	/**
	 * 
	 */
	public TJ_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odt_no", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("dealType", 1);
		this.setString("dealAmount", 9);
		this.setString("keyInType", 1);
		this.setString("certiNumber", 39);
		
		this.setByte("etb");
		this.setString("crc16", 4);
		this.setByte("etx");
		this.setByte("sum");
		
	}
}	// end TD_DataStruct

