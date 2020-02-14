package com.tao.forestryquesting.rewards;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.IGuiEmbedded;
import betterquesting.api.enums.EnumSaveType;
import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import bq_standard.NBTReplaceUtil;
import bq_standard.rewards.RewardItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tao.forestryquesting.ForestryQuestingMod;
import com.tao.forestryquesting.client.gui.rewards.GuiRewardForestry;
import com.tao.forestryquesting.rewards.factory.FactoryRewardForestryBee;
import com.tao.forestryquesting.util.FQJsonHelper;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.genetics.IAllele;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tao on 12/14/2017.
 */
public class RewardForestryBee extends RewardItem {
    public ArrayList<BigItemStack> items = new ArrayList<>();

    @Override
    public ResourceLocation getFactoryID() {
        return FactoryRewardForestryBee.instance.getRegistryName();
    }

    @Override
    public String getUnlocalisedName() {
        return ForestryQuestingMod.MOD_ID + ".reward.bee";
    }

    @Override
    public void claimReward(EntityPlayer player, IQuest quest) {
        for (BigItemStack r : this.items) {
            BigItemStack stack = r.copy();
            for (ItemStack s : stack.getCombinedStacks()) {
                if (s.getTagCompound() != null) {
                    s.setTagCompound(NBTReplaceUtil.replaceStrings(s.getTagCompound(), "VAR_NAME", player.getName()));
                    s.setTagCompound(NBTReplaceUtil.replaceStrings(s.getTagCompound(), "VAR_UUID", QuestingAPI.getQuestingUUID(player).toString()));
                }
                if (!player.inventory.addItemStackToInventory(s)) {
                    player.dropItem(s, true, false);
                }
            }
        }
    }

    @Override
    public void readFromJson(JsonObject json, EnumSaveType saveType) {
        this.items = new ArrayList<>();
        for (JsonElement entry : JsonHelper.GetArray(json, "rewards")) {
            if ((entry != null) && (entry.isJsonObject())) {
                try {
                    BigItemStack item = FQJsonHelper.JsonToItemStack(entry.getAsJsonObject());
                    if (item != null) {
                        this.items.add(item);
                    } else {
                        continue;
                    }
                } catch (Exception e) {
                    ForestryQuestingMod.logger.log(Level.ERROR, "Unable to load reward item data", e);
                }
            }
        }
    }

    @Override
    public JsonObject writeToJson(JsonObject json, EnumSaveType saveType) {
        JsonArray rJson = new JsonArray();

        List<BigItemStack> cleanedItems = new ArrayList<>();
        for (BigItemStack stack : this.items) {
            if (!stack.getBaseStack().hasTagCompound()) {
                IAllele[] template = BeeManager.beeRoot.getTemplate("forestry.speciesCommon");
                IBeeGenome genome = BeeManager.beeRoot.templateAsGenome(template);
                IBee bee = BeeManager.beeRoot.getBee(genome);
                cleanedItems.add(new BigItemStack(BeeManager.beeRoot.getMemberStack(bee, EnumBeeType.PRINCESS)));
            } else {
                cleanedItems.add(stack);
            }
        }

        for (BigItemStack stack : cleanedItems) {
            rJson.add(FQJsonHelper.ItemStackToJson(stack, new JsonObject()));
        }
        json.add("rewards", rJson);
        return json;
    }

    @Override
    public IGuiEmbedded getRewardGui(int posX, int posY, int sizeX, int sizeY, IQuest quest) {
        return new GuiRewardForestry(this, posX, posY, sizeX, sizeY);
    }
}
