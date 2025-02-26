package com.gsc.kixxhub.module.pumpm.pump.manager;

import com.gsc.kixxhub.common.utility.log.LogUtility;
import com.gsc.kixxhub.common.utility.sync.SyncManager;

public class PumpMSyncManager {

	/**
	 * ���� �ٸ� ������ ��Ž� ��û�� ������ ���� ������ �����Ҷ� ���ȴ�.
	 */
	private static SyncManager syncManager = new SyncManager() ;

	public static void destroy() {
		if (syncManager != null) {
			syncManager.clear() ;
			syncManager = null ;
		} 
	}
	
	/**
	 * ������ ��ٸ��� ������ �� key �� ������ �����Ͱ� �ִ��� �˻��ϰ�, ������ �� �����͸� Return �Ѵ�.
	 * 
	 * @param key	: Key ��
	 * @return
	 * 		Key �� �ش��ϴ� Object or Null.
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
	 * ������ ��ٸ��� ������ �� key �� ������ �����Ͱ� �ִ��� �˻��ϰ�, ������ �� �����͸� Return �� ���� ����ҿ��� �����Ѵ�.
	 * 
	 * @param key	: Key ��
	 * @return
	 * 		Key �� �ش��ϴ� Object or Null.
	 */
	public synchronized static Object getPreambleAndRemove(String key) {
		Object preamble = syncManager.getPreamble(key) ;
		if (preamble != null) {
			// ��ٸ��� ������ ���
			LogUtility.getPumpMLogger().info("[Pump M] Waiting Content. preamble.key=" + key) ;
			syncManager.remove(key) ;
		} else {
			LogUtility.getPumpMLogger().warn("[Pump M] Not Waiting Content. preamble.key=" + key) ;
		}
		
		removeElapsedSyncData() ;
		
		return preamble ;
	}
	
	/**
	 * PumpMSyncManager ���� �����ϰ� �ִ� �����͵��� �ʱ�ȭ �Ѵ�.
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
	 * Pump M ���� �����ϰ� �ִ� ����ȭ �����Ͱ� ������� ���� ������ ���� ��� ������ ������ ���� �� �ִ�. 
	 * �̸� �����ϱ� ���ؼ� Ư�� �ð����� ������ ���� �����ʹ� �����ϵ��� �Ѵ�.
	 *
	 */
	private static void removeElapsedSyncData() {
		int elapsedSeconds = 240 ;
		syncManager.removeContentAfterSeconds(elapsedSeconds) ;
	}
	
	/**
	 * ������ ��ٸ��� ������ �� Key �� �����Ѵ�.
	 * 
	 * @param key			: Key ��
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
