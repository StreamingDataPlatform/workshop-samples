package com.dellemc.nautilus.pravega.client.auth;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import com.dellemc.oe.util.Constants;
import io.pravega.client.ClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.ScalingPolicy;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.client.stream.impl.JavaSerializer;
import io.pravega.client.admin.StreamInfo;

import com.dellemc.oe.util.CommonParams;
import io.pravega.client.stream.impl.StreamImpl;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PravegaAuthHello {

    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(PravegaAuthHello.class);

    public final String scope;
    public final String streamName;
    public final URI controllerURI;

    public PravegaAuthHello(String scope,String streamName,URI controllerURI) {
        this.scope = scope;
        this.streamName = streamName;
        this.controllerURI = controllerURI;
    }

    public void run() {
        try(StreamManager streamManager = StreamManager.create(controllerURI);) {
            java.util.Iterator streamsList = streamManager.listStreams(scope);

            //  List Streams API not exposed by Pravega. This code can be uncommented when the API is supported.
            if (CommonParams.isPravegaStandalone()) {
                LOG.info("@@@@@@@@@@@@@@@@@@@@@ streamsList :  " + streamsList.hasNext());
                // same as above -- just different syntax
                while (streamsList.hasNext()) {
                    StreamImpl streamImpl = (StreamImpl) streamsList.next();
                    LOG.info("@@@@@@@@@@@@@@@@@@@@ Stream :  " + streamImpl.getStreamName());
                }
            }

            StreamInfo streamInfo = streamManager.getStreamInfo(scope, streamName);
            LOG.info("@@@@@@@@@@@@@@@@@@@@streamInfo  @@@@@@@@@@@:  " + streamInfo.toString());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        CommonParams.init(args);
        final String scope = CommonParams.getParam(Constants.SCOPE);
        final String streamName = CommonParams.getParam(Constants.STREAM_NAME);
        final URI controllerURI = URI.create(CommonParams.getParam(Constants.CONTROLLER_URI));
        PravegaAuthHello hww = new PravegaAuthHello(scope,streamName,controllerURI);
        hww.run();
    }
   
}
