CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (username, email, password)
VALUES ('demo', 'demo@bookmanager.local', crypt('demo123', gen_salt('bf', 12)))
ON CONFLICT (email) DO NOTHING;

INSERT INTO books (title, author, year, description, user_id)
SELECT v.title, v.author, v.year, v.description, u.id
FROM (
    VALUES
        ('1984', 'George Orwell', 1949,
         'Distopia clássica sobre vigilância totalitária e controle do pensamento.'),
        ('Dom Casmurro', 'Machado de Assis', 1899,
         'Retrato da dúvida e do ciúme narrado por Bentinho sobre Capitu.'),
        ('O Senhor dos Anéis', 'J.R.R. Tolkien', 1954,
         'Epopeia fantástica sobre a jornada para destruir o Um Anel.'),
        ('Clean Code', 'Robert C. Martin', 2008,
         'Princípios e práticas para escrever código legível e sustentável.'),
        ('O Pequeno Príncipe', 'Antoine de Saint-Exupéry', 1943,
         'Fábula poética sobre amizade, responsabilidade e o essencial invisível aos olhos.'),
        ('Cem Anos de Solidão', 'Gabriel García Márquez', 1967,
         'Saga mágica da família Buendía na fictícia Macondo.'),
        ('O Hobbit', 'J.R.R. Tolkien', 1937,
         'Aventura de Bilbo Bolseiro rumo à Montanha Solitária.'),
        ('A Revolução dos Bichos', 'George Orwell', 1945,
         'Sátira política sobre poder, propaganda e corrupção.'),
        ('Orgulho e Preconceito', 'Jane Austen', 1813,
         'Romance de costumes sobre classe social, orgulho e reconciliação.'),
        ('Duna', 'Frank Herbert', 1965,
         'Ficção científica épica de intriga política e ecologia no planeta Arrakis.'),
        ('O Nome da Rosa', 'Umberto Eco', 1980,
         'Mistério medieval em um mosteiro italiano do século XIV.'),
        ('Crime e Castigo', 'Fiódor Dostoiévski', 1866,
         'Drama psicológico sobre culpa, moral e redenção em São Petersburgo.'),
        ('Harry Potter e a Pedra Filosofal', 'J.K. Rowling', 1997,
         'O início da saga do jovem bruxo na Escola de Magia e Bruxaria de Hogwarts.'),
        ('O Alquimista', 'Paulo Coelho', 1988,
         'Jornada de autodescoberta de um pastor andaluz em busca de um tesouro.'),
        ('Fundação', 'Isaac Asimov', 1951,
         'Primeiro volume da série sobre o declínio e a reconstrução de um império galáctico.'),
        ('Neuromancer', 'William Gibson', 1984,
         'Romance cyberpunk que consolidou o gênero na ficção científica.'),
        ('A Metamorfose', 'Franz Kafka', 1915,
         'Relato sobre alienação quando Gregor Samsa acorda transformado em inseto.'),
        ('Memórias Póstumas de Brás Cubas', 'Machado de Assis', 1881,
         'Romance narrado por um defunto autor de suas próprias memórias irônicas.'),
        ('O Cortiço', 'Aluísio Azevedo', 1890,
         'Retrato naturalista da vida e das tensões sociais em um cortiço carioca.'),
        ('Sapiens', 'Yuval Noah Harari', 2011,
         'Panorama da história da humanidade desde a pré-história até o presente.')
) AS v(title, author, year, description)
CROSS JOIN (
    SELECT id FROM users WHERE email = 'demo@bookmanager.local'
) AS u;
