package com.gsc.kixxhub.device.pumpa.common;


public class BytesQue { 
	
    /**
     * 
     */
    private int itemCount, maxQ=50;
    /**
     * 
     */
    private String[] que;

    /**
     * @param maxCount
     */
    public BytesQue(int maxCount) {
        que= new String[maxCount];
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
            String obj= que[0];
            itemCount--;
            System.arraycopy(que, 1, que, 0, itemCount);
            //LogUtility.getPumpALogger().debug(obj + " Output : count " + itemCount);
            return obj.getBytes();
        }
    }

    /**
     * @param obj
     */
    public synchronized void enQueue(byte[] obj) throws Exception {
    	
        if(itemCount>=maxQ) deQueue();

        que[itemCount]=new String(obj);
        itemCount++;
        //LogUtility.getPumpALogger().debug(obj + " Input : count " + itemCount);
    }

    /**
     * @param obj
     */
    public synchronized void enQueueNewer(byte[] obj) throws Exception { // Appneded 2008:04:11
    	
    	boolean newer=true;
    	String szObj = new String (obj);
    	
    	for (int i=0; i<itemCount; i++) {
    		if (que[i].equals(szObj))
    			newer = false;
    	}
    	
    	if (newer==true) {
	        if(itemCount>=maxQ) deQueue();
	
	        que[itemCount]=new String(obj);
	        itemCount++;
	        //LogUtility.getPumpALogger().debug(obj + " Input : count " + itemCount);
    	}
    }
    
    /**
     * @return
     */
    public synchronized byte[] getFirstItem () throws Exception {
    	
        if(isEmpty()){
            return null;
        } else {
        	return que[0].getBytes();
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
}