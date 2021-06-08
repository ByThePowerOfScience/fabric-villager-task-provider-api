package com.bythepowerofscience.taskapi.mixin;

import com.bythepowerofscience.taskapi.impl.VillagerTaskProviderRetriever;
import com.bythepowerofscience.taskapi.api.VillagerTaskProvider;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@SuppressWarnings({"unchecked"})
@Mixin(VillagerTaskListProvider.class)
abstract class VillagerTaskListProviderMixin {
	
	private static final int RANDOM_TASK_WEIGHT = 3;
	
	/*
	 * Possible issues in the future:
	 * -"MixinTransformerError"/"Critical injection failure: Multi-argument modifier method..."
	 * 		Cause: RandomTask constructor can take either (Map, List) or (List).
	 * 			   Mojang likely changed which constructor is being used.
	 * 		Fix: Change "RandomTask.<init>(Ljava/util/List;)V" to "(Ljava/util/Map;Ljava/util/List;)V", or vice versa.
	 * 
	 * 
	 */
	
	
	
	
	
	
	@Inject(method = "createCoreTasks", at = @At("RETURN"), cancellable=true)
	private static void addCustomConstantCoreTasks(VillagerProfession profession, float f, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir)
	{
		ImmutableList.Builder<Pair<Integer, ? extends Task<? super VillagerEntity>>> taskList = ImmutableList.builder();
		taskList.addAll(cir.getReturnValue());
		taskList.addAll(VillagerTaskProviderRetriever.getConstantTasks(VillagerTaskProvider.TaskType.CORE, profession, f));
		
		//START ADD NEW RANDOM TASK
		//	NOTE: This is to reuse the addCustomRandomTasks code.
		//		If Mojang ever adds a "RandomTask" in this section, 
		//			delete this block and uncomment the "@ModifyArgs" annotation below.
		if (VillagerTaskProviderRetriever.hasCustomRandomTasks(VillagerTaskProvider.TaskType.CORE, profession))
		{
			Args randomTasksDummyParam = new Args(new ImmutableList[] {ImmutableList.of()}) {
				@Override
				public <T> void set(final int index, final T value)
				{
					this.values[index] = value;
				}
				
				@Override
				public void setAll(final Object... values)
				{ }
			};
			
			addCustomRandomCoreTasks(randomTasksDummyParam, profession, f);
			ImmutableList<Pair<Task<? super VillagerEntity>, Integer>> randomTasks = randomTasksDummyParam.get(0);
			if (!randomTasks.isEmpty())
				taskList.add(Pair.of(RANDOM_TASK_WEIGHT, new RandomTask<>(randomTasks)));
		}
		//END ADD NEW RANDOM TASK
		
		cir.setReturnValue(taskList.build());
	}
	
