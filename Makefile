#Esse makefile só serve para fazer o arquivo a ser enviado pro moodle

.PHONY: all package verify compile submission clean

MVN=mvn
ifeq ($(wildcard .mvn mvnw), .mvn mvnw)
	MVN=./mvnw
endif
$(shell chmod +x mvnw || true)

all: package

package:
	$(MVN) -DskipTests=true package
verify:
	$(MVN) verify
compile:
	$(MVN) compile

# Prepara .tar.gz pra submissão no moodle
# Note que antes de preparar o tar.gz, é feito um clean
submission: clean
	$(MVN) verify || true
	@echo "Envio deve ser feito via git push!!!!"

# Limpa binários
clean:
	rm -fr target dependency-reduced-pom.xml
