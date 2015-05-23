package me.libs.utils;

import retrofit.RestAdapter;

/**
 * Created by luftzug on 5/23/15.
 */
public class LibsRestAdapter {
    private RestAdapter delegate;
    private static LibsRestAdapter instance;

    private LibsRestAdapter() {
        delegate = new RestAdapter.Builder().setEndpoint("https://libs-server.herokuapp.com/api/v1/").build();
    }

    public static LibsService getService() {
        if (instance == null) {
            instance = new LibsRestAdapter();
        }
        return instance.delegate.create(LibsService.class);
    }
}
