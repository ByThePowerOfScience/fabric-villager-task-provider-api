package com.bythepowerofscience.taskapi.example;

import com.bythepowerofscience.taskapi.impl.WorkerVillagerTask;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Collections;

public class BoneMealTask extends WorkerVillagerTask {
    
    private long lastEndEntityAge;
    
    @Override
    protected boolean isSuitableTarget(BlockPos pos, ServerWorld world) {
        return canBoneMeal(pos, world);
    }
    
    @Override
    protected boolean checkRunConditions(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        return villagerEntity.age % 10 == 0
                && (this.lastEndEntityAge == 0L || this.lastEndEntityAge + 160L <= (long) villagerEntity.age)
                && villagerEntity.getInventory().count(Items.BONE_MEAL) > 0;
    }
    
    @Override
    protected boolean deactivateIfMobGriefingDisabled() {
        return false;
    }
    
    @Override
    protected void doWorldActions(BlockPos currentTarget, ServerWorld serverWorld, VillagerEntity villagerEntity, long startTick) {
        ItemStack itemStack = ItemStack.EMPTY;
        SimpleInventory simpleInventory = villagerEntity.getInventory();
        int i = simpleInventory.size();

        for(int j = 0; j < i; ++j) {
            ItemStack itemStack2 = simpleInventory.getStack(j);
            if (itemStack2.getItem() == Items.BONE_MEAL) {
                itemStack = itemStack2;
                break;
            }
        }
        
        if (!itemStack.isEmpty() && BoneMealItem.useOnFertilizable(itemStack, serverWorld, currentTarget)) {
            serverWorld.syncWorldEvent(2005, currentTarget, 0);
            this.setCurrentTarget(serverWorld, villagerEntity);
            this.addLookWalkTarget(villagerEntity, this.currentTarget);
            this.nextResponseTime = startTick + 40L;
        }
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long startTick)
    {
        addLookWalkTarget(villagerEntity, currentTarget);
        villagerEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BONE_MEAL));
        this.nextResponseTime = startTick;
        this.ticksRan = 0;
    }

    @Override
    protected int getDuration() {
        return 80;
    }
    
    private boolean canBoneMeal(BlockPos pos, ServerWorld world) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block instanceof CropBlock && !((CropBlock)block).isMature(blockState);
    }
    
    
    @Override
    protected void getTargetsInRange(ServerWorld world, VillagerEntity entity)
    {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos res = null;
        int i = 0;
        
        for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
                for(int l = -1; l <= 1; ++l, ++i) 
                {
                    mutable.set((Vec3i)entity.getBlockPos(), j, k, l);
                    
                    if (this.isSuitableTarget(mutable, world)
                            && world.random.nextInt(i) == 0) {
                        res = mutable.toImmutable();
                    }
                }
            }
        }
        
        addPotentialTarget(res);
    }
    
    @Override
    protected void addPotentialTarget(final BlockPos pos)
    {
        this.targetPositions = Collections.singletonList(pos);
    }
    
    
    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long startTick)
    {
        villagerEntity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.lastEndEntityAge = (long)villagerEntity.age;
    }
}
