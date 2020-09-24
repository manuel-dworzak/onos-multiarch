package de.uniba.ktr.crma;

import org.onosproject.net.flow.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.onlab.packet.Ethernet;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.packet.PacketPriority;
import org.onosproject.net.packet.PacketProcessor;
import org.onosproject.net.packet.PacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component(immediate = true)
public class CloudlessMonitoringApp {
    // Instantiates the relevant services.

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PacketService packetService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    private final Logger logger = LoggerFactory.getLogger(CloudlessMonitoringApp.class.getName());

    private ApplicationId appId;
    private PacketProcessor processor;
    private ReportPacketBuffer buffer;
    private ExecutorService reporters;

    /**
     * Create a variable of the SwitchPacketProcessor class using the PacketProcessor defined above.
     * Activates the app.
     *
     * Create code to add a processor
     */
    @Activate
    protected void activate() {
        logger.info("Starting CloudlessMonitoringApp...");
        appId = coreService.getAppId("de.uniba.ktr.crma");
        // Create a buffer for ResourceMonitoringPacketProcessor & InfluxWriter to produce/consume
        buffer = new ReportPacketBuffer(1000);

        //Create and processor and add it using packetService
        processor = new ResourceMonitoringPacketProcessor(buffer);
        // See more about PacketProcessing here: https://wiki.onosproject.org/pages/viewpage.action?pageId=13995707
        packetService.addProcessor(processor, PacketProcessor.director(2));
        /*
         * Restricts packet types to IPV4by only requesting those types
         */
        packetService.requestPackets(DefaultTrafficSelector.builder()
                .matchEthType(Ethernet.TYPE_IPV4).build(), PacketPriority.REACTIVE, appId);

        // Init InfluxManager
        InfluxManager.getInstance().initialize();

        // Create InfluxWriter
        reporters = Executors.newFixedThreadPool(1);
        reporters.execute(new InfluxWriter(buffer, "InfluxWriter"));
        logger.info("CloudlessMonitoringApp started!!!");
    }

    /**
     * Deactivates the processor by removing it.
     *
     * Create code to remove the processor.
     */
    @Deactivate
    protected void deactivate() {
        logger.info("Stopping CloudlessMonitoringApp...");
        // remove the reporter
        reporters.shutdown();
        reporters = null;

        //Remove the processor
        packetService.removeProcessor(processor);
        processor = null;

        // remove buffer
        buffer = null;

        // close connection
        InfluxManager.getInstance().close();

        // notify
        logger.info("CloudlessMonitoringApp stopped!!!");
    }
}
