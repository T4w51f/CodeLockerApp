CREATE TABLE public.app_user
(
    id integer NOT NULL,
    user_id integer NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    email character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    username character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT app_user_pkey PRIMARY KEY (id)
);

CREATE TABLE public.platform
(
    id integer NOT NULL,
    user_id integer NOT NULL,
    appname character varying(255) COLLATE pg_catalog."default",
    username character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    CONSTRAINT platform_pkey PRIMARY KEY (id)
);

INSERT INTO app_user (id, user_id, name, email, password) 
VALUES ('1', '1f44f1ae-ecab-11e9-81b4-2a2ae2dbcce4', 'Tawsif Hasan', 'th@gmail.com', 'testpassword');