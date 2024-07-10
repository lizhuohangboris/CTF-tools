package org.apache.catalina.users;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/users/MemoryUserDatabase.class */
public class MemoryUserDatabase implements UserDatabase {
    protected final HashMap<String, Group> groups;
    protected final String id;
    protected String pathname;
    protected String pathnameOld;
    protected String pathnameNew;
    protected boolean readonly;
    protected final HashMap<String, Role> roles;
    protected final HashMap<String, User> users;
    private static final Log log = LogFactory.getLog(MemoryUserDatabase.class);
    private static final StringManager sm = StringManager.getManager(Constants.Package);

    public MemoryUserDatabase() {
        this(null);
    }

    public MemoryUserDatabase(String id) {
        this.groups = new HashMap<>();
        this.pathname = "conf/tomcat-users.xml";
        this.pathnameOld = this.pathname + ".old";
        this.pathnameNew = this.pathname + ".new";
        this.readonly = true;
        this.roles = new HashMap<>();
        this.users = new HashMap<>();
        this.id = id;
    }

    @Override // org.apache.catalina.UserDatabase
    public Iterator<Group> getGroups() {
        Iterator<Group> it;
        synchronized (this.groups) {
            it = this.groups.values().iterator();
        }
        return it;
    }

    @Override // org.apache.catalina.UserDatabase
    public String getId() {
        return this.id;
    }

    public String getPathname() {
        return this.pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
        this.pathnameOld = pathname + ".old";
        this.pathnameNew = pathname + ".new";
    }

