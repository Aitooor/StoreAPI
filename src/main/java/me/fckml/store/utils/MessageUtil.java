package me.fckml.store.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class MessageUtil {

    public static String translate(String line) {
        return ChatColor.translateAlternateColorCodes('&', line);
    }

    public static List<String> translate(List<String> list) {
        return list.stream().map(MessageUtil::translate).collect(Collectors.toList());
    }
}
