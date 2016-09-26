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
import hudson.model.Label;
import hudson.model.Node;
import hudson.model.listeners.ItemListener;
import hudson.slaves.Cloud;
import hudson.slaves.CloudProvisioningListener;
import hudson.slaves.NodeProvisioner;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Receives job status notifications {@link ItemListener}.
 * Initialize the MQ connection.
 *
 * @author Örjan Percy &lt;orjan.percy@sonymobile.com&gt;
 */
@Extension
public class CloudProvisioningListenerImpl extends CloudProvisioningListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudProvisioningListenerImpl.class);

    /**
     * Create instance.
     */
    public CloudProvisioningListenerImpl() {
        super();
    }

    @Override
    public void onStarted(Cloud cloud, Label label,
            Collection<NodeProvisioner.PlannedNode> plannedNodes) {
        LOGGER.warn("[CloudProvisioningListenerImpl][onStarted] Node size : " + plannedNodes.size());
    }

    @Override
    public void onComplete(NodeProvisioner.PlannedNode plannedNode, Node node) {
        LOGGER.warn("[CloudProvisioningListenerImpl][onComplete] Node : "
                + node.getNodeName());
    }
}
