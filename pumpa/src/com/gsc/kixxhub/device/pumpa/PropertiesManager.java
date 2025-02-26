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
			LogUtility.getPumpALogger().debug("������ ���������� �����ϴ�.");
			return;
		} 
		
		for(int i = 0; i < list.size(); i++) {
			PropNozInfo noz = list.get(i);
			LogUtility.getPumpALogger().debug("�����ȣ : " + noz.getNozNo());
			LogUtility.getPumpALogger().debug("��Ʈ��ȣ : " + noz.getPortNo());
			LogUtility.getPumpALogger().debug("�������� : " + noz.getProtocol());
			LogUtility.getPumpALogger().debug("��żӵ� : " + noz.getBaudRate());
			LogUtility.getPumpALogger().debug("����̽� ���� :" + noz.getDeviceType());
			LogUtility.getPumpALogger().debug("����odt : " + noz.getOdtNo());
			LogUtility.getPumpALogger().debug("���� : " + noz.getOil());
			LogUtility.getPumpALogger().debug("�ܰ� : " + noz.getBasePrice());
			LogUtility.getPumpALogger().debug("�� ���� : " + noz.getRomVersion());
			LogUtility.getPumpALogger().debug("���Ź��۴��ð� : " + noz.getReadBufferInterval());
			LogUtility.getPumpALogger().debug("���Ž��۴��ð� : " + noz.getReadStartInterval());
			LogUtility.getPumpALogger().debug("�۽Ž��۴��ð� : " + noz.getWriteStartInterval());
			LogUtility.getPumpALogger().debug("ȸ���ҷ�����Ƚ�� : " + noz.getLineErrorCount());
			LogUtility.getPumpALogger().debug("ȸ���ҷ���Ȯ��Ƚ�� : " + noz.getLineErrorSkipCount());
			LogUtility.getPumpALogger().debug("���׼��� : " + noz.isSetLiter());
			LogUtility.getPumpALogger().debug("�������� : " + noz.isSetPrice());
			LogUtility.getPumpALogger().debug("������ �� : " + noz.getPresetValue());
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
