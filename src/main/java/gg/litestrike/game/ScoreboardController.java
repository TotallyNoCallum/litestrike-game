package gg.litestrike.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ScoreboardController {
	public static void setup_scoreboard(Teams t) {
		for (Player p : t.get_placers()) {
			give_player_scoreboard(p, gg.litestrike.game.Team.Placer);
		}
		for (Player p : t.get_breakers()) {
			give_player_scoreboard(p, gg.litestrike.game.Team.Breaker);
		}

	}

	private static void give_player_scoreboard(Player p, gg.litestrike.game.Team t) {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

		Team placers = sb.registerNewTeam("placers");
		placers.color(NamedTextColor.RED);
		placers.setAllowFriendlyFire(false);
		placers.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);

		Team breakers = sb.registerNewTeam("breakers");
		breakers.color(NamedTextColor.GREEN);
		breakers.setAllowFriendlyFire(false);
		placers.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);


		Component title = Component.text("LITESTRIKE").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, true);
		Objective obj = sb.registerNewObjective("main", Criteria.DUMMY, title);

		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		obj.getScore("9").setScore(9);
		obj.getScore("9").customName(Component.text(""));

		if (t == gg.litestrike.game.Team.Breaker) {
			obj.getScore("8").customName(Component.text("ᴛᴇᴀᴍ: ").append(Litestrike.BREAKER_TEXT));
			obj.getScore("8").setScore(8);
		} else {
			obj.getScore("8").customName(Component.text("ᴛᴇᴀᴍ: ").append(Litestrike.PLACER_TEXT));
			obj.getScore("8").setScore(8);
			// obj.getScore("ᴛᴇᴀᴍ: " + Litestrike.PLACER_TEXT).setScore(8);
		}
		obj.getScore("7").setScore(7);
		obj.getScore("7").customName(Component.text(""));

		obj.getScore("6").setScore(6);
		obj.getScore("6").customName(Component.text(""));

		obj.getScore("5").setScore(5);
		obj.getScore("5").customName(Component.text(""));

		obj.getScore("ꜱᴛᴀʏ ᴡɪᴛʜ ʏᴏᴜʀ ᴛᴇᴀᴍ!").setScore(4);

		obj.getScore("3").setScore(3);
		obj.getScore("3").customName(Component.text(""));

		obj.getScore("2").setScore(2);
		obj.getScore("2").customName(Component.text(""));

		obj.getScore("1").setScore(1);
		obj.getScore("1").customName(Component.text(""));

		obj.getScore("0").setScore(0);
		obj.getScore("0").customName(Component.text(""));

		Team money_count = sb.registerNewTeam("money_count");
		money_count.addEntry("7");
		money_count.prefix(Component.text("ᴍᴏɴᴇʏ: "));
		money_count.suffix(Component.text("error"));
		obj.getScore("7").setScore(7);

		Team wins_placers = sb.registerNewTeam("wins_placers");
		wins_placers.addEntry("3");
		wins_placers.prefix(Component.text("  "));
		wins_placers.suffix(Component.text("error2"));
		obj.getScore("3").setScore(3);

		Team wins_breakers = sb.registerNewTeam("wins_breakers");
		wins_breakers.addEntry("2");
		wins_breakers.prefix(Component.text("  "));
		wins_breakers.suffix(Component.text("error2"));
		obj.getScore("2").setScore(2);

		Team footline = sb.registerNewTeam("footline");
		footline.addEntry("0");
		footline.prefix(Component.text("ᴄʀʏꜱᴛᴀʟɪᴢᴇᴅ.ᴄᴄ "));
		// TODO put in the game id here
		footline.suffix(Component.text("TODO game id here"));
		obj.getScore("0").setScore(0);

		p.setScoreboard(sb);

	}

	public static void set_win_display(List<gg.litestrike.game.Team> wins) {
		Bukkit.getServer().sendMessage(Component.text("set_win_display was called"));
		int placer_wins_amt = 0;
		int breaker_wins_amt = 0;
		for (gg.litestrike.game.Team w : wins) {
			if (w == gg.litestrike.game.Team.Placer) {
				placer_wins_amt += 1;
			} else {
				breaker_wins_amt += 1;
			}
		}

		Component placer_text = Component.text(render_win_display(placer_wins_amt));
		Component breaker_text = Component.text(render_win_display(breaker_wins_amt));
		Teams t = Litestrike.getInstance().game_controller.teams;

		for (Player p : t.get_placers()) {
			Team breakers = p.getScoreboard().getTeam("wins_breakers");
			Team placers = p.getScoreboard().getTeam("wins_placers");
			placers.prefix(Component.text("> ").decoration(TextDecoration.BOLD, true));

			breakers.suffix(breaker_text);
			placers.suffix(placer_text);
		}

		for (Player p : t.get_breakers()) {
			Team breakers = p.getScoreboard().getTeam("wins_breakers");
			Team placers = p.getScoreboard().getTeam("wins_placers");
			breakers.prefix(Component.text("> ").decoration(TextDecoration.BOLD, true));

			breakers.suffix(breaker_text);
			placers.suffix(placer_text);
		}

	}

	// e.g. 3 -> ◉︎ ◉︎ ◉︎ ○︎ ☆
	public static String render_win_display(int amt) {
		String s = "";
		for (int i = 1; i <= GameController.switch_round; i++) {
			if (i <= amt) {
				s += "⚫";
			} else {
				s += "⚬︎ ";
			}
		}
		if (GameController.switch_round == amt) {
			s += "★ ";
		} else {
			s += "☆ ";
		}

		s += "	(" + amt + ")";
		return s;
	}

	public static void set_player_money(String player, int money) {
		Player p = Bukkit.getPlayer(player);
		Scoreboard sb = p.getScoreboard();
		Team money_count = sb.getTeam("money_count");
		money_count.suffix(Component.text(money));
	}

}
