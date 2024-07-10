package org.apache.catalina.realm;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.LifecycleException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.file.ConfigFileLoader;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/MemoryRealm.class */
public class MemoryRealm extends RealmBase {
    private static final Log log = LogFactory.getLog(MemoryRealm.class);
    private static Digester digester = null;
    private String pathname = "conf/tomcat-users.xml";
    private final Map<String, GenericPrincipal> principals = new HashMap();

    public String getPathname() {
        return this.pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(String username, String credentials) {
        if (username == null || credentials == null) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("memoryRealm.authenticateFailure", username));
                return null;
            }
            return null;
        }
        GenericPrincipal principal = this.principals.get(username);
        if (principal == null || principal.getPassword() == null) {
            getCredentialHandler().mutate(credentials);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("memoryRealm.authenticateFailure", username));
                return null;
            }
            return null;
        }
        boolean validated = getCredentialHandler().matches(credentials, principal.getPassword());
        if (validated) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("memoryRealm.authenticateSuccess", username));
            }
            return principal;
        } else if (log.isDebugEnabled()) {
            log.debug(sm.getString("memoryRealm.authenticateFailure", username));
            return null;
        } else {
            return null;
        }
    }

    public void addUser(String username, String password, String roles) {
        List<String> list = new ArrayList<>();
        String str = roles + ",";
        while (true) {
            String roles2 = str;
            int comma = roles2.indexOf(44);
            if (comma >= 0) {
                String role = roles2.substring(0, comma).trim();
                list.add(role);
                str = roles2.substring(comma + 1);
            } else {
                GenericPrincipal principal = new GenericPrincipal(username, password, list);
                this.principals.put(username, principal);
                return;
            }
        }
    }

    protected synchronized Digester getDigester() {
        if (digester == null) {
            digester = new Digester();
            digester.setValidating(false);
            try {
                digester.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
            } catch (Exception e) {
                log.warn(sm.getString("memoryRealm.xmlFeatureEncoding"), e);
            }
            digester.addRuleSet(new MemoryRuleSet());
        }
        return digester;
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected String getPassword(String username) {
        GenericPrincipal principal = this.principals.get(username);
        if (principal != null) {
            return principal.getPassword();
        }
        return null;
    }

    @Override // org.apache.catalina.realm.RealmBase
    public Principal getPrincipal(String username) {
        return this.principals.get(username);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Not initialized variable reg: 13, insn: 0x0074: MOVE  (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = 
      (r13 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY] A[D('digester' org.apache.tomcat.util.digester.Digester)])
    , block:B:85:0x0074 */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void startInternal() throws LifecycleException {
        Digester digester2;
        String pathName = getPathname();
        try {
            InputStream is = ConfigFileLoader.getInputStream(pathName);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("memoryRealm.loadPath", pathName));
            }
            try {
                Digester digester3 = getDigester();
                try {
                    synchronized (digester3) {
                        digester3.push(this);
                        digester3.parse(is);
                    }
                    digester3.reset();
                    if (is != null) {
                        if (0 != 0) {
                            is.close();
                        } else {
                            is.close();
                        }
                    }
                    super.startInternal();
                } catch (Exception e) {
                    throw new LifecycleException(sm.getString("memoryRealm.readXml"), e);
                }
            } catch (Throwable th) {
                digester2.reset();
                throw th;
            }
        } catch (IOException ioe) {
            throw new LifecycleException(sm.getString("memoryRealm.loadExist", pathName), ioe);
        }
    }
}