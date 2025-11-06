package net.mmly.openminemap.event;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.UUID;

class CoordinateValue {
    public String value;

    CoordinateValue(String s) {
        value = s;
    }

    public static CoordinateValue fromString(String s) {
        return new CoordinateValue(s);
    }

}
//  33.451363° -112.072678°
public class CoordinateArgumentType implements ArgumentType<CoordinateValue> {
    @Override
    public CoordinateValue parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();
        if (!reader.canRead()) {
            reader.skip();
        }
        while (reader.canRead()) {
            reader.skip();
        }
        String value = reader.getString().substring(argBeginning, reader.getCursor());
        return CoordinateValue.fromString(value);
    }

    public static CoordinateArgumentType coordinateArgumentType() {
        return new CoordinateArgumentType();
    }
}


