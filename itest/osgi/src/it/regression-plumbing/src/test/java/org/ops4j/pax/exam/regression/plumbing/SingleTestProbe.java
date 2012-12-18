/*
 * Copyright (C) 2010 Toni Menzel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.exam.regression.plumbing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * External TestProbe. Assemble yourself using: createProbe().addTest( SingleTestProbe.class )
 */
public class SingleTestProbe {

    private static Logger LOG = LoggerFactory.getLogger(SingleTestProbe.class);

    public void withoutBCTest() {
        LOG.info("INSIDE OSGI " + SingleTestProbe.class.getName() + " Method withoutBCTest");
        LOG.info("Environment: " + System.getenv("foo"));
    }

    @SuppressWarnings("unused")
    private void neverCall() {
        fail("Don't call me !");
    }
}
