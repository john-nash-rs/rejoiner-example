package com.nulpointerexception.server;

import com.google.inject.AbstractModule;
import com.nulpointerexception.server.proto.SignUpServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GRPCAppClientModule extends AbstractModule {
    private static final String HOST = "localhost";
    private static final int PORT = 8089;

    @Override
    protected void configure() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT).usePlaintext().build();
        bind(SignUpServiceGrpc.SignUpServiceBlockingStub.class).toInstance(SignUpServiceGrpc.newBlockingStub(channel));
        bind(SignUpServiceGrpc.SignUpServiceFutureStub.class).toInstance(SignUpServiceGrpc.newFutureStub(channel));
    }
}
