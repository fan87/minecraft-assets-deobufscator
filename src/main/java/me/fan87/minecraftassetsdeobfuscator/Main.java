package me.fan87.minecraftassetsdeobfuscator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        input("It's for private use only. If anything went wrong (Minecraft Folder not found etc.), you might need to modify the code.\nPress ENTER to Continue");
        print("Scanning Minecraft's Default Assets Index Folder....");
        File minecraftFolder = new File(System.getProperties().getProperty("user.home"), "/.minecraft/");
        print("Minecraft Folder: " + minecraftFolder.getAbsolutePath());
        File assetsFolder = new File(minecraftFolder, "assets/");
        File indexesFolder = new File(assetsFolder, "indexes/");
        File objectsFolder = new File(assetsFolder, "objects/");
        print("Index Folder: " + indexesFolder.getAbsolutePath());
        print("Scanning Indexes Folder....");
        print("================================");
        print("Hint: Launch the version at least once if it's not appearing");
        for (File file : indexesFolder.listFiles((dir, name) -> name.endsWith(".json"))) {
            print("Index found: " + file.getName());
            print("Indexing " + file.getName() + ", please wait...");
            remap(file, objectsFolder);
        }
        print("================================");
        input("Done! Press ENTER to exit!");

        File indexFile = null;
        String indexFileName;
        while (indexFile == null) {
            indexFileName = input("Please enter an index file name (Ex. 1.8.json)\n>> ");
            try {
                indexFile = new File(indexesFolder, indexFileName);
                if (!indexFile.isFile()) throw new NullPointerException();
            } catch (Exception e) {
                print("Invalided Filename!");
                indexFile = null;
            }
        }


    }

    private static void remap(File indexFile, File objectsFolder) throws IOException {
        Gson gson = new Gson();
        JsonObject element = gson.fromJson(new FileReader(indexFile), JsonObject.class);
        JsonObject assets = element.getAsJsonObject("objects");
        File outputFolder = new File("deobfuscsated/" + indexFile.getName().replaceAll(".json", "") + "/");
        for (String assetName : assets.keySet()) {
            JsonObject object = assets.getAsJsonObject(assetName);
            String hash = object.get("hash").getAsString();
            long size = object.get("size").getAsLong();
//            print("Found Asset, Name: " + assetName + "( " + size + " bytes" + ", Assets Hash: " + hash + ")");
            StringBuilder builder = new StringBuilder();
            String[] folders = assetName.split("/");
            for (int i = 0; i < folders.length - 1; i++) {
                builder.append(folders[i]).append("/");
            }
            File outputMappedFolder = new File(outputFolder, builder.toString());
            if (!outputMappedFolder.exists()) outputMappedFolder.mkdirs();
            File outputFile = new File(outputFolder, assetName);
            if (outputFile.exists()) outputFile.delete();
            File inputFolder = new File(objectsFolder, hash.substring(0, 2) + "/");
            File inputFile = new File(inputFolder ,hash);
            if (Files.size(Paths.get(inputFile.toURI())) == size) {
                Files.copy(Paths.get(inputFile.toURI()), Paths.get(outputFile.toURI()));
            } else {
                print("Invalid File! File name: " + assetName + "  Expected File size: " + size + " , Real size: " + Files.size(Paths.get(inputFile.toURI())));
            }
        }
    }

    private static String input() {
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    private static String input(String message) {
        System.out.print(message);
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    private static void print(Object... objects) {
        StringBuilder builder = new StringBuilder();
        for (Object object : objects) {
            builder.append(object);
        }
        System.out.println(builder.toString());
    }
}
