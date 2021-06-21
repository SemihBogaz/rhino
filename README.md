## RHINO
Basic Android app for secure (end-to-end encrypted) messaging.
Made as a project for CSE 472 Information Systems Security.


# Contributors

- Semih Boğaz     20150807006
- Bekir Nazlıgül  20170808046

# Methodology

This project uses two encryption algorithms together for safety: RSA and AES. When sending a message a new AES key is generated and used to encrypt the message text. Then the receiver's public key is used in RSA to encrypt the AES key. This encrypted text and AES Key is delivered to the receiver, who then can decrypt and read it using their private RSA key.

As a result, the messages in delivery are always encrypted and is near impossible to crack and read. This picture shows what the server (Using FireBase storage) sees while two clients are messaging:

![Server's view of a message](https://github.com/SemihBogaz/rhino/blob/master/example.png)

A new AES key is generated for every message, ensuring high security. In the current version the RSA key pair is refreshed every time the application is restarted. This provides an additional layer of security.

The keys are kept in the application's internal storage in private mode, thus no other application can read or modify them. 

Another addition is the ProGuard. ProGuard obfuscates the program code and makes it hard to decompile and search for vulnerabilities.

# Short Demo

This video shows the registration process and a simple example conversation:

