## RHINO
Basic Android app for secure (end-to-end encrypted) messaging.
Made as a project for CSE 472 Information Systems Security.


# Contributors

> Semih Boğaz     20150807006
> Bekir Nazlıgül  20170808046

# Methodology

This project uses two encryption algorithms together for safety: RSA and AES. When sending a message a new AES key is generated and used to encrypt the message text. Then the receiver's public key is used in RSA to encrypt the AES key. This encrypted text and AES Key is delivered to the receiver, who then can decrypt and read it using their private key.

As a result, the messages in delivery are always encrypted and is near impossible to crack and read. This picture shows what the server sees while two clients are messaging:


A new AES key is generated for every message, ensuring high security. In the current version the RSA key pair is refreshed every time the application is restarted. 
