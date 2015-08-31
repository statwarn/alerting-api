-- Database generated with pgModeler (PostgreSQL Database Modeler).
-- pgModeler  version: 0.8.0-beta1
-- PostgreSQL version: 9.2
-- Project Site: pgmodeler.com.br
-- Model Author: ---


-- Database creation must be done outside an multicommand file.
-- These commands were put in this file only for convenience.
-- -- object: "statwarn-alerting" | type: DATABASE --
-- -- DROP DATABASE IF EXISTS "statwarn-alerting";
-- CREATE DATABASE "statwarn-alerting"
-- ;
-- -- ddl-end --
--

-- object: public.alert | type: TABLE --
-- DROP TABLE IF EXISTS public.alert;
CREATE TABLE public.alert(
	alert_id uuid NOT NULL,
	name text NOT NULL,
	"createdAt" timestamptz NOT NULL,
	"updatedAt" timestamptz NOT NULL,
	"deletedAt" timestamptz,
	activated boolean NOT NULL DEFAULT true,
	measurement_id text NOT NULL,
	CONSTRAINT alert_alert_id_pkey PRIMARY KEY (alert_id)

);
-- ddl-end --
ALTER TABLE public.alert OWNER TO qsgrmczhhjhwpp;
-- ddl-end --

-- object: public.action | type: TABLE --
-- DROP TABLE IF EXISTS public.action;
CREATE TABLE public.action(
	action_id text NOT NULL,
	"defaultConfiguration" jsonb NOT NULL,
	CONSTRAINT action_action_id_pkey PRIMARY KEY (action_id)

);
-- ddl-end --
ALTER TABLE public.action OWNER TO qsgrmczhhjhwpp;
-- ddl-end --

-- object: public.alert_action | type: TABLE --
-- DROP TABLE IF EXISTS public.alert_action;
CREATE TABLE public.alert_action(
	alert_id uuid NOT NULL,
	action_id text NOT NULL,
	action_configuration jsonb NOT NULL,
	"createdAt" timestamptz NOT NULL,
	"updatedAt" timestamptz NOT NULL,
	"deletedAt" timestamptz
);
-- ddl-end --
ALTER TABLE public.alert_action OWNER TO qsgrmczhhjhwpp;
-- ddl-end --

-- object: public.trigger | type: TABLE --
-- DROP TABLE IF EXISTS public.trigger;
CREATE TABLE public.trigger(
	trigger_id uuid NOT NULL,
	operator_id text NOT NULL,
	alert_id uuid NOT NULL,
	target_id text NOT NULL,
	target_value text NOT NULL,
	operator_configuration jsonb NOT NULL,
	"createdAt" timestamptz NOT NULL,
	"updatedAt" timestamptz NOT NULL,
	"deletedAt" timestamptz,
	CONSTRAINT trigger_trigger_id_pkey PRIMARY KEY (trigger_id)

);
-- ddl-end --
ALTER TABLE public.trigger OWNER TO qsgrmczhhjhwpp;
-- ddl-end --

-- object: public.operator | type: TABLE --
-- DROP TABLE IF EXISTS public.operator;
CREATE TABLE public.operator(
	operator_id text NOT NULL,
	"defaultConfiguration" jsonb NOT NULL,
	CONSTRAINT operator_operator_id_pkey PRIMARY KEY (operator_id)

);
-- ddl-end --
ALTER TABLE public.operator OWNER TO qsgrmczhhjhwpp;
-- ddl-end --

-- object: public.target | type: TABLE --
-- DROP TABLE IF EXISTS public.target;
CREATE TABLE public.target(
	target_id text NOT NULL,
	wildcard boolean NOT NULL,
	CONSTRAINT target_target_id_pkey PRIMARY KEY (target_id)

);
-- ddl-end --
ALTER TABLE public.target OWNER TO qsgrmczhhjhwpp;
-- ddl-end --

-- object: public.operator_target | type: TABLE --
-- DROP TABLE IF EXISTS public.operator_target;
CREATE TABLE public.operator_target(
	operator_id text NOT NULL,
	target_id text NOT NULL,
	CONSTRAINT operator_target_operator_id_target_id_key UNIQUE (operator_id,target_id)

);
-- ddl-end --
ALTER TABLE public.operator_target OWNER TO qsgrmczhhjhwpp;
-- ddl-end --

-- object: alert_action_alert_id_fkey | type: CONSTRAINT --
-- ALTER TABLE public.alert_action DROP CONSTRAINT IF EXISTS alert_action_alert_id_fkey;
ALTER TABLE public.alert_action ADD CONSTRAINT alert_action_alert_id_fkey FOREIGN KEY (alert_id)
REFERENCES public.alert (alert_id) MATCH FULL
ON DELETE CASCADE ON UPDATE NO ACTION;
-- ddl-end --

-- object: alert_action_action_id_fkey | type: CONSTRAINT --
-- ALTER TABLE public.alert_action DROP CONSTRAINT IF EXISTS alert_action_action_id_fkey;
ALTER TABLE public.alert_action ADD CONSTRAINT alert_action_action_id_fkey FOREIGN KEY (action_id)
REFERENCES public.action (action_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: trigger_alert_id_fkey | type: CONSTRAINT --
-- ALTER TABLE public.trigger DROP CONSTRAINT IF EXISTS trigger_alert_id_fkey;
ALTER TABLE public.trigger ADD CONSTRAINT trigger_alert_id_fkey FOREIGN KEY (alert_id)
REFERENCES public.alert (alert_id) MATCH FULL
ON DELETE CASCADE ON UPDATE NO ACTION;
-- ddl-end --

-- object: trigger_operator_id_target_id_fkey | type: CONSTRAINT --
-- ALTER TABLE public.trigger DROP CONSTRAINT IF EXISTS trigger_operator_id_target_id_fkey;
ALTER TABLE public.trigger ADD CONSTRAINT trigger_operator_id_target_id_fkey FOREIGN KEY (operator_id,target_id)
REFERENCES public.operator_target (operator_id,target_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: operator_target_operator_id_fkey | type: CONSTRAINT --
-- ALTER TABLE public.operator_target DROP CONSTRAINT IF EXISTS operator_target_operator_id_fkey;
ALTER TABLE public.operator_target ADD CONSTRAINT operator_target_operator_id_fkey FOREIGN KEY (operator_id)
REFERENCES public.operator (operator_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: operator_target_target_id_fkey | type: CONSTRAINT --
-- ALTER TABLE public.operator_target DROP CONSTRAINT IF EXISTS operator_target_target_id_fkey;
ALTER TABLE public.operator_target ADD CONSTRAINT operator_target_target_id_fkey FOREIGN KEY (target_id)
REFERENCES public.target (target_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --


