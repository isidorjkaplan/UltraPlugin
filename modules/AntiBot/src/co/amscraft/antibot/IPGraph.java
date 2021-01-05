package co.amscraft.antibot;

import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultralib.utils.ObjectUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

/**
 * A class that represents the connections that two players have
 */
public class IPGraph {
    /**
     * The static IPGraph for the server
     */
    private static IPGraph ipgraph;
    /**
     * The map that stores the vertecies of the graph
     */
    private HashMap<Vertex, Set<Vertex>> graph;
    //private HashMap<Integer, Set<Path>> allPaths = new HashMap<>();
    /**
     * Weather or not the graph is currently preforming a search
     */
    private boolean searchingAllPaths = false;

    /**
     * The constructor to read the IPGraph from a file
     * @param file The file to read from
     * @throws IOException
     */
    public IPGraph(File file) throws IOException {
        this(Files.readAllLines(file.toPath()));
    }

    /**
     * The no args constructor to create a new IPGraph
     */
    public IPGraph() {
        this(new ArrayList<>());
    }

    /**
     * The constructor that creates
     * @param serialized The list of all the vertecies seralized
     */
    public IPGraph(List<String> serialized) {
        graph = new HashMap<>();
        for (String row : serialized) {
            String[] strings = row.split(" ");
            String ip = strings[0];
            for (int i = 1; i < strings.length; i++) {
                if (strings[i] != null) {
                    UUID uuid = UUID.fromString(strings[i]);
                    if (uuid != null) {
                        this.addConnection(uuid, ip);
                    } else {
                        ObjectUtils.debug(Level.WARNING, "Error reading UUID " + strings[i]);
                    }
                } else {
                    ObjectUtils.debug(Level.WARNING, "Null UUID ");
                }
            }
        }
    }

    /**
     * The command to get the server-specific IPGraph
     * @return The server IPGraph
     */
    public static IPGraph getGraph() {
        if (ipgraph == null) {
            File file = new File(Module.getModule(AntiBot.class).getDataFolder() + "/graph.txt");
            if (file.exists()) {
                try {
                    ipgraph = new IPGraph(Files.readAllLines(file.toPath()));
                } catch (Exception e) {
                    ipgraph = null;
                }
            } else {
                ipgraph = new IPGraph();
            }
        }
        return ipgraph;
    }

    /**
     * A function to merge the data from two IPGrapph's
     * @param graph The graph to absorb
     */
    public void merge(IPGraph graph) {
        for (Vertex ip : graph.getAllVertecies(IPVertex.class)) {
            for (Vertex p : graph.getAdjasantVertecies(ip)) {
                this.addConnection(((PlayerVertex) p).getPlayer(), ((IPVertex) ip).getIp());
            }
        }
    }

    /**
     * A method to save the IPGraph
     * @throws IOException
     */
    public void save() throws IOException {
        this.save(new File(Module.getModule(AntiBot.class).getDataFolder() + "/graph.txt"));
    }


