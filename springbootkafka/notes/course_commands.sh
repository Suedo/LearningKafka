#enter the docker shell for kafka broker
docker-compose exec broker bash

# then issue command for creating a topic
kafka-topics --create --topic library-events --bootstrap-server broker:9092 --replication-factor 3 --partitions 3
kafka-topics --create --topic library-events --bootstrap-server broker:9092 --replication-factor 3 --partitions 3 --config min.insync.replicas=2


# have a console consumer up before you hit a producer endpoint. this will help us visually see the output in the consumer
kafka-console-consumer --topic library-events --bootstrap-server broker:9092


# ----------------------------------------------------------------------------------------------------------------------
# Kafka Security
# ----------------------------------------------------------------------------------------------------------------------

# create a server.keystore.jks file for ssl
keytool -keystore server.keystore.jks -alias localhost -validity 365 -genkey -keyalg RSA

# see details of generated keystore
keytool -list -v -keystore server.keystore.jks


# The below command will generate the ca cert(SSL cert) and private key.
# This is normally needed if we are self signing the request.
openssl req -new -x509 -keyout ca-key -out ca-cert -days 365 -subj "/CN=local-security-CA"


# create the CSR cert file from the keystore file
keytool -keystore server.keystore.jks -alias localhost -certreq -file cert-file

# mimics the CA signing the CSR cert file, and generates a `cert-signed` file
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 365 -CAcreateserial -passin pass:ssldemo1
  #o/p:
  #Signature ok
  #subject=/C=IN/ST=KN/L=Bangalore/O=localhost/OU=localhost/CN=localhost
  #Getting CA Private Key

# view contents of cert-signed file
keytool -printcert -v -file cert-signed

  # Issuer: CN=local-security-CA
  # Serial number: ad7a2006829bda7c
  # Valid from: Sun Oct 18 01:46:26 IST 2020 until: Mon Oct 18 01:46:26 IST 2021
  # Certificate fingerprints:
  # 	 MD5:  ED:C8:AF:9E:2B:2C:03:35:07:C1:40:A8:20:0C:8B:1F
  # 	 SHA1: 1C:BC:DC:CC:BC:39:B9:42:31:51:5B:99:EB:AB:09:10:9E:E7:E2:E4
  # 	 SHA256: 0D:30:9F:51:46:61:C0:37:C0:7B:7D:E9:B3:E4:CE:02:C2:1B:2B:99:4C:D0:A4:9C:5C:26:F3:C3:2B:07:0F:E6
  # Signature algorithm name: SHA1withRSA
  # Subject Public Key Algorithm: 2048-bit RSA key
  # Version: 1

# import the signed cert into keystore
keytool -keystore server.keystore.jks -alias CARoot -import -file ca-cert
keytool -keystore server.keystore.jks -alias localhost -import -file cert-signed
# generate the truststore
keytool -keystore client.truststore.jks -alias CARoot -import -file ca-cert
