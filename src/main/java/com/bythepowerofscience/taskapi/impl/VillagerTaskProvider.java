package com.bythepowerofscience.taskapi.impl;

import com.bythepowerofscience.taskapi.api.VillagerTaskProviderRegistry;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Provides task lists for villager professions. Called when initializing a {@code VillagerEntity}'s {@link net.minecraft.entity.ai.brain.Brain}. Must be registered with {@link VillagerTaskProviderRegistry#addTaskProvider} before it can be called.
 * @apiNote All custom tasks are appended onto the universal tasks, which are stored in {@link Base}.
 * @see VillagerTaskListProvider Vanilla Reference
 * @see Task Task&lt;VillagerEntity&gt;
 */
@SuppressWarnings({"ALL"})
public class VillagerTaskProvider {
	
	/**
	 * A helper class for building custom {@code VillagerTaskProvider}s, allowing the addition of tasks without requiring one to provide a full subclass implementation.
	 * @apiNote Implementations that affect multiple types of task may prefer to subclass {@link VillagerTaskProvider} for optimization purposes, as initializing large dynamic task-lists requires more time.
	 */
	public static final class Builder
	{
		private List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>>[] tasksArr;
		private static final int CORE = 0;
		private static final int WORK = 1;
		private static final int MEET = 2;
		private static final int IDLE = 3;
		private static final int REST = 4;
		private static final int PANIC = 5;
		private static final int PRERAID = 6;
		private static final int RAID = 7;
		private static final int HIDE = 8;
		
		/**
		 * Creates a new builder instance for {@link VillagerTaskProvider}s.
		 * @return A new {@code Builder} instance.
		 */
		public static final Builder builder()
		{
			return new Builder();
		}
		
