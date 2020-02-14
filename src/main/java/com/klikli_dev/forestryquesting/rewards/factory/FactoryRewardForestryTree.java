package com.tao.forestryquesting.rewards.factory;

import betterquesting.api.enums.EnumSaveType;
import com.google.gson.JsonObject;
import com.tao.forestryquesting.ForestryQuestingMod;
import com.tao.forestryquesting.rewards.RewardForestryBee;
import com.tao.forestryquesting.rewards.RewardForestryTree;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Tao on 12/14/2017.
 */
public class FactoryRewardForestryTree extends FactoryRewardForestryBee {
    public static final FactoryRewardForestryTree instance = new FactoryRewardForestryTree();


    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ForestryQuestingMod.MOD_ID, "reward.tree");
    }

    public RewardForestryBee createNew() {
        return new RewardForestryTree();
    }

    public RewardForestryBee loadFromJson(JsonObject json) {
        RewardForestryBee reward = createNew();
        reward.readFromJson(json, EnumSaveType.CONFIG);
        return reward;
    }
}

