/*
 * Copyright 2016 the original author or authors.
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
package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import griffon.inject.BindTo;
import griffon.javafx.test.GriffonTestFXRule;
import griffon.javafx.test.WindowMatchers;
import javafx.scene.control.Button;
import org.example.api.GithubAPI;
import org.example.api.Repository;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.service.support.WaitUntilSupport;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.util.stream.Collectors.toList;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReactiveIntegration2Test {
    private static final String ORGANIZATION = "foo";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Rule
    public GriffonTestFXRule testfx = new GriffonTestFXRule("mainWindow");

    @Inject private ObjectMapper objectMapper;

    @Test
    public void _01_happy_path() {
        // given:
        List<Repository> repositories = createSampleRepositories();
        stubFor(get(urlEqualTo("/orgs/" + ORGANIZATION + "/repos"))
            .willReturn(aResponse()
                .withFixedDelay(200)
                .withStatus(200)
                .withHeader("Content-Type", "text/json")
                .withBody(repositoriesAsJSON(repositories, objectMapper))));

        // when:
        testfx.clickOn("#organization")
            .eraseText(ORGANIZATION.length())
            .write(ORGANIZATION);

        // when:
        testfx.clickOn("#loadActionTarget");

        // wait
        Button loadButton = testfx.lookup("#loadActionTarget").query();
        new WaitUntilSupport().waitUntil(loadButton, isEnabled(), 10);

        // then:
        verifyThat("#total", hasText("10"));
        verifyThat("#repositories", hasItems(10));
    }

    @Test
    public void _02_cancel_path() throws Exception {
        // given:
        String nextUrl = "/organizations/1/repos?page=2";
        List<Repository> repositories = createSampleRepositories();
        stubFor(get(urlEqualTo("/orgs/" + ORGANIZATION + "/repos"))
            .willReturn(aResponse()
                .withFixedDelay(200)
                .withStatus(200)
                .withHeader("Content-Type", "text/json")
                .withHeader("Link", "<http://localhost:8080" + nextUrl + ">; rel=\"next\"")
                .withBody(repositoriesAsJSON(repositories.subList(0, 5), objectMapper))));
        stubFor(get(urlEqualTo(nextUrl))
            .willReturn(aResponse()
                .withFixedDelay(200)
                .withStatus(200)
                .withHeader("Content-Type", "text/json")
                .withBody(repositoriesAsJSON(repositories.subList(5, 10), objectMapper))));

        // when:
        testfx.clickOn("#organization")
            .eraseText(ORGANIZATION.length())
            .write(ORGANIZATION);
        testfx.clickOn("#loadActionTarget");
        testfx.clickOn("#cancelActionTarget");

        // wait
        Button loadButton = testfx.lookup("#loadActionTarget").query();
        new WaitUntilSupport().waitUntil(loadButton, isEnabled(), 2);

        // then:
        verifyThat("#total", hasText("5"));
        verifyThat("#repositories", hasItems(5));
    }

    @Test
    public void _03_failure_path() {
        // given:
        String nextUrl = "/organizations/1/repos?page=2";
        stubFor(get(urlEqualTo("/orgs/" + ORGANIZATION + "/repos"))
            .willReturn(aResponse()
                .withFixedDelay(200)
                .withStatus(200)
                .withHeader("Content-Type", "text/json")
                .withHeader("Link", "<http://localhost:8080" + nextUrl + ">; rel=\"next\"")
                .withBody(repositoriesAsJSON(createSampleRepositories().subList(0, 5), objectMapper))));
        stubFor(get(urlEqualTo(nextUrl))
            .willReturn(aResponse()
                .withFixedDelay(200)
                .withStatus(500)
                .withStatusMessage("Internal Error")));

        // when:
        testfx.clickOn("#organization")
            .eraseText(ORGANIZATION.length())
            .write(ORGANIZATION);
        testfx.clickOn("#loadActionTarget");

        // then:
        new WaitUntilSupport().waitUntil(testfx.window("Error"), WindowMatchers.isShowing(), 5);
    }

    @BindTo(String.class)
    @Named(GithubAPI.GITHUB_API_URL_KEY)
    private String githubApiUrl = "http://localhost:8080";

    public static List<Repository> createSampleRepositories() {
        return IntStream.rangeClosed(1, 10)
            .mapToObj(i -> Repository.builder().name("repo" + i).fullName("foo/repo" + i).build())
            .collect(toList());
    }

    public static String repositoriesAsJSON(Collection<Repository> repositories, ObjectMapper objectMapper) {
        StringWriter writer = new StringWriter();
        try {
            objectMapper.writeValue(writer, repositories);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return writer.toString();
    }
}