		/**
		 * Adds a <b>Core</b> task to this {@link VillagerProfession}'s task-list.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addCoreTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			return addCoreTask((p, f) -> task);
		}
		
		/**
		 * Adds a <b>Core</b> task to this {@link VillagerProfession}'s task-list.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addCoreTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (tasksArr[CORE] == null)
				tasksArr[CORE] = Lists.newArrayList();
			
			tasksArr[CORE].add(dynamicTask);
			
			return this;
		}
		
		/**
		 * Adds a <b>Work</b> task to this {@link VillagerProfession}'s task-list.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addWorkTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			return addWorkTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>Work</b> task to this {@link VillagerProfession}'s task-list.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addWorkTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (tasksArr[WORK] == null)
				tasksArr[WORK] = Lists.newArrayList();
			
			tasksArr[WORK].add(dynamicTask);
			
			return this;
		}
		
		/**
		 * Adds a <b>Rest</b> task to this {@link VillagerProfession}'s task-list. <b>Rest</b> tasks are executed when night begins.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addRestTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			return addRestTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>Rest</b> task to this {@link VillagerProfession}'s task-list. <b>Rest</b> tasks are executed when night begins.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addRestTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (tasksArr[REST] == null)
				tasksArr[REST] = Lists.newArrayList();
			
			tasksArr[REST].add(dynamicTask);
			
			return this;
		}
		
		/**
		 * Adds a <b>Meet</b> task to this {@link VillagerProfession}'s task-list. <b>Meet</b> tasks are executed when one villager decides to meet with another, or when a trade is initiated with the player.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addMeetTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			return addMeetTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>Meet</b> task to this {@link VillagerProfession}'s task-list. <b>Meet</b> tasks are executed when one villager decides to meet with another, or when a trade is initiated with the player.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addMeetTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (tasksArr[MEET] == null)
				tasksArr[MEET] = Lists.newArrayList();
			
			tasksArr[MEET].add(dynamicTask);
			
			return this;
		}
		
		/**
		 * Adds an <b>Idle</b> task to this {@link VillagerProfession}'s task-list. <b>Idle</b> tasks are executed when a villager is not performing any other action.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addIdleTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			return addIdleTask((profession, f) -> task);
		}
		
		/**
		 * Adds an <b>Idle</b> task to this {@link VillagerProfession}'s task-list. <b>Idle</b> tasks are executed when a villager is not performing any other action.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addIdleTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (tasksArr[IDLE] == null)
				tasksArr[IDLE] = Lists.newArrayList();
			
			tasksArr[IDLE].add(dynamicTask);
			
			return this;
		}
		
		/**
		 * Adds a <b>Panic</b> task to this {@link VillagerProfession}'s task-list. <b>Panic</b> tasks are executed when a villager is damaged.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addPanicTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			return addPanicTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>Panic</b> task to this {@link VillagerProfession}'s task-list. <b>Panic</b> tasks are executed when a villager is damaged.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addPanicTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (tasksArr[PANIC] == null)
				tasksArr[PANIC] = Lists.newArrayList();
			
			tasksArr[PANIC].add(dynamicTask);
			
			return this;
		}
		
		/**
		 * Adds a <b>PreRaid</b> task to this {@link VillagerProfession}'s task-list. <b>PreRaid</b> tasks are executed during the startup of an Illager {@link net.minecraft.village.raid.Raid}<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addPreRaidTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			return addPreRaidTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>PreRaid</b> task to this {@link VillagerProfession}'s task-list. <b>PreRaid</b> tasks are executed during the startup of an Illager {@link net.minecraft.village.raid.Raid}<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addPreRaidTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (tasksArr[PRERAID] == null)
				tasksArr[PRERAID] = Lists.newArrayList();
			
			tasksArr[PRERAID].add(dynamicTask);
			
			return this;
		}
		
		/**
		 * Adds a <b>Raid</b> task to this {@link VillagerProfession}'s task-list. <b>Raid</b> tasks are executed during an Illager {@link net.minecraft.village.raid.Raid}<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addRaidTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			return addRaidTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>Raid</b> task to this {@link VillagerProfession}'s task-list. <b>Raid</b> tasks are executed during an Illager {@link net.minecraft.village.raid.Raid}<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addRaidTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (tasksArr[RAID] == null)
				tasksArr[RAID] = Lists.newArrayList();
			
			tasksArr[RAID].add(dynamicTask);
			
			return this;
		}
		
		/**
		 * Adds a <b>Hide</b> task to this {@link VillagerProfession}'s task-list. <b>Hide</b> tasks are executed when a bell is rung.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addHideTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			return addHideTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>Hide</b> task to this {@link VillagerProfession}'s task-list. <b>Hide</b> tasks are executed when a bell is rung.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addHideTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (tasksArr[HIDE] == null)
				tasksArr[HIDE] = Lists.newArrayList();
			
			tasksArr[HIDE].add(dynamicTask);
			
			return this;
		}
		
		/**
		 * Builds the {@link VillagerTaskProvider} instance with the given inputs.
		 * @return VillagerTaskProvider
		 */
		public final VillagerTaskProvider build()
		{
			for (List l : tasksArr)
			{
				if (Objects.nonNull(l))
					return makeInstance();
			}
			
			throw new AssertionError("Must provide villager tasks to Builder. For professions without custom tasks, no VillagerTaskProvider is needed.");
		}
		
