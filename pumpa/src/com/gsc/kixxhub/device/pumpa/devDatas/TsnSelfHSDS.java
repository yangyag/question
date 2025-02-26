// - �߰� 2012.08, dhp
package com.gsc.kixxhub.device.pumpa.devDatas;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.DataStruct;

//-- ES : ODT �̻����� (KH <- ODT) --//	
class TH_ES_DataStruct extends DataStruct {

	public TH_ES_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("status", 3);	
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

/*
 * ���ϴ� �ű� ������ ����
 */
//-- FC : POS �������� ���� (KH -> ODT) --//	
class TH_FC_DataStruct extends DataStruct {

	public TH_FC_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("nozzleNo", 2);	
		this.setString("useFullPumping", 1);	// �������� ��뿩��
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- GD : ������ ������ ���ſϷ� (KH <- ODT) --//	
class TH_GD_DataStruct extends DataStruct {

	public TH_GD_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("trNo", 4);		// �ŷ���ȣ
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

/**
 * 
 * @author dhp
 * ���� �پ��뼿��(����) �ű� ����
 *
 */

/*
 *  �ʱ�ȭ �۾�
 */

//-- IQ : �ʱ�ȭ ���� ��û (KH <- ODT) --//	
class TH_IQ_DataStruct extends DataStruct {

	public TH_IQ_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- JP : ������ �׷� ����Ʈ ���� (KH -> ODT) --//	
class TH_JP_DataStruct extends DataStruct {

	public TH_JP_DataStruct(int nozCnt) throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		for(int i=0; i<nozCnt; i++)
			this.setString("nozzleNo" + i, 2);	// ������ ��ȣ(�ִ� 6��)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

/*
 *  �������� ó��
 */

//-- KA : �����㰡 (KH -> ODT) --//	
class TH_KA_DataStruct extends DataStruct {

	public TH_KA_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("trNo", 4);		// �ŷ���ȣ
		this.setString("wcc", 1);		// 'A'
		this.setString("value", 7);		// �ݾ� �Ǵ� ����(�Ҽ��� 3�ڸ�)
		//this.setString("message", 48); 	// �޽���(����, �ִ� 48 byte)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KB : �����㰡(��������) (KH -> ODT) --//	
class TH_KB_DataStruct extends DataStruct {

	public TH_KB_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("trNo", 4);		// �ŷ���ȣ
		this.setString("wcc", 1);		// 'A'
		this.setString("basePrice1", 4);// ������ �ܰ�
		this.setString("basePrice2", 4);// ������ �ܰ�
		this.setString("liter", 7);		// ����(�Ҽ��� 3�ڸ�)
		this.setString("price", 7);		// �ݾ�
		this.setString("message", 48); 	// �޽���(����, �ִ� 48 byte)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- KC :  �����ܻ� �� �����Ա� ��û�� ���� �㰡 (KH -> ODT) --//	
class TH_KC_DataStruct extends DataStruct {

	public TH_KC_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		

		this.setString("command", 2);
		this.setString("trNo", 4);		// �ŷ���ȣ
		this.setString("wcc", 1);		// 'A'
		this.setString("basePrice1", 4);// ������ �ܰ�
		this.setString("basePrice2", 4);// ������ �ܰ�
		this.setString("liter", 7);		// ����(�Ҽ��� 3�ڸ�)
		this.setString("price", 7);		// �ݾ�
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KD : �ſ���ο�û (KH <- ODT) --//	
class TH_KD_DataStruct extends DataStruct {

	public TH_KD_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// �Ǹſ� ��ȣ
		this.setString("saleType", 1);	// �Ǹű���(A:�ݾ�, Q:����: F: Full tank)
		this.setString("value", 7);		// �ݾ� �Ǵ� ����(�Ҽ��� 3�ڸ�)
		this.setString("wcc1", 1);		// 'A'
		this.setString("cardLen1", 2);	// ī�����1
		this.setString("cardNo1", 38);	// ī���ȣ1(�ſ�ī��)
		this.setString("monthCnt", 2);	// �Һΰ�����
		this.setString("fs", 1);		// FS
		this.setString("wcc2", 1);		// 'A'
		this.setString("cardLen2", 2);	// ī�����2
		this.setString("cardNo2", 38);	// ī���ȣ2(���ʽ�ī��)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KE : ���ݽ��ο�û (KH <- ODT) --//	
class TH_KE_DataStruct extends DataStruct {

