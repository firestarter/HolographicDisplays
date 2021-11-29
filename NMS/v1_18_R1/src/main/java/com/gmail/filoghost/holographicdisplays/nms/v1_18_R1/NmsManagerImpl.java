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
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.ChatComponentAdapter;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.CustomNameHelper;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.ItemPickupManager;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.NMSManager;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSArmorStand;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSEntityBase;
import com.gmail.filoghost.holographicdisplays.nms.interfaces.entity.NMSItem;
import com.gmail.filoghost.holographicdisplays.util.ConsoleLogger;
import com.gmail.filoghost.holographicdisplays.util.Validator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NmsManagerImpl implements NMSManager {
	
	@Override
	public void setup() {}
	
	@Override
	public NMSItem spawnNMSItem(org.bukkit.World bukkitWorld, double x, double y, double z, ItemLine parentPiece, ItemStack stack, ItemPickupManager itemPickupManager) {
		ServerLevel nmsWorld = ((CraftWorld) bukkitWorld).getHandle();
		com.gmail.filoghost.holographicdisplays.nms.v1_18_R1.EntityNMSItem customItem = new EntityNMSItem(nmsWorld, parentPiece, itemPickupManager);
		customItem.setLocationNMS(x, y, z);
		customItem.setItemStackNMS(stack);
		if (!addEntityToWorld(nmsWorld, customItem)) {
			ConsoleLogger.handleSpawnFail(parentPiece);
		}
		return customItem;
	}
	
	@Override
	public com.gmail.filoghost.holographicdisplays.nms.v1_18_R1.EntityNMSSlime spawnNMSSlime(org.bukkit.World bukkitWorld, double x, double y, double z, HologramLine parentPiece) {
		ServerLevel nmsWorld = ((CraftWorld) bukkitWorld).getHandle();
		com.gmail.filoghost.holographicdisplays.nms.v1_18_R1.EntityNMSSlime touchSlime = new EntityNMSSlime(nmsWorld, parentPiece);
		touchSlime.setLocationNMS(x, y, z);
		if (!addEntityToWorld(nmsWorld, touchSlime)) {
			ConsoleLogger.handleSpawnFail(parentPiece);
		}
		return touchSlime;
	}
	
	@Override
	public NMSArmorStand spawnNMSArmorStand(org.bukkit.World world, double x, double y, double z, HologramLine parentPiece, boolean broadcastLocationPacket) {
		ServerLevel nmsWorld = ((CraftWorld) world).getHandle();
		com.gmail.filoghost.holographicdisplays.nms.v1_18_R1.EntityNMSArmorStand invisibleArmorStand = new EntityNMSArmorStand(nmsWorld, parentPiece);
		invisibleArmorStand.setLocationNMS(x, y, z, broadcastLocationPacket);
		if (!addEntityToWorld(nmsWorld, invisibleArmorStand)) {
			ConsoleLogger.handleSpawnFail(parentPiece);
		}
		return invisibleArmorStand;
	}
	
	private boolean addEntityToWorld(ServerLevel nmsWorld, Entity nmsEntity) {
		Validator.isTrue(Bukkit.isPrimaryThread(), "Async entity add");
		
        final int chunkX = Mth.floor(nmsEntity.getX() / 16.0);
        final int chunkZ = Mth.floor(nmsEntity.getZ() / 16.0);
        
        if (!nmsWorld.hasChunk(chunkX, chunkZ)) {
        	// This should never happen
			nmsEntity.getBukkitEntity().remove();
            nmsEntity.setRemoved(RemovalReason.DISCARDED);
            return false;
        }
        
        try {
        	nmsWorld.entityManager.addNewEntity(nmsEntity);
        	return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
	
	@Override
	public boolean isNMSEntityBase(org.bukkit.entity.Entity bukkitEntity) {
		return ((CraftEntity) bukkitEntity).getHandle() instanceof NMSEntityBase;
	}

	@Override
	public NMSEntityBase getNMSEntityBase(org.bukkit.entity.Entity bukkitEntity) {
		Entity nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
		
		if (nmsEntity instanceof NMSEntityBase) {
			return ((NMSEntityBase) nmsEntity);
		} else {
			return null;
		}
	}
	
	@Override
	public NMSEntityBase getNMSEntityBaseFromID(org.bukkit.World bukkitWorld, int entityID) {
		ServerLevel nmsWorld = ((CraftWorld) bukkitWorld).getHandle();
		Entity nmsEntity = nmsWorld.getEntity(entityID);
		
		if (nmsEntity instanceof NMSEntityBase) {
			return ((NMSEntityBase) nmsEntity);
		} else {
			return null;
		}
	}
	
	@Override
	public Object replaceCustomNameText(Object customNameObject, String target, String replacement) {
		return CustomNameHelper.replaceCustomNameChatComponent(NMSChatComponentAdapter.INSTANCE, customNameObject, target, replacement);
	}
	
	private static enum NMSChatComponentAdapter implements ChatComponentAdapter<Component> {

		INSTANCE {
			
			public TextComponent cast(Object chatComponentObject) {
				return (TextComponent) chatComponentObject;
			}
			
			@Override
			public String getText(Component chatComponent) {
				return chatComponent.getString();
			}
	
			@Override
			public List<Component> getSiblings(Component chatComponent) {
				return chatComponent.getSiblings();
			}
	
			@Override
			public void addSibling(Component chatComponent, Component newSibling) {
				// newSibling.getChatModifier().setChatModifier(chatComponent.getChatModifier()); // TODO
				chatComponent.getSiblings().add(newSibling);
			}
	
			@Override
			public TextComponent cloneComponent(Component chatComponent, String newText) {
				TextComponent clonedChatComponent = new TextComponent(newText);
				// clonedChatComponent.setChatModifier(chatComponent.getChatModifier().a()); // TODO
				return clonedChatComponent;
			}
			
		}
		
	}
	
}
