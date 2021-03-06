package io.github.phantamanta44.pcrossbow.block.base;

import io.github.phantamanta44.pcrossbow.block.BlockLaser;
import io.github.phantamanta44.pcrossbow.block.BlockMachine;
import io.github.phantamanta44.pcrossbow.block.BlockOptics;
import io.github.phantamanta44.pcrossbow.block.BlockRedstoneWindow;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;

public class XbowProps {

    public static final PropertyEnum<BlockLaser.Type> LASER_TYPE = PropertyEnum.create("type", BlockLaser.Type.class);
    public static final PropertyEnum<BlockOptics.Type> OPTICS_TYPE = PropertyEnum.create("type", BlockOptics.Type.class);
    public static final PropertyEnum<BlockRedstoneWindow.Type> REDSTONE_WINDOW_TYPE = PropertyEnum.create("type", BlockRedstoneWindow.Type.class);
    public static final PropertyEnum<BlockMachine.Type> MACHINE_TYPE = PropertyEnum.create("type", BlockMachine.Type.class);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    public static final PropertyEnum<EnumFacing> ROTATION = PropertyEnum.create("rotation", EnumFacing.class);
    public static final PropertyEnum<EnumFacing> ROTATION_HOR = PropertyEnum.create("rotation", EnumFacing.class, EnumFacing.HORIZONTALS);

}
