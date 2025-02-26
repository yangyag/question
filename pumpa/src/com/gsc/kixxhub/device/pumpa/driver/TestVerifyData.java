package com.gsc.kixxhub.device.pumpa.driver;

import java.util.zip.CRC32;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Change;

public class TestVerifyData {

	static byte ETX = 0x03;
	static int m_nLastPrice=0;
	
	static int m_nPlusPrice=0;
	static byte STX = 0x02;
	
	protected static int calculateBasePrice(int i , int price, int liter) throws Exception {
		
		int   basePrice=0;
		
		// price=읽은값, m_nLastPrice=이전값
		if (price - m_nLastPrice <= -90000)
			m_nPlusPrice += 100000;
		
		if (liter>0)
			basePrice = (price+m_nPlusPrice) / liter;
		
		LogUtility.getPumpALogger().debug("\ni="+i+" Price="+price+" Liter="+liter+ " diff="+(price-m_nLastPrice));
		LogUtility.getPumpALogger().debug("m_nLastPrice="+m_nLastPrice+" m_nPlusPrice="+m_nPlusPrice+
				" nBasePrice=" + basePrice);

		m_nLastPrice = price;

		return basePrice;
	}
	
	protected static void calculateNozNo () { // 다쓰노 구형
		
		int baseNozNo=0;
		int nozNo1_16=0;
		int	nozNo;
		
		for (nozNo=1; nozNo<=18; nozNo++) {
			
			baseNozNo = (nozNo - 1) / 16;
			nozNo1_16 = nozNo - (baseNozNo * 16);
			
			LogUtility.getPumpALogger().debug("nozNo=" + nozNo + " nozNo1_16=" + nozNo1_16 + " baseNozNo=" + baseNozNo);
		}
	}
	
	protected static byte getBCC(byte[] dat) throws Exception {
		
		byte 	bcc=0;
		int 	stxIdx=0, etxIdx=0, i, j;
		
		for (i=0; i<dat.length-1; i++) {
			if (dat[i]==STX) 
				break;
		}
		stxIdx = i + 1; // start from STX + 3
		
		for(j=i; j<dat.length-1; j++) {
			if (dat[j]==ETX)
				break;
		}
		etxIdx = j;
		
		if (stxIdx<=0 || etxIdx<=0) // No exist STX, ETX
			return -1;
		
		//--- Calculate BCC
		for (i=stxIdx; i<=etxIdx; i++) {
			bcc = (byte) (bcc ^ dat[i]); // XOR
		}
		
		return bcc;
	}
	
	protected static byte getBCC2(byte[] dat) throws Exception {
		
		byte 	bcc=0;
		int 	stxIdx=0, etxIdx=0, i, j;
		
		for (i=0; i<dat.length-1; i++) {
			if (dat[i]==STX) 
				break;
		}
		stxIdx = i + 1; // start from STX + 3
		
		for(j=i; j<dat.length-1; j++) {
			if (dat[j]==ETX)
				break;
		}
		etxIdx = j;
		
		if (stxIdx<=0 || etxIdx<=0) // No exist STX, ETX
			return -1;
		
		//--- Calculate BCC
		for (i=stxIdx; i<=etxIdx; i++) {
			bcc = Change.getUnsignedByte((byte) (bcc ^ dat[i])); // XOR
		}
		
		return bcc;
	}
	
	protected static long getCRC32(byte[] dat) throws Exception {
		long crc=0;
		
		CRC32 crc32 = new CRC32();
		
		crc32.update(dat);
		crc = crc32.getValue();
		
		return crc;
	}
	
	public static int getPumpingNozzleCnt(byte flag) throws Exception {
		int cnt=0;
		byte b=0x01;
		
		for (int i=0; i<6; i++) {
			if ((flag & b) != 0)
				cnt++;
			b = (byte) (b << 1);
		}
		
		return cnt;
	}
	
	public static boolean isPumpingNow(byte flag, int index) throws Exception {
		
		byte b=0x01;

		b = (byte) (b << index);
		
		return ((flag & b) != 0? true: false);
	}
	
