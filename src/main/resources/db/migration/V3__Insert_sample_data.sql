-- Insert sample products
INSERT INTO products (name, description, price, image_url, available) VALUES 
('Camiseta EPTI 2024', 'Camiseta oficial do evento EPTI 2024', 45.00, 'https://example.com/images/camiseta.jpg', TRUE),
('Caneca EPTI', 'Caneca cerâmica com logo EPTI', 25.00, 'https://example.com/images/caneca.jpg', TRUE),
('Adesivo EPTI', 'Pacote com 5 adesivos oficiais', 10.00, 'https://example.com/images/adesivo.jpg', TRUE),
('Squeeze EPTI', 'Garrafa térmica 500ml', 35.00, 'https://example.com/images/squeeze.jpg', TRUE),
('Mochila EPTI', 'Mochila escolar com logo EPTI', 60.00, 'https://example.com/images/mochila.jpg', TRUE),
('Caderno EPTI', 'Caderno espiral 200 folhas', 15.00, 'https://example.com/images/caderno.jpg', TRUE),
('Pen Drive EPTI', 'Pen Drive 16GB personalizado', 30.00, 'https://example.com/images/pendrive.jpg', TRUE),
('Boné EPTI', 'Boné ajustável com logo', 28.00, 'https://example.com/images/bone.jpg', TRUE);

-- Insert sample kits
INSERT INTO kits (name, description, price, image_url, available) VALUES 
('Kit Básico EPTI', 'Camiseta + Caneca + Adesivo', 65.00, 'https://example.com/images/kit_basico.jpg', TRUE),
('Kit Completo EPTI', 'Camiseta + Caneca + Adesivo + Squeeze + Mochila', 150.00, 'https://example.com/images/kit_completo.jpg', TRUE),
('Kit Estudante EPTI', 'Caderno + Pen Drive + Boné + Adesivo', 75.00, 'https://example.com/images/kit_estudante.jpg', TRUE),
('Kit Premium EPTI', 'Todos os produtos com desconto especial', 180.00, 'https://example.com/images/kit_premium.jpg', TRUE);

-- Associate products with kits
-- Kit Básico (Camiseta + Caneca + Adesivo)
INSERT INTO kit_products (kit_id, product_id) VALUES 
(1, 1), (1, 2), (1, 3);

-- Kit Completo (Camiseta + Caneca + Adesivo + Squeeze + Mochila)
INSERT INTO kit_products (kit_id, product_id) VALUES 
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5);

-- Kit Estudante (Caderno + Pen Drive + Boné + Adesivo)
INSERT INTO kit_products (kit_id, product_id) VALUES 
(3, 6), (3, 7), (3, 8), (3, 3);

-- Kit Premium (Todos os produtos)
INSERT INTO kit_products (kit_id, product_id) VALUES 
(4, 1), (4, 2), (4, 3), (4, 4), (4, 5), (4, 6), (4, 7), (4, 8);

-- Add turma restrictions for participating turmas only
-- Products available for all participating turmas
INSERT INTO product_turmas (product_id, turma) VALUES 
(1, 'PRIMEIRO_D'), (1, 'SEGUNDO_A'), (1, 'SEGUNDO_D'), (1, 'SEGUNDO_D_MARTA'),
(2, 'PRIMEIRO_D'), (2, 'SEGUNDO_A'), (2, 'SEGUNDO_D'), (2, 'SEGUNDO_D_MARTA'),
(3, 'PRIMEIRO_D'), (3, 'SEGUNDO_A'), (3, 'SEGUNDO_D'), (3, 'SEGUNDO_D_MARTA'),
(4, 'PRIMEIRO_D'), (4, 'SEGUNDO_A'), (4, 'SEGUNDO_D'), (4, 'SEGUNDO_D_MARTA'),
(5, 'PRIMEIRO_D'), (5, 'SEGUNDO_A'), (5, 'SEGUNDO_D'), (5, 'SEGUNDO_D_MARTA'),
(6, 'PRIMEIRO_D'), (6, 'SEGUNDO_A'), (6, 'SEGUNDO_D'), (6, 'SEGUNDO_D_MARTA'),
(7, 'PRIMEIRO_D'), (7, 'SEGUNDO_A'), (7, 'SEGUNDO_D'), (7, 'SEGUNDO_D_MARTA'),
(8, 'PRIMEIRO_D'), (8, 'SEGUNDO_A'), (8, 'SEGUNDO_D'), (8, 'SEGUNDO_D_MARTA');

-- Kits available for all participating turmas
INSERT INTO kit_turmas (kit_id, turma) VALUES 
(1, 'PRIMEIRO_D'), (1, 'SEGUNDO_A'), (1, 'SEGUNDO_D'), (1, 'SEGUNDO_D_MARTA'),
(2, 'PRIMEIRO_D'), (2, 'SEGUNDO_A'), (2, 'SEGUNDO_D'), (2, 'SEGUNDO_D_MARTA'),
(3, 'PRIMEIRO_D'), (3, 'SEGUNDO_A'), (3, 'SEGUNDO_D'), (3, 'SEGUNDO_D_MARTA'),
(4, 'PRIMEIRO_D'), (4, 'SEGUNDO_A'), (4, 'SEGUNDO_D'), (4, 'SEGUNDO_D_MARTA');

-- Update sample admin user to have full name and turma
UPDATE users 
SET 
    full_name = 'Administrador Sistema',
    turma = 'SEGUNDO_A'
WHERE username = 'admin';

-- Insert sample users for testing (if they don't exist)
INSERT INTO users (username, email, password, full_name, turma, enabled) VALUES 
('joao.silva', 'joao.silva32@aluno.ce.gov.br', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'João Silva', 'PRIMEIRO_D', TRUE),
('maria.santos', 'maria.santos45@aluno.ce.gov.br', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Maria Santos', 'SEGUNDO_A', TRUE),
('pedro.oliveira', 'pedro.oliveira28@aluno.ce.gov.br', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Pedro Oliveira', 'SEGUNDO_D', TRUE),
('ana.costa', 'ana.costa33@aluno.ce.gov.br', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Ana Costa', 'SEGUNDO_D_MARTA', TRUE)
ON CONFLICT (username) DO NOTHING;

-- Assign USER role to sample users
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username IN ('joao.silva', 'maria.santos', 'pedro.oliveira', 'ana.costa') 
AND r.name = 'USER'
ON CONFLICT DO NOTHING;