    /*
     * A set of functions to test the limits of the graph, commented out beacuse they are no longer in use
     */
   /*public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        double dt = 0;
        int N = 15000;
        int M = N;
        int K = 10;
        DecimalFormat time = new DecimalFormat("#.### seconds");
        String[] ips = new String[M];
        Random random = new Random();
        for (int m = 0; m < M; m++) {
            ips[m] = random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
        }
        UUID[] players = new UUID[N];
        IPGraph graph = new IPGraph();
        for (int n = 0; n < N; n++) {
            UUID uuid = UUID.randomUUID();
            players[n] = uuid;
            int r = random.nextInt(K) + 1;
            for (int k = 0; k < r; k++) {
                String ip = ips[random.nextInt(M)];
                graph.addConnection(uuid, ip);
            }
        }
        dt = (System.currentTimeMillis() - start) / 1000.0;
        System.out.println("Graph has been constructed in: " + time.format(dt) + "s\n");
        System.out.println("============================================");

        int T = 5;
        double longest = 0;
        double sum = 0;
        PlayerVertex p = new PlayerVertex(players[random.nextInt(N)]);
        List<Vertex> vertices = new ArrayList<>();
        for (int t = 0; t < T; t++) {
            start = System.currentTimeMillis();
            PlayerVertex p2 = new PlayerVertex(players[random.nextInt(N)]);
            vertices.add(p2);
            Path genPath = graph.getPath(p, p2);
            PlayerVertex[] path = genPath != null?genPath.toVertexArray():null;
            dt = (System.currentTimeMillis() - start) / 1000.0;
            if (dt > longest) {
                longest = dt;
            }
            sum+=dt;
            String print = p.getPlayer().hashCode() + " ===> " + p2.getPlayer().hashCode() + ":";
            if (path != null) {
                for (PlayerVertex v : path) {
                    print += " > " + v.getPlayer().hashCode();
                }
                System.out.println(print);
            } else {
                System.out.println(print + " No path found");
            }
        }
        System.out.println("Generated all paths with longest time of: " + time.format(longest) + " for a total of " + time.format(sum));
        System.out.println("============================================");

        start = System.currentTimeMillis();
        Map<Vertex, Path> paths = graph.getPaths(p, vertices);
        dt = (System.currentTimeMillis() - start) / 1000.0;
        for (Vertex v: paths.keySet()) {
            String print = p.getPlayer().hashCode() + " ===> " + ((PlayerVertex)v).getPlayer().hashCode() + ":";
            Path path = paths.get(v);
            if (path != null) {
                for (PlayerVertex pv : path.toVertexArray()) {
                    print += " > " + pv.getPlayer().hashCode();
                }
                System.out.println(print);
            } else {
                System.out.println(print + " No path found");
            }
        }
        System.out.println("Found all paths in " + time.format(dt));

        System.out.println("============================================");


        start = System.currentTimeMillis();
        List<String> serialized = null; graph.serialize();
        dt = (System.currentTimeMillis() - start) / 1000.0;
        System.out.println("Serialized graph in: " + time.format(dt));

        start = System.currentTimeMillis();
        File file = new File("modules/AntiBot/graphTest.txt");
        Files.write(file.toPath(), graph.serialize().getBytes());
        dt = (System.currentTimeMillis() - start) / 1000.0;
        System.out.println("Wrote to file in " + time.format(dt));

        start = System.currentTimeMillis();
        serialized = Files.readAllLines(file.toPath());

        dt = (System.currentTimeMillis() - start) / 1000.0;
        System.out.println("Read from file in " + time.format(dt));


        start = System.currentTimeMillis();
        IPGraph graph2 = new IPGraph(serialized);
        dt = (System.currentTimeMillis() - start) / 1000.0;
        System.out.println("Reconstructed graph in " + time.format(dt));
        System.out.println("Equality Test: " + graph.graph.equals(graph2.graph));





        System.out.println("============================================");


        System.out.println("Stats: \n" +
                "Players: " + N + "\n" +
                "IP Addresses: " + M + "\n" +
                "IP Per Players: 1-" + K + "\n" );


    }
   /*
    public static void main(String[] args) throws IOException {
        File folder = new File("modules/AntiBot/userdata");
        File graphFile = new File("modules/AntiBot/graph.txt");
        IPGraph graph = new IPGraph(graphFile);
        for (File file: folder.listFiles()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            try {
                UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
                String ip = config.getString("ipAddress");
                if (ip != null) {
                    graph.addConnection(uuid, ip);
                    System.out.println("Indexing " + uuid);
                }
            } catch (Exception e) {
                System.out.println("Error in " + file.getName());
                e.printStackTrace();
            }
        }
        graph.save(graphFile);
    }*/
  /*public static void main(String[] args) throws IOException {
        IPGraph graph = new IPGraph(new File("modules/AntiBot/graph.txt"));
        System.out.println("Identifying all alts");
        for (Vertex v: graph.getAllVertecies(PlayerVertex.class)) {
            HashMap<Integer, List<Path>> paths = graph.getAllPaths(((PlayerVertex)v).getPlayer(), 2);
            if (!paths.isEmpty()) {
                System.out.println(((((PlayerVertex) v).getPlayer())));
                for (int i: paths.keySet()) {
                    for (Path p: paths.get(i)) {
                        System.out.println("\t" + p);
                    }
                }
            }
        }
    }*/
 /*public static void main(String[] args) throws IOException, InterruptedException {
      IPGraph graph = new IPGraph(new File("modules/AntiBot/graph.txt"));
      //Thread.sleep(20000);
      System.out.println("Resume");
      //updateAllPaths(graph);
      Set<Path> paths = graph.getAllPaths(2);
      for (Path p: paths) {
          System.out.println(p.toString());
      }

  }*/

