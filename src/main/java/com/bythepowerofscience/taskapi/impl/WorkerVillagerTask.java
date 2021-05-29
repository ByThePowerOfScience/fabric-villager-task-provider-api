package com.bythepowerofscience.taskapi.impl;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.FarmerVillagerTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A helper class for creating simple world-affecting {@code VillagerTask}s, including pre-made functions for scanning for targets, etc.
 * @apiNote Tasks may also extend {@code Task&lt;VillagerEntity&gt;}.
 * @see net.minecraft.entity.ai.brain.task.FarmerVillagerTask
 * @see net.minecraft.entity.ai.brain.task.BoneMealTask
 * @see Task
 */
public abstract class WorkerVillagerTask extends Task<VillagerEntity> {
    @Nullable
    protected BlockPos currentTarget;
    protected long nextResponseTime;
    protected int ticksRan;
    
    protected List<BlockPos> targetPositions = Lists.newArrayList();
    
    public WorkerVillagerTask() {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT,
                MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleState.VALUE_PRESENT));
    }
    
    public WorkerVillagerTask(ImmutableMap<MemoryModuleType<?>, MemoryModuleState> memoryMap)
    {
        super(memoryMap);
    }
    
    /**
     * Checks if the block at this position satisfies interaction requirements.<p>
     * 
     * Example: the Farmer {@link VillagerProfession} checks for one of two things:
     * <ol>
     *     <li>For harvesting: if the block is a crop and is mature,</li>
     *      <li>For planting: if the targeted block is air and the block below it is farmland.</li>
     * </ol>
     * Thus, the Farmer's implementation of this method would be as follows:
     * <pre>protected boolean isSuitableTarget(BlockPos pos, ServerWorld world) {
     *      BlockState blockState = world.getBlockState(pos);
     *      Block block = blockState.getBlock();
     *      Block block2 = world.getBlockState(pos.down()).getBlock();
     *      return (block instanceof CropBlock) &amp;&amp; ((CropBlock)block).isMature(blockState)
     *             || (blockState.isAir() &amp;&amp; block2 instanceof FarmlandBlock);
     * }</pre>
     * 
     * @param pos The position of the block in the world.
     * @param world The serverworld in which the block exists.
     * @return true if the block at that position satisfies the requirements to be interacted with.
     */
    protected abstract boolean isSuitableTarget(BlockPos pos, ServerWorld world);

    /**
     * The conditions for this task running, e.g. daylight level, villager age, profession, etc.<p>
     * 
     * Example:
     * <pre>protected boolean checkRunConditions(ServerWorld serverWorld, VillagerEntity villagerEntity) {
     *      return villagerEntity.getVillagerData().getProfession() == VillagerProfession.FARMER;
     * }</pre>
     * 
     * @param serverWorld the current server world.
     * @param villagerEntity the {@link VillagerEntity} currently attempting to execute this task.
     * @return {@code true} if this task should run with the given environment.
     */
    protected abstract boolean checkRunConditions(ServerWorld serverWorld, VillagerEntity villagerEntity);
    
    /**
     * If this task should run only when {@code doMobGriefing} is enabled.<p>
     * Generally necessary for any tasks that break blocks, but not necessary if they only <i>modify</i> blocks.<p>
     * 
     * Example: {@link FarmerVillagerTask} needs to break crops to harvest them, so its implementation would return {@code true}.<p>
     * @return true or false.
     */
    protected abstract boolean deactivateIfMobGriefingDisabled();
    
    
    /**
     * The world action to be performed on the currently-targeted block position.<p>
     *
     * @param currentTarget The {@link BlockPos} of the currently-targeted block.
     * @param serverWorld The {@link ServerWorld} the block is located in.
     * @param villagerEntity The {@link VillagerEntity} acting upon this block.
     * @param startTick The game tick that the task started on.
     */
    protected abstract void doWorldActions(BlockPos currentTarget, ServerWorld serverWorld, VillagerEntity villagerEntity, long startTick);
    
    
    /**
	 * @return The duration, in game ticks, that the task should run for.
	 */
    protected abstract int getDuration();
    
    
    /**
     * Checks if this task should be executed by the villager who randomly chose it.
     * @apiNote Developers using this method should avoid overriding this method, instead using {@link WorkerVillagerTask#checkRunConditions}.
     * @see Task#shouldRun
     * @param serverWorld The {@code ServerWorld} this task is being executed in.
     * @param villagerEntity The {@code VillagerEntity} attempting to execute the task.
     * @return {@code true} if this task should run.
     * @implNote Implementations of this method should also set the current target via side-effect.
     */
    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if ((deactivateIfMobGriefingDisabled() && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING))
                || !checkRunConditions(serverWorld, villagerEntity))
            return false;
        else {
            setCurrentTarget(serverWorld, villagerEntity);
            return this.currentTarget != null;
        }
    }
    
    /**
	 * Clears all potential targets stored, scans for new potential targets, then selects one randomly as the current target.
     * @apiNote Programs should not override this method, instead overriding {@link WorkerVillagerTask#chooseRandomTarget}.
     * @param serverWorld The {@code ServerWorld} this task is being executed in.
     * @param villagerEntity The {@code VillagerEntity} currently executing this task.
     */
    protected void setCurrentTarget(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        this.targetPositions.clear();
        
        this.getTargetsInRange(serverWorld, villagerEntity);
        
        this.currentTarget = this.chooseRandomTarget(serverWorld);
    }
    
    protected void getTargetsInRange(ServerWorld serverWorld, VillagerEntity villagerEntity)
    {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) 
                {
                    mutable.set(villagerEntity.getBlockPos(), i, j, k);
                    
                    if (this.isSuitableTarget(mutable, serverWorld)) 
                        addPotentialTarget(mutable);
                    
                }
            }
        }
    }
    
    /**
     * The mechanism for saving potential {@link BlockPos} target positions
     * @implNote The only reason to override this method is to use a {@code Collections.singletonList()} implementation instead, as with {@link net.minecraft.entity.ai.brain.task.BoneMealTask}.
     * @param pos The potential target position.
     */
    protected void addPotentialTarget(BlockPos pos) {
        this.targetPositions.add(pos);
    }
    
    /**
     * 
     * @param world The server world being scanned.
     * @return The {@link BlockPos} that should be chosen as a target.
     */
    @Nullable
    protected BlockPos chooseRandomTarget(ServerWorld world) {
        if (this.targetPositions.isEmpty())
            return null;
        else
            return this.targetPositions.get(world.getRandom().nextInt(this.targetPositions.size()));
    }
    
    
    
    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long startTick) {
        if (startTick > this.nextResponseTime && this.currentTarget != null) {
            addLookWalkTarget(villagerEntity, this.currentTarget);
        }
    }
    
    
    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long startTick) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = startTick + getEndDelay();
    }
	
	
	/**
     * Called to perform the task's main action.
     * @implNote {@code currentTarget} should be set in {@link WorkerVillagerTask#shouldRun}, and implementations of this method should assume {@code currentTarget} has already been set. This is to prevent the task running when no suitable target is within range.
     * @param serverWorld The world that this task is being executed in.
     * @param villagerEntity The {@code VillagerEntity} executing this task.
     * @param startTick The game tick the task was started on.
     */
    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long startTick) {
        if (this.currentTarget == null || this.currentTarget.isWithinDistance(villagerEntity.getPos(), 1.0D)) {
            if (this.currentTarget != null && startTick > this.nextResponseTime) {
                doWorldActions(currentTarget, serverWorld, villagerEntity, startTick);
            }

            ++this.ticksRan;
        }
    }
    
    /**
     * 
     * @return the number of game ticks before the next random target should be chosen.
     */
    protected long getEndDelay()
    {
        return 40L;
    }
    
    
    
    
    

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return this.ticksRan < getDuration();
    }
    
    
    
    
    protected void addLookWalkTarget(final VillagerEntity villagerEntity, final BlockPos target)
    {
        final BlockPosLookTarget lookTarget = new BlockPosLookTarget(target);
        villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, (new WalkTarget(lookTarget, 0.5F, 1)));
        villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, (lookTarget));
    }
}

