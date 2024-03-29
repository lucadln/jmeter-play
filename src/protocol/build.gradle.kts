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

subprojects {
    dependencies {
        api(project(":src:core"))
        testCompile(project(":src:core", "testClasses"))
    }
}

project("ftp") {
    dependencies {
        implementation("commons-net:commons-net:3.6")
        implementation("commons-io:commons-io") {
            because("IOUtils")
        }
        implementation("org.apache.commons:commons-lang3") {
            because("StringUtils")
        }
    }
}

project("http") {
    dependencies {
        // for SearchTextExtension
        api(project(":src:components"))
        testCompile(project(":src:components", "testClasses"))

        api("com.thoughtworks.xstream:xstream") {
            because("HTTPResultConverter uses XStream in public API")
        }

        compileOnly("javax.activation:javax.activation-api") {
            because("ParseCurlCommandAction uses new MimetypesFileTypeMap()")
        }

        implementation("com.github.ben-manes.caffeine:caffeine")
        implementation("commons-io:commons-io") {
            because("IOUtils")
        }
        implementation("org.apache.commons:commons-lang3") {
            because("StringUtils")
        }
        implementation("org.apache.commons:commons-text") {
            because("StringEscapeUtils")
        }
        implementation("org.jodd:jodd-lagarto")
        implementation("org.jsoup:jsoup")
        implementation("oro:oro")
        implementation("commons-collections:commons-collections")
        implementation("commons-net:commons-net:3.6")
        implementation("com.helger:ph-commons:9.2.1") {
            // We don't really need to use/distribute jsr305
            exclude("com.google.code.findbugs", "jsr305")
        }
        implementation("com.helger:ph-css:6.1.1") {
            // We don't really need to use/distribute jsr305
            exclude("com.google.code.findbugs", "jsr305")
        }
        implementation("dnsjava:dnsjava:2.1.8")
        implementation("org.apache.httpcomponents:httpmime:4.5.8")
        implementation("org.brotli:dec:0.1.2")
        testImplementation("com.github.tomakehurst:wiremock-jre8")
    }

}

project("java") {
    dependencies {
        implementation("org.apache.commons:commons-lang3") {
            because("ArrayUtils")
        }
        implementation("commons-io:commons-io") {
            because("IOUtils")
        }
    }
}

project("jdbc") {
    dependencies {
        implementation("org.apache.commons:commons-dbcp2:2.5.0")
        implementation("org.apache.commons:commons-lang3") {
            because("StringUtils, ObjectUtils")
        }
        implementation("commons-io:commons-io") {
            because("IOUtils")
        }
    }
}

project("jms") {
    dependencies {
        testCompile(project(":src:core", "testClasses"))
        api("com.github.ben-manes.caffeine:caffeine") {
            because("MessageRenderer#getValueFromFile(..., caffeine.cache.Cache)")
        }
        // TODO: technically speaking, jms_1.1_spec should be compileOnly
        // since we either include a JMS implementation or we can't use JMS at all
        implementation("org.apache.geronimo.specs:geronimo-jms_1.1_spec:1.1.1")
        implementation("org.apache.commons:commons-lang3") {
            because("StringUtils")
        }
        implementation("commons-io:commons-io") {
            because("IOUtils")
        }
    }
}

project("junit") {
    dependencies {
        api("junit:junit")
        implementation("org.apache.commons:commons-lang3") {
            because("ArrayUtils")
        }
        implementation("org.exparity:hamcrest-date") {
            because("hamcrest-date.jar was historically shipped with JMeter")
        }
    }
}

project("junit-sample") {
    dependencies {
        api("junit:junit")
    }
}

project("ldap") {
    dependencies {
        implementation("org.apache.commons:commons-text") {
            because("StringEscapeUtils")
        }
        implementation("org.apache.commons:commons-lang3") {
            because("StringUtils")
        }
    }
}

project("mail") {
    dependencies {
        api("javax.mail:mail:1.5.0-b01") {
            exclude("javax.activation", "activation")
        }
        // There's no javax.activation:activation:1.2.0, so we use com.sun...
        runtimeOnly("com.sun.activation:javax.activation")
        // This is an API-only jar. javax.activation is present in Java 8,
        // however it is not there in Java 9
        compileOnly("javax.activation:javax.activation-api")
        implementation("org.apache.commons:commons-lang3") {
            because("StringUtils")
        }
        implementation("commons-io:commons-io") {
            because("IOUtils")
        }
    }
}

project("mongodb") {
    dependencies {
        api("org.mongodb:mongo-java-driver:2.11.3")
        implementation("org.apache.commons:commons-lang3") {
            because("StringUtils")
        }
    }
}

project("native") {
    dependencies {
        implementation("org.apache.commons:commons-lang3") {
            because("StringUtils")
        }
    }
}

project("tcp") {
    dependencies {
        implementation("org.apache.commons:commons-lang3") {
            because("ArrayUtils")
        }
        implementation("commons-io:commons-io") {
            because("IOUtils")
        }
    }
}
