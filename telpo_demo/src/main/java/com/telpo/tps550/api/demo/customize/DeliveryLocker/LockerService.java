package com.telpo.tps550.api.demo.customize.DeliveryLocker;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LockerService {
    @GET("/api/lockers/{id}")
    Call<Locker> getLockerStatus(@Path("id") String lockerId);

    @GET("/api/lockers/{id}/unlock")
    Call<Void> unlockLocker(@Path("id") String lockerId);

    @GET("/api/lockers/{id}/lock")
    Call<Void> lockLocker(@Path("id") String lockerId);
}
