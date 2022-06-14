package com.ricky9090.smallworld.obj;

import com.ricky9090.smallworld.SmallInterpreter;

public class SmallInt extends SmallObject {

    public int value;

    public SmallInt(SmallObject IntegerClass, int v) {
        super(IntegerClass, 0);
        value = v;
    }

    public SmallInt() {
        super();
    }

    public static SmallInt create(int val) {
        if ((val >= 0) && (val < 10)) {
            return SmallInterpreter.smallIntCache[val];
        } else {
            return new SmallInt(SmallInterpreter.IntegerClass, val);
        }
    }

    public String toString() {
        return "SmallInt: " + value;
    }

    public Integer toInteger() {
        return Integer.valueOf(value);
    }
}