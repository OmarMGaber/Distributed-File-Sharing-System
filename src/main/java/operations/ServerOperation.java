package operations;

import servers.Server;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface ServerOperation extends Serializable {
//    enum SmartOperationReturnTypes {
//        BOOLEAN,
//        STRING,
//        LIST,
//        MAP,
//        VOID;
//
//        @SuppressWarnings("unchecked")
//        public <T> T cast(Object object) {
//            return switch (this) {
//                case BOOLEAN -> (T) Boolean.valueOf(object.toString());
//                case STRING -> (T) object.toString();
//                case LIST -> (T) object;
//                case MAP -> (T) object;
//                case VOID -> null;
//                default -> null;
//            };
//        }
//
//        public static SmartOperationReturnTypes getReturnType(Object object) {
//            return switch (object) {
//                case Boolean b -> BOOLEAN;
//                case String s -> STRING;
//                case List list -> LIST;
//                case Map map -> MAP;
//                case null, default -> VOID;
//            };
//        }
//    }

    Object execute(Server server, Object[] objects);
}
