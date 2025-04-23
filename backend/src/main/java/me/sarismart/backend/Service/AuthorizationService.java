package me.sarismart.backend.Service;

import me.sarismart.backend.Entity.Store;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public void authorizeOwner(Store store, String currentUserId) {
        if (!store.getOwner().getSupabaseUid().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to perform this action");
        }
    }

    public void authorizeOwnerOrWorker(Store store, String currentUserId) {
        boolean isWorker = store.getWorkers().stream()
                .anyMatch(worker -> worker.getSupabaseUid().equals(currentUserId));
        boolean isOwner = store.getOwner().getSupabaseUid().equals(currentUserId);

        if (!isWorker && !isOwner) {
            throw new RuntimeException("You are not authorized to perform this action");
        }
    }
}