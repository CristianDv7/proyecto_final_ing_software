-- Datos precargados. El catálogo de tipos de negocio es la precarga esencial:
-- permite mantener el formulario dentro del límite de campos y habilita la
-- selección desde un conjunto predefinido (ver spec: Assumptions).

INSERT INTO tipo_negocio (id, nombre) VALUES
    (1, 'Restaurante'),
    (2, 'Farmacia'),
    (3, 'Panadería'),
    (4, 'Ferretería'),
    (5, 'Tienda de abarrotes'),
    (6, 'Cafetería'),
    (7, 'Peluquería'),
    (8, 'Librería'),
    (9, 'Papelería'),
    (10, 'Verdulería');
