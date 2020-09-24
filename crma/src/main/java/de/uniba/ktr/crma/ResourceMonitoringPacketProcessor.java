package de.uniba.ktr.crma;

import org.onlab.packet.*;
import org.onosproject.net.packet.InboundPacket;
import org.onosproject.net.packet.PacketContext;
import org.onosproject.net.packet.PacketProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ResourceMonitoringPacketProcessor implements PacketProcessor {
    // logger
    private final Logger log = LoggerFactory.getLogger(ResourceMonitoringPacketProcessor.class.getName());

    //These are the messages I'm looking for in the packets
    private static final byte[] reportCookie = "influx_report".getBytes(StandardCharsets.UTF_8);

    private ReportPacketBuffer buffer;

    private ObjectMapper mapper;

    public ResourceMonitoringPacketProcessor(ReportPacketBuffer buffer) {
        this.buffer = buffer;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void process(PacketContext pc) {
        //Get needed information on packet, sender, receiver
        InboundPacket pkt = pc.inPacket();
        Ethernet ethPkt = pkt.parsed();

        //String packetInfo = getPacketInfo(pc);
        //log.info(packetInfo);

        // If already handled or packet null - return
        if (ethPkt == null || pc.isHandled()) {
            log.debug("Packet is already handled or null.");
            return;
        }

        if (ethPkt.getEtherType() != Ethernet.TYPE_IPV4) {
            log.debug("Packet is not TYPE_IPV4.");
            return;
        }

        byte pktProto = ((IPv4) pc.inPacket().parsed().getPayload()).getProtocol();
        boolean isInfluxReport = AppUtility.search(pkt.unparsed().array(), reportCookie);

        if (pktProto == IPv4.PROTOCOL_UDP && isInfluxReport) {
            try {
                UDP udp = (UDP) ethPkt.getPayload().getPayload();
                ResourceDataPoint point = mapper.readValue(AppUtility.decodeUTF8(udp.getPayload().serialize()),
                        ResourceDataPoint.class);
                log.debug(String.format("Put %s to buffer", point.toString()));
                buffer.put(point);
                // block packet, mark as handled so the downstream packetprocessors will ignore this packet
                pc.block();
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getPacketInfo(PacketContext pc) {
        StringBuilder builder = new StringBuilder();

        // append basic information
        builder.append("Processing packet with following information:\n");

        // show ethernet type
        builder.append("- EthType: ").append(EthType.EtherType.lookup(pc.inPacket().parsed().getEtherType()).toString()).append("\n");

        // get srcMac value
        builder.append("- srcMac: ").append(pc.inPacket().parsed().getSourceMAC().toString()).append("\n");

        // get dstMac value
        builder.append("- dstMac: ").append(pc.inPacket().parsed().getDestinationMAC().toString()).append("\n");


        Ethernet eth = pc.inPacket().parsed();
        if(eth.getEtherType() == Ethernet.TYPE_IPV4) {
            // check & get IPv4 protocol
            builder.append("- IPv4 proto: ");

            byte pktProto = ((IPv4) pc.inPacket().parsed().getPayload()).getProtocol();

            switch (pktProto) {
                case IPv4.PROTOCOL_ICMP:
                    builder.append("ICMP\n");
                    break;
                case IPv4.PROTOCOL_IGMP:
                    builder.append("IGMP\n");
                    break;
                case IPv4.PROTOCOL_PIM:
                    builder.append("PIM\n");
                    break;
                case IPv4.PROTOCOL_TCP:
                    builder.append("TCP\n");
                    break;
                case IPv4.PROTOCOL_UDP:
                    builder.append("UDP\n");
                    //byte[] pktArrayContent = ((IPv4) pc.inPacket().parsed().getPayload()).getPayload().serialize();
                    //String content = AppUtility.decodeUTF8(pktArrayContent);
                    //builder.append(" Content: " + content + "\n");
                    //log.info("UDP: " + content);
                    break;
            }
        }

        if(eth.getEtherType() == Ethernet.TYPE_ARP) {
            builder.append("- Proto: ARP");
        }

        return builder.toString();
    }
}
