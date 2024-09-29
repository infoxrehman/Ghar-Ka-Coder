package com.lixtanetwork.gharkacoder.geminiai;

public interface ResponseCallback {

    void onResponse(String response);

    void onError(String error);
}
