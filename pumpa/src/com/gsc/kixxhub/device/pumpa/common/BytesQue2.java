package com.gsc.kixxhub.device.pumpa.common;

import com.gsc.kixxhub.common.utility.log.LogUtility;

public class BytesQue2 { 
	
    /**
     * 
     */
    private int itemCount=0, maxQ=50;
    //private String[] que;
	/**
	 * 
	 */
	private byte[][] que;

    /**
     * @param maxCount
     */
    public BytesQue2 (int maxCount) {
    	que = new byte[maxCount][];
        itemCount=0;
        maxQ = maxCount;
    }

    /**
     * @return
     */
    public synchronized byte[] deQueue() throws Exception {
    	
        if(isEmpty()) {
            return null;
        } else {
            //String obj= que[0];
        	byte[] obj = que[0];
            itemCount--;
            System.arraycopy(que, 1, que, 0, itemCount);
            //LogUtility.getPumpALogger().debug(obj + " Output : count " + itemCount);
            //LogUtility.getPumpALogger().debug("++++ Bytes2 deQue : ["  + new String(obj) + "]");

            return obj;
        }
    }

    /**
     * @param obj
     */
    public synchronized void enQueue(byte[] buf) throws Exception {
    	
        if(itemCount>=maxQ) deQueue();

        que[itemCount]=buf;
        itemCount++;

        //LogUtility.getPumpALogger().debug("++++ Bytes2 enQue : ["  + new String(buf) + "]");
        /*
        LogUtility.getPumpALogger().debug("0.##### 큐에 신규추가 : item count=" + itemCount);
        Log.datas(buf, buf.length, 20);
        showItems();*/
    }

    /**
     * @param obj
     */
    public synchronized void enQueueNewer(byte[] buf) throws Exception { 
    	
    	boolean newer=true;
    	
    	for (int i=0; i<itemCount; i++) {

    		newer=false;
    		
    		for (int j=0; j<que[i].length; j++) {
    			if (que[i][j] != buf[j]) {
    				newer = true;
    				break;
    			}
    		}
    		
    		if (newer==false)
    			break;
    	}
    	
    	if (newer==true) {
	        if(itemCount>=maxQ) deQueue(); // 가장 오랜된것 삭제
	
	        que[itemCount]=buf.clone(); // 신규추가
	        itemCount++;
	        /*
	        LogUtility.getPumpALogger().debug("1.##### 큐에 신규추가 : item count=" + itemCount);
	        Log.datas(buf, buf.length, 20);
	        showItems();*/
    	}
    	/*else {
	        LogUtility.getPumpALogger().debug("1.##### 큐에 추가않음 : item count=" + itemCount);
	        Log.datas(buf, buf.length, 20);
	        showItems();
    	}*/
    	
        //LogUtility.getPumpALogger().debug("++++ Bytes2 enQueNewer : ["  + new String(buf) + "]");
    }
    
    public synchronized void flushQueue() throws Exception {
    	
    	while (this.getItemCount() > 0)
    		this.deQueue();
    }
    
    /**
     * @return
     */
    public synchronized byte[] getFirstItem () throws Exception {
    	
        if(isEmpty()){
            return null;
        } else {
        	return que[0];
        }
    }

    /**
     * @return
     */
    public synchronized int getItemCount () throws Exception {
        return itemCount;
    }

    /**
     * @return
     */
    public synchronized boolean isEmpty() throws Exception {
        return itemCount<=0;
    }
    
    public synchronized void showItems () throws Exception { 
    	
        LogUtility.getPumpALogger().debug("@@@ BytesQue2 Items : item count=" + itemCount);
    	for (int i=0; i<itemCount; i++) {
    		Log.datas(que[i], que[i].length, 20);
    	}
    }
}


