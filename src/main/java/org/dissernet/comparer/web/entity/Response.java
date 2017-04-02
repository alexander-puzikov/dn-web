package org.dissernet.comparer.web.entity;

/**
 * Created by APuzikov on 19.02.2017.
 */
public class Response {
    private boolean isError = false;
    private String value;

    public Response(String value) {
        this.value = value;
    }

    public Response(String value, boolean isError) {
        this.isError = isError;
        this.value = value;
    }

    public boolean isError() {
        return isError;
    }

    public String getValue() {
        return value;
    }
}
