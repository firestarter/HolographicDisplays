/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.gmail.filoghost.holographicdisplays.nms.v1_18_R1;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftSlime;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Collection;

public class CraftNMSSlime extends CraftSlime {

	public CraftNMSSlime(CraftServer server, EntityNMSSlime entity) {
		super(server, entity);
	}
	
	// Disallow all the bukkit methods.
	
	@Override
	public void remove() {
		// Cannot be removed, this is the most important to override.
	}
	
	// Methods from Slime class
	@Override public void setSize(int size) { }
	@Override public void setTarget(LivingEntity target) { }
	
	// Methods from Mob class
	@Override public void setLootTable(LootTable table) { }
	@Override public void setSeed(long seed) { }
	
	// Methods from LivingEntity class
	@Override public boolean addPotionEffect(PotionEffect effect) { return false; }
	@Override public boolean addPotionEffect(PotionEffect effect, boolean param) { return false; }
	@Override public boolean addPotionEffects(Collection<PotionEffect> effects) { return false; }
	@Override public void setRemoveWhenFarAway(boolean remove) { }
	@Override public void setAI(boolean ai) { }
	@Override public void setCanPickupItems(boolean pickup) { }
	@Override public void setCollidable(boolean collidable) { }
	@Override public void setGliding(boolean gliding) {	}
	@Override public boolean setLeashHolder(Entity holder) { return false; }
	@Override public void setSwimming(boolean swimming) { }
	@Override public void setInvisible(boolean invisible) { }

	// Methods from Entity class
	@Override public void setVelocity(Vector vel) { }
	@Override public boolean teleport(Location loc) { return false; }
	@Override public boolean teleport(Entity entity) { return false; }
	@Override public boolean teleport(Location loc, TeleportCause cause) { return false; }
	@Override public boolean teleport(Entity entity, TeleportCause cause) { return false; }
	@Override public void setFireTicks(int ticks) { }
	@Override public boolean setPassenger(Entity entity) { return false; }
	@Override public boolean eject() { return false; }
	@Override public boolean leaveVehicle() { return false; }
	@Override public void playEffect(EntityEffect effect) { }
	@Override public void setCustomName(String name) { }
	@Override public void setCustomNameVisible(boolean flag) { }
	@Override public void setGlowing(boolean flag) { }
	@Override public void setGravity(boolean gravity) { }
	@Override public void setInvulnerable(boolean flag) { }
	@Override public void setMomentum(Vector value) { }
	@Override public void setSilent(boolean flag) { }
	@Override public void setTicksLived(int value) { }
	@Override public void setPersistent(boolean flag) { }
	@Override public void setRotation(float yaw, float pitch) { }
	@Override public boolean addPassenger(Entity passenger) { return false; }
	@Override public boolean removePassenger(Entity passenger) { return false; }

}
