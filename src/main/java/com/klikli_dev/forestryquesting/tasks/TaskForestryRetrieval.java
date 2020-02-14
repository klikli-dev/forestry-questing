package com.tao.forestryquesting.tasks;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.IGuiEmbedded;
import betterquesting.api.enums.EnumSaveType;
import betterquesting.api.jdoc.IJsonDoc;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.party.IParty;
import betterquesting.api.questing.tasks.IItemTask;
import betterquesting.api.questing.tasks.IProgression;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.questing.tasks.ITickableTask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tao.forestryquesting.ForestryQuestingMod;
import com.tao.forestryquesting.client.gui.tasks.GuiTaskForestryRetrieval;
import com.tao.forestryquesting.tasks.factory.FactoryTaskForestryRetrieval;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

public class TaskForestryRetrieval implements ITask, IProgression<int[]>, IItemTask, ITickableTask {
    public ArrayList<BigItemStack> requiredItems = new ArrayList<>();
    public HashMap<UUID, int[]> userProgress = new HashMap<>();
    public boolean consume = true;
    public boolean autoConsume = false;
    public boolean onlyMated = false;
    private ArrayList<UUID> completeUsers = new ArrayList<>();

    @Override
    public String getUnlocalisedName() {
        return ForestryQuestingMod.MOD_ID + ".task.retrieval";
    }

    @Override
    public ResourceLocation getFactoryID() {
        return FactoryTaskForestryRetrieval.instance.getRegistryName();
    }

    @Override
    public boolean isComplete(UUID uuid) {
        return completeUsers.contains(uuid);
    }

    @Override
    public void setComplete(UUID uuid) {
        if (!completeUsers.contains(uuid)) {
            completeUsers.add(uuid);
        }
    }

    @Override
    public void update(EntityPlayer entityPlayer, IQuest iQuest) {
    }

    @Override
    public void updateTask(EntityPlayer player, IQuest quest) {
        if (player.ticksExisted % 60 == 0 && !QuestingAPI.getAPI(ApiReference.SETTINGS).getProperty(NativeProps.EDIT_MODE)) {
            if ((!this.consume) || (this.autoConsume)) {
                detect(player, quest);
            } else {
                boolean flag = true;

                int[] totalProgress = (quest == null) || !quest.getProperties().getProperty(NativeProps.GLOBAL) ? getPartyProgress(QuestingAPI.getQuestingUUID(player)) : getGlobalProgress();
                for (int j = 0; j < this.requiredItems.size(); j++) {
                    BigItemStack rStack = this.requiredItems.get(j);
                    if (rStack != null || totalProgress[j] >= rStack.stackSize) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    setComplete(QuestingAPI.getQuestingUUID(player));
                }
            }
        }
    }

    @Override
    public void detect(EntityPlayer player, IQuest quest) {
        UUID playerID = QuestingAPI.getQuestingUUID(player);

        if (player.inventory == null || isComplete(playerID)) {
            return;
        }

        int[] progress = this.getUsersProgress(playerID);

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            for (int j = 0; j < requiredItems.size(); j++) {
                ItemStack stack = player.inventory.getStackInSlot(i);

                if (stack == null) {
                    break;
                }

                BigItemStack rStack = requiredItems.get(j);

                if (rStack == null || progress[j] >= rStack.stackSize) {
                    continue;
                }

                if(onlyMated && !isMated(stack))
                    continue;

                if (matchSpecies(rStack.getBaseStack(), stack)) {
                    int remaining = rStack.stackSize - progress[j];
                    if (consume) {
                        ItemStack removed = player.inventory.decrStackSize(i, remaining);
                        progress[j] += removed.stackSize;
                    } else {
                        progress[j] += Math.min(remaining, stack.stackSize);
                    }
                }
            }
        }

        boolean flag = true;
        int[] totalProgress = progress;

        if (consume) {
            setUserProgress(playerID, progress);
            totalProgress = quest == null || !quest.getProperties().getProperty(NativeProps.GLOBAL) ? getPartyProgress(playerID) : getGlobalProgress();
        }

        for (int j = 0; j < requiredItems.size(); j++) {
            BigItemStack rStack = requiredItems.get(j);

            if (rStack == null || totalProgress[j] >= rStack.stackSize) {
                continue;
            }

            flag = false;
            break;
        }

