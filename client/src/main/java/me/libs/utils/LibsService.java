package me.libs.utils;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by luftzug on 2015-4-3.
 */
public interface LibsService {
    /**
     * @param login with full details.
     * @return hashed access token
     */
    @PUT("/auth/signup")
    String signUp(@Body UserLogin login);

    /**
     * @param login without email.
     * @return hashed access token
     */
    @POST("/auth/login")
    String login(@Body UserLogin login);
}
