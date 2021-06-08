package villager;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class FarmerVillager {
	
	private static final VillagerProfession FARMER_PROFESSION = VillagerProfessionBuilder.create()
			.id(new Identifier("examplemod", "farmer"))
			.harvestableItems(ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL))
			.workstation(PointOfInterestType.FARMER)
			.secondaryJobSites(ImmutableSet.of(Blocks.FARMLAND))
			.workSound(SoundEvents.ENTITY_VILLAGER_WORK_FARMER)
			.build();
	
	
	public static void init()
	{
		Registry.register(Registry.VILLAGER_PROFESSION, new Identifier("examplemod", "farmer"), FARMER_PROFESSION);
		
	}
}
