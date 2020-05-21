package com.nulpointerexception.server;

import com.google.api.graphql.rejoiner.Query;
import com.google.api.graphql.rejoiner.SchemaModification;
import com.google.api.graphql.rejoiner.SchemaModule;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.nulpointerexception.server.proto.BookingNexusServiceDemo;
import com.nulpointerexception.server.proto.SignUpServiceGrpc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingNexusDemoSchemaModule extends SchemaModule {
    @Inject
    SignUpServiceGrpc.SignUpServiceFutureStub client;
    @Inject
    SignUpServiceGrpc.SignUpServiceBlockingStub clientBlocking;

    @Query("bookings")
    ListenableFuture<BookingNexusServiceDemo.BookingResponse> getBooking(BookingNexusServiceDemo.BookingSearch request) throws InterruptedException {
        return client.searchBookings(request);
    }

    @Query("machines")
    @SchemaModification(addField = "machines", onType = BookingNexusServiceDemo.Booking.class)
    ListenableFuture<BookingNexusServiceDemo.Machine> machines(BookingNexusServiceDemo.Booking booking) throws InterruptedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("================= At machines call start============"+dtf.format(now));
        BookingNexusServiceDemo.MachineRequest machineRequest = BookingNexusServiceDemo.MachineRequest.newBuilder().
                setMachineId(String.valueOf(booking.getMachineId())).build();
        return client.findMachine(machineRequest);
    }

    @Query("crops")
    @SchemaModification(addField = "crops", onType = BookingNexusServiceDemo.Booking.class)
    ListenableFuture<BookingNexusServiceDemo.Crop> crops(BookingNexusServiceDemo.Booking booking) throws InterruptedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("================= At crop call start============"+dtf.format(now));
        BookingNexusServiceDemo.CropRequest cropRequest = BookingNexusServiceDemo.CropRequest.newBuilder()
                .setCropId(String.valueOf(booking.getCropId())).build();
        return client.findCrop(cropRequest);
    }
}