        if (flag) {
            setComplete(playerID);
        }
    }

    public JsonObject writeToJson(JsonObject json, EnumSaveType saveType) {
        if (saveType == EnumSaveType.PROGRESS) {
            return this.writeProgressToJson(json);
        } else if (saveType != EnumSaveType.CONFIG) {
            return json;
        }
        json.addProperty("consume", consume);
        json.addProperty("autoConsume", autoConsume);
        json.addProperty("onlyMated", onlyMated);
        JsonArray itemArray = new JsonArray();
        for (BigItemStack stack : this.requiredItems) {
            boolean isForestryOrganism = stack.getBaseStack().hasTagCompound();
            if (!isForestryOrganism) {
                NBTTagList chromosomes = new NBTTagList();
                NBTTagCompound species = new NBTTagCompound();
                species.setString("UID0", "forestry.speciesCommon");
                species.setString("UID1", "forestry.speciesCommon");
                species.setByte("Slot", (byte) 0);
                species.setByte("MatchAny", (byte) 0);
                chromosomes.appendTag(species);
                NBTTagCompound genome = new NBTTagCompound();
                genome.setTag("Chromosomes", chromosomes);
                NBTTagCompound itemData = new NBTTagCompound();
                itemData.setTag("Genome", genome);
                stack.getBaseStack().setTagCompound(itemData);

            }
            JsonObject itemJson = JsonHelper.ItemStackToJson(stack, new JsonObject());
            if (!isForestryOrganism) {
                itemJson.addProperty("id", "forestry:beePrincessGE");
            }
            itemArray.add(itemJson);
        }
        json.add("requiredItems", itemArray);
        return json;
    }

    public void readFromJson(JsonObject json, EnumSaveType saveType) {
        if (saveType == EnumSaveType.PROGRESS) {
            this.readProgressFromJson(json);
            return;
        } else if (saveType != EnumSaveType.CONFIG) {
            return;
        }

        consume = JsonHelper.GetBoolean(json, "consume", true);
        autoConsume = JsonHelper.GetBoolean(json, "autoConsume", false);
        onlyMated = JsonHelper.GetBoolean(json, "onlyMated", false);

        requiredItems = new ArrayList<BigItemStack>();
        for (JsonElement entry : JsonHelper.GetArray(json, "requiredItems")) {
            if (entry == null || !entry.isJsonObject()) {
                continue;
            }

            BigItemStack item = JsonHelper.JsonToItemStack(entry.getAsJsonObject());

            if (item != null) {
                requiredItems.add(item);
            } else {
                continue;
            }
        }
    }

    public void readProgressFromJson(JsonObject json) {
        completeUsers = new ArrayList<>();
        for (JsonElement entry : JsonHelper.GetArray(json, "completeUsers")) {
            if (entry == null || !entry.isJsonPrimitive()) {
                continue;
            }

            try {
                completeUsers.add(UUID.fromString(entry.getAsString()));
            } catch (Exception e) {
                ForestryQuestingMod.logger.log(Level.ERROR, "Unable to load UUID for task", e);
            }
        }

        userProgress = new HashMap<>();
        for (JsonElement entry : JsonHelper.GetArray(json, "userProgress")) {
            if (entry == null || !entry.isJsonObject()) {
                continue;
            }

            UUID uuid;
            try {
                uuid = UUID.fromString(JsonHelper.GetString(entry.getAsJsonObject(), "uuid", ""));
            } catch (Exception e) {
                ForestryQuestingMod.logger.log(Level.ERROR, "Unable to load user progress for task", e);
                continue;
            }

            int[] data = new int[requiredItems.size()];
            JsonArray dJson = JsonHelper.GetArray(entry.getAsJsonObject(), "data");
            for (int i = 0; i < data.length && i < dJson.size(); i++) {
                try {
                    data[i] = dJson.get(i).getAsInt();
                } catch (Exception e) {
                    ForestryQuestingMod.logger.log(Level.ERROR, "Incorrect task progress format", e);
                }
            }

            userProgress.put(uuid, data);
        }
    }

    public JsonObject writeProgressToJson(JsonObject json) {
        JsonArray jArray = new JsonArray();
        for (UUID uuid : completeUsers) {
            jArray.add(new JsonPrimitive(uuid.toString()));
        }
        json.add("completeUsers", jArray);

        JsonArray progArray = new JsonArray();
        for (Entry<UUID, int[]> entry : userProgress.entrySet()) {
            JsonObject pJson = new JsonObject();
            pJson.addProperty("uuid", entry.getKey().toString());
            JsonArray pArray = new JsonArray();
            for (int i : entry.getValue()) {
                pArray.add(new JsonPrimitive(i));
            }
            pJson.add("data", pArray);
            progArray.add(pJson);
        }
        json.add("userProgress", progArray);

        return json;
    }

    @Override
    public void resetUser(UUID uuid) {
        completeUsers.remove(uuid);
        userProgress.remove(uuid);
    }

    @Override
    public void resetAll() {
        completeUsers.clear();
        userProgress.clear();
    }

    @Override
    public float getParticipation(UUID uuid) {
        if (requiredItems.size() <= 0) {
            return 1F;
        }

        float total = 0F;

        int[] progress = getUsersProgress(uuid);
        for (int i = 0; i < requiredItems.size(); i++) {
            BigItemStack rStack = requiredItems.get(i);
            total += progress[i] / (float) rStack.stackSize;
        }

        return total / (float) requiredItems.size();
    }

    @Override
    public IGuiEmbedded getTaskGui(int posX, int posY, int sizeX, int sizeY, IQuest quest) {
        return new GuiTaskForestryRetrieval(this, quest, posX, posY, sizeX, sizeY);
    }

    @Override
    public boolean canAcceptItem(UUID owner, ItemStack stack) {
        if (owner == null || stack == null || !consume || isComplete(owner) || requiredItems.size() <= 0) {
            return false;
        }

        int[] progress = getUsersProgress(owner);

        for (int j = 0; j < requiredItems.size(); j++) {
            BigItemStack rStack = requiredItems.get(j);

            if (rStack == null || progress[j] >= rStack.stackSize) {
                continue;
            }

            if(onlyMated && !isMated(stack))
                continue;

            if (matchSpecies(rStack.getBaseStack(), stack))
                return true;
        }

        return false;
    }

    public boolean matchSpecies(ItemStack a, ItemStack b) {
        if (a.hasTagCompound() && b.hasTagCompound() && a.getTagCompound().hasKey("Genome") && b.getTagCompound().hasKey("Genome")) {
            NBTTagCompound genomeA = a.getTagCompound().getCompoundTag("Genome");
            NBTTagCompound genomeB = b.getTagCompound().getCompoundTag("Genome");
            if (genomeA.hasNoTags() || genomeB.hasNoTags())
                return false;

            NBTTagList chromosomesA = genomeA.getTagList("Chromosomes", 10);
            NBTTagList chromosomesB = genomeB.getTagList("Chromosomes", 10);
            if (chromosomesA.hasNoTags() || chromosomesB.hasNoTags())
                return false;

            NBTTagCompound speciesChromosomeA = chromosomesA.getCompoundTagAt(0);
            NBTTagCompound speciesChromosomeB = chromosomesB.getCompoundTagAt(0);

            return speciesChromosomeA.getString("UID0").equals(speciesChromosomeB.getString("UID0"))
                    || speciesChromosomeA.getBoolean("MatchAny")
                    || speciesChromosomeB.getBoolean("MatchAny");
        }
        return false;
    }

    public boolean isMated(ItemStack item)
    {
        return item.hasTagCompound() && item.getTagCompound().hasKey("Mate");
    }

    @Override
    public ItemStack submitItem(UUID owner, ItemStack input) {
        ItemStack stack = input;

        if (owner == null || stack == null || !consume || isComplete(owner)) {
            return stack;
        }

        int[] progress = getUsersProgress(owner);

        for (int j = 0; j < requiredItems.size(); j++) {
            if (stack == null) {
                break;
            }

            BigItemStack rStack = requiredItems.get(j);

            if (rStack == null || progress[j] >= rStack.stackSize) {
                continue;
            }

            int remaining = rStack.stackSize - progress[j];

            if (matchSpecies(rStack.getBaseStack(), stack)) {
                int removed = Math.min(stack.stackSize, remaining);
                stack.stackSize -= removed;
                progress[j] += removed;

                if (stack.stackSize <= 0) {
                    break;
                }
            }
        }

        setUserProgress(owner, progress);

        if (stack == null || stack.stackSize <= 0) {
            return null;
        } else {
            return stack;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getTaskEditor(GuiScreen parent, IQuest quest)//, JsonObject data)
    {
        return null;//new GuiNbtRetrievalEditor(parent, this, quest);
    }

    @Override
    public void setUserProgress(UUID uuid, int[] progress) {
        userProgress.put(uuid, progress);
    }

    @Override
    public int[] getUsersProgress(UUID... users) {
        int[] progress = new int[requiredItems.size()];

        for (UUID uuid : users) {
            int[] tmp = userProgress.get(uuid);

            if (tmp == null || tmp.length != requiredItems.size()) {
                continue;
            }

            for (int n = 0; n < progress.length; n++) {
                progress[n] += tmp[n];
            }
        }

        return progress == null || progress.length != requiredItems.size() ? new int[requiredItems.size()] : progress;
    }

    public int[] getPartyProgress(UUID uuid) {
        int[] total = new int[requiredItems.size()];

        IParty party = QuestingAPI.getAPI(ApiReference.PARTY_DB).getUserParty(uuid);

        if (party == null) {
            return getUsersProgress(uuid);
        } else {
            for (UUID mem : party.getMembers()) {
                if (mem != null && party.getStatus(mem).ordinal() <= 0) {
                    continue;
                }

                int[] progress = getUsersProgress(mem);

                for (int i = 0; i < progress.length; i++) {
                    total[i] += progress[i];
                }
            }
        }

        return total;
    }

    @Override
    public int[] getGlobalProgress() {
        int[] total = new int[requiredItems.size()];

        for (int[] up : userProgress.values()) {
            if (up == null) {
                continue;
            }

            int[] progress = up.length != requiredItems.size() ? new int[requiredItems.size()] : up;

            for (int i = 0; i < progress.length; i++) {
                total[i] += progress[i];
            }
        }

        return total;
    }

    @Override
    public IJsonDoc getDocumentation() {
        return null;
    }
}
