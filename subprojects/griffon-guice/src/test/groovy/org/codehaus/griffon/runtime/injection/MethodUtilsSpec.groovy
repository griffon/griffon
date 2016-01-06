/*
 * Copyright 2008-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.injection

import griffon.exceptions.InstanceMethodInvocationException
import spock.lang.Specification

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

class MethodUtilsSpec extends Specification {
    void 'Find @PostConstruct annotated method on valid instance'() {
        given:
        ValidBean bean = new ValidBean()

        expect:
        MethodUtils.hasMethodAnnotatedwith(bean, PostConstruct)
    }

    void 'Find @PreDestroy annotated method on valid instance'() {
        given:
        ValidBean bean = new ValidBean()

        expect:
        MethodUtils.hasMethodAnnotatedwith(bean, PreDestroy)
    }

    void 'Invoke @PostConstruct annotated method on valid instance'() {
        given:
        ValidBean bean = new ValidBean()

        expect:
        !bean.postConstruct

        when:
        MethodUtils.invokeAnnotatedMethod(bean, PostConstruct)

        then:
        bean.postConstruct
    }

    void 'Invoke @PreDestroy annotated method on valid instance'() {
        given:
        ValidBean bean = new ValidBean()

        expect:
        !bean.preDestroy

        when:
        MethodUtils.invokeAnnotatedMethod(bean, PreDestroy)

        then:
        bean.preDestroy
    }

    void 'Invoke @PostConstruct annotated method on invalid instance'() {
        given:

        InvalidBean bean = new InvalidBean()

        when:
        MethodUtils.invokeAnnotatedMethod(bean, PostConstruct)

        then:
        thrown(InstanceMethodInvocationException)
    }
}

class ValidBean {
    boolean postConstruct
    boolean preDestroy

    @PostConstruct
    void init() {
        postConstruct = true
    }

    @PreDestroy
    void destroy(){
       preDestroy = true
    }
}

class InvalidBean {
    @PostConstruct
    void init() {}

    @PostConstruct
    void start() {}
}