-- Datos FICTICIOS para pruebas. Script repetible sin duplicar claves únicas.
USE rrhh_iudigital;

INSERT INTO
    estado_civil (nombre)
VALUES
    ('Soltero/a'),
    ('Casado/a'),
    ('Unión libre'),
    ('Separado/a'),
    ('Divorciado/a'),
    ('Viudo/a'),
    ('Otro')
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre);

INSERT INTO
    tipo_documento (nombre, sigla)
VALUES
    ('Cédula de ciudadanía', 'CC'),
    ('Tarjeta de identidad', 'TI'),
    ('Registro civil', 'RC'),
    ('Cédula de extranjería', 'CE'),
    ('Pasaporte', 'PA'),
    ('Permiso por protección temporal', 'PPT'),
    ('Otro', 'OTR')
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    sigla = VALUES(sigla);

INSERT INTO
    funcionarios (
        id_tipo_documento,
        numero_identificacion,
        nombres,
        apellidos,
        id_estado_civil,
        sexo,
        direccion,
        telefono,
        fecha_nacimiento
    )
SELECT
    td.id_tipo_documento,
    '100000001',
    'Laura Marcela',
    'Gómez Ríos',
    ec.id_estado_civil,
    'Femenino',
    'Calle 10 # 20-30',
    '3000000001',
    '1995-04-12'
FROM
    tipo_documento td
    JOIN estado_civil ec ON ec.nombre = 'Soltero/a'
WHERE
    td.sigla = 'CC'
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    id_estado_civil = VALUES(id_estado_civil),
    sexo = VALUES(sexo),
    direccion = VALUES(direccion),
    telefono = VALUES(telefono),
    fecha_nacimiento = VALUES(fecha_nacimiento);

INSERT INTO
    funcionarios (
        id_tipo_documento,
        numero_identificacion,
        nombres,
        apellidos,
        id_estado_civil,
        sexo,
        direccion,
        telefono,
        fecha_nacimiento
    )
SELECT
    td.id_tipo_documento,
    '100000002',
    'Carlos Andrés',
    'Pérez López',
    ec.id_estado_civil,
    'Masculino',
    'Carrera 45 # 12-10',
    '3000000002',
    '1988-11-03'
FROM
    tipo_documento td
    JOIN estado_civil ec ON ec.nombre = 'Casado/a'
WHERE
    td.sigla = 'CC'
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    id_estado_civil = VALUES(id_estado_civil),
    sexo = VALUES(sexo),
    direccion = VALUES(direccion),
    telefono = VALUES(telefono),
    fecha_nacimiento = VALUES(fecha_nacimiento);

INSERT INTO
    funcionarios (
        id_tipo_documento,
        numero_identificacion,
        nombres,
        apellidos,
        id_estado_civil,
        sexo,
        direccion,
        telefono,
        fecha_nacimiento
    )
SELECT
    td.id_tipo_documento,
    '100000003',
    'Diana Carolina',
    'Martínez Ruiz',
    ec.id_estado_civil,
    'Femenino',
    'Vereda El Centro',
    '3000000003',
    '1992-07-21'
FROM
    tipo_documento td
    JOIN estado_civil ec ON ec.nombre = 'Unión libre'
WHERE
    td.sigla = 'CC'
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    id_estado_civil = VALUES(id_estado_civil),
    sexo = VALUES(sexo),
    direccion = VALUES(direccion),
    telefono = VALUES(telefono),
    fecha_nacimiento = VALUES(fecha_nacimiento);

INSERT INTO
    funcionarios (
        id_tipo_documento,
        numero_identificacion,
        nombres,
        apellidos,
        id_estado_civil,
        sexo,
        direccion,
        telefono,
        fecha_nacimiento
    )
SELECT
    td.id_tipo_documento,
    '100000004',
    'Juan Sebastián',
    'Restrepo Mesa',
    ec.id_estado_civil,
    'Masculino',
    'Calle 50 # 8-15',
    '3000000004',
    '1998-02-16'
FROM
    tipo_documento td
    JOIN estado_civil ec ON ec.nombre = 'Soltero/a'
WHERE
    td.sigla = 'CC'
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    id_estado_civil = VALUES(id_estado_civil),
    sexo = VALUES(sexo),
    direccion = VALUES(direccion),
    telefono = VALUES(telefono),
    fecha_nacimiento = VALUES(fecha_nacimiento);

