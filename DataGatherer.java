package com.gsc.kixxhub.device.pumpa.controller;

import java.util.Vector;

import com.gsc.kixxhub.common.data.pump.WorkingMessage;
import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.device.pumpa.common.Sleep;

public class DataGatherer implements Runnable {

	/**
	 * 
	 */
	DeviceSelector devSel;

	/**
	 * 
	 */
	Vector<DriverScheduler> drvSchVec;

	/**
	 * @param devSel
	 * @param drvSchVec
	 */
	public void init(DeviceSelector devSel, Vector<DriverScheduler> drvSchVec) {

		this.devSel = devSel;
		this.drvSchVec = drvSchVec;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		WorkingMessage rcvWm = null;
		DriverScheduler drvSch;
		int drvSchCnt = drvSchVec.size();

		while (true) {

			for (int i=0; i < drvSchCnt; i++) {

				try {

					drvSch = drvSchVec.get(i);

					if (drvSch.upQue.isEmpty() == false) {

						rcvWm = (WorkingMessage) drvSch.upQue.deQueue();
						String nozStr = rcvWm.getNozzleNo();

						if (rcvWm.isPassThrough() == true) // ���� ����̽��� �߰赥���� ó���ϱ� ����
							ProcessSelector.selectProcess(rcvWm);

						// ����(2008/11/27 �ȷ���: EX �߰�)
						if (devSel.isSelfDevice(nozStr) == true
								|| rcvWm.getCommand().equals("EX")) {
							devSel.selfDevExchangeData(rcvWm); // ����������� ����ODT���� �߰� �������̽�
						}
					}
				} catch (Exception e) {
					LogUtility.getPumpALogger().error("Catched Exception in DataGatherer:");
					LogUtility.getPumpALogger().error(e.getMessage(), e);
				}
			}

			Sleep.sleep(5);
		}
	}
}
