# Sistema de autenticación de usuarios con Spring Boot y Spring Security mediante JWT utilizando la librería vertx-auth-jwt (io.vertx)

## Configuración del proyecto

### 1. Generación de llaves (pública y privada)

#### Generar llave privada

```bash
openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:4096
```

#### Generar llave pública

```bash
openssl rsa -pubout -in private_key.pem -out public_key.pem
```

### 2. Configuración de las variables de entorno

```
JWT_ISSUER  =   auth0
PUBLIC_KEY  =   public_key.pem
PRIVATE_KEY =   private_key.pem
```

### 3. Configuración de la base de datos

```SQL
CREATE
DATABASE my_db;
```