package com.voxelgameslib.voxelgameslib.persistence;

import com.voxelgameslib.voxelgameslib.user.User;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * Empty persistence provider, used when persistence is disabled
 */
public class EmptyPersistenceProvider implements PersistenceProvider {

    @Override
    public void start() {
        // ignore
    }

    @Override
    public void stop() {
        // ignore
    }

    @Override
    public void saveUser(@Nonnull User user) {
        // ignore
    }

    @Override
    @Nonnull
    public Optional<User> loadUser(@Nonnull UUID id) {
        return Optional.empty();
    }
}
