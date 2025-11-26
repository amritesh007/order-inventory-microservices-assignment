package org.korber.inventory.service.handler;


import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InventoryHandlerFactory {

    private final Map<String, InventoryHandler> handlers;

    public InventoryHandlerFactory(Map<String, InventoryHandler> handlers) {
        this.handlers = handlers;
    }

    public InventoryHandler getHandler(String key){
        return handlers.getOrDefault(key, handlers.get("expiry"));
    }
}
