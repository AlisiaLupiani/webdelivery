DROP DATABASE IF EXISTS WEB_DELIVERY;
CREATE DATABASE WEB_DELIVERY CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE WEB_DELIVERY;

CREATE TABLE UTENTE (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    NOME VARCHAR(100) NOT NULL,
    COGNOME VARCHAR(100) NOT NULL,
    EMAIL VARCHAR(150) NOT NULL UNIQUE,
    PASSWORD VARCHAR(255) NOT NULL,
    RUOLO ENUM('CLIENTE', 'ADMIN', 'STAFF') DEFAULT 'CLIENTE',
    INDIRIZZO VARCHAR(255) DEFAULT NULL,
    TELEFONO VARCHAR(20) DEFAULT NULL,
    VERSION INT NOT NULL DEFAULT 1
);

CREATE TABLE GRUPPO (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    NOME VARCHAR(100) NOT NULL,
    SCELTA_SINGOLA BOOLEAN NOT NULL DEFAULT FALSE,
    VERSION INT NOT NULL DEFAULT 1
);

CREATE TABLE INGREDIENTE (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    NOME VARCHAR(100) NOT NULL,
    VERSION INT NOT NULL DEFAULT 1
);

CREATE TABLE PRODOTTO (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    NOME VARCHAR(150) NOT NULL,
    DESCRIZIONE TEXT,
    PREZZO DECIMAL(10, 2) NOT NULL,
    PROCEDURA TEXT,
    TEMPO_PREPARAZIONE INT,
    IMMAGINE VARCHAR(255),
    CATEGORIA VARCHAR(80) NOT NULL DEFAULT 'Altro',
    VERSION INT NOT NULL DEFAULT 1
);

CREATE TABLE CARATTERISTICA (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    NOME VARCHAR(100) NOT NULL,
    DESCRIZIONE TEXT,
    PREZZO DECIMAL(10, 2) DEFAULT 0.00,
    IS_DEFAULT BOOLEAN DEFAULT FALSE,
    GRUPPO_ID INT NOT NULL,
    VERSION INT NOT NULL DEFAULT 1,
    FOREIGN KEY (GRUPPO_ID) REFERENCES GRUPPO(ID) ON DELETE CASCADE
);

CREATE TABLE ORDINE (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    DATA_ORDINE DATE NOT NULL,
    ORARIO_CONSEGNA TIME,
    PREZZO_TOTALE DECIMAL(10, 2) NOT NULL,
    STATO VARCHAR(50) NOT NULL,
    METODO_PAGAMENTO VARCHAR(50),
    INDIRIZZO_CONSEGNA VARCHAR(255) NOT NULL,
    UTENTE_ID INT NOT NULL,
    VERSION INT NOT NULL DEFAULT 1,
    FOREIGN KEY (UTENTE_ID) REFERENCES UTENTE(ID) ON DELETE CASCADE
);

CREATE TABLE CONSUMAZIONE (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    NOME VARCHAR(100) NOT NULL,
    DESCRIZIONE TEXT,
    PRODOTTO_ID INT,
    VERSION INT NOT NULL DEFAULT 1,
    FOREIGN KEY (PRODOTTO_ID) REFERENCES PRODOTTO(ID) ON DELETE SET NULL
);

CREATE TABLE LOG_STATO (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP,
    STATO_FROM VARCHAR(50),
    STATO_TO VARCHAR(50),
    ORDINE_ID INT NOT NULL,
    UTENTE_ID INT NOT NULL,
    VERSION INT NOT NULL DEFAULT 1,
    FOREIGN KEY (ORDINE_ID) REFERENCES ORDINE(ID) ON DELETE CASCADE,
    FOREIGN KEY (UTENTE_ID) REFERENCES UTENTE(ID) ON DELETE CASCADE
);

