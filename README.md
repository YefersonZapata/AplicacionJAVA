# Sistema de Recursos Humanos — IU Digital de Antioquia

## Roles y cuentas

- **Admin RRHH:** puede crear, consultar, actualizar y eliminar funcionarios y administrar los demás datos de la aplicación. En **Cuentas de funcionarios** puede crear cuentas para funcionarios registrados, asignarles el rol Admin RRHH, cambiar sus contraseñas y eliminar sus cuentas de acceso. Las cuentas se muestran en tablas separadas para funcionarios y administradores. También puede promover a Admin RRHH una cuenta de funcionario existente.
- **Funcionario:** puede consultar los datos y registros de la aplicación; los formularios y acciones de escritura están deshabilitados.

Las contraseñas deben tener al menos 8 caracteres. Se almacenan con PBKDF2-HMAC-SHA256, sal aleatoria por cuenta y 210.000 iteraciones; no se guardan en texto plano. Para dar acceso a un funcionario, Admin RRHH registra primero al funcionario y luego crea su cuenta en **Cuentas de funcionarios**.
