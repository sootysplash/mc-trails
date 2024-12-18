package me.sootysplash;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrlClient implements ClientModInitializer {
	MinecraftClient mc = MinecraftClient.getInstance();
	public static long finalPing = 0;
	public static ArrayList<Pair<List<?>,Long>> particleMove = new ArrayList<>();
	public static long biggestNum = 0;
	TrlConfig config = TrlConfig.getInstance();
	public static final Logger LOGGER = LoggerFactory.getLogger("mc-trails");

	@Override
	public void onInitializeClient() {
		LOGGER.info("mc-trails | Sootysplash was here");

		WorldRenderEvents.END.register((tickDelta) -> {
			if(mc == null || mc.getNetworkHandler() == null || mc.player == null) {
				return;
			}

			try {
				if (finalPing != mc.getNetworkHandler().getPlayerListEntry(mc.player.getGameProfile().getName()).getLatency()) {
					if (config.ping) {
						finalPing = mc.getNetworkHandler().getPlayerListEntry(mc.player.getGameProfile().getName()).getLatency();
					} else {
						finalPing = 0;
					}
//					mc.inGameHud.getChatHud().addMessage(Text.of(" " + finalPing));
				}
			}catch (NullPointerException ignored){}

			if(!particleMove.isEmpty() && config.enabled) {
				if (particleMove.get(0).getRight() < System.currentTimeMillis()) {

						List<?> render = particleMove.get(0).getLeft();
						particleMove.remove(0);
						mc.particleManager.addParticle((ParticleEffect) render.get(0), (Double) render.get(1), (Double) render.get(2), (Double) render.get(3), (Double) render.get(4), (Double) render.get(5), (Double) render.get(6));


				}
			}

		});
	}
	// I don't have the brain capacity to code logic right now, I'll rewrite original code soon when I care enough
	// credit: https://www.baeldung.com/java-levenshtein-distance
	static int calculate(String x, String y) {
		int[][] dp = new int[x.length() + 1][y.length() + 1];

		for (int i = 0; i <= x.length(); i++) {
			for (int j = 0; j <= y.length(); j++) {
				if (i == 0) {
					dp[i][j] = j;
				}
				else if (j == 0) {
					dp[i][j] = i;
				}
				else {
					dp[i][j] = min(dp[i - 1][j - 1]
									+ costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
							dp[i - 1][j] + 1,
							dp[i][j - 1] + 1);
				}
			}
		}

		return dp[x.length()][y.length()];
	}
	public static int costOfSubstitution(char a, char b) {
		return a == b ? 0 : 1;
	}

	public static int min(int... numbers) {
		return Arrays.stream(numbers)
				.min().orElse(Integer.MAX_VALUE);
	}
	public static List<ParticleEffect> particles(){
		List<ParticleEffect> particle = new ArrayList<>();
		for(ParticleType<?> pk : Registries.PARTICLE_TYPE){
				try {
						particle.add((ParticleEffect) pk);
				} catch (Exception ignored) {}
		}
		return particle;
	}
	public static List<String> particleString(){
		List<String> particle = new ArrayList<>();
		for(ParticleEffect pk : particles()){
			try {
					particle.add(pk.asString());
			} catch (Exception ignored) {}
		}
		return particle;
	}
}