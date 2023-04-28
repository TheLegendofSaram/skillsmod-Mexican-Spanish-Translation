package net.puffish.skillsmod.experience.builtin;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.puffish.skillsmod.SkillsMod;
import net.puffish.skillsmod.SkillsAPI;
import net.puffish.skillsmod.experience.ExperienceSource;
import net.puffish.skillsmod.experience.calculation.CalculationManager;
import net.puffish.skillsmod.experience.calculation.condition.BlockCondition;
import net.puffish.skillsmod.experience.calculation.condition.BlockTagCondition;
import net.puffish.skillsmod.experience.calculation.condition.ConditionFactory;
import net.puffish.skillsmod.experience.calculation.parameter.EffectParameter;
import net.puffish.skillsmod.experience.calculation.parameter.ParameterFactory;
import net.puffish.skillsmod.json.JsonElementWrapper;
import net.puffish.skillsmod.json.JsonObjectWrapper;
import net.puffish.skillsmod.utils.Result;
import net.puffish.skillsmod.utils.error.Error;

import java.util.Map;

public class MineBlockExperienceSource implements ExperienceSource {
	public static final Identifier ID = SkillsMod.createIdentifier("mine_block");

	private static final Map<String, ConditionFactory<Context>> CONDITIONS = Map.ofEntries(
			Map.entry("block", ConditionFactory.map(BlockCondition::parse, Context::blockState)),
			Map.entry("block_tag", ConditionFactory.map(BlockTagCondition::parse, Context::blockState))
	);

	private static final Map<String, ParameterFactory<Context>> PARAMETERS = Map.ofEntries(
			Map.entry("player_effect", ParameterFactory.map(EffectParameter::parse, Context::player))
	);

	private final CalculationManager<Context> manager;

	private MineBlockExperienceSource(CalculationManager<Context> calculated) {
		this.manager = calculated;
	}

	public static void register() {
		SkillsAPI.registerExperienceSource(
				ID,
				maybeDataElement -> maybeDataElement
						.andThen(JsonElementWrapper::getAsObject)
						.andThen(MineBlockExperienceSource::create)
		);
	}

	private static Result<MineBlockExperienceSource, Error> create(JsonObjectWrapper rootObject) {
		return CalculationManager.create(rootObject, CONDITIONS, PARAMETERS).mapSuccess(MineBlockExperienceSource::new);
	}

	private record Context(ServerPlayerEntity player, BlockState blockState) {

	}

	public int getValue(ServerPlayerEntity player, BlockState blockState) {
		return manager.getValue(new Context(player, blockState));
	}

	@Override
	public void dispose(MinecraftServer server) {

	}
}
