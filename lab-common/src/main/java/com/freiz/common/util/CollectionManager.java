package com.freiz.common.util;

import com.freiz.common.data.MeleeWeapon;
import com.freiz.common.data.SpaceMarine;
import com.freiz.common.data.Weapon;
import com.freiz.common.db.SpaceMarineDao;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class CollectionManager implements ICollectionManager {
    private final HashSet<Long> hashSetId = new HashSet<>();
    private Long idIter = 1L;
    private Set<SpaceMarine> spaceMarinesCollection = Collections.synchronizedSet(new HashSet<>());
    private ZonedDateTime creationDate = ZonedDateTime.now();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    public CollectionManager() {
    }

    //public HashSet<SpaceMarine> getSpaceMarinesCollection() {
        //return spaceMarinesCollection;
    //}

    public void clear() {
        spaceMarinesCollection.clear();
    }

    @Override
    public void add(SpaceMarine spaceMarine) {
        lock.writeLock();
        spaceMarinesCollection.add(spaceMarine);
        if (lock.isWriteLockedByCurrentThread()) {
            lock.writeLock().unlock();
        }
    }
    @Override
    public boolean isAny(Long id) {
        return spaceMarinesCollection.stream().anyMatch(x -> x.getId().equals(id));
    }

    @Override
    public void remove(SpaceMarine spaceMarine) {
        spaceMarinesCollection.remove(spaceMarine);
    }

    @Override
    public SpaceMarine findSpaceMarineById(Long id) {
        return spaceMarinesCollection.stream().filter(x -> x.getId().equals(id)).findAny().get();
    }

    @Override
    public long countLessThanMeleeWeapon(MeleeWeapon meleeWeapon) {
        return spaceMarinesCollection.stream().filter(e -> e.getMeleeWeapon().compareTo(meleeWeapon) < 0).count();
    }

    @Override
    public long countLessThan(SpaceMarine spaceMarine) {
        return spaceMarinesCollection.stream().filter(x -> spaceMarine.compareTo(x) >= 0).count();
    }

    @Override
    public long countGreaterThan(SpaceMarine spaceMarine) {
        return spaceMarinesCollection.stream().filter(x -> spaceMarine.compareTo(x) <= 0).count();
    }

    @Override
    public long countGreaterThanHeartCount(int heartCount) {
        return spaceMarinesCollection.stream().filter(spaceMarine -> spaceMarine.getHeartCount() > heartCount).count();
    }

    @Override
    public Set<SpaceMarine> findByWeaponType(Weapon weapon) {
        return spaceMarinesCollection.stream().filter(it -> it.getWeaponType().equals(weapon)).collect(Collectors.toSet());
    }

    @Override
    public int getSize() {
        return spaceMarinesCollection.size();
    }

    @Override
    public boolean removeById(Long id) {
        return spaceMarinesCollection.removeIf(x -> x.getId() == id);
    }

    @Override
    public Set<SpaceMarine> getAll() {
        return spaceMarinesCollection;
    }

    @Override
    public List<SpaceMarine> getAllSorted() {
        return spaceMarinesCollection.stream().sorted(Comparator.comparing(SpaceMarine::getName)).collect(Collectors.toList());
    }

    public void collectionManagerLoadRecordsFromDB() {
        spaceMarinesCollection = new HashSet<>(SpaceMarineDao.getAllSpaceMarinesFromDB());
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public Long getNewID() {
        idIter = 1L;
        while (hashSetId.contains(idIter)) {
            idIter++;
        }
        return idIter;
    }

    public HashSet<Long> getHashSetId() {
        return hashSetId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (SpaceMarine i : spaceMarinesCollection) {
            sb.append('\n' + i.toString() + '\n');
        }
        sb.append(']');

        return sb.toString();
    }

}