	public TH_KE_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// �Ǹſ� ��ȣ
		this.setString("saleType", 1);	// �Ǹű���(A:�ݾ�, Q:����, F:Full tank)
		this.setString("value", 7);		// �ݾ� �Ǵ� ����(�Ҽ��� 3�ڸ�)
		this.setString("wcc1", 1);		// 'A'	
		this.setString("cardLen1", 2);	// ī�����1
		this.setString("cardNo1", 38);	// ī���ȣ1
		this.setString("fs", 1);		// FS
		this.setString("wcc2", 1);		// 'A'
		this.setString("cardLen2", 2); 	// ���ݿ����� ���� ����
		this.setString("cardNo2", 38); 	// ���ݿ����� ����
		this.setString("target", 1); 	// ������(0:���ݿ����� �̹���, 1:�Һ���, 2:�����)
		this.setString("inputCash", 1); // �����Աݾ�
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- KF : �����Ա� ���� (KH <- ODT) --//	
class TH_KF_DataStruct extends DataStruct {

	public TH_KF_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// �Ǹſ� ��ȣ
		this.setString("saleType", 1);	// �Ǹű���(A:�ݾ�, Q:����, F:Full tank)
		this.setString("value", 7);		// �ݾ� �Ǵ� ����(�Ҽ��� 3�ڸ�)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- KG :  ���ΰź� (KH -> ODT) --//	
class TH_KG_DataStruct extends DataStruct {

	public TH_KG_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("errorCode", 4); // ������ȣ
		this.setString("message", 48); 	// �޽���(����, �ִ� 48 byte)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- KI : ����ODT �ܻ��ÿ�û�� ���� �ܻ������Ա� �㰡 (KH -> ODT) --//	
class TH_KI_DataStruct extends DataStruct {

	public TH_KI_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");		

		this.setString("command", 2);
		this.setString("trNo", 4);		// �ŷ���ȣ
		this.setString("wcc", 1);		// 'A'
		this.setString("basePrice1", 4);// ������ �ܰ�
		this.setString("basePrice2", 4);// ������ �ܰ�
		this.setString("liter", 7);		// ����(�Ҽ��� 3�ڸ�)
		this.setString("price", 7);		// �ݾ�
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KJ : ����ODT �ܻ��ÿ�û�� ���� �ܻ��㰡 (KH -> ODT) --//	
class TH_KJ_DataStruct extends DataStruct {

	public TH_KJ_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");		

		this.setString("command", 2);
		this.setString("trNo", 4);		// �ŷ���ȣ
		this.setString("wcc", 1);		// 'A'
		this.setString("basePrice1", 4);// ������ �ܰ�
		this.setString("basePrice2", 4);// ������ �ܰ�
		this.setString("liter", 7);		// ����(�Ҽ��� 3�ڸ�)
		this.setString("price", 7);		// �ݾ�
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- KK : �ܻ���ο�û (KH <- ODT) --//	
class TH_KK_DataStruct extends DataStruct {

	public TH_KK_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// �Ǹſ� ��ȣ
		this.setString("saleType", 1);	// �Ǹű���(A:�ݾ�, Q:����, F:Full tank)
		this.setString("value", 7);		// �ݾ� �Ǵ� ����(�Ҽ��� 3�ڸ�)
		this.setString("wcc1", 1);		// 'A'		
		this.setString("cardLen1", 2);	// ī�����1
		this.setString("cardNo1", 38);	// ī���ȣ1(��ī��)
		this.setString("fs", 1);		// FS
		this.setString("wcc2", 1);		// 'A'
		this.setString("cardLen2", 2);	// ī�����2
		this.setString("cardNo2", 38);	// ī���ȣ2(���ʽ�ī��)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KP : ����ODT �ɼǻ��� (KH -> ODT) --//	
class TH_KP_DataStruct extends DataStruct {

