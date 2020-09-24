package de.uniba.ktr.crma;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ReportPacketBuffer {
    // Bounded ArrayBlockingQueue of size n
    BlockingQueue<ResourceDataPoint> dataQueue;

    public ReportPacketBuffer(int size) {
        dataQueue = new ArrayBlockingQueue<>(size);
    }

    public ResourceDataPoint get() {
        try {
            // take method to get resource data point from blocking queue
            ResourceDataPoint point = dataQueue.take();
            return point;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void put(ResourceDataPoint point) {
        try {
            dataQueue.put(point);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