INSERT INTO
    funcionarios (
        id_tipo_documento,
        numero_identificacion,
        nombres,
        apellidos,
        id_estado_civil,
        sexo,
        direccion,
        telefono,
        fecha_nacimiento
    )
SELECT
    td.id_tipo_documento,
    '100000005',
    'Sofía Elena',
    'Torres Vega',
    ec.id_estado_civil,
    'Femenino',
    'Carrera 7 # 40-18',
    '3000000005',
    '1985-09-30'
FROM
    tipo_documento td
    JOIN estado_civil ec ON ec.nombre = 'Casado/a'
WHERE
    td.sigla = 'CE'
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    id_estado_civil = VALUES(id_estado_civil),
    sexo = VALUES(sexo),
    direccion = VALUES(direccion),
    telefono = VALUES(telefono),
    fecha_nacimiento = VALUES(fecha_nacimiento);

INSERT INTO
    grupo_familiar (
        id_funcionario,
        id_tipo_documento,
        numero_identificacion,
        nombres,
        apellidos,
        rol,
        fecha_nacimiento,
        telefono
    )
SELECT
    f.id_funcionario,
    td.id_tipo_documento,
    '200000001',
    'Samuel',
    'Gómez Ríos',
    'Hijo',
    '2018-06-02',
    '3000000011'
FROM
    funcionarios f
    JOIN tipo_documento td ON td.sigla = 'TI'
WHERE
    f.numero_identificacion = '100000001'
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    rol = VALUES(rol),
    fecha_nacimiento = VALUES(fecha_nacimiento),
    telefono = VALUES(telefono);

INSERT INTO
    grupo_familiar (
        id_funcionario,
        id_tipo_documento,
        numero_identificacion,
        nombres,
        apellidos,
        rol,
        fecha_nacimiento,
        telefono
    )
SELECT
    f.id_funcionario,
    td.id_tipo_documento,
    '200000002',
    'Marta',
    'Ríos Castro',
    'Madre',
    '1965-01-14',
    '3000000012'
FROM
    funcionarios f
    JOIN tipo_documento td ON td.sigla = 'CC'
WHERE
    f.numero_identificacion = '100000001'
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    rol = VALUES(rol),
    fecha_nacimiento = VALUES(fecha_nacimiento),
    telefono = VALUES(telefono);

INSERT INTO
    grupo_familiar (
        id_funcionario,
        id_tipo_documento,
        numero_identificacion,
        nombres,
        apellidos,
        rol,
        fecha_nacimiento,
        telefono
    )
SELECT
    f.id_funcionario,
    td.id_tipo_documento,
    '200000003',
    'Paula Andrea',
    'López Díaz',
    'Cónyuge',
    '1990-08-24',
    '3000000013'
FROM
    funcionarios f
    JOIN tipo_documento td ON td.sigla = 'CC'
WHERE
    f.numero_identificacion = '100000002'
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    rol = VALUES(rol),
    fecha_nacimiento = VALUES(fecha_nacimiento),
    telefono = VALUES(telefono);

INSERT INTO
    grupo_familiar (
        id_funcionario,
        id_tipo_documento,
        numero_identificacion,
        nombres,
        apellidos,
        rol,
        fecha_nacimiento,
        telefono
    )
SELECT
    f.id_funcionario,
    td.id_tipo_documento,
    '200000004',
    'Mateo',
    'Pérez López',
    'Hijo',
    '2015-12-17',
    '3000000014'
FROM
    funcionarios f
    JOIN tipo_documento td ON td.sigla = 'TI'
WHERE
    f.numero_identificacion = '100000002'
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    rol = VALUES(rol),
    fecha_nacimiento = VALUES(fecha_nacimiento),
    telefono = VALUES(telefono);

INSERT INTO
    grupo_familiar (
        id_funcionario,
        id_tipo_documento,
        numero_identificacion,
        nombres,
        apellidos,
        rol,
        fecha_nacimiento,
        telefono
    )
SELECT
    f.id_funcionario,
    td.id_tipo_documento,
    '200000005',
    'Andrés Felipe',
    'Ruiz Cano',
    'Cónyuge',
    '1991-04-09',
    '3000000015'
