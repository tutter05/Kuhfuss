package de.tutter05.kuhfuss.listener;

import de.tutter05.kuhfuss.utils.ReflectionHelper;

public class LanguageChangeListener {

    /**
     * Called when the user changes the used language. Is injected by LanguageChangeTransformer
     */
    public static void onLanguageChange() {
        ReflectionHelper.clearTranslationCache();

        ReflectionHelper.getCooldownMessage();
        ReflectionHelper.getDefaultChestTitles();
    }

}