CREATE TABLE PRODOTTO_INGREDIENTE (
    PRODOTTO_ID INT NOT NULL,
    INGREDIENTE_ID INT NOT NULL,
    QUANTITA VARCHAR(50),
    PRIMARY KEY (PRODOTTO_ID, INGREDIENTE_ID),
    FOREIGN KEY (PRODOTTO_ID) REFERENCES PRODOTTO(ID) ON DELETE CASCADE,
    FOREIGN KEY (INGREDIENTE_ID) REFERENCES INGREDIENTE(ID) ON DELETE CASCADE
);

CREATE TABLE PRODOTTO_CARATTERISTICA (
    PRODOTTO_ID INT NOT NULL,
    CARATTERISTICA_ID INT NOT NULL,
    PRIMARY KEY (PRODOTTO_ID, CARATTERISTICA_ID),
    FOREIGN KEY (PRODOTTO_ID) REFERENCES PRODOTTO(ID) ON DELETE CASCADE,
    FOREIGN KEY (CARATTERISTICA_ID) REFERENCES CARATTERISTICA(ID) ON DELETE CASCADE
);

CREATE TABLE ORDINE_PRODOTTO (
    ORDINE_ID INT NOT NULL,
    PRODOTTO_ID INT NOT NULL,
    QUANTITA INT DEFAULT 1,
    PRIMARY KEY (ORDINE_ID, PRODOTTO_ID),
    FOREIGN KEY (ORDINE_ID) REFERENCES ORDINE(ID) ON DELETE CASCADE,
    FOREIGN KEY (PRODOTTO_ID) REFERENCES PRODOTTO(ID) ON DELETE CASCADE
);

CREATE TABLE CARRELLO (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    UTENTE_ID INT NOT NULL,
    STATO VARCHAR(30) NOT NULL DEFAULT 'ATTIVO',
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    VERSION INT NOT NULL DEFAULT 1,
    FOREIGN KEY (UTENTE_ID) REFERENCES UTENTE(ID) ON DELETE CASCADE,
    INDEX IDX_CARRELLO_UTENTE_STATO (UTENTE_ID, STATO)
);

CREATE TABLE CARRELLO_PRODOTTO (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    CARRELLO_ID INT NOT NULL,
    PRODOTTO_ID INT NOT NULL,
    QUANTITA INT NOT NULL DEFAULT 1,
    PREZZO_UNITARIO DECIMAL(10, 2) NOT NULL,
    VERSION INT NOT NULL DEFAULT 1,
    FOREIGN KEY (CARRELLO_ID) REFERENCES CARRELLO(ID) ON DELETE CASCADE,
    FOREIGN KEY (PRODOTTO_ID) REFERENCES PRODOTTO(ID) ON DELETE CASCADE,
    INDEX IDX_CARRELLO_PRODOTTO_CARRELLO (CARRELLO_ID),
    INDEX IDX_CARRELLO_PRODOTTO_PRODOTTO (PRODOTTO_ID)
);

CREATE TABLE CARRELLO_PRODOTTO_CARATTERISTICA (
    CARRELLO_PRODOTTO_ID INT NOT NULL,
    CARATTERISTICA_ID INT NOT NULL,
    PRIMARY KEY (CARRELLO_PRODOTTO_ID, CARATTERISTICA_ID),
    FOREIGN KEY (CARRELLO_PRODOTTO_ID) REFERENCES CARRELLO_PRODOTTO(ID) ON DELETE CASCADE,
    FOREIGN KEY (CARATTERISTICA_ID) REFERENCES CARATTERISTICA(ID) ON DELETE CASCADE
);
DROP VIEW IF EXISTS ORDINE_PRODOTTO_CARATTERISTICA;
DROP TABLE IF EXISTS ORDINE_PRODOTTO_OPZIONE;

