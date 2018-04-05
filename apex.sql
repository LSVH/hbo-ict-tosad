-- PACKAGES --

CREATE OR REPLACE PACKAGE brg_utils AS
	FUNCTION equal_amount_keys("csvX" VARCHAR2, "csvY" VARCHAR2)
		RETURN NUMBER;

	FUNCTION category_names
		RETURN VARCHAR2;
	FUNCTION rule_type_types
		RETURN VARCHAR2;
	FUNCTION rule_compare_operators
		RETURN VARCHAR2;
	FUNCTION rule_compare_inter_timing
		RETURN VARCHAR2;
	FUNCTION rule_compare_inter_action
		RETURN VARCHAR2;
	FUNCTION split(p_string VARCHAR2, p_seperator VARCHAR2 DEFAULT ',')
		RETURN T_NUMBERS;

	FUNCTION is_range_type(p_rule_type VARCHAR2)
		RETURN NUMBER;
	FUNCTION is_range_attr_type(p_rule_type VARCHAR2)
		RETURN NUMBER;

	FUNCTION is_compare_type(p_rule_type VARCHAR2)
		RETURN NUMBER;
	FUNCTION is_compare_attr_type(p_rule_type VARCHAR2)
		RETURN NUMBER;
	FUNCTION is_compare_tuple_type(p_rule_type VARCHAR2)
		RETURN NUMBER;
	FUNCTION is_compare_inter_type(p_rule_type VARCHAR2)
		RETURN NUMBER;
END brg_utils;
/
CREATE OR REPLACE PACKAGE BODY brg_utils AS
	FUNCTION equal_amount_keys("csvX" VARCHAR2, "csvY" VARCHAR2)
		RETURN NUMBER AS
		regex  VARCHAR2(15) := '[^,]*([,])[^,]*';
		output NUMBER(1, 0) := 0;
		BEGIN
			IF REGEXP_COUNT("csvX", regex) = REGEXP_COUNT("csvY", regex)
			THEN
				output := 1;
			END IF;
			RETURN output;
		END equal_amount_keys;
	FUNCTION category_names
		RETURN VARCHAR2 AS
		BEGIN
			RETURN 'SELECT "NAME", "ID" FROM CATEGORY';
		END category_names;
	FUNCTION rule_type_types
		RETURN VARCHAR2 AS
		BEGIN
			RETURN 'SELECT ''Attribute range rule'', ''ARNG'' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Attribute compare rule'', ''ACMP'' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Tuple compare rule'', ''TCMP'' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Inter-entity compare rule'', ''ICMP'' FROM dual';
		END rule_type_types;
	FUNCTION rule_compare_operators
		RETURN VARCHAR2 AS
		BEGIN
			RETURN 'SELECT ''Equal to'', ''='' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Not equal to'', ''!='' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Less than'', ''<'' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Less than or equal to'', ''<='' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Greater than'', ''>'' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Greater than or equal to'', ''>='' FROM dual';
		END rule_compare_operators;
	FUNCTION rule_compare_inter_timing
		RETURN VARCHAR2 AS
		BEGIN
			RETURN 'SELECT ''Before'', ''BEFORE'' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''After'', ''AFTER'' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Instead of'', ''INSTEAD OF'' FROM dual';
		END rule_compare_inter_timing;
	FUNCTION rule_compare_inter_action
		RETURN VARCHAR2 AS
		BEGIN
			RETURN 'SELECT ''Create'', ''CREATE'' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Create or update'', ''CREATE OR UPDATE'' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Create, update or delete'', ''CREATE OR UPDATE OR DELETE'' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Update'', ''UPDATE'' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Update or delete'', ''UPDATE OR DELETE'' FROM dual '
				   || 'UNION ALL '
				   || 'SELECT ''Delete'', ''DELETE'' FROM dual';
		END rule_compare_inter_action;
	FUNCTION split(p_string VARCHAR2, p_seperator VARCHAR2 DEFAULT ',')
		RETURN T_NUMBERS AS
		L_NUMBERS T_NUMBERS;
		BEGIN

			SELECT REGEXP_SUBSTR(p_string, '(.*?)(' || p_seperator || '|$)', 1, level, NULL, 1)
			BULK COLLECT INTO L_NUMBERS
			FROM dual
			CONNECT BY level <= REGEXP_COUNT(p_string, p_seperator) + 1;

			RETURN L_NUMBERS;
		END split;

	FUNCTION is_range_type(p_rule_type VARCHAR2)
		RETURN NUMBER AS
		output NUMBER(1, 0) := 0;
		BEGIN
			IF p_rule_type LIKE '%RNG'
			THEN
				output := 1;
			END IF;
			RETURN output;
		END is_range_type;
	FUNCTION is_range_attr_type(p_rule_type VARCHAR2)
		RETURN NUMBER AS
		output NUMBER(1, 0) := 0;
		BEGIN
			IF p_rule_type = 'ARNG'
			THEN
				output := 1;
			END IF;
			RETURN output;
		END is_range_attr_type;

	FUNCTION is_compare_type(p_rule_type VARCHAR2)
		RETURN NUMBER AS
		output NUMBER(1, 0) := 0;
		BEGIN
			IF p_rule_type LIKE '%CMP'
			THEN
				output := 1;
			END IF;
			RETURN output;
		END is_compare_type;
	FUNCTION is_compare_attr_type(p_rule_type VARCHAR2)
		RETURN NUMBER AS
		output NUMBER(1, 0) := 0;
		BEGIN
			IF p_rule_type = 'ACMP'
			THEN
				output := 1;
			END IF;
			RETURN output;
		END is_compare_attr_type;
	FUNCTION is_compare_tuple_type(p_rule_type VARCHAR2)
		RETURN NUMBER AS
		output NUMBER(1, 0) := 0;
		BEGIN
			IF p_rule_type = 'TCMP'
			THEN
				output := 1;
			END IF;
			RETURN output;
		END is_compare_tuple_type;
	FUNCTION is_compare_inter_type(p_rule_type VARCHAR2)
		RETURN NUMBER AS
		output NUMBER(1, 0) := 0;
		BEGIN
			IF p_rule_type = 'ICMP'
			THEN
				output := 1;
			END IF;
			RETURN output;
		END is_compare_inter_type;
