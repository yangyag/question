package com.gsc.kixxhub.device.pumpa;

public class PropNozInfo { 

	/**
	 * 
	 */
	private String basePrice;				// 단가
	/**
	 * 
	 */
	private String baudRate;				//통신속도
	/**
	 * 
	 */
	private String deviceType;				// 디바이스 종류
	/**
	 * 
	 */
	private boolean isSetLiter;
	/**
	 * 
	 */
	private boolean isSetPrice;
	/**
	 * 
	 */
	private boolean isUse;
	/**
	 * 
	 */
	private String lineErrorCount;			// 회선불량기준횟수
	/**
	 * 
	 */
	private String lineErrorSkipCount;		// 회선불량재확인횟수
	/**
	 * 
	 */
	private String nozNo;					//노즐번호
	/**
	 * 
	 */
	private String odtNo;					// 연결odt
	/**
	 * 
	 */
	private String oil;						// 유종
	/**
	 * 
	 */
	private String portNo;					//port번호
	/**
	 * 
	 */
	private String presetValue;		// 프리셋값
	/**
	 * 
	 */
	private String protocol;				//프로토콜
	/**
	 * 
	 */
	private String readBufferInterval;		// 수신버퍼대기시간
	/**
	 * 
	 */
	private String readStartInterval;		// 수신시작대기시간
	/**
	 * 
	 */
	private String romVersion;				// 롬 버전
	/**
	 * 
	 */
	private String writeStartInterval;		// 송신시작대기시간
	
	/**
	 * @param infos
	 */
	public PropNozInfo(String[] infos) {
		
		if("1".equals(infos[0])) isUse = true;
		else isUse = false;
		nozNo = infos[1];
		portNo = infos[2];
		protocol = infos[3];
		baudRate = infos[4];
		deviceType = infos[5];
		odtNo = infos[6];
		oil = infos[7];
		basePrice = infos[8];
		romVersion = infos[9];
		readBufferInterval = infos[10];
		readStartInterval = infos[11];
		writeStartInterval = infos[12];
		lineErrorCount = infos[13];
		lineErrorSkipCount = infos[14];
		if("1".equals(infos[15])) isSetLiter = true;
		else isSetLiter = false;
		if("1".equals(infos[16])) isSetPrice = true;
		else isSetPrice = false;
		presetValue = infos[17];
		
	}
	/**
	 * @return
	 */
	public String getBasePrice() {
		return basePrice;
	}
	/**
	 * @return
	 */
	public String getBaudRate() {
		return baudRate;
	}
	/**
	 * @return
	 */
	public String getDeviceType() {
		return deviceType;
	}
	/**
	 * @return
	 */
	public String getLineErrorCount() {
		return lineErrorCount;
	}
	/**
	 * @return
	 */
	public String getLineErrorSkipCount() {
		return lineErrorSkipCount;
	}
	/**
	 * @return
	 */
	public String getNozNo() {
		return nozNo;
	}
	/**
	 * @return
	 */
	public String getOdtNo() {
		return odtNo;
	}
	/**
	 * @return
	 */
	public String getOil() {
		return oil;
	}
	/**
	 * @return
	 */
	public String getPortNo() {
		return portNo;
	}
	/**
	 * @return the presetValue
	 */
	public String getPresetValue() {
		return presetValue;
	}
	/**
	 * @return
	 */
	public String getProtocol() {
		return protocol;
	}
	/**
	 * @return
	 */
	public String getReadBufferInterval() {
		return readBufferInterval;
	}
	/**
	 * @return
	 */
	public String getReadStartInterval() {
		return readStartInterval;
	}
	/**
	 * @return
	 */
	public String getRomVersion() {
		return romVersion;
	}
	/**
	 * @return
	 */
	public String getWriteStartInterval() {
		return writeStartInterval;
	}
	/**
	 * @return the isSetLiter
	 */
	public boolean isSetLiter() {
		return isSetLiter;
	}
	/**
	 * @return the isSetPrice
	 */
	public boolean isSetPrice() {
		return isSetPrice;
	}
	/**
	 * @return
	 */
	public boolean isUse() {
		return isUse;
	}
	/**
	 * @param basePrice
	 */
	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
	}
	/**
	 * @param baudRate
	 */
	public void setBaudRate(String baudRate) {
		this.baudRate = baudRate;
	}
	/**
	 * @param deviceType
	 */
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	/**
	 * @param lineErrorCount
	 */
	public void setLineErrorCount(String lineErrorCount) {
		this.lineErrorCount = lineErrorCount;
	}
	/**
	 * @param lineErrorSkipCount
	 */
	public void setLineErrorSkipCount(String lineErrorSkipCount) {
		this.lineErrorSkipCount = lineErrorSkipCount;
	}
	/**
	 * @param nozNo
	 */
	public void setNozNo(String nozNo) {
		this.nozNo = nozNo;
	}
	/**
	 * @param odtNo
	 */
	public void setOdtNo(String odtNo) {
		this.odtNo = odtNo;
	}
	/**
	 * @param oil
	 */
	public void setOil(String oil) {
		this.oil = oil;
	}
	/**
	 * @param portNo
	 */
	public void setPortNo(String portNo) {
		this.portNo = portNo;
	}
	/**
	 * @param presetValue the presetValue to set
	 */
	public void setPresetValue(String presetValue) {
		this.presetValue = presetValue;
	}
	/**
	 * @param protocol
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	/**
	 * @param readBufferInterval
	 */
	public void setReadBufferInterval(String readBufferInterval) {
		this.readBufferInterval = readBufferInterval;
	}
	/**
	 * @param readStartInterval
	 */
	public void setReadStartInterval(String readStartInterval) {
		this.readStartInterval = readStartInterval;
	}
	/**
	 * @param romVersion
	 */
	public void setRomVersion(String romVersion) {
		this.romVersion = romVersion;
	}
	/**
	 * @param isSetLiter the isSetLiter to set
	 */
	public void setSetLiter(boolean isSetLiter) {
		this.isSetLiter = isSetLiter;
	}
	/**
	 * @param isSetPrice the isSetPrice to set
	 */
	public void setSetPrice(boolean isSetPrice) {
		this.isSetPrice = isSetPrice;
	}
	/**
	 * @param isUse
	 */
	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}
	/**
	 * @param writeStartInterval
	 */
	public void setWriteStartInterval(String writeStartInterval) {
		this.writeStartInterval = writeStartInterval;
	}
}
