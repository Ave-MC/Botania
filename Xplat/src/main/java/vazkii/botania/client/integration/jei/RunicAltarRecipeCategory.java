/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.integration.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.RunicAltarRecipe;
import vazkii.botania.client.gui.HUDHandler;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import vazkii.botania.common.lib.LibMisc;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class RunicAltarRecipeCategory implements IRecipeCategory<RunicAltarRecipe> {

	public static final RecipeType<RunicAltarRecipe> TYPE =
			RecipeType.create(LibMisc.MOD_ID, "runic_altar", RunicAltarRecipe.class);
	private final IDrawable background;
	private final Component localizedName;
	private final IDrawable overlay;
	private final IDrawable icon;

	public RunicAltarRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(114, 104);
		localizedName = Component.translatable("botania.nei.runicAltar");
		overlay = guiHelper.createDrawable(prefix("textures/gui/petal_overlay.png"),
				17, 11, 114, 82);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BotaniaBlocks.runeAltar));
	}

	@NotNull
	@Override
	public RecipeType<RunicAltarRecipe> getRecipeType() {
		return TYPE;
	}

	@NotNull
	@Override
	public Component getTitle() {
		return localizedName;
	}

	@NotNull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@NotNull
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(RunicAltarRecipe recipe, @NotNull IRecipeSlotsView slotsView, @NotNull PoseStack ms, double mouseX, double mouseY) {
		RenderSystem.enableBlend();
		overlay.draw(ms, 0, 4);
		HUDHandler.renderManaBar(ms, 6, 98, 0x0000FF, 0.75F, recipe.getManaUsage(), ManaPoolBlockEntity.MAX_MANA / 10);
		RenderSystem.disableBlend();
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull RunicAltarRecipe recipe, @NotNull IFocusGroup focusGroup) {
		builder.addSlot(RecipeIngredientRole.CATALYST, 48, 45)
				.addItemStack(new ItemStack(BotaniaBlocks.runeAltar));

		double angleBetweenEach = 360.0 / recipe.getIngredients().size();
		Vec2 point = new Vec2(48, 13), center = new Vec2(48, 45);

		for (var ingr : recipe.getIngredients()) {
			builder.addSlot(RecipeIngredientRole.INPUT, (int) point.x, (int) point.y)
					.addIngredients(ingr);
			point = PetalApothecaryRecipeCategory.rotatePointAbout(point, center, angleBetweenEach);
		}

		// TODO 1.19.4 figure out the proper way to get a registry access
		builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 10)
				.addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
	}

}
