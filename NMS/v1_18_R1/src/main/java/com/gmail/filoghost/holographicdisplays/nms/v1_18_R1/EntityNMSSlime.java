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

import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSEntityBase;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSSlime;
import com.gmail.filoghost.holographicdisplays.util.ConsoleLogger;
import com.gmail.filoghost.holographicdisplays.util.reflection.ReflectField;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.phys.AABB;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import javax.annotation.Nullable;
import java.util.logging.Level;

public class EntityNMSSlime extends Slime implements NMSSlime {
	
	private static final ReflectField<Entity> VEHICLE_FIELD = new ReflectField<>(Entity.class, "au");

	private HologramLine parentPiece;
	private CraftEntity customBukkitEntity;
	
	public EntityNMSSlime(net.minecraft.world.level.Level world, HologramLine parentPiece) {
		super(EntityType.SLIME, world);
		super.setPersistenceRequired();
		super.collides = false;
		// a(0.0F, 0.0F); // TODO ?
		setSize(1, false);
		setInvisible(true);
		this.parentPiece = parentPiece;
		forceSetBoundingBox(new NullBoundingBox());
	}
	
	@Override
	public void tick() {
		// Disable normal ticking for this entity.
		
		// So it won't get removed.
		super.tickCount = 0;
	}
	
	@Override
	public void inactiveTick() {
		// Disable normal ticking for this entity.
		
		// So it won't get removed.
		super.tickCount = 0;
	}

	@Override
	public void setPos(double d0, double d1, double d2) {
		// Do not change it!
	}
	
	public void forceSetBoundingBox(AABB boundingBox) {
		super.setBoundingBox(boundingBox);
	}

	@Override
	public boolean save(CompoundTag nbttagcompound) {
		// Do not save NBT.
		return false;
	}

	@Override
	public boolean saveAsPassenger(CompoundTag nbttagcompound) {
		// Do not save NBT.
		return false;
	}

	@Override
	public CompoundTag saveWithoutId(CompoundTag nbttagcompound) {
		// Do not save NBT.
		return nbttagcompound;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbttagcompound) {
		// Do not load NBT.
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbttagcompound) {
		// Do not load NBT.
	}

	@Override
	protected boolean damageEntity0(DamageSource damagesource, float f) {
		if (damagesource instanceof EntityDamageSource) {
			EntityDamageSource entityDamageSource = (EntityDamageSource) damagesource;
			if (entityDamageSource.getEntity() instanceof ServerPlayer) {
				Bukkit.getPluginManager().callEvent(new PlayerInteractEntityEvent(((ServerPlayer) entityDamageSource.getEntity()).getBukkitEntity(), getBukkitEntity())); // Bukkit takes care of the exceptions
			}
		}
		return false;
	}

	@Override
	public boolean isInvulnerable() {
		/*
		 * The field Entity.invulnerable is private.
		 * It's only used while saving NBTTags, but since the entity would be killed
		 * on chunk unload, we prefer to override isInvulnerable().
		 */
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public void setCustomName(@Nullable Component ichatbasecomponent) {
		// Locks the custom name.
	}

	@Override
	public void setCustomNameVisible(boolean visible) {
		// Locks the custom name.
	}

	@Override
	public void playSound(SoundEvent soundeffect, float f, float f1) {
		// Remove sounds.
	}

	@Override
	public void kill() {
		// Prevent being killed.
	}
	
	@Override
	public CraftEntity getBukkitEntity() {
		if (customBukkitEntity == null) {
			customBukkitEntity = new CraftNMSSlime(super.getLevel().getCraftServer(), this);
	    }
		return customBukkitEntity;
	}

	@Override
	public boolean isDeadNMS() {
		return super.isRemoved();
	}
	
	@Override
	public void killEntityNMS() {
		super.setRemoved(RemovalReason.DISCARDED);
	}
	
	@Override
	public void setLocationNMS(double x, double y, double z) {
		super.setPos(x, y, z);
	}
	
	@Override
	public int getIdNMS() {
		return super.getId();
	}
	
	@Override
	public HologramLine getHologramLine() {
		return parentPiece;
	}

	@Override
	public org.bukkit.entity.Entity getBukkitEntityNMS() {
		return getBukkitEntity();
	}
	
	@Override
	public void setPassengerOfNMS(NMSEntityBase vehicleBase) {
		if (vehicleBase == null || !(vehicleBase instanceof Entity)) {
			// It should never dismount
			return;
		}
		
		Entity entity = (Entity) vehicleBase;
		
		try {
			if (super.getVehicle() != null) {
				Entity oldVehicle = super.getVehicle();
				VEHICLE_FIELD.set(this, null);
				oldVehicle.passengers = ImmutableList.of();
			}

			VEHICLE_FIELD.set(this, entity);
			entity.passengers = ImmutableList.of(this);

		} catch (Throwable t) {
			ConsoleLogger.logDebug(Level.SEVERE, "Couldn't set passenger", t);
		}
	}
}
