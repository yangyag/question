package com.gsc.kixxhub.device.pumpa.driver;

import gnu.io.SerialPortEvent;

import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;

import com.gsc.kixxhub.common.data.pump.S8_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.SE_WorkingMessage;
import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.BytesQue2;
import com.gsc.kixxhub.device.pumpa.common.Change;
import com.gsc.kixxhub.device.pumpa.common.SerialConnectException;
import com.gsc.kixxhub.device.pumpa.common.Sleep;
import com.gsc.kixxhub.device.pumpa.define.CCommDriver;

public class CommTatsunoMPPL_vODT extends CCommDriver {

	protected int	baseMinErrCnt=5, baseMaxSkipCnt=200;
	protected int	baseReadBuffInterval   = 50+3; // 스마트주유소 PJT 보정
	
	protected int	baseReadStartInterval  = 0+3; // 스마트주유소 PJT 보정
	
    protected int	baseWriteStartInterval = 6+3; // 스마트주유소 PJT 보정
    protected int 	dispLevel=0;
    protected boolean firstRequest=true;

	protected int 		m_statusCode=601;
	protected String	m_targetNozzle;
    //progressStep : 0=초기화전, 1=초기화후, 2=상태정보송신후, 3=주유중, 4=주유완료, 
    //               5=토털게이지수신/주유완료자료 송신
    protected int 	progressStep=3;
	protected S8_WorkingMessage S8_wm;
	    
	protected SE_WorkingMessage SE_wm;    
    protected BytesQue2 TxQue = new BytesQue2(30);		
		
	public CommTatsunoMPPL_vODT (int nozNum, String romVerStr, int nMPPCount) {
				
	}
		
	protected boolean isSkipWorkingMessage (WorkingMessage wm) throws Exception {
			
		return true;
	}
	
	protected void makeLineError () throws Exception {
		
		SE_wm = new SE_WorkingMessage();
		
		SE_wm.setNozzleNo(Change.toString("%02d", nozNo));
		SE_wm.setDeviceType(Change.toString("%02d", nozType));
		SE_wm.setStatus("1");
		SE_wm.setStatusCode("601"); // 회선불량
		SE_wm.setErrMsg("셀프ODT 회선불량");

		m_statusCode=601; // appended

		Calendar cal = new GregorianCalendar();
		Formatter form = new Formatter();
		String year = Change.toString("%04d", cal.get(Calendar.YEAR));
		form.format("%s%02d%02d%02d%02d%02d", year.substring(2, 4),
				cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 
				cal.get(Calendar.SECOND));
		String timeStr = form.toString();		
		SE_wm.setDetectTime(timeStr);
		
		insertRecvQueue(SE_wm);
	}
	
	protected void makeStatusInfo(int nozState) throws Exception {

		S8_wm = new S8_WorkingMessage();
		int		statusCode=0;
		int		thisNozType=nozType;
		String 	errorMsg="";
		boolean isNozStatus=false;
		
		switch (nozState) {
				
			case 650:
				statusCode = 650; // 정상
				errorMsg = "가상 셀프ODT 정상";
				break;
		}
		m_statusCode = statusCode;
		
		if (isNozStatus==true) {
			if (m_targetNozzle!=null)
				S8_wm.setNozzleNo(m_targetNozzle);
		} else
			S8_wm.setNozzleNo(Change.toString("%02d", nozNo));
		
		S8_wm.setDeviceType(Change.toString("%02d", thisNozType));
		S8_wm.setStatus("1");
		S8_wm.setStatusCode(Change.toString("%03d", statusCode)); 
		S8_wm.setErrMsg(errorMsg);
		
		Calendar cal = new GregorianCalendar();
		Formatter form = new Formatter();
		String year = Change.toString("%04d", cal.get(Calendar.YEAR));
		form.format("%s%02d%02d%02d%02d%02d", year.substring(2, 4),
				cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 
				cal.get(Calendar.SECOND));
		String timeStr = form.toString();
		S8_wm.setDetectTime(timeStr);

		insertRecvQueue(S8_wm);
	}
		
	public void ownershipChange(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void requestData() throws Exception, SerialConnectException {

		if (firstRequest==true) {
			firstRequest=false;
			makeStatusInfo(650);
		}
		
		Sleep.sleep(20);
		
		try {
			
			//--- Transfer from WorkingMessage to BytesStream
			while (sndQue.getItemCount() > 0) {
				WorkingMessage wm = (WorkingMessage)sndQue.deQueue();
				
				LogUtility.getPumpALogger().debug("\n>>>>> Received  Down data : " +
									"ODT_No=" + nozNo + " command=" + wm.getCommand());
	
				if (isSkipWorkingMessage(wm)==true) {
					wm=null;
					continue;
				}
			}
			
			if (dispLevel>=2)
				LogUtility.getPumpALogger().debug("\nStart request ===========> (ODT="+nozNo+")");

		} catch (Exception e) {
			LogUtility.getPumpALogger().error("Exception occurr! (ODT="+nozNo+")");
			LogUtility.getPumpALogger().error(e.getMessage(), e);
		}
	}

	public void run() {

	}

	// To be invoked when InputStream(is) has a receiving data
	public void serialEvent(SerialPortEvent event) {

	}
}
