package com.conta.comer.util;

import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mahyar
 * Date: 2/26/15
 * Time: 05:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Empty
{
    public static boolean isEmpty(Object o)
    {
        return o == null;
    }

    public static boolean isNotEmpty(Object o)
    {
        return o != null;
    }

    public static boolean isEmpty(String s)
    {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(String s)
    {
        return !isEmpty(s);
    }

    public static boolean isEmpty(Collection c)
    {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection c)
    {
        return !isEmpty(c);
    }

    public static boolean isEmpty(Map m)
    {
        return m == null || m.isEmpty();
    }

    public static boolean isNotEmpty(Map m)
    {
        return !isEmpty(m);
    }

    public static boolean isEmpty(Object[] m)
    {
        return m == null || m.length == 0;
    }

    public static boolean isNotEmpty(Object[] m)
    {
        return !isEmpty(m);
    }

    public static boolean isEmptyCustom(Object o, Object... emptyValue)
    {
        if (isEmpty(o))
        {
            return true;
        }
        for (Object ev : emptyValue)
        {
            if (o.equals(ev))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotEmptyCustom(Object o, Object... emptyValue)
    {
        return !isEmptyCustom(o, emptyValue);
    }

    public static boolean isEmptyCustom(String s, String... emptyValue)
    {
        if (isEmpty(s))
        {
            return true;
        }
        for (Object ev : emptyValue)
        {
            if (s.equals(ev))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotEmptyCustom(String s, String... emptyValue)
    {
        return !isEmptyCustom(s, emptyValue);
    }
}
