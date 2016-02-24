CREATE TABLE search (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    algorithm TEXT NOT NULL,
    datetime TEXT NOT NULL
);

CREATE TABLE search_snapshot (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    search_id INTEGER NOT NULL,
    FOREIGN KEY(search_id) REFERENCES search(id)
);

CREATE TABLE frontier (
    node INTEGER NOT NULL,
    search_snapshot_id INTEGER NOT NULL,
    FOREIGN KEY(search_snapshot_id) REFERENCES search_snapshot(id)
);

CREATE TABLE explored (
    node INTEGER NOT NULL,
    search_snapshot_id INTEGER NOT NULL,
    FOREIGN KEY(search_snapshot_id) REFERENCES search_snapshot(id)
);