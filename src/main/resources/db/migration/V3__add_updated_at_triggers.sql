CREATE OR REPLACE FUNCTION set_updated_at()
       RETURNS TRIGGER AS $$
       BEGIN
            NEW.updated_at = NOW();
            return NEW;
       END;
       $$ LANGUAGE plpgsql;


CREATE TRIGGER handle_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER handle_collections_updated_at
BEFORE UPDATE ON collections
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER handle_bookmarks_updated_at
BEFORE UPDATE ON bookmarks
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER handle_tags_updated_at
BEFORE UPDATE ON tags
FOR EACH ROW EXECUTE FUNCTION set_updated_at();




