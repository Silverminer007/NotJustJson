/*
 * Silverminer007
 * Copyright (c) 2022.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.ygdevs.notjustjson.toml;

import com.ygdevs.notjustjson.util.DataElementOps;

public class TomlOps extends DataElementOps<TomlElement> {
    public static final TomlOps INSTANCE = new TomlOps();

    private TomlOps() {
    }

    @Override
    public TomlElement createList() {
        return TomlElement.list();
    }

    @Override
    public TomlElement createObject() {
        return TomlElement.object();
    }

    @Override
    public TomlElement empty() {
        return TomlElement.NULL;
    }

    @Override
    public TomlElement createNumeric(Number i) {
        return TomlElement.number(i);
    }

    @Override
    public TomlElement createBoolean(boolean bool) {
        return TomlElement.bool(bool);
    }
    @Override
    public TomlElement createString(String value) {
        return TomlElement.string(value);
    }
}