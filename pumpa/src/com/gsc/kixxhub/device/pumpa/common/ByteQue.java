package com.gsc.kixxhub.device.pumpa.common;

import com.gsc.kixxhub.common.utility.log.LogUtility;

public class ByteQue {
	
    /**
     * 
     */
    private int itemCount, maxQ=50;
    /**
     * 
     */
    public byte[][] que;

    /**
     * @param maxCount
     */
    public ByteQue(int maxCount) {
        que = new byte[maxCount][];
        itemCount=0;
        maxQ = maxCount;
    }

    /**
     * @return
     */
    public synchronized byte[] deQueue() throws Exception {
    	
        if(isEmpty()){
            //LogUtility.getPumpALogger().debug("empty.");
            return null;
        } else {
            byte[] dat= que[0];
            itemCount--;
            //System.arraycopy(que, 1, que, 0, itemCount);
            //LogUtility.getPumpALogger().debug(obj + " Output : count " + itemCount);
            for (int i=0; i<itemCount; i++) {
            	que[i] = que[i+1];
                //LogUtility.getPumpALogger().debug("----> " + que[i][0] + ", " + que[i][1]);
            }
            return dat;
        }
    }

    /**
     * @param dat
     */
    public synchronized void enQueue(byte[] dat) throws Exception {
    	
        if (itemCount>=maxQ) deQueue();

        que[itemCount]=dat;
        itemCount++;
        

		//byte[] byt = new byte [2];
		//byt[0] = dat[0];
		//byt[1] = dat[1];
		//LogUtility.getPumpALogger().debug ("====>" + itemCount + "==" + dat[0] + ", " + dat[1]);
		LogUtility.getPumpALogger().debug ("In enQue[" + itemCount + "]  ==" + que[itemCount-1][0] + ", " + 
				que[itemCount-1][1]);
		
		
        //LogUtility.getPumpALogger().debug(que[itemCount] + que[itemCount][1] + " Input : count " + itemCount);
    }

    /**
     * @return
     */
    public synchronized int getItemCount () throws Exception {
        return itemCount;
    }

    /**
     * @param idx
     * @return
     */
    public synchronized byte[] getQueue(int idx) throws Exception {

        //byte[] dat= que[idx];
    	LogUtility.getPumpALogger().debug ("---->" + idx + " : " + que[idx][0] + "," + que[idx][1]);
        return que[idx];
    }

    /**
     * @return
     */
    public synchronized boolean isEmpty() throws Exception {
        return itemCount<=0;
    }
}


