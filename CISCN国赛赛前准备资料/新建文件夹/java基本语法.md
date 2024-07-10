Java是一种强类型的、面向对象的编程语言，广泛应用于各种开发领域。以下是一些Java的基本语句和常见用法，这些代码片段可以帮助你理解Java的基本结构和语法。

### 基本结构

#### 一个简单的Java程序
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

### 变量和数据类型

#### 变量声明和初始化
```java
int x = 10;
double y = 20.5;
String name = "CTF";
boolean isJavaFun = true;
```

### 条件语句

#### if-else语句
```java
int x = 10;
if (x > 5) {
    System.out.println("x is greater than 5");
} else if (x == 5) {
    System.out.println("x is equal to 5");
} else {
    System.out.println("x is less than 5");
}
```

#### switch语句
```java
int day = 3;
switch (day) {
    case 1:
        System.out.println("Monday");
        break;
    case 2:
        System.out.println("Tuesday");
        break;
    case 3:
        System.out.println("Wednesday");
        break;
    default:
        System.out.println("Other day");
        break;
}
```

### 循环

#### for循环
```java
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}
```

#### while循环
```java
int i = 0;
while (i < 5) {
    System.out.println(i);
    i++;
}
```

#### do-while循环
```java
int i = 0;
do {
    System.out.println(i);
    i++;
} while (i < 5);
```

### 数组

#### 声明和初始化数组
```java
int[] numbers = {1, 2, 3, 4, 5};
String[] names = {"Alice", "Bob", "Charlie"};
```

#### 访问数组元素
```java
System.out.println(numbers[0]);  // 输出：1
numbers[0] = 10;
System.out.println(numbers[0]);  // 输出：10
```

### 方法

#### 定义和调用方法
```java
public class MyClass {
    public static void main(String[] args) {
        int result = add(5, 10);
        System.out.println("Sum: " + result);
    }

    public static int add(int a, int b) {
        return a + b;
    }
}
```

### 类和对象

#### 定义类和创建对象
```java
public class Person {
    String name;
    int age;

    // 构造函数
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // 方法
    public void display() {
        System.out.println("Name: " + name + ", Age: " + age);
    }

    public static void main(String[] args) {
        Person p = new Person("Alice", 30);
        p.display();
    }
}
```

### 继承

#### 定义和使用继承
```java
class Animal {
    void eat() {
        System.out.println("This animal eats food");
    }
}

class Dog extends Animal {
    void bark() {
        System.out.println("This dog barks");
    }
}

public class Main {
    public static void main(String[] args) {
        Dog d = new Dog();
        d.eat();
        d.bark();
    }
}
```

### 接口

#### 定义和实现接口
```java
interface Animal {
    void makeSound();
}

class Dog implements Animal {
    public void makeSound() {
        System.out.println("Woof");
    }
}

public class Main {
    public static void main(String[] args) {
        Dog d = new Dog();
        d.makeSound();
    }
}
```

### 异常处理

#### try-catch块
```java
public class Main {
    public static void main(String[] args) {
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("Cannot divide by zero");
        } finally {
            System.out.println("This block always executes");
        }
    }
}
```

### 文件操作

#### 读取文件
```java
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            File file = new File("example.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }
    }
}
```

#### 写入文件
```java
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            FileWriter writer = new FileWriter("output.txt");
            writer.write("Hello, CTF!");
            writer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
```

### 综合示例

#### 简单的网络请求
使用`java.net`包发送简单的HTTP GET请求：
```java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://example.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();

            System.out.println(content.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

这些基本语句和示例代码展示了Java的常用功能和用法。通过掌握这些基础知识，你可以编写和调试Java程序，并在CTF比赛中解决各种Java相关的挑战。