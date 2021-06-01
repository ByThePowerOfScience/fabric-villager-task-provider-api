package com.bythepowerofscience.examplemod;

import com.bythepowerofscience.taskapi.impl.WorkerVillagerTask;
import net.minecraft.block.*;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

/**
 * @see net.minecraft.entity.ai.brain.task.FarmerVillagerTask
 */
public class FarmerVillagerTask extends WorkerVillagerTask {
    
    @Override
    protected boolean doesMobGriefing() {
        return true;
    }
    
    
    @Override
    protected boolean isSuitableTarget(BlockPos pos, ServerWorld world) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        Block block2 = world.getBlockState(pos.down()).getBlock();
        return block instanceof CropBlock && ((CropBlock)block).isMature(blockState) || blockState.isAir() && block2 instanceof FarmlandBlock;
    }

    @Override
    protected boolean checkRunConditions(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        return true;
    }

    @Override
    protected void doWorldActions(BlockPos currentTarget, ServerWorld serverWorld, VillagerEntity villagerEntity, long startTick) {
        BlockState blockState = serverWorld.getBlockState(this.currentTarget);
        Block block = blockState.getBlock();
        Block block2 = serverWorld.getBlockState(this.currentTarget.down()).getBlock();
        if (block instanceof CropBlock && ((CropBlock)block).isMature(blockState)) {
            serverWorld.breakBlock(this.currentTarget, true, villagerEntity);
        }

        if (blockState.isAir() && block2 instanceof FarmlandBlock && villagerEntity.hasSeedToPlant()) {
            SimpleInventory simpleInventory = villagerEntity.getInventory();

            for(int i = 0; i < simpleInventory.size(); ++i) {
                ItemStack itemStack = simpleInventory.getStack(i);
                boolean bl = false;
                if (!itemStack.isEmpty()) {
                    if (itemStack.getItem() == Items.WHEAT_SEEDS) {
                        serverWorld.setBlockState(this.currentTarget, Blocks.WHEAT.getDefaultState(), 3);
                        bl = true;
                    } else if (itemStack.getItem() == Items.POTATO) {
                        serverWorld.setBlockState(this.currentTarget, Blocks.POTATOES.getDefaultState(), 3);
                        bl = true;
                    } else if (itemStack.getItem() == Items.CARROT) {
                        serverWorld.setBlockState(this.currentTarget, Blocks.CARROTS.getDefaultState(), 3);
                        bl = true;
                    } else if (itemStack.getItem() == Items.BEETROOT_SEEDS) {
                        serverWorld.setBlockState(this.currentTarget, Blocks.BEETROOTS.getDefaultState(), 3);
                        bl = true;
                    }
                }

                if (bl) {
                    serverWorld.playSound((PlayerEntity)null, (double)this.currentTarget.getX(), (double)this.currentTarget.getY(), (double)this.currentTarget.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    itemStack.decrement(1);
                    if (itemStack.isEmpty()) {
                        simpleInventory.setStack(i, ItemStack.EMPTY);
                    }
                    break;
                }
            }

            if (block instanceof CropBlock && !((CropBlock)block).isMature(blockState)) {
                this.targetPositions.remove(this.currentTarget);
                this.currentTarget = this.chooseRandomTarget(serverWorld);
                if (this.currentTarget != null) {
                    this.nextResponseTime = startTick + 20L;
                    addLookWalkTarget(villagerEntity, currentTarget);
                }
            }
        }
    }
    
    @Override
    protected int getDuration() {
        return 200;
    }


}
