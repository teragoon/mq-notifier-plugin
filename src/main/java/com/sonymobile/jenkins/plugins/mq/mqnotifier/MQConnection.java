/*
 *  The MIT License
 *
 *  Copyright 2015 Sony Mobile Communications Inc. All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.sonymobile.jenkins.plugins.mq.mqnotifier;

import java.util.UUID;

import org.gearman.client.GearmanClient;
import org.gearman.client.GearmanClientImpl;
import org.gearman.client.GearmanJob;
import org.gearman.client.GearmanJobImpl;
import org.gearman.common.GearmanNIOJobServerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates an MQ connection.
 *
 * @author Örjan Percy &lt;orjan.percy@sonymobile.com&gt;
 */
public final class MQConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQConnection.class);

    private static MQConnection mqconn;

    private String host;
    private int port;

    private static GearmanClient client = null;
    private GearmanNIOJobServerConnection conn = null;

    /**
     * Lazy-loaded singleton using the initialization-on-demand holder pattern.
     */
    private MQConnection() { }

    /**
     * Gets the instance.
     *
     * @return the instance
     */
    public static MQConnection getInstance() {
        LOGGER.info("[MQConnection][getInstance]");
        if (mqconn == null) {
            LOGGER.info("[MQConnection][getInstance] mqconn is null");
            mqconn = new MQConnection();
        }
        return mqconn;
    }

    /**
     * Gets the connection.
     *
     * @return the connection.
     */
    public GearmanClient getConnection() {
        LOGGER.info("[MQConnection][getConnection]");
        if (client == null || client.isShutdown()) {
            LOGGER.info("[MQConnection][getConnection] client is null");
            LOGGER.info("[MQConnection][getConnection] host : " + host + ", port : " + port);
            client = new GearmanClientImpl();

            conn = new GearmanNIOJobServerConnection(host, port);

            boolean result = client.addJobServer(conn);

            LOGGER.info("[MQConnection][getConnection] result is " + result);
        }
        return client;
    }

    /**
     * Initializes this instance with supplied values.
     *
     */
    public void initialize(String host, int port) {
        LOGGER.info("[MQConnection][initialize] host : " + host + ", port : " + port);
        this.host = host;
        this.port = port;
    }

    /**
     * Sends a message.
     * *
     * @param body the message body
     */
    public void send(String function, byte[] body) {
        LOGGER.info("[MQConnection][send] function : " + function);
        client = getConnection();

        String uniqueId = UUID.randomUUID().toString();
        GearmanJob job = GearmanJobImpl.createBackgroundJob(function, body, uniqueId);


        client.submit(job);
    }

    // https://github.com/khaido/gearman-java-example/blob/master/src/main/java/org/gearman/example2/EchoClientAsynchronous.java
    // https://github.com/slok/mdissphoto/tree/53e5bd32b2f80589dabda49b559bbfdb41440566/thumbnailer/src/main/java/org/mdissjava/thumbnailer/gearman/client
}
