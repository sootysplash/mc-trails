package me.sootysplash;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.util.List;

public class TrlModMenu implements ModMenuApi {

        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return parent -> {
                TrlConfig config = TrlConfig.getInstance();

                ConfigBuilder builder = ConfigBuilder.create()
                        .setParentScreen(parent)
                        .setTitle(Text.of("Config"))
                        .setSavingRunnable(config::save);

                ConfigCategory handle = builder.getOrCreateCategory(Text.of("Handling"));
                ConfigEntryBuilder cfgHandle =  builder.entryBuilder();

                handle.addEntry(cfgHandle.startBooleanToggle(Text.of("Enabled"), config.enabled)
                        .setDefaultValue(true)
                        .setSaveConsumer(newValue -> config.enabled = newValue)
                        .setTooltip(Text.of("Render a trail?"))
                        .build());

                handle.addEntry(cfgHandle.startBooleanToggle(Text.of("Random"), config.random)
                        .setDefaultValue(false)
                        .setSaveConsumer(newValue -> config.random = newValue)
                        .setTooltip(Text.of("Create a trail with a completely random particle choice"))
                        .build());

                handle.addEntry(cfgHandle.startBooleanToggle(Text.of("Delay based off of ping"), config.ping)
                        .setDefaultValue(true)
                        .setSaveConsumer(newValue -> config.ping = newValue)
                        .setTooltip(Text.of("Ping is fetched from player list data"))
                        .build());

                handle.addEntry(cfgHandle.startIntField(Text.of("Custom delay (milliseconds)"), config.userDelay)
                        .setDefaultValue(0)
                        .setSaveConsumer(newValue -> config.userDelay = newValue)
                        .setTooltip(Text.of("If you wish to have a custom delay for when particles spawn, set to 0 to disable"))
                        .build());

                handle.addEntry(cfgHandle.startStringDropdownMenu(Text.of("Particle list:"), config.particle)
                        .setDefaultValue("minecraft:ash")
                        .setSelections(TrlClient.particleString())
                        .setTooltip(Text.of("A list of all particles in minecraft, fetched from the particle Registry"))
                        .setSuggestionMode(false)
                        .setSaveConsumer(newValue -> config.particle = newValue)
                        .build());

                handle.addEntry(cfgHandle.startStrList(Text.of("Custom trail"), config.userparticlelist)
                        .setInsertButtonEnabled(true)
                        .setExpanded(true)
                        .setDeleteButtonEnabled(true)
                        .setDefaultValue(List.of("minecraft:note 0", "minecraft:heart 1", "minecraft:angry_villager 2"))
                        .setTooltip(Text.of("Create custom trails through this setting! \n The syntax is: <particle name (a list is above)> <the order particles will be spawned in> \n Particle names will be autocorrected when you save"))
                        .setSaveConsumer(newValue -> config.userparticlelist = newValue)
                        .build());


                return builder.build();
            };
        }

    }
