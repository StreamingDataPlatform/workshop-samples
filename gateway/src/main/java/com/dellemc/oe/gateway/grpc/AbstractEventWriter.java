package com.dellemc.oe.gateway.grpc;

import io.pravega.client.stream.TxnFailedException;

abstract class AbstractEventWriter<T> {
    void open() {
    }

    abstract void writeEvent(String routingKey, T event) throws TxnFailedException;

    void commit() throws TxnFailedException {
    }

    void abort() {
    }

    void close() throws TxnFailedException {
    }
}
