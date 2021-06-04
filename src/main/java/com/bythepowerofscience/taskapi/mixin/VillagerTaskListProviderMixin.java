package com.bythepowerofscience.taskapi.mixin;

import com.bythepowerofscience.taskapi.api.BackendVillagerTaskRetriever;
import com.bythepowerofscience.taskapi.impl.VillagerTaskProvider;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(VillagerTaskListProvider.class)
abstract class VillagerTaskListProviderMixin {
	
	//Add tasks to the random task list on invoke, which means only when the brain is initialized.
	//The list is built ad-hoc anyway, so I don't need to worry about permanently altering the thing.
	@ModifyArgs(method = "createIdleTasks", at = @At(value = "INVOKE", target = "net/minecraft/entity/ai/brain/task/RandomTask.<init>(Ljava/util/List;)V"))
	private static void addCustomRandomIdleTasks(Args args, VillagerProfession profession, float f)
	{
		ImmutableList.Builder<Pair<Task<? super VillagerEntity>, Integer>> taskList = ImmutableList.<Pair<Task<? super VillagerEntity>, Integer>>builder();
		ImmutableList<Pair<Task<? super VillagerEntity>, Integer>> vanillaList = args.get(0);
		
		taskList.addAll(vanillaList);
		taskList.addAll(BackendVillagerTaskRetriever.getRandomTasks(VillagerTaskProvider.TaskType.IDLE, profession, f));
		
		args.set(0, taskList.build());
	}
	
}
