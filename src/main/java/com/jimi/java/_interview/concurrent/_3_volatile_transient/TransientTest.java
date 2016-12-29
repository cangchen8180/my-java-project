package com.jimi.java._interview.concurrent._3_volatile_transient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

/**
 * @author jimi
 * @version 2016-12-29 11:35.
 */
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
