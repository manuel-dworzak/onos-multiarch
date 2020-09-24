package de.uniba.ktr.crma;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

public class InfluxManager {
    private static InfluxManager instance;
    private InfluxDB manager;

    private InfluxManager() {
        String serverUrl = AppConfigurator.getInstance().getProperty("scheme") +
                AppConfigurator.getInstance().getProperty("address") + ":" +
                AppConfigurator.getInstance().getProperty("port");

        manager = InfluxDBFactory.connect(serverUrl, AppConfigurator.getInstance().getProperty("username"),
                AppConfigurator.getInstance().getProperty("password"));

        // set database to rmtb
        manager.setDatabase(AppConfigurator.getInstance().getProperty("database"));
    }

    synchronized public static InfluxManager getInstance() {
        if (instance == null) {
            instance = new InfluxManager();
        }
        return instance;
    }

    public void initialize() {
        // do nothing
    }

    public void write(ResourceDataPoint point) {
        try {
            this.manager.write(point.buildLineProtocol());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        this.manager.close();
    }
}
