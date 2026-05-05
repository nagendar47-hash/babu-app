# =============================================================
# Multi-Stage Build — Red Hat UBI8
# Stage 1: Build (JDK + Maven inside container)
# Stage 2: Runtime (JRE only — no source, no build tools)
# =============================================================
FROM registry.access.redhat.com/ubi8/ubi:latest AS builder

# Install JDK 21 and wget
RUN dnf install -y java-21-openjdk-devel wget tar && dnf clean all

# Install Maven 3.9.13 manually
RUN wget https://archive.apache.org/dist/maven/maven-3/3.9.13/binaries/apache-maven-3.9.13-bin.tar.gz -P /tmp && \
    tar -xzf /tmp/apache-maven-3.9.13-bin.tar.gz -C /opt && \
    rm /tmp/apache-maven-3.9.13-bin.tar.gz

ENV MAVEN_HOME=/opt/apache-maven-3.9.13
ENV PATH=$MAVEN_HOME/bin:$PATH
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Runtime image ─────────────────────────────────────────────
FROM registry.access.redhat.com/ubi8/ubi:latest AS runtime

LABEL maintainer="your-email@example.com" version="1.0"

ENV APP_HOME=/app \
    APP_USER=appuser \
    APP_PORT=8080 \
    TZ=Asia/Kolkata \
    LANG=en_US.UTF-8 \
    SPRING_PROFILES_ACTIVE=prod

RUN dnf install -y java-21-openjdk-headless curl \
    && dnf clean all && rm -rf /var/cache/dnf

RUN groupadd -r ${APP_USER} && \
    useradd -r -g ${APP_USER} -d ${APP_HOME} -s /sbin/nologin ${APP_USER} && \
    mkdir -p ${APP_HOME} && chown -R ${APP_USER}:${APP_USER} ${APP_HOME}

WORKDIR ${APP_HOME}
COPY --from=builder --chown=${APP_USER}:${APP_USER} /build/target/app.jar app.jar

USER ${APP_USER}
EXPOSE ${APP_PORT}

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:${APP_PORT}/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
