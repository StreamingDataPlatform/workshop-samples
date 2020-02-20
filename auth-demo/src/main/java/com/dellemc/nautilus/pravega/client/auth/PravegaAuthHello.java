package com.dellemc.nautilus.pravega.client.auth;

import java.net.URI;

import com.dellemc.oe.util.AbstractApp;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.admin.StreamInfo;

import com.dellemc.oe.util.AppConfiguration;
import io.pravega.client.stream.Stream;
import io.pravega.client.stream.impl.StreamImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PravegaAuthHello extends AbstractApp {

    // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(PravegaAuthHello.class);



    public PravegaAuthHello(AppConfiguration appConfiguration) {
        super(appConfiguration);
    }

    public void run() {
        createStream(appConfiguration.getInputStreamConfig());
        Stream stream = appConfiguration.getInputStreamConfig().getStream();
        StreamInfo streamInfo = getStreamInfo(stream);
        LOG.info("@@@@@@@@@@@@@@@@@@@@streamInfo  @@@@@@@@@@@:  " + streamInfo.toString());
    }

    public static void main(String[] args) {
        AppConfiguration appConfiguration = new AppConfiguration(args);
        PravegaAuthHello hww = new PravegaAuthHello(appConfiguration);
        hww.run();
    }
   
}