END;
/

CREATE OR REPLACE PACKAGE brg_manager AS
	PROCEDURE execute_apply_rule(
		p_type         IN VARCHAR2,
		p_entity       IN VARCHAR2,
		p_attribute    IN VARCHAR2,
		p_name         IN VARCHAR2 DEFAULT NULL,
		p_category_id  IN NUMBER DEFAULT NULL,
		p_r_negate     IN NUMBER DEFAULT 0,
		p_ra_comparer1 IN VARCHAR2 DEFAULT NULL,
		p_ra_comparer2 IN VARCHAR2 DEFAULT NULL,
		p_c_operator   IN VARCHAR2 DEFAULT NULL,
		p_ca_comparer  IN VARCHAR2 DEFAULT NULL,
		p_ct_attribute IN VARCHAR2 DEFAULT NULL,
		p_ci_entity    IN VARCHAR2 DEFAULT NULL,
		p_ci_attribute IN VARCHAR2 DEFAULT NULL,
		p_ci_foreach   IN NUMBER DEFAULT 0,
		p_ci_timing    IN VARCHAR2 DEFAULT NULL,
		p_ci_action    IN VARCHAR2 DEFAULT NULL,
		p_ci_fkeys     IN VARCHAR2 DEFAULT NULL,
		p_ci_rkeys     IN VARCHAR2 DEFAULT NULL,
		p_update       IN NUMBER DEFAULT 0
	);
	PROCEDURE apply_rule(
		p_name         IN  VARCHAR2,
		p_category_id  IN  NUMBER,
		p_type         IN  VARCHAR2,
		p_entity       IN  VARCHAR2,
		p_attribute    IN  VARCHAR2,
		v_rule_type_id OUT NUMBER,
		p_update       IN  NUMBER DEFAULT 0
	);
	PROCEDURE apply_rule_range(
		p_rule_type_id IN NUMBER,
		p_negate       IN NUMBER DEFAULT 0,
		p_update       IN NUMBER DEFAULT 0
	);
	PROCEDURE apply_rule_range_attr(
		p_name         IN VARCHAR2,
		p_category_id  IN NUMBER,
		p_entity       IN VARCHAR2,
		p_attribute    IN VARCHAR2,
		p_r_negate     IN NUMBER,
		p_ra_comparer1 IN VARCHAR2,
		p_ra_comparer2 IN VARCHAR2,
		p_update       IN NUMBER DEFAULT 0
	);
	PROCEDURE apply_rule_compare(
		p_rule_type_id IN NUMBER,
		p_operator     IN VARCHAR2,
		p_update       IN NUMBER DEFAULT 0
	);
	PROCEDURE apply_rule_compare_attr(
		p_name        IN VARCHAR2,
		p_category_id IN NUMBER,
		p_entity      IN VARCHAR2,
		p_attribute   IN VARCHAR2,
		p_c_operator  IN VARCHAR2,
		p_ca_comparer IN VARCHAR2,
		p_update      IN NUMBER DEFAULT 0
	);
	PROCEDURE apply_rule_compare_tuple(
		p_name         IN VARCHAR2,
		p_category_id  IN NUMBER,
		p_entity       IN VARCHAR2,
		p_attribute    IN VARCHAR2,
		p_c_operator   IN VARCHAR2,
		p_ct_attribute IN VARCHAR2,
		p_update       IN NUMBER DEFAULT 0
	);
	PROCEDURE apply_rule_compare_inter(
		p_name         IN VARCHAR2,
		p_category_id  IN NUMBER,
		p_entity       IN VARCHAR2,
		p_attribute    IN VARCHAR2,
		p_c_operator   IN VARCHAR2,
		p_ci_entity    IN VARCHAR2,
		p_ci_attribute IN VARCHAR2,
		p_ci_foreach   IN NUMBER,
		p_ci_timing    IN VARCHAR2,
		p_ci_action    IN VARCHAR2,
		p_ci_fkeys     IN VARCHAR2,
		p_ci_rkeys     IN VARCHAR2,
		p_update       IN NUMBER DEFAULT 0
	);
