package com.tools.wx;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void asf() {
        String filePath="afsasfaf.apk.1";
        File file=new File(filePath);
        System.out.println(file.getName());
        System.out.println("=============");
        int index = filePath.lastIndexOf(".1");
        String substring = filePath.substring(0, index);
        System.out.println(filePath);
        System.out.println(substring);
    }

    @Test
    public void sdf() {
        int a=9;
    }
}