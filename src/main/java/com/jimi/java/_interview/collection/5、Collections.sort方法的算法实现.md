## Collections.sort方法的算法实现

### 使用
#### 使用

```java
List<Integer> iList = new ArrayList<>();
iList.add(3);
iList.add(4);
iList.add(1);
iList.add(2);
iList.add(5);
System.out.println("=============排序前=============");
System.out.println("iList = " + iList);

System.out.println("");
Collections.sort(iList);
System.out.println("=============排序后=============");
System.out.println("iList = " + iList);
```

#### 效果

```java
=============排序前=============
iList = [3, 4, 1, 2, 5]

=============排序后=============
iList = [1, 2, 3, 4, 5]
```

### 算法
