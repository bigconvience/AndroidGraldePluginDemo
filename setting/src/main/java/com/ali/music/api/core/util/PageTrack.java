package com.ali.music.api.core.util;

/**
 * Created by jiangbenpeng on 15/06/2017.
 *
 * @author benpeng.jiang
 * @version 1.0.0
 */
public class PageTrack {
    private static PageTrack sInstance = new PageTrack();
    public static PageTrack getInstance() {
        return sInstance;
    }

    public void track(String name, long start, long end) {
        System.out.println(name + ", start:" + start + ", end:" + end);
    }
}