	public TH_KP_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("setSeller", 1);		// �Ǹſ� ��������
		this.setString("receiptType", 1);	// ������ ������
		this.setString("waitTime", 3);		// ���δ��ð�
		this.setString("storeCode", 5);		// �������ڵ�
		this.setString("introduce", 40);	// ODT �λ縻
		this.setString("receiptHead", 10);	// ������ ��� ������
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KU : ���ݰŷ�ó(���ݽ��ο�û) (KH <- ODT) ---//	
class TH_KU_DataStruct extends DataStruct {

	public TH_KU_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// �Ǹſ� ��ȣ
		this.setString("basePrice1", 4);// ������ �ܰ�
		this.setString("basePrice2", 4);// ������ �ܰ�	
		this.setString("liter", 7);		// ����
		this.setString("price", 7);		// �ݾ�
		this.setString("inputCash", 7);	// �����հ�ݾ�
		this.setString("cardLen1", 2);	// ī�����1
		this.setString("cardNo1", 38);	// ī���ȣ1(���ݿ�������ȣ)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KV : ���ݰŷ�ó(�ſ���ο�û) (KH <- ODT) --//	
class TH_KV_DataStruct extends DataStruct {

	public TH_KV_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// �Ǹſ� ��ȣ
		this.setString("basePrice1", 4);// ������ �ܰ�
		this.setString("basePrice2", 4);// ������ �ܰ�	
		this.setString("liter", 7);		// ����
		this.setString("price", 7);		// �ݾ�
		this.setString("wcc1", 1);		// 'A'		
		this.setString("cardLen1", 2);	// ī�����1
		this.setString("cardNo1", 38);	// ī���ȣ1
		this.setString("monthCnt", 2);	// �Һΰ�����
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KX : ���ݽ��ο�û-���ݿ����� ���� (KH <- ODT) --//	
class TH_KX_DataStruct extends DataStruct {

	public TH_KX_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// �Ǹſ� ��ȣ
		this.setString("saleType", 1);	// �Ǹű���(A:�ݾ�, Q:����, F:Full tank)
		this.setString("value", 7);		// �ݾ� �Ǵ� ����(�Ҽ��� 3�ڸ�)
		this.setString("wcc1", 1);		// 'A'		
		this.setString("cardLen1", 2);	// ī�����1
		this.setString("cardNo1", 38);	// ī���ȣ1
		this.setString("fs", 1);		// FS
		this.setString("wcc2", 1);		// 'A'
		this.setString("cardLen2", 2); 	// ���ݿ����� ���� ����
		this.setString("cardNo2", 38); 	// ���ݿ����� ����
		this.setString("target", 1); 	// ������(0:���ݿ����� �̹���, 1:�Һ���, 2:�����)
		this.setString("inputCash", 7); // �����Աݾ�
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- KY : ���ݰŷ�ó(���ݽ��ο�û-���ݿ�������ȣ) (KH <- ODT) --//	
class TH_KY_DataStruct extends DataStruct {

	public TH_KY_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// �Ǹſ� ��ȣ
		this.setString("basePrice1", 4);// ������ �ܰ�
		this.setString("basePrice2", 4);// ������ �ܰ�	
		this.setString("liter", 7);		// ����
		this.setString("price", 7);		// �ݾ�
		this.setString("wcc1", 1);		// 'A'		
		this.setString("cardLen1", 2);	// ī�����1
		this.setString("cardNo1", 38);	// ī���ȣ1(���ݿ�������ȣ)
		this.setString("target", 1);	// ������
		this.setString("inputCash", 7);	// �����հ�ݾ�
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- LA : �ܻ���ο�û-2 (KH <- ODT) --//	
class TH_LA_DataStruct extends DataStruct {

	public TH_LA_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// �Ǹſ� ��ȣ
		this.setString("saleType", 1);	// �Ǹű���(A:�ݾ�, Q:����, F:Full tank)
		this.setString("limitBase", 2);	// �ѵ�����
		this.setString("avaLimit", 18);	// �ܿ��ѵ�
		this.setString("value", 7);	    // ��û����/�ݾ�
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- LD : GS����Ʈ���ο�û (KH <- ODT) --//	
class TH_LD_DataStruct extends DataStruct {

