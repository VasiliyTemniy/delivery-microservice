CREATE TABLE IF NOT EXISTS delivery.order_trackings
(
    id                          UUID            DEFAULT uuid_generate_v4() PRIMARY KEY,
    order_id                    VARCHAR(36)     NOT NULL,
    point_number                INTEGER         NOT NULL,
    from_facility_id            VARCHAR(36)     NOT NULL,
    destination_id              VARCHAR(36)     NOT NULL,
    destination_type            VARCHAR(60)     NOT NULL,
    carrier_id                  VARCHAR(36)     NOT NULL,
    status                      VARCHAR(60)     NOT NULL,
    delivery_cost               INTEGER         NOT NULL,
    currency                    VARCHAR(3)      NOT NULL,
    currency_decimal_multiplier INTEGER         NOT NULL,
    mass_control_value          INTEGER,
    mass_measure                VARCHAR(60),
    lat                         DOUBLE PRECISION,
    lon                         DOUBLE PRECISION,
    estimated_delivery_at       TIMESTAMP WITH TIME ZONE,
    delivered_at                TIMESTAMP WITH TIME ZONE,

    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP
);

-- Calculate the default value for point_number based on the existing records for the same order_id
CREATE OR REPLACE FUNCTION calculate_default_point_number()
    RETURNS TRIGGER
    LANGUAGE plpgsql AS
$func$
BEGIN
  IF NEW.point_number IS NOT NULL THEN
    RETURN NEW;
  END IF;

  NEW.point_number = COALESCE((
      SELECT MAX(point_number) + 1
      FROM delivery.order_trackings
      WHERE order_id = NEW.order_id
  ), 0);

  RETURN NEW;
END;
$func$;

-- Set the default value for point_number based on the existing records for the same order_id
CREATE TRIGGER set_default_point_number
BEFORE INSERT ON delivery.order_trackings
FOR EACH ROW
EXECUTE FUNCTION calculate_default_point_number();


-- Update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_timestamp()
    RETURNS TRIGGER
    LANGUAGE plpgsql AS
$func$
BEGIN
    NEW.updated_at = transaction_timestamp();
    RETURN NEW;
END;
$func$;

CREATE TRIGGER update_order_trackings_modtime
BEFORE UPDATE ON delivery.order_trackings
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();


-- Create a unique index on order_id and point_number
CREATE UNIQUE INDEX IF NOT EXISTS order_trackings_order_id_point_number_idx ON delivery.order_trackings (order_id, point_number);