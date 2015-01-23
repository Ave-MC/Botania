/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 License
 * (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 * 
 * File Created @ [Jan 23, 2015, 9:22:10 PM (GMT)]
 */
package vazkii.botania.client.core.handler;

import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ItemsRemainingRenderHandler {
	
	private static int maxTicks = 30;
	private static int leaveTicks = 20;
	
	private static ItemStack stack;
	private static int ticks, count;
	
	@SideOnly(Side.CLIENT)
	public static void render(ScaledResolution resolution, float partTicks) {
		if(ticks > 0) {
			int pos = maxTicks - ticks;
			Minecraft mc = Minecraft.getMinecraft();
			int x = resolution.getScaledWidth() / 2 + 10 + Math.max(0, pos - leaveTicks);
			int y = resolution.getScaledHeight() / 2;
			
			int start = maxTicks - leaveTicks;
			float alpha = (ticks + partTicks) > start ? 1F : (float) (ticks + partTicks) / (float) start;

			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GL11.glColor4f(1F, 1F, 1F, alpha);
			RenderHelper.enableGUIStandardItemLighting();
			int xp = x + (int) (16F * (1F - alpha));
			GL11.glTranslatef(xp, y, 0F);
			GL11.glScalef(alpha, 1F, 1F);
			RenderItem.getInstance().renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, stack, 0, 0);
			GL11.glScalef(1F / alpha,1F, 1F);
			GL11.glTranslatef(-xp, -y, 0F);
			RenderHelper.disableStandardItemLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			
			int max = stack.getMaxStackSize();
			int stacks = count / max;
			int rem = count % max;
			
			int color = 0x00FFFFFF | ((int) (alpha * 0xFF) << 24);
			if(stacks == 0)
				mc.fontRenderer.drawStringWithShadow("" + count, x + 20, y + 6, color);
			else mc.fontRenderer.drawStringWithShadow(count + " (" + stacks + "*" + max + "+" + rem + ")", x + 20, y + 6, color);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void tick() {
		if(ticks > 0)
			--ticks;
	}
	
	public static void set(ItemStack stack, int count) {
		ItemsRemainingRenderHandler.stack = stack;
		ItemsRemainingRenderHandler.count = count;
		ticks = maxTicks;
	}
	
	public static void set(EntityPlayer player, ItemStack displayStack, Pattern pattern) {
		int count = 0;
		for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if(stack != null && pattern.matcher(stack.getUnlocalizedName()).find())
				count += stack.stackSize;
		}
		
		set(displayStack, count);
	}
	
}