    /**
     * A function to save this graph to a file
     * @param file The file to save to
     * @throws IOException
     */
    public void save(File file) throws IOException {
        if (!file.exists()) {
            FileConfiguration config = new YamlConfiguration();
            config.save(file);
        }
        Files.write(file.toPath(), this.serialize().getBytes());
    }

    /**
     * A function to check if a player is connected to an IP directly
     * @param player The plaayer
     * @param ip The IP
     * @return If they are coonnected
     */
    public boolean hasConnection(UUID player, String ip) {
        Vertex v = new PlayerVertex(player);
        return this.graph.containsKey(v) && this.graph.get(v).contains(new IPVertex(ip));
    }

    /**
     * A function to add an edge between two vertecies
     * @param player The player
     * @param ip The IP
     */
    public void addConnection(UUID player, String ip) {
        if (!this.hasConnection(player, ip)) {
            Vertex v = new IPVertex(ip);
            PlayerVertex p = new PlayerVertex(player);
            this.addEdge(v, p);
            this.addEdge(p, v);
        }
    }

    /**
     * A function to purge all the data associated with an IP
     * @param ip The IP to purge
     * @return If they were connected
     */
    public boolean purge(String ip) {
        IPVertex v = new IPVertex(ip);
        return purge(v);
    }

    /**
     * The function to purge all the data associated with a player
     * @param player The player to purge
     * @return If they were connected
     */
    public boolean purge(UUID player) {
        return purge(new PlayerVertex(player));
    }

    /**
     * The function to purge all the edges attached to a given vertex
     * @param v The vertex to purge
     * @return If the graph was changed
     */
    public boolean purge(Vertex v) {
        if (graph.containsKey(v)) {
            for (Vertex adj : new HashSet<>(getAdjasantVertecies(v))) {
                removeEdge(v, adj);
            }
            return true;
        }
        return false;
    }

    /**
     * A function to add an edge between two vertecies
     * @param v1 The first Vertex
     * @param v2 The second Vertex
     */
    private void addEdge(Vertex v1, Vertex v2) {
        if (!graph.containsKey(v1)) {
            graph.put(v1, new HashSet<>());
        }
        Set<Vertex> set = graph.get(v1);
        set.add(v2);
    }

    /**
     * A function to remove an edge between two vertecies
     * @param v1 The first vertex
     * @param v2 The second vertex
     */
    private void removeEdge(Vertex v1, Vertex v2) {
        if (graph.containsKey(v1) && graph.containsKey(v2)) {
            graph.get(v1).remove(v2);
            if (graph.get(v1).isEmpty()) {
                graph.remove(v1);
            }
            graph.get(v2).remove(v1);
            if (graph.get(v2).isEmpty()) {
                graph.remove(v2);
            }

        }
    }

    /**
     * A function to get all the adjasand vertecies to a given vertex
     * @param v The vertex
     * @return The adjasand vertecies
     */
    public Set<Vertex> getAdjasantVertecies(Vertex v) {
        return Collections.unmodifiableSet(graph.getOrDefault(v, new HashSet<>()));
    }

    /**
     * A function to get all the common IPs of two players
     * @param p1 Player 1
     * @param p2 Player 2
     * @return The common IPs
     */
    public String[] getCommonIPs(UUID p1, UUID p2) {
        PlayerVertex v1 = new PlayerVertex(p1);
        PlayerVertex v2 = new PlayerVertex(p2);
        if (!graph.containsKey(v1) || !graph.containsKey(v2)) {
            return null;
        }
        Set<Vertex> ip1 = graph.get(v1);
        Set<Vertex> ip2 = graph.get(v2);
        List<String> ips = new ArrayList<>();
        for (Vertex ip : ip1) {
            if (ip2.contains(ip)) {
                if (ip instanceof IPVertex) {
                    ips.add(((IPVertex) ip).getIp());
                }
            }
        }
        return ips.toArray(new String[ips.size()]);
    }

    /**
     * A function to get all the player's who have data in this graph
     * @return The player's who have data in this graph
     */
    public Set<UUID> getAllPlayers() {
        Set<UUID> uuids = new HashSet<>();
        for (Vertex v : this.graph.keySet()) {
            if (v instanceof PlayerVertex) {
                uuids.add(((PlayerVertex) v).getPlayer());
            }
        }
        return uuids;
    }

