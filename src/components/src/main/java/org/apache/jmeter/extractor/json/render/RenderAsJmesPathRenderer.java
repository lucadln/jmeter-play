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

package org.apache.jmeter.extractor.json.render;

import org.apache.jmeter.extractor.json.jmespath.JMESPathCache;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Implement ResultsRender for JMES Path tester
 * @since 5.2
 */
public class RenderAsJmesPathRenderer extends AbstractRenderAsJsonRenderer {
    private static final Logger log = LoggerFactory.getLogger(RenderAsJmesPathRenderer.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected String getTabLabel() {
        return JMeterUtils.getResString("jmespath_tester_title");
    }

    @Override
    protected String getTestButtonLabel() {
        return JMeterUtils.getResString("jmespath_tester_button_test");
    }

    @Override
    protected String getExpressionLabel() {
        return JMeterUtils.getResString("jmespath_tester_field");
    }

    @Override
    protected String process(String textToParse) {
        String expression = getExpression();
        try {
            JsonNode actualObj = OBJECT_MAPPER.readValue(textToParse, JsonNode.class);
            JsonNode result = JMESPathCache.getInstance().get(expression).search(actualObj);
            if (result.isNull()) {
                return NO_MATCH; //$NON-NLS-1$
            } else {
                StringBuilder builder = new StringBuilder();
                int i = 0;
                if (result.isArray()) {
                    for (JsonNode element : (ArrayNode) result) {
                        builder.append("Result[").append(i++).append("]=").append(writeJsonNode(OBJECT_MAPPER, element)).append("\n");
                    }
                } else {
                    builder.append("Result[").append(i++).append("]=").append(writeJsonNode(OBJECT_MAPPER, result)).append("\n");
                }
                return builder.toString();
            }
        } catch (Exception e) { // NOSONAR We handle it through return message
            log.debug("Exception extracting from '{}' with expression '{}'", textToParse, expression);
            return "Exception: " + e.getMessage(); //$NON-NLS-1$
        }
    }

    private static String writeJsonNode(ObjectMapper mapper, JsonNode element) throws JsonProcessingException {
        if (element.isTextual()) {
            return element.asText();
        } else {
            return mapper.writeValueAsString(element);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return JMeterUtils.getResString("jmespath_renderer"); // $NON-NLS-1$
    }
}
