package cjminecraft.doubleslabs.addons.stairwaytoaether;

import cjminecraft.doubleslabs.api.ISlabSupport;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class StairwayToAetherSlabSupport<T extends Enum<T> & IStringSerializable> implements ISlabSupport {

    private final Class<?> slab;
    private final PropertyEnum<T> typeProperty;
    private final T[] slabTypes;

    public StairwayToAetherSlabSupport() {
        Class<?> slab;
        PropertyEnum<T> typeProperty;
        T[] slabTypes;
        try {
            slab = Class.forName("mod.stairway.blocks.BlockSlab");
            typeProperty = (PropertyEnum<T>) slab.getField("HALF").get(null);
            Class<?> slabType = Class.forName("mod.stairway.blocks.BlockSlab$EnumBlockHalf");
            slabTypes = (T[]) slabType.getEnumConstants();
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            slab = null;
            typeProperty = null;
            slabTypes = null;
        }
        this.slab = slab;
        this.typeProperty = typeProperty;
        this.slabTypes = slabTypes;
    }

    @Override
    public boolean isHorizontalSlab(IBlockAccess world, BlockPos pos, IBlockState state) {
        return slab != null && slab.isAssignableFrom(state.getBlock().getClass()) && (state.getValue(typeProperty).equals(slabTypes[0]) || state.getValue(typeProperty).equals(slabTypes[1]));
    }

    @Override
    public boolean isHorizontalSlab(ItemStack stack, EntityPlayer player, EnumHand hand) {
        return slab != null && (stack.getItem() instanceof ItemBlock) && slab.isAssignableFrom(((ItemBlock)stack.getItem()).getBlock().getClass());
    }

    @Override
    public boolean isVerticalSlab(IBlockAccess world, BlockPos pos, IBlockState state) {
        return slab != null && slab.isAssignableFrom(state.getBlock().getClass()) && !(state.getValue(typeProperty).equals(slabTypes[0]) || state.getValue(typeProperty).equals(slabTypes[1])) && !state.getValue(typeProperty).equals(slabTypes[6]);
    }

    @Override
    public boolean isVerticalSlab(ItemStack stack, EntityPlayer player, EnumHand hand) {
        return isHorizontalSlab(stack, player, hand);
    }

    @Override
    public IBlockState getStateForDirection(World world, BlockPos pos, ItemStack stack, EnumFacing direction) {
        IBlockState state = net.minecraft.block.Block.getBlockFromItem(stack.getItem()).getDefaultState();
        return slab == null ? state : state.withProperty(typeProperty, slabTypes[direction.getIndex()]);
    }

    @Override
    public EnumFacing getDirection(World world, BlockPos pos, IBlockState state) {
        if (slab == null)
            return ISlabSupport.super.getDirection(world, pos, state);
        Enum<T> type = state.getValue(typeProperty);
        if (type.equals(slabTypes[2]))
            return EnumFacing.NORTH;
        else if (type.equals(slabTypes[3]))
            return EnumFacing.SOUTH;
        else if (type.equals(slabTypes[4]))
            return EnumFacing.EAST;
        return EnumFacing.WEST;
    }

    @Override
    public BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state) {
        if (slab == null)
            return ISlabSupport.super.getHalf(world, pos, state);
        Enum<T> type = state.getValue(typeProperty);
        if (type.equals(slabTypes[0]))
            return BlockSlab.EnumBlockHalf.TOP;
        else
            return BlockSlab.EnumBlockHalf.BOTTOM;
    }

    @Override
    public IBlockState getStateForHalf(World world, BlockPos pos, IBlockState state, BlockSlab.EnumBlockHalf half) {
        return slab == null ? state : state.withProperty(typeProperty, slabTypes[half == BlockSlab.EnumBlockHalf.TOP ? 0 : 1]);
    }
}