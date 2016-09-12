/*
 *  The MIT License
 *
 *  Copyright 2016 Sony Mobile Communications Inc. All rights reserved.
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
import hudson.model.Queue;
import hudson.model.queue.QueueListener;

import java.nio.charset.StandardCharsets;

import net.sf.json.JSONObject;

/**
 * Receives notifications about when tasks are submitted to the queue and publishes
 * messages on configured MQ server.
 * @author Tomas Westling &lt;tomas.westling@sonymobile.com&gt;
 */
@Extension
public class QueueListenerImpl extends QueueListener {
    private static MQNotifierConfig config;
    private String function = null;
    
    @Override
    public void onEnterWaiting(Queue.WaitingItem wi) {
        JSONObject json = new JSONObject();
        json.put(Util.KEY_STATE, Util.VALUE_ADDED_TO_QUEUE);
        json.put(Util.KEY_URL, Util.getJobUrl(wi));

        setFunction(Util.VALUE_ADDED_TO_QUEUE);
        publish(json);
    }

    @Override
    public void onLeft(Queue.LeftItem li) {
        JSONObject json = new JSONObject();
        json.put(Util.KEY_STATE, Util.VALUE_REMOVED_FROM_QUEUE);
        if (li.isCancelled()) {
            json.put(Util.KEY_DEQUEUE_REASON, Util.VALUE_CANCELLED);
        } else {
            json.put(Util.KEY_DEQUEUE_REASON, Util.VALUE_BUILDING);
        }
        json.put(Util.KEY_URL, Util.getJobUrl(li));

        setFunction(Util.VALUE_REMOVED_FROM_QUEUE);
        publish(json);
    }

    /**
     * Publish json message on configured MQ server.
     *
     * @param json the message in json format
     */
    private void publish(JSONObject json) {
        if (config == null) {
            config = MQNotifierConfig.get();
        }
        if (config != null) {
            MQConnection.getInstance().send(getFunction(),
                    json.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
