/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.core.replication

import com.citytechinc.aem.bedrock.core.servlets.replication.SelectiveReplicationServlet
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.day.cq.replication.Agent
import com.day.cq.replication.AgentManager
import com.day.cq.replication.ReplicationActionType
import com.day.cq.replication.Replicator
import groovy.json.JsonBuilder
import spock.lang.Unroll

@Unroll
class SelectiveReplicationServletSpec extends BedrockSpec {

    def "null parameters throw exception"() {
        setup:
        def servlet = new SelectiveReplicationServlet()

        def request = requestBuilder.build()
        def response = responseBuilder.build()

        servlet.agentManager = Mock(AgentManager)
        servlet.replicator = Mock(Replicator)

        when:
        servlet.doPost(request, response)

        then:
        thrown(IllegalArgumentException)
    }

    def "invalid parameters throw exception"() {
        setup:
        def servlet = new SelectiveReplicationServlet()

        def request = requestBuilder.build {
            parameters = [paths: paths, agentIds: agentIds, action: action]
        }

        def response = responseBuilder.build()

        servlet.agentManager = Mock(AgentManager)
        servlet.replicator = Mock(Replicator)

        when:
        servlet.doPost(request, response)

        then:
        thrown(IllegalArgumentException)

        where:
        paths        | agentIds    | action
        []           | []          | ""
        ["/content"] | ["publish"] | ""
        []           | ["publish"] | ReplicationActionType.ACTIVATE.name()
        ["/content"] | []          | ReplicationActionType.ACTIVATE.name()
    }

    def "valid parameters"() {
        setup:
        def servlet = new SelectiveReplicationServlet()

        def request = requestBuilder.build {
            parameters = [paths: ["/content", "/etc"], agentIds: ["publish"], action: ReplicationActionType.ACTIVATE.name()]
        }

        def response = responseBuilder.build()

        def agent = Mock(Agent)
        def agentManager = Mock(AgentManager)
        def replicator = Mock(Replicator)

        servlet.agentManager = agentManager
        servlet.replicator = replicator

        def json = new JsonBuilder(["/content": true, "/etc": true]).toString()

        when:
        servlet.doPost(request, response)

        then:
        1 * agentManager.agents >> ["publish": agent]
        2 * replicator.replicate(*_)

        then:
        response.contentAsString == json
    }
}