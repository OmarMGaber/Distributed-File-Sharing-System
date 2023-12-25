package operations;

import servers.Server;

public interface ServerOperations {
    Object preformOperation(Server server, ServerOperation operation, Object[] objects);
}
