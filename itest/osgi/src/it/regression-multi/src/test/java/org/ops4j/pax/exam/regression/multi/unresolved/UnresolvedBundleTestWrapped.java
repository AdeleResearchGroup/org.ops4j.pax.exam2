/*
 * Copyright 2012 Harald Wellmann
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
package org.ops4j.pax.exam.regression.multi.unresolved;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.regression.multi.RegressionConfiguration.regressionDefaults;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

/**
 * Regression test for PAXEXAM-433. The test is called {@code ...TestWrapped} to hide it from Maven
 * Surefire. {@link UnresolvedBundleInvokerTest} verifies that this test fails.
 * 
 * @author hwellmann
 * 
 */
@RunWith(PaxExam.class)
public class UnresolvedBundleTestWrapped {

    @Configuration
    public Option[] config() {
        return options( //
            regressionDefaults(), //
            mavenBundle("commons-dbcp", "commons-dbcp", "1.4"), //
            // uncomment next line to make test pass
            // mavenBundle("commons-pool", "commons-pool", "1.5.4"), //
            junitBundles());
    }

    @Test
    public void test() {
        // we should never get here, as one of the provisioned bundles is unresolved
    }
}