		private VillagerTaskProvider makeInstance() {
			return new VillagerTaskProvider() {
				//TODO figure out if a weak reference is preferred to avoid a memory leak? would this even cause a memory leak?
				private List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>>[] tasksArr = Builder.this.tasksArr;
				//private WeakReference<List[]> tasksArr = new WeakReference<>(VillagerTaskProvider.Builder.this.tasksArr);
				
				@Override
				public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomCoreTasks(final VillagerProfession villagerProfession, final float speed)
				{
					if (Objects.isNull(tasksArr[CORE]))
						return null;
					
					return ImmutableList.copyOf(applyToTasks(tasksArr[CORE], villagerProfession, speed));
				}
				
				@Override
				public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomWorkTasks(final VillagerProfession villagerProfession, final float speed)
				{
					if (Objects.isNull(tasksArr[WORK]))
						return null;
					
					return ImmutableList.copyOf(applyToTasks(tasksArr[WORK], villagerProfession, speed));
				}
				
				@Override
				public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomRestTasks(final VillagerProfession villagerProfession, final float speed)
				{
					if (Objects.isNull(tasksArr[REST]))
						return null;
					
					return ImmutableList.copyOf(applyToTasks(tasksArr[REST], villagerProfession, speed));
				}
				
				@Override
				public final ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomMeetTasks(final VillagerProfession villagerProfession, final float speed)
				{
					if (Objects.isNull(tasksArr[MEET]))
						return null;
					
					return ImmutableList.copyOf(applyToTasks(tasksArr[MEET], villagerProfession, speed));
				}
				
				@Override
				public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomIdleTasks(final VillagerProfession villagerProfession, final float speed)
				{
					if (Objects.isNull(tasksArr[IDLE]))
						return null;
					
					return ImmutableList.copyOf(applyToTasks(tasksArr[HIDE], villagerProfession, speed));
				}
				
				@Override
				public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomPanicTasks(final VillagerProfession villagerProfession, final float speed)
				{
					if (Objects.isNull(tasksArr[PANIC]))
						return null;
					
					return ImmutableList.copyOf(applyToTasks(tasksArr[PANIC], villagerProfession, speed));
				}
				
				@Override
				public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomPreRaidTasks(final VillagerProfession villagerProfession, final float speed)
				{
					if (Objects.isNull(tasksArr[PRERAID]))
						return null;
					
					return ImmutableList.copyOf(applyToTasks(tasksArr[PRERAID], villagerProfession, speed));
				}
				
				@Override
				public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomRaidTasks(final VillagerProfession villagerProfession, final float speed)
				{
					if (Objects.isNull(tasksArr[RAID]))
						return null;
					
					return ImmutableList.copyOf(applyToTasks(tasksArr[RAID], villagerProfession, speed));
				}
				
				@Override
				public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomHideTasks(final VillagerProfession villagerProfession, final float speed)
				{
					if (Objects.isNull(tasksArr[HIDE]))
						return null;
					
					return ImmutableList.copyOf(applyToTasks(tasksArr[HIDE], villagerProfession, speed));
				}
			};
		}
		
		
		
		
		
		
		
		private Builder() {
			tasksArr = new List[9];
		}
	}
	
	
	/**
	 * Generates {@link VillagerProfession}'s custom <b>Core</b> task-list.<p>
	 * @param profession The profession of the {@code VillagerEntity} executing this task.
	 * @param speed The speed of the villager. {@code 0.5F} by default.
	 * @apiNote All custom tasks, regardless of profession, have the list of {@link Base} tasks appended before being generated.
	 * @return {@code ImmutableList} of custom core tasks.
	 */
	public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomCoreTasks(VillagerProfession profession, float speed)
	{
		return null;
	}
	
	/**
	 * Generates {@link VillagerProfession}'s custom <b>Work</b> task-list.<p>
	 * @param profession The profession of the {@code VillagerEntity} executing this task.
	 * @param speed The speed of the villager. {@code 0.5F} by default.
	 * @apiNote All custom tasks, regardless of profession, have the list of {@link Base} tasks appended before being generated.
	 * @return {@code ImmutableList} of custom core tasks.
	 */
	public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomWorkTasks(VillagerProfession profession, float speed) {
		return null;
	}
	
	/**
	 * Generates {@link VillagerProfession}'s custom <b>Rest</b> task-list. <b>Rest</b> tasks are executed when night begins.
	 * @param profession The profession of the {@code VillagerEntity} executing this task.
	 * @param speed The speed of the villager. {@code 0.5F} by default.
	 * @apiNote All custom tasks, regardless of profession, have the list of {@link Base} tasks appended before being generated.
	 * @return {@code ImmutableList} of custom core tasks.
	 */
	public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomRestTasks(VillagerProfession profession, float speed) {
		return null;
	}
	
	/**
	 * Generates {@link VillagerProfession}'s custom <b>Meet</b> task-list. <b>Meet</b> tasks are executed when one villager decides to meet with another, or when a trade is initiated with the player.
	 * @param profession The profession of the {@code VillagerEntity} executing this task.
	 * @param speed The speed of the villager. {@code 0.5F} by default.
	 * @apiNote All custom tasks, regardless of profession, have the list of {@link Base} tasks appended before being generated.
	 * @return {@code ImmutableList} of custom core tasks.
	 */
	public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomMeetTasks(VillagerProfession profession, float speed) {
		return null;
	}
	
