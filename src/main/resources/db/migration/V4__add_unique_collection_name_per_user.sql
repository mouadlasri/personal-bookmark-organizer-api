ALTER TABLE collections
ADD CONSTRAINT uq_collections_user_name UNIQUE (user_id, name);