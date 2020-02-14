package com.tao.forestryquesting.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Collections;
import java.util.List;

public class CommandSpecies implements ICommand {

    @Override
    public String getCommandName() {
        return "species";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return getCommandName();
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;

            String species = getSpecies(player.getHeldItemMainhand());
            StringSelection speciesClipboard = new StringSelection(species);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(speciesClipboard, speciesClipboard);
            player.addChatComponentMessage(new TextComponentString(species == null ? "This item has no genome!" : species));
        }
    }

    private String getSpecies(ItemStack item)
    {
        try
        {
            if (item != null && item.hasTagCompound()) {
                if(item.getTagCompound().hasKey("Genome"))
                {
                    NBTTagCompound genome = item.getTagCompound().getCompoundTag("Genome");
                    if (!genome.hasNoTags())
                    {
                        NBTTagList chromosomes = genome.getTagList("Chromosomes", 10);
                        if(!chromosomes.hasNoTags())
                        {
                            NBTTagCompound speciesChromosome = chromosomes.getCompoundTagAt(0);
                            return speciesChromosome.getString("UID0");
                        }
                    }
                }
                if(item.getTagCompound().hasKey("samples"))
                {
                    NBTTagList samples = item.getTagCompound().getTagList("samples", 10);
                    if(!samples.hasNoTags())
                    {
                        NBTTagCompound speciesChromosome = samples.getCompoundTagAt(0);
                        return speciesChromosome.getString("allele");
                    }
                }
            }
        }
        catch(Exception e)
        {
        }
        return null;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return Collections.emptyList();
    }


    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return getCommandName().compareTo(o.getCommandName());
    }
}
