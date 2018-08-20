package io.github.phantamanta44.pcrossbow.wsd;

import io.github.phantamanta44.libnine.InitMe;
import io.github.phantamanta44.libnine.wsd.WSDIdentity;
import io.github.phantamanta44.pcrossbow.Xbow;

public class XbowWSDs {

    public static final WSDIdentity<LaserConsumerTracker> LASER_CONSUMER_TRACKER
            = new WSDIdentity<>("laser_consumer_tracker", LaserConsumerTracker.class);

    @InitMe
    public static void init() {
        Xbow.INSTANCE.getWsdManager().register(LASER_CONSUMER_TRACKER, LaserConsumerTracker::new);
    }

}
