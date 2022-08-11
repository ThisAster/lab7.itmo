create table spacemarine
(
    id serial,
    "name" varchar not null,
    coordinates_id int not null,
    "creationDate" timestamp not null,
    health real,
    heartcount int,
    weapon_type_id int not null,
    melee_weapon_id int not null,
    chapter_id int not null,
    user_id int not null
);

create unique index spacemarine_id_uindex
    on spacemarine (id);

create table coordinates
(
    id serial
        constraint coordinates_pk
            primary key,
    x real,
    y real
);

create table weapontype
(
    id serial
        constraint weapontype_pk
            primary key,
    "name" varchar not null
);

create table meleeweapon
(
    id serial
        constraint meleeweapon_pk
            primary key,
    "name" varchar not null
);

create table chapter
(
    id serial
        constraint chapter_pk
            primary key,
    "name" varchar not null,
    parentLegion varchar,
    marinescount int not null,
    world varchar
);

create table "user"
(
    id serial
        constraint user_pk
            primary key,
    "name" varchar not null,
    password varchar not null
);

INSERT INTO "weapontype" (id, name) VALUES (DEFAULT, 'HEAVY_BOLTGUN');
INSERT INTO "weapontype" (id, name) VALUES (DEFAULT, 'BOLT_RIFLE');
INSERT INTO "weapontype" (id, name) VALUES (DEFAULT, 'GRENADE_LAUNCHER');
INSERT INTO "weapontype" (id, name) VALUES (DEFAULT, 'INFERNO_PISTOL');
INSERT INTO "weapontype" (id, name) VALUES (DEFAULT, 'MULTI_MELTA');

INSERT INTO "meleeweapon" (id, name) VALUES (DEFAULT, 'CHAIN_SWORD');
INSERT INTO "meleeweapon" (id, name) VALUES (DEFAULT, 'MANREAPER');
INSERT INTO "meleeweapon" (id, name) VALUES (DEFAULT, 'LIGHTING_CLAW');
INSERT INTO "meleeweapon" (id, name) VALUES (DEFAULT, 'POWER_BLADE');
INSERT INTO "meleeweapon" (id, name) VALUES (DEFAULT, 'POWER_FIST');

