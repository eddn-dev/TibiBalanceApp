Estructura principal

Colección users

Documento {uid} con los campos de perfil básicos.

Sub-colección habits para los hábitos del usuario.

Sub-colección emotions con un documento por mes que almacena los estados anímicos diarios.

Sub-colección achievements para los logros y su progreso.

Documento settings con preferencias globales de la app.

Colección habitTemplates
Contiene las plantillas de hábitos predefinidos que se muestran como sugerencias en el asistente “Nuevo hábito”. Estas plantillas se descargan una vez al iniciar la aplicación, se guardan en caché local y se pueden clonar cuando el usuario crea un nuevo hábito.

Documento de hábito

Cada documento de la sub-colección habits incluye: nombre, descripción, duración de sesión (o indefinido), configuración de repetición, periodo límite (o indefinido), categoría, icono, preferencias de notificación, fecha de creación y la marca de tiempo nextTriggerAt, que indica el próximo momento en que debe mostrarse la notificación.

Sincronización offline-first

Todos los datos del usuario se almacenan primero en la base de datos local (p. ej. Room o DataStore). Un sincronizador de segundo plano detecta la conectividad y replica las operaciones pendientes hacia Firestore; de vuelta, aplica las actualizaciones remotas al caché usando “último escrito-gana”. De esta forma la aplicación funciona sin conexión y se mantiene consistente cuando la red vuelve.

Notificaciones

La lógica cliente calcula nextTriggerAt al crear o editar un hábito. Un WorkManager nocturno consulta los hábitos cuyo nextTriggerAt sea próximo y programa alarmas locales según las preferencias de notificación (silenciosa, con sonido o vibración). Tras disparar la alarma se vuelve a calcular el siguiente disparo y se actualiza el documento correspondiente.