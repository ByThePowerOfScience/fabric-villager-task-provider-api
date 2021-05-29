package com.bythepowerofscience.taskapi.api;

import com.bythepowerofscience.taskapi.impl.VillagerTaskProvider;
import com.google.common.collect.ImmutableMap;
import net.minecraft.village.VillagerProfession;

public class VillagerTaskProviderRegistry {
	private static final ImmutableMap.Builder<VillagerProfession, VillagerTaskProvider> VILLAGER_TASK_PROVIDER_BUILDER = ImmutableMap.builder();
	
	public static void addTaskProvider(VillagerProfession executingProfession, VillagerTaskProvider taskListProvider)
	{
		VILLAGER_TASK_PROVIDER_BUILDER.put(executingProfession, taskListProvider);
	}
	
	/**
	 * Implementation only. Do not use.
	 */
	protected static ImmutableMap.Builder<VillagerProfession, VillagerTaskProvider> getCompletedMap()
	{
		return VILLAGER_TASK_PROVIDER_BUILDER;
	}
}
