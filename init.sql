CREATE TABLE public.app_user
(
    id integer NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    email character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    username character varying(255) COLLATE pg_catalog."default",
    user_id uuid,
    CONSTRAINT app_user_pkey PRIMARY KEY (id)
);

CREATE TABLE public.platform
(
    id integer NOT NULL,
    app_name character varying(255) COLLATE pg_catalog."default",
    username character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    user_id uuid
)