package com.tao.forestryquesting.tasks.factory;

import betterquesting.api.enums.EnumSaveType;
import betterquesting.api.misc.IFactory;
import com.google.gson.JsonObject;
import com.tao.forestryquesting.ForestryQuestingMod;
import com.tao.forestryquesting.tasks.TaskBlockPlace;
import net.minecraft.util.ResourceLocation;

public final class FactoryTaskBlockPlace implements IFactory<TaskBlockPlace> {
    public static final FactoryTaskBlockPlace instance = new FactoryTaskBlockPlace();

    private FactoryTaskBlockPlace() {
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ForestryQuestingMod.MOD_ID, "task.block_place");
    }

    @Override
    public TaskBlockPlace createNew() {
        return new TaskBlockPlace();
    }

    @Override
    public TaskBlockPlace loadFromJson(JsonObject json) {
        TaskBlockPlace task = createNew();
        task.readFromJson(json, EnumSaveType.CONFIG);
        return task;
    }

}
