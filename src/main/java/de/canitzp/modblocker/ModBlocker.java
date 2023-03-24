package de.canitzp.modblocker;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.HandshakeMessages;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(ModBlocker.MODID)
public class ModBlocker {
    
    public static final String MODID = "modblocker";
    public static final String MODNAME = "Mod Blocker";
    
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);
    
    public ModBlocker() {
        LOGGER.info(String.format("%s was found and is getting loaded.", MODNAME));
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (s, aBoolean) -> true));
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }
    
    public static boolean transform(HandshakeMessages.S2CModList modListReply){
        for(String clientModID : modListReply.getModList()){
            if(!ModList.get().isLoaded(clientModID)){
                System.out.println(Config.CONFIG.WHITELISTED_MODS.get());
                if(!Config.CONFIG.WHITELISTED_MODS.get().contains(clientModID)){
                    LOGGER.warn(String.format("The connection to a client was terminated because the client has a not allowed mod installed! '%s'", clientModID));
                    return false;
                }
            }
        }
        return true;
    }
    
    public static class Config{
        
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final Config CONFIG = new Config(BUILDER);
        public static final ForgeConfigSpec SPEC = BUILDER.build();
    
        public final ForgeConfigSpec.ConfigValue<List<String>> WHITELISTED_MODS;
        
        public Config(ForgeConfigSpec.Builder builder){
            builder.push("general");
            
            WHITELISTED_MODS = builder
                .comment("Add modids to this list if clients are allowed to use these mods alongside all the others. All non included mods,that aren't on the server side are blocked and the client can't connect!")
                .worldRestart()
                .define("whitelisted_mods", new ArrayList<>());
            
            builder.pop();
        }
    }
    
}