	/**
	 * Generates {@link VillagerProfession}'s custom <b>Idle</b> task-list. <b>Idle</b> tasks are executed when a villager is not performing any other action.
	 * @param profession The profession of the {@code VillagerEntity} executing this task.
	 * @param speed The speed of the villager. {@code 0.5F} by default.
	 * @apiNote All custom tasks, regardless of profession, have the list of {@link Base} tasks appended before being generated.
	 * @return {@code ImmutableList} of custom core tasks.
	 */
	public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomIdleTasks(VillagerProfession profession, float speed) {
		return null;
	}
	
	/**
	 * Generates {@link VillagerProfession}'s custom <b>Panic</b> task-list. <b>Panic</b> tasks are executed when a villager is damaged.
	 * @param profession The profession of the {@code VillagerEntity} executing this task.
	 * @param speed The speed of the villager. {@code 0.5F} by default.
	 * @apiNote All custom tasks, regardless of profession, have the list of {@link Base} tasks appended before being generated.
	 * @return {@code ImmutableList} of custom core tasks.
	 */
	public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomPanicTasks(VillagerProfession profession, float speed) {
		return null;
	}
	
	/**
	 * Generates {@link VillagerProfession}'s custom <b>PreRaid</b> task-list. <b>PreRaid</b> tasks are executed during the startup of an Illager {@link net.minecraft.village.raid.Raid}.
	 * @param villagerProfession The profession of the {@code VillagerEntity} executing this task.
	 * @param speed The speed of the villager. {@code 0.5F} by default.
	 * @apiNote All custom tasks, regardless of profession, have the list of {@link Base} tasks appended before being generated.
	 * @return {@code ImmutableList} of custom core tasks.
	 */
	public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomPreRaidTasks(VillagerProfession villagerProfession, float speed) {
		return null;
	}
	
	/**
	 * Generates {@link VillagerProfession}'s custom <b>Raid</b> task-list. <b>Raid</b> tasks are executed during an Illager {@link net.minecraft.village.raid.Raid}.
	 * @param villagerProfession The profession of the {@code VillagerEntity} executing this task.
	 * @param speed The speed of the villager. {@code 0.5F} by default.
	 * @apiNote All custom tasks, regardless of profession, have the list of {@link Base} tasks appended before being generated.
	 * @return {@code ImmutableList} of custom core tasks.
	 */
	public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomRaidTasks(VillagerProfession villagerProfession, float speed) {
		return null;
	}
	
	/**
	 * Generates {@link VillagerProfession}'s custom <b>Hide</b> task-list. <b>Hide</b> tasks are executed when a bell is rung.
	 * @param villagerProfession The profession of the {@code VillagerEntity} executing this task.
	 * @param speed The speed of the villager. {@code 0.5F} by default.
	 * @apiNote All custom tasks, regardless of profession, have the list of {@link Base} tasks appended before being generated.
	 * @return {@code ImmutableList} of custom core tasks.
	 */
	public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomHideTasks(VillagerProfession villagerProfession, float speed) {
		return null;
	}
	
	
	/**
	 * Contains the tasks that are executed for <i>all</i> villagers, regardless of their {@link VillagerProfession}.  To add custom tasks for specific professions, use {@link VillagerTaskProviderRegistry#addTaskProvider}.
	 * @apiNote <b>Play</b> tasks are adjusted here, as baby villagers have no profession.
	 */
	public static final class Base {
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> coreTasks = new ArrayList<>();
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> workTasks = new ArrayList<>();
		
