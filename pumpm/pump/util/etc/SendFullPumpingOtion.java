package com.gsc.kixxhub.module.pumpm.pump.util.etc;

import com.gsc.kixxhub.common.utility.timer.KHTimerTask;
import com.gsc.kixxhub.module.pumpm.bundle.sub.PumpAController;

public class SendFullPumpingOtion extends KHTimerTask {

	private PumpAController pumpacon = null;

	public SendFullPumpingOtion() {
		this("PumpAController SendFullPumpingOtion TimerTask");
	}
	
	public SendFullPumpingOtion(String name) {
		super(name);
	}
	
	public void addListener(PumpAController pumpacon) {
		
		this.pumpacon = pumpacon;
	}

	@Override
	public void execute() {
		
		pumpacon.sendFullPumping();
	}
		
	public void removeListener() {
		pumpacon = null;
	}


}
