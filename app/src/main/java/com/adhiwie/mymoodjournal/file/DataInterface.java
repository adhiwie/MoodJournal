package com.adhiwie.mymoodjournal.file;

import org.json.JSONException;

public interface DataInterface {

    public String toJSONString() throws JSONException;

    public String getDataType();
}
