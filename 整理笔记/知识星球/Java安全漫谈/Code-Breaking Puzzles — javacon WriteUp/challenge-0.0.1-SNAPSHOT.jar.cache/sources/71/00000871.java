package org.apache.catalina.manager.util;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.security.auth.Subject;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Session;
import org.apache.tomcat.util.ExceptionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/manager/util/SessionUtils.class */
public class SessionUtils {
    private static final String STRUTS_LOCALE_KEY = "org.apache.struts.action.LOCALE";
    private static final String SPRING_LOCALE_KEY = "org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE";
    private static final String JSTL_LOCALE_KEY = "javax.servlet.jsp.jstl.fmt.locale";
    private static final String[] LOCALE_TEST_ATTRIBUTES = {STRUTS_LOCALE_KEY, SPRING_LOCALE_KEY, JSTL_LOCALE_KEY, "Locale", "java.util.Locale"};
    private static final String[] USER_TEST_ATTRIBUTES = {"Login", "User", "userName", "UserName", "Utilisateur", "SPRING_SECURITY_LAST_USERNAME"};

    private SessionUtils() {
    }

    public static Locale guessLocaleFromSession(Session in_session) {
        return guessLocaleFromSession(in_session.getSession());
    }

    public static Locale guessLocaleFromSession(HttpSession in_session) {
        Object probableEngine;
        if (null == in_session) {
            return null;
        }
        Locale locale = null;
        int i = 0;
        while (true) {
            try {
                if (i >= LOCALE_TEST_ATTRIBUTES.length) {
                    break;
                }
                Object obj = in_session.getAttribute(LOCALE_TEST_ATTRIBUTES[i]);
                if (obj instanceof Locale) {
                    locale = (Locale) obj;
                    break;
                }
                Object obj2 = in_session.getAttribute(LOCALE_TEST_ATTRIBUTES[i].toLowerCase(Locale.ENGLISH));
                if (obj2 instanceof Locale) {
                    locale = (Locale) obj2;
                    break;
                }
                Object obj3 = in_session.getAttribute(LOCALE_TEST_ATTRIBUTES[i].toUpperCase(Locale.ENGLISH));
                if (!(obj3 instanceof Locale)) {
                    i++;
                } else {
                    locale = (Locale) obj3;
                    break;
                }
            } catch (IllegalStateException e) {
                return null;
            }
        }
        if (null != locale) {
            return locale;
        }
        List<Object> tapestryArray = new ArrayList<>();
        Enumeration<String> enumeration = in_session.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            if (name.contains("tapestry") && name.contains("engine") && null != in_session.getAttribute(name)) {
                tapestryArray.add(in_session.getAttribute(name));
            }
        }
        if (tapestryArray.size() == 1 && null != (probableEngine = tapestryArray.get(0))) {
            try {
                Method readMethod = probableEngine.getClass().getMethod("getLocale", null);
                Object possibleLocale = readMethod.invoke(probableEngine, null);
                if (possibleLocale instanceof Locale) {
                    locale = (Locale) possibleLocale;
                }
            } catch (Exception e2) {
                Throwable t = ExceptionUtils.unwrapInvocationTargetException(e2);
                ExceptionUtils.handleThrowable(t);
            }
        }
        if (null != locale) {
            return locale;
        }
        List<Object> localeArray = new ArrayList<>();
        Enumeration<String> enumeration2 = in_session.getAttributeNames();
        while (enumeration2.hasMoreElements()) {
            Object obj4 = in_session.getAttribute(enumeration2.nextElement());
            if (obj4 instanceof Locale) {
                localeArray.add(obj4);
            }
        }
        if (localeArray.size() == 1) {
            locale = (Locale) localeArray.get(0);
        }
        return locale;
    }

    public static Object guessUserFromSession(Session in_session) {
        if (null == in_session) {
            return null;
        }
        if (in_session.getPrincipal() != null) {
            return in_session.getPrincipal().getName();
        }
        HttpSession httpSession = in_session.getSession();
        if (httpSession == null) {
            return null;
        }
        Object user = null;
        int i = 0;
        while (true) {
            try {
                if (i >= USER_TEST_ATTRIBUTES.length) {
                    break;
                }
                Object obj = httpSession.getAttribute(USER_TEST_ATTRIBUTES[i]);
                if (null != obj) {
                    user = obj;
                    break;
                }
                Object obj2 = httpSession.getAttribute(USER_TEST_ATTRIBUTES[i].toLowerCase(Locale.ENGLISH));
                if (null != obj2) {
                    user = obj2;
                    break;
                }
                Object obj3 = httpSession.getAttribute(USER_TEST_ATTRIBUTES[i].toUpperCase(Locale.ENGLISH));
                if (null == obj3) {
                    i++;
                } else {
                    user = obj3;
                    break;
                }
            } catch (IllegalStateException e) {
                return null;
            }
        }
        if (null != user) {
            return user;
        }
        List<Object> principalArray = new ArrayList<>();
        Enumeration<String> enumeration = httpSession.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            Object obj4 = httpSession.getAttribute(name);
            if ((obj4 instanceof Principal) || (obj4 instanceof Subject)) {
                principalArray.add(obj4);
            }
        }
        if (principalArray.size() == 1) {
            user = principalArray.get(0);
        }
        if (null != user) {
            return user;
        }
        return user;
    }

    public static long getUsedTimeForSession(Session in_session) {
        try {
            long diffMilliSeconds = in_session.getThisAccessedTime() - in_session.getCreationTime();
            return diffMilliSeconds;
        } catch (IllegalStateException e) {
            return -1L;
        }
    }

    public static long getTTLForSession(Session in_session) {
        try {
            long diffMilliSeconds = (1000 * in_session.getMaxInactiveInterval()) - (System.currentTimeMillis() - in_session.getThisAccessedTime());
            return diffMilliSeconds;
        } catch (IllegalStateException e) {
            return -1L;
        }
    }

    public static long getInactiveTimeForSession(Session in_session) {
        try {
            long diffMilliSeconds = System.currentTimeMillis() - in_session.getThisAccessedTime();
            return diffMilliSeconds;
        } catch (IllegalStateException e) {
            return -1L;
        }
    }
}