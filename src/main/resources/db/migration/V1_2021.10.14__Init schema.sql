CREATE TABLE "role" (
                                 "id" SERIAL PRIMARY KEY,
                                 "name" VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE "user" (
                                 "id" SERIAL PRIMARY KEY,
                                 "login" VARCHAR(128) UNIQUE NOT NULL,
                                 "password" VARCHAR(128) NOT NULL,
                                 "first_name" VARCHAR(128) NOT NULL,
                                 "last_name" VARCHAR(128) NOT NULL
);

CREATE TABLE "room" (
                                 "id" SERIAL PRIMARY KEY,
                                 "name" VARCHAR(64) NOT NULL,
                                 "author_id" INTEGER NOT NULL,
                                 "created" TIMESTAMP NOT NULL,
                                 "expired" TIMESTAMP NOT NULL,
                                 "voting" BOOLEAN NOT NULL
);

CREATE INDEX "idx_poker_room__author_id" ON "room" ("author_id");

ALTER TABLE "room" ADD CONSTRAINT "room_author" FOREIGN KEY ("author_id") REFERENCES "user" ("id") ON DELETE CASCADE;

CREATE TABLE "room_role" (
                                      "room_id" INTEGER NOT NULL,
                                      "role_id" INTEGER NOT NULL,
                                      PRIMARY KEY ("room_id", "role_id")
);

CREATE INDEX "idx_poker_room_role__poker_role" ON "room_role" ("role_id");

ALTER TABLE "room_role" ADD CONSTRAINT "room_role__role_id" FOREIGN KEY ("role_id") REFERENCES "role" ("id") ON DELETE CASCADE;

ALTER TABLE "room_role" ADD CONSTRAINT "room_role__room_id" FOREIGN KEY ("room_id") REFERENCES "room" ("id") ON DELETE CASCADE;

CREATE TABLE "participant" (
                                        "user_id" INTEGER NOT NULL,
                                        "room_id" INTEGER NOT NULL,
                                        "role_id" INTEGER NOT NULL,
                                        "score" VARCHAR(16),
                                        PRIMARY KEY ("user_id", "room_id")
);

CREATE INDEX "idx_poker_participant__room_id_poker_room_ro" ON "participant" ("room_id", "role_id");

CREATE INDEX "idx_poker_participant__user_id" ON "participant" ("user_id");

ALTER TABLE "participant" ADD CONSTRAINT "participant__room_id__poker_room_ro" FOREIGN KEY ("room_id", "role_id") REFERENCES "room_role" ("room_id", "role_id") ON DELETE CASCADE;

ALTER TABLE "participant" ADD CONSTRAINT "participant__user_id" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE;

CREATE TABLE "client_session" (
                                  "session_id" VARCHAR(128) PRIMARY KEY,
                                  "user_id" INTEGER NOT NULL,
                                  "created_at" TIMESTAMP NOT NULL,
                                  "valid_to" TIMESTAMP NOT NULL
);

ALTER TABLE "client_session" ADD CONSTRAINT "client_session_user" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE;

CREATE INDEX "idx_client_session__user_id" ON "client_session" ("user_id");

CREATE TABLE "room_secret" (
                               "room_id" INTEGER PRIMARY KEY,
                               "secret_code" VARCHAR(128) NOT NULL
);

ALTER TABLE "room_secret" ADD CONSTRAINT "room_code_room" FOREIGN KEY ("room_id") REFERENCES "room" ("id") ON DELETE CASCADE;