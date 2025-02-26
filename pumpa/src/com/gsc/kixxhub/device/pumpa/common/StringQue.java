package com.gsc.kixxhub.device.pumpa.common;


public class StringQue {
	
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
    public StringQue(int maxCount) {
        que= new String[maxCount];
        itemCount=0;
        maxQ = maxCount;
    }

    /**
     * @return
     */
    public synchronized String deQueue() throws Exception {
    	
        if(isEmpty()){
            return null;
        } else {
            String obj= que[0];
            itemCount--;
            System.arraycopy(que, 1, que, 0, itemCount);
            //LogUtility.getPumpALogger().debug(obj + " Output : count " + itemCount);
            return obj;
        }
    }

    /**
     * @param obj
     */
    public synchronized void enQueue(String obj) throws Exception {
    	
        if(itemCount>=maxQ) deQueue();

        que[itemCount]=obj;
        itemCount++;
        //LogUtility.getPumpALogger().debug(obj + " Input : count " + itemCount);
    }

    /**
     * @return
     */
    public synchronized String getFirstItem () throws Exception {
    	
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
}


