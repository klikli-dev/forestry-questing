package com.tao.forestryquesting.client.gui.rewards;

import betterquesting.api.client.gui.GuiElement;
import betterquesting.api.client.gui.lists.GuiScrollingText;
import betterquesting.api.client.gui.misc.IGuiEmbedded;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.RenderUtils;
import com.tao.forestryquesting.rewards.RewardForestryBee;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

/**
 * Created by Tao on 12/15/2017.
 */
public class GuiRewardForestry extends GuiElement implements IGuiEmbedded {
    private Minecraft mc = Minecraft.getMinecraft();
    private GuiScrollingText rewardScroll;
    private int posX = 0;
    private int posY = 0;
    private int sizeY = 0;

    public GuiRewardForestry(RewardForestryBee reward, int posX, int posY, int sizeX, int sizeY) {
        this.posX = posX;
        this.posY = posY;
        this.sizeY = sizeY;


        this.rewardScroll = new GuiScrollingText(this.mc, posX + 36, posY, sizeX - 36, sizeY);
        Iterator var6 = reward.items.iterator();

        StringBuffer itemBuffer = new StringBuffer();
        for (BigItemStack stack : reward.items) {
            itemBuffer.append(stack.stackSize + "x " + stack.getBaseStack().getDisplayName() + System.lineSeparator());

        }
        this.rewardScroll.SetText(itemBuffer.toString());

    }

    public void drawBackground(int mx, int my, float partialTick) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) this.posX, (float) (this.posY + this.sizeY / 2 - 16), 0.0F);
        GlStateManager.scale(2.0F, 2.0F, 1.0F);
        GlStateManager.enableDepth();
        RenderUtils.RenderItemStack(this.mc, new ItemStack(Blocks.CHEST), 0, 0, "");
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
        this.rewardScroll.drawBackground(mx, my, partialTick);
    }

    public void drawForeground(int mx, int my, float partialTick) {
        this.rewardScroll.drawForeground(mx, my, partialTick);
    }

    public void onMouseClick(int mx, int my, int button) {
    }

    public void onMouseScroll(int mx, int my, int scroll) {
        this.rewardScroll.onMouseScroll(mx, my, scroll);
    }

    public void onKeyTyped(char c, int keyCode) {
    }
}
