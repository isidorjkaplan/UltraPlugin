package co.amscraft.pvpmanager.shapes;


public class Cube extends Shape {
    public Location corner1;
    public Location corner2;

    public Location getMaxCorner() {
        return new Location(corner1.getWorld(), corner1.x > corner2.x ? corner1.x : corner2.x, corner1.y > corner2.y ? corner1.y : corner2.y, corner1.z > corner2.z ? corner1.z : corner2.z);
    }

    public Location getMinCorner() {
        return new Location(corner1.getWorld(), corner1.x < corner2.x ? corner1.x : corner2.x, corner1.y < corner2.y ? corner1.y : corner2.y, corner1.z < corner2.z ? corner1.z : corner2.z);
    }

    @Override
    public boolean isInside(org.bukkit.Location location) {
        return location.getX() <= getMaxCorner().x && location.getX() >= getMinCorner().x && location.getY() <= getMaxCorner().y && location.getY() >= getMinCorner().y && location.getZ() <= getMaxCorner().z && location.getZ() >= getMinCorner().z;
    }

    @Override
    public Cube toCube() {
        return this;
    }
}
