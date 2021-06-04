package com.bythepowerofscience.taskapi.api;

import com.bythepowerofscience.taskapi.impl.VillagerTaskProvider;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class VillagerTaskProviderRegistry {
	private static final Map<VillagerProfession, VillagerTaskProvider> VILLAGER_TASK_PROVIDER_BUILDER = new HashMap<>();
	
	public static void addTaskProvider(VillagerProfession executingProfession, VillagerTaskProvider taskListProvider)
	{
		assert(executingProfession != null || taskListProvider != null);
		
		VillagerTaskProvider old = VILLAGER_TASK_PROVIDER_BUILDER.put(executingProfession, taskListProvider);
		
		// Protection in case multiple people try to modify the same Profession
		if (old != null)
			VILLAGER_TASK_PROVIDER_BUILDER.put(executingProfession, combineTaskLists(old, taskListProvider));
	}
	
	protected static ImmutableMap<VillagerProfession, VillagerTaskProvider> getCompletedMap()
	{
		return ImmutableMap.copyOf(VILLAGER_TASK_PROVIDER_BUILDER);
	}
	
	
	@Contract("_, _ -> new")
	private static VillagerTaskProvider combineTaskLists(VillagerTaskProvider a, VillagerTaskProvider b)
	{
		final VillagerTaskProvider out = new VillagerTaskProvider();
		
		combineConstantTasks(out, a, b);
		combineRandomTasks(out, a, b);
		
		return out;
	}
	
	@Contract(mutates="param1")
	private static void combineConstantTasks(final VillagerTaskProvider out, final VillagerTaskProvider a, final VillagerTaskProvider b)
	{
		for (VillagerTaskProvider.TaskType taskType : VillagerTaskProvider.TaskType.values())
		{
			List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> l = new ArrayList<>();
			l.addAll(a.getRawConstantTasks(taskType));
			l.addAll(b.getRawConstantTasks(taskType));
			out.addConstantTasks(taskType, l);
		}
	}
	
	@Contract(mutates="param1")
	private static void combineRandomTasks(final VillagerTaskProvider out, final VillagerTaskProvider a, final VillagerTaskProvider b)
	{
		for (VillagerTaskProvider.TaskType taskType : VillagerTaskProvider.TaskType.values())
		{
			List<BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>> l = new ArrayList<>();
			l.addAll(a.getRawRandomTasks(taskType));
			l.addAll(b.getRawRandomTasks(taskType));
			out.addRandomTasks(taskType, l);
		}
	}
}
