/*
 * Copyright 2010 Harald Wellmann
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.ops4j.pax.exam.servlet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.ops4j.pax.exam.TestContainerException;
import org.ops4j.pax.exam.util.Injector;
import org.ops4j.pax.exam.util.InjectorFactory;
import org.ops4j.spi.ServiceProviderFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for test runner servlets, providing the communication link between the embedded test
 * container and the test driver.
 * <p>
 * Derived classes shall provide a method of dependency injection.
 * 
 * @author Harald Wellmann
 * 
 */
@WebServlet(urlPatterns = "/testrunner")
public class TestRunnerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(TestRunnerServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info("TestRunnerServlet loaded");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String className = request.getParameter("class");
        String methodName = request.getParameter("method");
        try {
            Class<?> clazz = getClass().getClassLoader().loadClass(className);
            response.setContentType("application/octet-stream");
            ServletOutputStream os = response.getOutputStream();
            runSuite(os, clazz, methodName);
            os.flush();
        }
        catch (ClassNotFoundException exc) {
            throw new ServletException("cannot load test class " + className, exc);
        }
    }

    private void runSuite(OutputStream os, Class<?> clazz, String methodName) throws IOException {

        InjectorFactory injectorFactory = ServiceProviderFinder
            .loadUniqueServiceProvider(InjectorFactory.class);
        injectorFactory.setContext(getServletContext());
        Injector injector = injectorFactory.createInjector();
        Request classRequest = new ContainerTestRunnerClassRequest(clazz, injector);
        Description method = Description.createTestDescription(clazz, methodName);
        Request request = classRequest.filterWith(method);

        JUnitCore core = new JUnitCore();
        Result result = core.run(request);
        List<Failure> failures = result.getFailures();

        /*
         * The invoker may not be able to deserialize the original exception due to different
         * classloaders. For this reason, we only take the stack trace and wrap it in a
         * TestContainerException.
         */
        ObjectOutputStream oos = new ObjectOutputStream(os);
        for (Failure failure : failures) {
            Exception exc = new TestContainerException(failure.getTrace());
            oos.writeObject(exc);
        }
        if (failures.isEmpty()) {
            oos.writeObject("ok");
        }
    }
}
