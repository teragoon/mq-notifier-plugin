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

import hudson.Extension;
import hudson.util.FormValidation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.servlet.ServletException;

import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Adds the MQ notifier plugin configuration to the system config page.
 *
 * @author Örjan Percy &lt;orjan.percy@sonymobile.com&gt;
 */
@Extension
public final class MQNotifierConfig extends GlobalConfiguration {
    private static final String SERVER_HOST = "host";
    private static final String SERVER_PORT = "port";

    /* The MQ server host */
    private String host;
    private int port;


    /**
     * Creates an instance with specified parameters.
     * 
     * @param host the server uri
     * @param port the user name
     * @param userPassword the user password
     * @param exchangeName the name of the exchange
     * @param virtualHost the name of the virtual host
     * @param routingKey the routing key
     * @param persistentDelivery if using persistent delivery mode
     * @param appId the application id
     */
    @DataBoundConstructor
    public MQNotifierConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Load configuration on invoke.
     */
    public MQNotifierConfig() {
        load();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        req.bindJSON(this, formData);
        save();
        return true;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    /**
     * Gets this extension's instance.
     *
     * @return the instance of this extension.
     */
    public static MQNotifierConfig get() {
        return GlobalConfiguration.all().get(MQNotifierConfig.class);
    }

    /*
     * This method runs when user clicks Test Connection button.
     * 
     * @return message indicating whether connection test passed or failed
     */
    public FormValidation doTestConnection(
            @QueryParameter(SERVER_HOST) final String host,
            @QueryParameter(SERVER_PORT) final int port) throws IOException,
            ServletException {

        if (connectionIsAvailable(host, port, 5000)) {
            return FormValidation.ok("Success");
        } else {
            return FormValidation.error("Failed: Unable to Connect");
        }
    }

    /*
     * This method checks whether a connection is open and available on $host:$port
     * 
     * @param host the host name
     * 
     * @param port the host port
     * 
     * @param timeout the timeout (milliseconds) to try the connection
     * 
     * @return boolean true if a socket connection can be established otherwise false
     */
    private boolean connectionIsAvailable(String host, int port,
            int timeout) {

        InetSocketAddress endPoint = new InetSocketAddress(host, port);
        Socket socket = new Socket();

        if (endPoint.isUnresolved()) {
            System.out.println("Failure " + endPoint);
        } else {
            try {
                socket.connect(endPoint, timeout);
                System.out.println("Connection Success:    " + endPoint);
                return true;
            } catch (Exception e) {
                System.out.println("Connection Failure:    " + endPoint + " message: "
                        + e.getClass().getSimpleName() + " - " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        return false;
    }

}
