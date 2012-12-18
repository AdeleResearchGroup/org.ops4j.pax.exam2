/*
 * Copyright 2011 Harald Wellmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.exam.forked;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.junit.Test;
import org.ops4j.pax.swissbox.framework.RemoteFramework;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.FrameworkFactory;

public class ForkedFrameworkFactoryTest {

    @Test
    public void forkEquinox() throws BundleException, IOException, InterruptedException,
        NotBoundException, URISyntaxException {
        ServiceLoader<FrameworkFactory> loader = ServiceLoader.load(FrameworkFactory.class);
        FrameworkFactory frameworkFactory = loader.iterator().next();

        File storage = new File("target/storage");
        storage.mkdir();
        ForkedFrameworkFactory forkedFactory = new ForkedFrameworkFactory(frameworkFactory);

        Map<String, Object> frameworkProperties = new HashMap<String, Object>();
        frameworkProperties.put(Constants.FRAMEWORK_STORAGE, storage.getAbsolutePath());
        RemoteFramework framework = forkedFactory.fork(Collections.<String> emptyList(),
            Collections.<String, String> emptyMap(), frameworkProperties);
        framework.start();

        long bundleId = framework
            .installBundle("file:target/bundles/regression-pde-bundle-2.3.0.jar");
        framework.startBundle(bundleId);

        framework.callService("(objectClass=org.ops4j.pax.exam.regression.pde.HelloService)",
            "getMessage");

        Thread.sleep(3000);
        framework.stop();

        forkedFactory.join();
    }
}
