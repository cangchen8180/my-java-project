### 关于++操作和线程安全的问题
测试代码
```java
public class IntegerMultiThreadSafeTest implements Runnable {

    private Object o1 = new Object();

    private static Object o = new Object();
    private static Integer sum = 0;
    private static CountDownLatch latch = new CountDownLatch(2);

    @Override
    public void run() {
        for (int j = 0; j < 2000000; j++) {
            /*
            情况一、Short型、Integer型、Long型等
            结果：sum < 4000000
            结论：同步块会失效
            原因：因为计算后，拆箱和封箱后，对象就变了
             */
            /*synchronized (sum) {
                sum++;  //该步实际操作：sum = new Integer(sum+1);
            }*/

            /*
            情况二、this也只对当前对象有效
            结果：sum < 4000000
            结论：同步块会失效
             */
            synchronized (this) {
                sum++;
            }

            /*
            情况三、成员变量和this一样，跟着对象走，不同的实例，则o1不同，
            结果：sum < 4000000
            结论：同步块会失效
             */
            /*synchronized (o1) {
                sum++;
            }*/

            /*
            情况四：手动改变成员变量
            结果：sum < 4000000 （但最接近4000000）
            结论：同步块会失效
             */
            /*synchronized (o) {
                sum++;

                Object temp = o;
                //为什么要使用20次循环，因为单次太快，达不到同步块失效的效果。
                for (int i = 0; i < 20; i++) {
                    o = new Object();
                }
                o = temp;
            }*/
            
            /*
            情况五、静态成员对象
            结果：sum = 4000000
            结论：同步块会有效
             */
            /*synchronized (o) {
                sum++;
            }*/
            
            /*
            情况六、类
            结果：sum = 4000000
            结论：同步块会有效
             */
            synchronized (IntegerMultiThreadSafeTest.class) {
                sum++;
            }

        }
        latch.countDown();
    }


    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        ExecutorService executorService1 = Executors.newFixedThreadPool(1);

        executorService.execute(new IntegerMultiThreadSafeTest());
        executorService1.execute(new IntegerMultiThreadSafeTest());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("sum = " + sum);

        executorService.shutdown();
        executorService1.shutdown();
    }
}
```