END brg_manager;
/
CREATE OR REPLACE PACKAGE BODY brg_manager AS
	PROCEDURE execute_apply_rule(
		p_type         IN VARCHAR2,
		p_entity       IN VARCHAR2,
		p_attribute    IN VARCHAR2,
		p_name         IN VARCHAR2 DEFAULT NULL,
		p_category_id  IN NUMBER DEFAULT NULL,
		p_r_negate     IN NUMBER DEFAULT 0,
		p_ra_comparer1 IN VARCHAR2 DEFAULT NULL,
		p_ra_comparer2 IN VARCHAR2 DEFAULT NULL,
		p_c_operator   IN VARCHAR2 DEFAULT NULL,
		p_ca_comparer  IN VARCHAR2 DEFAULT NULL,
		p_ct_attribute IN VARCHAR2 DEFAULT NULL,
		p_ci_entity    IN VARCHAR2 DEFAULT NULL,
		p_ci_attribute IN VARCHAR2 DEFAULT NULL,
		p_ci_foreach   IN NUMBER DEFAULT 0,
		p_ci_timing    IN VARCHAR2 DEFAULT NULL,
		p_ci_action    IN VARCHAR2 DEFAULT NULL,
		p_ci_fkeys     IN VARCHAR2 DEFAULT NULL,
		p_ci_rkeys     IN VARCHAR2 DEFAULT NULL,
		p_update       IN NUMBER DEFAULT 0
	) AS
		BEGIN
			IF p_type = 'ARNG'
			THEN
				brg_manager.apply_rule_range_attr(
					p_name, p_category_id, p_entity, p_attribute, p_r_negate, p_ra_comparer1, p_ra_comparer2, p_update
				);
			ELSIF p_type = 'ACMP'
				THEN
					brg_manager.apply_rule_compare_attr(
						p_name, p_category_id, p_entity, p_attribute, p_c_operator, p_ca_comparer, p_update
					);
			ELSIF p_type = 'TCMP'
				THEN
					brg_manager.apply_rule_compare_tuple(
						p_name, p_category_id, p_entity, p_attribute, p_c_operator, p_ct_attribute, p_update
					);
			ELSIF p_type = 'ICMP'
				THEN
					brg_manager.apply_rule_compare_inter(
						p_name, p_category_id, p_entity, p_attribute, p_c_operator, p_ci_entity, p_ci_attribute,
						p_ci_foreach, p_ci_timing, p_ci_action, p_ci_fkeys, p_ci_rkeys, p_update
					);
			END IF;
		END execute_apply_rule;

	PROCEDURE apply_rule(
		p_name         IN  VARCHAR2,
		p_category_id  IN  NUMBER,
		p_type         IN  VARCHAR2,
		p_entity       IN  VARCHAR2,
		p_attribute    IN  VARCHAR2,
		v_rule_type_id OUT NUMBER,
		p_update       IN  NUMBER DEFAULT 0
	) AS
		v_count       NUMBER := 0;
		v_category_id CATEGORY."ID"%TYPE;
		BEGIN
			SELECT count(*)
			INTO v_count
			FROM CATEGORY
			WHERE "NAME" = p_name;

			IF v_count = 0 AND p_name IS NOT NULL
			THEN
				IF p_update = 0
				THEN
					SELECT CATEGORY_SEQ.nextval
					INTO
						v_category_id
					FROM dual;
					INSERT INTO CATEGORY C (C."ID", C."NAME")
					VALUES (v_category_id, p_name);
				ELSE
					UPDATE (SELECT
								c.NAME         as name,
								rt.CATEGORY_ID as cat_id
							FROM CATEGORY c
								JOIN RULE_TYPE rt
									ON c.ID = rt.CATEGORY_ID
							WHERE rt.ID = p_update) j
					SET j.name = p_name
					WHERE j.cat_id = p_category_id;
				END IF;
			ELSE
				SELECT p_category_id
				INTO v_category_id
				FROM dual;
			END IF;

			IF p_update = 0
			THEN
				SELECT RULE_TYPE_SEQ.nextval
				INTO
					v_rule_type_id
				FROM dual;
				INSERT INTO RULE_TYPE RT (RT."ID", RT."CATEGORY_ID", RT."TYPE", RT."ENTITY", RT."ATTRIBUTE")
				VALUES (v_rule_type_id, v_category_id, p_type, p_entity, p_attribute);
			ELSE
				IF v_count = 0 AND p_name IS NOT NULL
				THEN
					UPDATE RULE_TYPE
					SET
						ENTITY    = p_entity,
						ATTRIBUTE = p_attribute
					WHERE ID = p_update;
				ELSE
					UPDATE RULE_TYPE
					SET
						CATEGORY_ID = p_category_id,
						ENTITY      = p_entity,
						ATTRIBUTE   = p_attribute
					WHERE ID = p_update;
				END IF;

			END IF;

			COMMIT;

		END apply_rule;

	-- Rule range --

	PROCEDURE apply_rule_range(
		p_rule_type_id IN NUMBER,
		p_negate       IN NUMBER DEFAULT 0,
		p_update       IN NUMBER DEFAULT 0
	) AS
		BEGIN
			IF p_update = 0
			THEN
				INSERT INTO RULE_RANGE ("RULE_TYPE_ID", "NEGATE")
				VALUES (p_rule_type_id, p_negate);
			ELSE
				UPDATE RULE_RANGE
				SET
					NEGATE = p_negate
				WHERE RULE_TYPE_ID = p_update;
			END IF;
		END apply_rule_range;

	PROCEDURE apply_rule_range_attr(
		p_name         IN VARCHAR2,
		p_category_id  IN NUMBER,
		p_entity       IN VARCHAR2,
		p_attribute    IN VARCHAR2,
		p_r_negate     IN NUMBER,
		p_ra_comparer1 IN VARCHAR2,
		p_ra_comparer2 IN VARCHAR2,
		p_update       IN NUMBER DEFAULT 0
	) AS
		v_rule_type_id RULE_TYPE."ID"%TYPE;
		BEGIN
			brg_manager.apply_rule(p_name, p_category_id, 'ARNG', p_entity, p_attribute, v_rule_type_id, p_update);
			brg_manager.apply_rule_range(v_rule_type_id, p_r_negate, p_update);
			IF p_update = 0
			THEN
				INSERT INTO RULE_RANGE_ATTRIBUTE ("RULE_TYPE_ID", "COMPARER1", "COMPARER2")
				VALUES (v_rule_type_id, p_ra_comparer1, p_ra_comparer2);
			ELSE
				UPDATE RULE_RANGE_ATTRIBUTE
				SET
					COMPARER1 = p_ra_comparer1,
					COMPARER2 = p_ra_comparer2
				WHERE RULE_TYPE_ID = p_update;
			END IF;
		END apply_rule_range_attr;

	-- Rule compare --

	PROCEDURE apply_rule_compare(
		p_rule_type_id IN NUMBER,
		p_operator     IN VARCHAR2,
		p_update       IN NUMBER DEFAULT 0
	) AS
		BEGIN
			IF p_update = 0
			THEN
				INSERT INTO RULE_COMPARE ("RULE_TYPE_ID", "OPERATOR")
				VALUES (p_rule_type_id, p_operator);
			ELSE
				UPDATE RULE_COMPARE
				SET
					OPERATOR = p_operator
				WHERE RULE_TYPE_ID = p_update;
			END IF;
		END apply_rule_compare;

	PROCEDURE apply_rule_compare_attr(
		p_name        IN VARCHAR2,
		p_category_id IN NUMBER,
		p_entity      IN VARCHAR2,
		p_attribute   IN VARCHAR2,
		p_c_operator  IN VARCHAR2,
		p_ca_comparer IN VARCHAR2,
		p_update      IN NUMBER DEFAULT 0
	) AS
		v_rule_type_id RULE_TYPE."ID"%TYPE;
		BEGIN
			brg_manager.apply_rule(p_name, p_category_id, 'ACMP', p_entity, p_attribute, v_rule_type_id, p_update);
			brg_manager.apply_rule_compare(v_rule_type_id, p_c_operator, p_update);
			IF p_update = 0
			THEN
				INSERT INTO RULE_COMPARE_ATTRIBUTE ("RULE_TYPE_ID", "COMPARER")
				VALUES (v_rule_type_id, p_ca_comparer);
			ELSE
				UPDATE RULE_COMPARE_ATTRIBUTE
				SET
					COMPARER = p_ca_comparer
				WHERE RULE_TYPE_ID = p_update;
			END IF;
		END apply_rule_compare_attr;

	PROCEDURE apply_rule_compare_tuple(
		p_name         IN VARCHAR2,
		p_category_id  IN NUMBER,
		p_entity       IN VARCHAR2,
		p_attribute    IN VARCHAR2,
		p_c_operator   IN VARCHAR2,
		p_ct_attribute IN VARCHAR2,
		p_update       IN NUMBER DEFAULT 0
	) AS
		v_rule_type_id RULE_TYPE."ID"%TYPE;
		BEGIN
			brg_manager.apply_rule(p_name, p_category_id, 'TCMP', p_entity, p_attribute, v_rule_type_id, p_update);
			brg_manager.apply_rule_compare(v_rule_type_id, p_c_operator, p_update);
			IF p_update = 0
			THEN
				INSERT INTO RULE_COMPARE_TUPLE ("RULE_TYPE_ID", "ATTRIBUTE")
				VALUES (v_rule_type_id, p_ct_attribute);
			ELSE
				UPDATE RULE_COMPARE_TUPLE
				SET
					ATTRIBUTE = p_attribute
				WHERE RULE_TYPE_ID = p_update;
			END IF;
		END apply_rule_compare_tuple;

	PROCEDURE apply_rule_compare_inter(
		p_name         IN VARCHAR2,
		p_category_id  IN NUMBER,
		p_entity       IN VARCHAR2,
		p_attribute    IN VARCHAR2,
		p_c_operator   IN VARCHAR2,
		p_ci_entity    IN VARCHAR2,
		p_ci_attribute IN VARCHAR2,
		p_ci_foreach   IN NUMBER,
		p_ci_timing    IN VARCHAR2,
		p_ci_action    IN VARCHAR2,
		p_ci_fkeys     IN VARCHAR2,
		p_ci_rkeys     IN VARCHAR2,
		p_update       IN NUMBER DEFAULT 0
	) AS
		v_rule_type_id RULE_TYPE."ID"%TYPE;
		BEGIN
			brg_manager.apply_rule(p_name, p_category_id, 'ICMP', p_entity, p_attribute, v_rule_type_id, p_update);
			brg_manager.apply_rule_compare(v_rule_type_id, p_c_operator, p_update);
			IF p_update = 0
			THEN
				INSERT INTO RULE_COMPARE_INTER ("RULE_TYPE_ID", "ENTITY", "ATTRIBUTE", "FOREACH", "TIMING", "ACTION", "FOREIGN_KEYS", "REFERENCE_KEYS")
				VALUES (v_rule_type_id, p_ci_entity, p_ci_attribute, p_ci_foreach, p_ci_timing, p_ci_action, p_ci_fkeys,
						p_ci_rkeys);
			ELSE
				UPDATE RULE_COMPARE_INTER
				SET
					ENTITY         = p_ci_entity,
					ATTRIBUTE      = p_ci_attribute,
					FOREACH        = p_ci_foreach,
					TIMING         = p_ci_timing,
					ACTION         = p_ci_action,
					FOREIGN_KEYS   = p_ci_fkeys,
					REFERENCE_KEYS = p_ci_rkeys
				WHERE RULE_TYPE_ID = p_update;
			END IF;
		END apply_rule_compare_inter;
