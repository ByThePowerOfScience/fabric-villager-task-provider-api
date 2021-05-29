package com.bythepowerofscience.taskapi.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.village.VillagerProfession;

public class VillagerTaskProviderRegistry {
	private static final ImmutableMap.Builder<VillagerProfession, com.bythepowerofscience.taskapi.api.VillagerTaskProvider> VILLAGER_TASK_PROVIDER_BUILDER = ImmutableMap.builder();
	
	public static void addTaskProvider(VillagerProfession executingProfession, com.bythepowerofscience.taskapi.api.VillagerTaskProvider taskListProvider)
	{
		VILLAGER_TASK_PROVIDER_BUILDER.put(executingProfession, taskListProvider);
	}
	
	
	protected static ImmutableMap.Builder<VillagerProfession, com.bythepowerofscience.taskapi.api.VillagerTaskProvider> getCompletedMap()
	{
		return VILLAGER_TASK_PROVIDER_BUILDER;
	}
}
