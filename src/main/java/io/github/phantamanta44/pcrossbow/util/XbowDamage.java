package io.github.phantamanta44.pcrossbow.util;

import net.minecraft.util.DamageSource;

public class XbowDamage {

    public static final DamageSource SRC_LASER = new DamageSource("xbow_laser")
            .setDamageBypassesArmor().setFireDamage();

}
