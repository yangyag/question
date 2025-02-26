package com.gsc.kixxhub.module.pumpm.bundle;

 
import com.gsc.kixxhub.eventhandler.action.BaseBundleActivator;
import com.gsc.kixxhub.module.pumpm.bundle.sub.CatMController;
import com.gsc.kixxhub.module.pumpm.bundle.sub.PosAController;
import com.gsc.kixxhub.module.pumpm.bundle.sub.PumpAController;
import com.gsc.kixxhub.module.pumpm.bundle.sub.SaleMController;
import com.gsc.kixxhub.module.pumpm.bundle.sub.StateMController;
 
public class PumpMBundleActivator extends BaseBundleActivator {

	@Override
	public void registerListener() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void start() throws Exception {
		CatMController catMController = new CatMController() ;
		catMController.start(getBundleContext());
		
		PosAController posAController = new PosAController() ;
		posAController.start(getBundleContext());
		
		PumpAController pumpAController = new PumpAController() ;
		pumpAController.start(getBundleContext());
		
		SaleMController saleMController = new SaleMController() ;
		saleMController.start(getBundleContext());
		
		StateMController stateMController = new StateMController() ;
		stateMController.start(getBundleContext());		
	}
	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
