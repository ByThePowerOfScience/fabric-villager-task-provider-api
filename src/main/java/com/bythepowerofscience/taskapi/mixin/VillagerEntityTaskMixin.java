package com.bythepowerofscience.taskapi.mixin;

import com.bythepowerofscience.taskapi.api.BackendVillagerTaskRetriever;
import com.bythepowerofscience.taskapi.impl.VillagerTaskProvider;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VillagerEntity.class)
abstract class VillagerEntityTaskMixin {
	//TODO change these redirects to ModifyArgs in the list.
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createCoreTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptCoreTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getConstantTasks(VillagerTaskProvider.TaskType.CORE, profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createWorkTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptWorkTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getConstantTasks(VillagerTaskProvider.TaskType.WORK, profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createPlayTasks(F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptPlayTasks(float f)
	{
		return BackendVillagerTaskRetriever.getConstantTasks(VillagerTaskProvider.TaskType.PLAY, null, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createMeetTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptMeetTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getConstantTasks(VillagerTaskProvider.TaskType.MEET, profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createRestTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptRestTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getConstantTasks(VillagerTaskProvider.TaskType.REST, profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createIdleTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptIdleTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getConstantTasks(VillagerTaskProvider.TaskType.IDLE, profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createPanicTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptPanicTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getConstantTasks(VillagerTaskProvider.TaskType.PANIC, profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createPreRaidTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptPreRaidTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getConstantTasks(VillagerTaskProvider.TaskType.PRERAID, profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createRaidTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptRaidTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getConstantTasks(VillagerTaskProvider.TaskType.RAID, profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createHideTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptHideTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getConstantTasks(VillagerTaskProvider.TaskType.HIDE, profession, f);
	}
	
	
}
