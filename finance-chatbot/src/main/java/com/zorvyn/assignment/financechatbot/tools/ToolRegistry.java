package com.zorvyn.assignment.financechatbot.tools;

import com.zorvyn.assignment.financechatbot.security.Role;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Decides which tool objects are exposed to the LLM for a given role. This is the
 * first line of role-based access control for the chatbot: a VIEWER never even
 * receives the write/admin tools. The Finance API re-checks permissions on every
 * forwarded call as defense-in-depth.
 *
 * <pre>
 *   VIEWER   -> dashboard
 *   ANALYST  -> dashboard + record reads
 *   ADMIN    -> dashboard + record reads + record writes + user administration
 * </pre>
 */
@Component
public class ToolRegistry {

    private final DashboardTools dashboardTools;
    private final RecordReadTools recordReadTools;
    private final RecordWriteTools recordWriteTools;
    private final UserAdminTools userAdminTools;

    public ToolRegistry(DashboardTools dashboardTools,
                        RecordReadTools recordReadTools,
                        RecordWriteTools recordWriteTools,
                        UserAdminTools userAdminTools) {
        this.dashboardTools = dashboardTools;
        this.recordReadTools = recordReadTools;
        this.recordWriteTools = recordWriteTools;
        this.userAdminTools = userAdminTools;
    }

    public Object[] toolsFor(Role role) {
        List<Object> tools = new ArrayList<>();
        tools.add(dashboardTools);                  // VIEWER and up

        if (role == Role.ANALYST || role == Role.ADMIN) {
            tools.add(recordReadTools);
        }
        if (role == Role.ADMIN) {
            tools.add(recordWriteTools);
            tools.add(userAdminTools);
        }
        return tools.toArray();
    }
}
