# Paper Plugin Suite - Vollständige Feature-Plugin für Paper 1.21.10

Ein umfassendes Minecraft Paper-Server-Plugin mit allen essentiellen Spieler-Features für Survival-Server.

## ✨ Features

### 1. **Random Teleport (/rtp)**
- Teleportiert Spieler zufällig auf der Welt
- Sicherheitsprüfungen: Lava, Wasser, Block-Collision
- Warmup-Delay mit Abbruch bei Bewegung
- Cooldown und Kosten-System
- Biom-Filter und Anti-Spawn-Farm-Checks
- Hourly-Limits gegen Abuse

**Befehle:**
- `/rtp` - Teleportiert zur aktuellen Welt
- `/rtp [welt]` - Teleportiert zu spezifischer Welt

**Permissions:**
- `plugin.rtp.use` - Benutze /rtp
- `plugin.rtp.bypasscooldown` - Ignoriere Cooldown
- `plugin.rtp.bypasscost` - Ignoriere Kosten

---

### 2. **NPC Management (/npc)**
- Erstelle statische oder interaktive NPCs
- NPC-AI Verhalten (Stationär, Patrouille, Blick-Rotation)
- Hologramme über NPCs
- Shop-, Quest- und Teleporter-Integration
- Auto-Despawn nach Server-Restart

**Befehle:**
- `/npc create <name>` - Erstelle NPC an deiner Position
- `/npc remove <id>` - Lösche NPC
- `/npc list` - Zeige alle NPCs
- `/npc look <id>` - NPC schaut auf dich

**Permissions:**
- `plugin.npc.create` - Erstelle NPCs
- `plugin.npc.remove` - Lösche NPCs
- `plugin.npc.modify` - Bearbeite NPCs
- `plugin.npc.admin` - Admin-Zugriff

---

### 3. **Auction House (/auction)**
- Spieler können Items versteigern
- Gebühren-System (Listing Fee + Verkaufsprovision)
- Mindestgebot und Sofortkauf-Option
- Item-Blacklist (verhindert OP-Items)
- Anti-Bid-Bot-Schutz
- Automatische Auktionsverwaltung

**Befehle:**
- `/auction create <item> <preis>` - Erstelle Auktion
- `/auction bid <id> <betrag>` - Platziere Gebot
- `/auction list` - Zeige aktive Auktionen
- `/auction cancel <id>` - Hebe Auktion auf
- `/ah` - GUI (möglich)

**Permissions:**
- `plugin.auction.create` - Erstelle Auktionen
- `plugin.auction.bid` - Platziere Gebote
- `plugin.auction.list` - Zeige Auktionen
- `plugin.auction.cancel` - Hebe auf
- `plugin.auction.admin` - Admin-Zugriff

---

### 4. **Shop System (/shop)**
- Admin-Shops mit vordefinierten Items
- Player-Shops mit Limits
- GUI-basierte Shop-Interaktion
- Steuern und Diskounts nach Rang
- Item-Kategorien
- Anti-Duping-Schutz

**Befehle:**
- `/shop create <name>` - Erstelle eigenen Shop
- `/shop list` - Zeige Shops
- `/shop buy <id> <anzahl>` - Kaufe Items
- `/shop sell <id> <anzahl>` - Verkaufe Items

**Permissions:**
- `plugin.shop.use` - Nutze Shops
- `plugin.shop.create` - Erstelle Shops
- `plugin.shop.admin` - Admin-Zugriff

---

### 5. **Spawn System (/spawn)**
- Schnelle Rückkehr zum Server-Spawn
- Warmup-Delay
- Cooldown-System
- Welten-spezifische Spawns
- Combat-Log-Prävention

**Befehle:**
- `/spawn` - Teleportiere zum Spawn
- `/spawn set` - Setze Spawn an deiner Position
- `/setspawn` (Alias für Set)

**Permissions:**
- `plugin.spawn.use` - Nutze Spawn
- `plugin.spawn.set` - Setze Spawn
- `plugin.spawn.bypasswarmup` - Ignoriere Warmup

---

### 6. **Homes (/home, /sethome, /delhome)**
- Persönliche Wiederaufenthaltsorte
- Rank-basierte Home-Limits
- Warmup und Cooldown
- Optionale Kosten
- Blacklist-Welten
- Beschränkungen für PvP/Claims

**Befehle:**
- `/home [name]` - Teleportiere zu Home
- `/sethome [name]` - Setze Home
- `/delhome [name]` - Lösche Home
- `/homes` - Zeige deine Homes

**Permissions:**
- `plugin.home.use` - Nutze Homes
- `plugin.home.set` - Setze Homes
- `plugin.home.del` - Lösche Homes
- `plugin.home.limit.<num>` - Setze Limit per Rang

---

### 7. **Teleport Requests (/tpa, /tpahere)**
- Höfliche Teleport-Anfragen zwischen Spielern
- TPA und TPA-Here Modi
- Timeout für Anfragen (30 Sekunden)
- Accept/Deny System
- Spieler-Blockier-Liste
- Cooldown und Rate-Limits