CREATE TABLE ORDINE_PRODOTTO_OPZIONE (
    ORDINE_ID INT NOT NULL,
    PRODOTTO_ID INT NOT NULL,
    OPZIONE_ID INT NOT NULL,

    PRIMARY KEY (ORDINE_ID, PRODOTTO_ID, OPZIONE_ID),

    FOREIGN KEY (ORDINE_ID, PRODOTTO_ID)
        REFERENCES ORDINE_PRODOTTO(ORDINE_ID, PRODOTTO_ID)
        ON DELETE CASCADE,

    FOREIGN KEY (OPZIONE_ID)
        REFERENCES CARATTERISTICA(ID)
        ON DELETE CASCADE
);

CREATE VIEW ORDINE_PRODOTTO_CARATTERISTICA AS
SELECT
    ORDINE_ID,
    PRODOTTO_ID,
    OPZIONE_ID AS CARATTERISTICA_ID
FROM ORDINE_PRODOTTO_OPZIONE;


INSERT INTO UTENTE (ID, NOME, COGNOME, EMAIL, PASSWORD, RUOLO, INDIRIZZO, TELEFONO) VALUES
(1, 'Rachid', 'Nhachi', 'rachid@webdelivery.it', 'admin123', 'ADMIN', 'Via Sede Centrale, 1', '3330000001'),
(2, 'Mario', 'Rossi', 'mario.rossi@email.it', 'cliente123', 'CLIENTE', 'Via Roma, 10', '3339876543'),
(3, 'Luigi', 'Verdi', 'luigi.verdi@email.it', 'cliente123', 'CLIENTE', 'Piazza Napoli, 5', '3335555555'),
(4, 'Giulia', 'Staff', 'giulia.staff@webdelivery.it', 'staff123', 'STAFF', 'Via Cucina, 2', '3334444444'),
(5, 'Alisia', 'Lupiani', 'alisia@webdelivery.it', 'admin123', 'ADMIN', 'Via Sede Centrale, 2', '3330000002'),
(6, 'Alessandro', 'Casasanta', 'alessandro@webdelivery.it', 'admin123', 'ADMIN', 'Via Sede Centrale, 3', '3330000003');

INSERT INTO INGREDIENTE (ID, NOME) VALUES
(1, 'Salsa di Pomodoro'),
(2, 'Mozzarella di Bufala'),
(3, 'Basilico Fresco'),
(4, 'Salame Piccante'),
(5, 'Prosciutto Cotto'),
(6, 'Funghi Champignon'),
(7, 'Carciofini'),
(8, 'Olive Nere'),
(9, 'Pane Casereccio'),
(10, 'Pomodorini Freschi'),
(11, 'Aglio'),
(12, 'Olio EVO'),
(13, 'Salumi Misti'),
(14, 'Formaggi Misti'),
(15, 'Miele'),
(16, 'Noci'),
(17, 'Patate'),
(18, 'Olio per Frittura'),
(19, 'Sale'),
(20, 'Pangrattato'),
(21, 'Uova'),
(22, 'Riso'),
(23, 'Ragu di Carne'),
(24, 'Piselli'),
(25, 'Sfoglia Fresca'),
(26, 'Besciamella'),
(27, 'Parmigiano'),
(28, 'Spaghetti'),
(29, 'Guanciale'),
(30, 'Pecorino Romano'),
(31, 'Pepe Nero'),
(32, 'Fior di Latte'),
(33, 'Mortadella'),
(34, 'Pistacchio'),
(35, 'Burrata'),
(36, 'Pane Burger'),
(37, 'Hamburger di Manzo'),
(38, 'Cheddar'),
(39, 'Bacon'),
(40, 'Salsa BBQ'),
(41, 'Pane Saltimbocca'),
(42, 'Salsiccia'),
(43, 'Friarielli'),
(44, 'Piadina'),
(45, 'Prosciutto Crudo'),
(46, 'Squacquerone'),
(47, 'Rucola'),
(48, 'Focaccia'),
(49, 'Provola'),
(50, 'Mascarpone'),
(51, 'Savoiardi'),
(52, 'Caffe'),
(53, 'Cacao'),
(54, 'Biscotti'),
(55, 'Formaggio Cremoso'),
(56, 'Zucchero'),
(57, 'Coca Cola'),
(58, 'Birra Bionda'),
(59, 'Acqua Naturale');

