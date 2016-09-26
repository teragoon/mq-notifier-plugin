package com.sonymobile.jenkins.plugins.mq.mqnotifier;

import org.gearman.client.GearmanIOEventListener;
import org.gearman.common.GearmanPacket;
import org.gearman.common.GearmanPacketType;
import org.gearman.util.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQEventListener implements GearmanIOEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQEventListener.class);

    public MQEventListener() {
    }

    @Override
    public void handleGearmanIOEvent(GearmanPacket event) throws IllegalArgumentException {
        GearmanPacketType packetType = event.getPacketType();

        LOGGER.info("Packet from Gearman received: {}", packetType.toString());

        // Convert bytes to string
        String result = ByteUtils.fromUTF8Bytes(event.getData());

        // Check the type of event that Gearman is submitting us
        if (packetType == GearmanPacketType.WORK_COMPLETE)
        {
            LOGGER.info("Gearman worker completed with: {}", result);

        }
        if (packetType == GearmanPacketType.WORK_FAIL)
        {
            LOGGER.info("Gearman worker failed with: {}", result);
        }
    }

}
