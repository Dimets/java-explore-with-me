DROP TABLE IF EXISTS users CASCADE;

DROP TABLE IF EXISTS categories CASCADE;

DROP TABLE IF EXISTS locations CASCADE;

DROP TABLE IF EXISTS events CASCADE;

DROP TABLE IF EXISTS event_states CASCADE;

DROP TABLE IF EXISTS request_statuses CASCADE;

DROP TABLE IF EXISTS requests CASCADE;

DROP INDEX IF EXISTS requests_id_status_idx;

DROP TABLE IF EXISTS compilations CASCADE;

DROP TABLE IF EXISTS events_compilations CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name character varying(256),
    email character varying(256),
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_email_key UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS categories
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name text,
    CONSTRAINT categories_pkey PRIMARY KEY (id),
    CONSTRAINT categories_name_key UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS locations
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    lat numeric,
    lon numeric,
    CONSTRAINT locations_pkey PRIMARY KEY (id),
    CONSTRAINT locations_lat_lon_key UNIQUE (lat, lon)
);

CREATE TABLE IF NOT EXISTS event_states
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    state text NOT NULL,
    CONSTRAINT event_states_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    annotation text,
    category_id bigint,
    description text,
    event_date timestamp without time zone,
    initiator_id bigint,
    location_id bigint,
    is_paid boolean DEFAULT FALSE,
    participiant_limit integer DEFAULT 0,
    published_date timestamp without time zone,
    is_moderated boolean,
    state_id bigint,
    title text,
    create_date timestamp without time zone,
    CONSTRAINT events_pkey PRIMARY KEY (id),
    CONSTRAINT events_category_id_fkey FOREIGN KEY (category_id)
        REFERENCES categories (id),
    CONSTRAINT events_initiator_id_fkey FOREIGN KEY (initiator_id)
        REFERENCES users (id),
    CONSTRAINT events_location_id_fkey FOREIGN KEY (location_id)
        REFERENCES locations (id),
    CONSTRAINT events_state_id_fkey FOREIGN KEY (state_id)
        REFERENCES event_states (id)
);

CREATE TABLE IF NOT EXISTS request_statuses
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    status text NOT NULL,
    CONSTRAINT request_statuses_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    requester_id bigint,
    event_id bigint,
    created timestamp without time zone,
    status_id bigint,
    CONSTRAINT  requests_pkey PRIMARY KEY (id),
    CONSTRAINT requests_requester_id_fkey FOREIGN KEY (requester_id)
        REFERENCES users (id),
    CONSTRAINT requests_event_id_fkey FOREIGN KEY (event_id)
        REFERENCES events (id),
    CONSTRAINT requests_status_id_fkey FOREIGN KEY (status_id)
        REFERENCES request_statuses (id)
);


CREATE INDEX IF NOT EXISTS requests_id_status_idx
    ON public.requests USING btree
    (id, status_id);

CREATE TABLE IF NOT EXISTS compilations
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    is_pinned boolean DEFAULT FALSE,
    title text,
    CONSTRAINT compilations_pkey PRIMARY KEY (id),
    CONSTRAINT compilations_title_key UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS events_compilations
(
    compilation_id bigint,
    event_id bigint,
    CONSTRAINT events_compilations_key UNIQUE (compilation_id, event_id),
    CONSTRAINT events_compilations_compilation_id_fkey FOREIGN KEY (compilation_id)
        REFERENCES compilations (id),
    CONSTRAINT events_compilations_event_id_fkey FOREIGN KEY (event_id)
        REFERENCES events (id)
);


