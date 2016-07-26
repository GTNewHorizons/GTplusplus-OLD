package miscutil.core.xmod.gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Maintenance;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;

import java.util.ArrayList;
import java.util.List;

import miscutil.core.block.ModBlocks;
import miscutil.core.lib.CORE;
import miscutil.core.util.Utils;
import miscutil.core.xmod.gregtech.api.gui.GUI_MultiMachine;
import miscutil.core.xmod.gregtech.api.metatileentity.implementations.base.GregtechMeta_MultiBlockBase;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.ArrayUtils;

public class GregtechMetaTileEntityIndustrialCentrifuge
extends GregtechMeta_MultiBlockBase {
	private static boolean controller;
	private static boolean isDisabled = false;
	private static ITexture frontFace;
	private static ITexture frontFaceActive;
	private static Textures.BlockIcons.CustomIcon GT9_5_Active = new Textures.BlockIcons.CustomIcon("iconsets/LARGETURBINE_ST_ACTIVE5");
	private static Textures.BlockIcons.CustomIcon GT9_5 = new Textures.BlockIcons.CustomIcon("iconsets/LARGETURBINE_ST5");
	private static Textures.BlockIcons.CustomIcon GT8_5_Active = new Textures.BlockIcons.CustomIcon("iconsets/LARGETURBINE_ACTIVE5");
	private static Textures.BlockIcons.CustomIcon GT8_5 = new Textures.BlockIcons.CustomIcon("iconsets/LARGETURBINE5");
	//public static double recipesComplete = 0;

	public GregtechMetaTileEntityIndustrialCentrifuge(int aID, String aName, String aNameRegional) {
		super(aID, aName, aNameRegional);
		if (CORE.MAIN_GREGTECH_5U_EXPERIMENTAL_FORK){
			frontFaceActive = new GT_RenderedTexture(GT9_5_Active);
			frontFace = new GT_RenderedTexture(GT9_5);
		}
		else{
			frontFaceActive = new GT_RenderedTexture(GT8_5_Active);
			frontFace = new GT_RenderedTexture(GT8_5);
		}
	}

	public GregtechMetaTileEntityIndustrialCentrifuge(String aName) {
		super(aName);
	}

	@Override
	public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
		return new GregtechMetaTileEntityIndustrialCentrifuge(this.mName);
	}

	@Override
	public String[] getDescription() {
		return new String[]{
				"Controller Block for the Industrial Centrifuge",
				"Size: 3x3x3 (Hollow)", 
				"Controller (Front Center) [Orange]",
				"1x Maintenance Hatch (Rear Center) [Green]",
				"The rest can be placed anywhere except the Front [Red]",
				"1x Input Hatch",
				"1x Output Hatch",
				"1x Input Bus",
				"1x Output Bus",
				"1x [EV] Energy Hatch (Can be higher Tier) [Blue]",
				"Centrifuge Casings for the rest (16 at least)",};
	}



	@Override
	public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
		return new ITexture[]{Textures.BlockIcons.MACHINE_CASINGS[1][aColorIndex + 1], aFacing == aSide ? aActive ? frontFaceActive : frontFace : Textures.BlockIcons.CASING_BLOCKS[57]};
	}


	@Override
	public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
		return new GUI_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "IndustrialCentrifuge.png");
	}

	@Override
	public GT_Recipe.GT_Recipe_Map getRecipeMap() {
		return GT_Recipe.GT_Recipe_Map.sCentrifugeRecipes;
	}

	@Override
	public boolean isFacingValid(byte aFacing) {
		return aFacing > 1;
	}

	ArrayList<ItemStack> tInputList = getStoredInputs();
	GT_Recipe mLastRecipe;

	@Override
	public boolean checkRecipe(ItemStack aStack) {
		/*if (!isCorrectMachinePart(mInventory[1])) {
			return false;
		}*/
		GT_Recipe.GT_Recipe_Map map = getRecipeMap();
		if (map == null) {
			return false;
		}
		ArrayList<ItemStack> tInputList = getStoredInputs();
		int tTier = 0;
		if (mInventory[1].getUnlocalizedName().endsWith("1")) {
			tTier = 1;
		}
		if (mInventory[1].getUnlocalizedName().endsWith("2")) {
			tTier = 2;
		}
		if (mInventory[1].getUnlocalizedName().endsWith("3")) {
			tTier = 3;
		}
		if (mInventory[1].getUnlocalizedName().endsWith("4")) {
			tTier = 4;
		}
		if (mInventory[1].getUnlocalizedName().endsWith("5")) {
			tTier = 5;
		}
		if (mInventory[1].getUnlocalizedName().endsWith("6")) {
			tTier = 6;
		}
		if (mInventory[1].getUnlocalizedName().endsWith("7")) {
			tTier = 7;
		}
		if (mInventory[1].getUnlocalizedName().endsWith("8")) {
			tTier = 8;
		}
		ItemStack[] tInputs = (ItemStack[]) tInputList.toArray(new ItemStack[tInputList.size()]);
		ArrayList<FluidStack> tFluidList = getStoredFluids();
		FluidStack[] tFluids = (FluidStack[]) tFluidList.toArray(new FluidStack[tFluidList.size()]);
		if (tInputList.size() > 0 || tFluids.length > 0) {
			GT_Recipe tRecipe = map.findRecipe(getBaseMetaTileEntity(), mLastRecipe, false, gregtech.api.enums.GT_Values.V[tTier], tFluids, tInputs);
			if (tRecipe != null) {
				mLastRecipe = tRecipe;
				this.mEUt = 0;
				this.mOutputItems = null;
				this.mOutputFluids = null;
				int machines = Math.min(16, mInventory[1].stackSize);
				int i = 0;
				for (; i < machines; i++) {
					if (!tRecipe.isRecipeInputEqual(true, tFluids, tInputs)) {
						if (i == 0) {
							return false;
						}
						break;
					}
				}
				this.mMaxProgresstime = tRecipe.mDuration;
				this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
				this.mEfficiencyIncrease = 10000;
				if (tRecipe.mEUt <= 16) {
					this.mEUt = (tRecipe.mEUt * (1 << tTier - 1) * (1 << tTier - 1));
					this.mMaxProgresstime = (tRecipe.mDuration / (1 << tTier - 1));
				} else {
					this.mEUt = tRecipe.mEUt;
					this.mMaxProgresstime = tRecipe.mDuration;
					while (this.mEUt <= gregtech.api.enums.GT_Values.V[(tTier - 1)]) {
						this.mEUt *= 4;
						this.mMaxProgresstime /= 2;
					}
				}
				this.mEUt *= i;
				if (this.mEUt > 0) {
					this.mEUt = (-this.mEUt);
				}
				ItemStack[] tOut = new ItemStack[tRecipe.mOutputs.length];
				for (int h = 0; h < tRecipe.mOutputs.length; h++) {
					tOut[h] = tRecipe.getOutput(h).copy();
					tOut[h].stackSize = 0;
				}
				FluidStack tFOut = null;
				if (tRecipe.getFluidOutput(0) != null) tFOut = tRecipe.getFluidOutput(0).copy();
				for (int f = 0; f < tOut.length; f++) {
					if (tRecipe.mOutputs[f] != null && tOut[f] != null) {
						for (int g = 0; g < i; g++) {
							if (getBaseMetaTileEntity().getRandomNumber(10000) < tRecipe.getOutputChance(f))
								tOut[f].stackSize += tRecipe.mOutputs[f].stackSize;
						}
					}
				}
				if (tFOut != null) {
					int tSize = tFOut.amount;
					tFOut.amount = tSize * i;
				}
				this.mMaxProgresstime = Math.max(1, this.mMaxProgresstime);
				List<ItemStack> overStacks = new ArrayList<ItemStack>();
				for (int f = 0; f < tOut.length; f++) {
					if (tOut[f].getMaxStackSize() < tOut[f].stackSize) {
						while (tOut[f].getMaxStackSize() < tOut[f].stackSize) {
							ItemStack tmp = tOut[f].copy();
							tmp.stackSize = tmp.getMaxStackSize();
							tOut[f].stackSize = tOut[f].stackSize - tOut[f].getMaxStackSize();
							overStacks.add(tmp);
						}
					}
				}
				if (overStacks.size() > 0) {
					ItemStack[] tmp = new ItemStack[overStacks.size()];
					tmp = overStacks.toArray(tmp);
					tOut = ArrayUtils.addAll(tOut, tmp);
				}
				List<ItemStack> tSList = new ArrayList<ItemStack>();
				for (ItemStack tS : tOut) {
					if (tS.stackSize > 0) tSList.add(tS);
				}
				tOut = tSList.toArray(new ItemStack[tSList.size()]);
				this.mOutputItems = tOut;
				this.mOutputFluids = new FluidStack[]{tFOut};
				updateSlots();
				Utils.LOG_WARNING("Centrifuge: True");
                return true;
            }
        }
        Utils.LOG_WARNING("Centrifuge: False");
        return false;
    }

	/*public boolean checkRecipe(ItemStack aStack) {
        ArrayList<ItemStack> tInputList = getStoredInputs();
        Utils.LOG_WARNING("Stored Input Items: "+tInputList.size());
        for (int i = 0; i < tInputList.size() - 1; i++) {
            for (int j = i + 1; j < tInputList.size(); j++) {
                if (GT_Utility.areStacksEqual((ItemStack) tInputList.get(i), (ItemStack) tInputList.get(j))) {
                    if (((ItemStack) tInputList.get(i)).stackSize >= ((ItemStack) tInputList.get(j)).stackSize) {
                        tInputList.remove(j--);
                    } else {
                        tInputList.remove(i--);
                        break;
                    }
                }
            }
        }
        ItemStack[] tInputs = (ItemStack[]) Arrays.copyOfRange(tInputList.toArray(new ItemStack[tInputList.size()]), 0, 2);

        ArrayList<FluidStack> tFluidList = getStoredFluids();
        Utils.LOG_WARNING("Stored Input Fluids: "+tFluidList.size());
        for (int i = 0; i < tFluidList.size() - 1; i++) {
            for (int j = i + 1; j < tFluidList.size(); j++) {
                if (GT_Utility.areFluidsEqual((FluidStack) tFluidList.get(i), (FluidStack) tFluidList.get(j))) {
                    if (((FluidStack) tFluidList.get(i)).amount >= ((FluidStack) tFluidList.get(j)).amount) {
                    	Utils.LOG_WARNING("Removing j from tFluidList");
                        tFluidList.remove(j--);
                    } else {
                    	Utils.LOG_WARNING("Removing i from tFluidList");
                        tFluidList.remove(i--);
                        break;
                    }
                }
            }
        }
        FluidStack[] tFluids = (FluidStack[]) Arrays.copyOfRange(tFluidList.toArray(new FluidStack[tInputList.size()]), 0, 1);
        Utils.LOG_WARNING("Size:"+tInputList.size());
        if (tInputList.size() > 0 || tFluidList.size() > 0) {
        	Utils.LOG_WARNING("Input size > 0");
            long tVoltage = getMaxInputVoltage();
            byte tTier = (byte) Math.max(1, GT_Utility.getTier(tVoltage));
            GT_Recipe tRecipe = GT_Recipe.GT_Recipe_Map.sCentrifugeRecipes.findRecipe(getBaseMetaTileEntity(), false, gregtech.api.enums.GT_Values.V[tTier], tFluids, tInputs);
            if ((tRecipe != null) && (0 >= tRecipe.mSpecialValue) && (tRecipe.isRecipeInputEqual(true, tFluids, tInputs))) {
                this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
                this.mEfficiencyIncrease = 10000;
                if (tRecipe.mEUt <= 16) {
                    this.mEUt = (tRecipe.mEUt * (1 << tTier - 1) * (1 << tTier - 1));
                    this.mMaxProgresstime = (tRecipe.mDuration / (1 << tTier - 1));
                } else {
                    this.mEUt = tRecipe.mEUt;
                    this.mMaxProgresstime = tRecipe.mDuration;
                    while (this.mEUt <= gregtech.api.enums.GT_Values.V[(tTier - 1)]) {
                        this.mEUt *= 4;
                        this.mMaxProgresstime /= 2;
                    }
                }
                if (this.mEUt > 0) {
                    this.mEUt = (-this.mEUt);
                }
                this.mMaxProgresstime = Math.max(1, this.mMaxProgresstime);
                this.mOutputItems = new ItemStack[]{tRecipe.getOutput(0)};
                this.mOutputFluids = new FluidStack[]{tRecipe.getFluidOutput(0)};
                updateSlots();
                Utils.LOG_WARNING("Centrifuge: True");
                return true;
            }
        }
        Utils.LOG_WARNING("Centrifuge: False");
        return false;
    }*/

	@SuppressWarnings("static-method")
	public Block getCasingBlock() {
		return ModBlocks.blockCasingsMisc;
	}

	@SuppressWarnings("static-method")
	public byte getCasingMeta() {
		return 0;
	}

	@SuppressWarnings("static-method")
	public byte getCasingTextureIndex() {
		return 0;
	}

	@Override
	public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
		if (CORE.disableCentrifugeFormation){
			EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(this.getBaseMetaTileEntity().getOwnerName());
			if (!player.getEntityWorld().isRemote && isDisabled == false)
				Utils.messagePlayer(player, "This Multiblock is disabled via the config. [Only re-enable if you're bugtesting.]");
			isDisabled = true;
			return false;
		}
		int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX;
		int yDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetY;
		int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ;
		//Utils.LOG_WARNING("X:"+xDir+" Y:"+yDir+" Z:"+zDir);
		if (!aBaseMetaTileEntity.getAirOffset(xDir, 0, zDir)) {
			return false;
		}
		int tAmount = 0;
		for (int i = -1; i < 2; i++) { //X-Dir
			for (int j = -1; j < 2; j++) { //Z-Dir
				for (int h = -1; h < 2; h++) { //Y-Dir
					if ((h != 0) || (((xDir + i != 0) || (zDir + j != 0)) && ((i != 0) || (j != 0)))) {			

						IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, h, zDir + j);
						//Utils.LOG_WARNING("X:"+tTileEntity.getXCoord()+" Y:"+tTileEntity.getYCoord()+" Z:"+tTileEntity.getZCoord());
						if ((!addMaintenanceToMachineList(tTileEntity, 57)) && (!addInputToMachineList(tTileEntity, 57)) && (!addOutputToMachineList(tTileEntity, 57)) && (!addEnergyInputToMachineList(tTileEntity, 57))) {

							//Maintenance Hatch
							if ((tTileEntity != null) && (tTileEntity.getMetaTileEntity() != null)) {
								if (tTileEntity.getXCoord() == aBaseMetaTileEntity.getXCoord() && tTileEntity.getYCoord() == aBaseMetaTileEntity.getYCoord() && tTileEntity.getZCoord() == (aBaseMetaTileEntity.getZCoord()+2)) {
									if ((tTileEntity.getMetaTileEntity() instanceof GT_MetaTileEntity_Hatch_Maintenance)) {
										Utils.LOG_WARNING("MAINT HATCH IN CORRECT PLACE");
										this.mMaintenanceHatches.add((GT_MetaTileEntity_Hatch_Maintenance) tTileEntity.getMetaTileEntity());
										((GT_MetaTileEntity_Hatch) tTileEntity.getMetaTileEntity()).mMachineBlock = getCasingTextureIndex();
									} else {
										return false;
									}
								}	
								else {
									Utils.LOG_WARNING("MAINT HATCH IN WRONG PLACE");
								}
							}

							if (aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j) != ModBlocks.blockCasingsMisc) {
								return false;
							}
							if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, h, zDir + j) != 0) {
								return false;
							}
							tAmount++;

						}
					}
				}
			}
		}		
		return tAmount >= 16;
	}

	@SuppressWarnings("static-method")
	public boolean ignoreController(Block tTileEntity) {
		if (!controller && tTileEntity == GregTech_API.sBlockMachines) {
			return true;
		}
		return false;
	}

	@Override
	public int getMaxEfficiency(ItemStack aStack) {
		return 10000;
	}

	@Override
	public int getPollutionPerTick(ItemStack aStack) {
		return 0;
	}

	@Override
	public int getAmountOfOutputs() {
		return 1;
	}

	@Override
	public boolean explodesOnComponentBreak(ItemStack aStack) {
		return false;
	}

}