INSERT INTO GRUPPO (ID, NOME, SCELTA_SINGOLA) VALUES
(1, 'Tipo di Impasto', TRUE),
(2, 'Aggiunte Extra', FALSE),
(3, 'Scegli il Gusto', TRUE),
(4, 'Salse Extra', FALSE),
(5, 'Cottura Carne', TRUE),
(6, 'Opzioni Bevande', TRUE);

INSERT INTO CARATTERISTICA (ID, NOME, DESCRIZIONE, PREZZO, IS_DEFAULT, GRUPPO_ID) VALUES
(1, 'Impasto Classico', 'Il nostro impasto tradizionale', 0.00, TRUE, 1),
(2, 'Impasto Integrale', 'Ricco di fibre e leggero', 1.50, FALSE, 1),
(3, 'Senza Glutine', 'Preparata in ambiente separato', 2.00, FALSE, 1),
(4, 'Doppia Mozzarella', 'Per i piu golosi', 1.50, FALSE, 2),
(5, 'Patatine Fritte Extra', 'Patatine fritte sopra la pizza', 2.00, FALSE, 2),
(6, 'Frutti di Bosco', 'Classica con coulis di frutti rossi', 0.00, TRUE, 3),
(7, 'Cioccolato Fondente', 'Con colata di cioccolato fuso', 0.00, FALSE, 3),
(8, 'Pistacchio di Bronte', 'Con crema di pistacchio e granella', 0.50, FALSE, 3),
(9, 'Caramello Salato', 'Dolce e salato in perfetto equilibrio', 0.00, FALSE, 3),
(10, 'Ketchup', 'Bustina monoporzione', 0.00, FALSE, 4),
(11, 'Maionese', 'Bustina monoporzione', 0.00, FALSE, 4),
(12, 'Salsa BBQ', 'Salsa barbecue affumicata', 0.50, FALSE, 4),
(13, 'Al Sangue', 'Cottura rapida, cuore rosso', 0.00, FALSE, 5),
(14, 'Media', 'Cottura standard, cuore rosato', 0.00, TRUE, 5),
(15, 'Ben Cotta', 'Nessuna traccia di rosa', 0.00, FALSE, 5),
(16, 'Con Ghiaccio', 'Bicchiere a parte con ghiaccio', 0.00, TRUE, 6),
(17, 'Senza Ghiaccio', '', 0.00, FALSE, 6),
(18, 'Fetta di Limone', '', 0.00, FALSE, 6);

