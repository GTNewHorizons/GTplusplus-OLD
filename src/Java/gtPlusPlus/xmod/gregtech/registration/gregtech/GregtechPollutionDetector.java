package gtPlusPlus.xmod.gregtech.registration.gregtech;

import gtPlusPlus.core.lib.CORE;
import gtPlusPlus.core.util.Utils;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtPlusPlus.xmod.gregtech.common.tileentities.machines.basic.GregtechMetaCondensor;

public class GregtechPollutionDetector {

	public static void run(){
		if (gtPlusPlus.core.lib.LoadedMods.Gregtech){
			Utils.LOG_INFO("Gregtech5u Content | Registering Pollution Detector.");
			if (CORE.MAIN_GREGTECH_5U_EXPERIMENTAL_FORK) {
				run1();
			}
		}
	}

	private static void run1(){
		//759
		GregtechItemList.Pollution_Detector.set(new GregtechMetaCondensor(759, "pollutiondetector.01.tier.single", "Pollution Detection Device").getStackForm(1L));
	}
	
}
