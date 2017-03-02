## Collections.sort方法的算法实现

### 一、使用
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

### 二、算法
对list的排序是，先转成数组，再利用Arrays类的sort()排序，最后再复制到list中。

```java
Object[] a = list.toArray();
Arrays.sort(a);
ListIterator i = list.listIterator();

for(int j = 0; j < a.length; ++j) {
    i.next();
    i.set((Comparable)a[j]);
}
```        

#### Arrays的sort()方法代码

```java
if(Arrays.LegacyMergeSort.userRequested) {
    legacyMergeSort(a);
} else {
    ComparableTimSort.sort(a);
}
```        

- legacyMergeSort()采用传统归并排序实现，与jdk1.5实现相同。 
- ComparableTimSort是改进的归并排序。
    - 对反向排好序的输入为o(n^2)的情况，做了优化。
    - 对已经正向排好序的输入做了优化，减少回溯。

#### legacyMergeSort实现

```java

private static void legacyMergeSort(Object[] a) {
    Object[] aux = (Object[])a.clone();
    mergeSort(aux, a, 0, a.length, 0);
}
...
private static void mergeSort(Object[] src, Object[] dest, int low, int high, int off) {
    int length = high - low;
    int destLow;
    int destHigh;
    if(length < 7) {
        for(destLow = low; destLow < high; ++destLow) {
            for(destHigh = destLow; destHigh > low && ((Comparable)dest[destHigh - 1]).compareTo(dest[destHigh]) > 0; --destHigh) {
                swap(dest, destHigh, destHigh - 1);
            }
        }

    } else {
        destLow = low;
        destHigh = high;
        low += off;
        high += off;
        int mid = low + high >>> 1;
        mergeSort(dest, src, low, mid, -off);
        mergeSort(dest, src, mid, high, -off);
        if(((Comparable)src[mid - 1]).compareTo(src[mid]) <= 0) {
            System.arraycopy(src, low, dest, destLow, length);
        } else {
            int i = destLow;
            int p = low;

            for(int q = mid; i < destHigh; ++i) {
                if(q < high && (p >= mid || ((Comparable)src[p]).compareTo(src[q]) > 0)) {
                    dest[i] = src[q++];
                } else {
                    dest[i] = src[p++];
                }
            }

        }
    }
}
```    

对于数组小于7的情况，直接用swap排序，可以提高程序的执行效率。

**ps：为什么是7？**

#### ComparableTimSort实现

```java
static void sort(Object[] a, int lo, int hi) {
    rangeCheck(a.length, lo, hi);
    int nRemaining = hi - lo;
    if(nRemaining >= 2) {
        if(nRemaining < 32) {
            int ts1 = countRunAndMakeAscending(a, lo, hi);
            binarySort(a, lo, hi, lo + ts1);
        } else {
            ComparableTimSort ts = new ComparableTimSort(a);
            int minRun = minRunLength(nRemaining);

            do {
                int runLen = countRunAndMakeAscending(a, lo, hi);
                if(runLen < minRun) {
                    int force = nRemaining <= minRun?nRemaining:minRun;
                    binarySort(a, lo, lo + force, lo + runLen);
                    runLen = force;
                }

                ts.pushRun(lo, runLen);
                ts.mergeCollapse();
                lo += runLen;
                nRemaining -= runLen;
            } while(nRemaining != 0);

            assert lo == hi;

            ts.mergeForceCollapse();

            assert ts.stackSize == 1;

        }
    }
}

private static void binarySort(Object[] a, int lo, int hi, int start) {
    if($assertionsDisabled || lo <= start && start <= hi) {
        if(start == lo) {
            ++start;
        }

        while(start < hi) {
            Comparable pivot = (Comparable)a[start];
            int left = lo;
            int right = start;

            assert lo <= start;

            int n;
            while(left < right) {
                n = left + right >>> 1;
                if(pivot.compareTo(a[n]) < 0) {
                    right = n;
                } else {
                    left = n + 1;
                }
            }

            assert left == right;

            n = start - left;
            switch(n) {
            case 2:
                a[left + 2] = a[left + 1];
            case 1:
                a[left + 1] = a[left];
                break;
            default:
                System.arraycopy(a, left, a, left + 1, n);
            }

            a[left] = pivot;
            ++start;
        }

    } else {
        throw new AssertionError();
    }
}

private static int countRunAndMakeAscending(Object[] a, int lo, int hi) {
    assert lo < hi;

    int runHi = lo + 1;
    if(runHi == hi) {
        return 1;
    } else {
        if(((Comparable)a[runHi++]).compareTo(a[lo]) >= 0) {
            while(runHi < hi && ((Comparable)a[runHi]).compareTo(a[runHi - 1]) >= 0) {
                ++runHi;
            }
        } else {
            while(runHi < hi && ((Comparable)a[runHi]).compareTo(a[runHi - 1]) < 0) {
                ++runHi;
            }

            reverseRange(a, lo, runHi);
        }

        return runHi - lo;
    }
}

private static void reverseRange(Object[] a, int lo, int hi) {
    --hi;

    while(lo < hi) {
        Object t = a[lo];
        a[lo++] = a[hi];
        a[hi--] = t;
    }

}
```

**ps：为什么是32？**












