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

import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.ItemPickupManager;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSEntityBase;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSItem;
import com.gmail.filoghost.holographicdisplays.util.ConsoleLogger;
import com.gmail.filoghost.holographicdisplays.util.ItemUtils;
import com.gmail.filoghost.holographicdisplays.util.reflection.ReflectField;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class EntityNMSItem extends ItemEntity implements NMSItem {

    private static final ReflectField<Entity> VEHICLE_FIELD = new ReflectField<>(Entity.class, "av");

    private ItemLine parentPiece;
    private ItemPickupManager itemPickupManager;
    private CraftEntity customBukkitEntity;

    public EntityNMSItem(net.minecraft.world.level.Level world, ItemLine piece, ItemPickupManager itemPickupManager) {
        super(EntityType.ITEM, world);
        super.pickupDelay = 32767; // Lock the item pickup delay, also prevents entities from picking up the item
        this.parentPiece = piece;
        this.itemPickupManager = itemPickupManager;
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

    // Method called when a player is near.
    @Override
    public void playerTouch(net.minecraft.world.entity.player.Player entityhuman) {
        if (entityhuman.getY() < super.getY() - 1.5 || entityhuman.getY() > super.getY() + 1.0) {
            // Too low or too high, it's a bit weird./
            return;
        }

        if (parentPiece.getPickupHandler() != null && entityhuman instanceof ServerPlayer) {
            itemPickupManager.handleItemLinePickup((Player) entityhuman.getBukkitEntity(), parentPiece.getPickupHandler(), parentPiece.getParent());
            // It is never added to the inventory.
        }
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
    public void kill() {
        // Prevent being killed.
    }

    @Override
    public boolean isAlive() {
        // This override prevents items from being picked up by hoppers.
        // Should have no side effects.
        return false;
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (customBukkitEntity == null) {
            customBukkitEntity = new CraftNMSItem(super.getLevel().getCraftServer(), this);
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
    public void setItemStackNMS(org.bukkit.inventory.ItemStack stack) {
        ItemStack newItem = CraftItemStack.asNMSCopy(stack); // ItemStack.a is returned if the stack is null, invalid or the material is not an Item

        if (newItem == null || newItem == ItemStack.EMPTY) {
            newItem = new ItemStack(Blocks.BEDROCK);
        }

        if (newItem.getTag() == null) {
            newItem.setTag(new CompoundTag());
        }
        CompoundTag display = newItem.getTag().getCompound("display"); // Returns a new NBTTagCompound if not existing
        if (!newItem.getTag().contains("display")) {
            newItem.getTag().put("display", display);
        }

        ListTag tagList = new ListTag();
        tagList.add(StringTag.valueOf(ItemUtils.ANTISTACK_LORE)); // Antistack lore
        display.put("Lore", tagList);

        setItem(newItem);
    }

    @Override
    public int getIdNMS() {
        return super.getId();
    }

    @Override
    public ItemLine getHologramLine() {
        return parentPiece;
    }

    @Override
    public void allowPickup(boolean pickup) {
        if (pickup) {
            super.pickupDelay = 0;
        } else {
            super.pickupDelay = 32767;
        }
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

    @Override
    public Object getRawItemStack() {
        return super.getItem();
    }
}