END;
/


-- TABLES --

CREATE TABLE "CATEGORY" (
	"ID"   NUMBER(9, 0) NOT NULL ENABLE,
	"NAME" VARCHAR2(16) NOT NULL ENABLE,
	CONSTRAINT "CATEGORY_PK" PRIMARY KEY ("ID")
		USING INDEX ENABLE,
	CONSTRAINT "CATEGORY_NAME_UNQ" UNIQUE ("NAME")
		USING INDEX ENABLE,
	CONSTRAINT "CATEGORY_NAME_REGEXP" CHECK (REGEXP_LIKE("NAME", '^[A-Z_][A-Z0-9_]*[A-Z0-9]$', 'i')) ENABLE
)
/
CREATE TABLE "RULE_TYPE" (
	"ID"          NUMBER(9, 0)  NOT NULL ENABLE,
	"CATEGORY_ID" NUMBER(9, 0)  NOT NULL ENABLE,
	"TYPE"        VARCHAR2(4)   NOT NULL ENABLE,
	"ENTITY"      VARCHAR2(128) NOT NULL ENABLE,
	"ATTRIBUTE"   VARCHAR2(128) NOT NULL ENABLE,
	CONSTRAINT "RULE_TYPE_PK" PRIMARY KEY ("ID")
		USING INDEX ENABLE,
	CONSTRAINT "RT_TYPE_IN_CHK" CHECK ("TYPE" IN ('ACMP', 'TCMP', 'ICMP', 'ARNG')) ENABLE,
	CONSTRAINT "RT_ENTITY_REGEX_CHK" CHECK (REGEXP_LIKE("ENTITY", '^[A-Z_][A-Z0-9_]*$', 'i')) ENABLE,
	CONSTRAINT "RT_ATTR_REGEX_CHK" CHECK (REGEXP_LIKE("ATTRIBUTE", '^[A-Z_][A-Z0-9_]*$', 'i')) ENABLE,
	FOREIGN KEY ("CATEGORY_ID") REFERENCES "CATEGORY" ("ID") ON DELETE CASCADE ENABLE
)
/
CREATE TABLE "RULE_COMPARE" (
	"RULE_TYPE_ID" NUMBER(9, 0) NOT NULL ENABLE,
	"OPERATOR"     VARCHAR2(2)  NOT NULL ENABLE,
	CONSTRAINT "RULE_COMPARE_PK" PRIMARY KEY ("RULE_TYPE_ID")
		USING INDEX ENABLE,
	CONSTRAINT "RULE_CMP_OPERATOR_IN_CHK" CHECK ("OPERATOR" IN ('=', '!=', '<', '<=', '>', '>=')) ENABLE,
	FOREIGN KEY ("RULE_TYPE_ID") REFERENCES "RULE_TYPE" ("ID") ON DELETE CASCADE ENABLE
)
/
CREATE TABLE "RULE_COMPARE_ATTRIBUTE" (
	"RULE_TYPE_ID" NUMBER(9, 0)  NOT NULL ENABLE,
	"COMPARER"     VARCHAR2(256) NOT NULL ENABLE,
	CONSTRAINT "RULE_COMPARE_ATTRIBUTE_PK" PRIMARY KEY ("RULE_TYPE_ID")
		USING INDEX ENABLE,
	FOREIGN KEY ("RULE_TYPE_ID") REFERENCES "RULE_TYPE" ("ID") ON DELETE CASCADE ENABLE
)
/
CREATE TABLE "RULE_COMPARE_TUPLE" (
	"RULE_TYPE_ID" NUMBER(9, 0)  NOT NULL ENABLE,
	"ATTRIBUTE"    VARCHAR2(128) NOT NULL ENABLE,
	CONSTRAINT "RULE_COMPARE_TUPLE_PK" PRIMARY KEY ("RULE_TYPE_ID")
		USING INDEX ENABLE,
	CONSTRAINT "RULE_CMPT_ATTR_REGEX_CHK"
	CHECK (REGEXP_LIKE("ATTRIBUTE", '^[A-Z_][A-Z0-9_]*$', 'i')) ENABLE,
	FOREIGN KEY ("RULE_TYPE_ID") REFERENCES "RULE_TYPE" ("ID") ON DELETE CASCADE ENABLE
)
/
CREATE TABLE "RULE_COMPARE_INTER" (
	"RULE_TYPE_ID"   NUMBER(9, 0)  NOT NULL ENABLE,
	"ENTITY"         VARCHAR2(128) NOT NULL ENABLE,
	"ATTRIBUTE"      VARCHAR2(128) NOT NULL ENABLE,
	"FOREIGN_KEYS"   VARCHAR2(512) NOT NULL ENABLE,
	"REFERENCE_KEYS" VARCHAR2(512) NOT NULL ENABLE,
	"TIMING"         VARCHAR2(10)  NOT NULL ENABLE,
	"ACTION"         VARCHAR2(26)  NOT NULL ENABLE,
	"FOREACH"        NUMBER(1, 0)  NOT NULL ENABLE,
	CONSTRAINT "RULE_COMPARE_INTER_PK" PRIMARY KEY ("RULE_TYPE_ID")
		USING INDEX ENABLE,
	CONSTRAINT "RULE_CMPI_ENTITY_REGEX_CHK"
	CHECK (REGEXP_LIKE("ENTITY", '^[A-Z_][A-Z0-9_]*$', 'i')) ENABLE,
	CONSTRAINT "RULE_CMPI_ATTR_REGEX_CHK"
	CHECK (REGEXP_LIKE("ATTRIBUTE", '^[A-Z_][A-Z0-9_]*$', 'i')) ENABLE,
	CONSTRAINT "RULE_CMPI_FKEYS_REGEX_CHK"
	CHECK (REGEXP_LIKE("FOREIGN_KEYS", '^(([A-Z0-9][A-Z0-9_]*)([,][\s]*)?)*[A-Z0-9_]$', 'i')) ENABLE,
	CONSTRAINT "RULE_CMPI_RKEYS_REGEX_CHK"
	CHECK (REGEXP_LIKE("REFERENCE_KEYS", '^(([A-Z0-9][A-Z0-9_]*)([,][\s]*)?)*[A-Z0-9_]$', 'i')) ENABLE,
	CONSTRAINT "RULE_CMPI_TIMING_IN_CHK"
	CHECK ("TIMING" IN ('BEFORE', 'AFTER', 'INSTEAD OF')) ENABLE,
	CONSTRAINT "RULE_CMPI_ACTION_IN_CHK"
	CHECK ("ACTION" IN
		   ('CREATE', 'CREATE OR UPDATE', 'CREATE OR UPDATE OR DELETE', 'UPDATE', 'UPDATE OR DELETE', 'DELETE')) ENABLE,
	FOREIGN KEY ("RULE_TYPE_ID") REFERENCES "RULE_TYPE" ("ID") ON DELETE CASCADE ENABLE
)
/
CREATE TABLE "RULE_RANGE" (
	"RULE_TYPE_ID" NUMBER(9, 0) NOT NULL ENABLE,
	"NEGATE"       NUMBER(1, 0) NOT NULL ENABLE,
	CONSTRAINT "RULE_RANGE_PK" PRIMARY KEY ("RULE_TYPE_ID")
		USING INDEX ENABLE,
	FOREIGN KEY ("RULE_TYPE_ID") REFERENCES "RULE_TYPE" ("ID") ON DELETE CASCADE ENABLE
)
/
CREATE TABLE "RULE_RANGE_ATTRIBUTE" (
	"RULE_TYPE_ID" NUMBER(9, 0)  NOT NULL ENABLE,
	"COMPARER1"    VARCHAR2(256) NOT NULL ENABLE,
	"COMPARER2"    VARCHAR2(256) NOT NULL ENABLE,
	CONSTRAINT "RULE_RANGE_ATTRIBUTE_PK" PRIMARY KEY ("RULE_TYPE_ID")
		USING INDEX ENABLE,
	FOREIGN KEY ("RULE_TYPE_ID") REFERENCES "RULE_TYPE" ("ID") ON DELETE CASCADE ENABLE
)
/


