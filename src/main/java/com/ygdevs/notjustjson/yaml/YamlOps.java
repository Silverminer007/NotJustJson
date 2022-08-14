/*
 * Silverminer007
 * Copyright (c) 2022.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.ygdevs.notjustjson.yaml;

import com.ygdevs.notjustjson.util.DataElementOps;

public class YamlOps extends DataElementOps<YamlElement> {
    public static final YamlOps INSTANCE = new YamlOps();
    private YamlOps() {
    }

    @Override
    public YamlElement createList() {
        return YamlElement.list();
    }

    @Override
    public YamlElement createObject() {
        return YamlElement.object();
    }

    @Override
    public YamlElement empty() {
        return YamlElement.NULL;
    }

    @Override
    public YamlElement createNumeric(Number i) {
        return YamlElement.number(i);
    }

    @Override
    public YamlElement createBoolean(boolean bool) {
        return YamlElement.bool(bool);
    }

    @Override
    public YamlElement createString(String value) {
        return YamlElement.string(value);
    }
}
