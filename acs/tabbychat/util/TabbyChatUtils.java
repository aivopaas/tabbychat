package acs.tabbychat.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import acs.tabbychat.core.ChatChannel;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.ChannelDelimEnum;
import acs.tabbychat.settings.ColorCodeEnum;
import acs.tabbychat.settings.FormatCodeEnum;
import acs.tabbychat.settings.NotificationSoundEnum;
import acs.tabbychat.settings.TimeStampEnum;
import acs.tabbychat.threads.BackgroundChatThread;

import net.minecraft.client.Minecraft;

import net.minecraft.src.Gui;

public class TabbyChatUtils extends Thread {
	
	private static Calendar logDay = Calendar.getInstance();
	private static File logDir = new File(Minecraft.getMinecraftDir(), "TabbyChatLogs");
	private static File logFile;
	private static SimpleDateFormat logNameFormat = new SimpleDateFormat("'TabbyChatLog_'MM-dd-yyyy'.txt'");
	public static String version = "1.7.07b3";
	
	private TabbyChatUtils() {}
	
	public static String join(String[] arr, String glue) {
		if (arr.length < 1)
			return "";
		else if (arr.length == 1)
			return arr[0];
		StringBuilder bucket = new StringBuilder();
		for (String s : Arrays.copyOf(arr,  arr.length-1)) {
			bucket.append(s);
			bucket.append(glue);
		}
		bucket.append(arr[arr.length-1]);
		return bucket.toString();
	}
	
	public static boolean is(Gui _gui, String className) {
		try {
			return _gui.getClass().getSimpleName().contains(className);
		} catch (Throwable e) {}
		return false;
	}

	public static void writeLargeChat(String toSend) {
		BackgroundChatThread sendProc = new BackgroundChatThread(toSend);
		sendProc.start();
	}
	
	public static void logChat(String theChat) {
		Calendar tmpcal = Calendar.getInstance();
	
		if (logFile == null || tmpcal.get(Calendar.DAY_OF_YEAR) != logDay.get(Calendar.DAY_OF_YEAR)) {
			logDay = tmpcal;
			logFile = new File(logDir, logNameFormat.format(logDay.getTime()).toString());
		}
		
		if (!logFile.exists()) {
			try {
				logDir.mkdirs();
				logFile.createNewFile();
			} catch (Exception e) {
				TabbyChat.printErr("Cannot create log file : '" + e.getLocalizedMessage() + "' : " + e.toString());
				return;
			}
		}
		
		try {
			FileOutputStream logStream = new FileOutputStream(logFile, true);
			PrintStream logPrint = new PrintStream(logStream);
			logPrint.println(theChat);
			logPrint.close();
		} catch (Exception e) {
			TabbyChat.printErr("Cannot write to log file : '" + e.getLocalizedMessage() + "' : " + e.toString());
			return;
		}
	}

	public static Integer parseInteger(String _input, int min, int max, int fallback) {
		Integer result;
		System.out.println("parseInteger: parsing '"+_input+"'");
		try {
			result = Integer.parseInt(_input);
			result = Math.max(min, result);
			result = Math.min(max, result);
		} catch (NumberFormatException e) {
			result = fallback;
		}
		System.out.println("parsed to '"+result.toString()+"'");
		return result;
	}
	
	public static Float parseFloat(Object _input, float min, float max) {
		if(_input == null) return (Float)_input;
		String input = _input.toString();
		Float result;
		try {
			result = Float.valueOf(input);
			result = Math.max(min, result);
			result = Math.min(max, result);
		} catch (NumberFormatException e) {
			result = null;
		}
		return result;
	}

	public static ChannelDelimEnum parseDelimiters(Object _input) {
		if(_input == null) return null;
		String input = _input.toString();
		try {
			return ChannelDelimEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static ColorCodeEnum parseColor(Object _input) {
		if(_input == null) return null;
		String input = _input.toString();
		try {
			return ColorCodeEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static FormatCodeEnum parseFormat(Object _input) {
		if(_input == null) return null;
		String input = _input.toString();
		try {
			return FormatCodeEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static NotificationSoundEnum parseSound(Object _input) {
		if(_input == null) return NotificationSoundEnum.ORB;
		String input = _input.toString();
		try {
			return NotificationSoundEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return NotificationSoundEnum.ORB;
		}
	}
	
	public static String parseString(Object _input) {
		if(_input == null) return " ";
		else return _input.toString();
	}
	
	public static TimeStampEnum parseTimestamp(Object _input) {
		if(_input == null) return null;
		String input = _input.toString();
		try {
			return TimeStampEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static LinkedHashMap<String, ChatChannel> swapChannels(LinkedHashMap<String, ChatChannel> currentMap, int _left, int _right) {
		// Ensure ordering of 'indices' is 0<=_left<_right<=end
		if(_left == _right) return currentMap;
		else if (_left > _right) {
			int _tmp = _left;
			_left = _right;
			_right = _tmp;
		}
		if(_right >= currentMap.size()) return currentMap;
		
		// Convert map to array for access by index
		String[] arrayCopy = new String[currentMap.size()];
		arrayCopy = currentMap.keySet().toArray(arrayCopy);
		// Swap array entries using passed index arguments
		String tmp = arrayCopy[_left];
		arrayCopy[_left] = arrayCopy[_right];
		arrayCopy[_right] = tmp;
		// Create new map and populate
		int n = arrayCopy.length;
		LinkedHashMap<String, ChatChannel> returnMap = new LinkedHashMap(n);
		for(int i=0; i<n; i++) {
			returnMap.put(arrayCopy[i], currentMap.get(arrayCopy[i]));
		}
		return returnMap;
	}
}
