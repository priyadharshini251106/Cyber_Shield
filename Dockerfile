FROM eclipse-temurin:21-jdk

WORKDIR /app

# ------------------------------------------
# Copy MySQL JDBC driver
# ------------------------------------------

COPY backend/lib /app/backend/lib


# ------------------------------------------
# Copy normal Java source code
# ------------------------------------------

COPY backend/src/main/java /app/backend/src/main/java


# ------------------------------------------
# Copy Render-specific source files
# These replace the local versions
# ------------------------------------------

COPY backend/render/com/cybershield/incidentmanagement/database/DatabaseConnection.java \
     /app/backend/src/main/java/com/cybershield/incidentmanagement/database/DatabaseConnection.java

COPY backend/render/com/cybershield/incidentmanagement/server/CyberShieldServer.java \
     /app/backend/src/main/java/com/cybershield/incidentmanagement/server/CyberShieldServer.java

COPY frontend /app/frontend

# ------------------------------------------
# Create compiled output directory
# ------------------------------------------

RUN mkdir -p /app/backend/out


# ------------------------------------------
# Compile Java application
# ------------------------------------------

RUN javac \
    -cp "/app/backend/lib/*" \
    -d "/app/backend/out" \
    $(find /app/backend/src/main/java -name "*.java")


# ------------------------------------------
# Render web service port
# ------------------------------------------

EXPOSE 10000


# ------------------------------------------
# Start CyberShield
# ------------------------------------------

CMD ["sh", "-c", "java -cp '/app/backend/out:/app/backend/lib/*' com.cybershield.incidentmanagement.server.CyberShieldServer"]