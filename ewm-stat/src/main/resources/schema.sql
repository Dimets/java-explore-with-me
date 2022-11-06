DROP TABLE IF EXISTS hits CASCADE;

CREATE TABLE IF NOT EXISTS hits
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    app text,
    uri text,
    ip text,
    dttm timestamp without time zone,
    CONSTRAINT hits_pkey PRIMARY KEY (id)
    );

