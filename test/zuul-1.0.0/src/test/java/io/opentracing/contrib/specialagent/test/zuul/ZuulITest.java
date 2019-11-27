/* Copyright 2019 The OpenTracing Authors
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

package io.opentracing.contrib.specialagent.test.zuul;

import io.opentracing.contrib.specialagent.TestUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableZuulProxy
public class ZuulITest {
  public static void main(final String[] args) {
    TestUtil.initTerminalExceptionHandler();

    SpringApplication.run(ZuulITest.class, args).close();
  }

  @Bean
  public CommandLineRunner commandLineRunner() {
    return new CommandLineRunner() {
      @Override
      public void run(String... args) {

        RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:8080", String.class);
        if (entity.getStatusCode().value() != 200) {
          throw new AssertionError("ERROR: Zuul failed");
        }
        System.out.println(entity.getStatusCode());

        TestUtil.checkSpan("zuul", 4);
      }
    };
  }

}