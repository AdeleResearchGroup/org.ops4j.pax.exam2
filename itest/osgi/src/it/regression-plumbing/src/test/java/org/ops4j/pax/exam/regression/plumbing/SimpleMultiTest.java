package org.ops4j.pax.exam.regression.plumbing;

import static org.ops4j.pax.exam.spi.PaxExamRuntime.createTestSystem;
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
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.TestProbeProvider;

/**
 *
 */
public class SimpleMultiTest {

    @Test
    public void bareRunTest() throws Exception {
        Option[] options = new Option[] {};
        ExamSystem system = createTestSystem(options);
        TestProbeProvider p = makeProbe(system);

        // the parse will split all single containers
        for (TestContainer testContainer : getTestContainerFactory().create(system)) {
            try {
                testContainer.start();
                testContainer.install(p.getStream());
                for (TestAddress test : p.getTests()) {
                    testContainer.call(test);
                }
            }
            finally {
                testContainer.stop();
            }
        }
        system.clear();
    }

    private TestProbeProvider makeProbe(ExamSystem system) throws IOException {
        TestProbeBuilder probe = system.createProbe();
        probe.addTests(Probe.class, getAllMethods(Probe.class));
        return probe.build();
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