		private static List<Function<Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> playTasks = new ArrayList<>();
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> restTasks = new ArrayList<>();
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> meetTasks = new ArrayList<>();
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> idleTasks = new ArrayList<>();
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> panicTasks = new ArrayList<>();
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> preRaidTasks = new ArrayList<>();
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> raidTasks = new ArrayList<>();
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> hideTasks = new ArrayList<>();
		
		
		/**
		 * Adds a <b>Core</b> task to ALL villagers' task-lists.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 */
		public static void addCoreTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			addCoreTask((profession, f) -> task);
		}
		
		
		/**
		 * Adds a <b>Core</b> task to ALL villagers' task-lists.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 */
		public static void addCoreTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			coreTasks.add(dynamicTask);
		}
		
		
		
		
		/**
		 * Adds a <b>Work</b> task to ALL villagers' task-lists.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 */
		public static void addWorkTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			addWorkTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>Work</b> task to ALL villagers' task-lists.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 */
		public static void addWorkTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			workTasks.add(dynamicTask);
		}
		
		
		
		
		
		/**
		 * Adds a <b>Play</b> task to ALL villagers' task-lists.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 */
		public static void addPlayTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			addPlayTask((f) -> task);
		}
		
		/**
		 * Adds a <b>Play</b> task to ALL villagers' task-lists.<p>  <b>Play</b> tasks are executed by baby villagers while in their "play" state.
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code Function}, taking as an argument: the <b>speed</b> of the villager; returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 */
		public static void addPlayTask(Function<Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			playTasks.add(dynamicTask);
		}
		
		
		
		
		
		/**
		 * Adds a <b>Rest</b> task to ALL villagers' task-lists. <b>Rest</b> tasks are executed when night begins.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()));</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 */
		public static void addRestTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			addRestTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>Rest</b> task to ALL villagers' task-lists. <b>Rest</b> tasks are executed when night begins.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addRestTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			restTasks.add(dynamicTask);
		}
		
		
		
		
		
		/**
		 * Adds a <b>Meet</b> task to ALL villagers' task-lists. <b>Meet</b> tasks are executed when one villager decides to meet with another, or when a trade is initiated with the player.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 */
		public static void addMeetTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			addMeetTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>Meet</b> task to ALL villagers' task-lists. <b>Meet</b> tasks are executed when one villager decides to meet with another, or when a trade is initiated with the player.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addMeetTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			meetTasks.add(dynamicTask);
		}
		
		/**
		 * Adds an <b>Idle</b> task to ALL villagers' task-lists. <b>Idle</b> tasks are executed when a villager is not performing any other action.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 */
		public static void addIdleTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			addIdleTask((profession, f) -> task);
		}
		
		/**
		 * Adds an <b>Idle</b> task to ALL villagers' task-lists. <b>Idle</b> tasks are executed when a villager is not performing any other action.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addIdleTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			idleTasks.add(dynamicTask);
		}
		
		/**
		 * Adds a <b>Panic</b> task to ALL villagers' task-lists. <b>Panic</b> tasks are executed when a villager is damaged.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 */
		public static void addPanicTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			addPanicTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>Panic</b> task to ALL villagers' task-lists. <b>Panic</b> tasks are executed when a villager is damaged.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addPanicTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			panicTasks.add(dynamicTask);
		}
		
		/**
		 * Adds a <b>PreRaid</b> task to ALL villagers' task-lists. <b>PreRaid</b> tasks are executed during the startup of an Illager {@link net.minecraft.village.raid.Raid}<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 */
		public static void addPreRaidTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			addPreRaidTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>PreRaid</b> task to ALL villagers' task-lists. <b>PreRaid</b> tasks are executed during the startup of an Illager {@link net.minecraft.village.raid.Raid}<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addPreRaidTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			preRaidTasks.add(dynamicTask);
		}
		
		
		/**
		 * Adds a <b>Raid</b> task to ALL villagers' task-lists. <b>Raid</b> tasks are executed during an Illager {@link net.minecraft.village.raid.Raid}<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 */
		public static void addRaidTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			addRaidTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>Raid</b> task to ALL villagers' task-lists. <b>Raid</b> tasks are executed during an Illager {@link net.minecraft.village.raid.Raid}<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addRaidTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			raidTasks.add(dynamicTask);
		}
		
		
		/**
		 * Adds a <b>Hide</b> task to ALL villagers' task-lists. <b>Hide</b> tasks are executed when a bell is rung.<p>
		 * Example Usage:<blockquote><pre>addCoreTask(Pair.of(5, new VillagerWorkTask()))</pre></blockquote>
		 * @param task A {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be executed.
		 */
		public static void addHideTask(Pair<Integer, ? extends Task<? super VillagerEntity>> task)
		{
			addHideTask((profession, f) -> task);
		}
		
		/**
		 * Adds a <b>Hide</b> task to ALL villagers' task-lists. <b>Hide</b> tasks are executed when a bell is rung.<p>
		 * Example Usage:
		 * <blockquote><pre>addCoreTask((profession, speed) -&gt; Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addHideTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			hideTasks.add(dynamicTask);
		}
		
		
		
		
		
		
		
		
		
		
		
		/**
		 * API RETRIEVAL METHOD
		 */
		
		
		/**
		 * Do not use.
		 */
		public static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCoreTasks(VillagerProfession p, float f)
		{
			List<Pair<Integer, ? extends Task<? super VillagerEntity>>> out = new ArrayList<>();
			out.addAll(VillagerTaskListProvider.createCoreTasks(p, f));
			out.addAll(applyToTasks(coreTasks, p, f));
			return out;
		}
		
		/**
		 * Do not use.
		 */
		public static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getWorkTasks(VillagerProfession p, float f)
		{
			List<Pair<Integer, ? extends Task<? super VillagerEntity>>> out = new ArrayList<>();
			out.addAll(VillagerTaskListProvider.createWorkTasks(p, f));
			out.addAll(applyToTasks(workTasks, p, f));
			return out;
		}
		
		/**
		 * Do not use.
		 */
		public static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getPlayTasks(float f)
		{
			List<Pair<Integer, ? extends Task<? super VillagerEntity>>> out = new ArrayList<>();
			out.addAll(VillagerTaskListProvider.createPlayTasks(f));
			out.addAll(playTasks.stream().map(func -> func.apply(f)).collect(Collectors.toList()));
			return out;
		}
		
		/**
		 * Do not use.
		 */
		public static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getRestTasks(VillagerProfession p, float f)
		{
			List<Pair<Integer, ? extends Task<? super VillagerEntity>>> out = new ArrayList<>();
			out.addAll(VillagerTaskListProvider.createRestTasks(p, f));
			out.addAll(applyToTasks(restTasks, p, f));
			return out;
		}
		
		/**
		 * Do not use.
		 */
		public static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getMeetTasks(VillagerProfession p, float f)
		{
			List<Pair<Integer, ? extends Task<? super VillagerEntity>>> out = new ArrayList<>();
			out.addAll(VillagerTaskListProvider.createMeetTasks(p, f));
			out.addAll(applyToTasks(meetTasks, p, f));
			return out;
		}
		
		/**
		 * Do not use.
		 */
		public static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getIdleTasks(VillagerProfession p, float f)
		{
			List<Pair<Integer, ? extends Task<? super VillagerEntity>>> out = new ArrayList<>();
			out.addAll(VillagerTaskListProvider.createIdleTasks(p, f));
			out.addAll(applyToTasks(idleTasks, p, f));
			return out;
		}
		
		/**
		 * Do not use.
		 */
		public static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getPanicTasks(VillagerProfession p, float f)
		{
			List<Pair<Integer, ? extends Task<? super VillagerEntity>>> out = new ArrayList<>();
			out.addAll(VillagerTaskListProvider.createPanicTasks(p, f));
			out.addAll(applyToTasks(panicTasks, p, f));
			return out;
		}
		
		/**
		 * Do not use.
		 */
		public static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getPreRaidTasks(VillagerProfession p, float f)
		{
			List<Pair<Integer, ? extends Task<? super VillagerEntity>>> out = new ArrayList<>();
			out.addAll(VillagerTaskListProvider.createPreRaidTasks(p, f));
			out.addAll(applyToTasks(preRaidTasks, p, f));
			return out;
		}
		
		
		/**
		 * Do not use.
		 */
		public static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getRaidTasks(VillagerProfession p, float f)
		{
			List<Pair<Integer, ? extends Task<? super VillagerEntity>>> out = new ArrayList<>();
			out.addAll(VillagerTaskListProvider.createRaidTasks(p, f));
			out.addAll(applyToTasks(raidTasks, p, f));
			return out;
		}
		
		
		/**
		 * Do not use.
		 */
		public static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getHideTasks(VillagerProfession p, float f)
		{
			List<Pair<Integer, ? extends Task<? super VillagerEntity>>> out = new ArrayList<>();
			out.addAll(VillagerTaskListProvider.createHideTasks(p, f));
			out.addAll(applyToTasks(hideTasks, p, f));
			return out;
		}
		
		
		
	}
	
	
	private static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> applyToTasks(List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> l, VillagerProfession p, float f)
	{
		return l.stream().map(func -> func.apply(p, f)).collect(Collectors.toList());
	}
}
