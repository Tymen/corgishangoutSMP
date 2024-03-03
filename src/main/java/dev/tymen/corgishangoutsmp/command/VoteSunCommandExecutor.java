package dev.tymen.corgishangoutsmp.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class VoteSunCommandExecutor implements CommandExecutor {

    private int yesVotes = 0;
    private boolean voteInProgress = false;
    private final JavaPlugin plugin;
    private final Set<Player> votedPlayers = new HashSet<>();

    public VoteSunCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can use this command.").color(NamedTextColor.RED));
            return true;
        }
        int playerCount = Bukkit.getServer().getOnlinePlayers().size();
        Player player = (Player) sender;

        // Start a vote
        if (args.length == 0) {
            if (!voteInProgress) {
                initiateVote(player);
            } else {
                player.sendMessage(Component.text("A vote is already in progress.").color(NamedTextColor.RED));
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("yes")) {
            // Register a "yes" vote
            if (!voteInProgress) {
                player.sendMessage(Component.text("There is no vote in progress.").color(NamedTextColor.RED));
                return true;
            }

            if (votedPlayers.contains(player)) {
                player.sendMessage(Component.text("You have already voted.").color(NamedTextColor.RED));
            } else {
                yesVotes++;
                votedPlayers.add(player);
                Bukkit.getServer().broadcast(Component.text(player.getName() + " has voted for sunny weather! " +
                        "Total votes: (" + yesVotes + "/" + (int) Math.ceil(playerCount / 2.0) + ")").color(NamedTextColor.GREEN));
                checkVote(playerCount, false);
            }
        }

        return true;
    }

    private void initiateVote(Player player) {
        int playerCount = Bukkit.getServer().getOnlinePlayers().size();

        // Reset votes for each voting session
        yesVotes = 0;
        votedPlayers.clear();

        if (playerCount <= 2) {
            // If only 1 or 2 players online, change weather immediately
            clearWeather();
            Bukkit.getServer().broadcast(Component.text("Since only one or two people are online, the weather has been changed to sunny.").color(NamedTextColor.YELLOW));
        } else {
            // For more than 2 players, start a vote
            voteInProgress = true;
            Bukkit.getServer().broadcast(Component.text("A Weather vote for sunny weather has started. Vote with /votesun yes. " +
                    "A total of " + (int) Math.ceil(playerCount / 2.0) + " votes are needed within 30 seconds.").color(NamedTextColor.GREEN));

            // Bukkit scheduler to wait for 30 seconds before checking the vote result
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (voteInProgress) {
                    checkVote(playerCount, true);
                    resetVote();
                }
            }, 600L); // 600L represents 30 seconds in ticks (20 ticks = 1 second)
        }
    }
    private void checkVote(int playerCount, boolean endVote) {
        boolean reachedVoteRequirement = yesVotes >= Math.ceil(playerCount / 2.0);
        if (reachedVoteRequirement) {
            clearWeather();
            resetVote();
            Bukkit.getServer().broadcast(Component.text("The weather has been changed to sunny!").color(NamedTextColor.GREEN));
        } else if (endVote){
            Bukkit.getServer().broadcast(Component.text("The vote to change the weather to sunny has failed.").color(NamedTextColor.RED));
        }
    }
    private void resetVote() {
        voteInProgress = false;
        votedPlayers.clear();
    }
    private void clearWeather() {
        org.bukkit.World world = Bukkit.getServer().getWorld("world");
        if (world != null) {
            world.setStorm(false);
            world.setThundering(false);
            world.setClearWeatherDuration(12000); // Set clear weather for 10 minutes
        }
    }
}