/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.v1_13_R2;

import me.filoghost.holographicdisplays.core.hologram.StandardHologramLine;
import me.filoghost.holographicdisplays.core.hologram.StandardItemLine;
import me.filoghost.holographicdisplays.core.nms.NMSCommons;
import me.filoghost.holographicdisplays.core.nms.entity.NMSItem;
import net.minecraft.server.v1_13_R2.Blocks;
import net.minecraft.server.v1_13_R2.DamageSource;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.EntityItem;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.ItemStack;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.NBTTagList;
import net.minecraft.server.v1_13_R2.NBTTagString;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;

public class EntityNMSItem extends EntityItem implements NMSItem {
    
    private final StandardItemLine parentHologramLine;
    
    public EntityNMSItem(World world, StandardItemLine parentHologramLine) {
        super(world);
        this.parentHologramLine = parentHologramLine;
        
        super.pickupDelay = 32767; // Lock the item pickup delay, also prevents entities from picking up the item
    }
    
    @Override
    public void tick() {
        // Disable normal ticking for this entity.
        
        // So it won't get removed.
        ticksLived = 0;
    }
    
    @Override
    public void inactiveTick() {
        // Disable normal ticking for this entity.
        
        // So it won't get removed.
        ticksLived = 0;
    }
    
    // Method called when a player is near.
    @Override
    public void d(EntityHuman human) {
        if (human.locY < super.locY - 1.5 || human.locY > super.locY + 1.0) {
            // Too low or too high, it's a bit weird.
            return;
        }
        
        if (human instanceof EntityPlayer) {
            parentHologramLine.onPickup(((EntityPlayer) human).getBukkitEntity());
            // It is never added to the inventory.
        }
    }
    
    @Override
    public void b(NBTTagCompound nbttagcompound) {
        // Do not save NBT.
    }
    
    @Override
    public boolean c(NBTTagCompound nbttagcompound) {
        // Do not save NBT.
        return false;
    }

    @Override
    public boolean d(NBTTagCompound nbttagcompound) {
        // Do not save NBT.
        return false;
    }
    
    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        // Do not save NBT.
        return nbttagcompound;
    }
    
    @Override
    public void f(NBTTagCompound nbttagcompound) {
        // Do not load NBT.
    }
    
    @Override
    public void a(NBTTagCompound nbttagcompound) {
        // Do not load NBT.
    }
    
    @Override
    public boolean isInvulnerable(DamageSource source) {
        /*
         * The field Entity.invulnerable is private.
         * It's only used while saving NBTTags, but since the entity would be killed
         * on chunk unload, we prefer to override isInvulnerable().
         */
        return true;
    }
    
    @Override
    public boolean isCollidable() {
        return false;
    }
    
    @Override
    public void die() {
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
        if (super.bukkitEntity == null) {
            super.bukkitEntity = new CraftNMSItem(super.world.getServer(), this);
        }
        return super.bukkitEntity;
    }

    @Override
    public boolean isDeadNMS() {
        return super.dead;
    }
    
    @Override
    public void killEntityNMS() {
        super.dead = true;
    }
    
    @Override
    public void setLocationNMS(double x, double y, double z) {
        super.setPosition(x, y, z);
    }

    @Override
    public void setItemStackNMS(org.bukkit.inventory.ItemStack stack) {
        ItemStack newItem = CraftItemStack.asNMSCopy(stack); // ItemStack.a is returned if the stack is not valid
        
        if (newItem == null || newItem == ItemStack.a) {
            newItem = new ItemStack(Blocks.BEDROCK);
        }

        if (newItem.getTag() == null) {
            newItem.setTag(new NBTTagCompound());
        }
        NBTTagCompound display = newItem.getTag().getCompound("display"); // Returns a new NBTTagCompound if not existing
        if (!newItem.getTag().hasKey("display")) {
            newItem.getTag().set("display", display);
        }

        NBTTagList tagList = new NBTTagList();
        tagList.add(new NBTTagString(NMSCommons.ANTI_STACK_LORE));
        display.set("Lore", tagList);
        
        super.setItemStack(newItem);
    }
    
    @Override
    public int getIdNMS() {
        return super.getId();
    }
    
    @Override
    public StandardHologramLine getHologramLine() {
        return parentHologramLine;
    }

    @Override
    public org.bukkit.entity.Entity getBukkitEntityNMS() {
        return getBukkitEntity();
    }

    @Override
    public Object getRawItemStack() {
        return super.getItemStack();
    }
    
}