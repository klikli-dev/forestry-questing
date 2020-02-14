package com.tao.forestryquesting.util;

import betterquesting.api.placeholders.PlaceholderConverter;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Tao on 12/15/2017.
 */
public class FQJsonHelper {
    /**
     * Converts a JsonObject to an ItemStack. May return a placeholder if the correct mods are not installed</br>
     * This should be the standard way to load items into quests in order to retain all potential data
     */
    public static BigItemStack JsonToItemStack(JsonObject json) {
        if (json == null || !json.has("id") || !json.get("id").isJsonPrimitive()) {
            return new BigItemStack(Blocks.STONE);
        }

        JsonPrimitive jID = json.get("id").getAsJsonPrimitive();
        int count = JsonHelper.GetNumber(json, "Count", 1).intValue();
        String oreDict = JsonHelper.GetString(json, "OreDict", "");
        int damage = JsonHelper.GetNumber(json, "Damage", OreDictionary.WILDCARD_VALUE).intValue();
        damage = damage >= 0 ? damage : OreDictionary.WILDCARD_VALUE;

        Item item;

        if (jID.isNumber()) {
            item = Item.REGISTRY.getObjectById(jID.getAsInt()); // Old format (numbers)
        } else {
            item = Item.REGISTRY.getObject(new ResourceLocation(jID.getAsString())); // New format (names)
        }

        NBTTagCompound tags = null;
        if (json.has("tag")) {
            tags = FQNBTConverter.JSONtoNBT_Object(JsonHelper.GetObject(json, "tag"), new NBTTagCompound(), true);
        }

        return PlaceholderConverter.convertItem(item, jID.getAsString(), count, damage, oreDict, tags);
    }


    /**
     * Use this for quests instead of converter NBT because this doesn't use ID numbers
     */
    public static JsonObject ItemStackToJson(BigItemStack stack, JsonObject json) {
        if (stack == null) {
            return json;
        }

        json.addProperty("id", Item.REGISTRY.getNameForObject(stack.getBaseStack().getItem()).toString());
        json.addProperty("Count", stack.stackSize);
        json.addProperty("OreDict", stack.oreDict);
        json.addProperty("Damage", stack.getBaseStack().getItemDamage());
        if (stack.HasTagCompound()) {
            json.add("tag", FQNBTConverter.NBTtoJSON_Compound(stack.GetTagCompound(), new JsonObject(), true));
        }
        return json;
    }
}
