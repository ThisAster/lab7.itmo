package com.freiz.common.util;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.freiz.common.data.MeleeWeapon;
import com.freiz.common.data.SpaceMarine;
import com.freiz.common.data.Weapon;

public interface ICollectionManager {

    ZonedDateTime getCreationDate();

    Long getNewID();

    void clear();

    void add(SpaceMarine spaceMarine);

    boolean isAny(Long id);

    void remove(SpaceMarine spaceMarine);

    SpaceMarine findSpaceMarineById(Long id);

    long countLessThanMeleeWeapon(MeleeWeapon meleeWeapon);

    long countLessThan(SpaceMarine spaceMarine);
    long countGreaterThan(SpaceMarine spaceMarine);

    long countGreaterThanHeartCount(int heartCount);

    Set<SpaceMarine> findByWeaponType(Weapon weapon);

    int getSize();

    boolean removeById(Long id);

    HashSet<SpaceMarine> getAll();

    List<SpaceMarine> getAllSorted();

}
