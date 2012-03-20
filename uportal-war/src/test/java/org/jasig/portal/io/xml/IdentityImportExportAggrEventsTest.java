/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portal.io.xml;

import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jasig.portal.events.aggr.groups.AggregatedGroupLookupDao;
import org.jasig.portal.io.xml.eventaggr.ExternalEventAggregationConfiguration;
import org.jasig.portal.spring.MockitoFactoryBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Function;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/org/jasig/portal/io/xml/importExportAggrEventTestContext.xml")
public class IdentityImportExportAggrEventsTest extends AbstractIdentityImportExportTest {
    @javax.annotation.Resource(name="eventAggregationConfigurationImporterExporter")
    private IDataImporter<ExternalEventAggregationConfiguration> eventAggregationConfigurationImporter;
    @javax.annotation.Resource(name="eventAggregationConfigurationImporterExporter")
    private IDataExporter<ExternalEventAggregationConfiguration> eventAggregationConfigurationExporter;
    
    @Autowired
    private AggregatedGroupLookupDao aggregatedGroupLookupDao;
    private TimeZone defaultTimeZone;
    
    @PersistenceContext(unitName = "uPortalAggrEventsPersistence")
    private EntityManager entityManager;
    
    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
    
    @Before
    public void setup() {
        MockitoFactoryBean.resetAllMocks();
        
        defaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("EST"));
    }
    
    @After
    public void cleanup() {
        if (defaultTimeZone != null) {
            TimeZone.setDefault(defaultTimeZone);
        }
    }

    
  
    @Test
    public void testEventAggregationConfigurationImportExport() throws Exception {
        final ClassPathResource dataResource = new ClassPathResource("/org/jasig/portal/io/xml/event-aggregation/default.event-aggregation.xml");
        
        this.<ExternalEventAggregationConfiguration>testIdentityImportExport(
                this.eventAggregationConfigurationImporter, this.eventAggregationConfigurationExporter,
                dataResource,
                new Function<ExternalEventAggregationConfiguration, String>() {
                    @Override
                    public String apply(ExternalEventAggregationConfiguration input) {
                        return "default";
                    }
                });
    }
    
    
    
}