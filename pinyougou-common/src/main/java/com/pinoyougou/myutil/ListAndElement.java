package com.pinoyougou.myutil;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.List;

public class ListAndElement {

    /**
     * 算法：一个存储某个对象的集合，如果结合中对象含有属性，就更新该元素
     *
     * @param list
     * @param entity
     * @param <T>
     * @return
     */
    public <T> T isListContainElemment(List<T> list, T entity, String field) throws NoSuchFieldException {
        //通过反射获取该元素的对象
        Class<?> clazz = entity.getClass();
        //获取目标字段
        Field property = clazz.getDeclaredField(field);
        //遍历集合
        for (T t : list) {

        }
        return entity;
    }

    public static void main(String[] args) {
        System.out.println(StringUtils.isEmpty(null));
    }

}
