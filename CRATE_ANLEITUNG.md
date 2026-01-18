#!/bin/bash
# ================================================================
# CRATE SYSTEM - ANLEITUNG
# ================================================================
#
# Datei: src/main/resources/config-crates.yml
# Befehl: /crate list (zeigt alle verfügbaren Crates)
# Befehl: /crate open <crate-name> (öffnet eine Crate)
# Befehl: /crate give <spieler> <crate-name> <anzahl> (gibt Crate an Spieler)
#
# ================================================================
# SCHRITT 1: NEUE CRATE ERSTELLEN
# ================================================================
#
# Öffne: src/main/resources/config-crates.yml
#
# Füge am Ende folgende Struktur hinzu:
#
# crates:
#   meine-crate:                 <- Eindeutiger Name (für /crate open meine-crate)
#     displayName: "§6Meine Crate"  <- Name mit Farbe (für /crate list)
#     totalWeight: 100           <- Summe aller Weight-Werte (AUTOMATISCH BERECHNET!)
#     items:
#       item1:                   <- Beliebiger Item-Key
#         itemName: "IRON_INGOT" <- Minecraft Material-Name
#         amount: 5              <- Anzahl Items beim Drop
#         weight: 30             <- Seltenheit (höher = wahrscheinlicher)
#       item2:
#         itemName: "GOLD_INGOT"
#         amount: 2
#         weight: 20
#
# ================================================================
# SCHRITT 2: WEIGHT SYSTEM verstehen
# ================================================================
#
# Weight bestimmt die WAHRSCHEINLICHKEIT, dass ein Item gewinnt!
#
# BEISPIEL:
# crates:
#   test:
#     displayName: "Test Crate"
#     totalWeight: 100
#     items:
#       item1:
#         itemName: "DIAMOND"
#         amount: 1
#         weight: 10           <- 10/100 = 10% Chance
#       item2:
#         itemName: "COAL_ORE"
#         amount: 5
#         weight: 30           <- 30/100 = 30% Chance
#       item3:
#         itemName: "IRON_INGOT"
#         amount: 10
#         weight: 60           <- 60/100 = 60% Chance (AM WAHRSCHEINLICHSTEN!)
#
# WICHTIG:
# - totalWeight muss NICHT manuell berechnet werden - wird ignoriert!
# - Es wird automatisch aus der Summe aller Item-Weights berechnet
# - Weight sollte nicht zu klein sein (mindestens 1)
#
# ================================================================
# SCHRITT 3: MATERIAL-NAMEN (itemName)
# ================================================================
#
# Alle gültigen Minecraft Material-Namen:
#
# INGOTS:
#   IRON_INGOT, GOLD_INGOT, COPPER_INGOT, NETHERITE_INGOT
#
# ORES:
#   IRON_ORE, GOLD_ORE, COAL_ORE, DIAMOND_ORE, EMERALD_ORE, 
#   LAPIS_ORE, REDSTONE_ORE, COPPER_ORE, TIN_ORE
#
# GEMS:
#   DIAMOND, EMERALD, LAPIS_LAZULI, AMETHYST_SHARD
#
# BLOCKS:
#   STONE, DIRT, OAK_LOG, OAK_LEAVES, OAK_PLANKS, 
#   GRASS_BLOCK, SAND, GRAVEL, COBBLESTONE
#
# SPEZIAL:
#   BEACON, DRAGON_EGG, END_ROD, AMETHYST_BLOCK, GOLD_BLOCK,
#   DIAMOND_BLOCK, EMERALD_BLOCK, NETHERITE_BLOCK
#
# Alle anderen Minecraft-Materialien sind auch möglich!
# Siehe: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html
#
# ================================================================
# SCHRITT 4: SERVER STARTEN & TESTEN
# ================================================================
#
# 1. config-crates.yml speichern
# 2. Projekt kompilieren:
#    mvn clean package -U
# 3. Server starten
# 4. Ingame testen:
#    /crate list          (alle Crates sehen)
#    /crate open common   (Common Crate öffnen)
#    /crate give <name> rare 5 (5 Rare Crates geben)
#
# ================================================================
# KOMPLETTES BEISPIEL: Luxus Crate
# ================================================================
#
# crates:
#   luxury:
#     displayName: "§6§lLuxury Crate"
#     totalWeight: 100
#     items:
#       diamanten:
#         itemName: "DIAMOND"
#         amount: 5
#         weight: 25
#       netherit:
#         itemName: "NETHERITE_INGOT"
#         amount: 2
#         weight: 20
#       smaragde:
#         itemName: "EMERALD"
#         amount: 10
#         weight: 30
#       gold:
#         itemName: "GOLD_BLOCK"
#         amount: 3
#         weight: 25
#
# ================================================================
# VORGEFERTIGTE CRATES IM PLUGIN
# ================================================================
#
# Das Plugin kommt mit diesen vordefinierten Crates:
#
# 1. COMMON       - Anfänger-Crate (Stein, Kohle, Holz)
# 2. RARE         - Mittlere Crate (Gold, Diamanten, Smaragde)
# 3. EPIC         - Seltene Crate (viele Diamanten, Netherite)
# 4. LEGENDARY    - Ultra-seltene Crate (Beacons, viele Netherite)
# 5. MONEY        - Geld-Crate (Gold und Smaragde)
# 6. BUILDER      - Baublock-Crate (Holz, Stein, Erde, Planken)
# 7. ORE          - Erz-Crate (verschiedene Erze)
#
# Du kannst diese JEDERZEIT ändern oder ergänzen!
#
# ================================================================
# HÄUFIGE FEHLER
# ================================================================
#
# FEHLER 1: Material-Name falsch geschrieben
# ❌ itemName: "Iron_Ingot"
# ✅ itemName: "IRON_INGOT"
#
# FEHLER 2: Fehlende Einträge
# ❌ items:          (kein Doppelpunkt!)
# ✅ items:          (mit Doppelpunkt!)
#
# FEHLER 3: Weight = 0
# ❌ weight: 0       (Item wird NIE gewinnen!)
# ✅ weight: 10      (mindestens 1!)
#
# FEHLER 4: totalWeight manuell berechnet falsch
# ❌ totalWeight: 95 (sollte 100 sein!)
# ✅ totalWeight: 100 oder einfach ignorieren
#
# ================================================================
# TIPPS & TRICKS
# ================================================================
#
# 1. SELTENE ITEMS: weight: 1 oder 2 (sehr selten)
# 2. NORMALE ITEMS: weight: 20-40 (häufig)
# 3. HÄUFIGE ITEMS: weight: 50+ (sehr häufig)
#
# 4. FARB-CODES für displayName:
#    §0 = Schwarz     §1 = Dunkelblau   §2 = Dunkelgrün   §3 = Cyan
#    §4 = Dunkelrot   §5 = Lila         §6 = Gold         §7 = Grau
#    §8 = Dunkelgrau  §9 = Blau         §a = Grün         §b = Türkis
#    §c = Rot         §d = Pink         §e = Gelb         §f = Weiß
#    §l = Bold        §o = Kursiv       §n = Unterstrichen
#
#    Beispiel: "§6§lGold Crate" = Fetter goldener Text
#
# 5. AMOUNTS: 1-64 sinnvoll (höher möglich, aber Spieler bekommt mehrere Stacks)
#
# ================================================================
