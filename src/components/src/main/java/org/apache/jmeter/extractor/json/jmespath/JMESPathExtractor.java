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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.processor.PostProcessor;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractScopedTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * JMESPATH based extractor
 *
 * @since 5.2
 */
public class JMESPathExtractor extends AbstractScopedTestElement
        implements Serializable, PostProcessor, TestStateListener {

    private static final long serialVersionUID = 3849270294526207081L;

    private static final Logger log = LoggerFactory.getLogger(JMESPathExtractor.class);
    private static final String JMES_PATH_EXPRESSION = "JMESExtractor.jmesPathExpr"; // $NON-NLS-1$
    private static final String REFERENCE_NAME = "JMESExtractor.referenceName"; // $NON-NLS-1$
    private static final String DEFAULT_VALUE = "JMESExtractor.defaultValue"; // $NON-NLS-1$
    private static final String MATCH_NUMBER = "JMESExtractor.matchNumber"; // $NON-NLS-1$
    private static final String REF_MATCH_NR = "_matchNr"; // $NON-NLS-1$
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void process() {
        JMeterContext context = getThreadContext();
        JMeterVariables vars = context.getVariables();
        String jsonResponse = getData(vars, context);
        String refName = getRefName();
        String defaultValue = getDefaultValue();
        int matchNumber = Integer.parseInt(getMatchNumber());
        final String jsonPathExpression = getJmesPathExpression().trim();
        clearOldRefVars(vars, refName);
        if (StringUtils.isEmpty(jsonResponse)) {
            if (log.isDebugEnabled()) {
                log.debug("Response or source variable is null or empty for {}", getName());
            }
            vars.put(refName, defaultValue);
        } else {
            try {
                JsonNode result = null;
                JsonNode actualObj = OBJECT_MAPPER.readValue(jsonResponse, JsonNode.class);
                result = JMESPathCache.getInstance().get(jsonPathExpression).search(actualObj);
                if (result.isNull()) {
                    vars.put(refName, defaultValue);
                    vars.put(refName + REF_MATCH_NR, "0"); //$NON-NLS-1$
                    if (matchNumber < 0) {
                        log.debug("No value extracted, storing empty in: {}", refName);
                    }
                } else {
                    List<String> resultList = splitJson(result);
                    // if more than one value extracted, suffix with "_index"
                    if (resultList.size() > 1) {
                        if (matchNumber < 0) {
                            // Extract all
                            int index = 1;
                            for (String extractedString : resultList) {
                                vars.put(refName + "_" + index, extractedString); // $NON-NLS-1$
                                index++;
                            }
                        } else if (matchNumber == 0) {
                            // Random extraction
                            int matchSize = resultList.size();
                            int matchNr = JMeterUtils.getRandomInt(matchSize);
                            placeObjectIntoVars(vars, refName, resultList, matchNr);
                        } else {
                            // extract at position
                            if (matchNumber > resultList.size()) {
                                if (log.isDebugEnabled()) {
                                    log.debug(
                                            "matchNumber({}) exceeds number of items found({}), default value will be used",
                                            matchNumber, resultList.size());
                                }
                                vars.put(refName, defaultValue);
                            } else {
                                placeObjectIntoVars(vars, refName, resultList, matchNumber - 1);
                            }
                        }
                    } else {
                        // else just one value extracted
                        String suffix = (matchNumber < 0) ? "_1" : "";
                        placeObjectIntoVars(vars, refName + suffix, resultList, 0);
                    }
                    vars.put(refName + REF_MATCH_NR, Integer.toString(resultList.size()));
                }
            } catch (Exception e) {
                // if something wrong, default value added
                if (log.isDebugEnabled()) {
                    log.debug("Error processing JSON content in {}, message: {}", getName(), e.getLocalizedMessage(), e);
                } else {
                    log.debug("Error processing JSON content in {}, message: {}", getName(), e.getLocalizedMessage());
                }
                vars.put(refName, defaultValue);
            }
        }
    }

    private String getData(JMeterVariables vars, JMeterContext context) {
        String jsonResponse = null;
        if (isScopeVariable()) {
            jsonResponse = vars.get(getVariableName());
            if (log.isDebugEnabled()) {
                log.debug("JMESExtractor is using variable: {}, which content is: {}", getVariableName(), jsonResponse);
            }
        } else {
            SampleResult previousResult = context.getPreviousResult();
            if (previousResult != null) {
                jsonResponse = previousResult.getResponseDataAsString();
            }
            if (log.isDebugEnabled()) {
                log.debug("JMESExtractor {} working on Response: {}", getName(), jsonResponse);
            }
        }
        return jsonResponse;
    }

    public List<String> splitJson(JsonNode jsonNode) throws IOException {
        List<String> splittedJsonElements = new ArrayList<>();
        if (jsonNode.isArray()) {
            for (JsonNode element : (ArrayNode) jsonNode) {
                splittedJsonElements.add(writeJsonNode(OBJECT_MAPPER, element));
            }
        } else {
            splittedJsonElements.add(writeJsonNode(OBJECT_MAPPER, jsonNode));
        }
        return splittedJsonElements;
    }

    private static String writeJsonNode(ObjectMapper mapper, JsonNode element) throws JsonProcessingException {
        if (element.isTextual()) {
            return element.asText();
        } else {
            return mapper.writeValueAsString(element);
        }
    }

    void clearOldRefVars(JMeterVariables vars, String refName) {
        vars.remove(refName + REF_MATCH_NR);
        for (int i = 1; vars.get(refName + "_" + i) != null; i++) {
            vars.remove(refName + "_" + i);
        }
    }

    private void placeObjectIntoVars(JMeterVariables vars, String refName, List<String> extractedValues, int matchNr) {
        vars.put(refName, extractedValues.get(matchNr));
    }

    public String getJmesPathExpression() {
        return getPropertyAsString(JMES_PATH_EXPRESSION);
    }

    public void setJmesPathExpression(String jsonPath) {
        setProperty(JMES_PATH_EXPRESSION, jsonPath);
    }

    public String getRefName() {
        return getPropertyAsString(REFERENCE_NAME);
    }

    public void setRefName(String refName) {
        setProperty(REFERENCE_NAME, refName);
    }

    public String getDefaultValue() {
        return getPropertyAsString(DEFAULT_VALUE);
    }

    public void setDefaultValue(String defaultValue) {
        setProperty(DEFAULT_VALUE, defaultValue, ""); // $NON-NLS-1$
    }

    public void setMatchNumber(String matchNumber) {
        setProperty(MATCH_NUMBER, matchNumber);
    }

    public String getMatchNumber() {
        return getPropertyAsString(MATCH_NUMBER);
    }

    @Override
    public void testStarted() {
        testStarted("");
    }

    @Override
    public void testStarted(String host) {
        // NOOP
    }

    @Override
    public void testEnded() {
        testEnded("");
    }

    @Override
    public void testEnded(String host) {
        JMESPathCache.getInstance().cleanUp();
    }
}
