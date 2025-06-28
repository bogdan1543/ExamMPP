package exam.networking.utils;

import com.google.gson.*;

import java.lang.reflect.Type;

public class EnumTypeAdapter<T extends Enum<T>> implements JsonSerializer<T>, JsonDeserializer<T> {

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name()); // trimite "RESERVE", nu 2
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) typeOfT;
        return Enum.valueOf(clazz, json.getAsString());
    }
}

