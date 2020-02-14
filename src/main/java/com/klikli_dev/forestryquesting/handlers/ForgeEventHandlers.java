package com.tao.forestryquesting.handlers;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.QuestCache;
import com.tao.forestryquesting.tasks.TaskBlockPlace;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (event.getPlayer() == null || event.getPlayer().worldObj.isRemote) {
            return;
        }

        for (Map.Entry<TaskBlockPlace, IQuest> entry : QuestCache.INSTANCE.getActiveTasks(QuestingAPI.getQuestingUUID(event.getPlayer()), TaskBlockPlace.class).entrySet()) {
            entry.getKey().onBlockPlace(entry.getValue(), event.getPlayer(), event.getState(), event.getPos());
        }
    }
}