INSERT INTO PRODOTTO (ID, NOME, DESCRIZIONE, PREZZO, PROCEDURA, TEMPO_PREPARAZIONE, IMMAGINE, CATEGORIA) VALUES
(1, 'Pizza Margherita', 'La regina, semplice e perfetta con pomodoro, mozzarella e basilico.', 7.50, 'Stendere l impasto, aggiungere pomodoro, mozzarella e basilico.', 10, 'margherita.jpg', 'Pizze Classiche'),
(2, 'Pizza Diavola', 'Per chi ama i sapori decisi, con salamino piccante di alta qualita.', 8.50, 'Aggiungere salame piccante abbondante prima di infornare.', 12, 'diavola.jpg', 'Pizze Classiche'),
(3, 'Pizza Capricciosa', 'Un grande classico guarnito con funghi, prosciutto cotto e carciofini.', 9.50, 'Disporre gli ingredienti in modo uniforme sulla base.', 15, 'capricciosa.jpg', 'Pizze Classiche'),
(4, 'Patatine Fritte', 'Porzione abbondante di patatine super croccanti e dorate.', 4.00, 'Friggere a 180 gradi per 5 minuti.', 5, 'patatine.jpg', 'Friggitoria'),
(5, 'Coca Cola in Lattina', '33cl, servita fredda di frigorifero.', 2.50, 'Servire fredda.', 1, 'cocacola.jpg', 'Bevande & Birre'),
(6, 'Tiramisu Artigianale', 'Fatto in casa seguendo la ricetta tradizionale con mascarpone fresco.', 4.50, 'Preparare strati di savoiardi e crema, spolverare di cacao.', 5, 'tiramisu.jpg', 'Dolci della Casa'),
(7, 'Bruschetta al Pomodoro', 'Pane casereccio tostato con pomodorini freschi, aglio e basilico.', 4.00, 'Tostare il pane e condire a crudo.', 5, 'bruschetta.jpg', 'Antipasti & Sfizi'),
(8, 'Tagliere Misto', 'Selezione di salumi e formaggi locali accompagnati da miele e noci.', 12.00, 'Affettare e disporre armonicamente su legno.', 10, 'tagliere.jpg', 'Antipasti & Sfizi'),
(9, 'Crocchette di Patate', '6 crocchette artigianali dal cuore caldo e filante di mozzarella.', 5.00, 'Friggere a 170 gradi fino a doratura esterna.', 6, 'crocchette.jpg', 'Friggitoria'),
(10, 'Arancino al Ragu', 'Classico arancino siciliano di riso ripieno di ragu di carne e piselli.', 3.50, 'Friggere a immersione totale.', 8, 'arancino.jpg', 'Friggitoria'),
(11, 'Pizza Bufalina', 'Pomodoro San Marzano, mozzarella di bufala campana DOP e olio EVO.', 10.00, 'Aggiungere la mozzarella all uscita dal forno.', 12, 'bufalina.jpg', 'Pizze Speciali'),
(12, 'Pizza Pistacchio', 'Fior di latte, mortadella di Bologna, granella di pistacchio e burrata.', 11.00, 'Aggiungere mortadella e burrata fuori dal forno.', 12, 'pistacchio.jpg', 'Pizze Speciali'),
(13, 'Bacon Cheeseburger', 'Hamburger di manzo sceltissimo 200g, cheddar fuso, bacon e salsa BBQ.', 10.50, 'Cuocere l hamburger su piastra bollente, piastrare il pane.', 15, 'burger.jpg', 'Panini & Burger'),
(14, 'Panino Salsiccia e Friarielli', 'Il re della tradizione partenopea racchiuso in un pane saltimbocca.', 7.50, 'Saltare i friarielli in padella e grigliare la salsiccia.', 12, 'salsiccia.jpg', 'Panini & Burger'),
(15, 'Piadina Crudo e Squacquerone', 'Piadina artigianale con prosciutto crudo di Parma, squacquerone e rucola.', 6.50, 'Scaldare leggermente la piadina sul testo e farcire.', 5, 'piadina.jpg', 'Focacce & Piadine'),
(16, 'Focaccia Ripiena', 'Focaccia calda della casa spaccata a meta e farcita con mortadella e provola.', 6.00, 'Tagliare a meta, farcire e riscaldare brevemente.', 5, 'focaccia.jpg', 'Focacce & Piadine'),
(17, 'Cheesecake', 'Base croccante di biscotto con crema morbida. Scegli il tuo gusto.', 5.50, 'Impiattare la fetta e guarnire.', 2, 'cheesecake.jpg', 'Dolci della Casa'),
(18, 'Birra Bionda', '33cl, birra bionda artigianale locale, fresca e beverina.', 3.50, 'Prendere dal frigo e stappare al momento.', 1, 'birra.jpg', 'Bevande & Birre'),
(19, 'Lasagna al Forno', 'Ricetta tradizionale emiliana con sfoglia fresca, ragu di carne e besciamella.', 9.00, 'Gratinare in forno per 20 minuti.', 20, 'lasagna.jpg', 'Primi Piatti'),
(20, 'Spaghetti alla Carbonara', 'Guanciale croccante, pecorino romano DOP, uova fresche e pepe nero.', 11.00, 'Saltare in padella mantecando lontano dal fuoco.', 15, 'spaghetti.jpg', 'Primi Piatti'),
(21, 'Acqua Naturale', 'Bottiglia da 50cl fresca di frigorifero.', 1.00, 'Prendere dal frigo.', 1, 'acqua.jpg', 'Bevande & Birre');

