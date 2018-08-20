package io.github.phantamanta44.pcrossbow.tile.base;

import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.libnine.recipe.IRcp;
import io.github.phantamanta44.libnine.recipe.IRecipeList;
import io.github.phantamanta44.libnine.recipe.input.IRcpIn;
import io.github.phantamanta44.libnine.recipe.output.IRcpOut;
import io.github.phantamanta44.libnine.tile.L9TileEntityTicking;
import io.github.phantamanta44.libnine.util.data.ByteUtils;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.libnine.util.data.serialization.IDatum;
import io.github.phantamanta44.libnine.util.world.IRedstoneControllable;
import io.github.phantamanta44.libnine.util.world.RedstoneBehaviour;
import io.github.phantamanta44.pcrossbow.api.tile.IMachineProgress;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public abstract class TileSimpleProcessor<T, I extends IRcpIn<T>, U, O extends IRcpOut<U>, R extends IRcp<T, I, O>>
        extends L9TileEntityTicking implements IRedstoneControllable, IMachineProgress {

    @AutoSerialize
    private final IDatum<EnumFacing> rotation;
    @AutoSerialize
    private final IDatum<RedstoneBehaviour> redstone;
    @AutoSerialize
    private final IDatum.OfInt currentWork;

    private final IRecipeList<T, I, O, R> recipeList;

    private boolean isWorking;
    private boolean inputDirty;
    @Nullable
    private R recipe;
    @Nullable
    private O output;
    private EnumFacing clientFace;

    public TileSimpleProcessor(Class<R> recipeType) {
        this.rotation = IDatum.of(EnumFacing.NORTH);
        this.redstone = IDatum.of(RedstoneBehaviour.IGNORED);
        this.currentWork = IDatum.ofInt(0);
        this.recipeList = LibNine.PROXY.getRecipeManager().getRecipeList(recipeType);
        this.isWorking = false;
        this.inputDirty = true;
        this.recipe = null;
        this.output = null;
        this.clientFace = EnumFacing.NORTH;
        markRequiresSync();
        setInitialized();
    }

    public void setDirection(EnumFacing dir) {
        rotation.set(dir);
        setDirty();
    }

    public EnumFacing getDirection() {
        return rotation.get();
    }

    @Override
    public boolean isWorking() {
        return isWorking;
    }

    @Override
    public float getProgress() {
        return recipe != null ? (float)currentWork.getInt() / getWorkNeeded(recipe) : 0;
    }

    @Override
    protected void tick() {
        if (inputDirty) {
            recipe = inputExists() ? recipeList.findRecipe(getInput()) : null;
            if (recipe != null) {
                output = recipe.mapToOutput(getInput());
            }
            if (!world.isRemote) currentWork.setInt(0);
            inputDirty = false;
            setDirty();
        }
        boolean wasWorking = isWorking;
        if (recipe != null && output != null && canWork()) {
            isWorking = updateWork();
            if (!world.isRemote) {
                int work = currentWork.getInt(), workNeeded = getWorkNeeded(recipe);
                if (work >= workNeeded) {
                    currentWork.setInt(0);
                    consumeInput(recipe.input());
                    acceptOutput(output);
                    setDirty();
                }
            }
        } else {
            isWorking = false;
        }
        if (isWorking != wasWorking) world.markBlockRangeForRenderUpdate(pos, pos);
    }

    protected void inputChanged() {
        inputDirty = true;
    }

    protected void doWork(int additionalWork) {
        if (canWork()) {
            currentWork.postincrement(additionalWork);
            setDirty();
        }
    }

    protected boolean canWork() {
        //noinspection ConstantConditions
        return output.isAcceptable(getOutputEnv()) && redstone.get().canWork(getWorldPos());
    }

    protected abstract boolean inputExists();

    protected abstract T getInput();

    protected int getWorkNeeded(R recipe) {
        return 200;
    }

    protected abstract boolean updateWork();

    protected abstract U getOutputEnv();

    protected abstract void consumeInput(I input);

    protected abstract void acceptOutput(O output);

    @Override
    public RedstoneBehaviour getRedstoneBehaviour() {
        return redstone.get();
    }

    @Override
    public void setRedstoneBehaviour(RedstoneBehaviour behaviour) {
        redstone.set(behaviour);
        setDirty();
    }

    @Override
    public void deserBytes(ByteUtils.Reader data) {
        super.deserBytes(data);
        if (clientFace != getDirection()) {
            world.markBlockRangeForRenderUpdate(pos, pos);
            clientFace = getDirection();
        }
    }

}
