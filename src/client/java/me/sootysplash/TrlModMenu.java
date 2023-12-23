package me.sootysplash;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

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

                handle.addEntry(cfgHandle.startBooleanToggle(Text.of("Random"), config.random)
                        .setDefaultValue(false)
                        .setSaveConsumer(newValue -> config.random = newValue)
                        .build());

                handle.addEntry(cfgHandle.startBooleanToggle(Text.of("Delay based off of ping"), config.ping)
                        .setDefaultValue(true)
                        .setSaveConsumer(newValue -> config.ping = newValue)
                        .build());

                handle.addEntry(cfgHandle.startIntField(Text.of("Custom delay (milliseconds)"), config.userDelay)
                        .setDefaultValue(0)
                        .setSaveConsumer(newValue -> config.userDelay = newValue)
                        .build());

                handle.addEntry(cfgHandle.startStringDropdownMenu(Text.of("Particle list:"), config.particle)
                        .setDefaultValue("minecraft:heart")
                        .setSelections(TrlClient.particleString())
                        .setSuggestionMode(false)
                        .setSaveConsumer(newValue -> config.particle = newValue)
                        .build());

                handle.addEntry(cfgHandle.startStrList(Text.of("Particle list"), config.userparticlelist)
                        .setInsertButtonEnabled(true)
                        .setExpanded(true)
                        .setDeleteButtonEnabled(true)
                        .setSaveConsumer(newValue -> config.userparticlelist = newValue)
                        .build());


                return builder.build();
            };
        }

    }
