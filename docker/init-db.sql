-- Initialize database for Docker container
-- This script runs when PostgreSQL container starts for the first time

-- Create additional indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_enabled ON users(enabled);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Create indexes for roles
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles(name);

-- Create composite index for user_roles
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);

-- Grant necessary permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO epti_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO epti_user;
