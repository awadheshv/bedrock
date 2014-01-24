/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.servlets.replication;

import com.citytechinc.cq.library.services.replication.SelectiveReplicationService;
import com.citytechinc.cq.library.servlets.AbstractJsonResponseServlet;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Selectively replicate content to one or more replication agents.
 */
@SlingServlet(paths = "/bin/replicate/selective")
public final class SelectiveReplicationServlet extends AbstractJsonResponseServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SelectiveReplicationServlet.class);

    private static final String PARAMETER_ACTION = "action";

    private static final String PARAMETER_AGENT_IDS = "agentIds";

    private static final String PARAMETER_PATHS = "paths";

    private static final long serialVersionUID = 1L;

    @Reference
    protected AgentManager agentManager;

    @Reference
    protected SelectiveReplicationService selectiveReplicationService;

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws ServletException, IOException {
        final String[] paths = request.getParameterValues(PARAMETER_PATHS);
        final String[] agentIds = request.getParameterValues(PARAMETER_AGENT_IDS);

        checkArgument(!(paths == null || paths.length == 0), "paths parameter must be non-null and non-empty");
        checkArgument(!(agentIds == null || agentIds.length == 0), "agentIds parameter must be non-null and non-empty");

        final String action = request.getParameter(PARAMETER_ACTION);
        final ReplicationActionType actionType = ReplicationActionType.fromName(action);

        checkArgument(actionType != null, "invalid action parameter = %s", action);

        final Set<String> uniquePaths = Sets.newLinkedHashSet(Arrays.asList(paths));
        final Set<String> uniqueAgentIds = Sets.newLinkedHashSet(Arrays.asList(agentIds));

        checkArgument(agentsExist(uniqueAgentIds),
            "invalid agent IDs, one or more of the provided agent IDs does not exist");

        final Session session = request.getResourceResolver().adaptTo(Session.class);

        final List<Map<String, Boolean>> result = Lists.newArrayList();

        for (final String path : uniquePaths) {
            boolean success;

            LOG.info("doPost() executing replication action = {} for path = {} to agent IDs = {}",
                new Object[]{ actionType, path, agentIds });

            try {
                selectiveReplicationService.replicate(session, path, actionType, uniqueAgentIds);

                success = true;
            } catch (ReplicationException e) {
                LOG.error("error executing replication action = " + actionType + " for path = " + path, e);

                success = false;
            }

            result.add(ImmutableMap.of(path, success));
        }

        writeJsonResponse(response, result);
    }

    private boolean agentsExist(final Set<String> agentIds) {
        return agentManager.getAgents().keySet().containsAll(agentIds);
    }
}
