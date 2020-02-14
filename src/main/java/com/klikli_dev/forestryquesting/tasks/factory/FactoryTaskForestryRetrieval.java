package com.tao.forestryquesting.tasks.factory;

import betterquesting.api.enums.EnumSaveType;
import betterquesting.api.misc.IFactory;
import com.google.gson.JsonObject;
import com.tao.forestryquesting.ForestryQuestingMod;
import com.tao.forestryquesting.tasks.TaskForestryRetrieval;
import net.minecraft.util.ResourceLocation;

public final class FactoryTaskForestryRetrieval implements IFactory<TaskForestryRetrieval> {
    public static final FactoryTaskForestryRetrieval instance = new FactoryTaskForestryRetrieval();

    private FactoryTaskForestryRetrieval() {
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ForestryQuestingMod.MOD_ID, "task.retrieval");
    }

    @Override
    public TaskForestryRetrieval createNew() {
        return new TaskForestryRetrieval();
    }

    @Override
    public TaskForestryRetrieval loadFromJson(JsonObject json) {
        TaskForestryRetrieval task = createNew();
        task.readFromJson(json, EnumSaveType.CONFIG);
        return task;
    }

}
