package com.nulpointerexception.server;

import com.google.api.graphql.rejoiner.Query;
import com.google.api.graphql.rejoiner.SchemaModule;
import com.google.common.util.concurrent.ListenableFuture;
import com.nulpointerexception.server.proto.SignUpServiceGrpc;
import com.nulpointerexception.server.proto.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GRPCAppSchemaModule extends SchemaModule {
    
    @Query("weather")
    String getWeather() throws InterruptedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("================= At weather service start============"+dtf.format(now));
        Thread.sleep(8000);
        now = LocalDateTime.now();
        System.out.println("================= At weather service stop============"+dtf.format(now));
        return "temperature is 32 degree celcius";
    }
    @Query("rewards")
    User.SignUpResponse reward(User.UserData request, SignUpServiceGrpc.SignUpServiceBlockingStub client) {
        return client.signUp(request);
    }

    @Query("rewardAsync")
    ListenableFuture<User.SignUpResponse> rewardAsync(User.UserData request, SignUpServiceGrpc.SignUpServiceFutureStub client) {
        return client.signUp(request);
    }


}
