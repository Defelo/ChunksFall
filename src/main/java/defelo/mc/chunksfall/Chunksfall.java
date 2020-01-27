package defelo.mc.chunksfall;

import defelo.mc.chunksfall.animations.*;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Chunksfall extends JavaPlugin implements Listener {

    private static Random random = new Random();

    private boolean running = false;

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "Chunksfall enabled");

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "Chunksfall disabled");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equals("chunksfall")) {
            if (args.length == 1) {
                return Stream.of("info", "start", "stop", "reset").filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
            } else if (args.length == 2) {
                if (args[0].equals("reset")) {
                    return Stream.of("chunk", "world").filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                }
            }
            return Collections.emptyList();
        }
        return super.onTabComplete(sender, command, alias, args);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("chunksfall")) {
            if (args.length < 1) return false;

            String cmd = args[0];
            switch (cmd) {
                case "info":
                    if (running) {
                        getServer().broadcastMessage(ChatColor.GREEN + "Chunksfall is currently running!");
                    } else {
                        getServer().broadcastMessage(ChatColor.RED + "Chunksfall has not been started.");
                    }
                    long count = getServer().getWorlds().stream().map(world ->
                            world.getEntities().stream().filter(entity -> entity.getScoreboardTags().contains("chunk_deleted")).count()
                    ).reduce(0L, Long::sum);
                    getServer().broadcastMessage(count + " Chunk(s) have been marked as deleted.");
                    break;
                case "start":
                    if (running) {
                        getServer().broadcastMessage(ChatColor.RED + "Chunksfall has already been started!");
                    } else {
                        getServer().broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Chunksfall started!");
                        running = true;
                    }
                    break;
                case "stop":
                    if (running) {
                        getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Chunksfall stopped!");
                        running = false;
                    } else {
                        getServer().broadcastMessage(ChatColor.RED + "Chunksfall has not been started!");
                    }
                    break;
                case "reset":
                    if (args.length < 2) return false;

                    if (args[1].equals("world")) {
                        getServer().getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
                            if (entity.getScoreboardTags().contains("chunk_deleted"))
                                entity.remove();
                        }));
                        getServer().broadcastMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Chunksfall has been reset!");
                    } else if (args[1].equals("chunk") && sender instanceof Player) {
                        Player player = (Player) sender;
                        Arrays.stream(player.getLocation().getChunk().getEntities()).forEach(entity -> {
                            if (entity.getScoreboardTags().contains("chunk_deleted"))
                                entity.remove();
                        });
                        getServer().broadcastMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Chunk has been reset!");
                    } else return false;
                    break;
                default:
                    return false;
            }
            return true;
        }
        return false;
    }

    private void delete_column(Chunk chunk, int x, int z) {
        for (int y = 255; y >= 1; y--) {
            chunk.getBlock(x, y, z).setType(Material.AIR);
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if (!running) return;
        Player player = event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();
        Chunk spawnChunk = chunk.getWorld().getSpawnLocation().getChunk();
        if (Math.abs(chunk.getX() - spawnChunk.getX()) <= 1 && Math.abs(chunk.getZ() - spawnChunk.getZ()) <= 1)
            return;
        if (Arrays.stream(chunk.getEntities()).noneMatch(entity -> entity.getScoreboardTags().contains("chunk_deleted"))) {
            AreaEffectCloud aec = (AreaEffectCloud) chunk.getWorld().spawnEntity(chunk.getBlock(0, 0, 0).getLocation().add(0, -20, 0), EntityType.AREA_EFFECT_CLOUD);
            aec.setDuration(Integer.MAX_VALUE - 20);
            aec.setRadius(0);
            aec.addScoreboardTag("chunk_deleted");

//            getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.MAGIC + "XXX"
//                    + ChatColor.RESET + "" + ChatColor.RED + "" + ChatColor.BOLD + "" + " DELETING CHUNK " + chunk.getX() + " " + chunk.getZ() + " " + ChatColor.MAGIC + "XXX");

            int rotation = random.nextInt(4);
            IAnimation animation;
            switch (random.nextInt(6)) {
                case 0:
                    animation = new LineByLineAnimation();
                    break;
                case 1:
                    animation = new LineByLine2Animation();
                    break;
                case 2:
                    animation = new LineByLine4Animation();
                    break;
                case 3:
                    animation = new SpiralAnimation();
                    break;
                case 4:
                    animation = new SpiralReverseAnimation();
                    break;
                case 5:
                default:
                    animation = new RandomAnimation();
                    break;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!running) {
                        cancel();
                        return;
                    }

                    int[] coords = animation.get_column();
                    switch (rotation) {
                        case 0:
                            delete_column(chunk, coords[0], coords[1]);
                            break;
                        case 1:
                            delete_column(chunk, 15 - coords[1], coords[0]);
                            break;
                        case 2:
                            delete_column(chunk, 15 - coords[0], 15 - coords[1]);
                            break;
                        case 3:
                            delete_column(chunk, coords[1], 15 - coords[0]);
                            break;
                    }
                    if (!animation.next()) cancel();
                }
            }.runTaskTimer(this, 0, 1);
        }
    }
}