	public static void main(String[] args) {
		// TODO 자동 생성된 메소드 스텁

		byte[] data_WooJoo = new byte[30];
		byte[] data_TnsN = new byte[60];
		
		//--- Start WooJoo ---//
		/*
		data_WooJoo[0] = 'A';
		data_WooJoo[1] = STX;
		data_WooJoo[2] = '0';
		data_WooJoo[3] = '1';
		data_WooJoo[4] = 's';
		data_WooJoo[5] = '0';
		data_WooJoo[6] = '3';
		data_WooJoo[7] = ETX;
		data_WooJoo[8] = 'B';
		*/

		/*
		data_WooJoo[0] = 'A';
		data_WooJoo[1] = STX;
		data_WooJoo[2] = '0';
		data_WooJoo[3] = '1';
		data_WooJoo[4] = 'p';
		data_WooJoo[5] = '0';
		data_WooJoo[6] = '2';

		data_WooJoo[7] = '2';
		data_WooJoo[8] = '2';
		data_WooJoo[9] = '1';
		data_WooJoo[10] = '8';
		data_WooJoo[11] = '5';
		data_WooJoo[12] = '2';
		data_WooJoo[13] = '2';
		
		data_WooJoo[14] = '9';
		data_WooJoo[15] = '2';
		data_WooJoo[16] = '2';
		data_WooJoo[17] = '0';
		
		data_WooJoo[18] = '9';
		data_WooJoo[19] = '0';
		data_WooJoo[20] = '2';
		data_WooJoo[21] = '2';
		data_WooJoo[22] = '9';
		data_WooJoo[23] = '2';
		
		data_WooJoo[24] = ETX;
		data_WooJoo[25] = 'B';
		*/
		/*
		data_WooJoo[0] = 'A';
		data_WooJoo[1] = STX;
		data_WooJoo[2] = '0';
		data_WooJoo[3] = '1';
		data_WooJoo[4] = 't';
		data_WooJoo[5] = '0';

		data_WooJoo[6] = '2';
		data_WooJoo[7] = '2';
		data_WooJoo[8] = '1';
		data_WooJoo[9] = '8';
		data_WooJoo[10] = '5';
		data_WooJoo[11] = '9';
		data_WooJoo[12] = '2';
		data_WooJoo[13] = '9';
		data_WooJoo[14] = '0';
		data_WooJoo[15] = '2';
		
		data_WooJoo[16] = ETX;
		data_WooJoo[17] = 'B';
		*/
		//--- End WooJoo ---//
		
		//--- Start TatsunoN ---//
		

		//--- End TatsunoN ---//
		
		
		int mode = 0;
		boolean rtn=false;
		
		try {
			
			class TestPrint {
				
				String mode = "";

				public String getMode() {
					return mode;
				}

				public void setMode(String mode) {
					this.mode = mode;
				}
			}
			
			TestPrint tp = new TestPrint();
			tp.setMode(null);
			LogUtility.getPumpALogger().info("=============" + tp.getMode());
			
			/*
			switch(mode) {
			case 0 : // 우주

				data_WooJoo[40] = 'B';
				rtn = verifyData_WooJoo(data_WooJoo);
				break;
			case 1 : // 다쓰노신형
				rtn = verifyData_TatsunoN(data_TnsN);
				break;
			}
			LogUtility.getPumpALogger().debug("\nresult====" + rtn);
			*/
			/*
			byte byt = (byte) 0x29;
			int N = getPumpingNozzleCnt(byt);
			LogUtility.getPumpALogger().debug("N======" + N);
			
			boolean pumpingNow = isPumpingNow(byt, 0);
			LogUtility.getPumpALogger().debug("pumpingNow======" + pumpingNow);
			*/
			
			// 10만원 이상시 단가 보정처리(Tokoco 구형)
			/*
			String[] sPrice = new String[10];
			sPrice[0] = "000000";
			sPrice[1] = "010000";
			sPrice[2] = "090000";
			sPrice[3] = "095000";
			sPrice[4] = "000000";
			sPrice[5] = "005000";
			sPrice[6] = "010000";
			sPrice[7] = "099000";
			sPrice[8] = "003000";
			sPrice[9] = "010000";
			
			String[] sLiter = new String[10];
			sLiter[0] = "0000000";
			sLiter[1] = "0010000";
			sLiter[2] = "0090000";
			sLiter[3] = "0095000";
			sLiter[4] = "0100000";
			sLiter[5] = "0105000";
			sLiter[6] = "0110000";
			sLiter[7] = "0199000";
			sLiter[8] = "0203000";
			sLiter[9] = "0210000";

			for (int i=0; i< 10; i++) {
				int price = Change.toValue(sPrice[i]);
				int liter = Change.toValue(sLiter[i].substring(0,4));
				int basePrice = calculateBasePrice(i, price, liter);
				
				LogUtility.getPumpALogger().debug("Price="+sPrice[i]+" sLiter="+sLiter[i]+
						" basePrice="+Change.toString("%04d00", basePrice));
				
				//Sleep.sleep(500);
			}
			*/
			
			//calculateNozNo();
			
			//testUnsigned();
			
			/*
			//byte[] byt = {'G', 0x02, '0', '7', 's', '0', 0x03, ' '};
			byte[] byt = {'S', 0x02, 
					Change.getUnsignedByte((byte)0x00), Change.getUnsignedByte((byte)0xff),
					Change.getUnsignedByte((byte)0x00), Change.getUnsignedByte((byte)0xFF), 
					Change.getUnsignedByte((byte)0x00), Change.getUnsignedByte((byte)0xFF), 
					Change.getUnsignedByte((byte)0x00), 
					0x03, ' '};
			byte bcc1 = getBCC (byt);
			byte bcc2 = getBCC2 (byt);
			System.out.printf("bcc1===0x%02X (%d)\n", bcc1, bcc1);
			System.out.printf("bcc2===0x%02X (%d)\n", bcc2, bcc2);
			
			
			long crc = getCRC32(byt);
			LogUtility.getPumpALogger().debug("crc===" + crc);
			*/
			
			
		} catch (Exception e) {
			// TODO 자동 생성된 catch 블록
//			LogUtility.getPumpALogger().debug("\nError====>>>" + e);
//			e.printStackTrace();
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}
	
	protected static void testUnsigned() throws Exception {
		
		byte by = (byte) 0x87;
		//byte by = Change.getUnsignedByte((byte)0x80);
		//byte by = (byte) (0xFF ^ 0x00);

		LogUtility.getPumpALogger().debug("by=" + by);
		LogUtility.getPumpALogger().debug("getUnsignedByte((byte)by)="+ Change.getUnsignedByte(by));
		System.out.printf ("getUnsignedByte((byte)by)=%x\n", Change.getUnsignedByte(by));
		System.out.printf ("by=0x%02X\n", by);
		//System.out.printf ("by===%02d\n", by);
		if (by == (byte) 0x87) 
			System.out.printf ("result=true\n");
		else
			System.out.printf ("result=false\n");
	}
	

	protected static boolean verifyData_TatsunoN (byte[] buf) throws Exception {
		
		int size=0, bufLen;
		String cmd="";
		
		for (bufLen=0; bufLen<buf.length; bufLen++)
			if (buf[bufLen]==ETX) break;
		bufLen+=2;
		
		LogUtility.getPumpALogger().debug("bufLen==" +bufLen);
		
		if (buf[3]=='6' && buf[4]=='0') { // 주유기 상태정보 수신
			size = 9;
			cmd = "60";
		}
		else if (buf[4]=='6' && buf[5]=='1') { // 주유자료 수신
			size = 26;
			cmd = "p0";
		}
		else if (buf[4]=='6' && buf[5]=='5') { // 토컬게이지 자료 수신
			size = 18;
			cmd = "t0";
		}
			
		//--- 데이터 검증(데이터 길이와 ETX 확인) ---//
		if (size != bufLen || buf[size-2] != ETX) {
			LogUtility.getPumpALogger().debug("이상데이터 수신("+cmd+"), 실데이터길이=" + size + ", 수신데이터길이=" + bufLen + ", ETX=" + buf[size-1]);
			return false;
		}

		//--- 데이터 검증(숫자여부) ---//
		for (int i=6; i<size-2; i++) {
			if (buf[i] < 0x30 || buf[i] > 0x39) { // 0 ~ 9 가 아니면
				LogUtility.getPumpALogger().debug("이상데이터 수신("+cmd+"), 숫자가 아님, 수신데이터=" + buf[i]);
				return false;
			}
		}
		
		return true;
	}
		
	protected static boolean verifyData_WooJoo (byte[] buf) throws Exception {
		
		int size=0, bufLen;
		String cmd="";
		
		for (bufLen=0; bufLen<buf.length; bufLen++)
			if (buf[bufLen]==ETX) break;
		bufLen+=2;
		
		LogUtility.getPumpALogger().debug("bufLen==" +bufLen);
		
		if (buf[4]=='s' && buf[5]=='0') { // 주유기 상태정보 수신
			size = 9;
			cmd = "s0";
		}
		else if (buf[4]=='p' && buf[5]=='0') { // 주유자료 수신
			size = 26;
			cmd = "p0";
		}
		else if (buf[4]=='t' && buf[5]=='0') { // 토컬게이지 자료 수신
			size = 18;
			cmd = "t0";
		}
			
		//--- 데이터 검증(데이터 길이와 ETX 확인) ---//
		if (size != bufLen || buf[size-2] != ETX) {
			LogUtility.getPumpALogger().debug("이상데이터 수신("+cmd+"), 실데이터길이=" + size + ", 수신데이터길이=" + bufLen + ", ETX=" + buf[size-1]);
			return false;
		}

		//--- 데이터 검증(숫자여부) ---//
		for (int i=6; i<size-2; i++) {
			if (buf[i] < 0x30 || buf[i] > 0x39) { // 0 ~ 9 가 아니면
				LogUtility.getPumpALogger().debug("이상데이터 수신("+cmd+"), 숫자가 아님, 수신데이터=" + buf[i]);
				return false;
			}
		}
		
		return true;
	}
}
