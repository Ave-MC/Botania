/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.components;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;


import dev.onyxstudios.cca.api.v3.component.Component;

public class LooniumComponent implements Component {
	private ItemStack toDrop = ItemStack.EMPTY;

	public ItemStack getDrop() {
		return toDrop;
	}

	public void setDrop(ItemStack stack) {
		this.toDrop = stack;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return obj instanceof LooniumComponent && ItemStack.areEqual(((LooniumComponent) obj).toDrop, toDrop);
	}

	@Override
	public int hashCode() {
		return toDrop.hashCode();
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		if (tag.contains("toDrop")) {
			toDrop = ItemStack.fromTag(tag.getCompound("toDrop"));
		} else {
			toDrop = ItemStack.EMPTY;
		}
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		if (!toDrop.isEmpty()) {
			tag.put("toDrop", toDrop.toTag(new CompoundTag()));
		}
	}
}
