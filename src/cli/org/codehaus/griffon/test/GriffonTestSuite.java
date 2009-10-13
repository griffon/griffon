/*
 * Copyright 2004-2005 the original author or authors.
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
package org.codehaus.griffon.test;

// import griffon.util.GriffonNameUtils;
// import griffon.util.GriffonWebUtil;
import griffon.util.IGriffonApplication;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
// import org.codehaus.groovy.griffon.commons.spring.GriffonWebApplicationContext;
// import org.codehaus.groovy.griffon.web.servlet.GriffonApplicationAttributes;
// import org.codehaus.groovy.griffon.web.servlet.mvc.GriffonWebRequest;
// import org.codehaus.groovy.griffon.web.context.ServletContextHolder;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.context.ApplicationContext;
// import org.springframework.transaction.PlatformTransactionManager;
// import org.springframework.transaction.TransactionStatus;
// import org.springframework.transaction.support.TransactionCallback;
// import org.springframework.transaction.support.TransactionTemplate;
// import org.springframework.web.context.request.RequestContextHolder;


/**
 * IoC class to inject properties of Griffon test case classes.
 * 
 *
 * @author GraemeRocher
 * @author Steven Devijver
 *
 *
 * @since 0.1
 *
 * Created: Aug 28, 2005
 */
public class GriffonTestSuite extends TestSuite {
//    private static final String TRANSACTIONAL = "transactional";

//    private Pattern controllerTestPattern;
//    private ApplicationContext applicationContext;
    private IGriffonApplication app;

    public GriffonTestSuite(IGriffonApplication app, String testSuffix) {
        super();
        init(app, testSuffix);
    }

    public GriffonTestSuite(IGriffonApplication app, Class clazz, String testSuffix) {
		super(clazz);
        init(app, testSuffix);
    }

	public void runTest(final Test test, final TestResult result) {
        // Auto-wire the test
//        if (test instanceof TestCase && applicationContext != null) {
//			applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(
//                    test,
//                    AutowireCapableBeanFactory.AUTOWIRE_BY_NAME,
//                    false);
//		}

        // If the test is ApplicationContextAware, wire the context in now.
//        if (test instanceof ApplicationContextAware && applicationContext != null) {
//            ((ApplicationContextAware) test).setApplicationContext(applicationContext);
//        }
        try {
            InvokerHelper.setProperty(test, "app", app);
        } catch (RuntimeException ignore) { }


//         try {
//             GriffonWebRequest webRequest = GriffonWebUtil.bindMockWebRequest(applicationContext);
//             ServletContextHolder.setServletContext(webRequest.getServletContext());
//             webRequest.getServletContext().setAttribute(GriffonApplicationAttributes.APPLICATION_CONTEXT, applicationContext);
// 
//             if (isTransactional(test)) {
//                 if (applicationContext.containsBean("transactionManager")) {
//                     // Run the test inside a transaction.
//                     TransactionTemplate template = new TransactionTemplate(
//                             (PlatformTransactionManager) applicationContext.getBean("transactionManager"));
//                     template.execute(new TransactionCallback() {
//                         public Object doInTransaction(TransactionStatus status) {
//                             test.run(result);
//                             status.setRollbackOnly();
//                             return null;
//                         }
//                     });
//                 } else {
//                     throw new RuntimeException("There is no test TransactionManager defined and integration " +
//                             "test ${test.name} does not set transactional = false");
//                 }
//             }
//             else {
                test.run(result);
//             }
//         }
//         finally {
//             RequestContextHolder.setRequestAttributes(null);
//             ServletContextHolder.setServletContext(null);
//         }
	}

//     public boolean isTransactional(Test test) {
//         Object val = GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(test, TRANSACTIONAL);
//         return !(val instanceof Boolean) || (Boolean) val;
//     }
    
    private void init(IGriffonApplication app, String testSuffix) {
 		this.app = app;
//        this.controllerTestPattern = Pattern.compile("^(\\w+)Controller" + testSuffix + "$");
    }

    /**
     * Some code under test will rely on the "controllerName" dynamic
     * property. This is normally done by
     * {@link org.codehaus.groovy.griffon.web.mapping.DefaultUrlMappingInfo},
     * but the URL mapping stuff doesn't happen in integration tests.
     * So, we set it manually if the name of the test looks like
     * "...Controller${testSuffix}". If the test name does not match
     * a controller, then we set "controllerName" to a default value.
     * @todo this is horrible and dirty, should find a better way
     * @param test The test we're running.
     * @param webRequest The Griffon web request to set the controller
     * name on.
     */
//     void initControllerName(Test test, GriffonWebRequest webRequest) {
//         Matcher matcher = controllerTestPattern.matcher(test.getClass().getName());
//         if (matcher.matches()) {
//             String name = matcher.group(1);
//             webRequest.setControllerName(GriffonNameUtils.getLogicalPropertyName(name, "Controller"));
//         }
//         else {
//             // Provide a default 'current' controller name.
//             webRequest.setControllerName("test");
//         }
//     }
}
