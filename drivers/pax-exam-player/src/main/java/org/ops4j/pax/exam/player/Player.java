/*
 * Copyright 2011 Toni Menzel.
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
package org.ops4j.pax.exam.player;

import static junit.framework.Assert.fail;
import static org.ops4j.pax.exam.spi.PaxExamRuntime.createTestSystem;
import static org.ops4j.pax.exam.spi.PaxExamRuntime.getTestContainerFactory;

import java.io.IOException;

import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.ExceptionHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestAddress;
import org.ops4j.pax.exam.TestContainerFactory;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.spi.DefaultExamReactor;
import org.ops4j.pax.exam.spi.ExamReactor;
import org.ops4j.pax.exam.spi.StagedExamReactor;
import org.ops4j.pax.exam.spi.StagedExamReactorFactory;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * Fully functional alternative Pax Exam Driver.
 * This lets your write fully functional setup-tests "in a tweet".
 *
 * Example :
 * <pre>
 *      new Player( new NativeTestContainerFactory() ).with( new PaxLoggingParts( "1.3.RC4" ) ).play( new BundleCheck().allResolved() );
 * </pre>
 *
 * @author Toni Menzel (toni@okidokiteam.com)
 * @since April, 1st, 2011
 */
public class Player {

    private static final StagedExamReactorFactory DEFAULT_STRATEGY = new PerClass();
    final private TestContainerFactory m_factory;
    final private Option[] m_parts;
    final private TestProbeBuilder m_builder;
    final private ExamSystem m_testSystem;
   
    public Player( TestContainerFactory containerFactory, Option... parts )
        throws IOException
    {
        m_testSystem = createTestSystem();
        m_factory = containerFactory;
        m_parts = parts;
        m_builder = m_testSystem.createProbe();
    }

    public Player( TestContainerFactory containerFactory )
        throws IOException
    {
        this( containerFactory, new Option[ 0 ] );
    }

    public Player()
        throws IOException
    {
        this( getTestContainerFactory() );
    }

    public Player with( Option... parts )
        throws IOException
    {
        return new Player( m_factory, parts );
    }

    public Player test( Class<?> clazz, Object... args )
        throws Exception
    {
        m_builder.addTest( clazz, args );
        return this;
    }

    public void play() throws IOException
    {
        play( DEFAULT_STRATEGY );
    }

    public void play( StagedExamReactorFactory strategy ) throws IOException
    {
        ExamReactor reactor = new DefaultExamReactor( m_testSystem, m_factory );
        reactor.addConfiguration(  m_parts  );
        reactor.addProbe( m_builder );

        StagedExamReactor stagedReactor = reactor.stage( strategy );
        stagedReactor.beforeClass();

        for( TestAddress target : stagedReactor.getTargets() ) {
            try {
                stagedReactor.invoke( target );
            } catch( Exception e ) {
                Throwable t = ExceptionHelper.unwind( e );
                t.printStackTrace();
                fail( t.getMessage() );
            }
        }
        stagedReactor.afterClass();
    }
}