    public boolean getReadonly() {
        return this.readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    @Override // org.apache.catalina.UserDatabase
    public Iterator<Role> getRoles() {
        Iterator<Role> it;
        synchronized (this.roles) {
            it = this.roles.values().iterator();
        }
        return it;
    }

    @Override // org.apache.catalina.UserDatabase
    public Iterator<User> getUsers() {
        Iterator<User> it;
        synchronized (this.users) {
            it = this.users.values().iterator();
        }
        return it;
    }

    @Override // org.apache.catalina.UserDatabase
    public void close() throws Exception {
        save();
        synchronized (this.groups) {
            synchronized (this.users) {
                this.users.clear();
                this.groups.clear();
            }
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public Group createGroup(String groupname, String description) {
        if (groupname == null || groupname.length() == 0) {
            String msg = sm.getString("memoryUserDatabase.nullGroup");
            log.warn(msg);
            throw new IllegalArgumentException(msg);
        }
        MemoryGroup group = new MemoryGroup(this, groupname, description);
        synchronized (this.groups) {
            this.groups.put(group.getGroupname(), group);
        }
        return group;
    }

    @Override // org.apache.catalina.UserDatabase
    public Role createRole(String rolename, String description) {
        if (rolename == null || rolename.length() == 0) {
            String msg = sm.getString("memoryUserDatabase.nullRole");
            log.warn(msg);
            throw new IllegalArgumentException(msg);
        }
        MemoryRole role = new MemoryRole(this, rolename, description);
        synchronized (this.roles) {
            this.roles.put(role.getRolename(), role);
        }
        return role;
    }

    @Override // org.apache.catalina.UserDatabase
    public User createUser(String username, String password, String fullName) {
        if (username == null || username.length() == 0) {
            String msg = sm.getString("memoryUserDatabase.nullUser");
            log.warn(msg);
            throw new IllegalArgumentException(msg);
        }
        MemoryUser user = new MemoryUser(this, username, password, fullName);
        synchronized (this.users) {
            this.users.put(user.getUsername(), user);
        }
        return user;
    }

    @Override // org.apache.catalina.UserDatabase
    public Group findGroup(String groupname) {
        Group group;
        synchronized (this.groups) {
            group = this.groups.get(groupname);
        }
        return group;
    }

    @Override // org.apache.catalina.UserDatabase
    public Role findRole(String rolename) {
        Role role;
        synchronized (this.roles) {
            role = this.roles.get(rolename);
        }
        return role;
    }

    @Override // org.apache.catalina.UserDatabase
    public User findUser(String username) {
        User user;
        synchronized (this.users) {
            user = this.users.get(username);
        }
        return user;
    }

    @Override // org.apache.catalina.UserDatabase
    public void open() throws Exception {
        synchronized (this.groups) {
            synchronized (this.users) {
                this.users.clear();
                this.groups.clear();
                this.roles.clear();
                String pathName = getPathname();
                try {
                    InputStream is = ConfigFileLoader.getInputStream(getPathname());
                    Throwable th = null;
                    try {
                        Digester digester = new Digester();
                        try {
                            digester.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
                        } catch (Exception e) {
                            log.warn(sm.getString("memoryUserDatabase.xmlFeatureEncoding"), e);
                        }
                        digester.addFactoryCreate("tomcat-users/group", new MemoryGroupCreationFactory(this), true);
                        digester.addFactoryCreate("tomcat-users/role", new MemoryRoleCreationFactory(this), true);
                        digester.addFactoryCreate("tomcat-users/user", new MemoryUserCreationFactory(this), true);
                        digester.parse(is);
                        if (is != null) {
                            if (0 != 0) {
                                try {
                                    is.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                is.close();
                            }
                        }
                    } finally {
                    }
                } catch (IOException e2) {
                    log.error(sm.getString("memoryUserDatabase.fileNotFound", pathName));
                }
            }
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void removeGroup(Group group) {
        synchronized (this.groups) {
            Iterator<User> users = getUsers();
            while (users.hasNext()) {
                User user = users.next();
                user.removeGroup(group);
            }
            this.groups.remove(group.getGroupname());
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void removeRole(Role role) {
        synchronized (this.roles) {
            Iterator<Group> groups = getGroups();
            while (groups.hasNext()) {
                Group group = groups.next();
                group.removeRole(role);
            }
            Iterator<User> users = getUsers();
            while (users.hasNext()) {
                User user = users.next();
                user.removeRole(role);
            }
            this.roles.remove(role.getRolename());
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void removeUser(User user) {
        synchronized (this.users) {
            this.users.remove(user.getUsername());
        }
    }

    public boolean isWriteable() {
        File file = new File(this.pathname);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty("catalina.base"), this.pathname);
        }
        File dir = file.getParentFile();
        return dir.exists() && dir.isDirectory() && dir.canWrite();
    }

    @Override // org.apache.catalina.UserDatabase
    public void save() throws Exception {
        if (getReadonly()) {
            log.error(sm.getString("memoryUserDatabase.readOnly"));
        } else if (!isWriteable()) {
            log.warn(sm.getString("memoryUserDatabase.notPersistable"));
        } else {
            File fileNew = new File(this.pathnameNew);
            if (!fileNew.isAbsolute()) {
                fileNew = new File(System.getProperty("catalina.base"), this.pathnameNew);
            }
            try {
                FileOutputStream fos = new FileOutputStream(fileNew);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
                Throwable th = null;
                try {
                    PrintWriter writer = new PrintWriter(osw);
                    Throwable th2 = null;
                    try {
                        writer.println("<?xml version='1.0' encoding='utf-8'?>");
                        writer.println("<tomcat-users xmlns=\"http://tomcat.apache.org/xml\"");
                        writer.println("              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
                        writer.println("              xsi:schemaLocation=\"http://tomcat.apache.org/xml tomcat-users.xsd\"");
                        writer.println("              version=\"1.0\">");
                        Iterator<?> values = getRoles();
                        while (values.hasNext()) {
                            writer.print("  ");
                            writer.println(values.next());
                        }
                        Iterator<?> values2 = getGroups();
                        while (values2.hasNext()) {
                            writer.print("  ");
                            writer.println(values2.next());
                        }
                        Iterator<?> values3 = getUsers();
                        while (values3.hasNext()) {
                            writer.print("  ");
                            writer.println(((MemoryUser) values3.next()).toXml());
                        }
                        writer.println("</tomcat-users>");
                        if (writer.checkError()) {
                            throw new IOException(sm.getString("memoryUserDatabase.writeException", fileNew.getAbsolutePath()));
                        }
                        if (writer != null) {
                            if (0 != 0) {
                                try {
                                    writer.close();
                                } catch (Throwable th3) {
                                    th2.addSuppressed(th3);
                                }
                            } else {
                                writer.close();
                            }
                        }
                        if (osw != null) {
                            if (0 != 0) {
                                try {
                                    osw.close();
                                } catch (Throwable th4) {
                                    th.addSuppressed(th4);
                                }
                            } else {
                                osw.close();
                            }
                        }
                        if (fos != null) {
                            if (0 != 0) {
                                fos.close();
                            } else {
                                fos.close();
                            }
                        }
                        File fileOld = new File(this.pathnameOld);
                        if (!fileOld.isAbsolute()) {
                            fileOld = new File(System.getProperty("catalina.base"), this.pathnameOld);
                        }
                        if (fileOld.exists() && !fileOld.delete()) {
                            throw new IOException(sm.getString("memoryUserDatabase.fileDelete", fileOld));
                        }
                        File fileOrig = new File(this.pathname);
                        if (!fileOrig.isAbsolute()) {
                            fileOrig = new File(System.getProperty("catalina.base"), this.pathname);
                        }
                        if (fileOrig.exists() && !fileOrig.renameTo(fileOld)) {
                            throw new IOException(sm.getString("memoryUserDatabase.renameOld", fileOld.getAbsolutePath()));
                        }
                        if (fileNew.renameTo(fileOrig)) {
                            if (fileOld.exists() && !fileOld.delete()) {
                                throw new IOException(sm.getString("memoryUserDatabase.fileDelete", fileOld));
                            }
                            return;
                        }
                        if (fileOld.exists() && !fileOld.renameTo(fileOrig)) {
                            log.warn(sm.getString("memoryUserDatabase.restoreOrig", fileOld));
                        }
                        throw new IOException(sm.getString("memoryUserDatabase.renameNew", fileOrig.getAbsolutePath()));
                    } catch (Throwable th5) {
                        try {
                            throw th5;
                        } catch (Throwable th6) {
                            if (writer != null) {
                                if (th5 != null) {
                                    try {
                                        writer.close();
                                    } catch (Throwable th7) {
                                        th5.addSuppressed(th7);
                                    }
                                } else {
                                    writer.close();
                                }
                            }
                            throw th6;
                        }
                    }
                } catch (Throwable th8) {
                    try {
                        throw th8;
                    } catch (Throwable th9) {
                        if (osw != null) {
                            if (th8 != null) {
                                try {
                                    osw.close();
                                } catch (Throwable th10) {
                                    th8.addSuppressed(th10);
                                }
                            } else {
                                osw.close();
                            }
                        }
                        throw th9;
                    }
                }
            } catch (IOException e) {
                if (fileNew.exists() && !fileNew.delete()) {
                    log.warn(sm.getString("memoryUserDatabase.fileDelete", fileNew));
                }
                throw e;
            }
        }
    }

    public String toString() {
        return "MemoryUserDatabase[id=" + this.id + ",pathname=" + this.pathname + ",groupCount=" + this.groups.size() + ",roleCount=" + this.roles.size() + ",userCount=" + this.users.size() + "]";
    }
}