-- VIEWS --

CREATE OR REPLACE VIEW "RULES" AS
	SELECT
		rt.*,
		cat.name           AS name,
		-- Compare Rules
		rc.operator        AS compare_operator,
		rca.comparer       AS c_attribute_comparer,
		rct.attribute      AS c_tuple_attribute,
		rci.entity         AS c_inter_entity,
		rci.attribute      AS c_inter_attribute,
		rci.foreach        AS c_inter_foreach,
		rci.timing         AS c_inter_timing,
		rci.action         AS c_inter_action,
		rci.foreign_keys   AS c_inter_fkeys,
		rci.reference_keys AS c_inter_rkeys,

		-- Range Rules
		rr.negate          AS range_negate,
		rra.comparer1      AS r_attribute_comparer1,
		rra.comparer2      AS r_attribute_comparer2
	FROM rule_type rt
		LEFT OUTER JOIN category cat ON cat.id = rt.category_id
		LEFT OUTER JOIN rule_compare rc ON rt.id = rc.rule_type_id
		LEFT OUTER JOIN rule_range rr ON rt.id = rr.rule_type_id
		LEFT OUTER JOIN rule_range_attribute rra ON rt.id = rra.rule_type_id
		LEFT OUTER JOIN rule_compare_attribute rca ON rt.id = rca.rule_type_id
		LEFT OUTER JOIN rule_compare_tuple rct ON rt.id = rct.rule_type_id
		LEFT OUTER JOIN rule_compare_inter rci ON rt.id = rci.rule_type_id
