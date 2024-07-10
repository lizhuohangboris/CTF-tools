## [羊城杯 2020]A Piece Of Java 22

MainController文件：

index路由是以cookie形式注册一个用户，而hello路由是通过cookie检测是否为有效用户，如果有效，则返回视图，并把相关信息info显示在页面中

```java
@Controller
public class MainController {
    public MainController() {
    }

    @GetMapping({"/index"})//index路由 get方法
    public String index(@CookieValue(value = "data",required = false) String cookieData) {
        //接受一个类型为String的参数cookieData，并且使用@CookieValue注解将名为"data"的cookie值注入到该参数中
        return cookieData != null && !cookieData.equals("") ? "redirect:/hello" : "index";
        //如果cookieData不为null且不为空字符串，则返回"redirect:/hello"，表示重定向到"/hello"路径；否则返回"index"，表示返回"index"视图。
    }

    @PostMapping({"/index"})
    //当通过POST请求访问"/index"路径时，从请求中获取用户名和密码，然后将用户名和密码信息序列化并保存到名为"data"的Cookie中，然后重定向到"/hello"路径。
    public String index(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletResponse response) {
        //声明了一个名为index的方法，它接受两个参数username和password，这两个参数由请求中的表单数据通过@RequestParam注解传递给方法
        //通过this.serialize(userinfo)方法对UserInfo对象进行序列化，得到一个字符串。
        UserInfo userinfo = new UserInfo();
        userinfo.setUsername(username);        
        userinfo.setPassword(password);
        //然后创建了一个名为"data"的Cookie对象，将序列化后的字符串作为值设置到Cookie中。
        Cookie cookie = new Cookie("data", this.serialize(userinfo));
        //cookie.setMaxAge(2592000)设置了Cookie的有效期为2592000秒（即30天）。
        cookie.setMaxAge(2592000);
        //最后，通过response.addCookie(cookie)将Cookie添加到响应中。
        response.addCookie(cookie);
        //最终，方法返回"redirect:/hello"，表示重定向到"/hello"路径。
        return "redirect:/hello";
    }

    @GetMapping({"/hello"})
    //当访问"/hello"路径时，首先检查是否存在名为"data"的cookie，如果存在，则从cookie中获取信息并展示在"hello"视图中，否则重定向到"/index"路径。
    public String hello(@CookieValue(value = "data",required = false) String cookieData, Model model) {
        //声明了一个名为hello的方法，它接受一个名为cookieData的参数，该参数由名为"data"的cookie值通过@CookieValue注解传递给方法。Model对象用于在视图中传递数据。
        if (cookieData != null && !cookieData.equals("")) {//首先检查cookieData是否为null或空字符串。如果不是，则表示有名为"data"的cookie存在
            Info info = (Info)this.deserialize(cookieData);//接着调用this.deserialize(cookieData)方法对cookieData进行反序列化，得到一个Info对象。
            if (info != null) {
                model.addAttribute("info", info.getAllInfo());
                //然后将该对象的信息添加到Model中，并返回"hello"，表示返回"hello"视图。
            }
            return "hello";
        } else {
            return "redirect:/index";//如果cookieData为null或空字符串，则表示名为"data"的cookie不存在或为空，此时重定向到"/index"路径。
        }
    }
```

InfoInvocationHandler文件：

```java
public class InfoInvocationHandler implements InvocationHandler, Serializable {
    private Info info;
    //类中有一个私有成员变量info，类型为Info，用于存储要代理的真实对象。
    public InfoInvocationHandler(Info info) {
        this.info = info;
        //构造函数接收一个Info类型的参数，并将其赋值给info成员变量。
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        //实现了InvocationHandler接口中的invoke方法。该方法在代理对象的方法被调用时执行。
        try {
            return method.getName().equals("getAllInfo") && !this.info.checkAllInfo() ? null : method.invoke(this.info, args);
            //首先判断被调用的方法是否为getAllInfo，同时检查info对象的checkAllInfo()方法的返回值。
            //如果被调用的方法是getAllInfo且checkAllInfo()返回值为false，则返回null；否则调用被代理对象info的相应方法。
        } catch (Exception var5) {
            //使用try-catch块捕获可能抛出的异常，并在捕获到异常时打印异常信息，并返回null。
            var5.printStackTrace();
            return null;
        }
    }
}
```

###### **JDBC反序列化漏洞**

[从一道题来看JDBC反序列化漏洞([羊城杯-2020\]A-Piece-Of-Java)_ctf jdbc-CSDN博客](https://blog.csdn.net/qq_42585535/article/details/130236514?ops_request_misc=%7B%22request%5Fid%22%3A%22170918790116800188564144%22%2C%22scm%22%3A%2220140713.130102334..%22%7D&request_id=170918790116800188564144&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduend~default-2-130236514-null-null.142^v99^pc_search_result_base3&utm_term=[羊城杯 2020]A Piece Of Java&spm=1018.2226.3001.4187)

JDBC和mysql之间是如何建立连接的：

```java
this.connection = DriverManager.getConnection(url);
```

在与MySQL数据库建立连接时，会交换一系列数据包。以下是主要的数据包交换过程：

> a. 客户端发送一个连接请求数据包，其中包含客户端的版本、线程ID、认证插件等信息。
> b. 服务器响应一个连接响应数据包，其中包含服务器的版本、线程ID、认证插件等信息。
> c. 客户端发送一个认证响应数据包，包含用户名、密码（加密后）和数据库名称。
> d. 服务器验证用户凭据，如果验证通过，则发送一个OK数据包，表明连接已建立；否则发送一个ERR数据包，表示认证失败。
> 自动执行的查询：在建立连接之后，JDBC驱动会自动执行一些查询，如**SHOW SESSION STATUS和SHOW COLLATION**。这些查询用于获取服务器的状态信息和字符集信息，以便在后续操作中使用。(漏洞关键点)
> 使用连接：现在可以使用Connection对象执行查询、更新等操作了。

**JDBC反序列化的漏洞:**

> 当JDBC连接到数据库时，驱动会自动执行SHOW SESSION STATUS和SHOW COLLATION查询，并对查询结果进行反序列化处理,如果我们可以控制jdbc客户端的url连接，去连接我们自己的一个恶意mysql服务(这个恶意服务只需要能回复jdbc发来的数据包即可)，当jdbc驱动自动执行一些查询(如show session status或show collation)这个服务会给jdbc发送序列化后的payload，然后jdbc本地进行反序列化处理后触发RCE
>

**JDBC_URL:**

```java
 String jdbc_url = "jdbc:mysql://localhost:3309/test?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai" +
                "&autoDeserialize=true" +
                "&queryInterceptors=com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor";
```


之所以要控制url连接，一个是要连接恶意mysql，还有一个就是要加入两个参数

```
autoDeserialize=true
queryInterceptors=com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor
```

ServerStatusDiffInterceptor拦截器的主要作用是在每个查询之前和之后记录服务器状态信息，以便计算状态差异。为了实现这个功能，拦截器会在查询执行前后分别执行SHOW SESSION STATUS查询。这使得攻击者可以利用这个拦截器将恶意代码注入到服务器状态查询的结果中，从而触发反序列化漏洞。

同时，攻击者还需要在URL中添加autoDeserialize=true参数，以告知JDBC驱动在处理查询结果时自动进行反序列化。这样，当驱动处理包含恶意Java对象的查询结果时，恶意代码就会被执行。
