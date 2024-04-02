FROM openjdk:8u302-oraclelinux8
LABEL maintainer=sibmaks; version=0.0.2; description="Project for group Scrum Poker voting"

RUN groupadd --gid 1000 docuser && useradd --uid 1000 --gid docuser --shell /bin/bash --create-home docuser
USER docuser

WORKDIR /home/docuser

COPY build/install/sp-boot scrum_poker

ENTRYPOINT ["./scrum_poker/bin/sp"]
