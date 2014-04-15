package me.AstramG.MoreMobHeads;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import me.AstramG.MoreMobHeads.ImgMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class MoreMobHeads extends JavaPlugin implements Listener{
	
	/*
	 The plugin will say on it's BukkitDev page that the default config for downloading the head file is true.
	 */
	
	Map<String, String> mobs = new HashMap<String, String>();
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		if (this.getConfig().getBoolean("useCloudFile")) {
			try {
				URL mobSkins = new URL(this.getConfig().getString("headFileURL"));
				BufferedReader in = new BufferedReader(new InputStreamReader(mobSkins.openStream()));
				String inputLine;
			 
				while ((inputLine = in.readLine()) != null) {
					String line[] = inputLine.split(",");
					mobs.put(line[0], line[1]);
				}
			 
				System.out.println("[MoreMobHeads] Heads file in the cloud has been successfully loaded!");
				in.close();
			 
				} catch (Exception e) {
					System.out.println("[MoreMobHeads] Heads file in the cloud hasn't loaded. Are you sure you have an active internet connection?");
					System.out.println("[MoreMobHeads] Cannot reach Cloud File... Will load head from flat file.");
					bootFromFlatFile();
				}
		} else {
			System.out.println("[MoreMobHeads] Cloud Head File is disabled... Will load head from flat file.");
			bootFromFlatFile();
		}
	}
	
	private void bootFromFlatFile() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(this.getResource("mobHeadFlatFile.txt")));
			String line = null;
			while ((line = reader.readLine()) != null) {
			    String[] parts = line.split(",");
			    mobs.put(parts[0], parts[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("mmh")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.BOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BLUE + "-------------------" + ChatColor.YELLOW + "---------------" + ChatColor.BLUE + "-------------------");
				sender.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "MoreMobHeads Commands:");
				sender.sendMessage(ChatColor.YELLOW + "- /mmh spawnHead <mobName> (Spawns the specified mob's head)");
				sender.sendMessage(ChatColor.YELLOW + "- /mmh spawnPlayerHead <playerName> (Spawns the specified player's head)");
				sender.sendMessage(ChatColor.YELLOW + "- /mmh list (Lists all the avaibile mob names)");
				sender.sendMessage(ChatColor.BOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BLUE + "-------------------" + ChatColor.YELLOW + "---------------" + ChatColor.BLUE + "-------------------");
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					sender.sendMessage(ChatColor.BOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BLUE + "-------------------" + ChatColor.YELLOW + "---------------" + ChatColor.BLUE + "-------------------");
					sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "List of Mobs:");
					for (String mob : mobs.keySet()) {
						sender.sendMessage(ChatColor.YELLOW + "- " + mob);
					}
					sender.sendMessage(ChatColor.BOLD + "" + ChatColor.STRIKETHROUGH + ChatColor.BLUE + "-------------------" + ChatColor.YELLOW + "---------------" + ChatColor.BLUE + "-------------------");
				}
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("spawnHead")) {
					if (sender.hasPermission("MoreMobHeads.SpawnMobHead") || sender.isOp()) {
						ItemStack head = spawnHead(args[1]);
						if (head == null) {
							sender.sendMessage(ChatColor.RED + "[MoreMobHeads] Invalid mob name!");
							return true;
						}
						if (sender instanceof Player) {
							Player player = (Player) sender;
							player.getInventory().addItem(head);
							player.sendMessage(ChatColor.GREEN + "[MoreMobHeads] " + args[1] + " Head Spawned!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[MoreMobHeads] You don't have permission for this task!");
					}
				} else if (args[0].equalsIgnoreCase("spawnPlayerHead")) {
					if (!(sender instanceof Player))
						return true;
					if (sender.hasPermission("MoreMobHeads.SpawnPlayerHead") || sender.isOp()) {
						Player player = (Player) sender;
						ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
						SkullMeta sm = (SkullMeta) head.getItemMeta();
						sm.setOwner(args[1]);
						if (this.getConfig().getBoolean("getLoreHead")) {
							String[] lines = null;
							try {
								BufferedImage image = (BufferedImage) getImage(args[1]);
								BufferedImage simage = image.getSubimage(8, 8, 8, 8);
								ChatColor[][] colors = ImgMessage.toChatColorArray(simage, 8);
								lines = ImgMessage.toImgMessage(colors, ImgMessage.ImgChar.MEDIUM_SHADE.getChar());
								sm.setLore(Arrays.asList(lines));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						head.setItemMeta(sm);
						player.getInventory().addItem(head);
						player.sendMessage(ChatColor.GREEN + "[MoreMobHeads] You spawned " + args[1] + "'s Head!");
					} else {
						sender.sendMessage(ChatColor.RED + "[MoreMobHeads] You don't have permission for this task!");
					}
				}
			}
		}
		return true;
	}
	
	public Image getImage(String name) {
		Image image = null;
		try {
			URL url = new URL("http://s3.amazonaws.com/MinecraftSkins/" + name + ".png");
			image = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public ItemStack spawnHead(String mobName) {
		if (mobs.containsKey(mobName)) {
			for (String mob : mobs.keySet()) {
				if (mobName.toLowerCase().equalsIgnoreCase(mob.toLowerCase())) {
					ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
					SkullMeta sm = (SkullMeta) item.getItemMeta();
					sm.setDisplayName(ChatColor.RESET + "" + mobName + " Head");
					sm.setOwner(mobs.get(mob));
					if (this.getConfig().getBoolean("getLoreHead")) {
						BufferedImage image = (BufferedImage) getImage(mobs.get(mob));
						BufferedImage simage = image.getSubimage(8, 8, 8, 8);
						ChatColor[][] colors = ImgMessage.toChatColorArray(simage, 8);
						String[] lines = ImgMessage.toImgMessage(colors, ImgMessage.ImgChar.MEDIUM_SHADE.getChar());
						sm.setLore(Arrays.asList(lines));
					}
					item.setItemMeta(sm);
					return item;
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "deprecation", "incomplete-switch" })
	@EventHandler
	public void death(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			String entityName = event.getEntityType().getName();
			if (mobs.containsKey(entityName)) {
				if (this.getConfig().getBoolean("Drops.MobHeads.Enabled")) {
					int chance = this.getConfig().getInt("Drops.MobHeads.Chance");
					Random rand = new Random();
					int fact = rand.nextInt(101);
					if (fact <= chance) {
						ItemStack head = spawnHead(entityName);
						event.getDrops().add(head);
					}
				}
			}
			if (event.getEntity().getType() == EntityType.CREEPER || event.getEntity().getType() == EntityType.SKELETON || event.getEntity().getType() == EntityType.ZOMBIE || event.getEntity().getType() == EntityType.WITHER) {
				if (this.getConfig().getBoolean("Drops.MobHeads.Enabled")) {
					int chance = this.getConfig().getInt("Drops.MobHeads.Chance");
					Random rand = new Random();
					int fact = rand.nextInt(101);
					if (fact <= chance) {
						byte damage = (byte) 0;
						switch(event.getEntity().getType()) {
						case CREEPER:
							damage = (byte) 4;
							break;
						case ZOMBIE:
							damage = (byte) 2;
							break;
						case WITHER:
							damage = (byte) 1;
							break;
						}
						ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, damage);
						event.getDrops().add(head);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent event) {
		if (this.getConfig().getBoolean("Drops.PlayerHeads.Enabled")) {
			int chance = this.getConfig().getInt("Drops.PlayerHeads.Chance");
			Random rand = new Random();
			int fact = rand.nextInt(101);
			if (fact <= chance) {
				ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
				SkullMeta sm = (SkullMeta) head.getItemMeta();
				sm.setOwner(event.getEntity().getName());
				if (this.getConfig().getBoolean("getLoreHead")) {
					String[] lines = null;
					try {
						BufferedImage image = (BufferedImage) getImage(event.getEntity().getName());
						BufferedImage simage = image.getSubimage(8, 8, 8, 8);
						ChatColor[][] colors = ImgMessage.toChatColorArray(simage, 8);
						lines = ImgMessage.toImgMessage(colors, ImgMessage.ImgChar.MEDIUM_SHADE.getChar());
						sm.setLore(Arrays.asList(lines));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				head.setItemMeta(sm);
				event.getDrops().add(head);
			}
		}
	}
	
	
}
