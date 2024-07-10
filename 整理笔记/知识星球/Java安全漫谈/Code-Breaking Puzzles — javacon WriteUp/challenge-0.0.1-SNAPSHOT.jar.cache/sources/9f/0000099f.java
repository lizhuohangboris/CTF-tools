package org.apache.catalina.valves;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.NetMask;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.PropertyAccessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/RemoteCIDRValve.class */
public final class RemoteCIDRValve extends ValveBase {
    private static final Log log = LogFactory.getLog(RemoteCIDRValve.class);
    private final List<NetMask> allow;
    private final List<NetMask> deny;

    public RemoteCIDRValve() {
        super(true);
        this.allow = new ArrayList();
        this.deny = new ArrayList();
    }

    public String getAllow() {
        return this.allow.toString().replace(PropertyAccessor.PROPERTY_KEY_PREFIX, "").replace("]", "");
    }

    public void setAllow(String input) {
        List<String> messages = fillFromInput(input, this.allow);
        if (messages.isEmpty()) {
            return;
        }
        for (String message : messages) {
            log.error(message);
        }
        throw new IllegalArgumentException(sm.getString("remoteCidrValve.invalid", "allow"));
    }

    public String getDeny() {
        return this.deny.toString().replace(PropertyAccessor.PROPERTY_KEY_PREFIX, "").replace("]", "");
    }

    public void setDeny(String input) {
        List<String> messages = fillFromInput(input, this.deny);
        if (messages.isEmpty()) {
            return;
        }
        for (String message : messages) {
            log.error(message);
        }
        throw new IllegalArgumentException(sm.getString("remoteCidrValve.invalid", "deny"));
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if (isAllowed(request.getRequest().getRemoteAddr())) {
            getNext().invoke(request, response);
        } else {
            response.sendError(403);
        }
    }

    private boolean isAllowed(String property) {
        try {
            InetAddress addr = InetAddress.getByName(property);
            for (NetMask nm : this.deny) {
                if (nm.matches(addr)) {
                    return false;
                }
            }
            for (NetMask nm2 : this.allow) {
                if (nm2.matches(addr)) {
                    return true;
                }
            }
            if (!this.deny.isEmpty() && this.allow.isEmpty()) {
                return true;
            }
            return false;
        } catch (UnknownHostException e) {
            log.error(sm.getString("remoteCidrValve.noRemoteIp"), e);
            return false;
        }
    }

    private List<String> fillFromInput(String input, List<NetMask> target) {
        String[] split;
        target.clear();
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> messages = new LinkedList<>();
        for (String s : input.split("\\s*,\\s*")) {
            try {
                NetMask nm = new NetMask(s);
                target.add(nm);
            } catch (IllegalArgumentException e) {
                messages.add(s + ": " + e.getMessage());
            }
        }
        return Collections.unmodifiableList(messages);
    }
}