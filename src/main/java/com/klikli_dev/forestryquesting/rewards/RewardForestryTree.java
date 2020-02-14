package com.tao.forestryquesting.rewards;

import betterquesting.api.enums.EnumSaveType;
import betterquesting.api.utils.BigItemStack;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tao.forestryquesting.ForestryQuestingMod;
import com.tao.forestryquesting.rewards.factory.FactoryRewardForestryTree;
import com.tao.forestryquesting.util.FQJsonHelper;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tao on 12/14/2017.
 */
public class RewardForestryTree extends RewardForestryBee {
    @Override
    public ResourceLocation getFactoryID() {
        return FactoryRewardForestryTree.instance.getRegistryName();
    }

    @Override
    public String getUnlocalisedName() {
        return ForestryQuestingMod.MOD_ID + ".reward.tree";
    }

    @Override
    public JsonObject writeToJson(JsonObject json, EnumSaveType saveType) {
        JsonArray rJson = new JsonArray();

        List<BigItemStack> cleanedItems = new ArrayList<>();
        for (BigItemStack stack : this.items) {
            if (!stack.getBaseStack().hasTagCompound()) {
                IAllele[] template = TreeManager.treeRoot.getTemplate("forestry.treeOak");
                ITreeGenome genome = TreeManager.treeRoot.templateAsGenome(template);
                ITree tree = TreeManager.treeRoot.getTree(null, genome);
                cleanedItems.add(new BigItemStack(TreeManager.treeRoot.getMemberStack(tree, EnumGermlingType.SAPLING)));
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
}
