package com.bythepowerofscience.taskapi;

import com.bythepowerofscience.Constants;
import com.bythepowerofscience.taskapi.impl.VillagerTaskProvider;
import com.bythepowerofscience.taskapi.impl.WorkerVillagerTask;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.GrassPathBlock;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Level;

public class Test implements ModInitializer {
	@Override
	public void onInitialize()
	{
		Constants.LOGGER.log(Level.INFO, "ModInitializer Initialized");
		VillagerTaskProvider.addBaseRandomTask(VillagerTaskProvider.TaskType.WORK, (p, f) -> Pair.of(new TestTask() {
			@Override
			protected boolean isSuitableTarget(final BlockPos pos, final ServerWorld world)
			{
				return world.getBlockState(pos).getBlock() instanceof GrassBlock;
			}
			
			@Override
			String getType()
			{
				return "WORK RANDOM";
			}
		}, 2));
		
		VillagerTaskProvider.addBaseRandomTask(VillagerTaskProvider.TaskType.IDLE, (p, f) -> Pair.of(new TestTask() {
			@Override
			protected boolean isSuitableTarget(final BlockPos pos, final ServerWorld world)
			{
				return world.getBlockState(pos).getBlock() instanceof GrassPathBlock;
			}
			
			@Override
			String getType()
			{
				return "IDLE RANDOM";
			}
		}, 2));
		
		VillagerTaskProvider.addBaseRandomTask(VillagerTaskProvider.TaskType.MEET, (p, f) -> Pair.of(new TestTask() {
			@Override
			protected boolean isSuitableTarget(final BlockPos pos, final ServerWorld world)
			{
				return world.getBlockState(pos).getBlock() instanceof FletchingTableBlock;
			}
			
			@Override
			String getType()
			{
				return "MEET RANDOM";
			}
		}, 2));
	}
}

abstract class TestTask extends WorkerVillagerTask {
	
	
	abstract String getType();
	
	@Override
	protected boolean checkRunConditions(final ServerWorld serverWorld, final VillagerEntity villagerEntity)
	{
		Constants.LOGGER.log(Level.INFO, "Test task " + getType() + " has been run by " + villagerEntity.getEntityId() + " " + villagerEntity.getVillagerData().getProfession());
		return true;
	}
	
	@Override
	protected boolean doesMobGriefing()
	{
		return false;
	}
	
	@Override
	protected void doWorldActions(final BlockPos currentTarget, final ServerWorld serverWorld, final VillagerEntity villagerEntity, final long startTick)
	{
		targetPositions.forEach(target -> {
			serverWorld.breakBlock(target, false);
		});
	}
	
	@Override
	protected int getDuration()
	{
		return 2000;
	}
	
}
