package com.ygdevs.notjustjson.util;

import java.io.IOException;

@FunctionalInterface
public interface IOFunction<F, T> {
    T apply(F f) throws IOException;
}