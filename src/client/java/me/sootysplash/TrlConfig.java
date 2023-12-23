package me.sootysplash;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.autoconfig.ConfigData;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@me.shedaniel.autoconfig.annotation.Config(name = "mc-trails")
public class TrlConfig implements ConfigData {


        //Andy is the goat https://github.com/AndyRusso/pvplegacyutils/blob/main/src/main/java/io/github/andyrusso/pvplegacyutils/PvPLegacyUtilsConfig.java

        private static final Path file = FabricLoader.getInstance().getConfigDir().resolve("mc-trails.json");
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        private static TrlConfig instance;
        public String particle = "minecraft:ash";
        public List<String> userparticlelist = List.of("minecraft:note 0", "minecraft:heart 1", "minecraft:angry_villager 2");
        public boolean random = false;
        public boolean ping = true;
        public boolean enabled = true;
        public int userDelay = 0;

        public void save() {
            TrlConfig config = getInstance();
            TrlClient.biggestNum = 0;
            for(String custom : config.userparticlelist){

                if(!TrlClient.particleString().contains(custom)){
                    int listPos = config.userparticlelist.indexOf(custom);
                    int currentDistance = Integer.MAX_VALUE;
                    int indexOfSmallest = 0;
                    int Levenshtein;
                    for(String particleName : TrlClient.particleString()){
                        Levenshtein = TrlClient.calculate(custom, particleName);
                        if(currentDistance > Levenshtein){
                            currentDistance = Levenshtein;
                            indexOfSmallest = TrlClient.particleString().indexOf(particleName);
                        }
                    }
                    String number = custom;
                    custom = TrlClient.particleString().get(indexOfSmallest);
                        Pattern pattern = Pattern.compile("[0123456789]+", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(number);
                        String peskyNum;
                        if (matcher.find()) {
                            peskyNum = number.substring(matcher.start(), matcher.end());
                        } else {
                            peskyNum = "0";
                        }
                    custom = custom.concat(" " + peskyNum);
                    config.userparticlelist.set(listPos, custom);
                }
            }
            Comparator<String> comp = Comparator.comparing(this::peskyNumber);
            config.userparticlelist.sort(comp);

            try {
                Files.writeString(file, GSON.toJson(this));
            } catch (IOException e) {
                TrlClient.LOGGER.error("mc-trails could not save the config.");
                throw new RuntimeException(e);
            }
        }
        public int peskyNumber(String str){
            Pattern pattern = Pattern.compile("[0123456789]+", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(str);
            if(matcher.find()) {
                return Integer.parseInt(str.substring(matcher.start(), matcher.end()));
            }else{
                return 0;
            }
        }

        public static TrlConfig getInstance() {
            if (instance == null) {
                try {
                    instance = GSON.fromJson(Files.readString(file), TrlConfig.class);
                } catch (IOException exception) {
                    TrlClient.LOGGER.warn("mc-trails couldn't load the config, using defaults.");
                    instance = new TrlConfig();
                }
            }

            return instance;
        }


}