/


-- TYPES --

CREATE TYPE T_NUMBERS AS TABLE OF NUMBER
/


-- SEQUENCES --

CREATE SEQUENCE "CATEGORY_SEQ"
	MINVALUE 1
	MAXVALUE 9999999999999999999999999999
	INCREMENT BY 1
	START WITH 1
	CACHE 20
	NOORDER
	NOCYCLE
/
CREATE SEQUENCE "RULE_TYPE_SEQ"
	MINVALUE 1
	MAXVALUE 9999999999999999999999999999
	INCREMENT BY 1
	START WITH 1
	CACHE 20
	NOORDER
	NOCYCLE
/


-- TRIGGERS --

CREATE OR REPLACE TRIGGER "BI_CATEGORY"
	BEFORE INSERT
	ON "CATEGORY"
	FOR EACH ROW
	BEGIN
		IF :NEW."ID" IS NULL
		THEN
			SELECT "CATEGORY_SEQ".nextval
			INTO :NEW."ID"
			FROM sys.dual;
		END IF;
	END;
/
CREATE OR REPLACE TRIGGER "BI_RULE_TYPE"
	BEFORE INSERT
	ON "RULE_TYPE"
	FOR EACH ROW
	BEGIN
		IF :NEW."ID" IS NULL
		THEN
			SELECT "RULE_TYPE_SEQ".nextval
			INTO :NEW."ID"
			FROM sys.dual;
		END IF;
	END;
/
CREATE OR REPLACE TRIGGER "BI_RULE_COMPARE_INTER_EAK"
	BEFORE INSERT OR UPDATE
	ON "RULE_COMPARE_INTER"
	FOR EACH ROW
	DECLARE
			ex_constraint_violation EXCEPTION;
	BEGIN
		IF brg_constraints.equal_amount_keys(:NEW."FOREIGN_KEYS", :NEW."REFERENCE_KEYS") <= 0
		THEN
			ROLLBACK;
			RAISE ex_constraint_violation;
		END IF;
		EXCEPTION
		WHEN ex_constraint_violation
		THEN
			RAISE_APPLICATION_ERROR(-20002, 'Constraint violation: amount of keys is not equal.');
	END;