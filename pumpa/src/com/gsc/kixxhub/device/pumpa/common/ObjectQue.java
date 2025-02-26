package com.gsc.kixxhub.device.pumpa.common;



public class ObjectQue { 
	
    /**
     * 
     */
    private int itemCount, maxQ=50;
    /**
     * 
     */
    private Object[] que;

    /**
     * @param maxCount
     */
    public ObjectQue(int maxCount) {
        que= new Object[maxCount];
        itemCount=0;
        maxQ = maxCount;
    }

    /**
     * @return
     */
    public synchronized Object deQueue() throws Exception {
    	
        if(isEmpty()){
            //LogUtility.getPumpALogger().debug("empty.");
            return null;
        } else {
            Object obj= que[0];
            itemCount--;
            System.arraycopy(que, 1, que, 0, itemCount);
            
            //WorkingMessage wm = (WorkingMessage) obj;
            //LogUtility.getPumpALogger().debug("++++ Object deQue : "  + wm.getCommand() + " <- devNo=" + wm.getNozzleNo()+ " count=" + itemCount);
            
            return obj;
        }
    }

    /**
     * @param obj
     */
    public synchronized void enQueue(Object obj) throws Exception {
    	
        if(itemCount>=maxQ)  deQueue();

        que[itemCount]=obj;
        itemCount++;
        
        //WorkingMessage wm = (WorkingMessage) obj;
        //LogUtility.getPumpALogger().debug("++++ Object enque : "  + wm.getCommand() + " -> devNo=" + wm.getNozzleNo()+ " count=" + itemCount);
    }

    /**
     * @return
     */
    public synchronized int getItemCount () throws Exception {
    	
        //LogUtility.getPumpALogger().debug("++++ Object getItemCount : " + itemCount);

        return itemCount;
    }

    /**
     * @return
     */
    public synchronized boolean isEmpty() throws Exception {
    	
        //LogUtility.getPumpALogger().debug("++++ Object isEmpty : " + itemCount);
        
        return itemCount<=0;
    }
}


