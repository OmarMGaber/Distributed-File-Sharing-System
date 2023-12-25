package operations;

import servers.Server;

public class ConcreteServerOperations implements ServerOperations {
    @Override
    public Object preformOperation(Server server, ServerOperation operation, Object[] objects) {
        return operation.execute(server, objects);
    }
}