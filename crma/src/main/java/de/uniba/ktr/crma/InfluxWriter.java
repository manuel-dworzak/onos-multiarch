package de.uniba.ktr.crma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfluxWriter implements Runnable {
    // logger
    private final Logger log = LoggerFactory.getLogger(InfluxWriter.class.getName());

    private ReportPacketBuffer buffer;
    private String name;

    public InfluxWriter(ReportPacketBuffer buffer, String name) {
        this.buffer = buffer;
        this.name = name;
    }

    @Override
    public void run() {
        log.info(String.format("%s: Start working...", this.name));
        try {
            while (true) {
                ResourceDataPoint point = buffer.get();
                //log.info(String.format("Get %s", point));
                if (point != null) {
                    log.debug(String.format("%s: Try to write %s to influxdb... (test interworking queue)", this.name, point.toString()));
                    InfluxManager.getInstance().write(point);
                }
                Thread.sleep(1);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        log.info(String.format("%s: Stop working...", this.name));
    }
}
