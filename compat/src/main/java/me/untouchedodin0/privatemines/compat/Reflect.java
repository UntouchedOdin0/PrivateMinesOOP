package me.untouchedodin0.privatemines.compat;

import org.jetbrains.annotations.Nullable;

class Reflect {
    private Reflect() {}
    static <T> @Nullable T instantiate(String className) {
        try {
            //noinspection unchecked
            Class<? extends T> clazz = (Class<? extends T>) Class.forName(className);
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
