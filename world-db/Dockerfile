# Use the MySQL image version 8.3 (allows native password plugin)
FROM mysql:8.3

WORKDIR /tmp
# Copy the SQL file to docker-entrypoint-initdb.d.
COPY world.sql /docker-entrypoint-initdb.d
# Set the root password
ENV MYSQL_ROOT_PASSWORD example