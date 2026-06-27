ALTER TABLE paintings DROP COLUMN original_available;
ALTER TABLE paintings DROP COLUMN original_price;
ALTER TABLE paintings ADD COLUMN description TEXT;