    /**
     * A function to get all the vertecies of a given type
     * @param type The vertex type
     * @return All the vertecies of that type
     */
    public Set<Vertex> getAllVertecies(Class<? extends Vertex> type) {
        Set<Vertex> list = new HashSet<>();
        for (Vertex v : graph.keySet()) {
            if (type.isAssignableFrom(v.getClass())) {
                list.add(v);
            }
        }
        return list;
    }

    /**
     * A method to get all the paths from a player that are over a given distance
     * @param player The player
     * @param distance The distance
     * @return The paths from that player within that distance
     */
    public Set<Path> getAllPathsList(UUID player, int distance) {
        Set<Path> list = new HashSet<>();
        Map<Vertex, Path> map = getPaths(new PlayerVertex(player), (Set<Vertex>) getAllVertecies(PlayerVertex.class), distance, false);
        for (Vertex v : map.keySet()) {
            if (map.get(v) != null) {
                list.add(map.get(v));
            }
        }
        return list;
    }

    /**
     * A function to get all the possible paths in the graph that are greater then a given scope
     * WARNING: This can take alot of time to complete, and it presents duplicate paths in oposate directions
     * @param scope The scope to search for
     * @return All the paths of a given scope
     */
    public Set<Path> getAllPaths(int scope) {
        if (!searchingAllPaths) {
            searchingAllPaths = true;
            Set<Path> set = new HashSet<>();
            for (UUID uuid : this.getAllPlayers()) {
                set.addAll(getAllPathsList(uuid, scope));
                //System.out.println("Indexed");
            }
            searchingAllPaths = false;
            return set;
        }
        return null;
    }

    /*@ServerTic(delay = 60*60, isAsync = true)
    public static void updateAllPaths() {
        updateAllPaths(IPGraph.getGraph());
    }

    public static void updateAllPaths(IPGraph graph) {
        ObjectUtils.debug(Level.WARNING, "Started to search paths for alts");
        graph.allPaths = new HashMap<>();
        for (UUID uuid: graph.getAllPlayers()) {
            HashMap<Integer, Set<Path>> paths = graph.getAllPaths(uuid);
            for (int key: paths.keySet()) {
                if (!graph.allPaths.containsKey(key)) {
                    graph.allPaths.put(key, new HashSet<>());
                }
               graph.allPaths.get(key).addAll(paths.get(key));
            }
            //System.out.println("Indexed");
        }
        ObjectUtils.debug(Level.WARNING, "Completed paths search");
    }*/

    /**
     * A function to get all the paths that a player connects to
     * @param player The player you are getting paths for
     * @return All the paths that the player has, sorted in a map by their length
     */
    public HashMap<Integer, Set<Path>> getAllPaths(UUID player) {
        return this.getAllPaths(player, 0);
    }

    /**
     * A function to get all the paths of a given player that are greater then a given distance
     * @param player The player
     * @param distance The distance
     * @return The paths sorted by length
     */
    public HashMap<Integer, Set<Path>> getAllPaths(UUID player, int distance) {
        PlayerVertex p = new PlayerVertex(player);
        Map<Vertex, Path> map = getPaths(p, (Set<Vertex>) getAllVertecies(PlayerVertex.class), distance, true);
        HashMap<Integer, Set<Path>> paths = new HashMap<>();
        for (Vertex v : map.keySet()) {
            Path path = map.get(v);
            if (path != null) {
                int length = path.length();
                Set<Path> list = paths.getOrDefault(length, new HashSet<Path>());
                list.add(path);
                paths.put(length, list);
            }
        }
        return paths;
    }

    /**
     * A function to seraize the IPGraph into a string
     * @return The IPGraph
     */
    public String serialize() {
        StringBuffer buffer = new StringBuffer();
        for (Vertex v : graph.keySet()) {
            if (v instanceof IPVertex) {
                StringBuilder s = new StringBuilder(((IPVertex) v).getIp());
                for (Vertex p : graph.get(v)) {
                    if (p instanceof PlayerVertex) {
                        s.append(" " + ((PlayerVertex) p).getPlayer());
                    }
                }
                buffer.append(s.toString() + "\n");
            }
        }
        return buffer.toString();

    }

