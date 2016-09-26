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
import hudson.FilePath;
import hudson.model.TaskListener;
import hudson.model.Run;
import hudson.model.listeners.ItemListener;
import hudson.model.listeners.SCMListener;
import hudson.scm.SCMRevisionState;
import hudson.scm.SCM;

import java.io.File;

import javax.annotation.CheckForNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Receives job status notifications {@link ItemListener}.
 * Initialize the MQ connection.
 *
 * @author Örjan Percy &lt;orjan.percy@sonymobile.com&gt;
 */
@Extension
public class ScmListenerImpl extends SCMListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScmListenerImpl.class);

    /**
     * Create instance.
     */
    public ScmListenerImpl() {
        super();
    }

    public void onCheckout(Run<?, ?> build, SCM scm, FilePath workspace, TaskListener listener,
            @CheckForNull File changelogFile, @CheckForNull SCMRevisionState pollingBaseline)
            throws Exception {
        LOGGER.warn("[ScmListenerImpl][onCheckout] build getBuildStatusUrl : "
                + build.getBuildStatusUrl());
        LOGGER.warn("[ScmListenerImpl][onCheckout] build getDisplayName : "
                + build.getDisplayName());

    }

}
