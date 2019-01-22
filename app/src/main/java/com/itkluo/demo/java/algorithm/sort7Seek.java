package com.itkluo.demo.java.algorithm;

import java.util.Arrays;

/**
 * 排序和查找算法
 * Created by luobingyong on 2018/12/14.
 */
public class sort7Seek {

    /**
     * 冒泡排序
     * 从大到小
     */
    public static void bubbleSort() {
        int[] array = {32, 21, 34, 6, 8, 11, 88, 3, 10};
        System.out.println("冒泡");
        System.out.println("原数组：" + Arrays.toString(array));
        //外层循环控制轮数, 总共轮数array.length - 1
        for (int i = 0; i < array.length - 1; i++) {
            //内层循环， 每轮确定一个最小值放到最后， 每一轮比较的次数array.length - 1， 每轮从0数据开始和后面数据比较
            for (int j = 0; j < array.length - 1 - i; j++) {
                //j j+1  小值往后放
                if (array[j] < array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        System.out.println("原数组：" + Arrays.toString(array));
        System.out.println("--------");
    }


    /**
     * 选择排序
     * 从大到小
     */
    public static void selectSort() {
        int[] array = {32, 21, 34, 6, 8, 11, 88, 3, 10};
        System.out.println("选择");
        System.out.println("原数组：" + Arrays.toString(array));
        //外层循环控制轮数, 总共轮数array.length - 1
        for (int i = 0; i < array.length - 1; i++) {
            //内层循环， 始终当前下标的数和后面比较， 选出大值放前面
            for (int j = i + 1; j < array.length; j++) {
                //i  i+1  len-1
                if (array[i] < array[j]) {
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }
        System.out.println("原数组：" + Arrays.toString(array));
        System.out.println("--------");
    }

    /**
     * 对从小到的有序数组做二分查找
     */
    public static void halfSeek() {
        int[] array = {3, 6, 8, 10, 11, 21, 32, 34, 88};
        int key = 34;
        int min = 0;
        int max = array.length - 1;
        int mid = (array.length - 1) / 2;
        System.out.println("二分查找");
        System.out.println("原数组：" + Arrays.toString(array));
        while (key != array[mid]) {
            if (key > array[mid]) {//在右边
                min = mid + 1;
                mid = (max + min) / 2;
            } else if (key < array[mid]) {//在左边
                max = mid - 1;
                mid = (max + min) / 2;
            }
        }
        System.out.printf("要找的key=%d在位置%d\n", key, mid);


    }

    public static void bubbleSort(int[] arr) {
        int temp;
        boolean flag;//是否交换的标志
        for (int i = 0; i < arr.length - 1; i++) {//表示趟数， 一共arr.length - 1趟
            flag = false;
            //从底部开始比较， 每次得到最小值往上冒
            for (int j = arr.length - 1; j > i; j--) {
                if (arr[j] < arr[j - 1]) {
                    temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = temp;
                    flag = true;
                }
            }
            if (!flag) {
                break;
            }

        }

    }

    public static void main(String[] args) throws Exception {
//        bubbleSort();
//        selectSort();
        int[] array = {32, 21, 34, 6, 8, 11, 88, 3, 10};
        bubbleSort(array);
        System.out.println("冒泡排后：" + Arrays.toString(array));
    }


}