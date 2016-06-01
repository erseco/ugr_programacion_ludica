package es.ernesto.minicars;

public class GameSettings {
	private static int currentLevel;
	private static int maxSpeed;
	private static String Language = java.util.Locale.getDefault().getDisplayName().substring(0, 7);

	public static int getCurrentLevel() {
		return currentLevel;
	}

	public static void setCurrentLevel(int level) {
		GameSettings.currentLevel = level;
	}

	public static int getMaxSpeed() {
		return maxSpeed;
	}

	public static void setMaxSpeed(int maxSpeed) {
		GameSettings.maxSpeed = maxSpeed;
	}

	public static String getLanguage() {
		return Language;
	}

	public static void setLanguage(String language) {
		Language = language;
	}
}
