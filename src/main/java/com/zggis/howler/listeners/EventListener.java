package com.zggis.howler.listeners;

import com.google.common.eventbus.Subscribe;

public interface EventListener {

    @Subscribe
    void stringEvent(String event);

    void test();
}
