package org.apache.tomcat.util.net.openssl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.SSLConf;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/openssl/OpenSSLConf.class */
public class OpenSSLConf implements Serializable {
    private static final long serialVersionUID = 1;
    private static final Log log = LogFactory.getLog(OpenSSLConf.class);
    private static final StringManager sm = StringManager.getManager(OpenSSLConf.class);
    private final List<OpenSSLConfCmd> commands = new ArrayList();

    public void addCmd(OpenSSLConfCmd cmd) {
        this.commands.add(cmd);
    }

    public List<OpenSSLConfCmd> getCommands() {
        return this.commands;
    }

    public boolean check(long cctx) throws Exception {
        boolean result = true;
        for (int i = 0; i < this.commands.size(); i++) {
            OpenSSLConfCmd cmd = this.commands.get(i);
            String name = cmd.getName();
            String value = cmd.getValue();
            if (name == null) {
                log.error(sm.getString("opensslconf.noCommandName", value));
                result = false;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("opensslconf.checkCommand", name, value));
                }
                try {
                    int rc = SSLConf.check(cctx, name, value);
                    if (rc <= 0) {
                        log.error(sm.getString("opensslconf.failedCommand", name, value, Integer.toString(rc)));
                        result = false;
                    } else if (log.isDebugEnabled()) {
                        log.debug(sm.getString("opensslconf.resultCommand", name, value, Integer.toString(rc)));
                    }
                } catch (Exception e) {
                    log.error(sm.getString("opensslconf.checkFailed"));
                    return false;
                }
            }
        }
        if (!result) {
            log.error(sm.getString("opensslconf.checkFailed"));
        }
        return result;
    }

    public boolean apply(long cctx, long ctx) throws Exception {
        boolean result = true;
        SSLConf.assign(cctx, ctx);
        for (int i = 0; i < this.commands.size(); i++) {
            OpenSSLConfCmd cmd = this.commands.get(i);
            String name = cmd.getName();
            String value = cmd.getValue();
            if (name == null) {
                log.error(sm.getString("opensslconf.noCommandName", value));
                result = false;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("opensslconf.applyCommand", name, value));
                }
                try {
                    int rc = SSLConf.apply(cctx, name, value);
                    if (rc <= 0) {
                        log.error(sm.getString("opensslconf.failedCommand", name, value, Integer.toString(rc)));
                        result = false;
                    } else if (log.isDebugEnabled()) {
                        log.debug(sm.getString("opensslconf.resultCommand", name, value, Integer.toString(rc)));
                    }
                } catch (Exception e) {
                    log.error(sm.getString("opensslconf.applyFailed"));
                    return false;
                }
            }
        }
        int rc2 = SSLConf.finish(cctx);
        if (rc2 <= 0) {
            log.error(sm.getString("opensslconf.finishFailed", Integer.toString(rc2)));
            result = false;
        }
        if (!result) {
            log.error(sm.getString("opensslconf.applyFailed"));
        }
        return result;
    }
}