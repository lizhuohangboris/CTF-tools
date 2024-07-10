package javax.servlet.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.servlet.ServletInputStream;
import org.apache.el.parser.ELParserConstants;
import org.springframework.beans.factory.BeanFactory;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpUtils.class */
public class HttpUtils {
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);

    public static Hashtable<String, String[]> parseQueryString(String s) {
        String[] valArray;
        if (s == null) {
            throw new IllegalArgumentException();
        }
        Hashtable<String, String[]> ht = new Hashtable<>();
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(s, BeanFactory.FACTORY_BEAN_PREFIX);
        while (st.hasMoreTokens()) {
            String pair = st.nextToken();
            int pos = pair.indexOf(61);
            if (pos == -1) {
                throw new IllegalArgumentException();
            }
            String key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos + 1, pair.length()), sb);
            if (ht.containsKey(key)) {
                String[] oldVals = ht.get(key);
                valArray = (String[]) Arrays.copyOf(oldVals, oldVals.length + 1);
                valArray[oldVals.length] = val;
            } else {
                valArray = new String[]{val};
            }
            ht.put(key, valArray);
        }
        return ht;
    }

    public static Hashtable<String, String[]> parsePostData(int len, ServletInputStream in) {
        if (len <= 0) {
            return new Hashtable<>();
        }
        if (in == null) {
            throw new IllegalArgumentException();
        }
        byte[] postedBytes = new byte[len];
        int offset = 0;
        do {
            try {
                int inputLen = in.read(postedBytes, offset, len - offset);
                if (inputLen <= 0) {
                    String msg = lStrings.getString("err.io.short_read");
                    throw new IllegalArgumentException(msg);
                }
                offset += inputLen;
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        } while (len - offset > 0);
        try {
            String postedBody = new String(postedBytes, 0, len, "8859_1");
            return parseQueryString(postedBody);
        } catch (UnsupportedEncodingException e2) {
            throw new IllegalArgumentException(e2.getMessage(), e2);
        }
    }

    private static String parseName(String s, StringBuilder sb) {
        sb.setLength(0);
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            switch (c) {
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
                        i += 2;
                        break;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException();
                    } catch (StringIndexOutOfBoundsException e2) {
                        String rest = s.substring(i);
                        sb.append(rest);
                        if (rest.length() != 2) {
                            break;
                        } else {
                            i++;
                            break;
                        }
                    }
                case ELParserConstants.EMPTY /* 43 */:
                    sb.append(' ');
                    break;
                default:
                    sb.append(c);
                    break;
            }
            i++;
        }
        return sb.toString();
    }

    public static StringBuffer getRequestURL(HttpServletRequest req) {
        StringBuffer url = new StringBuffer();
        String scheme = req.getScheme();
        int port = req.getServerPort();
        String urlPath = req.getRequestURI();
        url.append(scheme);
        url.append("://");
        url.append(req.getServerName());
        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            url.append(':');
            url.append(req.getServerPort());
        }
        url.append(urlPath);
        return url;
    }
}