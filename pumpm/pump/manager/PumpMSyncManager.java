package com.gsc.kixxhub.module.pumpm.pump.manager;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.sync.SyncManager;

public class PumpMSyncManager {

	/**
	 * 서로 다른 모듈과의 통신시 요청한 전문에 대한 응답을 관리할때 사용된다.
	 */
	private static SyncManager syncManager = new SyncManager() ;

	public static void destroy() {
		if (syncManager != null) {
			syncManager.clear() ;
			syncManager = null ;
		} 
	}
	
	/**
	 * 응답을 기다리는 데이터 중 key 와 동일한 데이터가 있는지 검사하고, 있으면 그 데이터를 Return 한다.
	 * 
	 * @param key	: Key 값
	 * @return
	 * 		Key 에 해당하는 Object or Null.
	 */
	public static Object getPreamble(String key) {
		Object preamble = syncManager.getPreamble(key) ;
		if (preamble != null) {
			LogUtility.getPumpMLogger().debug("[Pump M] Waiting Content. preamble.key=" + key) ;
		} else {
			LogUtility.getPumpMLogger().warn("[Pump M] Not Waiting Content. preamble.key=" + key) ;
		}
		return preamble ;
	}
	
	/**
	 * 응답을 기다리는 데이터 중 key 와 동일한 데이터가 있는지 검사하고, 있으면 그 데이터를 Return 한 이후 저장소에서 삭제한다.
	 * 
	 * @param key	: Key 값
	 * @return
	 * 		Key 에 해당하는 Object or Null.
	 */
	public synchronized static Object getPreambleAndRemove(String key) {
		Object preamble = syncManager.getPreamble(key) ;
		if (preamble != null) {
			// 기다리던 전문인 경우
			LogUtility.getPumpMLogger().info("[Pump M] Waiting Content. preamble.key=" + key) ;
			syncManager.remove(key) ;
		} else {
			LogUtility.getPumpMLogger().warn("[Pump M] Not Waiting Content. preamble.key=" + key) ;
		}
		
		removeElapsedSyncData() ;
		
		return preamble ;
	}
	
	/**
	 * PumpMSyncManager 에서 관리하고 있는 데이터들을 초기화 한다.
	 *
	 */
	public static void init() {
		if (syncManager != null) {
			syncManager.clear() ;
		} else {
			syncManager = new SyncManager() ;
		}
	}
	
	/**
	 * Pump M 에서 관리하고 있는 동기화 데이터가 상대측에 의해 반응이 없을 경우 무한정 가지고 있을 수 있다. 
	 * 이를 방지하기 위해서 특정 시간동안 응답이 없는 데이터는 삭제하도록 한다.
	 *
	 */
	private static void removeElapsedSyncData() {
		int elapsedSeconds = 240 ;
		syncManager.removeContentAfterSeconds(elapsedSeconds) ;
	}
	
	/**
	 * 응답을 기다리는 데이터 및 Key 를 저장한다.
	 * 
	 * @param key			: Key 값
	 * @param fromkey		: DISE PUMP MODULE
	 * @param from			: From Source
	 * @param preamble		: Object
	 * @throws Exception	
	 */
	public static void setSyncData(String key, String fromkey, int from, Object preamble) {
		try {
			LogUtility.getPumpMLogger().debug("[Pump M] Store content for Sync. preamble.key=" + key) ;
			syncManager.setSyncData(key, fromkey, from, preamble) ;
			
			removeElapsedSyncData() ;
		} catch (Exception e) {
			LogUtility.getPumpMLogger().error(e.getMessage(),e) ;
		}
	}
}
