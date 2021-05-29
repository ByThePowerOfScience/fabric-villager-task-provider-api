package com.bythepowerofscience.taskapi.api;

import com.bythepowerofscience.taskapi.impl.VillagerTaskProvider;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;

import java.util.List;
import java.util.Map;

public class BackendVillagerTaskRetriever {
	
	private static final Map<VillagerProfession, VillagerTaskProvider> villagerTaskProviderMap;
	
	private static final VillagerTaskProvider defaultTaskProvider = new VillagerTaskProvider();
	
	private static VillagerTaskProvider getTaskProvider(VillagerProfession profession)
	{
		VillagerTaskProvider p = villagerTaskProviderMap.get(profession);
		if (p == null)
			return defaultTaskProvider;
		else
			return p;
	}
	
	static {
		villagerTaskProviderMap = VillagerTaskProviderRegistry.getCompletedMap().build();
	}
	
	
	
	public static ImmutableList<Pair<Integer,? extends Task<? super VillagerEntity>>> getCoreTasks(VillagerProfession profession, float f) {
		ImmutableList.Builder<Pair<Integer,? extends Task<? super VillagerEntity>>> b = ImmutableList.builder();
		b.addAll(VillagerTaskProvider.Base.getCoreTasks(profession, f));
		
		List<Pair<Integer,? extends Task<? super VillagerEntity>>> l = getTaskProvider(profession).getCustomCoreTasks(profession, f);
		
		if (l != null)
			b.addAll(l);
		
		return b.build();
	}
	
	public static ImmutableList<Pair<Integer,? extends Task<? super VillagerEntity>>> getWorkTasks(VillagerProfession profession, float f) {
		ImmutableList.Builder<Pair<Integer,? extends Task<? super VillagerEntity>>> b = ImmutableList.builder();
		b.addAll(VillagerTaskProvider.Base.getWorkTasks(profession, f));
		
		List<Pair<Integer,? extends Task<? super VillagerEntity>>> l = getTaskProvider(profession).getCustomWorkTasks(profession, f);
		
		if (l != null)
			b.addAll(l);
		
		return b.build();
	}
	
	public static ImmutableList<Pair<Integer,? extends Task<? super VillagerEntity>>> getMeetTasks(VillagerProfession profession, float f) {
		ImmutableList.Builder<Pair<Integer,? extends Task<? super VillagerEntity>>> b = ImmutableList.builder();
		b.addAll(VillagerTaskProvider.Base.getMeetTasks(profession, f));
		
		List<Pair<Integer,? extends Task<? super VillagerEntity>>> l = getTaskProvider(profession).getCustomMeetTasks(profession, f);
		
		if (l != null)
			b.addAll(l);
		
		return b.build();
	}
	
	public static ImmutableList<Pair<Integer,? extends Task<? super VillagerEntity>>> getRestTasks(VillagerProfession profession, float f) {
		ImmutableList.Builder<Pair<Integer,? extends Task<? super VillagerEntity>>> b = ImmutableList.builder();
		b.addAll(VillagerTaskProvider.Base.getRestTasks(profession, f));
		
		List<Pair<Integer,? extends Task<? super VillagerEntity>>> l = getTaskProvider(profession).getCustomRestTasks(profession, f);
		
		if (l != null)
			b.addAll(l);
		
		return b.build();
	}
	
	public static ImmutableList<Pair<Integer,? extends Task<? super VillagerEntity>>> getIdleTasks(VillagerProfession profession, float f) {
		ImmutableList.Builder<Pair<Integer,? extends Task<? super VillagerEntity>>> b = ImmutableList.builder();
		b.addAll(VillagerTaskProvider.Base.getIdleTasks(profession, f));
		
		List<Pair<Integer,? extends Task<? super VillagerEntity>>> l = getTaskProvider(profession).getCustomIdleTasks(profession, f);
		
		if (l != null)
			b.addAll(l);
		
		return b.build();
	}
	
	public static ImmutableList<Pair<Integer,? extends Task<? super VillagerEntity>>> getPanicTasks(VillagerProfession profession, float f) {
		ImmutableList.Builder<Pair<Integer,? extends Task<? super VillagerEntity>>> b = ImmutableList.builder();
		b.addAll(VillagerTaskProvider.Base.getPanicTasks(profession, f));
		
		List<Pair<Integer,? extends Task<? super VillagerEntity>>> l = getTaskProvider(profession).getCustomPanicTasks(profession, f);
		
		if (l != null)
			b.addAll(l);
		
		return b.build();
	}
	
	public static ImmutableList<Pair<Integer,? extends Task<? super VillagerEntity>>> getPreRaidTasks(VillagerProfession profession, float f) {
		ImmutableList.Builder<Pair<Integer,? extends Task<? super VillagerEntity>>> b = ImmutableList.builder();
		b.addAll(VillagerTaskProvider.Base.getPreRaidTasks(profession, f));
		
		List<Pair<Integer,? extends Task<? super VillagerEntity>>> l = getTaskProvider(profession).getCustomPreRaidTasks(profession, f);
		
		if (l != null)
			b.addAll(l);
		
		return b.build();
	}
	
	public static ImmutableList<Pair<Integer,? extends Task<? super VillagerEntity>>> getRaidTasks(VillagerProfession profession, float f) {
		ImmutableList.Builder<Pair<Integer,? extends Task<? super VillagerEntity>>> b = ImmutableList.builder();
		b.addAll(VillagerTaskProvider.Base.getRaidTasks(profession, f));
		
		List<Pair<Integer,? extends Task<? super VillagerEntity>>> l = getTaskProvider(profession).getCustomRaidTasks(profession, f);
		
		if (l != null)
			b.addAll(l);
		
		return b.build();
	}
	
	public static ImmutableList<Pair<Integer,? extends Task<? super VillagerEntity>>> getHideTasks(VillagerProfession profession, float f) {
		ImmutableList.Builder<Pair<Integer,? extends Task<? super VillagerEntity>>> b = ImmutableList.builder();
		b.addAll(VillagerTaskProvider.Base.getHideTasks(profession, f));
		
		List<Pair<Integer,? extends Task<? super VillagerEntity>>> l = getTaskProvider(profession).getCustomHideTasks(profession, f);
		
		if (l != null)
			b.addAll(l);
		
		return b.build();
	}
	
	public static ImmutableList<Pair<Integer,? extends Task<? super VillagerEntity>>> getPlayTasks(float f) {
		return ImmutableList.copyOf(VillagerTaskProvider.Base.getPlayTasks(f));
	}
}
