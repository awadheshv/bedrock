package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import spock.lang.Unroll

@Unroll
class HrefTagSpec extends BedrockSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content"(path: "/content/global")
                ctmsp()
            }
        }
    }

    def "href for property"() {
        setup:
        def proxy = init(HrefTag, "/content/citytechinc/jcr:content")

        proxy.tag.propertyName = "path"

        when:
        proxy.tag.doEndTag()

        then:
        proxy.output == "/content/global.html"
    }

    def "href for inherited property"() {
        setup:
        def proxy = init(HrefTag, "/content/citytechinc/ctmsp/jcr:content")

        proxy.tag.with {
            propertyName = testPropertyName
            inherit = String.valueOf(testInherit)
        }

        when:
        proxy.tag.doEndTag()

        then:
        proxy.output == output

        where:
        testPropertyName  | testInherit | output
        "path"            | false       | ""
        "path"            | true        | "/content/global.html"
        "nonExistentPath" | false       | ""
        "nonExistentPath" | true        | ""
    }
}