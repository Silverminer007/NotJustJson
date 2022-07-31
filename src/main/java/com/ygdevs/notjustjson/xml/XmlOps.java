package com.ygdevs.notjustjson.xml;

import com.ygdevs.notjustjson.util.DataElementOps;

public class XmlOps extends DataElementOps<XmlElement> {
    public static final XmlOps INSTANCE = new XmlOps();
    private XmlOps(){};
    @Override
    public XmlElement createList() {
        return XmlElement.list();
    }

    @Override
    public XmlElement createObject() {
        return XmlElement.object();
    }

    @Override
    public XmlElement empty() {
        return XmlElement.NULL;
    }

    @Override
    public XmlElement createNumeric(Number i) {
        return XmlElement.number(i);
    }

    @Override
    public XmlElement createBoolean(boolean bool) {
        return XmlElement.bool(bool);
    }

    @Override
    public XmlElement createString(String value) {
        return XmlElement.string(value);
    }
}
