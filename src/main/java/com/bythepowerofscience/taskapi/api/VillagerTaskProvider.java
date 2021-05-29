package com.bythepowerofscience.taskapi.api;

import com.bythepowerofscience.taskapi.example.BoneMealTask;
import com.bythepowerofscience.taskapi.example.FarmerVillagerTask;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Provides task lists for villager professions. Called when initializing a {@code VillagerEntity}'s {@link net.minecraft.entity.ai.brain.Brain}. Must be registered with {@link com.bythepowerofscience.task.VillagerTaskProviderRegistry#addTaskProvider} before it can be called.
 * @apiNote All custom tasks are appended onto the universal tasks, which are stored in {@link Base}.
 * @see VillagerTaskListProvider Vanilla Reference
 * @see com.bythepowerofscience.task.WorkerVillagerTask Example Task
 * @see Task Task&lt;VillagerEntity&gt;
 */
@SuppressWarnings({"ALL"})
public class VillagerTaskProvider {
	
	/**
	 * A helper class for building custom {@code VillagerTaskProvider}s, allowing the addition of tasks without requiring one to provide a full subclass implementation.
	 * @apiNote Implementations that affect multiple types of task may prefer to subclass {@link VillagerTaskProvider} for optimizational purposes, as initializing large dynamic task-lists requires more time.
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addRestTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> task)
		{
			if (tasksArr[REST] == null)
				tasksArr[REST] = Lists.newArrayList();
			
			tasksArr[REST].add(task);
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addMeetTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> task)
		{
			if (tasksArr[MEET] == null)
				tasksArr[MEET] = Lists.newArrayList();
			
			tasksArr[MEET].add(task);
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addIdleTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> task)
		{
			if (tasksArr[IDLE] == null)
				tasksArr[IDLE] = Lists.newArrayList();
			
			tasksArr[IDLE].add(task);
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addPanicTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> task)
		{
			if (tasksArr[PANIC] == null)
				tasksArr[PANIC] = Lists.newArrayList();
			
			tasksArr[PANIC].add(task);
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addPreRaidTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> task)
		{
			if (tasksArr[PRERAID] == null)
				tasksArr[PRERAID] = Lists.newArrayList();
			
			tasksArr[PRERAID].add(task);
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addRaidTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> task)
		{
			if (tasksArr[RAID] == null)
				tasksArr[RAID] = Lists.newArrayList();
			
			tasksArr[RAID].add(task);
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 * @return This {@code Builder} instance.
		 */
		public final Builder addHideTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> task)
		{
			if (tasksArr[HIDE] == null)
				tasksArr[HIDE] = Lists.newArrayList();
			
			tasksArr[HIDE].add(task);
			
			return this;
		}
		
		/**
		 * Builds the {@link VillagerTaskProvider} instance with the given 
		 * @return
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
				//TODO figure out if a weak reference is needed
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
	 * @param profession The profession of the {@code VillagerEntity} executing this task.
	 * @param speed The speed of the villager. {@code 0.5F} by default.
	 * @apiNote All custom tasks, regardless of profession, have the list of {@link Base} tasks appended before being generated.
	 * @return {@code ImmutableList} of custom core tasks.
	 */
	public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomPreRaidTasks(VillagerProfession villagerProfession, float speed) {
		return null;
	}
	
	/**
	 * Generates {@link VillagerProfession}'s custom <b>Raid</b> task-list. <b>Raid</b> tasks are executed during an Illager {@link net.minecraft.village.raid.Raid}.
	 * @param profession The profession of the {@code VillagerEntity} executing this task.
	 * @param speed The speed of the villager. {@code 0.5F} by default.
	 * @apiNote All custom tasks, regardless of profession, have the list of {@link Base} tasks appended before being generated.
	 * @return {@code ImmutableList} of custom core tasks.
	 */
	public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomRaidTasks(VillagerProfession villagerProfession, float speed) {
		return null;
	}
	
	/**
	 * Generates {@link VillagerProfession}'s custom <b>Hide</b> task-list. <b>Hide</b> tasks are executed when a bell is rung.
	 * @param profession The profession of the {@code VillagerEntity} executing this task.
	 * @param speed The speed of the villager. {@code 0.5F} by default.
	 * @apiNote All custom tasks, regardless of profession, have the list of {@link Base} tasks appended before being generated.
	 * @return {@code ImmutableList} of custom core tasks.
	 */
	public ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCustomHideTasks(VillagerProfession villagerProfession, float speed) {
		return null;
	}
	
	
	/**
	 * Contains the tasks that are executed for <i>all</i> villagers, regardless of their {@link VillagerProfession}.  To add custom tasks for specific professions, use {@link com.bythepowerofscience.task.VillagerTaskProviderRegistry#addTaskProvider}.
	 * @apiNote <b>Play</b> tasks are adjusted here, as baby villagers have no profession.
	 */
	public static final class Base {
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> coreTasks = null;
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> workTasks = null;
		
		private static List<Function<Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> playTasks = null;
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> restTasks = null;
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> meetTasks = null;
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> idleTasks = null;
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> panicTasks = null;
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> preRaidTasks = null;
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> raidTasks = null;
		
		private static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> hideTasks = null;
		
		
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 */
		public static void addCoreTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (coreTasks == null)
			{ 
				coreTasks = Lists.newArrayList(
						(profession, f) -> Pair.of(0, new StayAboveWaterTask(0.8F)),
						(profession, f) -> Pair.of(0, new OpenDoorsTask()),
						(profession, f) -> Pair.of(0, new LookAroundTask(45, 90)),
						(profession, f) -> Pair.of(0, new PanicTask()),
						(profession, f) -> Pair.of(0, new WakeUpTask()),
						(profession, f) -> Pair.of(0, new HideWhenBellRingsTask()),
						(profession, f) -> Pair.of(0, new StartRaidTask()),
						(profession, f) -> Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE)),
						(profession, f) -> Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.POTENTIAL_JOB_SITE)),
						(profession, f) -> Pair.of(1, new WanderAroundTask()),
						(profession, f) -> Pair.of(2, new WorkStationCompetitionTask(profession)),
						(profession, f) -> Pair.of(3, new FollowCustomerTask(f)),
						(profession, f) -> Pair.of(5, (Task<? super VillagerEntity>) new WalkToNearestVisibleWantedItemTask(f, false, 4)),
						(profession, f) -> Pair.of(6, new FindPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())),
						(profession, f) -> Pair.of(7, new WalkTowardJobSiteTask(f)),
						(profession, f) -> Pair.of(8, new TakeJobSiteTask(f)),
						(profession, f) -> Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME, false, Optional.of((byte)14))),
						(profession, f) -> Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14))),
						(profession, f) -> Pair.of(10, new GoToWorkTask()),
						(profession, f) -> Pair.of(10, new LoseJobOnSiteLossTask()));
			}
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 */
		public static void addWorkTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (workTasks == null)
			{
				workTasks = Lists.newArrayList(
					(profession, f) -> Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
							ImmutableList.of(
									Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
									Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
									Pair.of(new WaitTask(30, 60), 8)))),
					(profession, f) -> Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
							ImmutableList.of(
									Pair.of(profession == VillagerProfession.FARMER ? new FarmerWorkTask() : new VillagerWorkTask(), 7),
									Pair.of(new GoToIfNearbyTask(MemoryModuleType.JOB_SITE, 0.4F, 4), 2),
									Pair.of(new GoToNearbyPositionTask(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5),
									Pair.of(new GoToSecondaryPositionTask(MemoryModuleType.SECONDARY_JOB_SITE, f, 1, 6, MemoryModuleType.JOB_SITE), 5),
									Pair.of(new FarmerVillagerTask(), profession == VillagerProfession.FARMER ? 2 : 5),
									Pair.of(new BoneMealTask(), profession == VillagerProfession.FARMER ? 4 : 7)))),
					(profession, f) -> Pair.of(10, new HoldTradeOffersTask(400, 1600)),
					(profession, f) -> Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
					(profession, f) -> Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.JOB_SITE, f, 9, 100, 1200)),
					(profession, f) -> Pair.of(3, new GiveGiftsToHeroTask(100)),
					(profession, f) -> Pair.of(99, new ScheduleActivityTask()));
			}
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code Function}, taking as an argument: the <b>speed</b> of the villager; returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList}) and the <b>task</b> to be performed.
		 */
		public static void addPlayTask(Function<Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (playTasks == null)
			{
				playTasks = Lists.newArrayList(
						f -> Pair.of(0, new WanderAroundTask(80, 120)),
						f -> Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
								ImmutableList.of(
										Pair.of(new FollowMobTask(EntityType.CAT, 8.0F), 8),
										Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
										Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
										Pair.of(new FollowMobTask(SpawnGroup.CREATURE, 8.0F), 1),
										Pair.of(new FollowMobTask(SpawnGroup.WATER_CREATURE, 8.0F), 1),
										Pair.of(new FollowMobTask(SpawnGroup.WATER_AMBIENT, 8.0F), 1),
										Pair.of(new FollowMobTask(SpawnGroup.MONSTER, 8.0F), 1),
										Pair.of(new WaitTask(30, 60), 2)))),
						f -> Pair.of(5, new PlayWithVillagerBabiesTask()),
						f -> Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
								ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleState.VALUE_ABSENT),
								ImmutableList.of(
										Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 2),
										Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 1),
										Pair.of(new FindWalkTargetTask(f), 1),
										Pair.of(new GoTowardsLookTarget(f, 2), 1),
										Pair.of(new JumpInBedTask(f), 2),
										Pair.of(new WaitTask(20, 40), 2)))),
						f -> Pair.of(99, new ScheduleActivityTask())
				);
			}
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addRestTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (restTasks == null)
			{
				restTasks = Lists.newArrayList(
						(profession, f) -> Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.HOME, f, 1, 150, 1200)),
						(profession, f) -> Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME)),
						(profession, f) -> Pair.of(3, new SleepTask()),
						(profession, f) -> Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
								ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT),
								ImmutableList.of(
										Pair.of(new WalkHomeTask(f), 1),
										Pair.of(new WanderIndoorsTask(f), 4),
										Pair.of(new GoToPointOfInterestTask(f, 4), 2),
										Pair.of(new WaitTask(20, 40), 2)))),
						(profession, f) -> Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
								ImmutableList.of(
										Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
										Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
										Pair.of(new WaitTask(30, 60), 8)))),
						(profession, f) -> Pair.of(99, new ScheduleActivityTask()));
			}
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addMeetTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (meetTasks == null)
			{
				meetTasks = Lists.newArrayList(
						(profession, f) -> Pair.of(2, (Task<? super VillagerEntity>) new RandomTask(
								ImmutableList.of(
										Pair.of(new GoToIfNearbyTask(MemoryModuleType.MEETING_POINT, 0.4F, 40), 2),
										Pair.of(new MeetVillagerTask(), 2)))),
						(profession, f) -> Pair.of(10, new HoldTradeOffersTask(400, 1600)),
						(profession, f) -> Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
						(profession, f) -> Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, f, 6, 100, 200)),
						(profession, f) -> Pair.of(3, new GiveGiftsToHeroTask(100)),
						(profession, f) -> Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT)),
						(profession, f) -> Pair.of(3, (Task<? super VillagerEntity>) new CompositeTask(
								ImmutableMap.of(),
								ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
								CompositeTask.Order.ORDERED,
								CompositeTask.RunMode.RUN_ONE,
								ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1)))),
						(profession, f) -> Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
								ImmutableList.of(
										Pair.of(new FollowMobTask(EntityType.CAT, 8.0F), 8),
										Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
										Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
										Pair.of(new FollowMobTask(SpawnGroup.CREATURE, 8.0F), 1),
										Pair.of(new FollowMobTask(SpawnGroup.WATER_CREATURE, 8.0F), 1),
										Pair.of(new FollowMobTask(SpawnGroup.WATER_AMBIENT, 8.0F), 1),
										Pair.of(new FollowMobTask(SpawnGroup.MONSTER, 8.0F), 1),
										Pair.of(new WaitTask(30, 60), 2)))),
						(profession, f) -> Pair.of(99, new ScheduleActivityTask()));
			}
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addIdleTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (idleTasks == null)
			{
				idleTasks = Lists.newArrayList(
						(profession, f) -> {
							Predicate<PassiveEntity> isReadyToBreed = PassiveEntity::isReadyToBreed;
							return Pair.of(2, (Task<? super VillagerEntity>) new RandomTask(
									ImmutableList.of(
											Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 2),
											Pair.of(new FindEntityTask(EntityType.VILLAGER, 8, isReadyToBreed, isReadyToBreed, MemoryModuleType.BREED_TARGET, f, 2), 1),
											Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 1),
											Pair.of(new FindWalkTargetTask(f), 1),
											Pair.of(new GoTowardsLookTarget(f, 2), 1),
											Pair.of(new JumpInBedTask(f), 1),
											Pair.of(new WaitTask(30, 60), 1))));
						},
						(profession, f) -> Pair.of(3, new GiveGiftsToHeroTask(100)),
						(profession, f) -> Pair.of(3, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
						(profession, f) -> Pair.of(3, new HoldTradeOffersTask(400, 1600)),
						(profession, f) -> Pair.of(3, (Task<? super VillagerEntity>) new CompositeTask(
								ImmutableMap.of(),
								ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
								CompositeTask.Order.ORDERED,
								CompositeTask.RunMode.RUN_ONE,
								ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1)))),
						(profession, f) -> Pair.of(3, (Task<? super VillagerEntity>) new CompositeTask(
								ImmutableMap.of(),
								ImmutableSet.of(MemoryModuleType.BREED_TARGET),
								CompositeTask.Order.ORDERED,
								CompositeTask.RunMode.RUN_ONE,
								ImmutableList.of(Pair.of(new VillagerBreedTask(), 1)))),
						(profession, f) -> Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
								ImmutableList.of(
										Pair.of(new FollowMobTask(EntityType.CAT, 8.0F), 8),
										Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
										Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
										Pair.of(new FollowMobTask(SpawnGroup.CREATURE, 8.0F), 1),
										Pair.of(new FollowMobTask(SpawnGroup.WATER_CREATURE, 8.0F), 1),
										Pair.of(new FollowMobTask(SpawnGroup.WATER_AMBIENT, 8.0F), 1),
										Pair.of(new FollowMobTask(SpawnGroup.MONSTER, 8.0F), 1),
										Pair.of(new WaitTask(30, 60), 2)))),
						(profession, f) -> Pair.of(99, new ScheduleActivityTask()));
			}
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addPanicTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (panicTasks == null)
			{
				panicTasks = Lists.newArrayList(
						(profession, f) -> Pair.of(0, new StopPanickingTask()),
						(profession, f) -> Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_HOSTILE, f * 1.5F, 6, false)),
						(profession, f) -> Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.HURT_BY_ENTITY, f * 1.5F, 6, false)),
						(profession, f) -> Pair.of(3, new FindWalkTargetTask(f * 1.5F, 2, 2)),
						(profession, f) -> Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
															ImmutableList.of(
																	Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
																	Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
																	Pair.of(new WaitTask(30, 60), 8)))));
			}
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addPreRaidTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (preRaidTasks == null)
			{
				preRaidTasks = Lists.newArrayList(
						(profession, f) -> Pair.of(0, new RingBellTask()),
						(profession, f) -> Pair.of(0, (Task<? super VillagerEntity>) new RandomTask(
								ImmutableList.of(
										Pair.of(new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, f * 1.5F, 2, 150, 200), 6),
										Pair.of(new FindWalkTargetTask(f * 1.5F), 2)))),
						(profession, f) -> Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
								ImmutableList.of(
										Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
										Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
										Pair.of(new WaitTask(30, 60), 8)))),
						(profession, f) -> Pair.of(99, new EndRaidTask())
				);
			}
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addRaidTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (raidTasks == null)
			{
				raidTasks = Lists.newArrayList(
						(profession, f) -> Pair.of(0, (Task<? super VillagerEntity>) new RandomTask(
								ImmutableList.of(
										Pair.of(new SeekSkyAfterRaidWinTask(f), 5),
										Pair.of(new RunAroundAfterRaidTask(f * 1.1F), 2)))),
						(profession, f) -> Pair.of(0, new CelebrateRaidWinTask(600, 600)),
						(profession, f) -> Pair.of(2, new HideInHomeDuringRaidTask(24, f * 1.4F)),
						(profession, f) -> Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
								ImmutableList.of(
										Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
										Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
										Pair.of(new WaitTask(30, 60), 8)))),
						(profession, f) -> Pair.of(99, new EndRaidTask())
				);
			}
			
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
		 * <blockquote><pre>addCoreTask((profession, speed) -> Pair.of(5, new FarmerVillagerTask(profession, speed)));</pre></blockquote>
		 * @apiNote Used for tasks that require the <b>villager speed</b> or <b>villager profession</b> as arguments, e.g. moving the villager to a location.
		 * @param dynamicTask {@code BiFunction}, taking as arguments: the <b>profession</b> of the villager attempting to execute the task, the <b>speed</b> of the villager ({@code 0.5F} by default); returns: a {@code Pair} containing the <b>weight</b> of the task (as used in {@link net.minecraft.util.collection.WeightedList} and the <b>task</b> to be performed.
		 */
		public static void addHideTask(BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
		{
			if (hideTasks == null)
			{
				hideTasks = Lists.newArrayList(
						(profession, f) -> Pair.of(0, new ForgetBellRingTask(15, 3)),
						(profession, f) -> Pair.of(1, new HideInHomeTask(32, f * 1.25F, 2)),
						(profession, f) -> Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
								ImmutableList.of(
										Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
										Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
										Pair.of(new WaitTask(30, 60), 8))))
				);
			}
			
			hideTasks.add(dynamicTask);
		}
		
		
		
		
		/**
		 * Used by the API to retrieve tasks
		 */
		protected static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getCoreTasks(VillagerProfession p, float f)
		{
			if (coreTasks != null)
				return applyToTasks(coreTasks, p, f);
			else
				return getDefaultCoreTasks(p, f);
		}
		
		private static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getDefaultCoreTasks(VillagerProfession profession, float f)
		{
			return ImmutableList.of(
					Pair.of(0, new StayAboveWaterTask(0.8F)),
					Pair.of(0, new OpenDoorsTask()),
					Pair.of(0, new LookAroundTask(45, 90)),
					Pair.of(0, new PanicTask()),
					Pair.of(0, new WakeUpTask()),
					Pair.of(0, new HideWhenBellRingsTask()),
					Pair.of(0, new StartRaidTask()),
					Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE)),
					Pair.of(0, new ForgetCompletedPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.POTENTIAL_JOB_SITE)),
					Pair.of(1, new WanderAroundTask()),
					Pair.of(2, new WorkStationCompetitionTask(profession)),
					Pair.of(3, new FollowCustomerTask(f)),
					Pair.of(5, new WalkToNearestVisibleWantedItemTask(f, false, 4)),
					Pair.of(6, new FindPointOfInterestTask(profession.getWorkStation(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())),
					Pair.of(7, new WalkTowardJobSiteTask(f)),
					Pair.of(8, new TakeJobSiteTask(f)),
					Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME, false, Optional.of((byte)14))),
					Pair.of(10, new FindPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14))),
					Pair.of(10, new GoToWorkTask()),
					Pair.of(10, new LoseJobOnSiteLossTask()));
		}
		
		
		
		
		protected static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getWorkTasks(VillagerProfession p, float f)
		{
			if (workTasks != null)
				return applyToTasks(workTasks, p, f);
			else
				return getDefaultWorkTasks(p, f);
		}
		
		private static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getDefaultWorkTasks(final VillagerProfession profession, final float f)
		{
			Object villagerWorkTask2;
			if (profession == VillagerProfession.FARMER) {
				villagerWorkTask2 = new FarmerWorkTask();
			} else {
				villagerWorkTask2 = new VillagerWorkTask();
			}
			
			return ImmutableList.of(
					createBusyFollowTask(),
					Pair.of(5, new RandomTask(
							ImmutableList.of(
									Pair.of(villagerWorkTask2, 7),
									Pair.of(new GoToIfNearbyTask(MemoryModuleType.JOB_SITE, 0.4F, 4), 2),
									Pair.of(new GoToNearbyPositionTask(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5),
									Pair.of(new GoToSecondaryPositionTask(MemoryModuleType.SECONDARY_JOB_SITE, f, 1, 6, MemoryModuleType.JOB_SITE), 5),
									Pair.of(new FarmerVillagerTask(), profession == VillagerProfession.FARMER ? 2 : 5),
									Pair.of(new BoneMealTask(), profession == VillagerProfession.FARMER ? 4 : 7)))),
					Pair.of(10, new HoldTradeOffersTask(400, 1600)),
					Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
					Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.JOB_SITE, f, 9, 100, 1200)),
					Pair.of(3, new GiveGiftsToHeroTask(100)),
					Pair.of(99, new ScheduleActivityTask()));
		}
		
		
		
		
		protected static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getPlayTasks(float f)
		{
			if (playTasks != null)
				return playTasks.stream().map(func -> func.apply(f)).collect(Collectors.toList());
			else
				return getDefaultPlayTasks(f);
		}
		
		private static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getDefaultPlayTasks(final float f)
		{
			return ImmutableList.of(
					Pair.of(0, new WanderAroundTask(80, 120)),
					createFreeFollowTask(),
					Pair.of(5, new PlayWithVillagerBabiesTask()),
					Pair.of(5, new RandomTask(
							ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleState.VALUE_ABSENT),
							ImmutableList.of(
									Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 2),
									Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 1),
									Pair.of(new FindWalkTargetTask(f), 1),
									Pair.of(new GoTowardsLookTarget(f, 2), 1),
									Pair.of(new JumpInBedTask(f), 2),
									Pair.of(new WaitTask(20, 40), 2)))),
					Pair.of(99, new ScheduleActivityTask()));
		}
		
		
		
		protected static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getRestTasks(VillagerProfession p, float f)
		{
			if (restTasks != null)
				return applyToTasks(restTasks, p, f);
			else
				return getDefaultRestTasks(p, f);
		}
		
		private static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getDefaultRestTasks(final VillagerProfession p, final float f)
		{
			return ImmutableList.of(
					Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.HOME, f, 1, 150, 1200)),
					Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME)),
					Pair.of(3, new SleepTask()),
					Pair.of(5, new RandomTask(
							ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT),
							ImmutableList.of(
									Pair.of(new WalkHomeTask(f), 1),
									Pair.of(new WanderIndoorsTask(f), 4),
									Pair.of(new GoToPointOfInterestTask(f, 4), 2),
									Pair.of(new WaitTask(20, 40), 2)))),
					createBusyFollowTask(),
					Pair.of(99, new ScheduleActivityTask()));
		}
		
		
		
		protected static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getMeetTasks(VillagerProfession p, float f)
		{
			if (meetTasks != null)
				return applyToTasks(meetTasks, p, f);
			else
				return getDefaultMeetTasks(p, f);
		}
		
		private static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getDefaultMeetTasks(final VillagerProfession p, final float f)
		{
			return ImmutableList.of(
					Pair.of(2, new RandomTask(
							ImmutableList.of(
									Pair.of(new GoToIfNearbyTask(MemoryModuleType.MEETING_POINT, 0.4F, 40), 2),
									Pair.of(new MeetVillagerTask(), 2)))),
					Pair.of(10, new HoldTradeOffersTask(400, 1600)),
					Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
					Pair.of(2, new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, f, 6, 100, 200)),
					Pair.of(3, new GiveGiftsToHeroTask(100)),
					Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT)),
					Pair.of(3, new CompositeTask(
							ImmutableMap.of(),
							ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
							CompositeTask.Order.ORDERED,
							CompositeTask.RunMode.RUN_ONE,
							ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1)))),
					createFreeFollowTask(),
					Pair.of(99, new ScheduleActivityTask()));
		}
		
		
		
		protected static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getIdleTasks(VillagerProfession p, float f)
		{
			if (idleTasks != null)
				return applyToTasks(idleTasks, p, f);
			else
				return getDefaultIdleTasks(p, f);
		}
		
		private static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getDefaultIdleTasks(final VillagerProfession p, final float f)
		{
			final Predicate<PassiveEntity> isReadyToBreed = PassiveEntity::isReadyToBreed; //Workaround because the compiler's stupid
			
			return ImmutableList.of(
					Pair.of(2, new RandomTask(
							ImmutableList.of(
									Pair.of(FindEntityTask.create(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 2),
									Pair.of(new FindEntityTask(EntityType.VILLAGER, 8, isReadyToBreed, isReadyToBreed, MemoryModuleType.BREED_TARGET, f, 2), 1),
									Pair.of(FindEntityTask.create(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), 1),
									Pair.of(new FindWalkTargetTask(f), 1),
									Pair.of(new GoTowardsLookTarget(f, 2), 1),
									Pair.of(new JumpInBedTask(f), 1),
									Pair.of(new WaitTask(30, 60), 1)))),
					Pair.of(3, new GiveGiftsToHeroTask(100)),
					Pair.of(3, new FindInteractionTargetTask(EntityType.PLAYER, 4)),
					Pair.of(3, new HoldTradeOffersTask(400, 1600)),
					Pair.of(3, new CompositeTask(
							ImmutableMap.of(),
							ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
							CompositeTask.Order.ORDERED,
							CompositeTask.RunMode.RUN_ONE,
							ImmutableList.of(Pair.of(new GatherItemsVillagerTask(), 1)))),
					Pair.of(3, new CompositeTask(
							ImmutableMap.of(),
							ImmutableSet.of(MemoryModuleType.BREED_TARGET),
							CompositeTask.Order.ORDERED,
							CompositeTask.RunMode.RUN_ONE,
							ImmutableList.of(Pair.of(new VillagerBreedTask(), 1)))),
					createFreeFollowTask(),
					Pair.of(99, new ScheduleActivityTask()));
		}
		
		
		
		protected static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getPanicTasks(VillagerProfession p, float f)
		{
			if (panicTasks != null)
				return applyToTasks(panicTasks, p, f);
			else
				return getDefaultPanicTasks(p, f);
		}
		
		private static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getDefaultPanicTasks(final VillagerProfession p, final float f)
		{
			float g = f * 1.5F;
			return ImmutableList.of(
					Pair.of(0, new StopPanickingTask()),
					Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_HOSTILE, g, 6, false)),
					Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.HURT_BY_ENTITY, g, 6, false)),
					Pair.of(3, new FindWalkTargetTask(g, 2, 2)),
					createBusyFollowTask());
		}
		
		
		
		
		protected static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getPreRaidTasks(VillagerProfession p, float f)
		{
			if (preRaidTasks != null)
				return applyToTasks(preRaidTasks, p, f);
			else
				return getDefaultPreRaidTasks(p, f);
		}
		
		private static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getDefaultPreRaidTasks(final VillagerProfession p, final float f)
		{
			return ImmutableList.of(
					Pair.of(0, new RingBellTask()),
					Pair.of(0, new RandomTask(
							ImmutableList.of(
									Pair.of(new VillagerWalkTowardsTask(MemoryModuleType.MEETING_POINT, f * 1.5F, 2, 150, 200), 6),
									Pair.of(new FindWalkTargetTask(f * 1.5F), 2)))),
					createBusyFollowTask(),
					Pair.of(99, new EndRaidTask()));
		}
		
		protected static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getRaidTasks(VillagerProfession p, float f)
		{
			if (raidTasks != null)
				return applyToTasks(raidTasks, p, f);
			else
				return getDefaultRaidTasks(p, f);
		}
		
		private static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getDefaultRaidTasks(final VillagerProfession p, final float f)
		{
			return ImmutableList.of(
					Pair.of(0, new RandomTask(
							ImmutableList.of(
									Pair.of(new SeekSkyAfterRaidWinTask(f), 5),
									Pair.of(new RunAroundAfterRaidTask(f * 1.1F), 2)))),
					Pair.of(0, new CelebrateRaidWinTask(600, 600)),
					Pair.of(2, new HideInHomeDuringRaidTask(24, f * 1.4F)),
					createBusyFollowTask(),
					Pair.of(99, new EndRaidTask()));
		}
		
		protected static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getHideTasks(VillagerProfession p, float f)
		{
			if (hideTasks != null)
				return applyToTasks(hideTasks, p, f);
			else
				return getDefaultHideTasks(p, f);
		}
		
		private static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getDefaultHideTasks(final VillagerProfession p, final float f)
		{
			return ImmutableList.of(
					Pair.of(0, new ForgetBellRingTask(15, 3)),
					Pair.of(1, new HideInHomeTask(32, f * 1.25F, 2)),
					createBusyFollowTask());
		}
		
		private static Pair<Integer, ? extends Task<? super VillagerEntity>> createBusyFollowTask()
		{
			return Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
					ImmutableList.of(
							Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
							Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
							Pair.of(new WaitTask(30, 60), 8))));
		}
		
		private static Pair<Integer, Task<? super VillagerEntity>> createFreeFollowTask() {
			return Pair.of(5, (Task<? super VillagerEntity>) new RandomTask(
					ImmutableList.of(
							Pair.of(new FollowMobTask(EntityType.CAT, 8.0F), 8),
							Pair.of(new FollowMobTask(EntityType.VILLAGER, 8.0F), 2),
							Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2),
							Pair.of(new FollowMobTask(SpawnGroup.CREATURE, 8.0F), 1),
							Pair.of(new FollowMobTask(SpawnGroup.WATER_CREATURE, 8.0F), 1),
							Pair.of(new FollowMobTask(SpawnGroup.WATER_AMBIENT, 8.0F), 1),
							Pair.of(new FollowMobTask(SpawnGroup.MONSTER, 8.0F), 1),
							Pair.of(new WaitTask(30, 60), 2))));
		}
	}
	
	
	
	
	private static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> applyToTasks(List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> l, VillagerProfession p, float f)
	{
		return l.stream().map(func -> func.apply(p, f)).collect(Collectors.toList());
	}
}
