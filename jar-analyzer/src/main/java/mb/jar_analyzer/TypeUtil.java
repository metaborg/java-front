package mb.jar_analyzer;

import java.util.Optional;

import org.objectweb.asm.Type;

public final class TypeUtil {

    public static Optional<String> getTypeName(Type type) {
        switch(type.getSort()) {
            case Type.ARRAY:
                return getTypeName(type.getElementType());
            case Type.OBJECT:
                return Optional.of(type.getInternalName());
            default:
                return Optional.empty();
        }
    }

}