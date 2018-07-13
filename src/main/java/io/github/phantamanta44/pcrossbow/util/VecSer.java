package io.github.phantamanta44.pcrossbow.util;

import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VecSer {

    private static final Pattern PATTERN = Pattern.compile("(-?(?:\\d+)?(?:\\.(?:\\d+)?)?) (-?(?:\\d+)?(?:\\.(?:\\d+)?+)?) (-?(?:\\d+)?(?:\\.(?:\\d+)?+)?)");

    public static boolean isValidVector(String s) {
        return PATTERN.matcher(s).matches();
    }

    @Nullable
    public static Vec3d deserialize(String s) {
        Matcher m = PATTERN.matcher(s);
        return m.matches()
                ? new Vec3d(Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)), Double.parseDouble(m.group(3)))
                : null;
    }

    public static String serialize(Vec3d vec) {
        return String.format("%.2f %.2f %.2f", vec.x, vec.y, vec.z);
    }

}
