/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.impl.corporea;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import vazkii.botania.api.corporea.CorporeaRequest;
import vazkii.botania.api.corporea.CorporeaSpark;

import java.util.List;

public class SidedVanillaCorporeaNode extends AbstractCorporeaNode {
	private final WorldlyContainer inv;
	private final Direction side;

	public SidedVanillaCorporeaNode(Level world, BlockPos pos, CorporeaSpark spark, WorldlyContainer inv, Direction side) {
		super(world, pos, spark);
		this.inv = inv;
		this.side = side;
	}

	@Override
	public List<ItemStack> countItems(CorporeaRequest request) {
		return examineInventory(request, false);
	}

	@Override
	public List<ItemStack> extractItems(CorporeaRequest request) {
		return examineInventory(request, true);
	}

	protected List<ItemStack> examineInventory(CorporeaRequest request, boolean doit) {
		ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();

		int[] slots = inv.getSlotsForFace(side);
		for (int i = slots.length - 1; i >= 0; i--) {
			int slot = slots[i];
			ItemStack stack = inv.getItem(slot);
			boolean canTake = inv.canTakeItemThroughFace(slot, stack, side);

			if (canTake && request.getMatcher().test(stack)) {
				request.trackFound(stack.getCount());

				int rem = Math.min(stack.getCount(), request.getStillNeeded() == -1 ? stack.getCount() : request.getStillNeeded());
				if (rem > 0) {
					request.trackSatisfied(rem);

					ItemStack copy = stack.copy();
					copy.setCount(rem);
					if (doit) {
						if (getSpark().isCreative()) {
							builder.add(copy);
						} else {
							builder.addAll(breakDownBigStack(inv.removeItem(i, rem)));
							inv.setChanged();
						}
						getSpark().onItemExtracted(copy);
						request.trackExtracted(rem);
					} else {
						copy.setCount(rem);
						builder.add(copy);
					}
				}
			}
		}

		return builder.build();
	}
}
