package me.AstramG.MoreMobHeads;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;
 

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
 
/**
 	ImgMessage resource created by bobacadodl! Credit for this class goes to him even though I made
 multiple modifications.
 */
public class ImgMessage {
    @SuppressWarnings("serial")
	public static HashMap<Color, ChatColor> colorMap = new HashMap<Color, ChatColor>() {{
        put(new Color(0, 0, 0), ChatColor.BLACK);
        put(new Color(0, 0, 170), ChatColor.DARK_BLUE);
        put(new Color(0, 170, 0), ChatColor.DARK_GREEN);
        put(new Color(0, 170, 170), ChatColor.DARK_AQUA);
        put(new Color(170, 0, 0), ChatColor.DARK_RED);
        put(new Color(170, 0, 170), ChatColor.DARK_PURPLE);
        put(new Color(255, 170, 0), ChatColor.GOLD);
        put(new Color(170, 170, 170), ChatColor.GRAY);
        put(new Color(85, 85, 85), ChatColor.DARK_GRAY);
        put(new Color(85, 85, 255), ChatColor.BLUE);
        put(new Color(85, 255, 85), ChatColor.GREEN);
        put(new Color(85, 255, 255), ChatColor.AQUA);
        put(new Color(255, 85, 85), ChatColor.RED);
        put(new Color(255, 85, 255), ChatColor.LIGHT_PURPLE);
        put(new Color(255, 255, 85), ChatColor.YELLOW);
        put(new Color(255, 255, 255), ChatColor.WHITE);
    }};
 
    public static ChatColor[][] toChatColorArray(BufferedImage image, int height) {
        double ratio = (double) image.getHeight() / image.getWidth();
        int width = (int) (height / ratio);
        if (width > 10) width = 10;
        BufferedImage resized = resizeImage(image, (int) (height / ratio), height);
 
        ChatColor[][] chatImg = new ChatColor[resized.getWidth()][resized.getHeight()];
        for (int x = 0; x < resized.getWidth(); x++) {
            for (int y = 0; y < resized.getHeight(); y++) {
                int rgb = resized.getRGB(x, y);
                ChatColor closest = getClosestChatColor(new Color(rgb));
                chatImg[x][y] = closest;
            }
        }
        return chatImg;
    }
 
    public static String[] toImgMessage(ChatColor[][] colors, char imgchar) {
        String[] lines = new String[colors[0].length];
        for (int y = 0; y < colors[0].length; y++) {
            String line = "";
            for (int x = 0; x < colors.length; x++) {
                line += colors[x][y].toString()+imgchar;
            }
            lines[y] = line + ChatColor.RESET;
        }
        return lines;
    }
 
    public static String[] appendTextToImg(String[] chatImg, String... text) {
        for (int y = 0; y < chatImg.length; y++) {
            if(text.length>y){
                chatImg[y] = chatImg[y] + " " + text[y];
            }
        }
        return chatImg;
    }
 
    public static String[] appendCenteredTextToImg(String[] chatImg, String... text) {
        for(int y=0; y < chatImg.length; y++) {
            if(text.length>y){
                int len = ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH-chatImg[y].length();
                chatImg[y] = chatImg[y]+center(text[y],len);
            }
            else{
                return chatImg;
            }
        }
        return chatImg;
    }
 
    public static String center(String s, int length) {
        if (s.length() > length) {
            return s.substring(0, length);
        } else if (s.length() == length) {
            return s;
        } else {
            int leftPadding = (length - s.length()) / 2;
            StringBuilder leftBuilder = new StringBuilder();
            for (int i = 0; i < leftPadding; i++) {
                leftBuilder.append(" ");
            }
            return leftBuilder.toString() + s;
        }
    }
 
    public static void imgMessage(Player player, BufferedImage image, int height, char imgchar, String... text){
        imgMessage(player,image,height,imgchar,true,text);
    }
 
