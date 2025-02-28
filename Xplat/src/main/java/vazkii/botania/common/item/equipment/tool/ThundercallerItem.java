/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.tool;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.item.equipment.tool.manasteel.ManasteelSwordItem;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;
import java.util.function.Predicate;

public class ThundercallerItem extends ManasteelSwordItem {
	public ThundercallerItem(Properties props) {
		super(BotaniaAPI.instance().getTerrasteelItemTier(), 3, -1.5F, props);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity entity, @NotNull LivingEntity attacker) {
		double range = 8;
		IntList alreadyTargetedEntities = new IntArrayList();

		Predicate<Entity> selector = e -> e instanceof LivingEntity && e instanceof Enemy && !(e instanceof Player) && !alreadyTargetedEntities.contains(e.getId());

		LivingEntity prevTarget = entity;
		int hops = entity.getLevel().isThundering() ? 10 : 4;
		int dmg = hops + 1;
		for (int i = 0; i < hops; i++) {
			List<Entity> entities = entity.getLevel().getEntities(prevTarget, new AABB(prevTarget.getX() - range, prevTarget.getY() - range, prevTarget.getZ() - range, prevTarget.getX() + range, prevTarget.getY() + range, prevTarget.getZ() + range), selector);
			if (entities.isEmpty()) {
				break;
			}

			LivingEntity target = (LivingEntity) entities.get(entity.getLevel().getRandom().nextInt(entities.size()));
			if (attacker instanceof Player player) {
				target.hurt(player.damageSources().playerAttack(player), dmg);
			} else {
				target.hurt(attacker.damageSources().mobAttack(attacker), dmg);
			}

			alreadyTargetedEntities.add(target.getId());
			prevTarget = target;
			dmg--;
		}

		if (!alreadyTargetedEntities.isEmpty()) {
			XplatAbstractions.INSTANCE.sendToTracking(attacker,
					new BotaniaEffectPacket(EffectType.THUNDERCALLER_EFFECT,
							attacker.getX(), attacker.getY() + attacker.getBbHeight() / 2.0, attacker.getZ(),
							alreadyTargetedEntities.toArray(new int[0])));
		}

		return super.hurtEnemy(stack, entity, attacker);
	}

}
