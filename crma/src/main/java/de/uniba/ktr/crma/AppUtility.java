package de.uniba.ktr.crma;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class AppUtility {

    private static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;

    public static String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    public static byte[] encodeUTF8(String string) {
        return string.getBytes(UTF8_CHARSET);
    }

    /**
     * Looks for a byte array (needle) in another byte array (haystack). Adapted from : https://codereview.stackexchange.com/questions/46220/find-byte-in-byte-with-java
     *
     * @param haystack
     * @param needle
     * @return true if needle in haystack
     */
    public static boolean search(byte[] haystack, byte[] needle) {
        //convert byte[] to Byte[]
        Byte[] searchedForB = new Byte[needle.length];
        for (int x = 0; x < needle.length; x++) {
            searchedForB[x] = needle[x];
        }

        int idx = -1;
        //search:
        Deque<Byte> q = new ArrayDeque<Byte>(haystack.length);
        for (int i = 0; i < haystack.length; i++) {
            if (q.size() == searchedForB.length) {
                //here I can check
                Byte[] cur = q.toArray(new Byte[]{});
                if (Arrays.equals(cur, searchedForB)) {
                    //found!
                    idx = i - searchedForB.length;
                    break;
                } else {
                    //not found
                    q.pop();
                    q.addLast(haystack[i]);
                }
            } else {
                q.addLast(haystack[i]);
            }
        }
        return idx >= 0;
    }

}
