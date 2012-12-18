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

import static org.ops4j.pax.exam.regression.plumbing.RegressionConfiguration.regressionDefaults;
import static org.ops4j.pax.exam.spi.PaxExamRuntime.getTestContainerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestAddress;
import org.ops4j.pax.exam.TestContainerFactory;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.spi.DefaultExamReactor;
import org.ops4j.pax.exam.spi.ExamReactor;
import org.ops4j.pax.exam.spi.PaxExamRuntime;
import org.ops4j.pax.exam.spi.StagedExamReactor;
import org.ops4j.pax.exam.spi.StagedExamReactorFactory;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

/**
 * Simple regression
 */
public class ReactorAPITest {

    private TestContainerFactory getFactory() {
        return getTestContainerFactory();
    }

    @Test
    public void reactorRunAllConfinedTest() throws Exception {
        reactorRun(new PerMethod());
    }

    @Test
    public void reactorRunEagerTest() throws Exception {
        reactorRun(new PerClass());
    }

    public void reactorRun(StagedExamReactorFactory strategy) throws Exception {
        TestContainerFactory factory = getFactory();
        Option[] options = new Option[] { regressionDefaults() };

        ExamSystem system = PaxExamRuntime.createTestSystem(options);
        ExamReactor reactor = new DefaultExamReactor(system, factory);
        TestProbeBuilder probe = makeProbe(system);

        reactor.addProbe(probe);
        reactor.addConfiguration(options);

        StagedExamReactor stagedReactor = reactor.stage(strategy);
        stagedReactor.beforeClass();
        try {
            for (TestAddress call : stagedReactor.getTargets()) {
                stagedReactor.invoke(call);
            }

        }
        finally {
            stagedReactor.afterClass();
            system.clear();
        }

    }

    private TestProbeBuilder makeProbe(ExamSystem system) throws IOException {
        TestProbeBuilder probe = system.createProbe();
        probe.addTests(SingleTestProbe.class, getAllMethods(SingleTestProbe.class));
        return probe;
    }

    private Method[] getAllMethods(Class<?> c) {
        List<Method> methods = new ArrayList<Method>();
        for (Method m : c.getDeclaredMethods()) {
            if (m.getModifiers() == Modifier.PUBLIC) {
                methods.add(m);
            }
        }
        return methods.toArray(new Method[methods.size()]);
    }
}
