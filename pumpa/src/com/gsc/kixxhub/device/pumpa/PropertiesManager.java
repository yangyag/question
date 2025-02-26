package com.gsc.kixxhub.device.pumpa;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import com.gsc.kixxhub.common.utility.log.LogUtility;

public class PropertiesManager { 

	//PropertiesManager test
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		PropertiesManager pm = new PropertiesManager();
		
		pm.loadProp(); // load properties
		ArrayList<PropNozInfo> list = pm.getNozzleInfos(); // get nozzles information
		
		if(list == null) {
			LogUtility.getPumpALogger().debug("설정된 노즐정보가 없습니다.");
			return;
		} 
		
		for(int i = 0; i < list.size(); i++) {
			PropNozInfo noz = list.get(i);
			LogUtility.getPumpALogger().debug("노즐번호 : " + noz.getNozNo());
			LogUtility.getPumpALogger().debug("포트버호 : " + noz.getPortNo());
			LogUtility.getPumpALogger().debug("프로토콜 : " + noz.getProtocol());
			LogUtility.getPumpALogger().debug("통신속도 : " + noz.getBaudRate());
			LogUtility.getPumpALogger().debug("디바이스 종류 :" + noz.getDeviceType());
			LogUtility.getPumpALogger().debug("연결odt : " + noz.getOdtNo());
			LogUtility.getPumpALogger().debug("유종 : " + noz.getOil());
			LogUtility.getPumpALogger().debug("단가 : " + noz.getBasePrice());
			LogUtility.getPumpALogger().debug("롬 버전 : " + noz.getRomVersion());
			LogUtility.getPumpALogger().debug("수신버퍼대기시간 : " + noz.getReadBufferInterval());
			LogUtility.getPumpALogger().debug("수신시작대기시간 : " + noz.getReadStartInterval());
			LogUtility.getPumpALogger().debug("송신시작대기시간 : " + noz.getWriteStartInterval());
			LogUtility.getPumpALogger().debug("회선불량기준횟수 : " + noz.getLineErrorCount());
			LogUtility.getPumpALogger().debug("회선불량재확인횟수 : " + noz.getLineErrorSkipCount());
			LogUtility.getPumpALogger().debug("정액설정 : " + noz.isSetLiter());
			LogUtility.getPumpALogger().debug("정량설정 : " + noz.isSetPrice());
			LogUtility.getPumpALogger().debug("프리셋 값 : " + noz.getPresetValue());
			LogUtility.getPumpALogger().debug("");
		}
	}
	
	/**
	 * 
	 */
	private ArrayList<PropNozInfo> nozInfos = new ArrayList<PropNozInfo>();
	
	/**
	 * 
	 */
	public Properties prop = new Properties();
	
	/**
	 * 
	 */
	private final String propFileName = "./com/gsc/kixxhub/adaptor/pump/config.properties";
	
	/**
	 * @return
	 */
	public ArrayList<PropNozInfo> getNozzleInfos() {
		return nozInfos;
	}
	
	/**
	 * 
	 */
	public void loadProp() {
		try {
			
			FileInputStream in = new FileInputStream(propFileName);
			prop.load(in);
			in.close();
			
			setNozzleInfo();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 */
	private void setNozzleInfo() {
		
		String z = "nozzle.";
		for(int i = 1; i <= 20; i++) {
			String str = prop.getProperty(z+i);
			
			if(str != null) {
				if(str.charAt(0) != '0') {
					PropNozInfo noz = new PropNozInfo(str.split(":"));
					nozInfos.add(noz);
				}
			}
			
		}
	}
}
