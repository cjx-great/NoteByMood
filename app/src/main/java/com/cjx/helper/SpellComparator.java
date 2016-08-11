package com.cjx.helper;

import java.util.Comparator;

/**
 * Created by CJX on 2016-8-10.
 *
 * 比较拼音实现排序
 */
public class SpellComparator implements Comparator<String> {

    /**
     * @return 1：前者大    -1：前者小      0：两者相等
     * */
    @Override
    public int compare(String s, String t1) {
        //按照首字母大写排序
        int c1 = (s.charAt(0) + "").toUpperCase().hashCode();
        int c2 = (t1.charAt(0) + "").toUpperCase().hashCode();

        boolean c1Flag = (c1 < "A".hashCode() || c1 > "Z".hashCode()); // 不是字母
        boolean c2Flag = (c2 < "A".hashCode() || c2 > "Z".hashCode()); // 不是字母

        //字母排在非字母前面
        if (c1Flag && !c2Flag) {
            return 1;
        } else if (!c1Flag && c2Flag) {
            return -1;
        }

        return c1 - c2;
    }
}
