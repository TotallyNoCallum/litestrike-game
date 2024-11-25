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

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import static net.kyori.adventure.text.Component.text;

public class ScoreboardController {
	public static void setup_scoreboard(Teams t, int game_id) {
		for (Player p : t.get_placers()) {
			give_player_scoreboard(p, gg.litestrike.game.Team.Placer, t, game_id);
		}
		for (Player p : t.get_breakers()) {
			give_player_scoreboard(p, gg.litestrike.game.Team.Breaker, t, game_id);
		}
	}

	public static void give_player_scoreboard(Player p, gg.litestrike.game.Team t, Teams teams, int game_id) {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

		Team placers = sb.registerNewTeam("placers");
		placers.color(NamedTextColor.RED);
		placers.setAllowFriendlyFire(false);
		placers.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
		for (Player player : teams.get_placers()) {
			placers.addPlayer(player);
		}

		Team breakers = sb.registerNewTeam("breakers");
		breakers.color(NamedTextColor.GREEN);
		breakers.setAllowFriendlyFire(false);
		breakers.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
		for (Player player : teams.get_breakers()) {
			breakers.addPlayer(player);
		}

		Component title = text("LITESTRIKE").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true);
		Objective obj = sb.registerNewObjective("main", Criteria.DUMMY, title);

		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		obj.getScore("9").setScore(9);
		obj.getScore("9").customName(text("").color(NamedTextColor.GREEN));

		if (t == gg.litestrike.game.Team.Breaker) {
			obj.getScore("8").customName(text("ᴛᴇᴀᴍ: ").append(Litestrike.BREAKER_TEXT));
			obj.getScore("8").setScore(8);
		} else {
			obj.getScore("8").customName(text("ᴛᴇᴀᴍ: ").append(Litestrike.PLACER_TEXT));
			obj.getScore("8").setScore(8);
		}
		obj.getScore("7").setScore(7);
		obj.getScore("7").customName(text("").color(NamedTextColor.RED));

		obj.getScore("6").setScore(6);
		obj.getScore("6").customName(text("").color(NamedTextColor.DARK_RED));

		obj.getScore("5").setScore(5);
		obj.getScore("5").customName(text("").color(NamedTextColor.DARK_BLUE));

		obj.getScore("4").setScore(4);
		obj.getScore("4").customName(text("ꜱᴛᴀʏ ᴡɪᴛʜ ʏᴏᴜʀ ᴛᴇᴀᴍ!").color(TextColor.color(0xe64cce)));

		obj.getScore("3").setScore(3);
		obj.getScore("3").customName(text("").color(NamedTextColor.AQUA));

		obj.getScore("2").setScore(2);
		obj.getScore("2").customName(text("").color(NamedTextColor.DARK_GREEN));

		obj.getScore("1").setScore(1);
		obj.getScore("1").customName(text("").color(NamedTextColor.DARK_PURPLE));

		obj.getScore("0").setScore(0);
		obj.getScore("0").customName(text("ᴄʀʏꜱᴛᴀʟɪᴢᴇᴅ.ᴄᴄ ").color(TextColor.color(0xc4b50a))
				.append(text("" + game_id).color(NamedTextColor.GRAY)));

		Team money_count = sb.registerNewTeam("money_count");
		money_count.addEntry("7");
		money_count.prefix(text("ᴍᴏɴᴇʏ: "));
		money_count.suffix(text("error"));
		obj.getScore("7").setScore(7);

		Team wins_placers = sb.registerNewTeam("wins_placers");
		wins_placers.addEntry("3");
		wins_placers.prefix(text("   "));
		wins_placers.suffix(text("\uE105 \uE105 \uE105 \uE105 \uE107 (0)"));
		obj.getScore("3").setScore(3);

		Team wins_breakers = sb.registerNewTeam("wins_breakers");
		wins_breakers.addEntry("2");
		wins_breakers.prefix(text("   "));
		wins_breakers.suffix(text("\uE105 \uE105 \uE105 \uE105 \uE107 (0)"));
		obj.getScore("2").setScore(2);

		p.setScoreboard(sb);
	}

	public static void set_win_display(List<gg.litestrike.game.Team> wins) {
		int placer_wins_amt = 0;
		int breaker_wins_amt = 0;
		for (gg.litestrike.game.Team w : wins) {
			if (w == gg.litestrike.game.Team.Placer) {
				placer_wins_amt += 1;
			} else {
				breaker_wins_amt += 1;
			}
		}

		Component placer_text = text(render_win_display(placer_wins_amt));
		Component breaker_text = text(render_win_display(breaker_wins_amt));
		Teams t = Litestrike.getInstance().game_controller.teams;

		for (Player p : t.get_placers()) {
			Team breakers = p.getScoreboard().getTeam("wins_breakers");
			Team placers = p.getScoreboard().getTeam("wins_placers");
			if (breakers == null || placers == null) {
				continue;
			}
			placers.prefix(text("\uE109 ").decoration(TextDecoration.BOLD, true));

			breakers.suffix(breaker_text);
			placers.suffix(placer_text);
		}

		for (Player p : t.get_breakers()) {
			Team breakers = p.getScoreboard().getTeam("wins_breakers");
			Team placers = p.getScoreboard().getTeam("wins_placers");
			if (breakers == null || placers == null) {
				continue;
			}
			breakers.prefix(text("\uE109 ").decoration(TextDecoration.BOLD, true));

			breakers.suffix(breaker_text);
			placers.suffix(placer_text);
		}
	}

	private static String render_win_display(int amt) {
		String s = "";
		for (int i = 1; i <= GameController.SWITCH_ROUND; i++) {
			if (i <= amt) {
				s += "\uE106";
			} else {
				s += "\uE105";
			}
		}
		if (GameController.SWITCH_ROUND + 1 == amt) {
			s += "\uE108";
		} else {
			s += "\uE107";
		}

		s += "  (" + amt + ")";
		return s;
	}

	public static void set_player_money(String player, int money) {
		Player p = Bukkit.getPlayer(player);
		if (p == null) {
			return;
		}
		Scoreboard sb = p.getScoreboard();
		Team money_count = sb.getTeam("money_count");
		if (money_count == null) {
			return;
		}
		money_count.suffix(text(money).color(TextColor.color(0x0ab1c4)).append(text("\uE104")));
	}

}
