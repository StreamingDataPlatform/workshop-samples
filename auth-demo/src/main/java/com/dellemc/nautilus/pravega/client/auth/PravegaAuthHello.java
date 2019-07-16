package com.dellemc.nautilus.pravega.client.auth;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import io.pravega.client.ClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.ScalingPolicy;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.client.stream.impl.JavaSerializer;
import io.pravega.client.admin.StreamInfo;

import com.dellemc.oe.util.CommonParams;


public class PravegaAuthHello {

    private final static String CONTROLLER_URI = "tcp://10.247.118.176:9090";
    private final static String SCOPE_NAME = "workshop-samples";

    public final String scope;
    public final String streamName;
    public final URI controllerURI;

    public PravegaAuthHello(String scope,String streamName,URI controllerURI) {
        this.scope = scope;
        this.streamName = streamName;
        this.controllerURI = controllerURI;
    }

    public void run() {
        StreamManager streamManager = StreamManager.create(controllerURI);
        java.util.Iterator streamsList = streamManager.listStreams(scope);

        /*System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ streamsList :  "+streamsList.hasNext());
        // same as above -- just different syntax
        while(streamsList.hasNext() ) {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ streamsList Loop @@@@@@@@@@@:  ");
            String name = (String)streamsList.next();
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Stream :  "+name);
        }*/

        StreamInfo  streamInfo=streamManager.getStreamInfo(scope,streamName);
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ streamInfo  @@@@@@@@@@@:  "+streamInfo.toString());
    }

    public static void main(String[] args) {
        final String scope = CommonParams.getScope();
        final String streamName = CommonParams.getStreamName();
        final URI controllerURI = CommonParams.getControllerURI();
        PravegaAuthHello hww = new PravegaAuthHello(scope,streamName,controllerURI);
        hww.run();
    }
   
}
