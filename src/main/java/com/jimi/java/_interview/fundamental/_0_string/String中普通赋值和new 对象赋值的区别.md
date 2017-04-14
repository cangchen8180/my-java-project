
分析如下代码，

```

        String strHellAndO = new String("hell") + new String("o");
        String strIntern = strHellAndO.intern();
        String strHello = "hello";
        String strHello1 = "hell" + new String("o");

        //////////////////// 为什么是false???????? /////////////////////////
        System.out.println("strHello == strHello1 = " + (strHello == strHello1));
        System.out.println("strHello ==strHello1 = " + strHello == strHello1 ? "" : "1");

        System.out.println("strHellAndO == strIntern = " + (strHellAndO == strIntern));

        //////////////////// 为什么是true???????? /////////////////////////
        System.out.println("strHello == strHellAndO = " + (strHello == strHellAndO));
        //////////////////// 为什么是true???????? /////////////////////////
        System.out.println("strHello== strIntern = " + (strIntern == strHello));
```

运行结果：
```
strHello == strHello1 = false
1
strHellAndO == strIntern = true
strHello == strHellAndO = true
strHello== strIntern = true
```


