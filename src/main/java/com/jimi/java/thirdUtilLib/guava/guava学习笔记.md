## 学习Guava

### 工具类的使用

```java
        //--------------- 前置条件-使用和避免null ------------
        //赋值
//        Optional<StudentTest> student  = Optional.of(new StudentTest());
        /*Optional<StudentTest> student  = Optional.fromNullable(null);
        //判断对象是否是有效对象
        if (student.isPresent()) {
            //如果为空，则报IllegalStateException异常
            StudentTest studentTest = student.get();
            //如果为空，返回参数对象
            StudentTest or = student.or(new StudentTest());
        }*/
        /*
        注：Optional<T>的最常用价值在于，
        例如，假设一个方法返回某一个数据类型，调用这个方法的代码来根据这个方法的返回值来做下一步的动作，
        若该方法可以返回一个null值表示成功，或者表示失败，在这里看来都是意义含糊的，所以使用Optional<T>作为返回值，
        则后续代码可以通过isPresent()来判断是否返回了期望的值（原本期望返回null或者返回不为null，其意义不清晰），
        并且可以使用get()来获得实际的返回值。
         */

        //--------------- 前置条件-简化方法参数判断 ------------
        /*List<Integer> listForTest = new ArrayList<>(20);
        //用于判断某个参数是否合法
        int id = 0;
        Preconditions.checkArgument(id > 0, "%s 必须大于 0", id);
        //用于判断某个对象的某种状态
        int p1 = 3;
        Preconditions.checkState(listForTest.contains(p1), "%s 不包含 %s", "listForTest", p1);*/


        //--------------- Object常用方法封装 ------------
        String a = "a";
        String a2 = "a";
        System.out.println("(a == \"a\") = " + (a == "a"));
        System.out.println("(\"a\" == \"a\") = " + ("a" == "a"));
        System.out.println("(a == a2) = " + (a == a2));
        System.out.println("(null == null) = " + (null == null));
        System.out.println("Objects.equal(null, \"a\") = " + Objects.equal(null, "a"));


        //--------------- 排序工具 ------------
        Ordering<Integer> orderUtil = new Ordering<Integer>() {
            @Override
            public int compare(Integer left, Integer right) {
                Preconditions.checkNotNull(left, "left 不能为空");
                Preconditions.checkNotNull(right, "right 不能为空");
                return left.compareTo(right);
            }
        };
        List<Integer> intList = new ArrayList<>(10);
        intList.add(2);
        intList.add(20);
        intList.add(3);
        intList.add(8);
        List<Integer> topK = orderUtil.greatestOf(intList, 3);
        System.out.println("topK = " + topK);
        /*
        Q：Ordering的greatestOf()方法怎么找出最大/小的k个元素？
        答：用到一个特殊算法。但如果要找的元素个数超过总数一半，则不用算法，而是直接排序截取，这样更快。算法适用于k远小n的情况。
        算法流程：
            保持一个2k大小的buffer；每次满了时，清掉较大的一半，剩下k位。
            *剪枝优化：维护一个第k小的阈值，大于它的可以直接忽略了
            *清掉一半的方法：快速选择。
                定一个标志位，比它小的挪到左边，比它大的挪到右边
        复杂度：
            时间O(n + k log k) 存储O(k)
         */

        //--------------- 异常捕获 ------------
        /*try {
            int i = 2 / 1;
            throw new RuntimeException("runtime exception");
        } catch (Exception e) {
            e.printStackTrace();

            //guava三个好用的工具方法
            *//*String stackTraceAsString = Throwables.getStackTraceAsString(e);
            System.out.println("stackTraceAsString = " + stackTraceAsString);

            List<Throwable> causalChain = Throwables.getCausalChain(e);

            Throwable rootCause = Throwables.getRootCause(e);*//*
        }
        System.out.println("i end = " + "i end");*/

        //--------------- 数组转list注意问题 ------------
        Integer[] ints = new Integer[]{1, 2, 4};
//        int[] ints = new int[5];
        ints[0] = 2;
        ints[2] = 3;
        /*
        asList使用注意：
        1、避免使用基本数据类型数组转换为列表。
        2、asList 产生的列表不可操作
         */
        List<Integer> integers = Arrays.asList(ints);
//        integers.add(5);

        //--------------- String工具类 ------------
        //guava连接器
        System.out.println("ints = " + Joiner.on(", ").join(integers));

        //guava分割器
        Iterable<String> split = Splitter.on(",").trimResults().omitEmptyStrings().split("3, 4, , 66,");

        //字符串处理，只保留数字
        String sequence = "23你00001好4";
        // --- guava 提供 ---
        String digit = CharMatcher.javaDigit().retainFrom(sequence);
        System.out.println("digit = " + digit);
        // --- 自己实现 ---
        UseGuava useGuava = new UseGuava();
        String digit1 = useGuava.getDigit(sequence);
        System.out.println("digit1 = " + digit1);

        //用*号替换所有数字
        String s = CharMatcher.javaDigit().replaceFrom(sequence, "*");
        System.out.println("s = " + s);
```