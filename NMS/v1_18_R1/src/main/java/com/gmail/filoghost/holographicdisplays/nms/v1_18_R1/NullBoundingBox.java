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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NullBoundingBox extends AABB {

    public NullBoundingBox() {
        super(0, 0, 0, 0, 0, 0);
    }

    @Override
    public AABB setMinX(double var0) {
        return this;
    }

    @Override
    public AABB setMinY(double var0) {
        return this;
    }

    @Override
    public AABB setMinZ(double var0) {
        return this;
    }

    @Override
    public AABB setMaxX(double var0) {
        return this;
    }

    @Override
    public AABB setMaxY(double var0) {
        return this;
    }

    @Override
    public AABB setMaxZ(double var0) {
        return this;
    }

    @Override
    public double min(Direction.Axis var0) {
        return 0.0D;
    }

    @Override
    public double max(Direction.Axis var0) {
        return 0.0D;
    }

    @Override
    public AABB contract(double var0, double var2, double var4) {
        return this;
    }

    @Override
    public AABB expandTowards(double var0, double var2, double var4) {
        return this;
    }

    @Override
    public AABB inflate(double var0, double var2, double var4) {
        return this;
    }

    @Override
    public AABB intersect(AABB var0) {
        return this;
    }

    @Override
    public AABB minmax(AABB var0) {
        return this;
    }

    @Override
    public AABB move(BlockPos var0) {
        return this;
    }

    @Override
    public AABB move(double var0, double var2, double var4) {
        return this;
    }

    @Override
    public boolean intersects(Vec3 var0, Vec3 var1) {
        return false;
    }

    @Override
    public boolean contains(double var0, double var2, double var4) {
        return false;
    }

    @Override
    public boolean hasNaN() {
        return false;
    }

    @Override
    public Vec3 getCenter() {
        return Vec3.ZERO;
    }
}