FROM
    funcionarios f
    JOIN tipo_documento td ON td.sigla = 'CC'
WHERE
    f.numero_identificacion = '100000003'
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    rol = VALUES(rol),
    fecha_nacimiento = VALUES(fecha_nacimiento),
    telefono = VALUES(telefono);

INSERT INTO
    grupo_familiar (
        id_funcionario,
        id_tipo_documento,
        numero_identificacion,
        nombres,
        apellidos,
        rol,
        fecha_nacimiento,
        telefono
    )
SELECT
    f.id_funcionario,
    td.id_tipo_documento,
    '200000006',
    'Elena',
    'Vega Mora',
    'Madre',
    '1960-03-11',
    '3000000016'
FROM
    funcionarios f
    JOIN tipo_documento td ON td.sigla = 'CC'
WHERE
    f.numero_identificacion = '100000005'
ON DUPLICATE KEY UPDATE
    nombres = VALUES(nombres),
    apellidos = VALUES(apellidos),
    rol = VALUES(rol),
    fecha_nacimiento = VALUES(fecha_nacimiento),
    telefono = VALUES(telefono);

INSERT INTO
    formacion_academica (
        id_funcionario,
        universidad,
        nivel_estudio,
        titulo_estudio
    )
SELECT
    id_funcionario,
    'Universidad de Antioquia',
    'Profesional',
    'Administración de Empresas'
FROM
    funcionarios
WHERE
    numero_identificacion = '100000001'
ON DUPLICATE KEY UPDATE
    titulo_estudio = VALUES(titulo_estudio);

INSERT INTO
    formacion_academica (
        id_funcionario,
        universidad,
        nivel_estudio,
        titulo_estudio
    )
SELECT
    id_funcionario,
    'IU Digital de Antioquia',
    'Especialización',
    'Gerencia de Proyectos'
FROM
    funcionarios
WHERE
    numero_identificacion = '100000001'
ON DUPLICATE KEY UPDATE
    titulo_estudio = VALUES(titulo_estudio);

INSERT INTO
    formacion_academica (
        id_funcionario,
        universidad,
        nivel_estudio,
        titulo_estudio
    )
SELECT
    id_funcionario,
    'Universidad Nacional de Colombia',
    'Profesional',
    'Ingeniería de Sistemas'
FROM
    funcionarios
WHERE
    numero_identificacion = '100000002'
ON DUPLICATE KEY UPDATE
    titulo_estudio = VALUES(titulo_estudio);

INSERT INTO
    formacion_academica (
        id_funcionario,
        universidad,
        nivel_estudio,
        titulo_estudio
    )
SELECT
    id_funcionario,
    'SENA',
    'Tecnólogo',
    'Análisis y Desarrollo de Software'
FROM
    funcionarios
WHERE
    numero_identificacion = '100000002'
ON DUPLICATE KEY UPDATE
    titulo_estudio = VALUES(titulo_estudio);

INSERT INTO
    formacion_academica (
        id_funcionario,
        universidad,
        nivel_estudio,
        titulo_estudio
    )
SELECT
    id_funcionario,
    'Universidad EAFIT',
    'Profesional',
    'Psicología'
FROM
    funcionarios
WHERE
    numero_identificacion = '100000003'
ON DUPLICATE KEY UPDATE
    titulo_estudio = VALUES(titulo_estudio);

INSERT INTO
    formacion_academica (
        id_funcionario,
        universidad,
        nivel_estudio,
        titulo_estudio
    )
SELECT
    id_funcionario,
    'IU Digital de Antioquia',
    'Tecnólogo',
    'Desarrollo de Software'
FROM
    funcionarios
WHERE
    numero_identificacion = '100000004'
ON DUPLICATE KEY UPDATE
    titulo_estudio = VALUES(titulo_estudio);

INSERT INTO
    formacion_academica (
        id_funcionario,
        universidad,
        nivel_estudio,
        titulo_estudio
    )
SELECT
    id_funcionario,
    'Universidad Pontificia Bolivariana',
    'Maestría',
    'Gestión del Talento Humano'
FROM
    funcionarios
WHERE
    numero_identificacion = '100000005'
ON DUPLICATE KEY UPDATE
    titulo_estudio = VALUES(titulo_estudio);
