package org.apache.catalina.realm;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/CombinedRealm.class */
public class CombinedRealm extends RealmBase {
    private static final Log log = LogFactory.getLog(CombinedRealm.class);
    protected final List<Realm> realms = new LinkedList();

    public void addRealm(Realm theRealm) {
        this.realms.add(theRealm);
        if (log.isDebugEnabled()) {
            sm.getString("combinedRealm.addRealm", theRealm.getClass().getName(), Integer.toString(this.realms.size()));
        }
    }

    public ObjectName[] getRealms() {
        ObjectName[] result = new ObjectName[this.realms.size()];
        for (Realm realm : this.realms) {
            if (realm instanceof RealmBase) {
                result[this.realms.indexOf(realm)] = ((RealmBase) realm).getObjectName();
            }
        }
        return result;
    }

    public Realm[] getNestedRealms() {
        return (Realm[]) this.realms.toArray(new Realm[0]);
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(String username, String clientDigest, String nonce, String nc, String cnonce, String qop, String realmName, String md5a2) {
        Principal authenticatedUser = null;
        Iterator<Realm> it = this.realms.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Realm realm = it.next();
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("combinedRealm.authStart", username, realm.getClass().getName()));
            }
            authenticatedUser = realm.authenticate(username, clientDigest, nonce, nc, cnonce, qop, realmName, md5a2);
            if (authenticatedUser == null) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("combinedRealm.authFail", username, realm.getClass().getName()));
                }
            } else if (log.isDebugEnabled()) {
                log.debug(sm.getString("combinedRealm.authSuccess", username, realm.getClass().getName()));
            }
        }
        return authenticatedUser;
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(String username) {
        Principal authenticatedUser = null;
        Iterator<Realm> it = this.realms.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Realm realm = it.next();
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("combinedRealm.authStart", username, realm.getClass().getName()));
            }
            authenticatedUser = realm.authenticate(username);
            if (authenticatedUser == null) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("combinedRealm.authFail", username, realm.getClass().getName()));
                }
            } else if (log.isDebugEnabled()) {
                log.debug(sm.getString("combinedRealm.authSuccess", username, realm.getClass().getName()));
            }
        }
        return authenticatedUser;
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(String username, String credentials) {
        Principal authenticatedUser = null;
        Iterator<Realm> it = this.realms.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Realm realm = it.next();
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("combinedRealm.authStart", username, realm.getClass().getName()));
            }
            authenticatedUser = realm.authenticate(username, credentials);
            if (authenticatedUser == null) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("combinedRealm.authFail", username, realm.getClass().getName()));
                }
            } else if (log.isDebugEnabled()) {
                log.debug(sm.getString("combinedRealm.authSuccess", username, realm.getClass().getName()));
            }
        }
        return authenticatedUser;
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Contained
    public void setContainer(Container container) {
        for (Realm realm : this.realms) {
            if (realm instanceof RealmBase) {
                ((RealmBase) realm).setRealmPath(getRealmPath() + "/realm" + this.realms.indexOf(realm));
            }
            realm.setContainer(container);
        }
        super.setContainer(container);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void startInternal() throws LifecycleException {
        Iterator<Realm> iter = this.realms.iterator();
        while (iter.hasNext()) {
            Realm realm = iter.next();
            if (realm instanceof Lifecycle) {
                try {
                    ((Lifecycle) realm).start();
                } catch (LifecycleException e) {
                    iter.remove();
                    log.error(sm.getString("combinedRealm.realmStartFail", realm.getClass().getName()), e);
                }
            }
        }
        super.startInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void stopInternal() throws LifecycleException {
        super.stopInternal();
        for (Realm realm : this.realms) {
            if (realm instanceof Lifecycle) {
                ((Lifecycle) realm).stop();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    public void destroyInternal() throws LifecycleException {
        for (Realm realm : this.realms) {
            if (realm instanceof Lifecycle) {
                ((Lifecycle) realm).destroy();
            }
        }
        super.destroyInternal();
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public void backgroundProcess() {
        super.backgroundProcess();
        for (Realm r : this.realms) {
            r.backgroundProcess();
        }
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(X509Certificate[] certs) {
        Principal authenticatedUser = null;
        String username = null;
        if (certs != null && certs.length > 0) {
            username = certs[0].getSubjectDN().getName();
        }
        Iterator<Realm> it = this.realms.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Realm realm = it.next();
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("combinedRealm.authStart", username, realm.getClass().getName()));
            }
            authenticatedUser = realm.authenticate(certs);
            if (authenticatedUser == null) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("combinedRealm.authFail", username, realm.getClass().getName()));
                }
            } else if (log.isDebugEnabled()) {
                log.debug(sm.getString("combinedRealm.authSuccess", username, realm.getClass().getName()));
            }
        }
        return authenticatedUser;
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(GSSContext gssContext, boolean storeCreds) {
        if (gssContext.isEstablished()) {
            Principal authenticatedUser = null;
            try {
                GSSName name = gssContext.getSrcName();
                String username = name.toString();
                Iterator<Realm> it = this.realms.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Realm realm = it.next();
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("combinedRealm.authStart", username, realm.getClass().getName()));
                    }
                    authenticatedUser = realm.authenticate(gssContext, storeCreds);
                    if (authenticatedUser == null) {
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("combinedRealm.authFail", username, realm.getClass().getName()));
                        }
                    } else if (log.isDebugEnabled()) {
                        log.debug(sm.getString("combinedRealm.authSuccess", username, realm.getClass().getName()));
                    }
                }
                return authenticatedUser;
            } catch (GSSException e) {
                log.warn(sm.getString("realmBase.gssNameFail"), e);
                return null;
            }
        }
        return null;
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected String getPassword(String username) {
        UnsupportedOperationException uoe = new UnsupportedOperationException(sm.getString("combinedRealm.getPassword"));
        log.error(sm.getString("combinedRealm.unexpectedMethod"), uoe);
        throw uoe;
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected Principal getPrincipal(String username) {
        UnsupportedOperationException uoe = new UnsupportedOperationException(sm.getString("combinedRealm.getPrincipal"));
        log.error(sm.getString("combinedRealm.unexpectedMethod"), uoe);
        throw uoe;
    }

    @Override // org.apache.catalina.Realm
    public boolean isAvailable() {
        for (Realm realm : this.realms) {
            if (!realm.isAvailable()) {
                return false;
            }
        }
        return true;
    }
}