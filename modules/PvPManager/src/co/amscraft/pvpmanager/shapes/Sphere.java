package co.amscraft.pvpmanager.shapes;


public class Sphere extends Shape {
    public Location center = new Location();
    public double radius = 10;

    @Override
    public boolean isInside(org.bukkit.Location location) {
        return center.getLocation().getWorld() == location.getWorld() && center.distance(location) < radius;
    }

    @Override
    public Cube toCube() {
        Cube cube = new Cube();
        cube.corner1 = new Location(this.center.getLocation().add(radius, radius, radius));
        cube.corner1 = new Location(this.center.getLocation().subtract(radius, radius, radius));
        return cube;
    }
}
