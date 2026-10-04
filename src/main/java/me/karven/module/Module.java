package me.karven.module;

import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class Module {
    abstract public void enable();
    abstract public void disable();
}
