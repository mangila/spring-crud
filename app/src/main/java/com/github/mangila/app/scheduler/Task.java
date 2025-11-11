package com.github.mangila.app.scheduler;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.concurrent.Callable;

public interface Task extends Callable<ObjectNode> {
    String name();
}
