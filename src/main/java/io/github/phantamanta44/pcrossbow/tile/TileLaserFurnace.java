package io.github.phantamanta44.pcrossbow.tile;

import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.libnine.capability.impl.L9AspectSlot;
import io.github.phantamanta44.libnine.capability.provider.CapabilityBrokerDirPredicated;
import io.github.phantamanta44.libnine.recipe.input.ItemStackInput;
import io.github.phantamanta44.libnine.recipe.output.ItemStackOutput;
import io.github.phantamanta44.libnine.recipe.type.SmeltingRecipe;
import io.github.phantamanta44.libnine.tile.RegisterTile;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.libnine.util.data.serialization.IDatum;
import io.github.phantamanta44.libnine.util.world.BlockSide;
import io.github.phantamanta44.libnine.util.world.IAllocableSides;
import io.github.phantamanta44.libnine.util.world.SideAlloc;
import io.github.phantamanta44.pcrossbow.LasingResult;
import io.github.phantamanta44.pcrossbow.api.capability.ILaserConsumer;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.api.tile.IHeatCarrier;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import io.github.phantamanta44.pcrossbow.tile.base.TileSimpleProcessor;
import io.github.phantamanta44.pcrossbow.util.SlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;

@RegisterTile(XbowConst.MOD_ID)
public class TileLaserFurnace extends TileSimpleProcessor<ItemStack, ItemStackInput, ItemStack, ItemStackOutput, SmeltingRecipe>
        implements ILaserConsumer, IHeatCarrier, IAllocableSides<SlotType.BasicIO> {

    @AutoSerialize
    private final L9AspectSlot slotIn, slotOut;
    @AutoSerialize
    private final SideAlloc<SlotType.BasicIO> sides;
    @AutoSerialize
    private final IDatum.OfDouble temp;

    public TileLaserFurnace() {
        super(SmeltingRecipe.class);
        this.slotIn = new L9AspectSlot.Observable(
                s -> LibNine.PROXY.getRecipeManager().getRecipeList(SmeltingRecipe.class).findRecipe(s) != null,
                (o, n) -> inputChanged());
        this.slotOut = new L9AspectSlot(s -> false);
        this.sides = new SideAlloc<>(SlotType.BasicIO.NONE, this::getDirection);
        this.temp = IDatum.ofDouble(300D);
    }

    @Override
    protected ICapabilityProvider initCapabilities() {
        return new CapabilityBrokerDirPredicated()
                .with(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, slotIn, sides.getPredicate(SlotType.BasicIO.INPUT))
                .with(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, slotOut, sides.getPredicate(SlotType.BasicIO.OUTPUT))
                .with(XbowCaps.LASER_CONSUMER, this);
    }

    public L9AspectSlot getInputSlot() {
        return slotIn;
    }

    public L9AspectSlot getOutputSlot() {
        return slotOut;
    }

    @Override
    public double getTemperature() {
        return temp.getDouble();
    }

    @Override
    public void setFace(BlockSide face, SlotType.BasicIO state) {
        sides.setFace(face, state);
        setDirty();
    }

    @Override
    public SlotType.BasicIO getFace(BlockSide face) {
        return sides.getFace(face);
    }

    @Override
    protected void tick() {
        super.tick();
        double temp = getTemperature();
        if (temp != 300D) {
            this.temp.postincrement((0.01D + world.rand.nextDouble()) * 0.01D * (300D - temp));
            setDirty();
        }
    }

    @Override
    protected boolean inputExists() {
        return !slotIn.getStackInSlot().isEmpty();
    }

    @Override
    protected ItemStack getInput() {
        return slotIn.getStackInSlot();
    }

    @Override
    protected int getWorkNeeded(SmeltingRecipe recipe) {
        return 1200;
    }

    @Override
    protected boolean updateWork() {
        double temp = getTemperature();
        if (temp >= 350D) {
            if (!world.isRemote) doWork(Math.max((int)Math.floor((temp - 340.5D) / 9D), 0));
            return true;
        }
        return false;
    }

    @Override
    protected ItemStack getOutputEnv() {
        return slotOut.getStackInSlot();
    }

    @Override
    protected void consumeInput(ItemStackInput input) {
        if (slotIn.getStackInSlot().getCount() == 1) {
            slotIn.setStackInSlot(ItemStack.EMPTY);
        } else {
            slotIn.getStackInSlot().shrink(1);
        }
    }

    @Override
    protected void acceptOutput(ItemStackOutput output) {
        if (slotOut.getStackInSlot().isEmpty()) {
            slotOut.setStackInSlot(output.getOutput().copy());
        } else {
            slotOut.getStackInSlot().grow(output.getOutput().getCount());
        }
    }

    @Override
    public LasingResult consumeBeam(Vec3d pos, Vec3d dir, EnumFacing face, double power, double radius, double fluxAngle) {
        double change = Math.max((int)Math.floor(power * (1 - Math.pow(2 * radius, 3))) / 4096D, 0);
        if (change > 0) {
            temp.postincrement(change);
            setDirty();
        }
        return LasingResult.CONSUME;
    }

}