	public TH_LD_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("nozzleNo", 2);
		this.setString("sellerNo", 3); 	// �Ǹſ� ��ȣ
		this.setString("saleType", 1);	// �Ǹű���(A:�ݾ�, Q:����, F:Full tank)
		this.setString("value", 7);		// �ݾ� �Ǵ� ����(�Ҽ��� 3�ڸ�)
		this.setString("wcc1", 1);		// 'A'		
		this.setString("cardLen1", 2);	// ī�����1
		this.setString("cardNo1", 37);	// ī���ȣ1(��ī��)
		this.setString("fs", 1);		// FS
		this.setString("pinLen", 2);	// ��й�ȣ ����
		this.setString("pin", 15);		// ��й�ȣ
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- LS : ����ODT �ܻ��ÿ�û�� ���� �ŷ�ó���� �۽�-�ܻ�ó���� ���� �ű��߰� (KH -> ODT) --//	
class TH_LS_DataStruct extends DataStruct {

	public TH_LS_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("trNo", 4);			// �ŷ���ȣ
		this.setString("wcc", 1);			// 'A'
		this.setString("basePrice1", 4);	// ������ �ܰ�
		this.setString("basePrice2", 4);	// ������ �ܰ�
		this.setString("liter", 7);			// ����(�Ҽ��� 3�ڸ�)
		this.setString("price", 7);			// �ݾ�
		this.setString("driveName", 20);	// �����ڸ�(�ŷ�ó��)
		this.setString("carNo", 18);		// ������ȣ
		this.setString("cardAdjInd", 02);	// ������
		this.setString("limitBase", 02);	// �ѵ�����
		this.setString("limit", 18);		// ���ѵ�
		this.setString("accLimit", 18);		// ��������ѵ�
		this.setString("message", 48);		// �޽���
				
		this.setByte("etx");
		this.setByte("bcc");
	}
}


//-- LT : POS ������ ���ÿ�û (KH -> ODT) --//	
class TH_LT_DataStruct extends DataStruct {

	public TH_LT_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("trNo", 4);		// �ŷ���ȣ
		this.setString("wcc", 1);		// 
		this.setString("value", 2);		// ���� �Ǵ� �ݾ�
		this.setString("message", 2);	// �޽���(����)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- NG : �۾���û �ź� (KH -> ODT) --//	
class TH_NG_DataStruct extends DataStruct {

	public TH_NG_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");
		
		this.setString("command", 2);
		this.setString("message", 48); // �޽���(����, �ִ� 48 byte)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- NR : ������ �����û (KH <- ODT) --//	
class TH_NR_DataStruct extends DataStruct {

	public TH_NR_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("trNo", 4);		// �ŷ���ȣ
		this.setString("currPage", 3);	// ����������
		this.setString("totalPage", 1);	// ��ü������
		this.setString("data", 4);		// ������(����, �ִ� 1024 byte)
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- OT : ODT �������� (KH <- ODT) --//	
class TH_OT_DataStruct extends DataStruct {

	public TH_OT_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("datas", 10);	
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- PS : ������ ������ �۽� (KH  ODT) --//	
class TH_PS_DataStruct extends DataStruct {

	public TH_PS_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("trNo", 4);		// �ŷ���ȣ
		this.setString("currPage", 1);	// ����������
		this.setString("totPage", 1);	// ��ü������
		this.setString("content", 1024);	// ������ ������
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- SP : ������ ���� �۽� (KH -> ODT) --//	
class TH_SP_DataStruct extends DataStruct {

	public TH_SP_DataStruct() throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("nozzleNo", 2);	// �������ȣ
		this.setString("state", 2);		// ����(AQ:�����㰡 ��û, LK:���ٿ�, PP:������, UL:����� �Ǵ� �ٿ�, ���õ�
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

//-- YL : �������� �۽� (KH -> ODT) --//	
class TH_YL_DataStruct extends DataStruct {

	public TH_YL_DataStruct(int nozCnt) throws Exception {
		this.setByte("soh");
		this.setString("odtNo", 2);
		this.setByte("stx");

		this.setString("command", 2);
		this.setString("oilCode", 4);	// �����ڵ�
		this.setString("oilName", 10);	// ������
		//this.setString("reserved", 6);	// �ǹ̾��� ������
		for(int i=0; i<nozCnt; i++)
			this.setString("nozzleNo" + i, 2);	// ������ ��ȣ
		
		this.setByte("etx");
		this.setByte("bcc");
	}
}

public class TsnSelfHSDS {
	