    /**
     * A function to get the path from one player to another, returns Null if no path exists
     * @param p1 Player 1
     * @param p2 Player 2
     * @return The path from Player 1 to Player 2
     */
    public Path getPath(UUID p1, UUID p2) {
        return getPath(new PlayerVertex(p1), new PlayerVertex(p2));
    }

    /**
     * A function to get the path from vertex 1 to vertex 2
     * @param v1 Vertex 1
     * @param v2 Vertex 2
     * @return The path from vertex 1 to 2, may return null if no path exists
     */
    public Path getPath(Vertex v1, Vertex v2) {
        Set<Vertex> vertices = new HashSet<>();
        vertices.add(v2);
        return getPaths(v1, vertices).get(v2);
    }

    /**
     * A function to get all the paths from a starting vertex to a set of vertecies at once!
     * @param v1 The starting vertex
     * @param vertices The vertecies to search for
     * @return The best path from the starting vertex to each vertex in the set
     */
    public Map<Vertex, Path> getPaths(Vertex v1, Set<Vertex> vertices) {
        return this.getPaths(v1, vertices, 0, true);
    }

    /**
     * A function to get all the paths from a starting vertex to a set of vertecies at once!
     * @param v1 The starting vertex
     * @param vertices The vertecies to search for
     * @param distance The distance that it must be in order to be registered
     * @param allowDuplicates Weather or not to allow duplicate paths
     * @return A map of all the possible paths, the key represents the final destination and the path represents how it got there
     */
    public Map<Vertex, Path> getPaths(Vertex v1, Set<Vertex> vertices, int distance, boolean allowDuplicates) {
        PriorityQueue<Path> PQ = new PriorityQueue<>(Path::compareTo);
        PQ.add(new Path(v1));
        HashMap<Vertex, Path> paths = new HashMap<>();
        Set<Vertex> travelled = new HashSet<>();
        Path path;
        do {
            path = PQ.poll();
            Vertex top = path.getTopVertex();
            int length = path.length();
            if (!travelled.contains(top)) {
                if (graph.containsKey(top)) {
                    if (vertices.contains(top) && length > 1 && length > distance) {
                        paths.put(top, path);
                        if (!allowDuplicates) {
                            Vertex v = path.getVertices().get(path.getVertices().size() - 2);
                            paths.remove(v);
                        }
                    }
                    for (Vertex v : graph.get(top)) {
                        if (!v.equals(top)) {
                            Path clone = (Path) path.clone();
                            clone.getVertices().add(v);
                            PQ.add(clone);
                        }
                    }
                }
                travelled.add(top);
            }
        } while (vertices.size() > paths.size() && !PQ.isEmpty());
        for (Vertex v : vertices) {
            if (!paths.containsKey(v)) {
                paths.put(v, null);
            }
        }
        return paths;
    }

    /**
     * The Vertex class
     */
    public static class Vertex implements Comparable<Vertex> {
        /**
         * The vertex number
         */
        private int num;

        /**
         * The vertex constructor
         * @param num The vertex number
         */
        public Vertex(int num) {
            this.num = num;
        }
        //Overrides the hashCode, this is used to make multiple different vertecies of the same object act as the same object
        @Override
        public int hashCode() {
            return num;
        }

        /**
         * Overrides the equalTo method to compare vertex numbers
         * @param obj The object to compare to
         * @return If they have the same vertex ID
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Vertex && this.compareTo((Vertex) obj) == 0;
        }

        /**
         * The compareTo method
         * @param o The object to compare to
         * @return The difference of their vertex IDs
         */
        @Override
        public int compareTo(Vertex o) {
            return this.num - o.num;
        }
    }

    /**
     * The IP Vertex class
     */
    public static class IPVertex extends Vertex {
        /**
         * The IP it represents
         */
        private String ip;

        /**
         * The constructor of the IP
         * @param ip The IP constructor
         */
        public IPVertex(String ip) {
            super(ip.hashCode());
            this.ip = ip;
        }

        /**
         * The IP of the vertex
         * @return The IP
         */
        public String getIp() {
            return ip;
        }
    }

    /**
     * A PlayerVertex
     */
    public static class PlayerVertex extends Vertex {

        /**
         * The UUID of the player
         */
        private UUID player;

        /**
         * The constructor for PlayerVertecies
         */

        public PlayerVertex(UUID player) {
            super(player.hashCode());
            this.player = player;
        }

        /**
         * The UUID of this vertex
         * @return The player UUID of the vertex
         */
        public UUID getPlayer() {
            return player;
        }
    }

