CREATE TABLE Chapter
(
    id          INTEGER          NOT NULL,
    name        VARCHAR(255)     NOT NULL,
    objectOrder DOUBLE PRECISION NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES JakonObject (id) ON DELETE CASCADE
)