package com.user114514.encryptor.extend_pack;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppPathManager {
    public static final AppPathManager pmgr = new AppPathManager("USER114514-JCrying");
    private final String appName;
    private final String userHome;
    private final String os;

    public AppPathManager(String appName) {
        this.appName = appName;
        this.userHome = System.getProperty("user.home");
        this.os = System.getProperty("os.name").toLowerCase();
    }

    public String getUserConfig() {
        Path p;
        if(os.contains("win")){
            String appData = System.getenv("APPDATA");
            p = Paths.get(appData,appName);
        }else if(os.contains("mac")){
            p = Paths.get(userHome,"Library","Application Support",appName);
        }else{
            String xdgConf = System.getenv("XDG_CONFIG_HOME");
            if(xdgConf==null||xdgConf.isBlank())xdgConf=Paths.get(userHome,".config").toString();
            p = Paths.get(xdgConf,appName);
        }
        return p.toString();
    }

    public String getUserCache() {
        Path p;
        if(os.contains("win")){
            String local = System.getenv("LOCALAPPDATA");
            p = Paths.get(local,appName,"cache");
        }else if(os.contains("mac")){
            p = Paths.get(userHome,"Library","Caches",appName);
        }else{
            String xdgCache = System.getenv("XDG_CACHE_HOME");
            if(xdgCache==null||xdgCache.isBlank())xdgCache=Paths.get(userHome,".cache").toString();
            p = Paths.get(xdgCache,appName);
        }
        return p.toString();
    }

    public String getGlobalDir() {
        Path p;
        if(os.contains("win")){
            String progData = System.getenv("PROGRAMDATA");
            p = Paths.get(progData,appName);
        }else if(os.contains("mac")){
            p = Paths.get("/Library","Application Support",appName);
        }else{
            p = Paths.get("/usr/share",appName);
        }
        return p.toString();
    }

    public String getExtDir(){
        return Paths.get(getUserConfig(),"extensions").toString();
    }

    public String getDownloadTemp(){
        return Paths.get(getUserCache(),"download_temp").toString();
    }

    public void mkdir(String path) throws Exception{
        Path p = Paths.get(path);
        if(!Files.exists(p))Files.createDirectories(p);
    }
}