	/**
	 * �������� ���� DataStruct�� ��´�. 
	 * 
	 * @param command	: ��� �ڵ� 
	 * @return
	 */
	public static DataStruct getDS(String command) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("IQ")) {
				returnData = new TH_IQ_DataStruct();
			} else if (command.equals("KD")) {
				returnData = new TH_KD_DataStruct();
			} else if (command.equals("KK")) {
				returnData = new TH_KK_DataStruct();
			} else if (command.equals("LA")) {
				returnData = new TH_LA_DataStruct();
			} else if (command.equals("KE")) {
				returnData = new TH_KE_DataStruct();
			} else if (command.equals("KX")) {
				returnData = new TH_KX_DataStruct();
			} else if (command.equals("KU")) {
				returnData = new TH_KU_DataStruct();
			} else if (command.equals("KY")) {
				returnData = new TH_KY_DataStruct();
			} else if (command.equals("KV")) {
				returnData = new TH_KV_DataStruct();
			} else if (command.equals("KF")) {
				returnData = new TH_KF_DataStruct();
			} else if (command.equals("KA")) {
				returnData = new TH_KA_DataStruct();
			} else if (command.equals("KB")) {
				returnData = new TH_KB_DataStruct();
			} else if (command.equals("NG")) {
				returnData = new TH_NG_DataStruct();
			} else if (command.equals("KG")) {
				returnData = new TH_KG_DataStruct();
			} else if (command.equals("KC")) {
				returnData = new TH_KC_DataStruct();
			} else if (command.equals("KI")) {
				returnData = new TH_KI_DataStruct();
			} else if (command.equals("KJ")) {
				returnData = new TH_KJ_DataStruct();
			} else if (command.equals("LS")) {
				returnData = new TH_LS_DataStruct();
			} else if (command.equals("LD")) {
				returnData = new TH_LD_DataStruct();
			} else if (command.equals("NR")) {
				returnData = new TH_NR_DataStruct();
			} else if (command.equals("OT")) {
				returnData = new TH_OT_DataStruct();
			} else if (command.equals("ES")) {
				returnData = new TH_ES_DataStruct();
			} else if (command.equals("GD")) {
				returnData = new TH_GD_DataStruct();
			} else if (command.equals("SP")) {
				returnData = new TH_SP_DataStruct();
			} else if (command.equals("LT")) {
				returnData = new TH_LT_DataStruct();
			} else if (command.equals("FC")) {
				returnData = new TH_FC_DataStruct();
			} else if (command.equals("KP")) {
				returnData = new TH_KP_DataStruct();
				
			} else {
				returnData = null;
			} // end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}
	
	public static DataStruct getDS(String command, int trTypeSize) {
		DataStruct returnData = null;
		
		try {
			if (command.equals("YL")) {
				returnData = new TH_YL_DataStruct(trTypeSize);
			} else if (command.equals("JP")) {
				returnData = new TH_JP_DataStruct(trTypeSize);
			} else {
				returnData = null;
			}	// end if
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return returnData;
		
	}	// end getDS
	
	/*
	 *  ODT_NO ��ȯ : POS -> ODT
	 *  71 (POS) -> S1 (ODT)
	 */
	public static String getOdtNo_forODT (String odtNo) {
		
		int nOdtNo = Integer.parseInt(odtNo) - 70;
		
		return "S" + Change.binToHex (nOdtNo);
	}


	/*
	 *  ODT_NO ��ȯ : ODT -> POS
	 *  S1 (ODT) -> 71 (POS)
	 */
	public static String getOdtNo_forPOS (String odtNo) {
		
		String odtStr = odtNo.substring(1,2);
				
		return Change.toString(70 + Change.hexToBin (odtStr));
	}	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		LogUtility.getPumpALogger().debug("for ODT======" + getOdtNo_forODT("83"));

		LogUtility.getPumpALogger().debug("for POS======" + getOdtNo_forPOS("SD"));
	}
}

