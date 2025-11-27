/*
 * File updated ~ 27 - 2 - 2023 ~ Leaf
 */

package leaf.cosmere.common.commands.subcommands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import leaf.cosmere.api.Constants;
import leaf.cosmere.api.cosmerePower.CosmerePower;
import leaf.cosmere.api.cosmerePower.CosmerePowerInstance;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.text.TextHelper;
import leaf.cosmere.common.cap.entity.SpiritwebCapability;
import leaf.cosmere.common.commands.arguments.ManifestationsArgumentType;
import leaf.cosmere.common.eventHandlers.EntityEventHandler;
import leaf.cosmere.common.registry.ManifestationRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class PowerCommand extends ModCommand
{

	@Override
	public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		return SINGLE_SUCCESS;
	}

	private static int check(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Collection<ServerPlayer> players = getPlayers(context, 3);

		for (ServerPlayer player : players)
		{
			ReportPowersFoundOnPlayer(context, player);
		}

		return SINGLE_SUCCESS;
	}

	public static void ReportPowersFoundOnPlayer(CommandContext<CommandSourceStack> context, ServerPlayer player)
	{
		SpiritwebCapability.get(player).ifPresent(spiritweb ->
		{
			CommandSourceStack source = context.getSource();

			MutableComponent powersFound = Component.translatable(Constants.Strings.POWERS_FOUND, TextHelper.getPlayerTextObject(player.serverLevel(), player.getUUID()));

			final MutableComponent leftBracketTextComponent = Component.literal("[");
			final MutableComponent rightBracketTextComponent = Component.literal("]");
			final MutableComponent space = Component.literal(" ");

			//figure out which manifestations a player has
			for (Manifestation manifestation : spiritweb.getAvailableManifestations())
			{
				powersFound.append(leftBracketTextComponent);
				final double baseStrength = manifestation.getStrength(spiritweb, true);
				final double totalStrength = manifestation.getStrength(spiritweb, false);
				powersFound.append(TextHelper.createTextWithTooltip(
						(MutableComponent) manifestation.getTextComponent(),
						Component.translatable(Constants.Strings.POWER_STRENGTH, baseStrength, totalStrength)));
				powersFound.append(rightBracketTextComponent);
				powersFound.append(space);
			}
			source.sendSuccess(() -> powersFound, true);
		});
	}

	private static int clear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Collection<ServerPlayer> players = getPlayers(context, 3);

		for (ServerPlayer player : players)
		{
			SpiritwebCapability.get(player).ifPresent(iSpiritweb ->
			{
				CommandSourceStack source = context.getSource();
				iSpiritweb.clearCosmerePowers();
				iSpiritweb.syncToClients(null);
				MutableComponent playerTextObject = TextHelper.getPlayerTextObject(context.getSource().getLevel(), player.getUUID());
				source.sendSuccess(() -> Component.translatable(Constants.Strings.POWER_SET_SUCCESS, playerTextObject), false);
			});
		}

		return SINGLE_SUCCESS;
	}

	private static int reroll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Collection<ServerPlayer> players = getPlayers(context, 3);

		for (ServerPlayer player : players)
		{
			SpiritwebCapability.get(player).ifPresent(iSpiritweb ->
			{
				CommandSourceStack source = context.getSource();
				iSpiritweb.clearCosmerePowers();
				EntityEventHandler.giveEntityStartingManifestation(player, (SpiritwebCapability) iSpiritweb);
				//set to none so that it auto updates to the new available ones on sync
				iSpiritweb.setSelectedManifestation(ManifestationRegistry.NONE.get());
				iSpiritweb.syncToClients(null);
				MutableComponent playerTextObject = TextHelper.getPlayerTextObject(player.serverLevel(), player.getUUID());
				source.sendSuccess(() -> Component.translatable(Constants.Strings.POWER_SET_SUCCESS, playerTextObject), false);
				ReportPowersFoundOnPlayer(context, player);
			});
		}


		return SINGLE_SUCCESS;
	}


	private static int give(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Collection<ServerPlayer> players = getPlayers(context, 4);

		for (ServerPlayer player : players)
		{
			CommandSourceStack source = context.getSource();
			CosmerePower cosmerePower = context.getArgument("cosmerePower", CosmerePower.class);

			MutableComponent playerText = TextHelper.getPlayerTextObject(player.serverLevel(), player.getUUID());

			MutableComponent cosmerePowerText = (MutableComponent) cosmerePower.getTextComponent();

			SpiritwebCapability.get(player).ifPresent((spiritweb) ->
			{
				//todo change this so that the user sets the strength in the command
				CosmerePowerInstance cosmerePowerInstance = new CosmerePowerInstance(
						cosmerePower,
						player.getUUID(),
						9,
						false
				);
				spiritweb.giveCosmerePower(cosmerePowerInstance);
				source.sendSuccess(() -> Component.translatable(Constants.Strings.POWER_SET_SUCCESS, playerText, cosmerePowerText), false);
				ReportPowersFoundOnPlayer(context, player);
				spiritweb.syncToClients(null);
			});
		}
		return SINGLE_SUCCESS;
	}

	private static int remove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Collection<ServerPlayer> players = getPlayers(context, 4);

		for (ServerPlayer player : players)
		{
			CommandSourceStack source = context.getSource();
			CosmerePower cosmerePower = context.getArgument("cosmerePower", CosmerePower.class);

			MutableComponent playerText = TextHelper.getPlayerTextObject(source.getLevel(), player.getUUID());

			MutableComponent cosmerePowerText = (MutableComponent) cosmerePower.getTextComponent();

			SpiritwebCapability.get(player).ifPresent((spiritweb) ->
			{
				spiritweb.removeCosmerePower(cosmerePower);
				spiritweb.syncToClients(null);
				source.sendSuccess(() -> Component.translatable(Constants.Strings.POWER_SET_SUCCESS, playerText, cosmerePowerText), false);
				ReportPowersFoundOnPlayer(context, player);
			});
		}
		return SINGLE_SUCCESS;
	}

	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		return Commands.literal("powers")
				.then(Commands.literal("check")
						.executes(PowerCommand::check)
						.then(Commands.argument("target", EntityArgument.players())
								.requires(context -> context.hasPermission(2))
								.executes(PowerCommand::check)))
				.then(Commands.literal("clear")
						.requires(context -> context.hasPermission(2))
						.executes(PowerCommand::clear)
						.then(Commands.argument("target", EntityArgument.players())
								.executes(PowerCommand::clear)))
				.then(Commands.literal("reroll")
						.requires(context -> context.hasPermission(2))
						.executes(PowerCommand::reroll)
						.then(Commands.argument("target", EntityArgument.players())
								.executes(PowerCommand::reroll)))
				.then(Commands.literal("give")
						.requires(context -> context.hasPermission(2))
						.then(Commands.argument("manifestation", ManifestationsArgumentType.createArgument())
								.executes(PowerCommand::give)
								.then(Commands.argument("target", EntityArgument.players())
										.executes(PowerCommand::give))))
				.then(Commands.literal("remove")
						.requires(context -> context.hasPermission(2))
						.then(Commands.argument("manifestation", ManifestationsArgumentType.createArgument())
								.executes(PowerCommand::remove)
								.then(Commands.argument("target", EntityArgument.players())
										.executes(PowerCommand::remove))))
				; // end add
	}
}