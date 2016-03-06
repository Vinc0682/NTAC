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

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

public class WrapperPlayClientBlockPlace extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.BLOCK_PLACE;
    
    public WrapperPlayClientBlockPlace() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientBlockPlace(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Location.
     * <p>
     * Notes: block position
     * @return The current Location
     */
    public BlockPosition getLocation() {
        return handle.getBlockPositionModifier().read(0);
    }
    
    /**
     * Set Location.
     * @param value - new value.
     */
    public void setLocation(BlockPosition value) {
        handle.getBlockPositionModifier().write(0, value);
    }
    
    /**
     * Retrieve Direction.
     * <p>
     * Notes: the offset to use for block/item placement (see below)
     * @return The current Direction
     */
    public int getFace() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Direction.
     * @param value - new value.
     */
    public void setFace(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Held item.
     * @return The current Held item
     */
    public ItemStack getHeldItem() {
        return handle.getItemModifier().read(0);
    }
    
    /**
     * Set Held item.
     * @param value - new value.
     */
    public void setHeldItem(ItemStack value) {
        handle.getItemModifier().write(0, value);
    }
    
    /**
     * Retrieve Cursor position X.
     * <p>
     * Notes: the position of the crosshair on the block
     * @return The current Cursor position X
     */
    public float getCursorPositionX() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set Cursor position X.
     * @param value - new value.
     */
    public void setCursorPositionX(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve Cursor position Y.
     * @return The current Cursor position Y
     */
    public float getCursorPositionY() {
        return handle.getFloat().read(1);
    }
    
    /**
     * Set Cursor position Y.
     * @param value - new value.
     */
    public void setCursorPositionY(float value) {
        handle.getFloat().write(1, value);
    }
    
    /**
     * Retrieve Cursor position Z.
     * @return The current Cursor position Z
     */
    public float getCursorPositionZ() {
        return handle.getFloat().read(2);
    }
    
    /**
     * Set Cursor position Z.
     * @param value - new value.
     */
    public void setCursorPositionZ(float value) {
        handle.getFloat().write(2, value);
    }
    
}

