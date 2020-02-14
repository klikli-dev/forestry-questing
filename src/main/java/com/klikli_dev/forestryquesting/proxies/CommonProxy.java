package com.tao.forestryquesting.proxies;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.rewards.IRewardRegistry;
import betterquesting.api.questing.tasks.ITaskRegistry;
import com.tao.forestryquesting.handlers.ForgeEventHandlers;
import com.tao.forestryquesting.rewards.factory.FactoryRewardForestryBee;
import com.tao.forestryquesting.rewards.factory.FactoryRewardForestryTree;
import com.tao.forestryquesting.tasks.factory.FactoryTaskBlockPlace;
import com.tao.forestryquesting.tasks.factory.FactoryTaskForestryRetrieval;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class CommonProxy {
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
    }

    public boolean isClient() {
        return false;
    }

    public void registerExpansion() {
        ITaskRegistry taskReg = QuestingAPI.getAPI(ApiReference.TASK_REG);
        taskReg.registerTask(FactoryTaskForestryRetrieval.instance);
        taskReg.registerTask(FactoryTaskBlockPlace.instance);

        IRewardRegistry rewardReg = QuestingAPI.getAPI(ApiReference.REWARD_REG);
        rewardReg.registerReward(FactoryRewardForestryBee.instance);
        rewardReg.registerReward(FactoryRewardForestryTree.instance);
    }
}