    /**
     * A path of vertecies along the graph
     */
    public class Path implements Comparable<Path> {
        /**
         * A list that keeps track of the path from point 0 to point vertecies.size()-1
         */
        private List<Vertex> vertices = new ArrayList<>();

        /**
         * The constructor for a path
         * @param vertex The starting vertex
         */
        public Path(Vertex vertex) {
            this.getVertices().add(vertex);
        }

        /**
         * Get the list of vertecies
         * @return The list of vertecies
         */
        public List<Vertex> getVertices() {
            return vertices;
        }

        /**
         * A function to get the last vertex quickly
         * @return The last vertex
         */
        public Vertex getTopVertex() {
            return this.getVertices().get(this.getVertices().size() - 1);
        }

        public PlayerVertex[] toVertexArray() {
            List<PlayerVertex> vertices = new ArrayList<>();
            for (Vertex v : this.getVertices()) {
                if (v instanceof PlayerVertex) {
                    vertices.add((PlayerVertex) v);
                }
            }
            return vertices.toArray(new PlayerVertex[vertices.size()]);
        }

        /**
         * A function to get the length of the path only counting players
         * @return The length of the path only counting players
         */
        public int length() {
            return this.toVertexArray().length;
        }

        /**
         * Overrides the to String method to show the path
         * @return The path as a String
         */
        @Override
        public String toString() {
            String print = ((PlayerVertex) this.getVertices().get(0)).getPlayer().hashCode() + " ===> " + ((PlayerVertex) this.getTopVertex()).getPlayer().hashCode() + ":";
            for (PlayerVertex pv : this.toVertexArray()) {
                print += " > " + pv.getPlayer().hashCode();
            }
            return print;
        }

        /**
         * A function to display the graph in a Minecraft-GUI format
         * @param s The color settings to display it in
         * @return The Minecraft Text Display of the Path
         */
        public TextComponent toTextComponent(EditorSettings s) {
            net.md_5.bungee.api.chat.TextComponent message = new net.md_5.bungee.api.chat.TextComponent("");
            for (int i = 0; i < vertices.size(); i++) {
                Vertex v = vertices.get(i);
                if (v instanceof IPGraph.PlayerVertex) {
                    net.md_5.bungee.api.chat.TextComponent component = new net.md_5.bungee.api.chat.TextComponent(s.getVariable() + Bukkit.getOfflinePlayer(((IPGraph.PlayerVertex) v).getPlayer()).getName());
                    String vertices = "";
                    for (IPGraph.Vertex adj : getAdjasantVertecies(v)) {
                        vertices += s.getHelp() + ((IPGraph.IPVertex) adj).getIp() + "\n";
                    }
                    if (vertices.endsWith("\n")) {
                        vertices = vertices.substring(0, vertices.length() - 1);
                    }
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new net.md_5.bungee.api.chat.TextComponent(vertices)}));
                    message.addExtra(component);
                } else if (v instanceof IPGraph.IPVertex) {
                    net.md_5.bungee.api.chat.TextComponent component = new net.md_5.bungee.api.chat.TextComponent(s.getValue() + ((IPGraph.IPVertex) v).getIp());
                    String vertices = "";
                    for (IPGraph.Vertex adj : getAdjasantVertecies(v)) {
                        vertices += s.getHelp() + Bukkit.getOfflinePlayer(((IPGraph.PlayerVertex) adj).getPlayer()).getName() + "\n";
                    }
                    if (vertices.endsWith("\n")) {
                        vertices = vertices.substring(0, vertices.length() - 1);
                    }
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new net.md_5.bungee.api.chat.TextComponent(vertices)}));
                    message.addExtra(component);
                }
                if (i != vertices.size() - 1) {
                    message.addExtra(s.getColon() + " <-> ");
                }
            }
            return message;
        }

        /**
         * A function to clone the path
         * @return An exact copy of the path
         */
        public Path clone() {
            Path path = new Path(this.getTopVertex());
            path.getVertices().clear();
            path.getVertices().addAll(this.getVertices());
            return path;
        }

        /**
         * A function to  compare paths by size
         * @param o The path to compare to
         * @return The difference in path sizes
         */
        @Override
        public int compareTo(Path o) {
            return this.getVertices().size() - o.getVertices().size();
        }
    }

}
