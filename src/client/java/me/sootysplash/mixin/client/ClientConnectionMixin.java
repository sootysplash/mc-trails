/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.sootysplash.mixin.client;

import me.sootysplash.TrlClient;
import me.sootysplash.TrlConfig;
import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin implements ChannelInfoHolder {

    @Unique
    Vec3d pos;
    @Unique
    long listPos = 0;

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At(value = "HEAD", remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
    private void sendInternal(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {

        if(packet instanceof PlayerMoveC2SPacket move && move.changesPosition()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null && mc.player != null && mc.world != null && mc.getNetworkHandler() != null) {

                if (pos == null || !pos.equals(new Vec3d(move.getX(mc.player.getX()), move.getY(mc.player.getY()), move.getZ(mc.player.getZ())))) {

                    pos = new Vec3d(move.getX(mc.player.getX()), move.getY(mc.player.getY()), move.getZ(mc.player.getZ()));

                    int index = 0;
                    TrlConfig config = TrlConfig.getInstance();

                    if (config.random) {
                        ParticleEffect pk = ParticleTypes.ELDER_GUARDIAN;
                        while (pk == ParticleTypes.ELDER_GUARDIAN || pk == ParticleTypes.EXPLOSION || pk == ParticleTypes.EXPLOSION_EMITTER || pk == ParticleTypes.FLASH || pk == ParticleTypes.SONIC_BOOM || pk == ParticleTypes.SWEEP_ATTACK){
                            index = Math.toIntExact(Math.round(Math.random() * TrlClient.particles().size()));
                        if (index >= TrlClient.particles().size()) {
                            index--;
                        }
                            pk = TrlClient.particles().get(index);
                    }
                        TrlClient.particleMove.add(new Pair<>(List.of(TrlClient.particles().get(index), pos.x, pos.y, pos.z, (Math.random() - 0.5) / 2, (Math.random() - 0.5) / 2, (Math.random() - 0.5) / 2), System.currentTimeMillis() + TrlClient.finalPing + config.userDelay));
                    }else {
                        for (String particleInt : config.userparticlelist) {

                            if (peskyNumber(particleInt) > TrlClient.biggestNum) {
                                TrlClient.biggestNum = peskyNumber(particleInt);
                            }
                            if (peskyNumber(particleInt) == listPos) {
                                Pattern pattern = Pattern.compile("[0123456789]+", Pattern.CASE_INSENSITIVE);
                                Matcher matcher = pattern.matcher(particleInt);
                                if (matcher.find()) {
                                    particleInt = particleInt.replace(matcher.group(), "").replace(" ", "");
                                }
                                try {
                                    index = TrlClient.particleString().indexOf(particleInt);

                                    TrlClient.particleMove.add(new Pair<>(List.of(TrlClient.particles().get(index), pos.x, pos.y, pos.z, 0.0, 0.1, 0.0), System.currentTimeMillis() + TrlClient.finalPing + config.userDelay));
                                }catch(IndexOutOfBoundsException ignored){}
                            }
                        }
                        listPos++;
                        if (listPos > TrlClient.biggestNum) {
                            listPos = 0;
                        }
                    }
                }
            }
        }
    }
    @Unique
    public int peskyNumber(String str){
        Pattern pattern = Pattern.compile("[0123456789]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()) {
            return Integer.parseInt(str.substring(matcher.start(), matcher.end()));
        }else{
            return 0;
        }
    }

}