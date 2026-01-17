package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.entity.Player;
import java.util.*;

public class NPCManager {

    private final PaperPluginSuite plugin;
    private final Map<Integer, NPCData> npcs = new HashMap<>();
    private int npcIdCounter = 0;

    public class NPCData {
        public int id;
        public String name;
        public String world;
        public double x, y, z;
        public float yaw, pitch;
        public String behavior;
        public String type;

        public NPCData(int id, String name, String world, double x, double y, double z) {
            this.id = id;
            this.name = name;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.behavior = "STATIONARY";
            this.type = "CITIZEN";
        }
    }

    public NPCManager(PaperPluginSuite plugin) {
        this.plugin = plugin;
        loadNPCs();
    }

    public boolean createNPC(Player creator, String name, String world) {
        if (!creator.hasPermission("plugin.npc.create")) {
            creator.sendMessage("§c✗ Du darfst keine NPCs erstellen!");
            return false;
        }

        NPCData npc = new NPCData(npcIdCounter++, name, world, creator.getX(), creator.getY(), creator.getZ());
        npcs.put(npc.id, npc);
        creator.sendMessage("§a✓ NPC '" + name + "' erstellt! ID: " + npc.id);

        return true;
    }

    public boolean removeNPC(Player remover, int npcId) {
        if (!remover.hasPermission("plugin.npc.remove")) {
            remover.sendMessage("§c✗ Du darfst keine NPCs löschen!");
            return false;
        }

        if (npcs.remove(npcId) != null) {
            remover.sendMessage("§a✓ NPC mit ID " + npcId + " gelöscht!");
            return true;
        }

        remover.sendMessage("§c✗ NPC mit ID " + npcId + " nicht gefunden!");
        return false;
    }

    public NPCData getNPC(int id) {
        return npcs.get(id);
    }

    public List<NPCData> getAllNPCs() {
        return new ArrayList<>(npcs.values());
    }

    public void saveNPCs() {
        // Placeholder für Datenspeicherung
    }

    public void loadNPCs() {
        // Placeholder für Datenladen
    }
}
