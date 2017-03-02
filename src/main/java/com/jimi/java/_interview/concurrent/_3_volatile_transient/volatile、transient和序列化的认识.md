## volatile、transient和序列化的认识

### volatile

#### 简介
对于volatile修饰的变量，线程不能保存它的私有拷贝，都强迫从主内存读取该变量值，从而保证在任何时刻，两个不同的线程总是看到某个成员变量的同一个值。

也就是说，volatile能够让多线程的每次操作，把值从主存中取出load到本地栈，完成操作后再Save回去，volatile对每次操作都激发一次Load and Save，因此原子性不能保证。

#### 使用建议
- 在两个或者更多的线程访问的成员变量上使用volatile。当要访问的变量已在synchronized代码块中，或者为常量时，不必使用。但是由于使用volatile屏蔽掉了VM中必要的代码优化，所以在效率上比较低，因此一定在必要时才使用此关键字。
- 对多线程操作volatile变量时，对Integer、Short和Long型变量，synchronized锁是不起作用的。

    线程不安全代码
    
```java
Integer i = 0;
......
　　synchronized(i) {
　　　　i++;
　　}
......
```
    
**原因：** i++的操作是i=new Integer(i+1)，有个自动拆箱和封箱的过程，这样 i就不是原来对象，同步锁也就失效了。

**解决：** 直接锁当前类对象。

具体参考[volatile自增和synchronized使用中线程安全的问题.md](com/jimi/java/_interview/concurrent/_1_synchronized_reentrantLock/volatile自增和synchronized使用中线程安全的问题.md)

### transient

#### 简介
Java的serialization提供了一种持久化对象实例的机制。当持久化对象时，可能有一个特殊的对象数据成员，我们不想用serialization机制来保存它。为了在一个特定对象的一个域上关闭serialization，可以在这个域前加上关键字transient。

transient是Java语言的关键字，用来表示一个域不是该对象串行化的一部分。当一个对象被串行化的时候，transient型变量的值不包括在串行化的表示中，然而非transient型的变量是被包括进去的。

#### 实例

```java
public class TransientTest {
    public static final Gson GSON = new GsonBuilder().create();

    public static void main(String[] args) {
        UserEntity userEntity = new UserEntity("小李", "p_123");
        System.out.println("userEntity = " + GSON.toJson(userEntity));

        /*
        粗心对待transient域可能引起的潜在问题：在构造方法设置了默认值，但无法序列化，因为使用了transient。
         */
        UserEntity userEntity1 = new UserEntity();
        String toJson = GSON.toJson(userEntity1);
        System.out.println("userEntity1 = " + toJson);

        UserEntity.id = 20;

        UserEntity userEntity2 = GSON.fromJson(toJson, UserEntity.class);
        System.out.println("userEntity2.id = " + userEntity2.id);
    }

}

class UserEntity implements Serializable{

    /*
     静态变量也不参与序列化
     此处需要注意：
        如果反序列化之前修改静态变量的值，原来序号化的对象被反序列化后，其中的静态变量为修改后的值，而不是序列化时的值。
     */
    static int id = 10;
    private String name;
    /*
    不让该变量参与序列化
     */
    private transient String pass;

    public UserEntity() {
        this.name = "default_name_小李";
        this.pass = "default_pass_123";
    }

    public UserEntity(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}
```

执行结果
```java
userEntity = {"name":"小李"}
userEntity1 = {"name":"default_name_小李"}
userEntity2.id = 20
```

## 附录
### 序列化相关知识
- 静态变量不会被序列化
    代码
    
```java
UserEntity userEntity1 = new UserEntity();
String toJson = GSON.toJson(userEntity1);
System.out.println("userEntity1 = " + toJson);

UserEntity.id = 20;

UserEntity userEntity2 = GSON.fromJson(toJson, UserEntity.class);
System.out.println("userEntity2.id = " + userEntity2.id);
```

结果
    
```java
userEntity1 = {"name":"default_name_小李"}
userEntity2.id = 20
```

- transient修饰变量不参与序列化
    实体代码
    
```java
private transient String pass;
```
    
序列化代码
    
```java
UserEntity userEntity = new UserEntity("小李", "p_123");
System.out.println("userEntity = " + GSON.toJson(userEntity));
```
    
结果
    
```java
userEntity = {"name":"小李"}
```

- 序列化和反序列时，自动对敏感字段加解密

    **情境：** 服务器端给客户端发送序列化对象数据，对象中有一些数据是敏感的，比如密码字符串等，希望对该密码字段在序列化时，进行加密，而客户端如果拥有解密的密钥，只有在客户端进行反序列化时，才可以对密码进行读取，这样可以一定程度保证序列化对象的数据安全。

    **解决：** 在序列化过程中，虚拟机会试图调用对象类里的 writeObject 和 readObject 方法，进行用户自定义的序列化和反序列化，如果没有这样的方法，则默认调用是 ObjectOutputStream 的 defaultWriteObject 方法以及 ObjectInputStream 的 defaultReadObject 方法。用户自定义的 writeObject 和 readObject 方法可以允许用户控制序列化的过程，比如可以在序列化的过程中动态改变序列化的数值。基于这个原理，可以在实际应用中得到使用，用于敏感字段的加密工作，清单 3 展示了这个过程。

    清单 3. 静态变量序列化问题代码
 
```java
private static final long serialVersionUID = 1L;

private String password = "pass";

public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;
}

private void writeObject(ObjectOutputStream out) {
    try {
        PutField putFields = out.putFields();
        System.out.println("原密码:" + password);
        password = "encryption";//模拟加密
        putFields.put("password", password);
        System.out.println("加密后的密码" + password);
        out.writeFields();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private void readObject(ObjectInputStream in) {
    try {
        GetField readFields = in.readFields();
        Object object = readFields.get("password", "");
        System.out.println("要解密的字符串:" + object.toString());
        password = "pass";//模拟解密,需要获得本地的密钥
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }

}

public static void main(String[] args) {
    try {
        ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("result.obj"));
        out.writeObject(new Test());
        out.close();

        ObjectInputStream oin = new ObjectInputStream(new FileInputStream(
                "result.obj"));
        Test t = (Test) oin.readObject();
        System.out.println("解密后的字符串:" + t.getPassword());
        oin.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
}
```
	
在清单 3 的 writeObject 方法中，对密码进行了加密，在 readObject 中则对 password 进行解密，只有拥有密钥的客户端，才可以正确的解析出密码，确保了数据的安全。执行清单 3 后控制台输出如图 3 所示。