	//@ModifyArgs(method = "createCoreTasks", at = @At(value = "INVOKE", target = "net/minecraft/entity/ai/brain/task/RandomTask.<init>(Ljava/util/List;)V"))
	private static void addCustomRandomCoreTasks(Args args, VillagerProfession profession, float f)
	{
		ImmutableList.Builder<Pair<Task<? super VillagerEntity>, Integer>> taskList = ImmutableList.builder();
		
		int listIndex = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i) instanceof List<?>)
				listIndex = i;
		}
		assert listIndex != -1 : "No List argument provided.";
		taskList.addAll((List<Pair<Task<? super VillagerEntity>, Integer>>) args.get(listIndex));
		
		taskList.addAll(VillagerTaskProviderRetriever.getRandomTasks(VillagerTaskProvider.TaskType.CORE, profession, f));

		args.set(listIndex, taskList.build());
	}
	
	
	
	
	
	
	
	
	
	@Inject(method = "createWorkTasks", at = @At(value = "RETURN"), cancellable=true)
	private static void addCustomConstantWorkTasks(VillagerProfession profession, float f, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir)
	{
		ImmutableList.Builder<Pair<Integer, ? extends Task<? super VillagerEntity>>> taskList = ImmutableList.builder();
		
		taskList.addAll(cir.getReturnValue());
		taskList.addAll(VillagerTaskProviderRetriever.getConstantTasks(VillagerTaskProvider.TaskType.WORK, profession, f));
		
		cir.setReturnValue(taskList.build());
	}
	
	@ModifyArgs(method = "createWorkTasks", at = @At(value = "INVOKE", target = "net/minecraft/entity/ai/brain/task/RandomTask.<init>(Ljava/util/List;)V"))
	private static void addCustomRandomWorkTasks(Args args, VillagerProfession profession, float f)
	{
		ImmutableList.Builder<Pair<Task<? super VillagerEntity>, Integer>> taskList = ImmutableList.builder();
		
		int listIndex = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i) instanceof List<?>)
				listIndex = i;
		}
		assert listIndex != -1 : "No List argument provided.";
		taskList.addAll((List<Pair<Task<? super VillagerEntity>, Integer>>) args.get(listIndex));
		
		taskList.addAll(VillagerTaskProviderRetriever.getRandomTasks(VillagerTaskProvider.TaskType.WORK, profession, f));
		
		args.set(listIndex, taskList.build());
	}
	
	
	
	
	
	
	
	
	
	@Inject(method = "createPlayTasks", at = @At(value = "RETURN"), cancellable=true)
	private static void addCustomConstantPlayTasks(float f, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir)
	{
		ImmutableList.Builder<Pair<Integer, ? extends Task<? super VillagerEntity>>> taskList = ImmutableList.builder();
		
		taskList.addAll(cir.getReturnValue());
		taskList.addAll(VillagerTaskProviderRetriever.getConstantTasks(VillagerTaskProvider.TaskType.PLAY, null, f));
		
		cir.setReturnValue(taskList.build());
	}
	
	@ModifyArgs(method = "createPlayTasks", at = @At(value = "INVOKE", target = "net/minecraft/entity/ai/brain/task/RandomTask.<init>(Ljava/util/Map;Ljava/util/List;)V"))
	private static void addCustomRandomPlayTasks(Args args, float f)
	{
		ImmutableList.Builder<Pair<Task<? super VillagerEntity>, Integer>> taskList = ImmutableList.builder();
		
		int listIndex = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i) instanceof List<?>)
				listIndex = i;
		}
		assert listIndex != -1 : "No List argument provided.";
		taskList.addAll((List<Pair<Task<? super VillagerEntity>, Integer>>) args.get(listIndex));
		
		taskList.addAll(VillagerTaskProviderRetriever.getRandomTasks(VillagerTaskProvider.TaskType.PLAY, null, f));
		
		args.set(listIndex, taskList.build());
	}
	
	
	
	@Inject(method = "createRestTasks", at = @At(value = "RETURN"), cancellable=true)
	private static void addCustomConstantRestTasks(VillagerProfession profession, float f, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir)
	{
		ImmutableList.Builder<Pair<Integer, ? extends Task<? super VillagerEntity>>> taskList = ImmutableList.builder();
		
		taskList.addAll(cir.getReturnValue());
		taskList.addAll(VillagerTaskProviderRetriever.getConstantTasks(VillagerTaskProvider.TaskType.REST, profession, f));
		
		cir.setReturnValue(taskList.build());
	}
	
	
	@ModifyArgs(method = "createRestTasks", at = @At(value = "INVOKE", target = "net/minecraft/entity/ai/brain/task/RandomTask.<init>(Ljava/util/Map;Ljava/util/List;)V"))
	private static void addCustomRandomRestTasks(Args args, VillagerProfession profession, float f)
	{
		ImmutableList.Builder<Pair<Task<? super VillagerEntity>, Integer>> taskList = ImmutableList.builder();
		
		int listIndex = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i) instanceof List<?>)
				listIndex = i;
		}
		assert listIndex != -1 : "No List argument provided.";
		taskList.addAll((List<Pair<Task<? super VillagerEntity>, Integer>>) args.get(listIndex));
		
		taskList.addAll(VillagerTaskProviderRetriever.getRandomTasks(VillagerTaskProvider.TaskType.REST, profession, f));
		
		args.set(listIndex, taskList.build());
	}
	
	
	
	
	
	
	
	
	@Inject(method = "createMeetTasks", at = @At(value = "RETURN"), cancellable=true)
	private static void addCustomConstantMeetTasks(VillagerProfession profession, float f, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir)
	{
		ImmutableList.Builder<Pair<Integer, ? extends Task<? super VillagerEntity>>> taskList = ImmutableList.builder();
		
		taskList.addAll(cir.getReturnValue());
		taskList.addAll(VillagerTaskProviderRetriever.getConstantTasks(VillagerTaskProvider.TaskType.MEET, profession, f));
		
		cir.setReturnValue(taskList.build());
	}
	
	@ModifyArgs(method = "createMeetTasks", at = @At(value = "INVOKE", target = "net/minecraft/entity/ai/brain/task/RandomTask.<init>(Ljava/util/List;)V"))
	private static void addCustomRandomMeetTasks(Args args, VillagerProfession profession, float f)
	{
		ImmutableList.Builder<Pair<Task<? super VillagerEntity>, Integer>> taskList = ImmutableList.builder();
		
		int listIndex = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i) instanceof List<?>)
				listIndex = i;
		}
		assert listIndex != -1 : "No List argument provided.";
		taskList.addAll((List<Pair<Task<? super VillagerEntity>, Integer>>) args.get(listIndex));
		
		taskList.addAll(VillagerTaskProviderRetriever.getRandomTasks(VillagerTaskProvider.TaskType.MEET, profession, f));
		
		args.set(listIndex, taskList.build());
	}
	
	
	
	
	
	
	
	@Inject(method = "createIdleTasks", at = @At(value = "RETURN"), cancellable=true)
	private static void addCustomConstantIdleTasks(VillagerProfession profession, float f, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir)
	{
		ImmutableList.Builder<Pair<Integer, ? extends Task<? super VillagerEntity>>> taskList = ImmutableList.builder();
		
		taskList.addAll(cir.getReturnValue());
		taskList.addAll(VillagerTaskProviderRetriever.getConstantTasks(VillagerTaskProvider.TaskType.IDLE, profession, f));
		
		cir.setReturnValue(taskList.build());
	}
	
	@ModifyArgs(method = "createIdleTasks", at = @At(value = "INVOKE", target = "net/minecraft/entity/ai/brain/task/RandomTask.<init>(Ljava/util/List;)V"))
	private static void addCustomRandomIdleTasks(Args args, VillagerProfession profession, float f)
	{
		ImmutableList.Builder<Pair<Task<? super VillagerEntity>, Integer>> taskList = ImmutableList.builder();
		
		int listIndex = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i) instanceof List<?>)
				listIndex = i;
		}
		assert listIndex != -1 : "No List argument provided.";
		taskList.addAll((List<Pair<Task<? super VillagerEntity>, Integer>>) args.get(listIndex));
		
		taskList.addAll(VillagerTaskProviderRetriever.getRandomTasks(VillagerTaskProvider.TaskType.IDLE, profession, f));
		
		args.set(listIndex, taskList.build());
	}
	
	
	
	
	
	
	
	@Inject(method = "createPanicTasks", at = @At(value = "RETURN"), cancellable=true)
	private static void addCustomConstantPanicTasks(VillagerProfession profession, float f, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir)
	{
		ImmutableList.Builder<Pair<Integer, ? extends Task<? super VillagerEntity>>> taskList = ImmutableList.builder();
		taskList.addAll(cir.getReturnValue());
		taskList.addAll(VillagerTaskProviderRetriever.getConstantTasks(VillagerTaskProvider.TaskType.PANIC, profession, f));
		
		//START ADD NEW RANDOM TASK
		//	NOTE: This is to reuse the addCustomRandomTasks code.
		//		If Mojang ever adds a "RandomTask" in the target method, 
		//			delete this block and uncomment the "@ModifyArgs" annotation below.
		if (VillagerTaskProviderRetriever.hasCustomRandomTasks(VillagerTaskProvider.TaskType.PANIC, profession))
		{
			Args randomTasksDummyParam = new Args(new ImmutableList[] {ImmutableList.of()}) {
				@Override
				public <T> void set(final int index, final T value)
				{
					this.values[index] = value;
				}
				
				@Override
				public void setAll(final Object... values)
				{ }
			};
			
			addCustomRandomPanicTasks(randomTasksDummyParam, profession, f);
			ImmutableList<Pair<Task<? super VillagerEntity>, Integer>> randomTasks = randomTasksDummyParam.get(0);
			if (!randomTasks.isEmpty())
				taskList.add(Pair.of(RANDOM_TASK_WEIGHT, new RandomTask<>(randomTasks)));
		}
		//END ADD NEW RANDOM TASK
		
		cir.setReturnValue(taskList.build());
	}
	
	//@ModifyArgs(method = "createPanicTasks", at = @At(value = "INVOKE", target = "com/google/common/collect/ImmutableList.of (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList"))
	private static void addCustomRandomPanicTasks(Args args, VillagerProfession profession, float f)
	{
		ImmutableList.Builder<Pair<Task<? super VillagerEntity>, Integer>> taskList = ImmutableList.builder();
		
		int listIndex = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i) instanceof List<?>)
				listIndex = i;
		}
		assert listIndex != -1 : "No List argument provided.";
		taskList.addAll((List<Pair<Task<? super VillagerEntity>, Integer>>) args.get(listIndex));
		
		taskList.addAll(VillagerTaskProviderRetriever.getRandomTasks(VillagerTaskProvider.TaskType.PANIC, profession, f));
		
		args.set(listIndex, taskList.build());
	}
	
	
	
	
	
	@Inject(method = "createPreRaidTasks", at = @At(value = "RETURN"), cancellable=true)
	private static void addCustomConstantPreRaidTasks(VillagerProfession profession, float f, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir)
	{
		ImmutableList.Builder<Pair<Integer, ? extends Task<? super VillagerEntity>>> taskList = ImmutableList.builder();
		
		taskList.addAll(cir.getReturnValue());
		taskList.addAll(VillagerTaskProviderRetriever.getConstantTasks(VillagerTaskProvider.TaskType.PRERAID, profession, f));
		
		cir.setReturnValue(taskList.build());
	}
	
	@ModifyArgs(method = "createPreRaidTasks", at = @At(value = "INVOKE", target = "net/minecraft/entity/ai/brain/task/RandomTask.<init>(Ljava/util/List;)V"))
	private static void addCustomRandomPreRaidTasks(Args args, VillagerProfession profession, float f)
	{
		ImmutableList.Builder<Pair<Task<? super VillagerEntity>, Integer>> taskList = ImmutableList.builder();
		
		int listIndex = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i) instanceof List<?>)
				listIndex = i;
		}
		assert listIndex != -1 : "No List argument provided.";
		taskList.addAll((List<Pair<Task<? super VillagerEntity>, Integer>>) args.get(listIndex));
		
		taskList.addAll(VillagerTaskProviderRetriever.getRandomTasks(VillagerTaskProvider.TaskType.PRERAID, profession, f));
		
		args.set(listIndex, taskList.build());
	}
	
	
	
	@Inject(method = "createRaidTasks", at = @At(value = "RETURN"), cancellable=true)
	private static void addCustomConstantRaidTasks(VillagerProfession profession, float f, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir)
	{
		ImmutableList.Builder<Pair<Integer, ? extends Task<? super VillagerEntity>>> taskList = ImmutableList.builder();
		
		taskList.addAll(cir.getReturnValue());
		taskList.addAll(VillagerTaskProviderRetriever.getConstantTasks(VillagerTaskProvider.TaskType.RAID, profession, f));
		
		cir.setReturnValue(taskList.build());
	}
	
	@ModifyArgs(method = "createRaidTasks", at = @At(value = "INVOKE", target = "net/minecraft/entity/ai/brain/task/RandomTask.<init>(Ljava/util/List;)V"))
	private static void addCustomRandomRaidTasks(Args args, VillagerProfession profession, float f)
	{
		ImmutableList.Builder<Pair<Task<? super VillagerEntity>, Integer>> taskList = ImmutableList.builder();
		
		int listIndex = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i) instanceof List<?>)
				listIndex = i;
		}
		assert listIndex != -1 : "No List argument provided.";
		taskList.addAll((List<Pair<Task<? super VillagerEntity>, Integer>>) args.get(listIndex));
		
		taskList.addAll(VillagerTaskProviderRetriever.getRandomTasks(VillagerTaskProvider.TaskType.RAID, profession, f));
		
		args.set(listIndex, taskList.build());
	}
	
	
	
	
	
	
	
	@Inject(method = "createHideTasks", at = @At("RETURN"), cancellable=true)
	private static void addCustomConstantHideTasks(VillagerProfession profession, float f, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir)
	{
		ImmutableList.Builder<Pair<Integer, ? extends Task<? super VillagerEntity>>> taskList = ImmutableList.builder();
		taskList.addAll(cir.getReturnValue());
		taskList.addAll(VillagerTaskProviderRetriever.getConstantTasks(VillagerTaskProvider.TaskType.HIDE, profession, f));
		
		//START ADD NEW RANDOM TASK
		//	NOTE: This is to reuse the addCustomRandomTasks code.
		//		If Mojang ever adds a "RandomTask" in the target method, 
		//			delete this block and uncomment the "@ModifyArgs" annotation below.
		if (VillagerTaskProviderRetriever.hasCustomRandomTasks(VillagerTaskProvider.TaskType.HIDE, profession))
		{
			Args randomTasksDummyParam = new Args(new ImmutableList[] {ImmutableList.of()}) {
				@Override
				public <T> void set(final int index, final T value)
				{
					this.values[index] = value;
				}
				
				@Override
				public void setAll(final Object... values)
				{ }
			};
			
			addCustomRandomHideTasks(randomTasksDummyParam, profession, f);
			ImmutableList<Pair<Task<? super VillagerEntity>, Integer>> randomTasks = randomTasksDummyParam.get(0);
			if (!randomTasks.isEmpty())
				taskList.add(Pair.of(RANDOM_TASK_WEIGHT, new RandomTask<>(randomTasks)));
		}
		//END ADD NEW RANDOM TASK
		
		cir.setReturnValue(taskList.build());
	}
	
	//@ModifyArgs(method = "createHideTasks", at = @At(value = "INVOKE", target = "com/google/common/collect/ImmutableList.of (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList"))
	private static void addCustomRandomHideTasks(Args args, VillagerProfession profession, float f)
	{
		ImmutableList.Builder<Pair<Task<? super VillagerEntity>, Integer>> taskList = ImmutableList.builder();
		
		int listIndex = -1;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i) instanceof List<?>)
				listIndex = i;
		}
		assert listIndex != -1 : "No List argument provided.";
		taskList.addAll((List<Pair<Task<? super VillagerEntity>, Integer>>) args.get(listIndex));
		
		taskList.addAll(VillagerTaskProviderRetriever.getRandomTasks(VillagerTaskProvider.TaskType.HIDE, profession, f));
		
		args.set(listIndex, taskList.build());
	}
}
