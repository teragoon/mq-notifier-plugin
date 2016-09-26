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
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.TopLevelItem;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Run;
import hudson.model.listeners.RunListener;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Receives notifications about builds and publish messages on configured MQ server.
 *
 * @author Örjan Percy &lt;orjan.percy@sonymobile.com&gt;
 */
@Extension
public class RunListenerImpl extends RunListener<Run> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunListenerImpl.class);

    private static MQNotifierConfig config;
    private String function = null;

    /**
     * Constructor for RunListenerImpl.
     */
    public RunListenerImpl() {
        super(Run.class);
    }

    @Override
    public void onStarted(Run r, TaskListener listener) {
        if (r instanceof AbstractBuild) {
            AbstractBuild<?, ?> build = (AbstractBuild<?, ?>)r;
            List<String> causes = new LinkedList<String>();
            for (Object o : build.getCauses()) {
                causes.add(o.getClass().getSimpleName());
            }

            Cause.UpstreamCause upstreamCause = (Cause.UpstreamCause)r.getCause(Cause.UpstreamCause.class);
            if (upstreamCause != null) {
                causes.add(upstreamCause.getShortDescription());

                TopLevelItem item = Jenkins.getInstance().getItem(upstreamCause.getUpstreamProject());
                if (item != null && item instanceof MatrixProject) {
                    //Find the build
                    MatrixBuild mb = ((MatrixProject)item).getBuildByNumber(upstreamCause.getUpstreamBuild());
                    causes.add(mb.getUrl());
                }
            }
            Cause.RemoteCause remoteCause = (Cause.RemoteCause)r.getCause(Cause.RemoteCause.class);
            if (remoteCause != null) {
                causes.add(remoteCause.getShortDescription());
            }
            Cause.UserIdCause userIdCause = (Cause.UserIdCause)r.getCause(Cause.UserIdCause.class);
            if (userIdCause != null) {
                causes.add(userIdCause.getShortDescription());
            }

            JSONObject json = new JSONObject();
            json.put(Util.KEY_STATE, Util.VALUE_STARTED);
            json.put(Util.KEY_URL, Util.getJobUrl(r));
            json.put(Util.KEY_CAUSES, causes.toString());

            json.put(Util.KEY_PARAMS, ((AbstractBuild) r).getBuildVariables());
            json.put(Util.KEY_NODE_NAME, ((AbstractBuild) r).getBuiltOnStr());

            LOGGER.warn("[RunListenerImpl][onStarted]" + Util.KEY_NODE_NAME + " : "
                    + ((AbstractBuild) r).getBuiltOnStr());
            setFunction(Util.VALUE_STARTED);
            publish(json);
        }
    }

    @Override
    public void onCompleted(Run r, TaskListener listener) {
        if (r instanceof AbstractBuild) {
            AbstractBuild<?, ?> build = (AbstractBuild<?, ?>)r;
            JSONObject json = new JSONObject();
            json.put(Util.KEY_STATE, Util.VALUE_COMPLETED);
            json.put(Util.KEY_URL, Util.getJobUrl(r));
            String status = "";
            Result res = build.getResult();
            if (res != null) {
                status = res.toString();
            }
            json.put(Util.KEY_STATUS, status);

            json.put(Util.KEY_PARAMS, ((AbstractBuild) r).getBuildVariables());
            json.put(Util.KEY_NODE_NAME, ((AbstractBuild) r).getBuiltOnStr());

            setFunction(Util.VALUE_COMPLETED);
            publish(json);
        }
    }
    @Override
    public void onDeleted(Run r) {
        if (r instanceof AbstractBuild) {
            // Deleting a Job does not fire the RunListener.onDeleted event for its Runs
            // https://issues.jenkins-ci.org/browse/JENKINS-26708
            JSONObject json = new JSONObject();
            json.put(Util.KEY_STATE, Util.VALUE_DELETED);
            json.put(Util.KEY_URL, Util.getJobUrl(r));
            json.put(Util.KEY_STATUS, Util.VALUE_DELETED);

            json.put(Util.KEY_PARAMS, ((AbstractBuild) r).getBuildVariables());
            json.put(Util.KEY_NODE_NAME, ((AbstractBuild) r).getBuiltOnStr());

            setFunction(Util.VALUE_DELETED);
            publish(json);
        }
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
