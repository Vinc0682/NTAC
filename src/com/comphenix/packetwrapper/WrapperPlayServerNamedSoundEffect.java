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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerNamedSoundEffect extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.NAMED_SOUND_EFFECT;
    
    public WrapperPlayServerNamedSoundEffect() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerNamedSoundEffect(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Sound name.
     * @return The current Sound name
     */
    public String getSoundName() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set Sound name.
     * @param value - new value.
     */
    public void setSoundName(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve Effect position X.
     * <p>
     * Notes: effect X multiplied by 8
     * @return The current Effect position X
     */
    public int getEffectPositionX() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Effect position X.
     * @param value - new value.
     */
    public void setEffectPositionX(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Effect position Y.
     * <p>
     * Notes: effect Y multiplied by 8
     * @return The current Effect position Y
     */
    public int getEffectPositionY() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Effect position Y.
     * @param value - new value.
     */
    public void setEffectPositionY(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve Effect position Z.
     * <p>
     * Notes: effect Z multiplied by 8
     * @return The current Effect position Z
     */
    public int getEffectPositionZ() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set Effect position Z.
     * @param value - new value.
     */
    public void setEffectPositionZ(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve Volume.
     * <p>
     * Notes: 1 is 100%, can be more
     * @return The current Volume
     */
    public float getVolume() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set Volume.
     * @param value - new value.
     */
    public void setVolume(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve Pitch.
     * <p>
     * Notes: 63 is 100%, can be more
     * @return The current Pitch
     */
    public int getPitch() {
        return handle.getIntegers().read(3);
    }
    
    /**
     * Set Pitch.
     * @param value - new value.
     */
    public void setPitch(byte value) {
        handle.getIntegers().write(3, (int) value);
    }
    
}