    public static void imgMessage(Player player, BufferedImage image, int height, char imgchar, boolean centered, String... text) {
        ChatColor[][] colors = toChatColorArray(image, height);
        String[] lines = toImgMessage(colors, imgchar);
        if(centered){
            lines = appendCenteredTextToImg(lines,text);
        }
        else{
            lines = appendTextToImg(lines, text);
        }
        for (String line : lines) {
            player.sendMessage(line);
        }
    }
 
    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, 6);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return resizedImage;
    }
 
    private static ChatColor getClosestChatColor(Color c){
        Integer r = c.getRed();
        Integer g = c.getGreen();
        Integer b = c.getBlue();
 
        float[] hsb = new float[3];
        Color.RGBtoHSB(r, g, b, hsb);
 
        float h = hsb[0]; // HUE
        float s = hsb[1]; // SATURATION
        float v = hsb[2]; // BRIGHTNESS
 
        if(s > 0.4 && v > 0.2 && h < 0.03333333333){
            return ChatColor.RED;
        }else if(s > 0.6 && v > 0.7 && h > 0.0333333333 && h < 0.1138888888){ // s > 0.4 && v > 0.5
            return ChatColor.GOLD;
        }else if(s > 0.4 && v > 0.14 && h > 0.019 && h < 0.15){ // v < 0.5 // s < 0.801 // v > 0.2
            return ChatColor.DARK_RED;
        }else if(s > 0.6 && v > 0.09 && h > 0.019 && h < 0.15){ // v < 0.5 // s < 0.801 // v > 0.2
            return ChatColor.DARK_RED;
        }else if(s > 0.3 && v > 0.5 && h > 0.02 && h < 0.15){ // v < 0.5 // s < 0.801 // v > 0.2
            return ChatColor.DARK_RED;
        }else if(s < 0.41 && v < 0.2 && h > 0.01 && h < 0.15){ // v < 0.5 // s < 0.801 // v > 0.2
            return ChatColor.BLACK;
        }else if(s > 0.4 && v < 0.35 && v > 0.2 && h > 0.969){
            return ChatColor.DARK_RED;
        }else if(s > 0.4 && v < 0.2 && v > 0.1 && h > 0.079999999 && h < 0.1222222){
            return ChatColor.DARK_RED;
        }else if(s > 0.8 && v < 0.15 && v > 0.05 && h > 0.079999999 && h < 0.1222222){
            return ChatColor.DARK_RED;
        }else if(s > 0.4 && v > 0.5 && h > 0.1138888888 && h < 0.1916666666){
            return ChatColor.YELLOW;
        }else if(s > 0.4 && v < 0.51 && v > 0.1 && h > 0.1138888888 && h < 0.1916666666){ // new
            return ChatColor.DARK_RED;
        }else if(s > 0.4 && v > 0.2 && v < 0.81 && h > 0.1916666666 && h < 0.3805555555){
            return ChatColor.DARK_GREEN;
        }else if(s > 0.4 && v > 0.5 && h > 0.1916666666 && h < 0.3805555555){
            return ChatColor.GREEN;
        }else if(s > 0.2 && v > 0.75 && h > 0.1916666666 && h < 0.3805555555){
            return ChatColor.GREEN;
        }else if(s > 0.2 && v > 0.8 && h > 0.3805555555 && h < 0.5194444444){ // v > 0.4 adjusted 3
            return ChatColor.BLUE;
        }else if(s > 0.1 && s < 0.21 && v > 0.9 && h > 0.3805555555 && h < 0.5194444444){ // new 3
            return ChatColor.BLUE;
        }else if(s > 0.4 && v < 0.81 && v > 0.2 && h > 0.3805555555 && h < 0.6027777777){ // adjusted 3
            return ChatColor.AQUA;
        }else if(s > 0.4 && v > 0.2 && h > 0.5194444444 && h < 0.6027777777){
            return ChatColor.AQUA;
        }else if(s > 0.4 && v > 0.4 && h > 0.6027777777 && h < 0.6944444444){
            return ChatColor.DARK_BLUE;
        }else if(s > 0.2 && s < 0.41 && v > 0.7 && h > 0.6027777777 && h < 0.6944444444){ // adjusted 3
            return ChatColor.BLUE;
        }else if(s > 0.114 && s < 0.2 && v > 0.6 && h > 0.6027777777 && h < 0.6944444444){ // new 3
            return ChatColor.DARK_BLUE;
        }else if(s > 0.1 && s < 0.2 && v > 0.6 && v < 0.91 && h > 0.6027777777 && h < 0.6944444444){ // new 3
            return ChatColor.BLUE;
        }else if(s > 0.114 && s < 0.2 && v > 0.9 && h > 0.6027777777 && h < 0.6944444444){ // new 3
            return ChatColor.DARK_BLUE;
        }else if(s > 0.6 && v > 0.1 && h > 0.6027777777 && h < 0.6944444444){
            return ChatColor.DARK_BLUE;
        }else if(s > 0.4 && v > 0.3 && h > 0.6944444444 && h < 0.8305555555){
            return ChatColor.DARK_PURPLE;
        }else if(s > 0.4 && v > 0.4 && h > 0.8305555555 && h < 0.8777777777){
            return ChatColor.LIGHT_PURPLE;
        }else if(s > 0.3 && v > 0.4 && h > 0.8777777777 && h < 0.9611111111){
            return ChatColor.LIGHT_PURPLE;
        }else if(s > 0.4 && v > 0.4 && h > 0.9361111111 && h < 1.0000000001){
            return ChatColor.RED;
        }else if(s < 0.11 && v > 0.9){
            return ChatColor.WHITE;
        }else if(s < 0.11 && v < 0.91 && v > 0.7){
            return ChatColor.WHITE;
        }else if(s < 0.11 && v < 0.71 && v > 0.2){
            return ChatColor.WHITE;
        }else if(s < 0.11 && v < 0.21){
            return ChatColor.BLACK;
        }else if(s < 0.3 && v < 0.3 && v > 0.1){
            return ChatColor.GRAY;
        }else if(s < 0.3 && v < 0.11){
            return ChatColor.BLACK;
        }else if(s < 0.7 && v < 0.6){
            return ChatColor.BLACK;
        }else if(v < 0.1){ // 0.05
            return ChatColor.BLACK;
        }else if(s > 0.29 && s < 0.8 && v < 0.11){
            return ChatColor.GRAY;
        }else if(s > 0.29 && s < 0.6 && v < 0.2){
            return ChatColor.GRAY;
            //NEW COLORS
        }else if(s > 0.6 && h > 0.5666666 && h < 0.602777 && v > 0.12 && v < 0.3){
            return ChatColor.DARK_BLUE;
        }else if(h > 0.5 && h < 0.602777 && v < 0.13){
            return ChatColor.BLACK;
        }else if(h > 0.95833333 && s > 0.7 && v > 0.19 && v < 0.4){
            return ChatColor.RED;
        }else if(h > 0.8 && h < 0.91666666 && s > 0.35 && v > 0.16 && v < 0.4){
            return ChatColor.DARK_PURPLE;
        }else if(h > 0.3055555 && h < 0.3888888 && s < 0.35 && v > 0.6 && v < 0.8){
            return ChatColor.AQUA;
        }else if(h > 0.38 && h < 0.5833333 && s < 0.35 && v > 0.7 && v < 0.95){
            return ChatColor.BLUE;
        }else if(h > 0.38 && h < 0.5833333 && s < 0.35 && v > 0.5 && v < 0.71){
            return ChatColor.DARK_BLUE;
        }else if(h > 0.5 && h < 0.61 && s > 0.2 && v > 0.7){
            return ChatColor.BLUE;
        }else if(h > 0.5 && h < 0.61 && s > 0.2 && v < 0.71){
            return ChatColor.DARK_BLUE;
        }else if(s < 0.31 && v < 0.16){
            return ChatColor.BLACK;
            //NEW COLORS 2:
        }else if(h > 0.32 && h < 0.501 && s > 0.99 && v < 0.12){
            return ChatColor.BLACK;
        }else if(h > 0.53 && h < 0.7 && s > 0.5 && v < 0.3 && v > 0.15){
            return ChatColor.DARK_BLUE;
        }else if(h > 0.4 && h < 0.53 && s > 0.5 && v < 0.3 && v > 0.15){
            return ChatColor.AQUA;
        }else if(h < 0.4 && h > 0.2777777 && s > 0.5 && v < 0.3 && v > 0.15){
            return ChatColor.DARK_GREEN;
        }else if(h < 0.25 && h > 0.2 && s > 0.6 && v < 0.25 && v > 0.15){
            return ChatColor.DARK_RED;
        }else if(h > 833333 && h < 94 && s > 0.6 && v < 0.4 && v > 0.15){
            return ChatColor.DARK_PURPLE;
        }else if(h > 0.47222222 && h < 0.541 && s < 0.4 && s > 0.2 && v > 0.8){
            return ChatColor.BLUE;
        }else if(h > 0.541 && h < 0.64 && s < 0.4 && s > 0.2 && v > 0.3){
            return ChatColor.DARK_BLUE;
        }else if(h > 0.47222222 && h < 0.541 && s < 0.4 && s > 0.2 && v < 0.5 && v > 0.2){
            return ChatColor.DARK_BLUE;
        }else if(h > 0.32 && h < 0.501 && s > 0.99 && v < 0.12){
            return ChatColor.GRAY;
            // NEW COLORS 3
        }else if(h > 0.85 && s > 0.2 && s < 0.41 && v > 0.9){
            return ChatColor.LIGHT_PURPLE;
        }else if(h > 0.763 && s > 0.2 && s < 0.41 && v > 0.5){
            return ChatColor.DARK_PURPLE;
        }else if(h > 0.125 && h < 0.191666666 && s > 0.25 && s < 0.4 && b > 0.89){
            return ChatColor.YELLOW;
        }else if(h > 0.125 && h < 0.191666666 && s > 0.25 && s < 0.4 && b < 0.81 && b > 0.3){
            return ChatColor.DARK_RED;
        }else if(h > 0.222222 && h < 0.2777777777 && s > 0.2 && s > 0.4 && b > 0.85){
            return ChatColor.GREEN;
        }else if(h > 0.222222 && h < 0.2777777777 && s > 0.2 && s > 0.4 && b > 0.4 && b < 0.8){
            return ChatColor.DARK_GREEN;
        }else if(s < 0.114 && b < 0.71 && b > 0.15){
            return ChatColor.GRAY;
        }else{
            return ChatColor.WHITE; // nothing matched
        }
    }
 
    public enum ImgChar {
        BLOCK('\u2588'),
        DARK_SHADE('\u2593'),
        MEDIUM_SHADE('\u2592'),
        LIGHT_SHADE('\u2591');
        private char c;
 
        ImgChar(char c) {
            this.c = c;
        }
 
        public char getChar() {
            return c;
        }
    }
}