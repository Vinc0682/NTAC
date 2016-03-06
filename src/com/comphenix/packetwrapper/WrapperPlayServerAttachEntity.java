/**
 * This file is part of PacketWrapper.
 * Copyright (C) 2012-2015 Kristian S. Strangeland
 * Copyright (C) 2015 dmulloy2
 *
 * PacketWrapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PacketWrapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PacketWrapper.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.comphenix.packetwrapper;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class WrapperPlayServerAttachEntity extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ATTACH_ENTITY;
    
    public WrapperPlayServerAttachEntity() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerAttachEntity(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     * @return The current Entity ID
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Entity ID.
     * @param value - new value.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     * @param event - the packet event.
     * @return The spawned entity.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }
    
    /**
     * Retrieve Vehicle ID.
     * <p>
     * Notes: vechicle's Entity ID
     * @return The current Vehicle ID
     */
    public int getVehicleId() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set Vehicle ID.
     * @param value - new value.
     */
    public void setVehicleId(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve Leash.
     * @return The current Leash
     */
    public boolean getLeash() {
        return handle.getIntegers().read(0) != 0;
    }
    
    /**
     * Set Leash.
     * @param value - new value.
     */
    public void setLeash(boolean value) {
        handle.getIntegers().write(0, value ? 1 : 0);
    }
}
