package com.bythepowerofscience.taskapi.mixins;

import com.bythepowerofscience.taskapi.api.BackendVillagerTaskRetriever;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VillagerEntity.class)
public abstract class VillagerBrainInterceptor {
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createCoreTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptCoreTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getCoreTasks(profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createWorkTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptWorkTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getWorkTasks(profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createMeetTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptMeetTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getMeetTasks(profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createRestTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptRestTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getRestTasks(profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createIdleTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptIdleTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getIdleTasks(profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createPanicTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptPanicTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getPanicTasks(profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createPreRaidTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptPreRaidTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getPreRaidTasks(profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createRaidTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptRaidTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getRaidTasks(profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createHideTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptHideTasks(VillagerProfession profession, float f)
	{
		return BackendVillagerTaskRetriever.getHideTasks(profession, f);
	}
	
	@Redirect(method="initBrain", at=@At(value="INVOKE", target = "net/minecraft/entity/ai/brain/task/VillagerTaskListProvider.createPlayTasks(F)Lcom/google/common/collect/ImmutableList;"))
	private ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> interceptPlayTasks(float f)
	{
		return BackendVillagerTaskRetriever.getPlayTasks(f);
	}
	
}
