FROM gradle:8.13-jdk21 AS builder

WORKDIR /workspace
COPY --chown=gradle:gradle . .
RUN gradle installBootDist --no-daemon

FROM eclipse-temurin:21-jre-alpine-3.23
ARG APP_VERSION=dev

LABEL maintainer="sibmaks" \
      version="${APP_VERSION}" \
      description="Project for group Scrum Poker voting"

RUN apk upgrade --no-cache \
    && addgroup --system --gid 10001 docuser \
    && adduser --system --disabled-password --uid 10001 --ingroup docuser \
        --home /home/docuser --shell /sbin/nologin docuser
USER docuser

WORKDIR /home/docuser

COPY --from=builder --chown=docuser:docuser /workspace/build/install/sp-boot scrum_poker

HEALTHCHECK --interval=10s --timeout=3s --start-period=20s --retries=5 \
    CMD wget --quiet --spider "http://localhost:${SERVER_PORT:-8080}/actuator/health" || exit 1

ENTRYPOINT ["./scrum_poker/bin/sp"]
