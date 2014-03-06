/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.page.predicates

import com.citytechinc.aem.bedrock.testing.specs.BedrockSpec

class TemplatePredicateSpec extends BedrockSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content"("cq:template": "homepage")
                child1 {
                    "jcr:content"("cq:template": "template")
                }
                child2()
            }
        }
    }

    def "page has no template property"() {
        setup:
        def page = getPage("/content/citytechinc/child2")
        def predicate = new TemplatePredicate("template")

        expect:
        !predicate.apply(page)
    }

    def "template matches page template"() {
        setup:
        def page = getPage("/content/citytechinc/child1")
        def predicate = new TemplatePredicate("template")

        expect:
        predicate.apply(page)
    }

    def "template does not match page template"() {
        setup:
        def page = getPage("/content/citytechinc")
        def predicate = new TemplatePredicate("template")

        expect:
        !predicate.apply(page)
    }
}