**Befehle:**
- `/tpa <spieler>` - Anfrage, zu Spieler zu teleportieren
- `/tpahere <spieler>` - Anfrage Spieler zu dir
- `/tpaccept [spieler]` - Akzeptiere Anfrage
- `/tpdeny [spieler]` - Lehne Anfrage ab

**Permissions:**
- `plugin.tpa.send` - Sende TPA
- `plugin.tpa.accept` - Akzeptiere
- `plugin.tpa.deny` - Lehne ab
- `plugin.tpa.here` - Nutze TPA-Here

---

### 8. **Crates (/crate)**
- Verschiedene Crate-Typen (Common, Rare, Epic)
- Gewichtetes Loot-System
- Keys (physisch oder digital)
- GUI-basiertes Öffnen mit Animation
- Rare-Roll Broadcast
- Daily Limits

**Befehle:**
- `/crate open <crate>` - Öffne Crate
- `/crate give <spieler> <crate> <anzahl>` - Gib Crate
- `/crate list` - Zeige Crates
- `/crate preview <crate>` - Vorschau

**Permissions:**
- `plugin.crate.use` - Öffne Crates
- `plugin.crate.give` - Gib Crates
- `plugin.crate.admin` - Admin

---

## ⚙️ Konfiguration

### Hauptconfig (config.yml)
Alle Features sind einzeln aktivierbar/deaktivierbar.

### Feature-spezifische Configs
- `config-rtp.yml` - RTP-Einstellungen
- `config-npc.yml` - NPC-Verwaltung
- `config-auction.yml` - Auktionshaus
- `config-shop.yml` - Shop-System
- `config-spawn.yml` - Spawn-Punkte
- `config-homes.yml` - Homes
- `config-tpa.yml` - TPA
- `config-crates.yml` - Crates

### Economy Integration
- Vault-Support für Economy
- Configurable Kosten für alle Features
- Gebühren-Systeme

### Permission Integration
- LuckPerms Support (optional)
- Standard Permission-Nodes
- Rank-basierte Features

---

## 📋 Permission-Beispiele (LuckPerms)

```bash
# Gruppe erstellen
/lp creategroup vip

# Permissions setzen
/lp group vip permission set plugin.rtp.use true
/lp group vip permission set plugin.home.limit.10 true
/lp group vip permission set plugin.tpa.send true

# Prefix setzen
/lp group vip meta setprefix 100 "&6[VIP] &7"

# Spieler hinzufügen
/lp user <spieler> parent add group.vip
```

---

## 🔧 Installation

1. **Voraussetzungen:**
   - Paper 1.21.10 oder höher
   - Java 21+
   - Maven (zum Kompilieren)
   - Vault-Plugin (optional, für Economy)
   - LuckPerms-Plugin (optional, für Permissions)

2. **Kompilieren:**
   ```bash
   mvn clean package
   ```

3. **Installation:**
   - Kopiere JAR in `plugins/` Ordner
   - Starte Server neu
   - Konfiguriere `plugins/PaperPluginSuite/config.yml`

4. **Konfigurieren:**
   - Bearbeite alle `config-*.yml` Dateien
   - Setze Permissions über LuckPerms/Standard-Permissions
   - Restart Server

---

## 🚀 Best Practices

### Für Server-Administratoren:

1. **Economy-Balancing:**
   - Setze realistische Kosten basierend auf deinem Server
   - Verwende Auktions-Gebühren zur Geldvernichtung
   - Balance zwischen Free und Premium-Features

2. **Rank-System:**
   - Erstelle Ränge: default → trusted → vip → premium
   - Nutze Permission-Inheritance
   - Vergebe mehr Homes/Features mit höheren Rängen

3. **Anti-Abuse:**
   - Logge alle Transaktionen
   - Setze Rate-Limits
   - Prüfe regelmäßig auf Exploit-Versuche

4. **Performance:**
   - Deaktiviere unnötige Features
   - Nutze File-basierte DB für kleine Server
   - MySQL für größere Installationen

### Für Entwickler:

1. **Custom Integrationen:**
   - Erweiterbar durch Listener und Manager
   - Event-basierte Architektur
   - Configuration-basierte Anpassungen

2. **Sicherheit:**
   - Alle User-Inputs validieren
   - Permission-Checks in jedem Command
   - Keine sensitive Daten in Config

3. **Testing:**
   - Test alle Commands mit verschiedenen Perms
   - Prüfe Economy-Integration
   - Validate Config-Parsing

---

## 📝 Logging & Debugging

Enable Debug-Modus in `config.yml`:
```yaml
debug: true
```

Logs werden in `plugins/PaperPluginSuite/logs/` gespeichert.

---

## 🐛 Bekannte Limitierungen

- NPCs sind derzeit Placeholder (keine echten Entitäten)
- Shops speichern Daten in Files (für größere Server: MySQL empfohlen)
- Einige Features benötigen Vault für volle Funktionalität

---

## 📞 Support & Erweiterungen

Das Plugin ist als Basis gedacht. Du kannst einfach erweitern durch:
- Neue Commands
- Zusätzliche Manager-Klassen
- Custom Events
- GUI-Integration
- Datenbank-Integration

---

## 📄 Lizenz

MIT License - Frei verwendbar und modifizierbar.

---

**Viel Spaß beim Nutzen! 🎮**
