package com.zeter.meme;

import java.util.HashMap;

/**
 * A HashMap for integers, with methods to easily increment or decrement its values
 */
public class HashCounter<T> extends HashMap<T, Integer> {
    
    public HashCounter() {
        super();
    }

    public Integer increment(T key) {
        if (this.containsKey(key)) {
            return put(key, get(key) + 1);
        } else {
            return put(key, 1);
        }
    }

    public Integer increment(T key, int amount) {
        if (this.containsKey(key)) {
            return put(key, get(key) + amount);
        } else {
            return put(key, amount);
        }
    }

    public Integer decrement(T key) {
        if (this.containsKey(key)) {
            return put(key, get(key) - 1);
        } else {
            return put(key, -1);
        }
    }
}
