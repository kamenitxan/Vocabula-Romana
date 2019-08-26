CREATE TABLE Word
(
    id         INTEGER      NOT NULL,
    chapter_id INTEGER,
    latin      VARCHAR(255) NOT NULL,
    cz         VARCHAR(255),
    en         VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES JakonObject (id) ON DELETE CASCADE,
    FOREIGN KEY (chapter_id) REFERENCES Chapter (id) ON DELETE SET NULL
)