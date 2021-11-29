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
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSArmorStand;
import com.gmail.filoghost.holographicdisplays.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftChatMessage;

import javax.annotation.Nullable;

public class EntityNMSArmorStand extends ArmorStand implements NMSArmorStand {

    private HologramLine parentPiece;
    private CraftEntity customBukkitEntity;
    private String customName;

    public EntityNMSArmorStand(Level world, HologramLine parentPiece) {
        super(EntityType.ARMOR_STAND, world);
        super.setInvisible(true);
        super.setSmall(true);
        super.setShowArms(false);
        super.setNoGravity(true);
        super.setNoBasePlate(true);
        super.setMarker(true);
        super.collides = false;
        this.parentPiece = parentPiece;
        forceSetBoundingBox(new NullBoundingBox());
        
        this.onGround /* onGround */ = true; // Workaround to force EntityTrackerEntry to send a teleport packet.
    }
    
	@Override
	public void tick() {
		// Disable normal ticking for this entity.
		
		// Workaround to force EntityTrackerEntry to send a teleport packet immediately after spawning this entity.
		if (this.onGround /* onGround */) {
			this.onGround = false;
		}
	}
	
	@Override
	public void inactiveTick() {
		// Disable normal ticking for this entity.
		
		// Workaround to force EntityTrackerEntry to send a teleport packet immediately after spawning this entity.
		if (this.onGround /* onGround */) {
			this.onGround = false;
		}
	}

    @Override
    public void setPos(double d0, double d1, double d2) {
        // Do not change it!
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
    public void setCustomName(@Nullable Component ichatbasecomponent) {
        // Locks the custom name.
    }

    @Override
    public void setCustomNameVisible(boolean visible) {
        // Locks the custom name.
    }

    @Override
    public InteractionResult interactAt(Player entityhuman, Vec3 vec3d, InteractionHand enumhand) {
        return InteractionResult.PASS;
    }

    @Override
    public void setItemSlot(EquipmentSlot enumitemslot, ItemStack itemstack, boolean silent) {
        // Prevent stand being equipped
    }

    public void forceSetBoundingBox(AABB boundingBox) {
        super.setBoundingBox(boundingBox);
    }

    @Override
    public void playSound(SoundEvent soundeffect, float f, float f1) {
        // Remove sounds.
    }

    @Override
    public void setCustomNameNMS(String name) {
        this.customName = Utils.limitLength(name, 300);
        super.setCustomName(CraftChatMessage.fromStringOrNull(customName));
        super.setCustomNameVisible(customName != null && !customName.isEmpty());
    }

    @Override
    public String getCustomNameStringNMS() {
        return this.customName;
    }
    
	@Override
	public Object getCustomNameObjectNMS() {
		return super.getCustomName();
	}

    @Override
    public void kill() {
        // Prevent being killed.
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (customBukkitEntity == null) {
            customBukkitEntity = new CraftNMSArmorStand(super.getLevel().getCraftServer(), this);
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
    public void setLocationNMS(double x, double y, double z, boolean broadcastLocationPacket) {
        super.setPos(x, y, z);
        if (broadcastLocationPacket) {
        	broadcastLocationPacketNMS();
        }
    }
    
    private void broadcastLocationPacketNMS() {
        ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(this);

        for (Object obj : super.getLevel().players()) {
            if (obj instanceof ServerPlayer) {
                ServerPlayer nmsPlayer = (ServerPlayer) obj;

                double distanceSquared = Utils.square(nmsPlayer.getX() - super.getX()) + Utils.square(nmsPlayer.getX() - super.getX());
                if (distanceSquared < 8192 && nmsPlayer.connection != null) {
                    nmsPlayer.connection.send(teleportPacket);
                }
            }
        }
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
}