INSERT INTO PRODOTTO_INGREDIENTE (PRODOTTO_ID, INGREDIENTE_ID, QUANTITA) VALUES
(1, 1, '80g'), (1, 2, '100g'), (1, 3, 'q.b.'),
(2, 1, '80g'), (2, 2, '100g'), (2, 4, '50g'),
(3, 1, '80g'), (3, 2, '100g'), (3, 5, '50g'), (3, 6, '40g'), (3, 7, '40g'), (3, 8, 'q.b.'),
(4, 17, '250g'), (4, 18, 'q.b.'), (4, 19, 'q.b.'),
(5, 57, '33cl'),
(6, 50, '80g'), (6, 51, '4 pezzi'), (6, 52, 'q.b.'), (6, 53, 'q.b.'), (6, 21, '1'),
(7, 9, '2 fette'), (7, 10, '80g'), (7, 11, 'q.b.'), (7, 3, 'q.b.'), (7, 12, 'q.b.'),
(8, 13, '120g'), (8, 14, '120g'), (8, 15, 'q.b.'), (8, 16, 'q.b.'),
(9, 17, '200g'), (9, 2, '60g'), (9, 20, 'q.b.'), (9, 21, '1'),
(10, 22, '120g'), (10, 23, '80g'), (10, 24, '30g'), (10, 20, 'q.b.'),
(11, 1, '80g'), (11, 2, '120g'), (11, 3, 'q.b.'), (11, 12, 'q.b.'),
(12, 32, '100g'), (12, 33, '60g'), (12, 34, 'q.b.'), (12, 35, '80g'),
(13, 36, '1'), (13, 37, '200g'), (13, 38, '1 fetta'), (13, 39, '2 fette'), (13, 40, 'q.b.'),
(14, 41, '1'), (14, 42, '120g'), (14, 43, '80g'),
(15, 44, '1'), (15, 45, '70g'), (15, 46, '60g'), (15, 47, 'q.b.'),
(16, 48, '1'), (16, 33, '70g'), (16, 49, '60g'),
(17, 54, '80g'), (17, 55, '120g'), (17, 56, '40g'),
(18, 58, '33cl'),
(19, 25, '150g'), (19, 23, '120g'), (19, 26, '80g'), (19, 27, 'q.b.'),
(20, 28, '120g'), (20, 29, '60g'), (20, 30, '40g'), (20, 21, '1'), (20, 31, 'q.b.'),
(21, 59, '50cl');

INSERT INTO PRODOTTO_CARATTERISTICA (PRODOTTO_ID, CARATTERISTICA_ID) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(2, 1), (2, 2), (2, 3), (2, 4),
(3, 1), (3, 2), (3, 3),
(4, 10), (4, 11), (4, 12),
(13, 13), (13, 14), (13, 15), (13, 10), (13, 11), (13, 12),
(17, 6), (17, 7), (17, 8), (17, 9),
(5, 16), (5, 17), (5, 18);

INSERT INTO ORDINE (ID, DATA_ORDINE, ORARIO_CONSEGNA, PREZZO_TOTALE, STATO, METODO_PAGAMENTO, INDIRIZZO_CONSEGNA, UTENTE_ID) VALUES
(1, '2026-05-26', '20:00:00', 14.50, 'IN_CONSEGNA', 'CARTA_CREDITO', 'Via Roma, 10', 2),
(2, '2026-05-26', '20:30:00', 21.00, 'IN_PREPARAZIONE', 'CONTANTI', 'Piazza Napoli, 5', 3);

INSERT INTO ORDINE_PRODOTTO (ORDINE_ID, PRODOTTO_ID, QUANTITA) VALUES
(1, 1, 1),
(1, 5, 1),
(1, 6, 1),
(2, 2, 2),
(2, 4, 1);



