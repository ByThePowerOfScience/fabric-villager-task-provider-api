import net.fabricmc.api.ModInitializer;
import villager.FarmerVillager;

public class Initializer implements ModInitializer {
	@Override
	public void onInitialize()
	{
		FarmerVillager.init();
	}
}
