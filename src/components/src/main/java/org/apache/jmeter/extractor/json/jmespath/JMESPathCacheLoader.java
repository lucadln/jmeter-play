/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.jmeter.extractor.json.jmespath;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.CacheLoader;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.RuntimeConfiguration;
import io.burt.jmespath.function.FunctionRegistry;
import io.burt.jmespath.jackson.JacksonRuntime;

/**
 * @since 5.2
 */
public class JMESPathCacheLoader implements CacheLoader<String, Expression<JsonNode>> {
    final JmesPath<JsonNode> runtime;

    public JMESPathCacheLoader() {
        runtime = new JacksonRuntime(
                new RuntimeConfiguration.Builder().withFunctionRegistry(FunctionRegistry.defaultRegistry()).build());
    }

    @Override
    public Expression<JsonNode> load(String jmesPathExpression) throws Exception {
        return runtime.compile(jmesPathExpression);
    }
}
