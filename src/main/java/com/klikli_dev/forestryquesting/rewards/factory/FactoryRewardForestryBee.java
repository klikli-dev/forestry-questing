package com.tao.forestryquesting.rewards.factory;

import betterquesting.api.enums.EnumSaveType;
import betterquesting.api.misc.IFactory;
import com.google.gson.JsonObject;
import com.tao.forestryquesting.ForestryQuestingMod;
import com.tao.forestryquesting.rewards.RewardForestryBee;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Tao on 12/14/2017.
 */
public class FactoryRewardForestryBee implements IFactory<RewardForestryBee> {
    public static final FactoryRewardForestryBee instance = new FactoryRewardForestryBee();

    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ForestryQuestingMod.MOD_ID, "reward.bee");
    }

    public RewardForestryBee createNew() {
        return new RewardForestryBee();
    }

    public RewardForestryBee loadFromJson(JsonObject json) {
        RewardForestryBee reward = createNew();
        reward.readFromJson(json, EnumSaveType.CONFIG);
        return reward;
    }
}

