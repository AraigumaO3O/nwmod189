package com.tumod.networthmod.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommandNetworth extends CommandBase {

    @Override
    public String getCommandName() {
        return "nw";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/nw <nickname>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText("§cUso correcto: /nw <nickname>"));
            return;
        }

        String nickname = args[0];
        sender.addChatMessage(new ChatComponentText("§eBuscando networth para: " + nickname + "..."));

        try {
            String urlStr = "https://sky.shiiyu.moe/api/v1/profile/" + nickname;
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            if (status != 200) {
                sender.addChatMessage(new ChatComponentText("§cNo se encontró información para el jugador: " + nickname));
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JsonObject json = new JsonParser().parse(content.toString()).getAsJsonObject();

            double networthSoulbound = 0;
            double networthUnsoulbound = 0;
            String discord = "No tiene Discord público";

            if (json.has("networth")) {
                JsonObject networth = json.getAsJsonObject("networth");
                if (networth.has("soulbound")) {
                    networthSoulbound = networth.get("soulbound").getAsDouble();
                }
                if (networth.has("unsoulbound")) {
                    networthUnsoulbound = networth.get("unsoulbound").getAsDouble();
                }
            }

            if (json.has("socialMedia") && json.getAsJsonObject("socialMedia").has("DISCORD")) {
                discord = json.getAsJsonObject("socialMedia").get("DISCORD").getAsString();
            }

            sender.addChatMessage(new ChatComponentText("§aNetworth Soulbound: §6" + String.format("%.2f", networthSoulbound) + " coins"));
            sender.addChatMessage(new ChatComponentText("§aNetworth Unsoulbound: §6" + String.format("%.2f", networthUnsoulbound) + " coins"));
            sender.addChatMessage(new ChatComponentText("§bDiscord: §f" + discord));

        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText("§cError al obtener datos: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
